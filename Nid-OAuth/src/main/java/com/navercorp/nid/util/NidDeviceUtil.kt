package com.navercorp.nid.util

import android.content.Context
import android.os.Build
import java.net.URLEncoder
import java.util.*

/**
 *
 * Created on 2021.10.19
 * Updated on 2021.10.19
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 기기 관련 유틸리티 클래스
 */
object NidDeviceUtil {

    /**
     * 현재 기기의 locale 값을 반환한다.
     */
    fun getSystemLocale(context: Context) : Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
    }

    /**
     * 현재 기기에 지정된 언어값을 반환한다.
     */
    fun getLocale(context: Context) : String {
        var systemLocale = getSystemLocale(context)
        if (systemLocale.toString().isEmpty()) {
            return "ko_KR"
        }

        var locale = systemLocale.toString()

        // 일부 toString 값에 #로 추가 정보가 붙는 경우가 있는데 이럴 경우 {language code}_{country code}로 처리한다.
        var encodedLocale = URLEncoder.encode(locale, "utf-8")
        if (!locale.equals(encodedLocale, ignoreCase = true)) {
            locale = "${systemLocale.language}_${systemLocale.country}"
        }
        return locale
    }

    fun isKorean(context: Context) : Boolean = getSystemLocale(context).language.startsWith("ko")
}