package com.navercorp.nid.oauth.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.NidServiceLocator
import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.core.log.NidLog
import com.navercorp.nid.core.util.AndroidVer
import com.navercorp.nid.core.util.NidApplicationUtil
import com.navercorp.nid.oauth.domain.enum.LoginBehavior
import com.navercorp.nid.oauth.domain.usecase.SetUpOAuthInfo
import com.navercorp.nid.oauth.domain.vo.LoginInfo
import com.navercorp.nid.oauth.view.NidProgressDialog
import com.navercorp.nid.oauth.viewModel.NidOAuthBridgeViewModel
import com.nhn.android.oauth.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *
 * Created on 2021.10.22
 * Updated on 2021.10.22
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * refresh token 이 있는 경우 refresh token 으로 access token 을 가져옴
 * access token 가져오는 걸 실패하는 경우엔 로그인 창을 보여줌 (네이버앱 or 커스텀탭 호출)
 */
class NidOAuthBridgeActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "NidOAuthBridgeActivity"
        private const val ORIENTATION = "orientation"
        private const val AUTH_TYPE = "auth_type"

        fun getIntent(context: Context, authType: String? = null): Intent =
            Intent(context, NidOAuthBridgeActivity::class.java).apply {
                putExtra(ORIENTATION, context.resources.configuration.orientation)
                authType?.let { putExtra(AUTH_TYPE, it) }
            }
    }

    private val viewModel by viewModels<NidOAuthBridgeViewModel>()

    private val progress by lazy {
        NidProgressDialog(this)
    }

    private var authType: String? = null

    private val setUpOAuthInfo by lazy {
        SetUpOAuthInfo(NidServiceLocator.provideOAuthRepository())
    }

    private val naverappLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.isNotForcedFinish()

        requestLogin(result.data)
    }

    private val customTabLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.isNotForcedFinish()

        requestLogin(result.data)
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        NidLog.d(TAG, "called onCreate()")

        initData()

        NidLog.d(TAG, "onCreate() | isLoginActivityStarted : ${viewModel.getIsLoginActivityStarted()}")

        viewModel.setIsRotated(false)

        if (!viewModel.getIsLoginActivityStarted()) {
            viewModel.startLoginActivity()
            NidLog.d(TAG, "onCreate() first init.")
            val refreshToken = NidOAuth.getRefreshToken()
            if (refreshToken.isNullOrEmpty().not() && authType.isNullOrEmpty()) {
                viewModel.refreshToken()
            } else {
                lifecycleScope.launch {
                    startLoginActivity()
                }
            }
        }

        viewModel.isSuccessRefreshToken.observe(this) { isSuccess ->
            NidLog.d(TAG, "isSuccessRefreshToken : $isSuccess")
            if (isSuccess) {
                viewModel.isNotForcedFinish()
                NidOAuth.oauthLoginCallback?.onSuccess()
                setResult(RESULT_OK)
                finish()
            } else {
                lifecycleScope.launch {
                    startLoginActivity()
                }
            }
        }

        viewModel.isShowProgress.observe(this) { isShowProgress ->
            if (isShowProgress) {
                progress.showProgress(R.string.naveroauthlogin_string_getting_token)
            } else {
                progress.hideProgress()
            }

        }
    }

    private fun initData() {
        val screenOrientation = intent.getIntExtra("orientation", 1)
        requestedOrientation = screenOrientation
        authType = intent.getStringExtra("auth_type")
    }

    /**
     * 각 상황별로 네이버 로그인을 진행할 액티비티를 실행
     * @param mOAuthLoginData 네아로 메타 정보
     */
    private suspend fun startLoginActivity() {
        NidLog.d(TAG, "startLoginActivity()")

        // 명시적으로 Behavior를 지정한 경우 해당 인증 방식만 제공한다.
        when (NidOAuth.behavior) {
            LoginBehavior.NAVERAPP -> {
                if (!tryOAuthByNaverapp()) {
                    oauthFinish(Intent(), NidOAuthErrorCode.NO_APP_FOR_AUTHENTICATION, "기기에 네이버앱이 없습니다.")
                }
                return
            }
            LoginBehavior.CUSTOMTABS -> {
                if (!tryOAuthByCustomTab()) {
                    if (NidApplicationUtil.isNotCustomTabsAvailable(this) && NidApplicationUtil.isExistNaverApp(this) && tryOAuthByNaverapp()) {
                        return
                    }
                    oauthFinish(Intent(), NidOAuthErrorCode.NO_APP_FOR_AUTHENTICATION, "커스텀탭을 실행할 수 없습니다.")
                }
                return
            }

            // 기본값의 경우 OAuth 인증 시도의 우선순위는 아래와 같다.
            // 1. 네이버앱
            // 2. 크롬 커스텀탭
            LoginBehavior.DEFAULT -> {
                if (tryOAuthByNaverapp()) return
                if (tryOAuthByCustomTab()) return
                viewModel.isNotForcedFinish()
                oauthFinish(Intent(), NidOAuthErrorCode.NO_APP_FOR_AUTHENTICATION, "인증을 진행할 수 있는 앱이 없습니다.")
            }
        }
    }

    /**
     * 네이버 앱으로 로그인 시도
     * @param loginData 네아로 메타 정보
     * @return 실행 여부
     */
    private suspend fun tryOAuthByNaverapp(): Boolean {
        val intent = NidOAuthIntent.Builder(this)
            .setType(NidOAuthIntent.Type.NAVER_APP)
            .setAuthType(authType)
            .build()
        return if (intent == null) {
            false
        } else if (intent.data != null) {
            try {
                startActivity(intent)
                viewModel.isNotForcedFinish()

                val appUpdateError = NidOAuthErrorCode.NEED_APP_UPDATE
                NidOAuth.oauthLoginCallback?.onFailure(appUpdateError.code, "네이버앱 업데이트가 필요합니다.")
                setResult(RESULT_CANCELED)
                finish()
                true
            } catch (_: ActivityNotFoundException) {
                false
            }
        } else {
            naverappLauncher.launch(intent)
            true
        }
    }

    /**
     * 커스텀 탭으로 로그인 시도
     * @param loginData 네아로 메타 정보
     * @return 실행 여부
     */
    private suspend fun tryOAuthByCustomTab(): Boolean {
        val intent = NidOAuthIntent.Builder(this)
            .setType(NidOAuthIntent.Type.CUSTOM_TABS)
            .setAuthType(authType)
            .build()
        return if (intent == null) {
            false
        } else {
            customTabLauncher.launch(intent)
            true
        }
    }

    /**
     * 네이버앱, 커스텀탭 실행 결과 처리
     * - 로그인 결과를 가지고 AccessToken / RefreshToken 발급 요청
     */

    private fun requestLogin(
        data: Intent?,
    ) = CoroutineScope(Dispatchers.Main).launch {
        if (data == null) {
            finishWithErrorResult(NidOAuthErrorCode.CLIENT_USER_CANCEL)
            return@launch
        }

        val state = data.getStringExtra(NidOAuthIntent.Companion.OAUTH_RESULT_STATE)
        val code = data.getStringExtra(NidOAuthIntent.Companion.OAUTH_RESULT_CODE)
        val errorCode = data.getStringExtra(NidOAuthIntent.Companion.OAUTH_RESULT_ERROR_CODE)
        val errorDescription = data.getStringExtra(NidOAuthIntent.Companion.OAUTH_RESULT_ERROR_DESCRIPTION)
        val loginInfo = LoginInfo(
            oauthCode = code,
            oauthState = state,
            errorCode = errorCode,
            errorDesc = errorDescription
        )

        if (code.isNullOrEmpty()) {
            finishWithErrorResult(data)
        } else {
            login(loginInfo)
        }
    }

    private suspend fun login(
        loginInfo: LoginInfo,
    ) {
        val progressDialog = NidProgressDialog(this@NidOAuthBridgeActivity)
        progressDialog.showProgress(R.string.naveroauthlogin_string_getting_token)

        val callback = NidOAuth.oauthLoginCallback
        if (callback != null) {
            val oauthToken = viewModel.requestLogin(loginInfo)
            if (oauthToken.isOAuthInterrupted || !oauthToken.isOAuthSuccess) {
                callback.onFailure(oauthToken.error.code, oauthToken.errorDescription)
            } else {
                callback.onSuccess()
            }
        } else {
            val oauthToken = viewModel.requestLogin(loginInfo)
            if (oauthToken.isOAuthInterrupted || !oauthToken.isOAuthSuccess) {
                setResult(RESULT_CANCELED)
            } else {
                setResult(RESULT_OK)
            }
        }

        progressDialog.hideProgress()
        if (!this.isFinishing) {
            finish()
        }
    }

    /**
     * Activity Lifecycle 콜백 함수 오버라이딩
     */

    override fun onResume() {
        super.onResume()
        NidLog.d(TAG, "called onResume()")
    }

    override fun onPause() {
        super.onPause()
        NidLog.d(TAG, "called onPause()")
    }

    override fun onDestroy() {
        super.onDestroy()
        NidLog.d(TAG, "called onDestroy()")

        if (viewModel.getIsForceDestroyed() && !viewModel.getIsRotated()) {
            val errorCode = NidOAuthErrorCode.ACTIVITY_IS_SINGLE_TASK
            setUpLastErrorInfo(
                errorCode = errorCode.code,
                errorDesc = "OAuthLoginActivity is destroyed."
            )

            NidOAuth.oauthLoginCallback?.onFailure(errorCode.code, "OAuthLoginActivity is destroyed.")
            setResult(RESULT_CANCELED)
        }
    }

    /**
     * Error 저장 및 Activity 종료
     */

    private fun finishWithErrorResult(intent: Intent) {
        val errorCode = intent.getStringExtra(NidOAuthIntent.Companion.OAUTH_RESULT_ERROR_CODE)
        val errorDesc = intent.getStringExtra(NidOAuthIntent.Companion.OAUTH_RESULT_ERROR_DESCRIPTION).orEmpty()
        oauthFinish(intent, NidOAuthErrorCode.INSTANCE.fromString(errorCode), errorDesc)
    }

    private fun finishWithErrorResult(errCode: NidOAuthErrorCode) {
        val intent = Intent().apply {
            // TODO code 넣을때 state체크 처리해야 함.
            putExtra(NidOAuthIntent.Companion.OAUTH_RESULT_ERROR_CODE, errCode.code)
            putExtra(NidOAuthIntent.Companion.OAUTH_RESULT_ERROR_DESCRIPTION, errCode.description)
        }

        oauthFinish(intent, errCode, errCode.description)
    }

    private fun oauthFinish(intent: Intent, errorCode: NidOAuthErrorCode, errorDescription: String) {
        setUpLastErrorInfo(
            errorCode = errorCode.code,
            errorDesc = errorDescription,
        )

        viewModel.isNotForcedFinish()
        NidOAuth.oauthLoginCallback?.onFailure(errorCode.code, errorDescription)
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    private fun setUpLastErrorInfo(
        errorCode: String,
        errorDesc: String,
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            setUpOAuthInfo.setUpLastErrorInfo(
                errorCode = errorCode,
                errorDesc = errorDesc
            )
        } catch (e: Exception) {
            NidLog.e(TAG, "setUpLastErrorInfo error : ${e.message}")
        }
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (Build.VERSION.SDK_INT != AndroidVer.API_26_OREO) {
            super.setRequestedOrientation(requestedOrientation)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        NidLog.d(TAG, "called onConfigurationChanged()")
        viewModel.setIsRotated(true)
    }
}