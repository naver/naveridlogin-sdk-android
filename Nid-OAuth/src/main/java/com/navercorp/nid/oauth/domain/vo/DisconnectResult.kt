package com.navercorp.nid.oauth.domain.vo

import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode

data class DisconnectResult(
    val result: String,
    val error: NidOAuthErrorCode,
    val errorDescription: String,
) {
    val isDisconnectSuccess: Boolean
        get() = "success".equals(result, ignoreCase = true)
}
