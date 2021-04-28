package com.nhn.android.oauth.test.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.nhn.android.oauth.test.R;

import java.lang.ref.WeakReference;

/// 네이버 아이디로 로그인 샘플앱

/**
 * <br/> OAuth2.0 인증을 통해 Access Token을 발급받는 예제, 연동해제하는 예제,
 * <br/> 발급된 Token을 활용하여 Get 등의 명령을 수행하는 예제, 네아로 커스터마이징 버튼을 사용하는 예제 등이 포함되어 있다.
 *
 * @author naver
 */
public class OAuthSampleActivity extends Activity {

	private static final String TAG = "OAuthSampleActivity";

	/**
	 * client 정보를 넣어준다.
	 */
	private static final String OAUTH_CLIENT_ID = "jyvqXeaVOVmV";
	private static final String OAUTH_CLIENT_SECRET = "527300A0_COq1_XV33cf";
	private static final String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";

	private Context mContext;

	/**
	 * UI 요소들
	 */
	private TextView mApiResultText;
	private TextView mOauthAT;
	private TextView mOauthRT;
	private TextView mOauthExpires;
	private TextView mOauthTokenType;
	private TextView mOAuthState;

	@SuppressWarnings("FieldCanBeLocal")
	private OAuthLoginButton mOAuthLoginButton;

	private static OAuthLogin mOAuthLoginInstance;
	private OAuthLoginHandler mOAuthLoginHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.naveroauthlogin_sample_main);

		mContext = this;
		mOAuthLoginHandler = new MyOAuthLoginHandler(this);

		initData();
		initView();

		this.setTitle("OAuthLoginSample Ver." + OAuthLogin.getVersion());
	}


	private void initData() {
		mOAuthLoginInstance = OAuthLogin.getInstance();

		mOAuthLoginInstance.showDevelopersLog(true);
		mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

		/*
	     * 2015년 8월 이전에 등록하고 앱 정보 갱신을 안한 경우 기존에 설정해준 callback intent url 을 넣어줘야 로그인하는데 문제가 안생긴다.
		 * 2015년 8월 이후에 등록했거나 그 뒤에 앱 정보 갱신을 하면서 package name 을 넣어준 경우 callback intent url 을 생략해도 된다.
		 */
		//mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME, OAUTH_callback_intent_url);
	}

	private void initView() {
		mApiResultText = findViewById(R.id.api_result_text);

		mOauthAT = findViewById(R.id.oauth_access_token);
		mOauthRT = findViewById(R.id.oauth_refresh_token);
		mOauthExpires = findViewById(R.id.oauth_expires);
		mOauthTokenType = findViewById(R.id.oauth_type);
		mOAuthState = findViewById(R.id.oauth_state);

		mOAuthLoginButton = findViewById(R.id.buttonOAuthLoginImg);
		mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);

		updateView();
	}


	private void updateView() {
		mOauthAT.setText(mOAuthLoginInstance.getAccessToken(mContext));
		mOauthRT.setText(mOAuthLoginInstance.getRefreshToken(mContext));
		mOauthExpires.setText(String.valueOf(mOAuthLoginInstance.getExpiresAt(mContext)));
		mOauthTokenType.setText(mOAuthLoginInstance.getTokenType(mContext));
		mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());
	}

	@Override
	protected void onResume() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onResume();

	}

	/**
	 * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
	 */
	private static class MyOAuthLoginHandler extends OAuthLoginHandler {

		WeakReference<OAuthSampleActivity> mActivityReference;

		private final TextView mOauthAT;
		private final TextView mOauthRT;
		private final TextView mOauthExpires;
		private final TextView mOauthTokenType;
		private final TextView mOAuthState;

		MyOAuthLoginHandler(OAuthSampleActivity context) {
			mActivityReference = new WeakReference<>(context);

			mOauthAT = mActivityReference.get().findViewById(R.id.oauth_access_token);
			mOauthRT = mActivityReference.get().findViewById(R.id.oauth_refresh_token);
			mOauthExpires = mActivityReference.get().findViewById(R.id.oauth_expires);
			mOauthTokenType = mActivityReference.get().findViewById(R.id.oauth_type);
			mOAuthState = mActivityReference.get().findViewById(R.id.oauth_state);
		}

		@Override
		public void run(boolean success) {
			if (success) {
				String accessToken = mOAuthLoginInstance.getAccessToken(mActivityReference.get());
				String refreshToken = mOAuthLoginInstance.getRefreshToken(mActivityReference.get());
				long expiresAt = mOAuthLoginInstance.getExpiresAt(mActivityReference.get());
				String tokenType = mOAuthLoginInstance.getTokenType(mActivityReference.get());
				mOauthAT.setText(accessToken);
				mOauthRT.setText(refreshToken);
				mOauthExpires.setText(String.valueOf(expiresAt));
				mOauthTokenType.setText(tokenType);
				mOAuthState.setText(mOAuthLoginInstance.getState(mActivityReference.get()).toString());
			} else {
				String errorCode = mOAuthLoginInstance.getLastErrorCode(mActivityReference.get()).getCode();
				String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mActivityReference.get());
				Toast.makeText(mActivityReference.get(), "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
			}
		}

	};

	public void onButtonClick(View v) throws Throwable {
		if (v.getId() == R.id.buttonOAuth) {
			mOAuthLoginInstance.startOauthLoginActivity(OAuthSampleActivity.this, mOAuthLoginHandler);
		} else if (v.getId() == R.id.buttonVerifier) {
			new RequestApiTask().execute();
		} else if (v.getId() == R.id.buttonRefresh) {
			new RefreshTokenTask().execute();
		} else if (v.getId() == R.id.buttonOAuthLogout) {
			mOAuthLoginInstance.logout(mContext);
			updateView();
		} else if (v.getId() == R.id.buttonOAuthDeleteToken) {
			new DeleteTokenTask().execute();
		} else {
			Log.e(TAG, "Unexpected view ID");
		}
	}

	private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(mContext);

			if (!isSuccessDeleteToken) {
				// 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
				// 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
				Log.d(TAG, "errorCode:" + mOAuthLoginInstance.getLastErrorCode(mContext));
				Log.d(TAG, "errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(mContext));
			}

			return null;
		}

		protected void onPostExecute(Void v) {
			updateView();
		}
	}

	private class RequestApiTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			mApiResultText.setText((String) "");
		}

		@Override
		protected String doInBackground(Void... params) {
			String url = "https://openapi.naver.com/v1/nid/me";
			String at = mOAuthLoginInstance.getAccessToken(mContext);
			return mOAuthLoginInstance.requestApi(mContext, at, url);
		}

		protected void onPostExecute(String content) {
			mApiResultText.setText((String) content);
		}
	}

	private class RefreshTokenTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			return mOAuthLoginInstance.refreshAccessToken(mContext);
		}

		protected void onPostExecute(String res) {
			updateView();
		}
	}
}