package com.navercorp.nid.oauth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.legacy.OAuthQueryGenerator
import com.navercorp.nid.util.NidDeviceUtil.getLocale
import com.navercorp.nid.util.NidNetworkUtil.getType
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class NidOAuthQueryTest : NaverIdTestCase() {

    lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        EncryptedPreferences.setContext(context)
    }

    @Test
    fun generateCustomTabsOAuthQuery_테스트() {
        val query = NidOAuthQuery.Builder(context)
            .setMethod(NidOAuthQuery.Method.CUSTOM_TABS)
            .build()

        Assert.assertTrue(query.contains(REQUEST_AUTHORIZE_URL))
        Assert.assertTrue(query.contains("client_id"))
        Assert.assertTrue(query.contains("inapp_view"))
        Assert.assertTrue(query.contains("response_type"))
        Assert.assertTrue(query.contains("oauth_os"))
        Assert.assertTrue(query.contains("version"))
        Assert.assertTrue(query.contains("locale"))
        Assert.assertTrue(query.contains("redirect_uri"))
        Assert.assertTrue(query.contains("state"))
        Assert.assertTrue(query.contains("network"))

        if (NaverIdLoginSDK.isRequiredCustomTabsReAuth) {
            Assert.assertTrue(query.contains("auth_type"))
        } else {
            Assert.assertFalse(query.contains("auth_type"))
        }

    }

    @Test
    fun generateWebViewOAuthQuery_테스트() {
        val query = NidOAuthQuery.Builder(context)
            .setMethod(NidOAuthQuery.Method.WEB_VIEW)
            .build()

        Assert.assertTrue(query.contains(REQUEST_AUTHORIZE_URL))
        Assert.assertTrue(query.contains("client_id"))
        Assert.assertTrue(query.contains("inapp_view"))
        Assert.assertTrue(query.contains("response_type"))
        Assert.assertTrue(query.contains("oauth_os"))
        Assert.assertTrue(query.contains("version"))
        Assert.assertTrue(query.contains("locale"))
        Assert.assertTrue(query.contains("redirect_uri"))
        Assert.assertTrue(query.contains("state"))
        Assert.assertTrue(query.contains("network"))
    }

    @Test
    fun generateRequestCustomTabAuthorizationUrl_리그레션_테스트() {
        NidOAuthPreferencesManager.clientId = "clientId"
        NidOAuthPreferencesManager.callbackUrl = "callbackUrl"

        val oldCustomTabUrl = OAuthQueryGenerator().generateRequestCustomTabAuthorizationUrl(
            NidOAuthPreferencesManager.clientId,
            NidOAuthPreferencesManager.initState,
            NidOAuthPreferencesManager.callbackUrl,
            getLocale(context),
            getType(context),
            NidOAuthConstants.SDK_VERSION
        )
        val newCustomTabUrl = NidOAuthQuery.Builder(context)
            .setMethod(NidOAuthQuery.Method.CUSTOM_TABS)
            .build()

        Assert.assertEquals(oldCustomTabUrl, newCustomTabUrl)
    }

    @Test
    fun generateRequestWebViewAuthorizationUrl_리그레션_테스트() {

        NidOAuthPreferencesManager.clientId = "clientId"
        NidOAuthPreferencesManager.callbackUrl = "callbackUrl"

        val oldWebViewUrl = OAuthQueryGenerator().generateRequestWebViewAuthorizationUrl(
            NidOAuthPreferencesManager.clientId,
            NidOAuthPreferencesManager.initState,
            NidOAuthPreferencesManager.callbackUrl,
            getLocale(context),
            getType(context),
            NidOAuthConstants.SDK_VERSION
        )
        val newWebViewUrl = NidOAuthQuery.Builder(context)
            .setMethod(NidOAuthQuery.Method.WEB_VIEW)
            .build()

        Assert.assertEquals(oldWebViewUrl, newWebViewUrl)
    }

    companion object {
        const val REQUEST_AUTHORIZE_URL = "https://nid.naver.com/oauth2.0/authorize?"

        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            FakeAndroidKeyStore.setup
        }
    }

}