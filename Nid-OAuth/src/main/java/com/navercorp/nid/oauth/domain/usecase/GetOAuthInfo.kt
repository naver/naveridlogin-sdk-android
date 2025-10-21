package com.navercorp.nid.oauth.domain.usecase

import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.oauth.domain.repository.OAuthRepository

/**
 * 로컬에 저장된 OAuth 정보 조회 Usecase
 *
 * @property oauthRepository OAuth 관련 Repository
 */
internal class GetOAuthInfo(
    private val oauthRepository: OAuthRepository,
) {
    fun getAccessToken(): String? =
        oauthRepository.getAccessToken()

    fun getRefreshToken(): String? =
        oauthRepository.getRefreshToken()

    fun getTokenType(): String? =
        oauthRepository.getTokenType()

    fun getAccessTokenExpiresAt(): Long =
        oauthRepository.getAccessTokenExpiresAt()

    fun getLastErrorCode(): NidOAuthErrorCode =
        oauthRepository.getLastErrorInfo().lastErrorCode

    fun getLastErrorDesc(): String? =
        oauthRepository.getLastErrorInfo().lastErrorDescription

    fun getOAuthState(): String? =
        oauthRepository.getOAuthState()

    suspend fun getInitState(): String? =
        oauthRepository.getInitState()
}