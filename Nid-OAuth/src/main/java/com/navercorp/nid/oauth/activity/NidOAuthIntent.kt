package com.navercorp.nid.oauth.activity

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.NidServiceLocator
import com.navercorp.nid.core.util.NidApplicationUtil
import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.oauth.domain.usecase.GetClientInfo
import com.navercorp.nid.oauth.domain.usecase.GetOAuthInfo
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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
        CUSTOM_TABS
    }

    class Builder {
        private val oauthRepository by lazy {
            NidServiceLocator.provideOAuthRepository()
        }
        private val getOAuthInfo by lazy {
            GetOAuthInfo(oauthRepository)
        }
        private val getClientInfo by lazy {
            GetClientInfo(oauthRepository)
        }

        private var context: Context
        private var type: Type? = null
        private var authType: String? = null

        private val mutex = Mutex()

        constructor(context: Context) {
            this.context = context
        }

        private suspend fun getIntent(): Intent? {
            return when (type) {
                Type.NAVER_APP -> { getNaverAppIntent() }
                Type.CUSTOM_TABS -> { getCustomTabsIntent() }
                else -> { null }
            }
        }

        /**
         * 네이버앱으로 OAuth를 시도하는 경우의 intent
         */
        private suspend fun getNaverAppIntent(): Intent? {
            return if (NidApplicationUtil.isNotExistIntentFilter(context, NidOAuthConstants.PACKAGE_NAME_NAVERAPP, NidOAuthConstants.SCHEME_OAUTH_LOGIN)) {
                null
            } else {
                Intent().apply {
                    putExtra(OAUTH_REQUEST_CLIENT_ID, getClientInfo.getClientId())
                    putExtra(OAUTH_REQUEST_CALLBACK_URL, getClientInfo.getCallbackUrl())
                    putExtra(OAUTH_REQUEST_CLIENT_NAME, getClientInfo.getClientName())
                    putExtra(OAUTH_REQUEST_INIT_STATE, getOAuthInfo.getInitState())
                    putExtra(OAUTH_REQUEST_SDK_VERSION, NidOAuthConstants.SDK_VERSION)

                    authType?.let {
                        val naverAppVersion = NidApplicationUtil.getNaverAppVersion(context)
                        var needUpdate = naverAppVersion < 11_16_00_00L

                        if (needUpdate) {
                            return Intent(
                                Intent.ACTION_VIEW,
                                "market://details?id=${NidOAuthConstants.PACKAGE_NAME_NAVERAPP}".toUri()
                            )
                        }
                        putExtra(OAUTH_REQUEST_AUTH_TYPE, authType)
                    }

                    if (NidOAuth.naverappIntentFlag != -1) {
                        addFlags(NidOAuth.naverappIntentFlag)
                    }

                    setPackage(NidOAuthConstants.PACKAGE_NAME_NAVERAPP)
                    setAction(NidOAuthConstants.SCHEME_OAUTH_LOGIN)
                }
            }
        }

        /**
         * Custom Tabs로 OAuth를 시도하는 경우의 intent
         */
        private suspend fun getCustomTabsIntent(): Intent? {
            val flag = Settings.Global.getInt(context.contentResolver, Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0)
            if (flag == 1) {
                return null
            }

            if (NidApplicationUtil.isNotCustomTabsAvailable(context)) {
                return null
            }

            return Intent(context, NidOAuthCustomTabActivity::class.java).apply {
                putExtra(OAUTH_REQUEST_CLIENT_ID, getClientInfo.getClientId())
                putExtra(OAUTH_REQUEST_CALLBACK_URL, getClientInfo.getCallbackUrl())
                putExtra(OAUTH_REQUEST_INIT_STATE, getOAuthInfo.getInitState())
                putExtra(OAUTH_REQUEST_SDK_VERSION, NidOAuthConstants.SDK_VERSION)

                authType?.let {
                    putExtra(OAUTH_REQUEST_AUTH_TYPE, authType)
                }

                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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

        // mutex를 통해 여러 중간에 dataStore의 값이 변경되는 문제 예방
        suspend fun build(): Intent? = mutex.withLock {
            if (type == null) {
                return null
            }
            if (getClientInfo.getClientId().isNullOrEmpty()) {
                return null
            }
            if (type == Type.NAVER_APP && getClientInfo.getClientName().isNullOrEmpty()) {
                return null
            }
            if (getClientInfo.getCallbackUrl().isNullOrEmpty()) {
                return null
            }
            if (getOAuthInfo.getInitState().isNullOrEmpty()) {
                return null
            }
            return getIntent()
        }

    }
}