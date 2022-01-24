package com.navercorp.nid.scheme.api

import com.navercorp.nid.oauth.NidOAuthConstants
import com.nhn.android.oauth.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

interface NidSchemeService {

    @POST("login/api/log.report")
    fun requestSchemeLog(@QueryMap bodies: MutableMap<String, Any>) : Call<String>

    companion object Factory {

        private const val BASE_URL = "https://nid.naver.com/"

        private val httpClient: OkHttpClient = OkHttpClient()
            .newBuilder()
            .readTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
            .connectTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                }
            }
            .build()

        fun create(): NidSchemeService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(NidSchemeService::class.java)
        }
    }

}