package com.navercorp.nid.oauth.activity

import com.navercorp.nid.NidOAuth
import com.navercorp.nid.NidServiceLocator
import com.navercorp.nid.core.util.NidDeviceUtil
import com.navercorp.nid.core.util.NidNetworkUtil
import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.oauth.domain.usecase.GetClientInfo
import com.navercorp.nid.oauth.domain.usecase.GetOAuthInfo
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 *
 * Created on 2021.10.20
 * Updated on 2021.10.20
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * OAuth 인증을 위한 Query를 생성하는 클래스
 */
internal class NidOAuthQuery {
    enum class Method {
        CUSTOM_TABS
    }

    companion object {
        const val REQUEST_AUTHORIZE_URL = "https://nid.naver.com/oauth2.0/authorize?"
    }

    class Builder {
        private val oauthRepository by lazy {
            NidServiceLocator.provideOAuthRepository()
        }
        private val getClientInfo by lazy {
            GetClientInfo(oauthRepository)
        }
        private val getOAuthInfo by lazy {
            GetOAuthInfo(oauthRepository)
        }

        private var method: Method? = null
        private var locale = NidDeviceUtil.getLocale()
        private var network = NidNetworkUtil.getType()
        private var version = NidOAuthConstants.SDK_VERSION
        private var authType: String? = null

        private val mutex = Mutex()

        fun setMethod(method: Method): Builder {
            this.method = method
            return this
        }

        fun setAuthType(authType: String?): Builder{
            this.authType = authType
            return this
        }

        private suspend fun generateQuery(): String {
            return when (method) {
                Method.CUSTOM_TABS -> generateCustomTabsOAuthQuery()
                else -> generateCustomTabsOAuthQuery()
            }
        }

        // mutex를 통해 여러 중간에 dataStore의 값이 변경되는 문제 예방
        private suspend fun generateCustomTabsOAuthQuery(): String = mutex.withLock {
            val parameters: MutableMap<String, String?> = hashMapOf(
                "client_id" to getClientInfo.getClientId(),
                "inapp_view" to "custom_tab",
                "response_type" to "code",
                "oauth_os" to "android",
                "version" to "android-$version",
                "locale" to locale,
                "redirect_uri" to getClientInfo.getCallbackUrl(),
                "state" to getOAuthInfo.getOAuthState()
            )
            parameters["network"] = network

            if (NidOAuth.isRequiredCustomTabsReAuth) {
                parameters["auth_type"] = "reauthenticate"
            }

            if (authType == "reprompt") {
                parameters["auth_type"] = "reprompt"
            }

            return "$REQUEST_AUTHORIZE_URL${parametersToQuery(parameters)}"
        }

        private fun parametersToQuery(parameters: Map<String, String?>): String {
            var keys = parameters.keys
            var query = StringBuilder()
            keys.forEach { key ->
                var value = parameters[key]
                if (query.isNotEmpty()) {
                    query.append("&")
                }
                query.append("$key=")
                try {
                    query.append(encode(value))
                } catch (e: UnsupportedEncodingException) {
                    query.append(value)
                }
            }
            return query.toString()
        }

        private fun encode(s: String?): String {
            return if (s == null) {
                ""
            } else URLEncoder.encode(s, "UTF-8") // OAuth encodes some characters differently:
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~")
        }

        suspend fun build() = generateQuery()
    }

}