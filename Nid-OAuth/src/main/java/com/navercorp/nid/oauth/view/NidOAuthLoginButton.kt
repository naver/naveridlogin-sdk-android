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
        lateinit var launcher: ActivityResultLauncher<Intent>
        lateinit var oauthLoginCallback: OAuthLoginCallback
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
            NaverIdLoginSDK.authenticate(context, launcher, oauthLoginCallback)
        }
    }

    fun setOAuthLogin(launcher: ActivityResultLauncher<Intent>, oauthLoginCallback: OAuthLoginCallback) {
        Companion.launcher = launcher
        Companion.oauthLoginCallback = oauthLoginCallback
    }

}

