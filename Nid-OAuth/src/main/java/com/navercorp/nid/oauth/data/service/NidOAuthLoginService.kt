package com.navercorp.nid.oauth.data.service

import com.navercorp.nid.oauth.data.dto.NidOAuthResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * token 관련 RestApi 호출을 위한 Retrofit 인터페이스
 */
interface NidOAuthLoginService {
    @GET("token")
    suspend fun requestAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String = "authorization_code",
        @Query("state") state: String,
        @Query("code") code: String,
        @Query("oauth_os") oauthOs: String = "android",
        @Query("version") version: String,
        @Query("locale") locale: String
    ): Response<NidOAuthResponse>

    @GET("token")
    suspend fun requestRefreshToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String = "refresh_token",
        @Query("refresh_token") refreshToken: String,
        @Query("oauth_os") oauthOs: String = "android",
        @Query("version") version: String,
        @Query("locale") locale: String
    ) : Response<NidOAuthResponse>

    @GET("token")
    suspend fun requestDeleteToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String = "delete",
        @Query("access_token") accessToken: String,
        @Query("service_provider") serviceProvider: String = "NAVER",
        @Query("oauth_os") oauthOs: String = "android",
        @Query("version") version: String,
        @Query("locale") locale: String
    ) : Response<NidOAuthResponse>
}