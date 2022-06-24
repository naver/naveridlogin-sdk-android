package com.navercorp.nid.profile.api

import com.navercorp.nid.oauth.NidOAuthPreferencesManager
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Response

class NidProfileApi {

    suspend fun requestApi() : Response<NidProfileResponse> {

        val accessToken = NidOAuthPreferencesManager.accessToken ?: ""

        val service = NidProfileService.create()

        return service.requestApi("Bearer $accessToken")
    }


}