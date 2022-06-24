package com.navercorp.nid.profile

interface NidProfileCallback<T> {
    fun onSuccess(result: T)
    fun onFailure(httpStatus: Int, message: String)
    fun onError(errorCode: Int, message: String)
}