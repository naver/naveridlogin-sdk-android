package com.navercorp.nid.oauth.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.oauth.NidOAuthErrorCode
import com.navercorp.nid.oauth.NidOAuthIntent
import com.navercorp.nid.oauth.NidOAuthQuery
import kotlinx.coroutines.*
import java.net.URLDecoder

class NidOAuthCustomTabActivity : AppCompatActivity() {

    companion object {
        const val TAG = "NidOAuthCustomTabActivity"

        const val SAVE_CUSTOM_TAB_OPEN = "isCustomTabOpen"
        const val ACTION_NAVER_CUSTOM_TAB = "ACTION_NAVER_3RDPARTY_CUSTOM_TAB"
    }

    private var isCustomTabOpen = false
    private var isCalledNewIntent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NidLog.d(TAG, "called onCreate()")
    }

    override fun onResume() {
        super.onResume()

        if (isCustomTabOpen) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                if (!isCalledNewIntent) {
                    responseError(null, NidOAuthErrorCode.CLIENT_USER_CANCEL.code, NidOAuthErrorCode.CLIENT_USER_CANCEL.description)
                }
            }
            return
        }
        openCustomTab()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVE_CUSTOM_TAB_OPEN, isCustomTabOpen)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isCustomTabOpen = savedInstanceState.getBoolean(SAVE_CUSTOM_TAB_OPEN, false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        isCalledNewIntent = true

        if (intent == null) {
            return
        }
        val code = intent.getStringExtra("code")
        val state = intent.getStringExtra("state")
        val error = intent.getStringExtra("error")
        val errorDescription = getDecodedString(intent.getStringExtra("error_description"));

        if (!code.isNullOrEmpty() || !error.isNullOrEmpty()) {
            responseResult(state, code, error, errorDescription)
        } else {
            responseError(state, error, errorDescription)
        }
    }

    private fun openCustomTab() {
        isCustomTabOpen = true

        val oauthUrl = NidOAuthQuery.Builder(this)
            .setMethod(NidOAuthQuery.Method.CUSTOM_TABS)
            .setAuthType(intent.getStringExtra("auth_type"))
            .build()

        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()

        customTabsIntent.launchUrl(this@NidOAuthCustomTabActivity, Uri.parse(oauthUrl))
    }

    private fun responseResult(state: String?, code: String?, error: String?, errorDescription: String?) {
        val intent = Intent().apply {
            putExtra(NidOAuthIntent.OAUTH_RESULT_STATE, state)
            putExtra(NidOAuthIntent.OAUTH_RESULT_CODE, code)
            putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_CODE, error)
            putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_DESCRIPTION, errorDescription)
        }
        returnResult(intent)
    }


    private fun responseError(state: String?, error: String?, errorDescription: String?) {
        val intent =  Intent().apply {
            putExtra(NidOAuthIntent.OAUTH_RESULT_STATE, state)
            putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_CODE, error)
            putExtra(NidOAuthIntent.OAUTH_RESULT_ERROR_DESCRIPTION, errorDescription)
        }
        returnResult(intent)
    }

    private fun returnResult(data: Intent) {
        data.action = ACTION_NAVER_CUSTOM_TAB

        val instance = LocalBroadcastManager.getInstance(this)
        instance.sendBroadcast(data)
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun getDecodedString(str: String?): String? {
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