package com.navercorp.nid

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.*

/**
 *
 * Created on 2021.10.18
 * Updated on 2021.10.18
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네아로SDK를 초기화하고, 사용하기 위한 매니저 클래스
 */
object NaverIdLoginSDK {

    /**
     * 네이버앱에 대한 market link 팝업의 노출 여부를 결정한다.
     */
    var isShowMarketLink: Boolean = true

    /**
     * InApp Browser에서 하단 탭의 노출 여부를 결정한다.
     */
    var isShowBottomTab: Boolean = true

    /**
     * Custom Tabs를 활용한 OAuth에서 재인증 수행 여부를 결정한다.
     */
    var isRequiredCustomTabsReAuth: Boolean = false

    /**
     * 로그인 후 실행될 callback
     */
    lateinit var oauthLoginCallback: OAuthLoginCallback

    /**
     * OAuth 인증시 필요한 값들을 preference에 저장함. 2015년 8월 이후에 등록하여 package name 을 넣은 경우 사용.
     * @param context shared Preference를 얻어올 때 사용할 context
     * @param clientId OAuth client id 값
     * @param clientSecret OAuth client secret 값
     * @param clientName OAuth client name 값 (네이버앱을 통한 로그인시 보여짐)
     */
    fun initialize(context: Context, clientId: String, clientSecret: String, clientName: String) {

        // 1. SharedPreferences 초기화
        EncryptedPreferences.setContext(context.applicationContext)

        // 2. 데이터 초기화
        NidOAuthPreferencesManager.apply {
            this.clientId = clientId
            this.clientSecret = clientSecret
            this.clientName = clientName
            this.callbackUrl = context.packageName
            this.lastErrorCode = NidOAuthErrorCode.NONE
            this.lastErrorDesc = ""
        }

        // 3. Log Prefix 초기화
        NidLog.setPrefix("NaverIdLogin|${context.packageName}|")
    }

    /**
     * 개발자 로그를 보여줄 것인지? (마켓 등에 릴리즈시엔 false 로 하거나 호출안하면 기본값은 false)
     * @param show if true, show detail-log.
     */
    fun showDevelopersLog(isShow: Boolean) {
        NidLog.showLog(isShow)
    }

    /**
     * 네아로SDK의 버전을 반환한다.
     */
    fun getVersion(): String = NidOAuthConstants.SDK_VERSION

    /**
     * OAuth 2.0 로그인을 수행한다.
     *
     * RefreshToken이 존재하는 경우, 이미 연동이 된 것이므로 AccessToken을 갱신해준다.
     *
     * @param context authenticate 메서드를 호출한 Activity의 Context
     * @param callback 결과값을 받을 콜백
     */
    fun authenticate(context: Context, callback: OAuthLoginCallback) {
        if (getState() == NidOAuthLoginState.NEED_INIT) {
            Toast.makeText(context.applicationContext, "SDK 초기화가 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        oauthLoginCallback = callback

        val refreshToken = getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            val intent = Intent(context, NidOAuthBridgeActivity::class.java)
            val orientation = context.resources.configuration.orientation
            intent.putExtra("orientation", orientation)
            context.startActivity(intent)
        } else {
            NidOAuthLogin().refreshToken(context, callback)
        }
    }

    /**
     * 지난 로그인 시도가 실패한 경우 Error code를 반환
     */
    fun getLastErrorCode(): NidOAuthErrorCode = NidOAuthPreferencesManager.lastErrorCode

    /**
     * 지난 로그인 시도가 실패한 경우 Error description 을 리턴함
     */
    fun getLastErrorDescription(): String? = NidOAuthPreferencesManager.lastErrorDesc

    /**
     * 특정 로그인 모드를 저장한다.
     */
    var behavior: NidOAuthBehavior = NidOAuthBehavior.DEFAULT

    /**
     * OAuth Login 이후 획득한 AccessToken을 반환한다.
     */
    fun getAccessToken(): String? = NidOAuthPreferencesManager.accessToken

    /**
     * OAuth Login 이후 획득한 RefreshToken을 반환한다.
     */
    fun getRefreshToken(): String? = NidOAuthPreferencesManager.refreshToken

    /**
     * AccessToken의 만료 시간을 반환한다.
     */
    fun getExpiresAt(): Long = NidOAuthPreferencesManager.expiresAt

    /**
     * OAuth Login 이후 얻어온 token의 타입을 반환한다.
     */
    fun getTokenType(): String? = NidOAuthPreferencesManager.tokenType

    fun getState(): NidOAuthLoginState {
        val clientId = NidOAuthPreferencesManager.clientId
        if (clientId.isNullOrEmpty()) {
            return NidOAuthLoginState.NEED_INIT
        }
        val clientSecret = NidOAuthPreferencesManager.clientSecret
        if (clientSecret.isNullOrEmpty()) {
            return NidOAuthLoginState.NEED_INIT
        }

        val accessToken = getAccessToken()
        val refreshToken = getRefreshToken()

        if (accessToken.isNullOrEmpty()) {
            return if (refreshToken.isNullOrEmpty()) {
                NidOAuthLoginState.NEED_LOGIN
            } else {
                NidOAuthLoginState.NEED_REFRESH_TOKEN
            }
        }
        return NidOAuthLoginState.OK


    }

}