package com.navercorp.nid.oauth.sample

import android.app.Application
import android.widget.Toast
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.log.NidLog

class OAuthApplication: Application() {

    companion object {
        const val TAG = "OAuthApplication"
    }

    override fun onCreate() {
        super.onCreate()
        NaverIdLoginSDK.init(this)
    }
}