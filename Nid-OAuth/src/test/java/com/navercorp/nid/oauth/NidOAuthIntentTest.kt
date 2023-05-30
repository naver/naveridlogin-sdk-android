package com.navercorp.nid.oauth

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.oauth.activity.NidOAuthCustomTabActivity
import com.navercorp.nid.oauth.legacy.OAuthLoginActivity
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class NidOAuthIntentTest : NaverIdTestCase() {

    lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        EncryptedPreferences.setContext(context)
    }

    @Test
    fun getNaverAppIntent_테스트() {
        NidOAuthPreferencesManager.clientId = "clientId"
        NidOAuthPreferencesManager.callbackUrl = "callbackUrl"
        NidOAuthPreferencesManager.clientName = "clientName"

        var intent = NidOAuthIntent.Builder(context)
            .setType(NidOAuthIntent.Type.NAVER_APP)
            .build()

        Assert.assertNull(intent)

        intent = Builder(context)
            .setType(NidOAuthIntent.Type.NAVER_APP)
            .build()!!

        Assert.assertEquals(NidOAuthPreferencesManager.clientId, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID))
        Assert.assertEquals(NidOAuthPreferencesManager.callbackUrl, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL))
        Assert.assertEquals(NidOAuthPreferencesManager.clientName, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_NAME))
        Assert.assertEquals(NidOAuthPreferencesManager.initState, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE))
        Assert.assertEquals(NidOAuthConstants.SDK_VERSION, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION))
        Assert.assertEquals(NidOAuthConstants.PACKAGE_NAME_NAVERAPP, intent.`package`)
        Assert.assertEquals(NidOAuthConstants.SCHEME_OAUTH_LOGIN, intent.action)
    }

    @Test
    fun getNaverAppIntent_리그레션_테스트() {
        NidOAuthPreferencesManager.clientId = "clientId"
        NidOAuthPreferencesManager.callbackUrl = "callbackUrl"
        NidOAuthPreferencesManager.clientName = "clientName"

        val intent = OAuthLoginActivity(context).tryOAuthByNaverapp(NidOAuthPreferencesManager.clientId, NidOAuthPreferencesManager.callbackUrl,
            NidOAuthPreferencesManager.clientName, NidOAuthPreferencesManager.initState)

        val nidIntent = Builder(context)
            .setType(NidOAuthIntent.Type.NAVER_APP)
            .build()!!

        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_NAME), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_NAME))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION))
        Assert.assertEquals(intent.`package`, nidIntent.`package`)
        Assert.assertEquals(intent.action, nidIntent.action)
    }

    @Test
    fun getCustomTabsIntent_테스트() {
        NidOAuthPreferencesManager.clientId = "clientId"
        NidOAuthPreferencesManager.callbackUrl = "callbackUrl"

        var intent = NidOAuthIntent.Builder(context)
            .setType(NidOAuthIntent.Type.CUSTOM_TABS)
            .build()

        Assert.assertNull(intent)

        intent = Builder(context)
            .setType(NidOAuthIntent.Type.CUSTOM_TABS)
            .build()!!

        Assert.assertEquals(NidOAuthCustomTabActivity::class.java.name, intent.component!!.className)

        Assert.assertEquals(NidOAuthPreferencesManager.clientId, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID))
        Assert.assertEquals(NidOAuthPreferencesManager.callbackUrl, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL))
        Assert.assertEquals(NidOAuthPreferencesManager.initState, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE))
        Assert.assertEquals(NidOAuthConstants.SDK_VERSION, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION))
        Assert.assertEquals(Intent.FLAG_ACTIVITY_NO_ANIMATION, intent.flags)
    }

    @Test
    fun getCustomTabsIntent_리그레션_테스트() {
        NidOAuthPreferencesManager.clientId = "clientId"
        NidOAuthPreferencesManager.callbackUrl = "callbackUrl"

        val intent = OAuthLoginActivity(context).tryOAuthByCustomTab(NidOAuthPreferencesManager.clientId, NidOAuthPreferencesManager.callbackUrl,
            NidOAuthPreferencesManager.initState)

        val nidIntent = Builder(context)
            .setType(NidOAuthIntent.Type.CUSTOM_TABS)
            .build()!!

        Assert.assertEquals(intent.component!!.className, nidIntent.component!!.className)

        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION))
        Assert.assertEquals(intent.flags, nidIntent.flags)
    }

    @Test
    fun getWebViewIntent_테스트() {
        NidOAuthPreferencesManager.clientId = "clientId"
        NidOAuthPreferencesManager.callbackUrl = "callbackUrl"

        val intent = NidOAuthIntent.Builder(context)
            .setType(NidOAuthIntent.Type.WEB_VIEW)
            .build()!!

        Assert.assertEquals(NidOAuthWebViewActivity::class.java.name, intent.component!!.className)

        Assert.assertEquals(NidOAuthPreferencesManager.clientId, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID))
        Assert.assertEquals(NidOAuthPreferencesManager.callbackUrl, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL))
        Assert.assertEquals(NidOAuthPreferencesManager.initState, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE))
        Assert.assertEquals(NidOAuthConstants.SDK_VERSION, intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION))
    }

    @Test
    fun getWebViewIntent_리그레션_테스트() {
        NidOAuthPreferencesManager.clientId = "clientId"
        NidOAuthPreferencesManager.callbackUrl = "callbackUrl"

        val intent = OAuthLoginActivity(context).startLoginWebviewActivity(NidOAuthPreferencesManager.clientId,
            NidOAuthPreferencesManager.callbackUrl, NidOAuthPreferencesManager.initState)

        val nidIntent = NidOAuthIntent.Builder(context)
            .setType(NidOAuthIntent.Type.WEB_VIEW)
            .build()!!

        Assert.assertEquals(intent.component!!.className, nidIntent.component!!.className)

        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE))
        Assert.assertEquals(intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION), nidIntent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION))
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            FakeAndroidKeyStore.setup
        }
    }

    inner class Builder {

        private var context: Context

        private var type: NidOAuthIntent.Type? = null

        private var clientId: String? = NidOAuthPreferencesManager.clientId
        private var callbackUrl: String? = NidOAuthPreferencesManager.callbackUrl
        private var clientName: String? = NidOAuthPreferencesManager.clientName
        private var initState: String? = NidOAuthPreferencesManager.initState

        constructor(context: Context) {
            this.context = context
        }

        private fun getIntent(): Intent? {
            return when (type) {
                NidOAuthIntent.Type.NAVER_APP -> { getNaverAppIntent() }
                NidOAuthIntent.Type.CUSTOM_TABS -> { getCustomTabsIntent() }
                NidOAuthIntent.Type.WEB_VIEW -> { getWebViewIntent() }
                else -> { null }
            }
        }

        /**
         * 네이버앱으로 OAuth를 시도하는 경우의 intent
         */
        private fun getNaverAppIntent(): Intent {
            return Intent().apply {
                putExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID, clientId)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL, callbackUrl)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_NAME, clientName)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE, initState)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION, NidOAuthConstants.SDK_VERSION)

                setPackage(NidOAuthConstants.PACKAGE_NAME_NAVERAPP)
                action = NidOAuthConstants.SCHEME_OAUTH_LOGIN
            }
        }

        /**
         * Custom Tabs로 OAuth를 시도하는 경우의 intent
         */
        private fun getCustomTabsIntent(): Intent {
            return Intent(context, NidOAuthCustomTabActivity::class.java).apply {
                putExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID, clientId)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL, callbackUrl)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE, initState)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION, NidOAuthConstants.SDK_VERSION)

                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
        }

        /**
         * WebView로 OAuth를 시도하는 경우의 intent
         */
        private fun getWebViewIntent(): Intent {
            return Intent(context, NidOAuthWebViewActivity::class.java).apply {
                putExtra(NidOAuthIntent.OAUTH_REQUEST_CLIENT_ID, clientId)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_CALLBACK_URL, callbackUrl)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_INIT_STATE, initState)
                putExtra(NidOAuthIntent.OAUTH_REQUEST_SDK_VERSION, NidOAuthConstants.SDK_VERSION)
            }
        }

        fun setType(type: NidOAuthIntent.Type): Builder {
            this.type = type
            return this
        }

        fun build(): Intent? {
            if (type == null) {
                return null
            }
            if (clientId.isNullOrEmpty()) {
                return null
            }
            if (type == NidOAuthIntent.Type.NAVER_APP && clientName.isNullOrEmpty()) {
                return null
            }
            if (callbackUrl.isNullOrEmpty()) {
                return null
            }
            if (initState.isNullOrEmpty()) {
                return null
            }
            return getIntent()
        }

    }

}