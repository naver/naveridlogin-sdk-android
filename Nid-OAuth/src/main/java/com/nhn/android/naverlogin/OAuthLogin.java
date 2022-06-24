package com.nhn.android.naverlogin;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Surface;

import com.navercorp.nid.NaverIdLoginSDK;
import com.navercorp.nid.oauth.OAuthLoginCallback;
import com.navercorp.nid.log.NidLog;
import com.navercorp.nid.oauth.NidOAuthBehavior;
import com.navercorp.nid.oauth.NidOAuthErrorCode;
import com.navercorp.nid.oauth.NidOAuthLoginState;


/**
 * Created on 2011.08.31
 * Updated on 2021.10.19
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 * Naver Digital ID & Authentication Platform.
 *
 * 네이버아이디로로그인 SDK 를 사용할 때 주로 사용되는 class
 * - getInstance() 로 인스턴스를 받아서 필요한 동작을 수행한다.
 * - OAuth2.0 연동 / 연동 해제 / OpenAPI 호출 등이 가능하다.
 */
@Deprecated
public class OAuthLogin {
	private static final String TAG = "OAuthLogin";

	private static OAuthLogin instance;

	public static OAuthLogin getInstance() {
		if (instance == null) {
			instance = new OAuthLogin();
		}
		return instance;
	}
	private OAuthLogin() {}


	@Deprecated
	public void init(Context context, String clientId, String clientSecret, String clientName) {
		NaverIdLoginSDK.INSTANCE.initialize(context, clientId, clientSecret, clientName);
	}

	@Deprecated
	public void init(Context context, String clientId, String clientSecret, String clientName, String callbackIntent) {
		NaverIdLoginSDK.INSTANCE.initialize(context, clientId, clientSecret, clientName);
	}

	@Deprecated
	public void showDevelopersLog(boolean show) {
		NaverIdLoginSDK.INSTANCE.showDevelopersLog(show);
	}

	@Deprecated
	public static String getVersion() {
		return NaverIdLoginSDK.INSTANCE.getVersion();
	}

	@Deprecated
	public NidOAuthLoginState getState(Context context) {
		return NaverIdLoginSDK.INSTANCE.getState();
	}

	@Deprecated
	public void initializeLoginFlag() {
		NaverIdLoginSDK.INSTANCE.setBehavior(NidOAuthBehavior.DEFAULT);
	}

	@Deprecated
	public void enableNaverAppLoginOnly() {
		NaverIdLoginSDK.INSTANCE.setBehavior(NidOAuthBehavior.NAVERAPP);
	}

	@Deprecated
	public void enableCustomTabLoginOnly() {
		NaverIdLoginSDK.INSTANCE.setBehavior(NidOAuthBehavior.CUSTOMTABS);
	}

	@Deprecated
	public void enableWebViewLoginOnly() {
		NaverIdLoginSDK.INSTANCE.setBehavior(NidOAuthBehavior.WEBVIEW);
	}

	@Deprecated
	public void setCustomTabReAuth(boolean value) {
		NaverIdLoginSDK.INSTANCE.setRequiredCustomTabsReAuth(value);
	}

	@Deprecated
	public void setMarketLinkWorking(boolean set) {
		NaverIdLoginSDK.INSTANCE.setShowMarketLink(set);
	}

	@Deprecated
	public void setShowingBottomTab(boolean set) {
		NaverIdLoginSDK.INSTANCE.setShowBottomTab(set);
	}

	@Deprecated
	public void startOauthLoginActivity(final Activity activity, final OAuthLoginCallback oauthLoginCallback) {
		NaverIdLoginSDK.INSTANCE.authenticate(activity, oauthLoginCallback);
	}

	@Deprecated
	public String getAccessToken(Context context) {
		return NaverIdLoginSDK.INSTANCE.getAccessToken();
	}

	@Deprecated
	public String getRefreshToken(Context context) {
		return NaverIdLoginSDK.INSTANCE.getRefreshToken();
	}

	@Deprecated
	public long getExpiresAt(Context context) {
		return NaverIdLoginSDK.INSTANCE.getExpiresAt();
	}

	@Deprecated
	public String getTokenType(Context context) {
		return NaverIdLoginSDK.INSTANCE.getTokenType();
	}

	@Deprecated
	public NidOAuthErrorCode getLastErrorCode(Context context) {
		return NaverIdLoginSDK.INSTANCE.getLastErrorCode();
	}

	@Deprecated
	public String getLastErrorDesc(Context context) {
		return NaverIdLoginSDK.INSTANCE.getLastErrorDescription();
	}

	public int getOrientation(Context context) {
		return getOrientation((Activity)context);
	}

	private int getOrientation(Activity activity) {
		if (activity.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90) {
			NidLog.d(TAG, "getOrientation : landscape");
			return Configuration.ORIENTATION_LANDSCAPE;
		} else {
			NidLog.d(TAG, "getOrientation : portrait");
			return Configuration.ORIENTATION_PORTRAIT;
		}
	}
}