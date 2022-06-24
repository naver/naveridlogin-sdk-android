package com.navercorp.nid.oauth.plugin.legacy;

import android.text.TextUtils;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class OAuthWebviewUrlUtil {

    private static final String TAG = "OAuthWebvewUrlUtil" ;

    public static final String FINAL_URL 			= "http://nid.naver.com/com.nhn.login_global/inweb/finish";
    public static final String FINAL_URL_HTTPS		= "https://nid.naver.com/com.nhn.login_global/inweb/finish";

    private static Map<String, String> getQueryMapFromUrl(String url) {

        if (url.contains("?")) {
            url = url.split("\\?")[1];
        }

        return getQueryMap(url);
    }

    private static Map<String, String> getQueryMap(String query)
    {
        if (query == null){
            return null;
        }
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] keyname = param.split("=");

            if (keyname.length == 2) {
                String name = keyname[0];
                String value = keyname[1];
                map.put(name, value);
            } else if (keyname.length == 1){
                String name = keyname[0];
                String value = "";
                map.put(name, value);
            }
        }
        return map;
    }


    public static String getDecodedString(String oriStr) {

        if (TextUtils.isEmpty(oriStr)) {
            return oriStr;
        }

        String decodedStr = "";
        try {
            decodedStr = URLDecoder.decode(oriStr, "UTF-8");
        } catch (Exception e) {
            // do nothing
        }
        if (!TextUtils.isEmpty(decodedStr)
                && !decodedStr.equalsIgnoreCase(oriStr)) {
            return decodedStr;
        }
        return oriStr;
    }


    public static boolean isFinalUrl(boolean isShouldOverrideUrl, String preUrl, String url) {
        if (url == null) {
            return false;
        }
        if (url.equalsIgnoreCase(FINAL_URL)
                || url.equalsIgnoreCase(FINAL_URL_HTTPS)
                || url.equalsIgnoreCase("http://m.naver.com/")
                || url.equalsIgnoreCase("http://m.naver.com") ) {
            return true;
        }

        // shoudOverrideUrl 에선 post method 로 넘어오는 건 처리 안함 (로그인의 경우)
        if (true == isShouldOverrideUrl) {
            if (url.startsWith("https://nid.naver.com/nidlogin.login?svctype=262144") ) {
                return true;
            }
        }

        // 휴면 계정 (한글) -- https://nid.naver.com/mobile/user/help/sleepId.nhn?m=actionCheckSleepId -- 휴면해제 후 정상적인 로그인 페이지로 이동함
        // 보호 조치 (영어) -- https://nid.naver.com/mobile/user/global/idSafetyRelease.nhn?m=viewInitPasswd&token_help=
        // 보호 조치 (한글) -- https://nid.naver.com/mobile/user/help/idSafetyRelease.nhn?m=viewInputPasswd&token_help=
        if (false == isShouldOverrideUrl
                && ((preUrl.startsWith("https://nid.naver.com/mobile/user/help/sleepId.nhn?m=viewSleepId&token_help=")
                && url.startsWith("https://nid.naver.com/nidlogin.login?svctype=262144"))
                || (preUrl.startsWith("https://nid.naver.com/mobile/user/global/idSafetyRelease.nhn?")
                && url.startsWith("https://nid.naver.com/nidlogin.login?svctype=262144"))
                || (preUrl.startsWith("https://nid.naver.com/mobile/user/help/idSafetyRelease.nhn?")
                && url.startsWith("https://nid.naver.com/nidlogin.login?svctype=262144"))) ) {
            return true;
        }

        return false;
    }


}
