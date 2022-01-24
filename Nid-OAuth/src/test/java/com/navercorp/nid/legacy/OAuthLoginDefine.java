package com.navercorp.nid.legacy;

import android.os.Build;

import com.navercorp.nid.oauth.NidOAuthConstants;

public class OAuthLoginDefine {

    // 네아로 Lib 버젼
    public static final String 		VERSION = NidOAuthConstants.SDK_VERSION;
    // 로그 TAG
    public static final String 		LOG_TAG ="NaverLoginOAuth|";
    // 네이버앱 package name
    public static final String 		NAVER_PACKAGE_NAME = "com.nhn.android.search";
    // Naver App 혹은 앱스토어앱 에서 받을 OAuth 2.0 로그인 Action
    public static final String 		ACTION_OAUTH_LOGIN 			= "com.nhn.android.search.action.OAUTH2_LOGIN";
    public static final String 		ACTION_OAUTH_LOGIN_2NDAPP 	= "com.naver.android.action.OAUTH2_LOGIN";


    /**
     * 사용자가 수정 가능한 값들
     */
    // 네이버앱이 없거나 업그레이드가 필요한 경우 네이버앱에 대한 market link 팝업을 띄울것인지 여부
    public static boolean		MARKET_LINK_WORKING = true;

    // 닫기 버튼 등이 들어가 있는 하단 탭의 노출 유무
    public static boolean		BOTTOM_TAB_WORKING = true;


    // 로그인을 webview 혹은 네이버앱 통해서 할 수 있는데 true 로 설정하면 webview 로만 로그인하게 됨.
    public static boolean		LOGIN_BY_NAVERAPP_ONLY = false;

    // 로그인을 webview 혹은 네이버앱 통해서 할 수 있는데 true 로 설정하면 webview 로만 로그인하게 됨.
    public static boolean		LOGIN_BY_CUSTOM_TAB_ONLY = false;

    // 로그인을 webview 혹은 네이버앱 통해서 할 수 있는데 true 로 설정하면 webview 로만 로그인하게 됨.
    public static boolean		LOGIN_BY_WEBVIEW_ONLY = false;
    // network timeout
    public static int 			TIMEOUT = 10000;

    public static boolean CUSTOM_TAB_REQUIRED_RE_AUTH = false;

    /**
     * 커스텀탭 사용가능 버전
     */
    public static final int		CUSTOMTAB_AVAILABLE_VER = Build.VERSION_CODES.JELLY_BEAN;

}
