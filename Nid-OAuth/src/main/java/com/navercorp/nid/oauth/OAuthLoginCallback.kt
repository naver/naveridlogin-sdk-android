package com.navercorp.nid.oauth

interface OAuthLoginCallback {
    fun onSuccess()
    fun onFailure(httpStatus: Int, message: String)
    fun onError(errorCode: Int, message: String)
}