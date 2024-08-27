package com.navercorp.nid.oauth

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.NaverIdLoginSDK.behavior
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.NidOAuthErrorCode.INSTANCE.fromString
import com.navercorp.nid.oauth.viewModel.NidOAuthBridgeViewModel
import com.navercorp.nid.progress.NidProgressDialog
import com.navercorp.nid.util.AndroidVer
import com.navercorp.nid.util.NidApplicationUtil
import com.nhn.android.oauth.R


/**
 *
 * Created on 2021.10.22
 * Updated on 2021.10.22
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * TODO : Write a role for this class.
 * refresh token 이 있는 경우 refresh token 으로 access token 을 가져옴
 * access token 가져오는 걸 실패하는 경우엔 로그인 창을 보여줌 (네이버앱을 통하거나, 직접 webview를 생성해서..)
 */
class NidOAuthBridgeActivity : AppCompatActivity() {

    companion object {
        const val TAG = "NidOAuthBridgeActivity"
    }

    private val viewModel by viewModels<NidOAuthBridgeViewModel>()

    private val progress by lazy {
        NidProgressDialog(this)
    }

    private var authType: String? = null

    private val naverappLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.isNotForcedFinish()

        requestAccessTokenWithData(result.data)
    }

    private val customTabLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.isNotForcedFinish()

        requestAccessTokenWithData(result.data)
    }

    private fun requestAccessTokenWithData(data: Intent?) {
        if (data == null) {
            finishWithErrorResult(NidOAuthErrorCode.CLIENT_USER_CANCEL)
            return
        }

        val state = data.getStringExtra(NidOAuthIntent.OAUTH_RESULT_STATE)
        val code = data.getStringExtra(NidOAuthIntent.OAUTH_RESULT_CODE)
        val errorCode = data.getStringExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_CODE)
        val errorDescription = data.getStringExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_DESCRIPTION)

        NidOAuthPreferencesManager.apply {
            this.code = code
            this.state = state
            this.errorCode = errorCode
            this.errorDescription = errorDescription
        }

        if (code.isNullOrEmpty()) {
            finishWithErrorResult(data)
        } else {
            NidOAuthLogin().accessToken(this, NaverIdLoginSDK.oauthLoginCallback)
        }
    }

    private fun initData(): Boolean {

        if (NaverIdLoginSDK.isInitialized().not()) {
            finishWithErrorResult(NidOAuthErrorCode.SDK_IS_NOT_INITIALIZED)
            return false
        }

        if (NidOAuthPreferencesManager.clientId.isNullOrEmpty()) {
			finishWithErrorResult(NidOAuthErrorCode.CLIENT_ERROR_NO_CLIENTID)
			return false
		}
		if (NidOAuthPreferencesManager.clientSecret.isNullOrEmpty()) {
			finishWithErrorResult(NidOAuthErrorCode.CLIENT_ERROR_NO_CLIENTSECRET)
			return false
		}
		if (NidOAuthPreferencesManager.clientName.isNullOrEmpty()) {
			finishWithErrorResult(NidOAuthErrorCode.CLIENT_ERROR_NO_CLIENTNAME)
			return false
		}
		if (NidOAuthPreferencesManager.callbackUrl.isNullOrEmpty()) {
			finishWithErrorResult(NidOAuthErrorCode.CLIENT_ERROR_NO_CALLBACKURL)
			return false
		}

        val screenOrientation = intent.getIntExtra("orientation", 1)
        requestedOrientation = screenOrientation

        authType = intent.getStringExtra("auth_type")

		return true

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NidLog.d(TAG, "called onCreate()")

        NaverIdLoginSDK.init(this)

        if (!initData()) {
            return
        }

        NidLog.d(TAG, "onCreate() | isLoginActivityStarted : ${viewModel.getIsLoginActivityStarted()}")

        viewModel.setIsRotated(false)

        if (!viewModel.getIsLoginActivityStarted()) {
            viewModel.startLoginActivity()
            NidLog.d(TAG, "onCreate() first init.")
            val refreshToken = NaverIdLoginSDK.getRefreshToken()
            if (refreshToken.isNullOrEmpty().not() && authType.isNullOrEmpty()) {
                viewModel.refreshToken()
            } else {
                startLoginActivity()
            }
        }

        viewModel.isSuccessRefreshToken.observe(this) { isSuccess ->
            NidLog.d(TAG, "isSuccessRefreshToken : $isSuccess")
            if (isSuccess) {
                viewModel.isNotForcedFinish()
                NaverIdLoginSDK.oauthLoginCallback?.onSuccess()
                setResult(RESULT_OK)
                finish()
                overridePendingTransition(0, 0)
            } else {
                startLoginActivity()
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

    override fun onDestroy() {
        super.onDestroy()

        NidLog.d(TAG, "called onDestroy()")

        if (viewModel.getIsForceDestroyed() && !viewModel.getIsRotated()) {
            NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.ACTIVITY_IS_SINGLE_TASK
            NidOAuthPreferencesManager.lastErrorDesc = "OAuthLoginActivity is destroyed."

            NaverIdLoginSDK.oauthLoginCallback?.onError(-1, "OAuthLoginActivity is destroyed.")
            setResult(RESULT_CANCELED)
        }
    }

    /**
     * 각 상황별로 네이버 로그인을 진행할 액티비티를 실행
     * @param mOAuthLoginData 네아로 메타 정보
     */
    private fun startLoginActivity() {
        NidLog.d(TAG, "startLoginActivity()")

        // 명시적으로 Behavior를 지정한 경우 해당 인증 방식만 제공한다.

        when (behavior) {
            NidOAuthBehavior.NAVERAPP -> {
                if (!tryOAuthByNaverapp()) {
                    oauthFinish(Intent(), NidOAuthErrorCode.NO_APP_FOR_AUTHENTICATION, "기기에 네이버앱이 없습니다.")
                }
                return
            }
            NidOAuthBehavior.CUSTOMTABS -> {
                if (!tryOAuthByCustomTab()) {
                    if (NidApplicationUtil.isNotCustomTabsAvailable(this) && NidApplicationUtil.isExistNaverApp(this) && tryOAuthByNaverapp()) {
                        return
                    }
                    oauthFinish(Intent(), NidOAuthErrorCode.NO_APP_FOR_AUTHENTICATION, "커스텀탭을 실행할 수 없습니다.")
                }
                return
            }
            NidOAuthBehavior.WEBVIEW -> {
                startLoginWebviewActivity()
                return
            }
            // 기본값의 경우 OAuth 인증 시도의 우선순위는 아래와 같다.
            // 1. 네이버앱
            // 2. 크롬 커스텀탭
            NidOAuthBehavior.DEFAULT -> {
                if (tryOAuthByNaverapp()) return
                if (tryOAuthByCustomTab()) return
                viewModel.isNotForcedFinish()
                oauthFinish(Intent(), NidOAuthErrorCode.NO_APP_FOR_AUTHENTICATION, "인증을 진행할 수 있는 앱이 없습니다.")
            }
        }
//        startLoginWebviewActivity()
    }

    /**
     * 네이버 앱으로 로그인 시도
     * @param loginData 네아로 메타 정보
     * @return 실행 여부
     */
    private fun tryOAuthByNaverapp(): Boolean {
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
                NaverIdLoginSDK.oauthLoginCallback?.onError(-1, "네이버앱 업데이트가 필요합니다.")
                setResult(Activity.RESULT_CANCELED)
                finish()
                true
            } catch (e: ActivityNotFoundException) {
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
    private fun tryOAuthByCustomTab(): Boolean {
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
     * login webview 를 실행함
     * 네이버 앱도 없고 커스텀 탭도 불가능한 상황일 때 혹은 필요에 의해 웹뷰만 호출
     */
    @Deprecated("WebView is deprecated")
    private fun startLoginWebviewActivity() {
        Toast.makeText(this, "더이상 인앱브라우저(웹뷰)는 사용할 수 없습니다.(WebView is deprecated)", Toast.LENGTH_SHORT).show()
        viewModel.isNotForcedFinish()
        oauthFinish(Intent(), NidOAuthErrorCode.WEB_VIEW_IS_DEPRECATED, "webView is deprecated")
//        val intent = NidOAuthIntent.Builder(this)
//            .setType(NidOAuthIntent.Type.WEB_VIEW)
//            .build()
//        startActivityForResult(intent, REQUEST_CODE_LOGIN)
    }

    override fun onResume() {
        super.onResume()
        NidLog.d(TAG, "called onResume()")
    }

    override fun onPause() {
        super.onPause()
        NidLog.d(TAG, "called onPause()")
    }

    private fun finishWithErrorResult(intent: Intent) {
        val errorCode = intent.getStringExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_CODE)
        val errorDesc = intent.getStringExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_DESCRIPTION) ?: ""
        oauthFinish(intent, fromString(errorCode), errorDesc)
    }

    private fun finishWithErrorResult(errCode: NidOAuthErrorCode) {
        val intent = Intent().apply {
            // TODO code 넣을때 state체크 처리해야 함.
            putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_CODE, errCode.code)
            putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_DESCRIPTION, errCode.description)
        }

        oauthFinish(intent, errCode, errCode.description)
    }

    private fun oauthFinish(intent: Intent, errorCode: NidOAuthErrorCode, errorDescription: String) {
        NidOAuthPreferencesManager.lastErrorCode = errorCode
        NidOAuthPreferencesManager.lastErrorDesc = errorDescription

        viewModel.isNotForcedFinish()
        NaverIdLoginSDK.oauthLoginCallback?.onError(-1, errorDescription)
        setResult(RESULT_CANCELED, intent)
        finish()
    }
}
