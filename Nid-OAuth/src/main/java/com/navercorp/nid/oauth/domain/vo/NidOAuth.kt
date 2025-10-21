package com.navercorp.nid.oauth.domain.vo

import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode

data class NidOAuth(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    private val expiresInString: String,
    val result: String,
    val error: NidOAuthErrorCode,
    val errorDescription: String,
) {
    val expiresIn: Long
        get() = expiresInString.toLong()

    val isTokenValid: Boolean
        get() = accessToken.isNotEmpty() && refreshToken.isNotEmpty()

    val isOAuthSuccess: Boolean
        get() = isTokenValid && error.code.isEmpty()

    val isOAuthInterrupted: Boolean
        get() = !isTokenValid && error.code.isEmpty()

    companion object {
        fun emptyWithError(
            error: NidOAuthErrorCode,
            errorDescription: String
        ) = NidOAuth(
            accessToken = "",
            refreshToken = "",
            tokenType = "",
            expiresInString = "0",
            result = "",
            error = error,
            errorDescription = errorDescription
        )
    }
}