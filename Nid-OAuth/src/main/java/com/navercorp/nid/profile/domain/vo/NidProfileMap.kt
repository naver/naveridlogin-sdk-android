package com.navercorp.nid.profile.domain.vo

import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode

data class NidProfileMap(
    val profile: Map<String, Any>,
    val error: NidOAuthErrorCode,
    val errorDescription: String,
) {
    val isValid : Boolean
        get() = !(profile["id"] as? String).isNullOrEmpty()
}