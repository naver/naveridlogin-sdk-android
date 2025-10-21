package com.navercorp.nid

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.oauth.domain.enum.LoginBehavior
import com.navercorp.nid.oauth.domain.enum.NidOAuthBehavior
import com.navercorp.nid.oauth.domain.enum.NidOAuthLoginState
import com.navercorp.nid.oauth.util.NidOAuthCallback

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
@Deprecated(
    message = "This class will be removed from v6.1.0. Use NidOAuth instead.",
    replaceWith = ReplaceWith("NidOAuth"),
)
object NaverIdLoginSDK {

    /**
     * 네이버앱에 대한 market link 팝업의 노출 여부를 결정한다.
     */
    @Deprecated(
        message = "This property will be removed from v6.1.0. Use NidOAuth.isShowMarketLink instead.",
        replaceWith = ReplaceWith("NidOAuth.isShowMarketLink"),
    )
    var isShowMarketLink: Boolean
        get() = NidOAuth.isShowMarketLink
        set(value) { NidOAuth.isShowMarketLink = value }

    /**
     * InApp Browser에서 하단 탭의 노출 여부를 결정한다.
     */
    @Deprecated(
        message = "This property will be removed from v6.1.0. Use NidOAuth.isShowBottomTab instead.",
        replaceWith = ReplaceWith("NidOAuth.isShowBottomTab"),
    )
    var isShowBottomTab: Boolean
        get() = NidOAuth.isShowBottomTab
        set(value) { NidOAuth.isShowBottomTab = value }

    /**
     * Custom Tabs를 활용한 OAuth에서 재인증 수행 여부를 결정한다.
     */
    @Deprecated(
        message = "This property will be removed from v6.1.0. Use NidOAuth.isRequiredCustomTabsReAuth instead.",
        replaceWith = ReplaceWith("NidOAuth.isRequiredCustomTabsReAuth"),
    )
    var isRequiredCustomTabsReAuth: Boolean
        get() = NidOAuth.isRequiredCustomTabsReAuth
        set(value) { NidOAuth.isRequiredCustomTabsReAuth = value }

    /**
     * 네이버앱 호출 시 추가할 FLAG
     */
    @Deprecated(
        message = "This property will be removed from v6.1.0. Use NidOAuth.naverappIntentFlag instead.",
        replaceWith = ReplaceWith("NidOAuth.naverappIntentFlag"),
    )
    var naverappIntentFlag: Int
        get() = NidOAuth.naverappIntentFlag
        set(value) { NidOAuth.naverappIntentFlag = value }

