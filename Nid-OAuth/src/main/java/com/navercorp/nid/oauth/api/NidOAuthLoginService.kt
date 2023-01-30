package com.navercorp.nid.oauth.api

import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.oauth.data.NidOAuthResponse
import com.nhn.android.oauth.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 *
 * Created on 2021.10.15
 * Updated on 2021.10.15
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네아로SDK에서 사용하는 Retrofit 기반
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


    companion object Factory {

        private val BASE_URL = "https://nid.naver.com/oauth2.0/"

        private val httpClient: OkHttpClient = OkHttpClient()
            .newBuilder()
            .readTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
            .connectTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
            .apply {
                addInterceptor(NetworkConnectionInterceptor())
                addInterceptor(UserAgentInterceptor())
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                }
            }
            .build()

        fun create(): NidOAuthLoginService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(NidOAuthLoginService::class.java)
        }
    }

}