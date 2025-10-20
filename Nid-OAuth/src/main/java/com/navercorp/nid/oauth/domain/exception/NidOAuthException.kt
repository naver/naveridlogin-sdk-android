package com.navercorp.nid.oauth.domain.exception

/**
 * NidOAuth 공통 Exception 클래스
 * @param message Exception 메시지
 */
class NidOAuthException(
    override val message: String?
): Exception()