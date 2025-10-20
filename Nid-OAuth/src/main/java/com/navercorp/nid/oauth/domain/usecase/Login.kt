package com.navercorp.nid.oauth.domain.usecase

import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.core.util.NidDeviceUtil
import com.navercorp.nid.oauth.domain.repository.OAuthRepository
import com.navercorp.nid.oauth.domain.vo.LoginInfo
import com.navercorp.nid.oauth.domain.vo.Token

/**
 * Login을 수행하는 UseCase
 *
 * @param oauthRepository OAuth 관련 Repository
 */
internal class Login(
    private val oauthRepository: OAuthRepository,
) {

    /**
     * OAuth Code를 가지고 Access / Refresh Token 요청 수행
     */
    suspend operator fun invoke(
        loginInfo: LoginInfo,
    ): Token {
        val clientId = oauthRepository.getClientId().orEmpty()
        val clientSecret = oauthRepository.getClientSecret().orEmpty()
        val oauthCode = loginInfo.oauthCode.orEmpty()
        val oauthState = loginInfo.oauthState.orEmpty()
        val locale = NidDeviceUtil.getLocale()

        // oauth 성공 / 실패 여부와 상관없이 Login 정보 저장
        saveLoginInfo(loginInfo)

        // 1. Access Token 요청
        val oauthResult = oauthRepository.requestAccessToken(
            clientId = clientId,
            clientSecret = clientSecret,
            state = oauthState,
            code = oauthCode,
            locale = locale,
        )
        var errorCode = oauthResult.error

        if (oauthResult.isOAuthInterrupted) {
            // 2. OAuth 진행 중 사용자가 취소한 경우 처리
            errorCode = NidOAuthErrorCode.CLIENT_USER_CANCEL
            oauthRepository.saveLastErrorInfo(
                code = errorCode.code,
                desc = errorCode.description,
            )
        } else if (oauthResult.isOAuthSuccess) {
            // 3. Access Token 요청이 성공한 경우 처리
            oauthRepository.saveOAuthResult(oauthResult)
        } else {
            // 4. Access Token 요청이 실패한 경우 처리
            oauthRepository.saveLastErrorInfo(
                code = oauthResult.error.code,
                desc = oauthResult.errorDescription,
            )
        }

        return Token(
            accessToken = oauthResult.accessToken,
            refreshToken = oauthResult.refreshToken,
            error = errorCode,
            errorDescription = errorCode.description,
        )
    }

    private suspend fun saveLoginInfo(
        loginInfo: LoginInfo,
    ) {
        oauthRepository.apply {
            saveOAuthCode(loginInfo.oauthCode)
            saveOAuthState(loginInfo.oauthState)
            saveLastErrorInfo(
                code = loginInfo.errorCode,
                desc = loginInfo.errorDesc
            )
        }
    }

    /**
     * Refresh Token을 사용하여 Access Token을 갱신하는 함수
     */
    suspend fun refreshToken(): Token{
        val clientId = oauthRepository.getClientId().orEmpty()
        val clientSecret = oauthRepository.getClientSecret().orEmpty()
        val refreshToken = oauthRepository.getRefreshToken().orEmpty()
        val locale = NidDeviceUtil.getLocale()

        val oauthResult = oauthRepository.requestRefreshToken(
            clientId = clientId,
            clientSecret = clientSecret,
            refreshToken = refreshToken,
            locale = locale,
        )

        if (oauthResult.isOAuthSuccess) {
            // Refresh Token 요청이 성공한 경우 처리
            oauthRepository.apply {
                saveAccessToken(oauthResult.accessToken)
                val expireAt = System.currentTimeMillis() / 1000 + oauthResult.expiresIn
                saveAccessTokenExpiresAt(expireAt)
            }
        } else {
            // Access Token 요청이 실패한 경우 처리
            oauthRepository.saveLastErrorInfo(
                code = oauthResult.error.code,
                desc = oauthResult.errorDescription,
            )
        }

        return Token(
            accessToken = oauthResult.accessToken,
            refreshToken = oauthResult.refreshToken,
            error = oauthResult.error,
            errorDescription = oauthResult.errorDescription
        )
    }
}