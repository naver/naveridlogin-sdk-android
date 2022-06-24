package com.navercorp.nid.oauth.legacy;

import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.NidOAuthConstants;

import java.util.HashMap;
import java.util.Map;

public class OAuthQueryGenerator extends CommonLoginQuery {
    private static final String TAG = "OAuthQueryGenerator";

    private final static String OAUTH_REQUEST_AUTH_URL = "https://nid.naver.com/oauth2.0/authorize?";
    private final static String OAUTH_REQUEST_ACCESS_TOKEN_URL = "https://nid.naver.com/oauth2.0/token?";


    /**
     * webview에서 인증 시작할때 쓸 url 을 만듬
     *
     * @param clientId    client id
     * @param state       OAuth2.0 에서 쓰이는 state string (random seed)
     * @param callbackUrl 완료 후 돌아갈 url
     * @param locale      언어값
     * @return generated url
     */
    public String generateRequestWebViewAuthorizationUrl(String clientId, String state, String callbackUrl, String locale) {
        return generateRequestWebViewAuthorizationUrl(clientId, state, callbackUrl, locale, null, NidOAuthConstants.SDK_VERSION);
    }

    /**
     * webview에서 인증 시작할때 쓸 url 을 만듬
     *
     * @param clientId    client id
     * @param state       OAuth2.0 에서 쓰이는 state string (random seed)
     * @param callbackUrl 완료 후 돌아갈 url
     * @param locale      언어값
     * @param network     네트워크 상태(wifi, 3g ...)
     * @return generated url
     */
    public String generateRequestWebViewAuthorizationUrl(String clientId, String state, String callbackUrl, String locale, String network, String version) {
        return String.format("%s%s", OAUTH_REQUEST_AUTH_URL,
                getQueryParameter(newAuthorizationParamMap(clientId, state, callbackUrl, locale, network, "true", version)));
    }

    /**
     * 커스텀 탭에서 인증 시작할때 쓸 url 을 만듬
     *
     * @param clientId    client id
     * @param state       OAuth2.0 에서 쓰이는 state string (random seed)
     * @param appPackageName 앱의 패키지 명
     * @param locale      언어값
     * @param network     네트워크 상태(wifi, 3g ...)
     * @return generated url
     */
    public String generateRequestCustomTabAuthorizationUrl(String clientId, String state, String appPackageName, String locale, String network, String version) {
        return String.format("%s%s", OAUTH_REQUEST_AUTH_URL,
                getQueryParameter(newAuthorizationParamMap(clientId, state, appPackageName, locale, network, "custom_tab", version)));
    }

    /**
     * 파라메터 맵을 생성한다.
     * @param clientId    client id
     * @param state       OAuth2.0 에서 쓰이는 state string (random seed)
     * @param callbackUrl 완료 후 돌아갈 url
     * @param locale      언어값
     * @param network     네트워크 상태(wifi, 3g ...)
     * @param inAppType   요청의 주체 (웹뷰 "true", 커스텀 탭 "custom_tab")
     * @param version     현재 sdk 버전
     * @return 해당 정보들이 포함된 해시맵 객체
     */
    private Map<String, String> newAuthorizationParamMap(String clientId, String state, String callbackUrl, String locale, String network, String inAppType, String version) {
        Map<String, String> paramArray = new HashMap<>();
        paramArray.put("client_id", clientId);
        paramArray.put("inapp_view", inAppType);
        paramArray.put("response_type", "code");
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        if (null != network) {
            paramArray.put("network", network);
        }
        paramArray.put("locale", locale);
        paramArray.put("redirect_uri", callbackUrl);        // getQueryParameter 에서 encoding 됨. 2014.11.27일 강병국님 메일로 수정됨
        paramArray.put("state", state);


        if (NaverIdLoginSDK.INSTANCE.isRequiredCustomTabsReAuth()) {
            paramArray.put("auth_type", "reauthenticate");
        }

        return paramArray;
    }

    public String generateRequestAccessTokenUrl(String clientId, String clientSecret, String state, String code, String locale, String version) {
        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put("client_id", clientId);
        paramArray.put("client_secret", clientSecret);
        paramArray.put("grant_type", "authorization_code");
        paramArray.put("state", state);
        paramArray.put("code", code);
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        paramArray.put("locale", locale);


        return String.format("%s%s", OAUTH_REQUEST_ACCESS_TOKEN_URL, getQueryParameter(paramArray));
    }

    public String generateRequestRefreshAccessTokenUrl(String clientId, String clientSecret, String refreshToken, String locale, String version) {

        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put("client_id", clientId);
        paramArray.put("client_secret", clientSecret);
        paramArray.put("grant_type", "refresh_token");
        paramArray.put("refresh_token", refreshToken);
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        paramArray.put("locale", locale);

        return String.format("%s%s", OAUTH_REQUEST_ACCESS_TOKEN_URL, getQueryParameter(paramArray));
    }

    public String generateRequestDeleteAccessTokenUrl(String clientId, String clientSecret, String accessToken, String locale, String version) {

        Map<String, String> paramArray = new HashMap<String, String>();

        paramArray.put("client_id", clientId);
        paramArray.put("client_secret", clientSecret);
        paramArray.put("grant_type", "delete");
        paramArray.put("access_token", accessToken);
        paramArray.put("service_provider", "NAVER");
        paramArray.put("oauth_os", "android");
        paramArray.put("version", "android-"+ version);
        paramArray.put("locale", locale);

        return String.format("%s%s", OAUTH_REQUEST_ACCESS_TOKEN_URL, getQueryParameter(paramArray));
    }

}
