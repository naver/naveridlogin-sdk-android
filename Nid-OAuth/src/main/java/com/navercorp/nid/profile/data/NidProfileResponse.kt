package com.navercorp.nid.profile.data

import com.google.gson.annotations.SerializedName

data class NidProfileResponse(
    @SerializedName("resultcode")
    val resultCode: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("response")
    val profile: NidProfile?
)