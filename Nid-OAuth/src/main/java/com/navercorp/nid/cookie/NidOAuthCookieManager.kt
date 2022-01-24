package com.navercorp.nid.cookie

import android.webkit.CookieManager
import com.navercorp.nid.log.NidLog


/**
 *
 * Created on 2021.10.19
 * Updated on 2021.10.19
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * OAuth로 획득한 쿠키를 관리하는 클래스
 */
object NidOAuthCookieManager {
    private val TAG = "NidOAuthCookieManager"

    private val NID_DOMAIN = "https://nid.naver.com"

    /**
     * CookieManager에 naver 도메인에 login cookie를 심는다.
     * (cookie String -> cookie manager)
     * @param cookies
     * @throws InterruptedException
     */
    fun setCookie(url: String, cookies: List<String>) {
        val cookieManager = CookieManager.getInstance()
        NidLog.i(TAG, "setCookie url : $url")

        cookies.forEach {
            cookieManager.setCookie(url, it)
            NidLog.i(TAG, "setCookie : $it")
        }
        cookieManager.flush()
    }

    /**
     * .naver.com 및 nid.naver.com에 심어진 쿠키를 반환한다.
     */
    fun getCookie(): String = CookieManager.getInstance().getCookie(NID_DOMAIN)

    /**
     * Api 통신의 결과로 얻은 header를 파싱해서 Cookie 리스트를 반환한다.
     */
    fun getCookieListFromHeader(header: Map<String, List<String>>): List<String> {
        var cookieList = ArrayList<String>()

        header.keys.forEach {
            if ("Set-Cookie".equals(it, ignoreCase = true)) {
                val headerList = header[it]
                if (headerList != null && headerList.isNotEmpty()) {
                    for (c in headerList) {
                        var cookie = c.trim()
                        if (cookie.endsWith(";")) {
                            cookieList.add("$cookie")
                        } else {
                            cookieList.add("$cookie;")
                        }
                        NidLog.d(TAG, "cookie : $cookie")
                    }
                }
            }
        }
        return cookieList
    }
}