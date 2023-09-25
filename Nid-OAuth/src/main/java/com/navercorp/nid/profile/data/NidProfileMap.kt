package com.navercorp.nid.profile.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NidProfileMap(
    @SerializedName("resultcode")
    val resultCode: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val profile: Map<String, Any>?
)