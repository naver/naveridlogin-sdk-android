package com.navercorp.nid.oauth.domain.vo

data class LoginInfo(
    val oauthCode: String?,
    val oauthState: String?,
    val errorCode: String?,
    val errorDesc: String?
)
