package com.navercorp.nid.oauth.plugin

import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.oauth.plugin.legacy.OAuthWebviewUrlUtil
import com.navercorp.nid.oauth.plugin.legacy.WebLoadUtil
import org.junit.Assert
import org.junit.Test

class NidOAuthWebViewPluginTest: NaverIdTestCase() {
    @Test
    fun isFinalUrl_테스트() {

        Assert.assertFalse(NidOAuthWebViewPlugin.isFinalUrl(false, "", null))
        Assert.assertFalse(NidOAuthWebViewPlugin.isFinalUrl(false, "", ""))

        Assert.assertTrue(NidOAuthWebViewPlugin.isFinalUrl(false, "", "http://nid.naver.com/com.nhn.login_global/inweb/finish"))
        Assert.assertTrue(NidOAuthWebViewPlugin.isFinalUrl(false, "", "https://nid.naver.com/com.nhn.login_global/inweb/finish"))
        Assert.assertTrue(NidOAuthWebViewPlugin.isFinalUrl(false, "", "http://m.naver.com/"))
        Assert.assertTrue(NidOAuthWebViewPlugin.isFinalUrl(false, "", "http://m.naver.com"))


        Assert.assertTrue(NidOAuthWebViewPlugin.isFinalUrl(true, "", "https://nid.naver.com/nidlogin.login?svctype=262144"))
        Assert.assertFalse(NidOAuthWebViewPlugin.isFinalUrl(true, "", "https://nid.naver.com/nidlogin.login?"))

        Assert.assertFalse(NidOAuthWebViewPlugin.isFinalUrl(false, "", "https://nid.naver.com/nidlogin.login?"))

        Assert.assertFalse(NidOAuthWebViewPlugin.isFinalUrl(false, "", "https://nid.naver.com/nidlogin.login?svctype=262144"))
        Assert.assertFalse(NidOAuthWebViewPlugin.isFinalUrl(false, "", "https://nid.naver.com/nidlogin.login?svctype=262144"))

        Assert.assertTrue(NidOAuthWebViewPlugin.isFinalUrl(false, "https://nid.naver.com/mobile/user/help/sleepId.nhn?m=viewSleepId&token_help=", "https://nid.naver.com/nidlogin.login?svctype=262144"))
        Assert.assertTrue(NidOAuthWebViewPlugin.isFinalUrl(false, "https://nid.naver.com/mobile/user/global/idSafetyRelease.nhn?", "https://nid.naver.com/nidlogin.login?svctype=262144"))
        Assert.assertTrue(NidOAuthWebViewPlugin.isFinalUrl(false, "https://nid.naver.com/mobile/user/help/idSafetyRelease.nhn?", "https://nid.naver.com/nidlogin.login?svctype=262144"))

    }

