package com.navercorp.nid.exception

class NaverIdLoginSDKNotInitializedException: Exception() {
    override val message: String
        get() = "SDK 초기화가 필요합니다."
}