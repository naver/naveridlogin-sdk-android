package com.navercorp.nid.profile.api

import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.oauth.api.NetworkConnectionInterceptor
import com.navercorp.nid.oauth.api.UserAgentInterceptor
import com.navercorp.nid.profile.data.NidProfileMap
import com.navercorp.nid.profile.data.NidProfileResponse
import com.nhn.android.oauth.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import java.util.concurrent.TimeUnit

interface NidProfileService {

    @GET("nid/me")
    suspend fun requestApi(
        @Header("Authorization") authorization: String
    ): Response<NidProfileResponse>

    @GET("nid/me")
    suspend fun getProfileMap(
        @Header("Authorization") authorization: String
    ): Response<NidProfileMap>

    companion object Factory {

        private val BASE_URL = "https://openapi.naver.com/v1/"

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

        fun create(): NidProfileService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(NidProfileService::class.java)
        }
    }

}