package com.navercorp.nid.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.browser.customtabs.CustomTabsService
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.NidOAuthConstants


/**
 *
 * Created on 2021.10.19
 * Updated on 2021.10.19
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 애플리케이션 관련 유틸리티 클래스
 */
object NidApplicationUtil {
    const val TAG = "NidApplicationUtil"


    /**
     * 네이버앱의 설치 여부를 검증한다.
     */
    fun isExistNaverApp(context: Context): Boolean = isExistApplication(context, NidOAuthConstants.PACKAGE_NAME_NAVERAPP)

    /**
     * 크롬의 설치 여부를 검증한다.
     */
    fun isExistChromeApp(context: Context): Boolean = isExistApplication(context, NidOAuthConstants.PACKAGE_NAME_CHROMEAPP)

    /**
     * 특정 앱의 설치 여부를 검증한다.
     *
     * @param packageName 검증할 앱의 packageName
     */
    fun isExistApplication(context: Context, packageName: String): Boolean {
        var intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return (intent != null)
    }

    /**
     * IntentName으로 쿼리가 가능한 Activity 리스트를 획득한 뒤, 리스트에 가용한 애플리케이션이 있는지 검증한다.
     *
     * @param packageName 검증할 앱의 packageName
     * @param intentName 쿼리할 intent filter name
     */
    fun isExistIntentFilter(context: Context, packageName: String, intentName: String): Boolean {
        var resolveInfoList = context.packageManager.queryIntentActivities(
            Intent(intentName),
            PackageManager.GET_META_DATA
        )

        resolveInfoList.forEach {
            NidLog.d(TAG, "intent filter name : $intentName")
            NidLog.d(TAG, "resolveInfo.activityInfo.packageName : ${it.activityInfo.packageName}")
            if (it.activityInfo.packageName.equals(packageName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun isNotExistIntentFilter(context: Context, packageName: String, intentName: String): Boolean = !isExistIntentFilter(context, packageName, intentName)

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
            NidLog.d(TAG, "getCustomTabsPackageList : ${it.activityInfo.packageName}")
            if (packageManager.resolveService(intent, 0) != null) {
                try {
                    val applicationInfo = packageManager.getApplicationInfo(it.activityInfo.packageName, 0)
                    if (applicationInfo.enabled) {
                        val packageInfo = packageManager.getPackageInfo(it.activityInfo.packageName, 0)
                        customTabsPackageList.add(packageInfo)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    NidLog.d(TAG, e)
                }
            }
        }
        return customTabsPackageList
    }

    fun isCustomTabsAvailable(context: Context): Boolean {
        val customTabsPackageList = getCustomTabsPackageList(context)
        return customTabsPackageList.isNotEmpty()
    }

    fun isNotCustomTabsAvailable(context: Context): Boolean = !isCustomTabsAvailable(context)

    fun getNaverAppVersion(context: Context): Long = getApplicationVersion(context, NidOAuthConstants.PACKAGE_NAME_NAVERAPP)

    private fun getApplicationVersion(context: Context, packageName: String): Long {
        val packageManager = context.packageManager

        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            if (Build.VERSION.SDK_INT >= AndroidVer.API_28_PIE) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            NidLog.d(TAG, e)
            -1
        }
    }

}