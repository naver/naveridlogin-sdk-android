package com.navercorp.nid.oauth.domain.usecase

import com.navercorp.nid.oauth.domain.repository.OAuthRepository

/**
 * OAuth 관련 정보 세팅 Usecase
 *
 * @property oauthRepository OAuth 관련 Repository
 */
internal class SetUpOAuthInfo(
    private val oauthRepository: OAuthRepository,
) {
    suspend fun initData(
        clientId: String?,
        clientSecret: String?,
        clientName: String?,
        callbackUrl: String?,
        lastErrorCode: String,
        lastErrorDesc: String,
    ) {
        oauthRepository.apply {
            saveClientId(clientId)
            saveClientSecret(clientSecret)
            saveClientName(clientName)
            saveCallbackUrl(callbackUrl)
            saveLastErrorInfo(
                code = lastErrorCode,
                desc = lastErrorDesc,
            )
        }
    }

    suspend fun setUpLastErrorInfo(
        errorCode: String,
        errorDesc: String,
    ) {
        oauthRepository.apply {
            saveLastErrorInfo(
                code = errorCode,
                desc = errorDesc,
            )
        }
    }
}