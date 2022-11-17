package com.navercorp.nid.oauth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.exception.NoConnectivityException
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.api.NidOAuthApi
import com.navercorp.nid.oauth.data.NidOAuthResponse
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.api.NidProfileApi
import com.navercorp.nid.profile.data.NidProfileResponse
import com.navercorp.nid.progress.NidProgressDialog
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.oauth.R
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.net.ssl.*

class NidOAuthLogin {

    companion object {
        const val TAG = "NidOAuthLogin"
    }

    private suspend fun requestAccessToken(context: Context): NidOAuthResponse? {

        val response: Response<NidOAuthResponse>
        try {
            response = withContext(Dispatchers.IO) {
                NidOAuthApi().requestAccessToken(context)
            }
        } catch (t: Throwable) {
            errorHandling(throwable = t)
            if (context is Activity) context.setResult(Activity.RESULT_CANCELED)
            return null
        }

        when (response.code()) {
            in 200 until 300 -> {
                val res = response.body()
                var isSuccess = false
                if (res != null) {
                    if (res.error.isNullOrEmpty() && !res.accessToken.isNullOrEmpty()) {
                        NidOAuthPreferencesManager.apply {
                            accessToken = res.accessToken
                            refreshToken = res.refreshToken
                            expiresAt = System.currentTimeMillis() / 1000 + res.expiresIn
                            tokenType = res.tokenType
                            lastErrorCode = NidOAuthErrorCode.NONE
                            lastErrorDesc = NidOAuthErrorCode.NONE.description
                        }
                        isSuccess = true

                    } else {
                        NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.fromString(res.error)
                        NidOAuthPreferencesManager.lastErrorDesc = res.errorDescription ?: ""
                    }
                }
                when (isSuccess) {
                    true -> if (context is Activity) context.setResult(Activity.RESULT_OK)
                    false -> if (context is Activity) context.setResult(Activity.RESULT_CANCELED)
                }
            }
            in 400 until 500 -> if (context is Activity) context.setResult(Activity.RESULT_CANCELED)
            else -> {
                errorHandling(errorCode = response.code())
                if (context is Activity) context.setResult(Activity.RESULT_CANCELED)
            }
        }
        return response.body()

    }

    private suspend fun requestAccessToken(context: Context, callback: OAuthLoginCallback): NidOAuthResponse? {

        val response: Response<NidOAuthResponse>
        try {
            response = withContext(Dispatchers.IO) {
                NidOAuthApi().requestAccessToken(context)
            }
        } catch (t: Throwable) {
            errorHandling(throwable = t)
            callback.onError(-1, t.toString())
            return null
        }

        when (response.code()) {
            in 200 until 300 -> {
                val res = response.body()
                var isSuccess = false
                if (res != null) {
                    if (res.error.isNullOrEmpty() && !res.accessToken.isNullOrEmpty()) {
                        NidOAuthPreferencesManager.apply {
                            accessToken = res.accessToken
                            refreshToken = res.refreshToken
                            expiresAt = System.currentTimeMillis() / 1000 + res.expiresIn
                            tokenType = res.tokenType
                            lastErrorCode = NidOAuthErrorCode.NONE
                            lastErrorDesc = NidOAuthErrorCode.NONE.description
                        }
                        isSuccess = true

                    } else {
                        NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.fromString(res.error)
                        NidOAuthPreferencesManager.lastErrorDesc = res.errorDescription ?: ""
                    }
                }
                when (isSuccess) {
                    true -> callback.onSuccess()
                    false -> callback.onFailure(response.code(), response.message())
                }
            }
            in 400 until 500 -> callback.onFailure(response.code(), response.message())
            else -> {
                errorHandling(errorCode = response.code())
                callback.onError(response.code(), response.message())
            }
        }
        return response.body()

    }

    private suspend fun requestRefreshAccessToken(context: Context, callback: OAuthLoginCallback): String? {

        val response: Response<NidOAuthResponse>
        try {
            response = withContext(Dispatchers.IO) {
                NidOAuthApi().requestRefreshToken(context)
            }
        } catch (t: Throwable) {
            errorHandling(throwable = t)
            callback.onError(-1, t.toString())
            return null
        }

        when (response.code()) {
            in 200 until 300 -> {
                val res = response.body()
                var isSuccess = false
                if (res != null) {
                    if (res.error.isNullOrEmpty() && !res.accessToken.isNullOrEmpty()) {
                        NidOAuthPreferencesManager.accessToken = res.accessToken
                        NidOAuthPreferencesManager.expiresAt = System.currentTimeMillis() / 1000 + res.expiresIn
                        isSuccess = true
                    } else {
                        NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.fromString(res.error)
                        NidOAuthPreferencesManager.lastErrorDesc = res.errorDescription ?: ""
                    }
                }
                when (isSuccess) {
                    true -> callback.onSuccess()
                    false -> callback.onFailure(response.code(), response.message())
                }
            }
            in 400 until 500 -> callback.onFailure(response.code(), response.message())
            else -> {
                errorHandling(errorCode = response.code())
                callback.onError(response.code(), response.message())
            }
        }
        return response.body()?.accessToken
    }

    fun callRefreshAccessTokenApi(context: Context, callback: OAuthLoginCallback) = CoroutineScope(Dispatchers.Main).launch {
        requestRefreshAccessToken(context, callback)
    }

