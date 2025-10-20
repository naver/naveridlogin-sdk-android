package com.navercorp.nid.profile.util

interface NidProfileCallback<T> {
    fun onSuccess(result: T)
    fun onFailure(errorCode: String, errorDesc: String)
}