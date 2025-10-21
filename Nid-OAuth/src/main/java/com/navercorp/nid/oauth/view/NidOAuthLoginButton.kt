package com.navercorp.nid.oauth.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatImageView
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.util.NidOAuthCallback
import com.nhn.android.oauth.R

@Deprecated(
    message = "This will be removed from v6.1.0. Use NidLoginButton instead.",
    replaceWith = ReplaceWith("NidLoginButton"),
)
class NidOAuthLoginButton: AppCompatImageView {

    companion object {
        const val TAG = "NidOAuthLoginButton"
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
                NaverIdLoginSDK.authenticate(context, launcher)
                return@setOnClickListener
            }

            oauthLoginCallback?.let { callback ->
                NaverIdLoginSDK.authenticate(context, callback)
                return@setOnClickListener
            }
        }
    }

    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidLoginButton.setOAuthLogin(launcher) instead.",
        replaceWith = ReplaceWith("NidLoginButton.setOAuthLogin(launcher)"),
    )
    fun setOAuthLogin(launcher: ActivityResultLauncher<Intent>) {
        Companion.launcher = launcher
    }

    @Deprecated(
        message = "This method will be removed from v6.1.0. Use NidLoginButton.setOAuthLogin(oauthLoginCallback) instead.",
        replaceWith = ReplaceWith("NidLoginButton.setOAuthLogin(oauthLoginCallback)"),
    )
    fun setOAuthLogin(oauthLoginCallback: NidOAuthCallback) {
        Companion.oauthLoginCallback = oauthLoginCallback
    }

}
