package com.navercorp.nid.oauth.domain.repository

import com.navercorp.nid.oauth.domain.vo.LastErrorInfo
import com.navercorp.nid.oauth.domain.vo.NidOAuth

internal interface OAuthRepository {
    suspend fun requestAccessToken(
        clientId: String,
        clientSecret: String,
        state: String,
        code: String,
        locale: String,
    ): NidOAuth

    suspend fun requestRefreshToken(
        clientId: String,
        clientSecret: String,
        refreshToken: String,
        locale: String,
    ): NidOAuth

    suspend fun requestDeleteToken(
        clientId: String,
        clientSecret: String,
        accessToken: String,
        locale: String,
    ): NidOAuth

    suspend fun saveOAuthResult(oauthResult: NidOAuth)

    suspend fun saveAccessToken(token: String?)
    fun getAccessToken(): String?

    suspend fun saveRefreshToken(token: String?)
    fun getRefreshToken(): String?

    suspend fun saveAccessTokenExpiresAt(expiresAt: Long)
    fun getAccessTokenExpiresAt(): Long

    suspend fun saveTokenType(tokenType: String?)
    fun getTokenType(): String?

    suspend fun saveLastErrorInfo(code: String?, desc: String?)
    fun getLastErrorInfo(): LastErrorInfo

    suspend fun saveClientId(clientId: String?)
    fun getClientId(): String?

    suspend fun saveClientSecret(clientSecret: String?)
    fun getClientSecret(): String?

    suspend fun saveClientName(clientName: String?)
    fun getClientName(): String?

    suspend fun saveCallbackUrl(callbackUrl: String?)
    fun getCallbackUrl(): String?

    suspend fun saveInitState(state: String?)
    suspend fun getInitState(): String?

    suspend fun saveOAuthCode(code: String?)
    fun getOAuthCode(): String?

    suspend fun saveOAuthState(state: String?)
    fun getOAuthState(): String?
}