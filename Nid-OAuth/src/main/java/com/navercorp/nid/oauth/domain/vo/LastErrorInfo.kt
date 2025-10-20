package com.navercorp.nid.oauth.domain.vo

import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode

data class LastErrorInfo(
    val lastErrorCode: NidOAuthErrorCode,
    val lastErrorDescription: String?,
)