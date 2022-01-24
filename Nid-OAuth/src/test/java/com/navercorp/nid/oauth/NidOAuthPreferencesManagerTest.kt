package com.navercorp.nid.oauth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.legacy.OAuthErrorCode
import com.navercorp.nid.oauth.legacy.OAuthLoginPreferenceManager
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class NidOAuthPreferencesManagerTest : NaverIdTestCase() {

    private lateinit var context: Context
    private lateinit var oAuthLoginPreferenceManager: OAuthLoginPreferenceManager

    fun init() {
        context = ApplicationProvider.getApplicationContext()
        EncryptedPreferences.setContext(context)
        NidLog.init()
        oAuthLoginPreferenceManager = OAuthLoginPreferenceManager(context)
    }

    @Before
    fun setUp() {
        init()
    }

    @Test
    fun accessToken_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.accessToken = value
        NidOAuthPreferencesManager.expiresAt = 0L
        Assert.assertEquals(null, NidOAuthPreferencesManager.accessToken)

        NidOAuthPreferencesManager.accessToken = value
        NidOAuthPreferencesManager.expiresAt = System.currentTimeMillis() /1000 + 1000
        Assert.assertEquals(value, NidOAuthPreferencesManager.accessToken)
    }

    @Test
    fun refreshToken_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.refreshToken = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.refreshToken)
    }

    @Test
    fun expiresAt_테스트() {
        val value = 1000L
        NidOAuthPreferencesManager.expiresAt = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.expiresAt)
    }

    @Test
    fun clientId_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.clientId = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.clientId)
    }

    @Test
    fun clientSecret_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.clientSecret = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.clientSecret)
    }
    @Test
    fun clientName_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.clientName = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.clientName)
    }

    @Test
    fun callbackUrl_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.callbackUrl = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.callbackUrl)
    }

    @Test
    fun tokenType_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.tokenType = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.tokenType)
    }

    @Test
    fun lastErrorCode_테스트() {
        val value = NidOAuthErrorCode.NONE
        NidOAuthPreferencesManager.lastErrorCode = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.lastErrorCode)
    }
    @Test
    fun lastErrorDesc_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.lastErrorDesc = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.lastErrorDesc)
    }
    @Test
    fun initState_테스트() {
        var value: String? = null
        Assert.assertNotNull(NidOAuthPreferencesManager.initState)

        value = "value"
        NidOAuthPreferencesManager.initState = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.initState)
    }
    @Test
    fun code_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.code = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.code)
    }

    @Test
    fun state_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.state = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.state)
    }

    @Test
    fun errorCode_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.errorCode = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.errorCode)
    }

    @Test
    fun errorDescription_테스트() {
        val value = "value"
        NidOAuthPreferencesManager.errorDescription = value
        Assert.assertEquals(value, NidOAuthPreferencesManager.errorDescription)
    }

    @Test
    fun accessToken_리그레션_테스트() {
        val value = "value"
        oAuthLoginPreferenceManager.accessToken = value
        oAuthLoginPreferenceManager.expiresAt = 0L
        NidOAuthPreferencesManager.accessToken = value
        NidOAuthPreferencesManager.expiresAt = 0L
        Assert.assertEquals(oAuthLoginPreferenceManager.accessToken, NidOAuthPreferencesManager.accessToken)

        oAuthLoginPreferenceManager.accessToken = value
        oAuthLoginPreferenceManager.expiresAt = System.currentTimeMillis() / 1000 + 1000
        NidOAuthPreferencesManager.accessToken = value
        NidOAuthPreferencesManager.expiresAt = System.currentTimeMillis() / 1000 + 1000

        Assert.assertEquals(oAuthLoginPreferenceManager.accessToken, NidOAuthPreferencesManager.accessToken)
    }

    @Test
    fun refreshToken_리그레션_테스트() {
        val value = "value"
        oAuthLoginPreferenceManager.refreshToken = value
        NidOAuthPreferencesManager.refreshToken = value

        Assert.assertEquals(oAuthLoginPreferenceManager.refreshToken, NidOAuthPreferencesManager.refreshToken)
    }

    @Test
    fun expiresAt_리그레션_테스트() {
        val value = 1000L
        oAuthLoginPreferenceManager.expiresAt = value
        NidOAuthPreferencesManager.expiresAt = value

        Assert.assertEquals(oAuthLoginPreferenceManager.expiresAt, NidOAuthPreferencesManager.expiresAt)
    }

    @Test
    fun clientId_리그레션_테스트() {
        val value = "value"
        oAuthLoginPreferenceManager.clientId = value
        NidOAuthPreferencesManager.clientId = value

        Assert.assertEquals(oAuthLoginPreferenceManager.clientId, NidOAuthPreferencesManager.clientId)
    }

    @Test
    fun clientSecret_리그레션_테스트() {
        val value = "value"
        oAuthLoginPreferenceManager.clientSecret = value
        NidOAuthPreferencesManager.clientSecret = value

        Assert.assertEquals(oAuthLoginPreferenceManager.clientSecret, NidOAuthPreferencesManager.clientSecret)
    }

    @Test
    fun clientName_리그레션_테스트() {
        val value = "value"
        oAuthLoginPreferenceManager.clientName = value
        NidOAuthPreferencesManager.clientName = value

        Assert.assertEquals(oAuthLoginPreferenceManager.clientName, NidOAuthPreferencesManager.clientName)
    }

    @Test
    fun callbackUrl_리그레션_테스트() {
        val value = "value"
        oAuthLoginPreferenceManager.callbackUrl = value
        NidOAuthPreferencesManager.callbackUrl = value

        Assert.assertEquals(oAuthLoginPreferenceManager.callbackUrl, NidOAuthPreferencesManager.callbackUrl)
    }

    @Test
    fun tokenType_리그레션_테스트() {
        val value = "value"
        oAuthLoginPreferenceManager.tokenType = value
        NidOAuthPreferencesManager.tokenType = value

        Assert.assertEquals(oAuthLoginPreferenceManager.tokenType, NidOAuthPreferencesManager.tokenType)
    }

    @Test
    fun lastErrorCode_리그레션_테스트() {
        oAuthLoginPreferenceManager.lastErrorCode = OAuthErrorCode.NONE
        NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.NONE

        Assert.assertEquals(oAuthLoginPreferenceManager.lastErrorCode.name, NidOAuthPreferencesManager.lastErrorCode.name)
    }

    @Test
    fun lastErrorDesc_리그레션_테스트() {
        val value = "value"
        oAuthLoginPreferenceManager.lastErrorDesc = value
        NidOAuthPreferencesManager.lastErrorDesc = value

        Assert.assertEquals( oAuthLoginPreferenceManager.lastErrorDesc, NidOAuthPreferencesManager.lastErrorDesc)
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            FakeAndroidKeyStore.setup
        }
    }

}