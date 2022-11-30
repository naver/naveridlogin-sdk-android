package com.navercorp.nid.oauth

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.util.NidDeviceUtil
import com.navercorp.nid.util.NidNetworkUtil
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
class NidOAuthQuery {

    enum class Method {
        CUSTOM_TABS
//        WEB_VIEW
    }

    companion object {
        const val REQUEST_AUTHORIZE_URL = "https://nid.naver.com/oauth2.0/authorize?"
//        const val REQUEST_ACCESS_TOKEN_URL = "https://nid.naver.com/oauth2.0/token?"
    }

    class Builder(context: Context) {
        private var method: Method? = null
        private var clientId: String? = NidOAuthPreferencesManager.clientId
        private var state: String? = NidOAuthPreferencesManager.initState
        private var callbackUrl: String? = NidOAuthPreferencesManager.callbackUrl
        private var locale = NidDeviceUtil.getLocale(context)
        private var network = NidNetworkUtil.getType(context)
        private var version = NidOAuthConstants.SDK_VERSION
        private var authType: String? = null

        fun setMethod(method: Method): Builder {
            this.method = method
            return this
        }

        fun setAuthType(authType: String?): Builder{
            this.authType = authType
            return this
        }

        private fun generateQuery(): String {
            return when (method) {
                Method.CUSTOM_TABS -> generateCustomTabsOAuthQuery()
//                Method.WEB_VIEW -> generateWebViewOAuthQuery()
                else -> generateCustomTabsOAuthQuery()
            }
        }

        private fun generateCustomTabsOAuthQuery(): String {
            val parameters: MutableMap<String, String?> = hashMapOf(
                "client_id" to clientId,
                "inapp_view" to "custom_tab",
                "response_type" to "code",
                "oauth_os" to "android",
                "version" to "android-$version",
                "locale" to locale,
                "redirect_uri" to callbackUrl,
                "state" to state
            )
            parameters["network"] = network

            if (NaverIdLoginSDK.isRequiredCustomTabsReAuth) {
                parameters["auth_type"] = "reauthenticate"
            }

            if (authType == "reprompt") {
                parameters["auth_type"] = "reprompt"
            }

            return "$REQUEST_AUTHORIZE_URL${parametersToQuery(parameters)}"
        }

//        private fun generateWebViewOAuthQuery(): String {
//            val parameters: MutableMap<String, String?> = hashMapOf(
//                "client_id" to clientId,
//                "inapp_view" to "true",
//                "response_type" to "code",
//                "oauth_os" to "android",
//                "version" to "android-$version",
//                "locale" to locale,
//                "redirect_uri" to callbackUrl,
//                "state" to state
//            )
//            parameters["network"] = network
//
//            if (authType == "reprompt") {
//                parameters["auth_type"] = "reprompt"
//            }
//
//            return "$REQUEST_AUTHORIZE_URL${parametersToQuery(parameters)}"
//        }

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

        fun build() = generateQuery()
    }

}