package com.nhn.android.oauth.test.task;

import android.os.AsyncTask;

/**
 * 기본 AsyncTask promise 패턴으로 콜백을 설정합니다.
 * Created by Naver on 17. 10. 31.
 */
public class OAuthLoginAsyncTask<Params, Result> extends AsyncTask<Params, Void, Result> {

	private Runnable mPreTask;
	private Job<Params, Result> mJob;
	private ResultCallback<Result> mPostTask;

	public OAuthLoginAsyncTask<Params, Result> preCallback(Runnable preTask) {
		this.mPreTask = preTask;
		return this;
	}

	public OAuthLoginAsyncTask<Params, Result> job(Job<Params, Result> job) {
		this.mJob = job;
		return this;
	}

	public OAuthLoginAsyncTask<Params, Result> postCallback(ResultCallback<Result> postTask) {
		this.mPostTask = postTask;
		return this;
	}

	@Override
	protected Result doInBackground(Params[] params) {
		return mJob.job(params);
	}

	@Override
	protected void onPreExecute() {
		if(mPreTask != null) {
			mPreTask.run();
		}
	}

	@Override
	protected void onPostExecute(Result o) {
		if(mPostTask != null) {
			mPostTask.callback(o);
		}
	}
}
