package com.navercorp.nid.profile.domain.vo

import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode

data class NidProfile(
    val profile: NidProfileDetail,
    val error: NidOAuthErrorCode,
    val errorDescription: String,
) {
    val isValid: Boolean
        get() = profile.id.isNotEmpty()
}