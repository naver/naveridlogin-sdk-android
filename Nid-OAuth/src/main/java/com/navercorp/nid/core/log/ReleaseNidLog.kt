package com.navercorp.nid.core.log

/**
 *
 * Created on 2021.09.16
 * Updated on 2021.09.16
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * Release 형상일때 적용되어 Log를 출력하지 않는 클래스
 */
class ReleaseNidLog : INidLog {

    private var prefix: String = ""

    override fun setPrefix(prefix: String) {
        this.prefix = prefix
    }

    override fun v(tag: String, message: String) {
        // Do nothings.
    }

    override fun d(tag: String, message: String) {
        // Do nothings.
    }

    override fun i(tag: String, message: String) {
        // Do nothings.
    }

    override fun w(tag: String, message: String) {
        // Do nothings.
    }

    override fun e(tag: String, message: String) {
        // Do nothings.
    }

    override fun wtf(tag: String, message: String) {
        // Do nothings.
    }
}