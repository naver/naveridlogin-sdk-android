package com.navercorp.nid.profile.data.datasource

import com.navercorp.nid.core.base.handleApiResult
import com.navercorp.nid.profile.data.mapper.NidProfileMapper
import com.navercorp.nid.profile.data.service.NidProfileService
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.domain.vo.NidProfileMap

/**
 * Profile 관련 네아로 SDK Api 호출을 위한 dataSource
 */
internal class NidProfileRemoteDataSource(
    private val nidProfileService: NidProfileService
) {
    /**
     * 사용자 프로필 조회 요청
     *
     * @param accessToken 발급 받은 access token
     */
    suspend fun requestUserProfile(
        accessToken: String,
    ) : NidProfile {
        val apiResult = handleApiResult {
            nidProfileService.requestApi("Bearer $accessToken")
        }
        return NidProfileMapper.toNidProfile(apiResult)
    }

    /**
     * 사용자 프로필 맵 조회 요청
     *
     * @param accessToken 발급 받은 access token
     */
    suspend fun requestUserProfileMap(
        accessToken: String,
    ): NidProfileMap {
        val apiResult = handleApiResult {
            nidProfileService.getProfileMap("Bearer $accessToken")
        }
        return NidProfileMapper.toNidProfileMap(apiResult)
    }
}