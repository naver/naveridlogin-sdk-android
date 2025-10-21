package com.navercorp.nid.oauth.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatImageView
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.util.NidOAuthCallback
import com.nhn.android.oauth.R

class NidLoginButton: AppCompatImageView {

    companion object {
        const val TAG = "NidLoginButton"
        var launcher: ActivityResultLauncher<Intent>? = null
        var oauthLoginCallback: NidOAuthCallback? = null
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
                NidOAuth.requestLogin(context, launcher)
                return@setOnClickListener
            }

            oauthLoginCallback?.let { callback ->
                NidOAuth.requestLogin(context, callback)
                return@setOnClickListener
            }
        }
    }

    fun setOAuthLogin(launcher: ActivityResultLauncher<Intent>) {
        Companion.launcher = launcher
    }

    fun setOAuthLogin(oauthLoginCallback: NidOAuthCallback) {
        Companion.oauthLoginCallback = oauthLoginCallback
    }

}

