package com.navercorp.nid.oauth.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.navercorp.nid.NidServiceLocator
import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.core.log.NidLog
import com.navercorp.nid.oauth.domain.usecase.Login
import com.navercorp.nid.oauth.domain.vo.LoginInfo
import com.navercorp.nid.oauth.domain.vo.Token
import com.navercorp.nid.oauth.util.NidOAuthCallback
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NidOAuthBridgeViewModel : ViewModel() {
    private val login by lazy {
        Login(
            oauthRepository = NidServiceLocator.provideOAuthRepository()
        )
    }

    /**
     * 에러 핸들러가 포함된 IO 스코프
     */
    fun createErrorHandlingIoScope(
        errorHandle: ((Throwable) -> Unit)? = null,
    ) = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        NidLog.e(TAG, "CoroutineExceptionHandler got $throwable")
        errorHandle?.invoke(throwable)
    })

    /**
     * ProgressBar 표시 여부 LiveData
     */

    private var _isShowProgress = MutableLiveData<Boolean>()
    val isShowProgress: LiveData<Boolean>
        get() = _isShowProgress

    init {
        _isShowProgress.value = false
    }

    /**
     * NidOAuthBridgeActivity 강제 종료 여부 저장
     */
    private var isForceDestroyed = true
    fun isNotForcedFinish() {
        isForceDestroyed = false
    }
    fun getIsForceDestroyed(): Boolean = isForceDestroyed

    /**
     * 화면 회전 여부 저장
     */
    private var isRotated = false
    fun setIsRotated(value: Boolean) {
        isRotated = value
    }
    fun getIsRotated(): Boolean = isRotated

    /**
     * 로그인 Activity 시작 여부 저장
     */
    private var isLoginActivityStarted = false
    fun startLoginActivity() {
        isLoginActivityStarted = true
    }
    fun getIsLoginActivityStarted(): Boolean = isLoginActivityStarted

    /**
     * Refresh accessToken 성공 여부 LiveData
     */
    private val _isSuccessRefreshToken = MutableLiveData<Boolean>()
    val isSuccessRefreshToken: LiveData<Boolean>
        get() = _isSuccessRefreshToken

    /**
     * Refresh accessToken 진행
     */
    fun refreshToken() {
        val oauthLoginCallback = object: NidOAuthCallback {
            override fun onSuccess() {
                NidLog.d(TAG, "refreshToken | onSuccess()")
                _isShowProgress.postValue(false)
                _isSuccessRefreshToken.postValue(true)
            }

            override fun onFailure(errorCode: String, errorDesc: String) {
                NidLog.d(TAG, "refreshToken | onFailure() errorCode: $errorCode, errorDesc: $errorDesc")
                _isShowProgress.postValue(false)
                _isSuccessRefreshToken.postValue(false)
            }
        }

        createErrorHandlingIoScope(
            errorHandle = { throwable ->
                val unknownError = NidOAuthErrorCode.ERROR_NO_CATAGORIZED
                oauthLoginCallback.onFailure(unknownError.code, unknownError.description)
            }
        ).launch {
            _isShowProgress.postValue(true)
            val oauthToken = login.refreshToken()

            if (oauthToken.isOAuthSuccess) {
                oauthLoginCallback.onSuccess()
            } else {
                oauthLoginCallback.onFailure(oauthToken.error.code, oauthToken.errorDescription)
            }
        }
    }

    suspend fun requestLogin(
        loginInfo: LoginInfo,
    ): Token = withContext(Dispatchers.IO) {
        login(loginInfo)
    }

    companion object {
        private const val TAG = "NidOAuthBridgeViewModel"
    }
}