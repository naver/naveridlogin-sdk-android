package com.navercorp.nid.oauth.domain.enum

/**
 *
 * Created on 2021.10.18
 * Updated on 2021.10.18
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네아로SDK의 OAuth 동작을 구분하기 위한 Behavior 값
 */
enum class LoginBehavior(
    val allowsNaverApp: Boolean,
    val allowsCustomTabs: Boolean,
) {
    DEFAULT     (allowsNaverApp = true,  allowsCustomTabs = true),
    NAVERAPP    (allowsNaverApp = true,  allowsCustomTabs = false),
    CUSTOMTABS  (allowsNaverApp = false, allowsCustomTabs = true);

    companion object {
        /**
         * Convert to NidOAuthBehavior.
         */
        fun toNidOAuthBehavior(behavior: LoginBehavior): NidOAuthBehavior {
            return when (behavior) {
                DEFAULT     -> NidOAuthBehavior.DEFAULT
                NAVERAPP    -> NidOAuthBehavior.NAVERAPP
                CUSTOMTABS  -> NidOAuthBehavior.CUSTOMTABS
            }
        }
    }
}