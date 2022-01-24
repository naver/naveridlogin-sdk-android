package com.navercorp.nid.oauth.data

import com.google.gson.annotations.SerializedName

data class NidOAuthResponse (
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("token_type")
    val tokenType: String?,
    @SerializedName("expires_in")
    private val expiresInString: String?,
    @SerializedName("result")
    val result: String?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("error_description")
    val errorDescription: String?
) {
    val expiresIn: Long
    get() = expiresInString?.toLong() ?: 3600L
}