package com.navercorp.nid

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.legacy.LegacyOAuthLogin
import com.navercorp.nid.legacy.OAuthLoginDefine
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.*
import com.navercorp.nid.oauth.legacy.OAuthErrorCode
import com.navercorp.nid.oauth.legacy.OAuthLoginPreferenceManager
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class NaverIdLoginSDKTest: NaverIdTestCase() {

    lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        EncryptedPreferences.setContext(context)
        NidLog.init()
    }

    @Test
    fun initialize_테스트() {
        NaverIdLoginSDK.initialize(context, "clientId", "clientSecret", "clientName")

        Assert.assertEquals("clientId", NidOAuthPreferencesManager.clientId)
        Assert.assertEquals("clientSecret", NidOAuthPreferencesManager.clientSecret)
        Assert.assertEquals("clientName", NidOAuthPreferencesManager.clientName)
        Assert.assertEquals(context.packageName, NidOAuthPreferencesManager.callbackUrl)
        Assert.assertEquals(NidOAuthErrorCode.NONE, NidOAuthPreferencesManager.lastErrorCode)
        Assert.assertEquals("", NidOAuthPreferencesManager.lastErrorDesc)
    }

    @Test
    fun initialize_리그레션_테스트() {
        LegacyOAuthLogin.getInstance().init(context, "clientId", "clientSecret", "clientName")
        NaverIdLoginSDK.initialize(context, "clientId", "clientSecret", "clientName")

        val preferenceManager = OAuthLoginPreferenceManager(context)

        Assert.assertEquals(preferenceManager.clientId, NidOAuthPreferencesManager.clientId)
        Assert.assertEquals(preferenceManager.clientSecret, NidOAuthPreferencesManager.clientSecret)
        Assert.assertEquals(preferenceManager.clientName, NidOAuthPreferencesManager.clientName)
        Assert.assertEquals(preferenceManager.callbackUrl, NidOAuthPreferencesManager.callbackUrl)
        Assert.assertEquals(preferenceManager.lastErrorCode.name, NidOAuthPreferencesManager.lastErrorCode.name)
        Assert.assertEquals(preferenceManager.lastErrorDesc, NidOAuthPreferencesManager.lastErrorDesc)
    }

    @Test
    fun getVersion_테스트() {
        Assert.assertEquals(NidOAuthConstants.SDK_VERSION, NaverIdLoginSDK.getVersion())
    }

    @Test
    fun getVersion_리그레션_테스트() {
        Assert.assertEquals(LegacyOAuthLogin.getVersion(), NaverIdLoginSDK.getVersion())
    }

    @Test
    fun getLastErrorCode_테스트() {
        for (errorCode in NidOAuthErrorCode.values()) {
            NidOAuthPreferencesManager.lastErrorCode = errorCode
            Assert.assertEquals(NidOAuthPreferencesManager.lastErrorCode, NaverIdLoginSDK.getLastErrorCode())
        }
    }

    @Test
    fun getLastErrorCode_리그레션_테스트() {
        val preferenceManager = OAuthLoginPreferenceManager(context)

        for (errorCode in NidOAuthErrorCode.values()) {
            preferenceManager.lastErrorCode = OAuthErrorCode.fromString(errorCode.name)
            NidOAuthPreferencesManager.lastErrorCode = errorCode
            Assert.assertEquals(LegacyOAuthLogin.getInstance().getLastErrorCode(context).name, NaverIdLoginSDK.getLastErrorCode().name)
        }
    }

    @Test
    fun getLastErrorDescription_테스트() {
        NidOAuthPreferencesManager.lastErrorDesc = "lastErrorDesc"
        Assert.assertEquals(NidOAuthPreferencesManager.lastErrorDesc, NaverIdLoginSDK.getLastErrorDescription())
    }

    @Test
    fun getLastErrorDescription_리그레션_테스트() {
        OAuthLoginPreferenceManager(context).lastErrorDesc = "lastErrorDesc"
        NidOAuthPreferencesManager.lastErrorDesc = "lastErrorDesc"
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getLastErrorDesc(context), NaverIdLoginSDK.getLastErrorDescription())
    }

    @Test
    fun behavior_리그레션_테스트() {
        LegacyOAuthLogin.getInstance().enableNaverAppLoginOnly()
        NaverIdLoginSDK.behavior = NidOAuthBehavior.NAVERAPP
        Assert.assertEquals(OAuthLoginDefine.LOGIN_BY_NAVERAPP_ONLY, NaverIdLoginSDK.behavior.allowsNaverApp)
        Assert.assertEquals(OAuthLoginDefine.LOGIN_BY_CUSTOM_TAB_ONLY, NaverIdLoginSDK.behavior.allowsCustomTabs)
        Assert.assertEquals(OAuthLoginDefine.LOGIN_BY_WEBVIEW_ONLY, NaverIdLoginSDK.behavior.allowsWebView)

        LegacyOAuthLogin.getInstance().enableCustomTabLoginOnly()
        NaverIdLoginSDK.behavior = NidOAuthBehavior.CUSTOMTABS
        Assert.assertEquals(OAuthLoginDefine.LOGIN_BY_NAVERAPP_ONLY, NaverIdLoginSDK.behavior.allowsNaverApp)
        Assert.assertEquals(OAuthLoginDefine.LOGIN_BY_CUSTOM_TAB_ONLY, NaverIdLoginSDK.behavior.allowsCustomTabs)
        Assert.assertEquals(OAuthLoginDefine.LOGIN_BY_WEBVIEW_ONLY, NaverIdLoginSDK.behavior.allowsWebView)

        LegacyOAuthLogin.getInstance().enableWebViewLoginOnly()
        NaverIdLoginSDK.behavior = NidOAuthBehavior.WEBVIEW
        Assert.assertEquals(OAuthLoginDefine.LOGIN_BY_NAVERAPP_ONLY, NaverIdLoginSDK.behavior.allowsNaverApp)
        Assert.assertEquals(OAuthLoginDefine.LOGIN_BY_CUSTOM_TAB_ONLY, NaverIdLoginSDK.behavior.allowsCustomTabs)
        Assert.assertEquals(OAuthLoginDefine.LOGIN_BY_WEBVIEW_ONLY, NaverIdLoginSDK.behavior.allowsWebView)
    }

    @Test
    fun getAccessToken_테스트() {
        NidOAuthPreferencesManager.accessToken = "accessToken"
        Assert.assertNull(NaverIdLoginSDK.getAccessToken())

        NidOAuthPreferencesManager.expiresAt = (System.currentTimeMillis() / 1000) + 1
        Assert.assertEquals(NidOAuthPreferencesManager.accessToken, NaverIdLoginSDK.getAccessToken())
    }

    @Test
    fun getAccessToken_리그레션_테스트() {
        OAuthLoginPreferenceManager(context).accessToken = "accessToken"
        NidOAuthPreferencesManager.accessToken = "accessToken"
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getAccessToken(context), NaverIdLoginSDK.getAccessToken())

        OAuthLoginPreferenceManager(context).expiresAt = (System.currentTimeMillis() / 1000) + 1
        NidOAuthPreferencesManager.expiresAt = (System.currentTimeMillis() / 1000) + 1
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getAccessToken(context), NaverIdLoginSDK.getAccessToken())
    }

    @Test
    fun getRefreshToken_테스트() {
        NidOAuthPreferencesManager.refreshToken = "refreshToken"
        Assert.assertEquals(NidOAuthPreferencesManager.refreshToken, NaverIdLoginSDK.getRefreshToken())
    }

    @Test
    fun getRefreshToken_리그레션_테스트() {
        OAuthLoginPreferenceManager(context).refreshToken = "refreshToken"
        NidOAuthPreferencesManager.refreshToken = "refreshToken"
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getRefreshToken(context), NaverIdLoginSDK.getRefreshToken())
    }

    @Test
    fun getExpiresAt_테스트() {
        NidOAuthPreferencesManager.expiresAt = 1000L
        Assert.assertEquals(NidOAuthPreferencesManager.expiresAt, NaverIdLoginSDK.getExpiresAt())
    }

    @Test
    fun getExpiresAt_리그레션_테스트() {
        OAuthLoginPreferenceManager(context).expiresAt = 1000L
        NidOAuthPreferencesManager.expiresAt = 1000L
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getExpiresAt(context), NaverIdLoginSDK.getExpiresAt())
    }

    @Test
    fun getTokenType_테스트() {
        NidOAuthPreferencesManager.tokenType = "tokenType"
        Assert.assertEquals(NidOAuthPreferencesManager.tokenType, NaverIdLoginSDK.getTokenType())
    }

    @Test
    fun getTokenType_리그레션_테스트() {
        OAuthLoginPreferenceManager(context).tokenType = "tokenType"
        NidOAuthPreferencesManager.tokenType = "tokenType"
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getTokenType(context), NaverIdLoginSDK.getTokenType())
    }

    @Test
    fun getState_테스트() {
        NidOAuthPreferencesManager.clientId = null
        NidOAuthPreferencesManager.clientSecret = null
        NidOAuthPreferencesManager.refreshToken = null
        NidOAuthPreferencesManager.accessToken = null
        NidOAuthPreferencesManager.expiresAt = 0L

        Assert.assertEquals(NidOAuthLoginState.NEED_INIT, NaverIdLoginSDK.getState())

        NidOAuthPreferencesManager.clientId = "clientId"
        Assert.assertEquals(NidOAuthLoginState.NEED_INIT, NaverIdLoginSDK.getState())

        NidOAuthPreferencesManager.clientSecret = "clientSecret"
        Assert.assertEquals(NidOAuthLoginState.NEED_LOGIN, NaverIdLoginSDK.getState())

        NidOAuthPreferencesManager.refreshToken = "refreshToken"
        Assert.assertEquals(NidOAuthLoginState.NEED_REFRESH_TOKEN, NaverIdLoginSDK.getState())

        NidOAuthPreferencesManager.accessToken = "accessToken"
        NidOAuthPreferencesManager.expiresAt = (System.currentTimeMillis() / 1000) + 1
        Assert.assertEquals(NidOAuthLoginState.OK, NaverIdLoginSDK.getState())
    }

    @Test
    fun getState_리그레션_테스트() {
        NidOAuthPreferencesManager.clientId = null
        NidOAuthPreferencesManager.clientSecret = null
        NidOAuthPreferencesManager.refreshToken = null
        NidOAuthPreferencesManager.accessToken = null
        NidOAuthPreferencesManager.expiresAt = 0L

        val preferenceManager = OAuthLoginPreferenceManager(context)
        preferenceManager.clientId = null
        preferenceManager.clientSecret = null
        preferenceManager.refreshToken = null
        preferenceManager.accessToken = null
        preferenceManager.expiresAt = 0L

        Assert.assertEquals(LegacyOAuthLogin.getInstance().getState(context), NaverIdLoginSDK.getState())

        preferenceManager.clientId = "clientId"
        NidOAuthPreferencesManager.clientId = "clientId"
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getState(context), NaverIdLoginSDK.getState())

        preferenceManager.clientSecret = "clientSecret"
        NidOAuthPreferencesManager.clientSecret = "clientSecret"
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getState(context), NaverIdLoginSDK.getState())

        preferenceManager.refreshToken = "refreshToken"
        NidOAuthPreferencesManager.refreshToken = "refreshToken"
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getState(context), NaverIdLoginSDK.getState())

        preferenceManager.accessToken = "accessToken"
        preferenceManager.expiresAt = (System.currentTimeMillis() / 1000) + 1
        NidOAuthPreferencesManager.accessToken = "accessToken"
        NidOAuthPreferencesManager.expiresAt = (System.currentTimeMillis() / 1000) + 1
        Assert.assertEquals(LegacyOAuthLogin.getInstance().getState(context), NaverIdLoginSDK.getState())
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            FakeAndroidKeyStore.setup
        }
    }

}