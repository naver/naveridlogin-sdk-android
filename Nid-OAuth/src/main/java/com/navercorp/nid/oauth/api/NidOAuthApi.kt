package com.navercorp.nid.oauth.api

import android.content.Context
import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.oauth.NidOAuthPreferencesManager
import com.navercorp.nid.oauth.data.NidOAuthResponse
import com.navercorp.nid.util.NidDeviceUtil
import retrofit2.Response

/**
 *
 * Created on 2021.10.15
 * Updated on 2021.10.15
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네아로 SDK Api 호출을 위한 클래스
 * - 네아로 SDK는 View가 없으므로 ViewModel을 차용해서 Repository로 처리하지 않음
 */
class NidOAuthApi {

    suspend fun requestAccessToken(): Response<NidOAuthResponse> {

        val clientId = NidOAuthPreferencesManager.clientId ?: ""
        val clientSecret = NidOAuthPreferencesManager.clientSecret ?: ""
        val state = NidOAuthPreferencesManager.state ?: ""
        val code = NidOAuthPreferencesManager.code ?: ""

        val service = NidOAuthLoginService.create()

        return service.requestAccessToken(
            clientId = clientId,
            clientSecret = clientSecret,
            state = state,
            code = code,
            version = "android-${NidOAuthConstants.SDK_VERSION}",
            locale = NidDeviceUtil.getLocale()
        )
    }

    suspend fun requestRefreshToken (): Response<NidOAuthResponse> {

        val clientId = NidOAuthPreferencesManager.clientId ?: ""
        val clientSecret = NidOAuthPreferencesManager.clientSecret ?: ""
        val refreshToken = NidOAuthPreferencesManager.refreshToken ?: ""

        val service = NidOAuthLoginService.create()

        return service.requestRefreshToken(
            clientId = clientId,
            clientSecret = clientSecret,
            refreshToken = refreshToken,
            version = "android-${NidOAuthConstants.SDK_VERSION}",
            locale = NidDeviceUtil.getLocale()
        )
    }

    suspend fun deleteToken(): Response<NidOAuthResponse> {

        val clientId = NidOAuthPreferencesManager.clientId ?: ""
        val clientSecret = NidOAuthPreferencesManager.clientSecret ?: ""
        val accessToken = NidOAuthPreferencesManager.accessToken ?: ""

        val service = NidOAuthLoginService.create()

        return service.requestDeleteToken(
            clientId = clientId,
            clientSecret = clientSecret,
            accessToken = accessToken,
            version = "android-${NidOAuthConstants.SDK_VERSION}",
            locale = NidDeviceUtil.getLocale()
        )
    }

}