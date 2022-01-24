package com.navercorp.nid.oauth

/**
 *
 * Created on 2021.10.19
 * Updated on 2021.10.19
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네아로SDK의 현재 상태를 정의한 클래스
 */
enum class NidOAuthLoginState {
    NEED_INIT, // 초기화가 필요한 상태
    NEED_LOGIN, // 로그인이 필요한 상태 (access token, refresh token 이 없음)
    NEED_REFRESH_TOKEN, // 토큰 refresh 가 필요한 상태 (access token 은 없고, refresh token 은 있음)
    OK // 정상
}