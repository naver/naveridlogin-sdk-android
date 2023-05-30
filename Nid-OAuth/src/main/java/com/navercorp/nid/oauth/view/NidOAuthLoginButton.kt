package com.navercorp.nid.oauth.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatImageView
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.nhn.android.oauth.R

class NidOAuthLoginButton: AppCompatImageView {

    companion object {
        const val TAG = "NidOAuthLoginButton"
        var launcher: ActivityResultLauncher<Intent>? = null
        var oauthLoginCallback: OAuthLoginCallback? = null
    }

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {
        init()
    }

    fun init() {
        setImageDrawable(context.getDrawable(R.drawable.login_btn_img))
        setOnClickListener {
            launcher?.let { launcher ->
                NaverIdLoginSDK.authenticate(context, launcher)
                return@setOnClickListener
            }

            oauthLoginCallback?.let { callback ->
                NaverIdLoginSDK.authenticate(context, callback)
                return@setOnClickListener
            }
        }
    }

    fun setOAuthLogin(launcher: ActivityResultLauncher<Intent>) {
        Companion.launcher = launcher
    }

    fun setOAuthLogin(oauthLoginCallback: OAuthLoginCallback) {
        Companion.oauthLoginCallback = oauthLoginCallback
    }

}

