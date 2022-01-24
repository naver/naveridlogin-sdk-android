package com.navercorp.nid.oauth

import com.nhn.android.oauth.BuildConfig

/**
 *
 * Created on 2021.09.16
 * Updated on 2021.09.16
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네아로SDK에서 사용하는 공통 상수값을 정의한 클래스
 */
object NidOAuthConstants {
    /* SDK */
    const val SDK_VERSION = BuildConfig.VERSION_NAME

    /* Package Name */
    const val PACKAGE_NAME_NAVERAPP = "com.nhn.android.search"
    const val PACKAGE_NAME_CHROMEAPP = "com.android.chrome"

    /* App Scheme */
    const val SCHEME_OAUTH_LOGIN = "com.nhn.android.search.action.OAUTH2_LOGIN"
    @Deprecated("This field was deprecated")
    const val SCHEME_OAUTH_LOGIN_2NDAPP = "com.naver.android.action.OAUTH2_LOGIN"

    /* Http Client */
    const val TIME_OUT = 10_000L
}