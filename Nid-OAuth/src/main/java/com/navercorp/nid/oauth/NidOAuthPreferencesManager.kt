package com.navercorp.nid.oauth

import android.media.UnsupportedSchemeException
import com.navercorp.nid.log.NidLog
import java.math.BigInteger
import java.net.URLEncoder
import java.security.SecureRandom

private const val TAG = "OAuthLoginEncryptedPreferenceManager"

object NidOAuthPreferencesManager {

    private const val ACCESS_TOKEN = "ACCESS_TOKEN"
    @JvmStatic
    var accessToken: String?
        set(value) = EncryptedPreferences.set(ACCESS_TOKEN, value)
        get() {
            val token = EncryptedPreferences.get(ACCESS_TOKEN, null)
            if (token.isNullOrEmpty()) {
                return null
            }

            // expires time 검증 후 return 해줌
            if (System.currentTimeMillis() / 1000 - expiresAt < 0) return token

            // 만료로 인해 값은 있으나 리턴안해줌
            NidLog.i(TAG, "access token is expired.")
            return null
        }

    private const val REFRESH_TOKEN = "REFRESH_TOKEN"
    @JvmStatic
    var refreshToken: String?
        set(value) = EncryptedPreferences.set(REFRESH_TOKEN, value)
        get() {
            val token = EncryptedPreferences.get(REFRESH_TOKEN, null)
            if (token.isNullOrEmpty()) {
                return null
            }
            return token
        }

    private const val EXPIRES_AT = "EXPIRES_AT"
    @JvmStatic
    var expiresAt: Long
        set(value) = EncryptedPreferences.set(EXPIRES_AT, value)
        get() = EncryptedPreferences.get(EXPIRES_AT, 0L)

    private const val CLIENT_ID = "CLIENT_ID"
    @JvmStatic
    var clientId: String?
        set(value) = EncryptedPreferences.set(CLIENT_ID, value)
        get() = EncryptedPreferences.get(CLIENT_ID, null)

    private const val CLIENT_SECRET = "CLIENT_SECRET"
    @JvmStatic
    var clientSecret: String?
        set(value) = EncryptedPreferences.set(CLIENT_SECRET, value)
        get() = EncryptedPreferences.get(CLIENT_SECRET, null)

    private const val CLIENT_NAME = "CLIENT_NAME"
    @JvmStatic
    var clientName: String?
        set(value) = EncryptedPreferences.set(CLIENT_NAME, value)
        get() = EncryptedPreferences.get(CLIENT_NAME, null)

    private const val CALLBACK_URL = "CALLBACK_URL"
    @JvmStatic
    var callbackUrl: String?
        set(value) = EncryptedPreferences.set(CALLBACK_URL, value)
        get() = EncryptedPreferences.get(CALLBACK_URL, null)

    private const val TOKEN_TYPE = "TOKEN_TYPE"
    @JvmStatic
    var tokenType: String?
        set(value) = EncryptedPreferences.set(TOKEN_TYPE, value)
        get() = EncryptedPreferences.get(TOKEN_TYPE, null)

    private const val LAST_ERROR_CODE = "LAST_ERROR_CODE"
    @JvmStatic
    var lastErrorCode: NidOAuthErrorCode
        set(value) = EncryptedPreferences.set(LAST_ERROR_CODE, value.code)
        get() {
            val code = EncryptedPreferences.get(LAST_ERROR_CODE, null) ?: ""
            return NidOAuthErrorCode.fromString(code)
        }

    private const val LAST_ERROR_DESC = "LAST_ERROR_DESC"
    @JvmStatic
    var lastErrorDesc: String?
        set(value) = EncryptedPreferences.set(LAST_ERROR_DESC, value)
        get() = EncryptedPreferences.get(LAST_ERROR_DESC, null)




    private const val OAUTH_INIT_STATE = "OAUTH_INIT_STATE"
    var initState: String?
        set(value) = EncryptedPreferences.set(OAUTH_INIT_STATE, value)
        get() {
            var value = EncryptedPreferences.get(OAUTH_INIT_STATE, null)
            if (value != null) {
                return value
            }

            value = BigInteger(130, SecureRandom()).toString(32)

            try {
                val encoded = URLEncoder.encode(value, "UTF-8")
                value = encoded
            } catch (e: UnsupportedSchemeException) {
                NidLog.e(TAG, e)
            }
            initState = value
            return value
        }

    private const val OAUTH_CODE = "OAUTH_CODE"
    var code: String?
        set(value) = EncryptedPreferences.set(OAUTH_CODE, value)
        get() = EncryptedPreferences.get(OAUTH_CODE, null)

    private const val OAUTH_CHECK_STATE = "OAUTH_CHECK_STATE"
    var state: String?
        set(value) = EncryptedPreferences.set(OAUTH_CHECK_STATE, value)
        get() = EncryptedPreferences.get(OAUTH_CHECK_STATE, null)

    private const val OAUTH_ERROR_CODE = "OAUTH_ERROR_CODE"
    var errorCode: String?
        set(value) = EncryptedPreferences.set(OAUTH_ERROR_CODE, value)
        get() = EncryptedPreferences.get(OAUTH_ERROR_CODE, null)

    private const val OAUTH_ERROR_DESCRIPTION = "OAUTH_ERROR_DESCRIPTION"
    var errorDescription: String?
        set(value) = EncryptedPreferences.set(OAUTH_ERROR_DESCRIPTION, value)
        get() = EncryptedPreferences.get(OAUTH_ERROR_DESCRIPTION, null)


}