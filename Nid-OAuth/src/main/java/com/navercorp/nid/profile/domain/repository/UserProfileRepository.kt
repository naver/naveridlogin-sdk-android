package com.navercorp.nid.profile.domain.repository

import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.domain.vo.NidProfileMap

internal interface UserProfileRepository {
    suspend fun requestUserProfile(accessToken: String): NidProfile
    suspend fun requestUserProfileMap(accessToken: String): NidProfileMap
}