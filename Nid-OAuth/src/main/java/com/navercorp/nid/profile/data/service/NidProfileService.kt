package com.navercorp.nid.profile.data.service

import com.navercorp.nid.profile.data.dto.NidProfileMapResponse
import com.navercorp.nid.profile.data.dto.NidProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * profile 관련 RestApi 호출을 위한 Retrofit 인터페이스
 */
interface NidProfileService {
    @GET("nid/me")
    suspend fun requestApi(
        @Header("Authorization") authorization: String
    ): Response<NidProfileResponse>

    @GET("nid/me")
    suspend fun getProfileMap(
        @Header("Authorization") authorization: String
    ): Response<NidProfileMapResponse>
}