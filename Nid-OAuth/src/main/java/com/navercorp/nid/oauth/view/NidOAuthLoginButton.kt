package com.navercorp.nid.oauth.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.nhn.android.oauth.R

class NidOAuthLoginButton: AppCompatImageView {

    companion object {
        const val TAG = "NidOAuthLoginButton"
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
            NaverIdLoginSDK.authenticate(context, oauthLoginCallback)
        }
    }

    fun setOAuthLoginCallback(oauthLoginCallback: OAuthLoginCallback) {
        Companion.oauthLoginCallback = oauthLoginCallback
    }

}

