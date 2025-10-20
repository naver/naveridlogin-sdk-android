package com.navercorp.nid.oauth.data.mapper

import com.navercorp.nid.core.base.NidApiResult
import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.oauth.data.dto.NidOAuthResponse
import com.navercorp.nid.oauth.domain.vo.NidOAuth

/**
 * DTO를 VO로 변환하는 Mapper
 * 실패할 경우, errorCode 및 errorDescription이 세팅된 빈 VO 반환
 */
internal object NidOAuthMapper {
    private const val DEFAULT_EXPIRES_IN_STR = "3600"

    fun toNidOAuth(
        response: NidApiResult<NidOAuthResponse>
    ): NidOAuth = when(response) {
        is NidApiResult.Success -> {
            val data = response.data
            NidOAuth(
                accessToken = data.accessToken.orEmpty(),
                refreshToken = data.refreshToken.orEmpty(),
                tokenType = data.tokenType.orEmpty(),
                expiresInString = data.expiresInString ?: DEFAULT_EXPIRES_IN_STR,
                result = data.result.orEmpty(),
                error = NidOAuthErrorCode.fromString(data.error),
                errorDescription = data.errorDescription.orEmpty()
            )
        }
        is NidApiResult.Failure -> {
            NidOAuth.emptyWithError(
                error = response.nidOAuthErrorCode,
                errorDescription = response.nidOAuthErrorDes
            )
        }
        is NidApiResult.Exception -> {
            NidOAuth.emptyWithError(
                error = response.nidOAuthErrorCode,
                errorDescription = response.nidOAuthErrorDes
            )
        }
    }
}