package com.navercorp.nid.core.exception

import java.io.IOException

/**
 * 네트워크 연결이 없을 때 발생하는 Exception 클래스
 */
class NoConnectivityException: IOException() {
    override val message: String
        get() = "No Internet Connection"
}