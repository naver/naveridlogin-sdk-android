package com.navercorp.nid.log

import com.nhn.android.oauth.BuildConfig

/**
 *
 * Created on 2021.09.16
 * Updated on 2021.09.16
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네아로SDK에서 사용하는 Logger 클래스
 */
object NidLog {
    private var instance: INidLog = ReleaseNidLog()

    fun init() {
        instance = if (BuildConfig.BUILD_TYPE.equals("debug", ignoreCase = true)) {
            DebugNidLog()
        } else {
            ReleaseNidLog()
        }
    }

    @JvmStatic
    fun showLog(isShow: Boolean) {
        if (isShow) {
            instance = DebugNidLog()
        }
    }

    @JvmStatic
    fun setPrefix(prefix: String) {
        instance.setPrefix(prefix)
    }

    @JvmStatic
    fun v(tag: String, message: String) {
        instance.v(tag, message)
    }

//    @JvmStatic
//    fun v(tag: String, exception: Exception) {
//        instance.v(tag, exception.toMessage())
//    }

    @JvmStatic
    fun d(tag: String, message: String) {
        instance.d(tag, message)
    }

    @JvmStatic
    fun d(tag: String, exception: Exception) {
        instance.d(tag, exception.toMessage())
    }

    @JvmStatic
    fun i(tag: String, message: String) {
        instance.i(tag, message)
    }

//    @JvmStatic
//    fun i(tag: String, exception: Exception) {
//        instance.i(tag, exception.toMessage())
//    }

    @JvmStatic
    fun w(tag: String, message: String) {
        instance.w(tag, message)
    }

//    @JvmStatic
//    fun w(tag: String, exception: Exception) {
//        instance.w(tag, exception.toMessage())
//    }

    @JvmStatic
    fun e(tag: String, message: String) {
        instance.e(tag, message)
    }

    @JvmStatic
    fun e(tag: String, exception: Exception) {
        instance.e(tag, exception.toMessage())
    }

    @JvmStatic
    fun wtf(tag: String, message: String) {
        instance.wtf(tag, message)
    }

//    @JvmStatic
//    fun wtf(tag: String, exception: Exception) {
//        instance.wtf(tag, exception.toMessage())
//    }
}

fun Exception?.toMessage(): String {
    if (this == null) {
        return "Unknown Exception"
    }
    return this.localizedMessage
}