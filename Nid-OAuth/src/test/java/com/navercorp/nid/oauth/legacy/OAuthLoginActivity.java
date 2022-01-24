package com.navercorp.nid.oauth.legacy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.navercorp.nid.oauth.NidOAuthConstants;
import com.navercorp.nid.oauth.activity.NidOAuthCustomTabActivity;
import com.navercorp.nid.oauth.activity.NidOAuthWebViewActivity;

public class OAuthLoginActivity extends Activity {

    private Context context;

    public OAuthLoginActivity(Context context) {
        this.context = context;
    }

    public Intent tryOAuthByNaverapp(String clientId, String callbackUrl, String clientName, String initState) {
        try {
            Intent intent = newParamIntent(clientId, initState, callbackUrl);
            intent.putExtra("app_name", clientName);
            intent.setPackage("com.nhn.android.search");
            intent.setAction("com.nhn.android.search.action.OAUTH2_LOGIN");
            return intent;

        } catch (Exception e) {

        }
        return new Intent();
    }

    public Intent tryOAuthByCustomTab(String clientId, String callbackUrl, String initState) {
        Intent intent = newParamIntent(NidOAuthCustomTabActivity.class, clientId, initState, callbackUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    public Intent startLoginWebviewActivity(String clientId, String callbackUrl, String initState) {
        return newParamIntent(NidOAuthWebViewActivity.class
                , clientId, initState, callbackUrl);
    }

    /**
     * 네아로 로그인을 위한 파라메터 인텐트를 생성
     * @param clientId 클라이언트 ID
     * @param initState 요청 state값
     * @param callbackUrl 콜백 url (패키지명)
     * @return 생성된 인텐트
     */
    @NonNull
    private Intent newParamIntent(String clientId, String initState, String callbackUrl) {
        return newParamIntent(null, clientId, initState, callbackUrl);
    }

    /**
     * 네아로 로그인을 위한 파라메터 인텐트를 생성
     * @param nextActivity 이동할 액티비티 클래스
     * @param clientId 클라이언트 ID
     * @param initState 요청 state
     * @param callbackUrl 콜백 url 패키지명
     * @return 생성된 인텐트
     */
    @NonNull
    private Intent newParamIntent(Class<? extends Activity> nextActivity, String clientId, String initState, String callbackUrl) {
        Intent intent;
        if(nextActivity == null) {
            intent = new Intent();
        } else {
            intent = new Intent(context, nextActivity);
        }
        intent.putExtra("ClientId", clientId);
        intent.putExtra("ClientCallbackUrl", callbackUrl);
        intent.putExtra("state", initState);
        intent.putExtra("oauth_sdk_version", NidOAuthConstants.SDK_VERSION);

        return intent;
    }


}