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
@Deprecated(
    message = "This class will be removed from v6.1.0. Use LoginBehavior instead.",
    replaceWith = ReplaceWith("LoginBehavior"),
)
enum class NidOAuthBehavior(
    val allowsNaverApp: Boolean,
    val allowsCustomTabs: Boolean,
) {
    @Deprecated(
        message = "This will be removed from v6.1.0. Use LoginBehavior.DEFAULT instead.",
        replaceWith = ReplaceWith("LoginBehavior.DEFAULT"),
    )
    DEFAULT     (allowsNaverApp = true,  allowsCustomTabs = true),
    @Deprecated(
        message = "This will be removed from v6.1.0. Use LoginBehavior.NAVERAPP instead.",
        replaceWith = ReplaceWith("LoginBehavior.NAVERAPP"),
    )
    NAVERAPP    (allowsNaverApp = true,  allowsCustomTabs = false),
    @Deprecated(
        message = "This will be removed from v6.1.0. Use LoginBehavior.CUSTOMTABS instead.",
        replaceWith = ReplaceWith("LoginBehavior.CUSTOMTABS"),
    )
    CUSTOMTABS  (allowsNaverApp = false, allowsCustomTabs = true);

    companion object {
        /**
         * Convert to LoginBehavior
         */
        fun toLoginBehavior(behavior: NidOAuthBehavior): LoginBehavior {
            return when (behavior) {
                DEFAULT -> LoginBehavior.DEFAULT
                NAVERAPP -> LoginBehavior.NAVERAPP
                CUSTOMTABS -> LoginBehavior.CUSTOMTABS
            }
        }
    }
}