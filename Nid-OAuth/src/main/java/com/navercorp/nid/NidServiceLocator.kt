package com.navercorp.nid

import com.navercorp.nid.core.data.datastore.NidOAuthLocalDataSource
import com.navercorp.nid.core.data.interceptor.NetworkConnectionInterceptor
import com.navercorp.nid.core.data.interceptor.UserAgentInterceptor
import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.oauth.data.datasource.NidOAuthRemoteDataSource
import com.navercorp.nid.oauth.data.repository.NidOAuthRepository
import com.navercorp.nid.oauth.data.service.NidOAuthLoginService
import com.navercorp.nid.oauth.domain.repository.OAuthRepository
import com.navercorp.nid.profile.data.datasource.NidProfileRemoteDataSource
import com.navercorp.nid.profile.data.repository.NidUserProfileRepository
import com.navercorp.nid.profile.data.service.NidProfileService
import com.navercorp.nid.profile.domain.repository.UserProfileRepository
import com.nhn.android.oauth.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 의존성을 한 곳에서 관리하고 주입해주는 ServiceLocator
 */

internal object NidServiceLocator {
    private const val NID_OAUTH_BASE_URL = "https://nid.naver.com/oauth2.0/"
    private const val NID_PROFILE_BASE_URL = "https://openapi.naver.com/v1/"

    /**
     * OkHttpClient dependency (singleton)
     */
    private val httpClient: OkHttpClient = OkHttpClient()
        .newBuilder()
        .readTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
        .connectTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
        .apply {
            addInterceptor(NetworkConnectionInterceptor())
            addInterceptor(UserAgentInterceptor())
            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                addInterceptor(
                    httpLoggingInterceptor.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
        }
        .build()

    /**
     * NidOAuthLoginService dependency (singleton)
     */
    private val nidOAuthLoginService: NidOAuthLoginService = Retrofit.Builder()
        .baseUrl(NID_OAUTH_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NidOAuthLoginService::class.java)

    /**
     * NidProfileService dependency (singleton)
     */
    private val nidProfileService: NidProfileService = Retrofit.Builder()
        .baseUrl(NID_PROFILE_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NidProfileService::class.java)

    /**
     * NidProfileRemoteDataSource dependency
     */
    private fun provideNidProfileRemoteDataSource(): NidProfileRemoteDataSource = NidProfileRemoteDataSource(
        nidProfileService = nidProfileService
    )

    /**
     * NidOAuthRemoteDataSource dependency
     */
    private fun provideNidOAuthRemoteDataSource(): NidOAuthRemoteDataSource = NidOAuthRemoteDataSource(
        nidOAUthLoginService = nidOAuthLoginService
    )

    private fun provideNidOAuthLocalDataSource(): NidOAuthLocalDataSource = NidOAuthLocalDataSource

    /**
     * OAuthRepository dependency
     */
    fun provideOAuthRepository(): OAuthRepository = NidOAuthRepository(
        nidOAuthRemoteDataSource = provideNidOAuthRemoteDataSource(),
        nidOAuthLocalDataSource = provideNidOAuthLocalDataSource(),
    )

    /**
     * UserProfileRepository dependency
     */
    fun provideUserProfileRepository(): UserProfileRepository = NidUserProfileRepository(
        nidProfileRemoteDataSource = provideNidProfileRemoteDataSource()
    )
}