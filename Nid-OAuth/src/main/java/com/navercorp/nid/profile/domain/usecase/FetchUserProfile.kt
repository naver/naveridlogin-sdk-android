package com.navercorp.nid.profile.domain.usecase

import com.navercorp.nid.profile.domain.repository.UserProfileRepository
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.domain.vo.NidProfileMap

/**
 * 유저 정보 fetch Usecase
 *
 * @property oauthRepository OAuth 관련 Repository
 * @property userProfileRepository 유저 정보 관련 Repository
 */
internal class FetchUserProfile(
    private val userProfileRepository: UserProfileRepository,
) {
    suspend fun getUserProfile(
        accessToken: String,
    ): NidProfile {
        return userProfileRepository.requestUserProfile(
            accessToken = accessToken,
        )
    }

    suspend fun getUserProfileMap(
        accessToken: String,
    ): NidProfileMap {
        return userProfileRepository.requestUserProfileMap(
            accessToken = accessToken,
        )
    }
}