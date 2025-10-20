package com.navercorp.nid.oauth.domain.vo

import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode

data class Token(
    val accessToken: String,
    val refreshToken: String,
    val error: NidOAuthErrorCode,
    val errorDescription: String,
) {
    val isTokenValid: Boolean
        get() = accessToken.isNotEmpty() && refreshToken.isNotEmpty()

    val isOAuthSuccess: Boolean
        get() = isTokenValid && error.code.isEmpty()

    val isOAuthInterrupted: Boolean
        get() = !isTokenValid && error.code.isEmpty()
}