package com.navercorp.nid.oauth.domain.enum

/**
 * 네아로SDK의 현재 상태를 정의한 클래스
 */
enum class NidOAuthLoginState {
    NEED_INIT, // 초기화가 필요한 상태
    OAUTH_DATA_INITIALIZING, // NidOAuth 필수 데이터 초기화 진행 중
    NEED_LOGIN, // 로그인이 필요한 상태 (access token, refresh token 이 없음)
    NEED_REFRESH_TOKEN, // 토큰 refresh 가 필요한 상태 (access token 은 없고, refresh token 은 있음)
    OK // 정상
}