    @Test
    fun isFinalUrl_리그레션_테스트() {

        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(false, "", null),
            NidOAuthWebViewPlugin.isFinalUrl(false, "", null)
        )

        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(false, "", "http://nid.naver.com/com.nhn.login_global/inweb/finish"),
            NidOAuthWebViewPlugin.isFinalUrl(false, "", "http://nid.naver.com/com.nhn.login_global/inweb/finish")
        )
        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(false, "", "https://nid.naver.com/com.nhn.login_global/inweb/finish"),
            NidOAuthWebViewPlugin.isFinalUrl(false, "", "https://nid.naver.com/com.nhn.login_global/inweb/finish")
        )
        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(false, "", "http://m.naver.com/"),
            NidOAuthWebViewPlugin.isFinalUrl(false, "", "http://m.naver.com/")
        )
        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(false, "", "http://m.naver.com"),
            NidOAuthWebViewPlugin.isFinalUrl(false, "", "http://m.naver.com")
        )

        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(true, "", "https://nid.naver.com/nidlogin.login?svctype=262144"),
            NidOAuthWebViewPlugin.isFinalUrl(true, "", "https://nid.naver.com/nidlogin.login?svctype=262144")
        )
        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(true, "", "https://nid.naver.com/nidlogin.login?"),
            NidOAuthWebViewPlugin.isFinalUrl(true, "", "https://nid.naver.com/nidlogin.login?")
        )

        Assert.assertEquals(
            Assert.assertFalse(OAuthWebviewUrlUtil.isFinalUrl(false, "", "https://nid.naver.com/nidlogin.login?")),
            Assert.assertFalse(NidOAuthWebViewPlugin.isFinalUrl(false, "", "https://nid.naver.com/nidlogin.login?"))
        )

        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(false, "", "https://nid.naver.com/nidlogin.login?svctype=262144"),
            NidOAuthWebViewPlugin.isFinalUrl(false, "", "https://nid.naver.com/nidlogin.login?svctype=262144")
        )

        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(false, "https://nid.naver.com/mobile/user/help/sleepId.nhn?m=viewSleepId&token_help=", "https://nid.naver.com/nidlogin.login?svctype=262144"),
            NidOAuthWebViewPlugin.isFinalUrl(false, "https://nid.naver.com/mobile/user/help/sleepId.nhn?m=viewSleepId&token_help=", "https://nid.naver.com/nidlogin.login?svctype=262144")
        )
        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(false, "https://nid.naver.com/mobile/user/global/idSafetyRelease.nhn?", "https://nid.naver.com/nidlogin.login?svctype=262144"),
            NidOAuthWebViewPlugin.isFinalUrl(false, "https://nid.naver.com/mobile/user/global/idSafetyRelease.nhn?", "https://nid.naver.com/nidlogin.login?svctype=262144")
        )
        Assert.assertEquals(
            OAuthWebviewUrlUtil.isFinalUrl(false, "https://nid.naver.com/mobile/user/help/idSafetyRelease.nhn?", "https://nid.naver.com/nidlogin.login?svctype=262144"),
            NidOAuthWebViewPlugin.isFinalUrl(false, "https://nid.naver.com/mobile/user/help/idSafetyRelease.nhn?", "https://nid.naver.com/nidlogin.login?svctype=262144")
        )

    }

    val urlList = listOf(
        "", // empty
        "about:blank", // blank
        "https://nid.naver.com", // nid_domain
        "https://nid.naver.com/mobile/user/help/idInquiry.nhn", // nid_domain_id
        "https://nid.naver.com/mobile/user/help/pwInquiry.nhn", // nid_domain_pw
        "https://nid.naver.com/user/mobile_join.nhn", // nid_domain_join
        "http://nid.naver.com/nidlogin.logout", // http_logout
        "https://nid.naver.com/nidlogin.logout", // https_logout
        "/sso/logout.nhn", // sso_logout
        "/sso/cross-domain.nhn", // sso_cross_domain
        "/sso/finalize.nhn", // sso_finalize
        "http://cc.naver.com", // http_cc_naver
        "https://cc.naver.com", // https_cc_naver
        "http://cr.naver.com", // http_cr_naver
        "https://cr.naver.com", // https_cr_naver
        "https://cert.vno.co.kr", // 나이스신용평가
        "https://ipin.ok-name.co.kr", // 코리아크레딧뷰로
        "https://ipin.siren24.com" // 서신평
    )

    @Test
    fun isInAppBrowserUrl_테스트() {

        for (url in urlList) {
            when (url) {
                "https://nid.naver.com/mobile/user/help/idInquiry.nhn",
                "https://nid.naver.com/mobile/user/help/pwInquiry.nhn",
                "https://nid.naver.com//user2/V2Join.nhn" -> {
                    Assert.assertFalse(NidOAuthWebViewPlugin.isInAppBrowserUrl(url))
                }
                else -> {
                    Assert.assertTrue(NidOAuthWebViewPlugin.isInAppBrowserUrl(url))
                }
            }
        }
    }

    @Test
    fun isInAppBrowserUrl_리그레션_테스트() {
        for (url in urlList) {
            Assert.assertEquals(
                WebLoadUtil.isInAppBrowserUrl(url), NidOAuthWebViewPlugin.isInAppBrowserUrl(url)
            )
        }
    }

    @Test
    fun isNotInAppBrowserUrl_테스트() {
        for (url in urlList) {
            if (NidOAuthWebViewPlugin.isInAppBrowserUrl(url)) {
                Assert.assertFalse(NidOAuthWebViewPlugin.isNotInAppBrowserUrl(url))
            } else {
                Assert.assertTrue(NidOAuthWebViewPlugin.isNotInAppBrowserUrl(url))
            }
        }

    }
}