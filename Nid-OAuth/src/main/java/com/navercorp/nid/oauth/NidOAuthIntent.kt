package com.navercorp.nid.oauth

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.provider.Settings
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.navercorp.nid.oauth.activity.NidOAuthCustomTabActivity
import com.navercorp.nid.oauth.activity.NidOAuthWebViewActivity
import com.navercorp.nid.util.NidApplicationUtil

/**
 *
 * Created on 2021.10.19
 * Updated on 2021.10.19
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * OAuth용 Intent Builder
 */
class NidOAuthIntent {

    companion object {
        /* OAuth Request Intent Key */
        const val OAUTH_REQUEST_CLIENT_ID = "ClientId"
        const val OAUTH_REQUEST_CALLBACK_URL = "ClientCallbackUrl"
//        const val OAUTH_REQUEST_STATE = "state"
        const val OAUTH_REQUEST_INIT_STATE = "state"
        const val OAUTH_REQUEST_CLIENT_NAME = "app_name"
        const val OAUTH_REQUEST_SDK_VERSION = "oauth_sdk_version"
        const val OAUTH_REQUEST_URL = "OAuthUrl"
        const val OAUTH_REQUEST_AGREE_FROM_URL = "agreeFormUrl"
        const val OAUTH_REQUEST_AGREE_FROM_CONTENT = "OAuthUrl"
        const val OAUTH_REQUEST_AUTH_TYPE = "auth_type"

        /* OAuth Result Intent Key */
        const val OAUTH_RESULT_STATE = "oauth_state"
        const val OAUTH_RESULT_CODE = "oauth_code"
        const val OAUTH_RESULT_ACCESS_TOKEN = "oauth_at"
        const val OAUTH_RESULT_ERROR_CODE = "oauth_error_code"
        const val OAUTH_RESULT_ERROR_DESCRIPTION = "oauth_error_desc"
    }


    enum class Type {
        NAVER_APP,
        CUSTOM_TABS,
        @Deprecated("WebView is deprecated")
        WEB_VIEW
    }

    class Builder {

        private var context: Context

        private var type: Type? = null

        private var clientId: String? = NidOAuthPreferencesManager.clientId
        private var callbackUrl: String? = NidOAuthPreferencesManager.callbackUrl
        private var clientName: String? = NidOAuthPreferencesManager.clientName
        private var initState: String? = NidOAuthPreferencesManager.initState

        private var authType: String? = null

        constructor(context: Context) {
            this.context = context
        }

        private fun getIntent(): Intent? {
            return when (type) {
                Type.NAVER_APP -> { getNaverAppIntent() }
                Type.CUSTOM_TABS -> { getCustomTabsIntent() }
                Type.WEB_VIEW -> { getWebViewIntent() }
                else -> { null }
            }
        }

        /**
         * 네이버앱으로 OAuth를 시도하는 경우의 intent
         */
        private fun getNaverAppIntent(): Intent? {
            return if (NidApplicationUtil.isNotExistIntentFilter(context, NidOAuthConstants.PACKAGE_NAME_NAVERAPP, NidOAuthConstants.SCHEME_OAUTH_LOGIN)) {
                null
            } else {
                Intent().apply {
                    putExtra(OAUTH_REQUEST_CLIENT_ID, clientId)
                    putExtra(OAUTH_REQUEST_CALLBACK_URL, callbackUrl)
                    putExtra(OAUTH_REQUEST_CLIENT_NAME, clientName)
                    putExtra(OAUTH_REQUEST_INIT_STATE, initState)
                    putExtra(OAUTH_REQUEST_SDK_VERSION, NidOAuthConstants.SDK_VERSION)

                    authType?.let {
                        val naverAppVersion = NidApplicationUtil.getNaverAppVersion(context)
                        var needUpdate = naverAppVersion < 11_16_00_00L

                        if (needUpdate) {
                            return Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${NidOAuthConstants.PACKAGE_NAME_NAVERAPP}"))
                        }
                        putExtra(OAUTH_REQUEST_AUTH_TYPE, authType)
                    }

                    setPackage(NidOAuthConstants.PACKAGE_NAME_NAVERAPP)
                    action = NidOAuthConstants.SCHEME_OAUTH_LOGIN
                }
            }
        }

        /**
         * Custom Tabs로 OAuth를 시도하는 경우의 intent
         */
        private fun getCustomTabsIntent(): Intent? {
            val flag = Settings.Global.getInt(context.contentResolver, Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0)
            if (flag == 1) {
                return null
            }

            if (NidApplicationUtil.isNotCustomTabsAvailable(context)) {
                return null
            }

            val listener = { intent: Intent? ->
                if (intent == null) {
                    val newIntent = Intent()
                    newIntent.putExtra(OAUTH_RESULT_ERROR_CODE, NidOAuthErrorCode.CLIENT_USER_CANCEL.code)
                    newIntent.putExtra(OAUTH_RESULT_ERROR_DESCRIPTION, NidOAuthErrorCode.CLIENT_USER_CANCEL.description)
                    (context as? NidOAuthBridgeActivity)?.onActivityResult(NidOAuthBridgeActivity.CUSTOM_TABS_LOGIN, Activity.RESULT_OK, newIntent)
                } else {
                    (context as? NidOAuthBridgeActivity)?.onActivityResult(NidOAuthBridgeActivity.CUSTOM_TABS_LOGIN, Activity.RESULT_OK, intent)
                }
            }

            val instance = LocalBroadcastManager.getInstance(context)
            instance.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent?) {
                    listener(intent)
                    instance.unregisterReceiver(this)
                }
            }, IntentFilter(NidOAuthCustomTabActivity.ACTION_NAVER_CUSTOM_TAB))

            return Intent(context, NidOAuthCustomTabActivity::class.java).apply {
                putExtra(OAUTH_REQUEST_CLIENT_ID, clientId)
                putExtra(OAUTH_REQUEST_CALLBACK_URL, callbackUrl)
                putExtra(OAUTH_REQUEST_INIT_STATE, initState)
                putExtra(OAUTH_REQUEST_SDK_VERSION, NidOAuthConstants.SDK_VERSION)

                authType?.let {
                    putExtra(OAUTH_REQUEST_AUTH_TYPE, authType)
                }

                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }
        }

        /**
         * WebView로 OAuth를 시도하는 경우의 intent
         */
        @Deprecated("WebView is deprecated")
        private fun getWebViewIntent(): Intent {
            return Intent(context, NidOAuthWebViewActivity::class.java).apply {
                putExtra(OAUTH_REQUEST_CLIENT_ID, clientId)
                putExtra(OAUTH_REQUEST_CALLBACK_URL, callbackUrl)
                putExtra(OAUTH_REQUEST_INIT_STATE, initState)
                putExtra(OAUTH_REQUEST_SDK_VERSION, NidOAuthConstants.SDK_VERSION)

                authType?.let {
                    putExtra(OAUTH_REQUEST_AUTH_TYPE, authType)
                }
            }
        }

        fun setType(type: Type): Builder {
            this.type = type
            return this
        }

        fun setAuthType(authType: String?): Builder {
            this.authType = authType
            return this
        }

        fun build(): Intent? {
            if (type == null) {
                return null
            }
            if (clientId.isNullOrEmpty()) {
                return null
            }
            if (type == Type.NAVER_APP && clientName.isNullOrEmpty()) {
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