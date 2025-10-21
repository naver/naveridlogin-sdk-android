package com.navercorp.nid.oauth

import com.navercorp.nid.NidOAuth
import com.navercorp.nid.NidServiceLocator
import com.navercorp.nid.core.log.NidLog
import com.navercorp.nid.oauth.domain.usecase.Login
import com.navercorp.nid.oauth.util.NidOAuthCallback
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.domain.vo.NidProfileMap
import com.navercorp.nid.profile.util.NidProfileCallback
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NidOAuthLogin {
    private val login by lazy {
        Login(NidServiceLocator.provideOAuthRepository())
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        NidLog.e(TAG, "CoroutineExceptionHandler $throwable")
    }
    private val ioScope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.requestLogin(cotext, callback) instead.",
        replaceWith = ReplaceWith("NidOAuth.requestLogin(context, callback)"),
    )
    fun callRefreshAccessTokenApi(callback: NidOAuthCallback) = ioScope.launch {
        val oauthToken = login.refreshToken()
        withContext(Dispatchers.Main) {
            if (oauthToken.isOAuthSuccess) {
                callback.onSuccess()
            } else {
                callback.onFailure(oauthToken.error.code, oauthToken.errorDescription)
            }
        }
    }

    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.disconnect(callback) instead.",
        replaceWith = ReplaceWith("NidOAuth.disconnect(callback)"),
    )
    fun callDeleteTokenApi(callback: NidOAuthCallback) {
        NidOAuth.disconnect(callback)
    }

    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getUserProfile(callback) instead.",
        replaceWith = ReplaceWith("NidOAuth.getUserProfile(callback)"),
    )
    fun callProfileApi(callback: NidProfileCallback<NidProfile>) {
        NidOAuth.getUserProfile(callback)
    }

    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getUserProfileMap(callback) instead.",
        replaceWith = ReplaceWith("NidOAuth.getUserProfileMap(callback)"),
    )
    fun getProfileMap(callback: NidProfileCallback<NidProfileMap>) {
        NidOAuth.getUserProfileMap(callback)
    }

    companion object {
        const val TAG = "NidOAuthLogin"
    }
}