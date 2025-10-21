package com.navercorp.nid.oauth.data.datasource

import com.navercorp.nid.core.base.handleApiResult
import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.oauth.data.mapper.NidOAuthMapper
import com.navercorp.nid.oauth.data.service.NidOAuthLoginService
import com.navercorp.nid.oauth.domain.vo.NidOAuth

/**
 * OAuth 관련 네아로 SDK Api 호출을 위한 dataSource
 */
internal class NidOAuthRemoteDataSource(
    private val nidOAUthLoginService: NidOAuthLoginService
) {
    /**
     * code로 access token 발급 요청
     *
     * @param clientId 앱 아이디
     * @param clientSecret 앱 시크릿
     * @param state 인증 요청 시 전달한 state
     * @param code 인증 성공 후 전달 받은 code
     * @param locale 디바이스 언어값
     */
    suspend fun requestAccessToken(
        clientId: String,
        clientSecret: String,
        state: String,
        code: String,
        locale: String,
    ): NidOAuth {
        val apiResult = handleApiResult {
            nidOAUthLoginService.requestAccessToken(
                clientId = clientId,
                clientSecret = clientSecret,
                state = state,
                code = code,
                version = ANDROID_VERSION,
                locale = locale,
            )
        }
        return NidOAuthMapper.toNidOAuth(apiResult)
    }

    /**
     * refresh token으로 access token 재발급 요청
     *
     * @param clientId 앱 아이디
     * @param clientSecret 앱 시크릿
     * @param refreshToken 발급 받은 refresh token
     * @param locale 디바이스 언어값
     */
    suspend fun requestRefreshToken (
        clientId: String,
        clientSecret: String,
        refreshToken: String,
        locale: String,
    ): NidOAuth {
        val apiResult = handleApiResult {
            nidOAUthLoginService.requestRefreshToken(
                clientId = clientId,
                clientSecret = clientSecret,
                refreshToken = refreshToken,
                version = ANDROID_VERSION,
                locale = locale,
            )
        }
        return NidOAuthMapper.toNidOAuth(apiResult)
    }

    /**
     * 서버에 저장된 access token 및 refresh token 삭제 요청
     *
     * @param clientId 앱 아이디
     * @param clientSecret 앱 시크릿
     * @param accessToken 발급 받은 access token
     * @param locale 디바이스 언어값
     */
    suspend fun deleteToken(
        clientId: String,
        clientSecret: String,
        accessToken: String,
        locale: String,
    ): NidOAuth {
        val apiResult = handleApiResult {
            nidOAUthLoginService.requestDeleteToken(
                clientId = clientId,
                clientSecret = clientSecret,
                accessToken = accessToken,
                version = ANDROID_VERSION,
                locale = locale,
            )
        }
        return NidOAuthMapper.toNidOAuth(apiResult)
    }

    companion object {
        private const val ANDROID_VERSION = "android-${NidOAuthConstants.SDK_VERSION}"
    }
}