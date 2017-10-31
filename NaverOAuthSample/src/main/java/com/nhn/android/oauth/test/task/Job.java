package com.nhn.android.oauth.test.task;

import android.os.AsyncTask;

/**
 * {@link AsyncTask}의 {@link AsyncTask#doInBackground(Object[])} 에서 실행할 인터페이스
 *
 * Created by Naver on 17. 10. 31.
 */
public interface Job<Param, Result> {
	Result job(Param... params);
}
