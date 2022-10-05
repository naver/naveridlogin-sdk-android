package com.navercorp.nid.oauth.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.webkit.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.NidOAuthIntent
import com.navercorp.nid.oauth.NidOAuthQuery
import com.navercorp.nid.oauth.plugin.NidOAuthWebViewPlugin
import com.navercorp.nid.oauth.view.DownloadBanner
import com.navercorp.nid.scheme.api.NidSchemeApi
import com.navercorp.nid.util.AndroidVer
import com.navercorp.nid.util.NidNetworkUtil
import com.navercorp.nid.util.UserAgentFactory
import com.nhn.android.oauth.R
import com.nhn.android.oauth.databinding.ActivityOauthWebviewBinding
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

/**
 *
 * Created on 2021.10.19
 * Updated on 2021.10.19
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * WebView를 통해 OAuth 2.0을 수행하는 Activity
 */
@Deprecated("WebView is deprecated")
class NidOAuthWebViewActivity : AppCompatActivity() {
//    private val TAG = "NidOAuthWebViewActivity"
//
//    /* Common */
//    private lateinit var context: Context
//    private var isAlreadyExecuted = false
//
//    /* View */
//    private lateinit var binding: ActivityOauthWebviewBinding
//    private var wholeViewHeight: Int = 0
//    private var isVisibleDownloadBanner = true
//    private var downloadBanner: DownloadBanner? = null
//    private var progressBar: ProgressBar? = null
//
//    /* WebView */
//    private var url: String? = null
//    private var wholeView: LinearLayout? = null
//    private var webView: WebView? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityOauthWebviewBinding.inflate(layoutInflater)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setContentView(binding.root)
//
//        init()
//        restoreActivity(savedInstanceState)
//    }
//
//    private fun init() {
//        context = this@NidOAuthWebViewActivity
//        initData()
//        initView()
//        initWebView()
//    }
//
//    private fun initData() {
//        if (intent == null) {
//            return
//        }
//
//        val authUrl = intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_URL)
//        url = if (!authUrl.isNullOrEmpty() && isValidNidUrl(authUrl)) {
//            authUrl
//        } else {
//            NidOAuthQuery.Builder(this)
//                .setMethod(NidOAuthQuery.Method.WEB_VIEW)
//                .build()
//        }
//        NidLog.d(TAG, "initData() | url : $url")
//    }
//
//    private fun initView() {
//        val navigationBar = binding.webviewNaviBar
//        if (!NaverIdLoginSDK.isShowBottomTab) {
//            navigationBar.visibility = View.GONE
//        }
//
//        wholeView = binding.wholeView
//        wholeView?.let {
//            it.viewTreeObserver.addOnGlobalLayoutListener {
//                if (wholeViewHeight == 0) {
//                    wholeViewHeight = it.height
//                }
//                if (wholeViewHeight > it.height || !NaverIdLoginSDK.isShowBottomTab) {
//                    navigationBar.visibility = View.GONE
//                } else {
//                    navigationBar.visibility = View.VISIBLE
//                }
//            }
//        }
//
//        binding.webviewEndKey.apply {
//            isClickable = true
//            setOnClickListener { finish() }
//        }
//
//        downloadBanner = binding.appDownloadBanner
//        if (NaverIdLoginSDK.isShowMarketLink) {
//            downloadBanner?.visibility = View.VISIBLE
//        }
//
//        progressBar = binding.progressBar
//    }
//
//    private fun initWebView() {
//        webView = binding.webView
//        webView?.apply {
//            setVerticalScrollbarOverlay(true)
//            setHorizontalScrollbarOverlay(true)
//        }
//        webView?.webViewClient = object : WebViewClient() {
//            var preUrl: String? = ""
//
//            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
//                NidLog.d(TAG, "onPageStarted() | preUrl : $preUrl")
//                NidLog.d(TAG, "onPageStarted() |    url : $url")
//                if (NidOAuthWebViewPlugin.isFinalUrl(true, preUrl, url)) {
//                    webView?.stopLoading()
//                    finish()
//                    return
//                }
//                if (NidOAuthWebViewPlugin.isDoneAuthorization(context, preUrl, url, intent)) {
//                    webView?.stopLoading()
//                    return
//                }
//                super.onPageStarted(view, url, favicon)
//                if (progressBar != null) {
//                    progressBar!!.visibility = View.VISIBLE
//                }
//            }
//
//            @RequiresApi(AndroidVer.API_21_LOLLIPOP)
//            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//                return shouldOverrideUrlLoading(view, request?.url.toString())
//            }
//
//            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                if (NidOAuthWebViewPlugin.isFinalUrl(false, preUrl, url)) {
//                    webView?.stopLoading()
//                    finish()
//                    return true
//                }
//                if (NidOAuthWebViewPlugin.isDoneAuthorization(context, preUrl, url, intent)) {
//                    return true
//                }
//                if (NidOAuthWebViewPlugin.isNotInAppBrowserUrl(url)) {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.data = Uri.parse(url)
//
//                    try {
//                        startActivity(intent)
//                    } catch (e: ActivityNotFoundException) {
//                        Toast.makeText(context, R.string.naveroauthlogin_string_browser_app_issue, Toast.LENGTH_SHORT).show()
//                    }
//                    return true
//                }
//
//                // TODO
//                var uri = Uri.parse(url)
//                var scheme = uri.scheme
//                if ("nidlogin".equals(scheme, ignoreCase = true)) {
//                    try {
//                        var intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
//                        if (intent.resolveActivity(packageManager) == null) {
//                            var builder = StringBuilder()
//                            builder.apply {
//                                append("SchemeLogin failed in NaverIdLogin SDK")
//                                append("\n")
//                                append("Naverapp is not exist")
//                                append("\n")
//                                append(UserAgentFactory.create(context))
//                            }
//                            NidSchemeApi().requestSchemeLog(context, builder.toString())
//                            return false
//                        } else {
//                            startActivity(intent)
//                            return true
//                        }
//                    } catch (e: URISyntaxException) {
//                        e.printStackTrace()
//                    }
//                }
//                if (!url.isNullOrEmpty()) {
//                    view?.loadUrl(url)
//                }
//                preUrl = url
//                return true
//            }
//
//            override fun onPageFinished(view: WebView, url: String?) {
//                super.onPageFinished(view, url)
//                if (progressBar != null) {
//                    progressBar!!.visibility = View.GONE
//                }
//                view.clearCache(true)
//            }
//
//            @RequiresApi(AndroidVer.API_23_MARSHMALLOW)
//            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
//                return onReceivedError(view, error.errorCode, error.description.toString(), request.url.toString())
//            }
//
//            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
//                if (progressBar != null) {
//                    progressBar!!.visibility = View.GONE
//                }
//
//                if (NidNetworkUtil.isNotAvailable(context)) {
//                    val msg = getString(R.string.naveroauthlogin_string_network_state_not_available)
//                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
//                    finish()
//                    return
//                }
//            }
//        }
//
//
//        webView?.webChromeClient = object : WebChromeClient() {
//            override fun onProgressChanged(view: WebView?, newProgress: Int) {
//                if (progressBar != null) {
//                    progressBar!!.progress = newProgress
//                }
//            }
//        }
//
//        webView?.setDownloadListener { url, _, _, mimetype, _ ->
//            var intent = Intent(Intent.ACTION_VIEW)
//            intent.setDataAndType(Uri.parse(url), mimetype)
//
//            try {
//                startActivity(intent)
//            } catch (t: Throwable) {
//                // In case of the context not being attached a activity, this is useless
//                try {
//                    intent.data = Uri.parse(url)
//                    startActivity(intent)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//        }
//
//        webView?.settings?.run {
//            javaScriptEnabled = true
//            userAgentString = "${this.userAgentString} ${UserAgentFactory.create(context)}"
//            setAppCacheEnabled(false)
//            cacheMode = WebSettings.LOAD_NO_CACHE
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (webView != null) {
//            webView?.resumeTimers()
//            webView?.onResume()
//        }
//
//        if (!isAlreadyExecuted) {
//            isAlreadyExecuted = true
//            auth()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        webView?.onPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        webView?.let {
//            it.stopLoading()
//            wholeView?.removeView(it)
//            it.clearCache(false)
//            it.removeAllViews()
//            it.destroy()
//        }
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        wholeViewHeight = 0
//    }
//
//    private val alreadyExecutedBundleKey = "alreadyExecutedBundleKey"
//    private val oauthUrlBundleKey = "oauthUrlBundleKey"
//    private val visibleBannerBundleKey = "visibleBannerBundleKey"
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putBoolean(alreadyExecutedBundleKey, isAlreadyExecuted)
//
//        if (webView != null) {
//            webView?.saveState(outState)
//        }
//
//        outState.putString(oauthUrlBundleKey, url)
//
//        if (isVisibleDownloadBanner && downloadBanner != null && downloadBanner!!.visibility == View.VISIBLE) {
//            outState.putBoolean(visibleBannerBundleKey, true)
//        } else {
//            outState.putBoolean(visibleBannerBundleKey, false)
//        }
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        restoreActivity(savedInstanceState)
//    }
//
//    private fun restoreActivity(bundle: Bundle?) {
//        if (bundle == null) {
//            return
//        }
//
//        isAlreadyExecuted = bundle.getBoolean(alreadyExecutedBundleKey)
//
//        if (webView != null) {
//            webView?.restoreState(bundle)
//        }
//        isVisibleDownloadBanner = bundle.getBoolean(visibleBannerBundleKey)
//        url = bundle.getString(oauthUrlBundleKey)
//    }
//
//    private fun auth() {
//        NidLog.d(TAG, "called auth()")
//        if (intent == null) {
//            return
//        }
//
//        // 동의 페이지의 내용을 이미 httpClient로 받아온 경우 그걸 그대로 보여준다
//        val agreeUrl = intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_AGREE_FROM_URL)
//        if (!agreeUrl.isNullOrEmpty()) {
//            url = agreeUrl
//        }
//        val webViewContent = intent.getStringExtra(NidOAuthIntent.OAUTH_REQUEST_AGREE_FROM_CONTENT)
//        if (webViewContent.isNullOrEmpty()) {
//            NidLog.d(TAG, "webview url : $url")
//            webView?.loadUrl(url!!)
//        } else {
//            NidLog.d(TAG, "webview url : $url")
//            NidLog.d(TAG, "webview context : $webViewContent")
//            webView?.loadDataWithBaseURL(url!!, webViewContent, "text/html", null, null)
//        }
//    }
//
//    private fun isValidNidUrl(url: String): Boolean {
//        var isValid = false
//        try {
//            val u = URL(url)
//
//            //Protocol
//            val protocol = u.protocol
//            if (protocol.equals("https", ignoreCase = true)) {
//                //Host
//                val host = u.host
//                if (host.equals("nid.naver.com", ignoreCase = true) || host.endsWith("nid.naver.com")) {
//                    isValid = true
//                }
//            }
//        } catch (e: MalformedURLException) {
//            isValid = false
//        }
//        return isValid
//    }
//
//    override fun setRequestedOrientation(requestedOrientation: Int) {
//        if (Build.VERSION.SDK_INT != AndroidVer.API_26_OREO) {
//            super.setRequestedOrientation(requestedOrientation)
//        }
//    }

}