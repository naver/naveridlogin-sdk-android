package com.navercorp.nid.log

import android.util.Log

/**
 *
 * Created on 2021.09.16
 * Updated on 2021.09.16
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * Debug 형상일 때 로그를 출력해주는 클래스
 */
class DebugNidLog : INidLog {

    private var prefix: String = ""

    override fun setPrefix(prefix: String) {
        this.prefix = prefix
    }

    override fun v(tag: String, message: String) {
        Log.v(prefix + tag, message)
    }

    override fun d(tag: String, message: String) {
        Log.d(prefix + tag, message)
    }

    override fun i(tag: String, message: String) {
        Log.i(prefix + tag, message)
    }

    override fun w(tag: String, message: String) {
        Log.w(prefix + tag, message)
    }

    override fun e(tag: String, message: String) {
        Log.e(prefix + tag, message)
    }

    override fun wtf(tag: String, message: String) {
        Log.wtf(prefix + tag, message)
    }
}
