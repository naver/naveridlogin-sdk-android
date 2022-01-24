package com.navercorp.nid.oauth

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.NaverIdLoginSDK.behavior
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.NidOAuthErrorCode.INSTANCE.fromString
import com.navercorp.nid.util.AndroidVer
import com.navercorp.nid.util.NidApplicationUtil


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
        private const val REQUEST_CODE_LOGIN = 100
        const val CUSTOM_TABS_LOGIN = -1
    }

    private lateinit var clientName: String

    private var isForceDestroyed = true
    private var isRotated = false

    private var isLoginActivityStarted = false

    private fun initData(): Boolean {

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

        clientName = NidOAuthPreferencesManager.clientName!!

        val screenOrientation = intent.getIntExtra("orientation", 1)
        requestedOrientation = screenOrientation

		return true

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NidLog.d(TAG, "called onCreate()")

        if (!initData()) {
            return
        }

        if (null != savedInstanceState) {
            isLoginActivityStarted = savedInstanceState.getBoolean("IsLoginActivityStarted")
        }

        NidLog.d(TAG, "onCreate() | isLoginActivityStarted : $isLoginActivityStarted")

        isRotated = false

        if (!isLoginActivityStarted) {
            isLoginActivityStarted = true
            NidLog.d(TAG, "onCreate() first init.")
            startLoginActivity()
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
        isRotated = true
    }

    override fun onDestroy() {
        super.onDestroy()

        NidLog.d(TAG, "called onDestroy()")

        if (isForceDestroyed && !isRotated) {
            NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.ACTIVITY_IS_SINGLE_TASK
            NidOAuthPreferencesManager.lastErrorDesc = "OAuthLoginActivity is destroyed."

            NaverIdLoginSDK.oauthLoginCallback.onError(-1, "OAuthLoginActivity is destroyed.")
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        NidLog.d(TAG, "called onRestoreInstanceState()")

        isLoginActivityStarted = savedInstanceState.getBoolean("IsLoginActivityStarted")
        isForceDestroyed = savedInstanceState.getBoolean("isForceDestroyed")
        isRotated = savedInstanceState.getBoolean("isRotated")

        val state = savedInstanceState.getString("OAuthLoginData_state")
        if (state.isNullOrEmpty()) {
            NidOAuthPreferencesManager.initState = state
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        NidLog.d(TAG, "called onSaveInstanceState()")

        outState.apply {
            putBoolean("IsLoginActivityStarted", isLoginActivityStarted)
            putBoolean("isForceDestroyed", isForceDestroyed)
            putBoolean("isRotated", isRotated)
            putString("OAuthLoginData_state", NidOAuthPreferencesManager.initState)
        }
    }

    /**
     * 각 상황별로 네이버 로그인을 진행할 액티비티를 실행
     * @param mOAuthLoginData 네아로 메타 정보
     */
    private fun startLoginActivity() {
        NidLog.d(TAG, "startLoginActivity()")

        // https://oss.navercorp.com/id-mobilesdk/naverid-3rdparty-sdk-android/issues/39
        // 에 의해 비공개로 로직을 남겨둔다

        // 명시적으로 Behavior를 지정한 경우 해당 인증 방식만 제공한다.

        when (behavior) {
            NidOAuthBehavior.NAVERAPP -> {
                if (!tryOAuthByNaverapp()) {
                    oauthFinish(Intent(), NidOAuthErrorCode.ERROR_NO_CATAGORIZED, "기기에 네이버앱이 없습니다.")
                }
                return
            }
            NidOAuthBehavior.CUSTOMTABS -> {
                if (!tryOAuthByCustomTab()) {
                    if (NidApplicationUtil.isNotCustomTabsAvailable(this)) {
                        // 커스텀탭을 실행할 수 있는 앱이 없는 경우 웹뷰가 열립니다.
                        startLoginWebviewActivity()
                        return
                    }
                    oauthFinish(Intent(), NidOAuthErrorCode.ERROR_NO_CATAGORIZED, "커스텀탭을 실행할 수 없습니다.")
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
            // 3. 인앱브라우저
            NidOAuthBehavior.DEFAULT -> {
                if (tryOAuthByNaverapp()) return
                if (tryOAuthByCustomTab()) return
            }
        }
        startLoginWebviewActivity()
    }

    /**
     * 네이버 앱으로 로그인 시도
     * @param loginData 네아로 메타 정보
     * @return 실행 여부
     */
    private fun tryOAuthByNaverapp(): Boolean {
        val intent = NidOAuthIntent.Builder(this)
            .setType(NidOAuthIntent.Type.NAVER_APP)
            .build()
        return if (intent == null) {
            false
        } else {
            startActivityForResult(intent, REQUEST_CODE_LOGIN)
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
            .build()
        return if (intent == null) {
            false
        } else {
            startActivityForResult(intent, CUSTOM_TABS_LOGIN)
            true
        }
    }

    /**
     * login webview 를 실행함
     * 네이버 앱도 없고 커스텀 탭도 불가능한 상황일 때 혹은 필요에 의해 웹뷰만 호출
     */
    private fun startLoginWebviewActivity() {
        val intent = NidOAuthIntent.Builder(this)
            .setType(NidOAuthIntent.Type.WEB_VIEW)
            .build()
        startActivityForResult(intent, REQUEST_CODE_LOGIN)
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
        setResult(RESULT_CANCELED, intent)
        finish()

        NaverIdLoginSDK.oauthLoginCallback.onError(-1, errorDescription)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        NidLog.d(TAG, "called onActivityResult()")
        isForceDestroyed = false

        if (requestCode == CUSTOM_TABS_LOGIN && resultCode == RESULT_CANCELED) {
            NidLog.d(TAG, "activity call by customtab")
            return
        }

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
            NidOAuthLogin().accessToken(this)
        }
    }
}
