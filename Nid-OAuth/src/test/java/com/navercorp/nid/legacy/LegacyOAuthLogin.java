package com.navercorp.nid.legacy;

import android.content.Context;
import android.text.TextUtils;

import com.navercorp.nid.oauth.NidOAuthLoginState;
import com.navercorp.nid.oauth.legacy.OAuthErrorCode;
import com.navercorp.nid.oauth.legacy.OAuthLoginPreferenceManager;

public class LegacyOAuthLogin {
    private static final String TAG = "OAuthLogin";

    private static LegacyOAuthLogin instance;

    /*
     * 싱글턴 패턴을 이용하여 OAuthLogin 객체를 생성하여 리턴하거나 기존에 생성했던 걸 리턴한다.
     */
    public static LegacyOAuthLogin getInstance() {
        if (instance == null) {
            instance = new LegacyOAuthLogin();
        }
        return instance;
    }

    private LegacyOAuthLogin() {
        // do nothing
    }


    /// OAuth 인증시 필요한 값들을 preference에 저장함
    /**
     * OAuth 인증시 필요한 값들을 preference에 저장함. 2015년 8월 이후에 등록하여 package name 을 넣은 경우 사용.
     * @param context shared Preference를 얻어올 때 사용할 context
     * @param clientId OAuth client id 값
     * @param clientSecret OAuth client secret 값
     * @param clientName OAuth client name 값 (네이버앱을 통한 로그인시 보여짐)
     */
    public void init(Context context, String clientId, String clientSecret, String clientName) {
        String packageName = context.getPackageName();

        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);

        prefMng.setClientId(clientId);
        prefMng.setClientSecret(clientSecret);

        prefMng.setClientName(clientName);
        prefMng.setCallbackUrl(packageName);

        prefMng.setLastErrorCode(OAuthErrorCode.NONE);
        prefMng.setLastErrorDesc("");

//        CookieSyncManager.createInstance(context);
    }


    /// OAuth 인증시 필요한 값들을 preference에 저장함
    /**
     * OAuth 인증시 필요한 값들을 preference에 저장함. 2015년 8월 이전 등록했고 그 뒤로 앱 정보 변경을 하지 않은 경우 사용.
     * @param context shared Preference를 얻어올 때 사용할 context
     * @param clientId OAuth client id 값
     * @param clientSecret OAuth client secret 값
     * @param clientName OAuth client name 값 (네이버앱을 통한 로그인시 보여짐)
     * @param callbackIntent 2015년 8월 이전에 등록한 사용자는 네아로 웹페이지에서 앱 등록시 넣어준 intent(callback url)를 넣어준다. 그 값과 다르면 인증을 실패한다.
     */
    @Deprecated
    public void init(Context context, String clientId, String clientSecret, String clientName, String callbackIntent) {
        init(context, clientId, clientSecret, clientName);
    }

    private boolean valid(Context context) {
        if (null == context) {
//            Logger.i(TAG, "context is null");
            return false;
        }
        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);
        if (TextUtils.isEmpty(prefMng.getClientId())) {
//            Logger.i(TAG, "CliendId is null");
            return false;
        }
        if (TextUtils.isEmpty(prefMng.getClientSecret())) {
//            Logger.i(TAG, "CliendSecret is null");
            return false;
        }
        return true;
    }


    /// 네아로 SDK의 버전을 리턴한다
    public static String getVersion() {
        return OAuthLoginDefine.VERSION;
    }

    /// 네아로 인스턴스의 로그인 상태를 리턴해줌
    public NidOAuthLoginState getState(Context context) {
        if (!valid(context)) {
            return NidOAuthLoginState.NEED_INIT;
        }
        OAuthLoginPreferenceManager prefMng = new OAuthLoginPreferenceManager(context);
        String at = prefMng.getAccessToken();
        String rt = prefMng.getRefreshToken();

        if (TextUtils.isEmpty(at)) {
            if (TextUtils.isEmpty(rt)) {
                return NidOAuthLoginState.NEED_LOGIN;
            } else {
                return NidOAuthLoginState.NEED_REFRESH_TOKEN;
            }
        }
        return NidOAuthLoginState.OK;
    }


    // 전체 로그인모드 비활성화 (기본값 사용)
    public void initializeLoginFlag() {
        disableNaverAppLoginOnly();
        disableCustomTabLoginOnly();
        disableWebViewLoginOnly();
    }

    // 네이버앱 로그인 활성화
    public void enableNaverAppLoginOnly() {
        OAuthLoginDefine.LOGIN_BY_NAVERAPP_ONLY = true;
        disableCustomTabLoginOnly();
        disableWebViewLoginOnly();
    }

    // 커스텀탭 로그인 활성화
    public void enableCustomTabLoginOnly() {
        disableNaverAppLoginOnly();
        OAuthLoginDefine.LOGIN_BY_CUSTOM_TAB_ONLY = true;
        disableWebViewLoginOnly();
    }

    // 웹뷰 로그인 활성화
    public void enableWebViewLoginOnly() {
        disableNaverAppLoginOnly();
        disableCustomTabLoginOnly();
        OAuthLoginDefine.LOGIN_BY_WEBVIEW_ONLY = true;
    }

    // 네이버앱 로그인 비활성화
    public void disableNaverAppLoginOnly() {
        OAuthLoginDefine.LOGIN_BY_NAVERAPP_ONLY = false;
    }

    // 커스텀탭 로그인 비활성화
    public void disableCustomTabLoginOnly() {
        OAuthLoginDefine.LOGIN_BY_CUSTOM_TAB_ONLY = false;
    }

    // 웹뷰 로그인 비활성화
    public void disableWebViewLoginOnly() {
        OAuthLoginDefine.LOGIN_BY_WEBVIEW_ONLY = false;
    }

    public void setCustomTabReAuth(boolean value) {
        OAuthLoginDefine.CUSTOM_TAB_REQUIRED_RE_AUTH = value;
    }

    /// 로그인 결과로 얻어온 Access Token 을 리턴함
    public String getAccessToken(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        String at = pref.getAccessToken();

        if (TextUtils.isEmpty(at)) {
            return null;
        }
        return at;
    }

    /// 로그인 결과로 얻어온 Refresh Token 을 리턴함
    public String getRefreshToken(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        String rt = pref.getRefreshToken();

        if (TextUtils.isEmpty(rt)) {
            return null;
        }
        return rt;
    }

    /// Access Token 의 만료 시간을 리턴함
    public long getExpiresAt(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        long expiresAt = pref.getExpiresAt();

        return expiresAt;
    }

    /// 로그인 결과로 얻어온 Token의 Type을 리턴함
    public String getTokenType(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        String tokenType = pref.getTokenType();

        if (TextUtils.isEmpty(tokenType)) {
            return null;
        }
        return tokenType;
    }

    /// 지난 로그인 시도가 실패한 경우 Error code 를 리턴함
    public OAuthErrorCode getLastErrorCode(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        return pref.getLastErrorCode();
    }

    /// 지난 로그인 시도가 실패한 경우 Error description 을 리턴함
    public String getLastErrorDesc(Context context) {
        OAuthLoginPreferenceManager pref = new OAuthLoginPreferenceManager(context);
        return pref.getLastErrorDesc();
    }
}
