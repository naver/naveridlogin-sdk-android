package com.navercorp.nid.profile.data.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NidProfileMapResponse(
    @SerializedName("resultcode")
    val resultCode: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val profile: Map<String, Any>?
)