    /**
     * 로그인 후 실행될 callback
     */
    @Deprecated(
        message = "This property will be removed from v6.1.0. Use NidOAuth.oauthLoginCallback instead.",
        replaceWith = ReplaceWith("NidOAuth.oauthLoginCallback"),
    )
    var oauthLoginCallback: NidOAuthCallback?
        get() = NidOAuth.oauthLoginCallback
        set(value) { NidOAuth.oauthLoginCallback = value }

    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.isInitialized instead.",
        replaceWith = ReplaceWith("NidOAuth.isInitialized"),
    )
    fun isInitialized(): Boolean = NidOAuth.isInitialized()

    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getApplicationContext instead.",
        replaceWith = ReplaceWith("NidOAuth.getApplicationContext"),
    )
    fun getApplicationContext(): Context = NidOAuth.getApplicationContext()

    /**
     * OAuth 인증시 필요한 값들을 preference에 저장함. 2015년 8월 이후에 등록하여 package name 을 넣은 경우 사용.
     * @param context shared Preference를 얻어올 때 사용할 context
     * @param clientId OAuth client id 값
     * @param clientSecret OAuth client secret 값
     * @param clientName OAuth client name 값 (네이버앱을 통한 로그인시 보여짐)
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.initialize(context, clientId, clientSecret) instead.",
        replaceWith = ReplaceWith("NidOAuth.initialize(context, clientId, clientSecret)"),
    )
    fun initialize(context: Context, clientId: String, clientSecret: String, clientName: String) = NidOAuth.initialize(context, clientId, clientSecret, clientName)

    /**
     * 개발자 로그를 보여줄 것인지? (마켓 등에 릴리즈시엔 false 로 하거나 호출안하면 기본값은 false)
     * @param show if true, show detail-log.
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.showDevelopersLog(isShow) instead.",
        replaceWith = ReplaceWith("NidOAuth.setLOgEnabled(enabled)"),
    )
    fun showDevelopersLog(isShow: Boolean) = NidOAuth.setLogEnabled(isShow)

    /**
     * 네아로SDK의 버전을 반환한다.
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getVersion instead.",
        replaceWith = ReplaceWith("NidOAuth.getVersion"),
    )
    fun getVersion(): String = NidOAuth.getVersion()

    /**
     * OAuth 2.0 로그인을 수행한다.
     *
     * RefreshToken이 존재하는 경우, 이미 연동이 된 것이므로 AccessToken을 갱신해준다.
     *
     * @param context authenticate 메서드를 호출한 Activity의 Context
     * @param launcher OAuth 인증을 실행할 ActivityResultLauncher
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.requestLogin(context, launcher) instead.",
        replaceWith = ReplaceWith("NidOAuth.requestLogin(context, launcher)"),
    )
    fun authenticate(context: Context, launcher: ActivityResultLauncher<Intent>) = NidOAuth.requestLogin(context, launcher)

    /**
     * OAuth 2.0 로그인을 수행한다.
     *
     * RefreshToken이 존재하는 경우, 이미 연동이 된 것이므로 AccessToken을 갱신해준다.
     *
     * @param context authenticate 메서드를 호출한 Activity의 Context
     * @param callback 결과값을 받을 콜백
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.requestLogin(context, callback) instead.",
        replaceWith = ReplaceWith("NidOAuth.requestLogin(context, callback)"),
    )
    fun authenticate(context: Context, callback: NidOAuthCallback) = NidOAuth.requestLogin(context, callback)

    /**
     * 재동의를 요청한다.
     *
     * @param context authenticate 메서드를 호출한 Activity의 Context
     * @param launcher OAuth 인증을 실행할 ActivityResultLauncher
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.repromptPermissions(context, launcher) instead.",
        replaceWith = ReplaceWith("NidOAuth.repromptPermissions(context, launcher)"),
    )
    fun reagreeAuthenticate(context: Context, launcher: ActivityResultLauncher<Intent>) = NidOAuth.repromptPermissions(context, launcher)

    /**
     * 재동의를 요청한다.
     *
     * @param context authenticate 메서드를 호출한 Activity의 Context
     * @param callback 결과값을 받을 콜백
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.repromptPermissions(context, callback) instead.",
        replaceWith = ReplaceWith("NidOAuth.repromptPermissions(context, callback)"),
    )
    fun reagreeAuthenticate(context: Context, callback: NidOAuthCallback) = NidOAuth.repromptPermissions(context, callback)

    /**
     * 클라이언트에 저장되어 있는 Access token 및 Refresh token을 삭제함
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.logout(callback) instead.",
        replaceWith = ReplaceWith("NidOAuth.logout(callback)"),
    )
    fun logout(callback: NidOAuthCallback) = NidOAuth.logout(callback)

    /**
     * 지난 로그인 시도가 실패한 경우 Error code를 반환
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getLastErrorCode instead.",
        replaceWith = ReplaceWith("NidOAuth.getLastErrorCode"),
    )
    fun getLastErrorCode(): NidOAuthErrorCode = NidOAuth.getLastErrorCode()

    /**
     * 지난 로그인 시도가 실패한 경우 Error description 을 리턴함
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getLastErrorDescription instead.",
        replaceWith = ReplaceWith("NidOAuth.getLastErrorDescription"),
    )
    fun getLastErrorDescription(): String? = NidOAuth.getLastErrorDescription()

    /**
     * 특정 로그인 모드를 저장한다.
     */
    @Deprecated(
        message = "This property will be removed from v6.1.0. Use NidOAuth.behavior instead.",
        replaceWith = ReplaceWith("NidOAuth.behavior"),
    )
    var behavior: NidOAuthBehavior
        get() = LoginBehavior.toNidOAuthBehavior(NidOAuth.behavior)
        set(value) { NidOAuth.behavior = NidOAuthBehavior.toLoginBehavior(value) }

    /**
     * OAuth Login 이후 획득한 AccessToken을 반환한다.
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getAccessToken instead.",
        replaceWith = ReplaceWith("NidOAuth.getAccessToken"),
    )
    fun getAccessToken(): String? = NidOAuth.getAccessToken()

    /**
     * OAuth Login 이후 획득한 RefreshToken을 반환한다.
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getRefreshToken instead.",
        replaceWith = ReplaceWith("NidOAuth.getRefreshToken"),
    )
    fun getRefreshToken(): String? = NidOAuth.getRefreshToken()

    /**
     * AccessToken의 만료 시간을 반환한다.
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getExpiresAt instead.",
        replaceWith = ReplaceWith("NidOAuth.getExpiresAt"),
    )
    fun getExpiresAt(): Long = NidOAuth.getExpiresAt()

    /**
     * OAuth Login 이후 얻어온 token의 타입을 반환한다.
     */
    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getTokenType instead.",
        replaceWith = ReplaceWith("NidOAuth.getTokenType"),
    )
    fun getTokenType(): String? = NidOAuth.getTokenType()

    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidOAuth.getState instead.",
        replaceWith = ReplaceWith("NidOAuth.getState"),
    )
    fun getState(): NidOAuthLoginState = NidOAuth.getState()

}