    fun callDeleteTokenApi(context: Context, callback: OAuthLoginCallback) = CoroutineScope(Dispatchers.Main).launch {
        val response: Response<NidOAuthResponse>
        try {
            response = withContext(Dispatchers.IO) {
                NidOAuthApi().deleteToken(context)
            }
        } catch (t: Throwable) {
            errorHandling(throwable = t)
            callback.onError(-1, t.toString())
            return@launch
        } finally {
            NaverIdLoginSDK.logout()
        }

        var isSuccess = false
        response.body()?.let {
            if ("success".equals(it.result, ignoreCase = true)) {
                isSuccess = true
            }
        }

        when (response.code()) {
            in 200 until 300 -> {
                val res = response.body()
                if (res != null) {
                    if (!isSuccess) {
                        NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.fromString(res.error)
                        NidOAuthPreferencesManager.lastErrorDesc = res.errorDescription ?: ""
                    }
                }
                when (isSuccess) {
                    true -> callback.onSuccess()
                    false -> callback.onFailure(response.code(), response.message())
                }
            }
            in 400 until 500 -> callback.onFailure(response.code(), response.message())
            else -> {
                errorHandling(errorCode = response.code())
                callback.onError(response.code(), response.message())
            }
        }
    }

    fun callProfileApi(callback: NidProfileCallback<NidProfileResponse>) = CoroutineScope(Dispatchers.Main).launch {
        val response: Response<NidProfileResponse>
        try {
            response = withContext(Dispatchers.IO) {
                NidProfileApi().requestApi()
            }
        } catch (t: Throwable) {
            errorHandling(throwable = t)
            callback.onError(-1, t.toString())
            return@launch
        }

        val res = response.body()

        when (response.code()) {
            in 200 until 300 -> {
                if (res?.profile != null && !res.profile.id.isNullOrEmpty()) {
                    callback.onSuccess(res)
                } else {
                    callback.onFailure(response.code(), "${res?.resultCode ?: ""} ${res?.message ?: ""}")
                }
            }
            in 400 until 500 -> callback.onFailure(response.code(), response.message())
            else -> {
                errorHandling(errorCode = response.code())
                callback.onError(response.code(), response.message())
            }
        }
    }

    fun refreshToken(context: Context, launcher: ActivityResultLauncher<Intent>?, callback: OAuthLoginCallback) {
        val progressDialog = NidProgressDialog(context)

        CoroutineScope(Dispatchers.Main).launch {

            progressDialog.showProgress(R.string.naveroauthlogin_string_getting_token)
            val at = requestRefreshAccessToken(context, object: OAuthLoginCallback {
                override fun onSuccess() {
                    NidLog.d(TAG, "requestRefreshAccessToken | onSuccess()")
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    NidLog.d(TAG, "requestRefreshAccessToken | onFailure()")
                }

                override fun onError(errorCode: Int, message: String) {
                    NidLog.d(TAG, "requestRefreshAccessToken | onError()")
                }

            })
            progressDialog.hideProgress()

            if (at.isNullOrEmpty()) {
                val orientation = context.resources.configuration.orientation
                val intent = Intent(context, NidOAuthBridgeActivity::class.java).apply {
                    putExtra("orientation", orientation)
                }

                launcher?.launch(intent) ?: context.startActivity(intent)
            } else {
                callback.onSuccess()
            }
        }
    }

    fun accessToken(context: Context, callback: OAuthLoginCallback?) {
        val progressDialog = NidProgressDialog(context)

        CoroutineScope(Dispatchers.Main).launch {

            progressDialog.showProgress(R.string.naveroauthlogin_string_getting_token)
            val res = if (callback == null) {
                requestAccessToken(context)
            } else {
                requestAccessToken(context, callback)
            }
            progressDialog.hideProgress()

            res?.let {
                val isSuccess = it.error.isNullOrEmpty() && !it.accessToken.isNullOrEmpty()

                if(!isSuccess && (NidOAuthErrorCode.NONE == NidOAuthErrorCode.fromString(it.error))){
                    val errorCode = NidOAuthErrorCode.CLIENT_USER_CANCEL
                    NidOAuthPreferencesManager.lastErrorCode = errorCode
                    NidOAuthPreferencesManager.lastErrorDesc = errorCode.description
                }
            }
            if (context is Activity && !context.isFinishing) {
                context.finish()
            }
        }
    }

    private fun errorHandling(throwable: Throwable) {
        when (throwable) {
            is NoConnectivityException, is IOException, is SocketTimeoutException, is SocketException -> {
                NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR
                NidOAuthPreferencesManager.lastErrorDesc = NidOAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR.description
            }
            is SSLPeerUnverifiedException, is SSLProtocolException, is SSLKeyException, is SSLHandshakeException, is SSLException -> {
                NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.CLIENT_ERROR_CERTIFICATION_ERROR
                NidOAuthPreferencesManager.lastErrorDesc = NidOAuthErrorCode.CLIENT_ERROR_CERTIFICATION_ERROR.description
            }
            else -> {
                NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.ERROR_NO_CATAGORIZED
                NidOAuthPreferencesManager.lastErrorDesc = NidOAuthErrorCode.ERROR_NO_CATAGORIZED.description
            }
        }
        NidLog.e(TAG, "$throwable")
    }

    private fun errorHandling(errorCode: Int) {
        when (errorCode) {
            500 -> {
                NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.SERVER_ERROR_SERVER_ERROR
                NidOAuthPreferencesManager.lastErrorDesc = NidOAuthErrorCode.SERVER_ERROR_SERVER_ERROR.description
            }
            503 -> {
                NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.SERVER_ERROR_TEMPORARILY_UNAVAILABLE
                NidOAuthPreferencesManager.lastErrorDesc = NidOAuthErrorCode.SERVER_ERROR_TEMPORARILY_UNAVAILABLE.description
            }
            else -> {
                NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.ERROR_NO_CATAGORIZED
                NidOAuthPreferencesManager.lastErrorDesc = NidOAuthErrorCode.ERROR_NO_CATAGORIZED.description
            }
        }
    }

}