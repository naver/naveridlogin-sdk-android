package com.navercorp.nid.oauth.plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.UriMatcher
import android.net.Uri
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.NidOAuthErrorCode
import com.navercorp.nid.oauth.NidOAuthIntent
import com.navercorp.nid.oauth.NidOAuthPreferencesManager
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder

/**
 *
 * Created on 2021.10.20
 * Updated on 2021.10.20
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * NidOAuthWebViewActivity 에서 주어지는 url을 통해 핸들링 여부 판별하기 위한 플러그인
 */
object NidOAuthWebViewPlugin {
    private const val TAG = "NidOAuthWebViewPlugin"

    private const val HTTP_FINAL_URL = "http://nid.naver.com/com.nhn.login_global/inweb/finish"
    private const val HTTPS_FINAL_URL = "https://nid.naver.com/com.nhn.login_global/inweb/finish"

    private const val EMPTY_URL = 10
    private const val BLANK_URL = 11
    private const val NID_DOMAIN = 20
    private const val NID_DOMAIN_ID = 21
    private const val NID_DOMAIN_PW = 22
    private const val NID_DOMAIN_JOIN = 23
    private const val NID_DOMAIN_LOGOUT = 24
    private const val SSO_LOGOUT = 30
    private const val SSO_CROSS_DOMAIN = 31
    private const val SSO_FINALIZE = 32
    private const val CC_NAVER = 40
    private const val CR_NAVER = 41
    private const val VNO = 50
    private const val OK_NAME = 51
    private const val SIREN = 52

