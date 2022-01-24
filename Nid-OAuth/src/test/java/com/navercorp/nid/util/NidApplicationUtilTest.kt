package com.navercorp.nid.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsService
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.util.NidApplicationUtil.isExistNaverApp
import com.navercorp.nid.util.legacy.ApplicationUtil
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class NidApplicationUtilTest : NaverIdTestCase() {

    lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun isExistNaverApp_테스트() {
        if (NidApplicationUtil.isExistNaverApp(context)) {
            Assert.assertTrue(true)
        } else {
            Assert.assertFalse(false)
        }
    }

    @Test
    fun isExistChromeApp_테스트() {
        if (NidApplicationUtil.isExistChromeApp(context)) {
            Assert.assertTrue(true)
        } else {
            Assert.assertFalse(false)
        }
    }

    @Test
    fun isExistApplication_테스트() {
        if (NidApplicationUtil.isExistApplication(context, NidOAuthConstants.PACKAGE_NAME_NAVERAPP)) {
            Assert.assertTrue(true)
        } else {
            Assert.assertFalse(false)
        }
    }

    @Test
    fun isExistIntentFilter_테스트() {
        val packageName = NidOAuthConstants.PACKAGE_NAME_NAVERAPP
        val intentName = NidOAuthConstants.SCHEME_OAUTH_LOGIN

        if (NidApplicationUtil.isExistNaverApp(context)) {
            Assert.assertTrue(NidApplicationUtil.isExistIntentFilter(context, packageName, intentName))
        }

        Assert.assertFalse(NidApplicationUtil.isExistIntentFilter(context, "", intentName))
        Assert.assertFalse(NidApplicationUtil.isExistIntentFilter(context, packageName, ""))
        Assert.assertFalse(NidApplicationUtil.isExistIntentFilter(context, "", ""))

    }

    @Test
    fun isNotExistIntentFilter_테스트() {

        val packageName = NidOAuthConstants.PACKAGE_NAME_NAVERAPP
        val intentName = NidOAuthConstants.SCHEME_OAUTH_LOGIN

        if (NidApplicationUtil.isExistNaverApp(context)) {
            Assert.assertFalse(NidApplicationUtil.isNotExistIntentFilter(context, packageName, intentName))
        }
        Assert.assertTrue(NidApplicationUtil.isNotExistIntentFilter(context, "", intentName))
        Assert.assertTrue(NidApplicationUtil.isNotExistIntentFilter(context, packageName, ""))
        Assert.assertTrue(NidApplicationUtil.isNotExistIntentFilter(context, "", ""))
    }

    @Test
    fun isCustomTabsAvailable_테스트() {

        val isCustomTabsAvailable = getCustomTabsPackageList(context).isNotEmpty()

        if (isCustomTabsAvailable) {
            Assert.assertTrue(NidApplicationUtil.isCustomTabsAvailable(context))
        } else {
            Assert.assertFalse(NidApplicationUtil.isCustomTabsAvailable(context))
        }
    }

    fun isNotCustomTabsAvailable_테스트() {

        if (NidApplicationUtil.isCustomTabsAvailable(context)) {
            Assert.assertFalse(false)
        } else {
            Assert.assertTrue(true)
        }
    }

    @Test
    fun isExistNaverApp_리그레션_테스트() {
        Assert.assertEquals(
            ApplicationUtil.isExistNaverApp(context),
            NidApplicationUtil.isExistNaverApp(context)
        )
    }

    @Test
    fun isExistChromeApp_리그레션_테스트() {
        Assert.assertEquals(
            ApplicationUtil.isExistApplication(context, NidOAuthConstants.PACKAGE_NAME_CHROMEAPP),
            NidApplicationUtil.isExistChromeApp(context)
        )
    }

    @Test
    fun isExistApplication_리그레션_테스트() {
        Assert.assertEquals(
            ApplicationUtil.isExistApplication(context, NidOAuthConstants.PACKAGE_NAME_NAVERAPP),
            NidApplicationUtil.isExistApplication(context, NidOAuthConstants.PACKAGE_NAME_NAVERAPP)
        )
        Assert.assertEquals(
            ApplicationUtil.isExistApplication(context, NidOAuthConstants.PACKAGE_NAME_CHROMEAPP),
            NidApplicationUtil.isExistApplication(context, NidOAuthConstants.PACKAGE_NAME_CHROMEAPP)
        )
    }


    @Test
    fun isExistIntentFilter_리그레션_테스트() {
        val packageName = NidOAuthConstants.PACKAGE_NAME_NAVERAPP
        val intentName = NidOAuthConstants.SCHEME_OAUTH_LOGIN
        if (isExistNaverApp(context)) {
            Assert.assertEquals(
                ApplicationUtil.isExistIntentFilter(context, packageName, intentName),
                NidApplicationUtil.isExistIntentFilter(context, packageName, intentName)
            )
        }
        Assert.assertEquals(
            ApplicationUtil.isExistIntentFilter(context, "", intentName),
            NidApplicationUtil.isExistIntentFilter(context, "", intentName)
        )
        Assert.assertEquals(
            ApplicationUtil.isExistIntentFilter(context, packageName, ""),
            NidApplicationUtil.isExistIntentFilter(context, packageName, "")
        )
        Assert.assertEquals(
            ApplicationUtil.isExistIntentFilter(context, "", ""),
            NidApplicationUtil.isExistIntentFilter(context, "", "")
        )
    }

    private fun getCustomTabsPackageList(context: Context): List<PackageInfo> {
        val packageManager = context.packageManager
        // Get default VIEW intent handler.
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))
        // Get all apps that can handle VIEW intents.
        val resolvedActivityList = packageManager.queryIntentActivities(intent, 0)

        val customTabsPackageList = mutableListOf<PackageInfo>()
        resolvedActivityList.forEach {
            val intent = Intent()
            intent.apply {
                action = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
                `package` = it.activityInfo.packageName
            }
            if (packageManager.resolveService(intent, 0) != null) {
                try {
                    val applicationInfo = packageManager.getApplicationInfo(it.activityInfo.packageName, 0)
                    if (applicationInfo.enabled) {
                        val packageInfo = packageManager.getPackageInfo(it.activityInfo.packageName, 0)
                        customTabsPackageList.add(packageInfo)
                    }
                } catch (e: PackageManager.NameNotFoundException) {

                }
            }
        }
        return customTabsPackageList
    }


}