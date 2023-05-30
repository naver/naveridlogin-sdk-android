package com.navercorp.nid.oauth.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navercorp.nid.oauth.NidOAuthLogin
import kotlinx.coroutines.launch

class NidOAuthBridgeViewModel : ViewModel() {

    private var isForceDestroyed = true
    fun isNotForcedFinish() {
        isForceDestroyed = false
    }
    fun getIsForceDestroyed(): Boolean = isForceDestroyed

    private var isRotated = false
    fun setIsRotated(value: Boolean) {
        isRotated = value
    }
    fun getIsRotated(): Boolean = isRotated

    private var isLoginActivityStarted = false
    fun startLoginActivity() {
        isLoginActivityStarted = true
    }
    fun getIsLoginActivityStarted(): Boolean = isLoginActivityStarted

    private var _isShowProgress = MutableLiveData<Boolean>()
    val isShowProgress: LiveData<Boolean>
        get() = _isShowProgress

    init {
        _isShowProgress.value = false
    }

    private val _isSuccessRefreshToken = MutableLiveData<Boolean>()
    val isSuccessRefreshToken: LiveData<Boolean>
        get() = _isSuccessRefreshToken

    fun refreshToken() {
        viewModelScope.launch {
            _isShowProgress.value = true
            val isSuccess = NidOAuthLogin().refreshToken()
            _isShowProgress.value = false
            _isSuccessRefreshToken.value = isSuccess
        }
    }


}