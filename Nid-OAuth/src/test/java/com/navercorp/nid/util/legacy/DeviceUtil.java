package com.navercorp.nid.util.legacy;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.navercorp.nid.log.NidLog;
import com.navercorp.nid.util.AndroidVer;

import java.net.URLEncoder;
import java.util.Locale;

public class DeviceUtil {
    private static final String TAG = "DeviceUtil";

    /**
     * 현재 기기의 locale 값을 반환한다.
     * @param context
     * @return
     */
    public static Locale getSystemLocale(Context context) {
        Locale locale;

        if (Build.VERSION.SDK_INT >= AndroidVer.API_24_NOUGAT) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale;
    }

    /**
     * 현재 기기에 지정된 언어 값을 반환한다.
     * @param context
     * @return
     */
    public static String getLocale(Context context) {
        String defaultValue = "ko_KR";
        String locale = defaultValue;

        try {
            Locale systemLocale = getSystemLocale(context);
            locale = systemLocale.toString();

            if (TextUtils.isEmpty(locale)) {
                return defaultValue;
            }

            String encodedLanguage = URLEncoder.encode(locale, "utf-8");
            // 일부 toString 값에 #로 추가 정보가 붙는 경우가 있는데 이럴 경우 {language code}_{country code}로 처리한다.
            if (!locale.equalsIgnoreCase(encodedLanguage)) {
                locale = systemLocale.getLanguage() + "_" + systemLocale.getCountry();
            }
        } catch (Exception e) {
            NidLog.e(TAG, e);
        }

        return locale;
    }

    /**
     * 현재 기기에 지정된 언어가 한국어인지 판정한다.
     * @param context
     * @return 한국어면 true, 아니면 false
     */
    public static boolean isKorean(Context context) {
        if (getSystemLocale(context).getLanguage().startsWith("ko")) {
            return true;
        }
        return false;
    }
}
