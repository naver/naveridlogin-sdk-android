package com.navercorp.nid.oauth.util

interface NidOAuthCallback {
    fun onSuccess()
    fun onFailure(errorCode: String, errorDesc: String)
}