    val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.apply {
            addURI("nid.naver.com", "mobile/user/help/idInquiry.nhn", NID_DOMAIN_ID)
            addURI("nid.naver.com", "mobile/user/help/pwInquiry.nhn", NID_DOMAIN_PW)
            addURI("nid.naver.com", "user2/V2Join.nhn", NID_DOMAIN_JOIN)
            addURI("nid.naver.com", "nidlogin.logout", NID_DOMAIN_LOGOUT)
            addURI("*", "/sso/logout.nhn", SSO_LOGOUT)
            addURI("*", "/sso/cross-domain.nhn", SSO_CROSS_DOMAIN)
            addURI("*", "/sso/finalize.nhn", SSO_FINALIZE)
            addURI("cc.naver.com", "*", CC_NAVER)
            addURI("cr.naver.com", "*", CR_NAVER)
            addURI("cert.vno.co.kr", "*", VNO)
            addURI("ipin.ok-name.co.kr", "*", OK_NAME)
            addURI("ipin.siren24.com", "*", SIREN)
        }
    }

    fun isFinalUrl(isShouldOverrideUrl: Boolean, preUrl: String?, url: String?): Boolean {
        if (url.isNullOrEmpty()) {
            return false
        }

        if (url.equals(HTTP_FINAL_URL, ignoreCase = true)
            || url.equals(HTTPS_FINAL_URL, ignoreCase = true)
            || url.equals("http://m.naver.com/", ignoreCase = true)
            || url.equals("http://m.naver.com", ignoreCase = true)
        ) {
            return true
        }

        return if (isShouldOverrideUrl) {
            url.startsWith("https://nid.naver.com/nidlogin.login?svctype=262144")
        } else {
            isFinalUrlOnPageStarted(preUrl, url)
        }
    }

    private fun isFinalUrlOnPageStarted(preUrl: String?, url: String): Boolean {
        if (url.startsWithout("https://nid.naver.com/nidlogin.login?svctype=262144")) {
            return false
        }

        if (preUrl.isNullOrEmpty()) {
            return false
        }

        if (preUrl.startsWith("https://nid.naver.com/mobile/user/help/sleepId.nhn?m=viewSleepId&token_help=")
            || preUrl.startsWith("https://nid.naver.com/mobile/user/global/idSafetyRelease.nhn?")
            || preUrl.startsWith("https://nid.naver.com/mobile/user/help/idSafetyRelease.nhn?")) {
            return true
        }
        return false
    }

    /**
     * 현재 url에 oauth 인증 완료를 알리는 값인 code와 state 값이 있으면 현재 activity를 종료해주고,
     * 사용자가 동의창에서 취소를 눌렀을 경우 현재 activity를 종료해준다.
     * @param context context
     * @param preUrlString 조금전 진입한 url
     * @param urlString	현재 url
     * @param oAuthLoginData login 시 생성되는 혹은 필요한 데이터
     * @return	if oauth_verifier value exist, return true. else false.
     */
    fun isDoneAuthorization(context: Context, preUrl: String?, url: String?, intent: Intent): Boolean {

        var result = false

        // preUrl이 nid.naver.com인지 검증한다.
        if (!preUrl.isNullOrEmpty()
            && !(preUrl!!.startsWith("https://nid.naver.com/")
                || preUrl.startsWith("https://nid.naver.com/"))) {
            NidLog.d(TAG, "isDoneAuthorization - pre url is not naver.com")
        }
        // url이 로그아웃 url이면 현재 창을 종료한다.
        else if (url!!.startsWith("https://nid.naver.com/login/noauth/logout.nhn")
                || url.startsWith("http://nid.naver.com/nidlogin.logout")) {
            val intent = Intent()
            intent.apply {
                putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_CODE, NidOAuthErrorCode.CLIENT_USER_CANCEL.code)
                putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_DESCRIPTION, NidOAuthErrorCode.CLIENT_USER_CANCEL.description)
            }
            (context as Activity).setResult(Activity.RESULT_OK, intent)
            context.finish()
            return true
        }
        // 그 외 케이스
        else {
            var queryMap: Map<String, String>? = getQueryMap(url)

            if (!queryMap.isNullOrEmpty() && queryMap.containsKey("code") && queryMap.containsKey("state")) {
                NidLog.d(TAG, "query map contain code and state")
                result = true
            }
            else if (!queryMap.isNullOrEmpty() && queryMap.containsKey("error") && queryMap.containsKey("error_description")) {
                NidLog.d(TAG, "query map contain error, url : $url")
                result = true
                NidOAuthPreferencesManager.lastErrorCode = NidOAuthErrorCode.fromString(queryMap["error"])
                NidOAuthPreferencesManager.lastErrorDesc = getDecodedString(queryMap["error_description"])!!
            }
            else {
                var uri = Uri.parse(url)
                NidLog.d(TAG, "uri.getFragment : ${uri.fragment}")
                queryMap = getQueryMap(uri.fragment)

                if (queryMap != null && queryMap.containsKey("access_token") && queryMap.containsKey("state")) {
                    val intent = Intent()
                    intent.apply {
                        putExtra(NidOAuthIntent.OAUTH_RESULT_ACCESS_TOKEN, queryMap["access_token"])
                        putExtra(NidOAuthIntent.OAUTH_RESULT_STATE, queryMap["state"])
                        putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_CODE, queryMap["error"])
                        putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_DESCRIPTION, getDecodedString(queryMap["error_description"]!!))
                    }
                    (context as Activity).setResult(Activity.RESULT_OK, intent)
                    context.finish()
                    return true
                }
                NidLog.d(TAG, "query map does not contain code and state, url : $url")
            }
        }

        var code: String? = null
        var state: String? = null
        var errorCode: String? = null
        var errorDescription: String? = null

        if (result) {
            try {
                val queryMap = getQueryMap(url)
                if (queryMap != null) {
                    code = queryMap["code"]
                    state = queryMap["state"]
                    errorCode = queryMap["error"]
                    errorDescription = getDecodedString(queryMap["error_description"])
                    NidLog.d(TAG, "isDoneAuthorization() | code : $code")
                    NidLog.d(TAG, "isDoneAuthorization() | state : $state")
                    NidLog.d(TAG, "isDoneAuthorization() | errorCode : $errorCode")
                    NidLog.d(TAG, "isDoneAuthorization() | errorDescription : $errorDescription")
                }


                val intent = Intent()
                intent.apply {
                    putExtra(NidOAuthIntent.OAUTH_RESULT_CODE, code)
                    putExtra(NidOAuthIntent.OAUTH_RESULT_STATE, NidOAuthPreferencesManager.initState)
                    putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_CODE, errorCode)
                    putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_DESCRIPTION, errorDescription)
                }
                (context as Activity).setResult(Activity.RESULT_OK, intent)
            } catch (e: MalformedURLException) {
                val intent = Intent()
                intent.apply {
                    putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_CODE, errorCode)
                    putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_DESCRIPTION, errorDescription)
                }
                (context as Activity).setResult(Activity.RESULT_CANCELED, intent)
            }
            (context as Activity).finish()
        }
        return result
    }

    fun isInAppBrowserUrl(url: String?): Boolean {
        if (url.isNullOrEmpty() || url.contentEquals("about:blank")) {
            return true
        }

        var match = uriMatcher.match(Uri.parse(url))

        when (match) {
            NID_DOMAIN_ID,
            NID_DOMAIN_PW,
            NID_DOMAIN_JOIN -> return false
        }
        return true
    }

    fun isNotInAppBrowserUrl(url: String?): Boolean {
        return !isInAppBrowserUrl(url)
    }

    private fun getQueryMap(url: String?): Map<String, String>? {
        NidLog.d(TAG, "called getQueryMap()")
        NidLog.d(TAG, "getQueryMap() | url : $url")
        if (url.isNullOrEmpty()) {
            return null
        }
        var query: String? = null
        try {
            query = URL(url).query
        } catch (e: Exception) {
            if (url.contains("?")) {
                query = url.split("\\?".toRegex()).toTypedArray()[1]
            }
        }

        if (query.isNullOrEmpty()) {
            return null
        }
        NidLog.d(TAG, "getQueryMap() | query : $query")

        val parameters = query.split("&")
        val result: MutableMap<String, String> = hashMapOf()

        parameters.forEach {
            val key = it.split("=").toTypedArray()
            NidLog.d(TAG, "getQueryMap() | key : $key")
            if (key.size == 2) {
                NidLog.d(TAG, "getQueryMap() | key[0] : ${key[0]}")
                NidLog.d(TAG, "getQueryMap() | key[1] : ${key[1]}")
                result[key[0]] = key[1]
            } else if (key.size == 1) {
                NidLog.d(TAG, "getQueryMap() | key[0] : ${key[0]}")
                result[key[0]] = ""
            }
        }
        NidLog.d(TAG, "getQueryMap() | result : $result")
        return result
    }

    fun getDecodedString(str: String?): String? {
        NidLog.d(TAG, "called getDecodedString()")
        NidLog.d(TAG, "getDecodedString() | str : $str")
        if (str.isNullOrEmpty()) {
            return str
        }
        val decoded = URLDecoder.decode(str, "UTF-8")
        if (!decoded.isNullOrEmpty() && !decoded.equals(str, ignoreCase = true)) {
            return decoded
        }
        return str
    }
}

fun String.startsWithout(str: String): Boolean = !this.startsWith(str)