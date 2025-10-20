package com.navercorp.nid.oauth.domain.usecase

import com.navercorp.nid.core.util.NidDeviceUtil
import com.navercorp.nid.oauth.domain.repository.OAuthRepository
import com.navercorp.nid.oauth.domain.vo.DisconnectResult

/**
 * Disconnect 처리하는 Usecase
 * - 서버에 토큰 삭제 요청
 *
 * @param oauthRepository OAuth 관련 Repository
 */
internal class Disconnect(
    private val oauthRepository: OAuthRepository
) {
    suspend operator fun invoke(): DisconnectResult {
        val clientId = oauthRepository.getClientId().orEmpty()
        val clientSecret = oauthRepository.getClientSecret().orEmpty()
        val accessToken = oauthRepository.getAccessToken().orEmpty()
        val locale = NidDeviceUtil.getLocale()

        val oauthResult = oauthRepository.requestDeleteToken(
            clientId = clientId,
            clientSecret = clientSecret,
            accessToken = accessToken,
            locale = locale,
        )

        return DisconnectResult(
            result = oauthResult.result,
            error = oauthResult.error,
            errorDescription = oauthResult.errorDescription,
        )
    }
}