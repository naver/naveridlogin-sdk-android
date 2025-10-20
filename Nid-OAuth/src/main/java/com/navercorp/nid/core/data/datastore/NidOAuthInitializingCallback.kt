package com.navercorp.nid.core.data.datastore

/**
 * NidOAuth 초기화 완료(성공 혹은 실패) 후 호출되는 Callback 인터페이스
 *
 * onSuccess() : NidOAuth 초기화가 성공적으로 완료된 경우 호출
 * onFailure(e: Exception) : NidOAuth 초기화가 실패한 경우 호출
 */
interface NidOAuthInitializingCallback {
    fun onSuccess()
    fun onFailure(e: Exception)
}