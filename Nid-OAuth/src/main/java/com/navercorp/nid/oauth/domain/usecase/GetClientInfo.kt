package com.navercorp.nid.oauth.domain.usecase

import com.navercorp.nid.oauth.domain.repository.OAuthRepository

/**
 * 로컬에 저장된 OAuth Client 정보 조회 Usecase
 *
 * @property oauthRepository OAuth 관련 Repository
 */
internal class GetClientInfo(
    private val oauthRepository: OAuthRepository,
) {
    fun getClientId(): String? =
        oauthRepository.getClientId()

    fun getClientSecret(): String? =
        oauthRepository.getClientSecret()

    fun getClientName(): String? =
        oauthRepository.getClientName()

    fun getCallbackUrl(): String? =
        oauthRepository.getCallbackUrl()
}