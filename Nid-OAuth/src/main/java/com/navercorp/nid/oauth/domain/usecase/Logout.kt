package com.navercorp.nid.oauth.domain.usecase

import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.oauth.domain.repository.OAuthRepository

/**
 * Logout을 수행하는 Usecase
 *
 * @property oauthRepository OAuth 관련 Repository
 */
internal class Logout(
    private val oauthRepository: OAuthRepository
) {
    suspend operator fun invoke() {
        oauthRepository.apply {
            saveAccessToken("")
            saveRefreshToken("")
            saveLastErrorInfo(
                code = NidOAuthErrorCode.NONE.code,
                desc = ""
            )
        }
    }
}