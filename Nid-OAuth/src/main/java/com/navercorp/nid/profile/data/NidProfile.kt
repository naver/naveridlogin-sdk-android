package com.navercorp.nid.profile.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NidProfile(
    @SerializedName("id")
    val id: String?,
    @SerializedName("nickname")
    val nickname: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("age")
    val age: String?,
    @SerializedName("birthday")
    val birthday: String?,
    @SerializedName("profile_image")
    val profileImage: String?,
    @SerializedName("birthyear")
    val birthYear: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("ci")
    val ci: String?

)
