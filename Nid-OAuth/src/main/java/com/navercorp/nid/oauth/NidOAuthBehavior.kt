package com.navercorp.nid.oauth

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
enum class NidOAuthBehavior(
    val allowsNaverApp: Boolean,
    val allowsCustomTabs: Boolean,
    val allowsWebView: Boolean
)
{
    DEFAULT     (allowsNaverApp = true,  allowsCustomTabs = true,  allowsWebView = true),
    NAVERAPP    (allowsNaverApp = true,  allowsCustomTabs = false, allowsWebView = false),
    CUSTOMTABS  (allowsNaverApp = false, allowsCustomTabs = true,  allowsWebView = false),
    WEBVIEW     (allowsNaverApp = false, allowsCustomTabs = false, allowsWebView = true)
}