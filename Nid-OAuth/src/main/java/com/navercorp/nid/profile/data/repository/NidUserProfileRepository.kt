package com.navercorp.nid.profile.data.repository

import com.navercorp.nid.profile.data.datasource.NidProfileRemoteDataSource
import com.navercorp.nid.profile.domain.repository.UserProfileRepository
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.domain.vo.NidProfileMap

/**
 * UserProfile 관련 네아로 SDK Api 호출을 위한 Repository
 *
 * @param nidProfileRemoteDataSource UserProfile 관련 Api 호출을 위한 dataSource
 */
internal class NidUserProfileRepository(
    private val nidProfileRemoteDataSource: NidProfileRemoteDataSource
): UserProfileRepository {
    override suspend fun requestUserProfile(accessToken: String): NidProfile =
        nidProfileRemoteDataSource.requestUserProfile(accessToken)

    override suspend fun requestUserProfileMap(accessToken: String): NidProfileMap =
        nidProfileRemoteDataSource.requestUserProfileMap(accessToken)
}