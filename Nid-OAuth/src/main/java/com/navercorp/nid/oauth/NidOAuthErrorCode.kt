package com.navercorp.nid.oauth

/**
 *
 * Created on 2021.10.19
 * Updated on 2021.10.19
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네아로SDK에서 OAuth 인증시 내려주는 에러 코드 정의
 *
 * Refs)
 * - http://tools.ietf.org/html/rfc6749
 */
enum class NidOAuthErrorCode(
    val code: String,
    val description: String
) {
    NONE("", ""),
    SERVER_ERROR_INVALID_REQUEST ("invalid_request", "invalid_request"),
    SERVER_ERROR_UNAUTHORIZED_CLIENT ("unauthorized_client", "unauthorized_client"),
    SERVER_ERROR_ACCESS_DENIED ("access_denied", "access_denied"),
    SERVER_ERROR_UNSUPPORTED_RESPONSE_TYPE ("unsupported_response_type", "unsupported_response_type"),
    SERVER_ERROR_INVALID_SCOPE ("invalid_scope", "invalid_scope"),
    SERVER_ERROR_SERVER_ERROR ("server_error", "server_error"),		// STATUS CODE == 500
    SERVER_ERROR_TEMPORARILY_UNAVAILABLE ("temporarily_unavailable", "temporarily_unavailable"),		// STATUS CODE == 503
    ERROR_NO_CATAGORIZED ("no_catagorized_error", "no_catagorized_error"),
    CLIENT_ERROR_PARSING_FAIL ("parsing_fail", "parsing_fail"),
    CLIENT_ERROR_NO_CLIENTID ("invalid_request", "no_clientid"),
    CLIENT_ERROR_NO_CLIENTSECRET ("invalid_request", "no_clientsecret"),
    CLIENT_ERROR_NO_CLIENTNAME ("invalid_request", "no_clientname"),
    CLIENT_ERROR_NO_CALLBACKURL ("invalid_request", "no_callbackurl"),
    CLIENT_ERROR_CONNECTION_ERROR ("server_error", "connection_error"),
    CLIENT_ERROR_CERTIFICATION_ERROR ("server_error", "certification_error"),
    CLIENT_USER_CANCEL ("user_cancel", "user_cancel"),
    ACTIVITY_IS_SINGLE_TASK("activity_is_single_task", "activity_is_single_task"),
    WEB_VIEW_IS_DEPRECATED("web_view_is_deprecated", "web_view_is_deprecated"),
    NO_APP_FOR_AUTHENTICATION("no_app_for_authentication", "no_app_for_authentication");

    companion object INSTANCE {
        fun fromString(code: String?): NidOAuthErrorCode {
            if (code.isNullOrEmpty()) {
                return NONE
            }
            values().forEach {
                if (code == it.code) {
                    return it
                }
                if (code == it.name) {
                    return it
                }
            }
            return ERROR_NO_CATAGORIZED
        }
    }
}