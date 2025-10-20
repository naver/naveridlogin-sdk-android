package com.navercorp.nid.profile.data.mapper

import com.navercorp.nid.core.base.NidApiResult
import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.profile.data.dto.NidProfileDetailResponse
import com.navercorp.nid.profile.data.dto.NidProfileMapResponse
import com.navercorp.nid.profile.data.dto.NidProfileResponse
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.domain.vo.NidProfileDetail
import com.navercorp.nid.profile.domain.vo.NidProfileMap
import kotlin.collections.orEmpty

/**
 * DTO를 VO로 변환하는 Mapper
 * 실패할 경우, errorCode 및 errorDescription이 세팅된 빈 VO 반환
 */
internal object NidProfileMapper {
    fun toNidProfile(
        response: NidApiResult<NidProfileResponse>,
    ): NidProfile = when(response) {
        is NidApiResult.Success -> {
            val data = response.data
            NidProfile(
                profile = data.profile?.let {
                    toNidProfileDetail(it)
                } ?: NidProfileDetail.empty(),
                error = NidOAuthErrorCode.fromString(data.resultCode),
                errorDescription = data.message.orEmpty(),
            )
        }
        is NidApiResult.Failure -> NidProfile(
            profile = NidProfileDetail.empty(),
            error = response.nidOAuthErrorCode,
            errorDescription = response.nidOAuthErrorDes,
        )
        is NidApiResult.Exception -> NidProfile(
            profile = NidProfileDetail.empty(),
            error = response.nidOAuthErrorCode,
            errorDescription = response.nidOAuthErrorDes,
        )
    }

    fun toNidProfileDetail(
        response: NidProfileDetailResponse
    ): NidProfileDetail = NidProfileDetail(
        id = response.id.orEmpty(),
        nickname = response.nickname.orEmpty(),
        name = response.name.orEmpty(),
        email = response.email.orEmpty(),
        gender = response.gender.orEmpty(),
        age = response.age.orEmpty(),
        birthday = response.birthday.orEmpty(),
        profileImage = response.profileImage.orEmpty(),
        birthYear = response.birthYear.orEmpty(),
        mobile = response.mobile.orEmpty(),
        ci = response.ci.orEmpty(),
        encId = response.encId.orEmpty(),
    )

    fun toNidProfileMap(
        response: NidApiResult<NidProfileMapResponse>,
    ): NidProfileMap = when(response) {
        is NidApiResult.Success -> {
            val data = response.data
            NidProfileMap(
                error = NidOAuthErrorCode.fromString(data.resultCode),
                errorDescription = data.message.orEmpty(),
                profile = data.profile.orEmpty(),
            )
        }
        is NidApiResult.Failure -> NidProfileMap(
            profile = emptyMap(),
            error = response.nidOAuthErrorCode,
            errorDescription = response.nidOAuthErrorDes
        )
        is NidApiResult.Exception -> NidProfileMap(
            profile = emptyMap(),
            error = response.nidOAuthErrorCode,
            errorDescription = response.nidOAuthErrorDes,
        )
    }
}