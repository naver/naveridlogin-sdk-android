package com.navercorp.nid.oauth.data.repository

import com.navercorp.nid.core.data.datastore.DataStoreKey
import com.navercorp.nid.core.data.datastore.NidOAuthLocalDataSource
import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.core.log.NidLog
import com.navercorp.nid.oauth.data.datasource.NidOAuthRemoteDataSource
import com.navercorp.nid.oauth.domain.repository.OAuthRepository
import com.navercorp.nid.oauth.domain.vo.LastErrorInfo
import com.navercorp.nid.oauth.domain.vo.NidOAuth
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.math.BigInteger
import java.net.URLEncoder
import java.security.SecureRandom

/**
 * OAuth 관련 Repository
 * - 네아로 SDK Api 호출
 * - OAuth 관련 데이터 로컬 저장소 save & load
 *
 * @param nidOAuthLocalDataSource 로컬 저장소인 NidDataStore
 * @param nidOAuthRemoteDataSource OAuth 관련 Api 호출을 위한 dataSource
 */
internal class NidOAuthRepository(
    private val nidOAuthLocalDataSource: NidOAuthLocalDataSource,
    private val nidOAuthRemoteDataSource: NidOAuthRemoteDataSource,
): OAuthRepository {
    private val mutex = Mutex()

    /**
     * 네아로 API 호출
     */
    override suspend fun requestAccessToken(
        clientId: String,
        clientSecret: String,
        state: String,
        code: String,
        locale: String
    ): NidOAuth = nidOAuthRemoteDataSource.requestAccessToken(
        clientId = clientId,
        clientSecret = clientSecret,
        state = state,
        code = code,
        locale = locale,
    )

    override suspend fun requestRefreshToken(
        clientId: String,
        clientSecret: String,
        refreshToken: String,
        locale: String
    ): NidOAuth = nidOAuthRemoteDataSource.requestRefreshToken(
        clientId = clientId,
        clientSecret = clientSecret,
        refreshToken = refreshToken,
        locale = locale,
    )

    override suspend fun requestDeleteToken(
        clientId: String,
        clientSecret: String,
        accessToken: String,
        locale: String
    ): NidOAuth = nidOAuthRemoteDataSource.deleteToken(
        clientId = clientId,
        clientSecret = clientSecret,
        accessToken = accessToken,
        locale = locale,
    )

    /**
     * OAuth Result 저장
     */
    override suspend fun saveOAuthResult(oauthResult: NidOAuth) {
        saveAccessToken(oauthResult.accessToken)
        saveRefreshToken(oauthResult.refreshToken)
        saveTokenType(oauthResult.tokenType)
        saveLastErrorInfo(oauthResult.error.code, oauthResult.errorDescription)

        // 현재 시간 + expiresIn 으로 expiresAt 계산
        val expiresAt = System.currentTimeMillis() / 1000 + oauthResult.expiresIn
        saveAccessTokenExpiresAt(expiresAt)
    }

    /**
     * accessToken 저장 및 접근
     */
    override suspend fun saveAccessToken(token: String?) {
        nidOAuthLocalDataSource.save(DataStoreKey.ACCESS_TOKEN.key, token)
    }

    override fun getAccessToken(): String? {
        val token = nidOAuthLocalDataSource.load(DataStoreKey.ACCESS_TOKEN.key, null)
            ?: return null

        // expires time 검증 후 return 해줌
        val isTokenValid = System.currentTimeMillis() / 1000 - getAccessTokenExpiresAt() < 0
        return if (isTokenValid) {
            token
        } else {
            NidLog.i(TAG, "access token is expired.")
            null
        }
    }

    /**
     * refreshToken 저장 및 접근
     */
    override suspend fun saveRefreshToken(token: String?) {
        nidOAuthLocalDataSource.save(DataStoreKey.REFRESH_TOKEN.key, token)
    }

    override fun getRefreshToken(): String? {
        val token = nidOAuthLocalDataSource.load(DataStoreKey.REFRESH_TOKEN.key, null)
        return if (token.isNullOrEmpty()) null else token
    }

    /**
     * accessToken 만료 시간 저장 및 접근
     */
    override suspend fun saveAccessTokenExpiresAt(expiresAt: Long) {
        nidOAuthLocalDataSource.save(DataStoreKey.EXPIRES_AT.key, expiresAt)
    }
    override fun getAccessTokenExpiresAt(): Long =
        nidOAuthLocalDataSource.load(DataStoreKey.EXPIRES_AT.key, 0L)

    /**
     * token type 저장 및 접근
     */
    override suspend fun saveTokenType(tokenType: String?) {
        nidOAuthLocalDataSource.save(DataStoreKey.TOKEN_TYPE.key, tokenType)
    }

    override fun getTokenType(): String? =
        nidOAuthLocalDataSource.load(DataStoreKey.TOKEN_TYPE.key, null)

    /**
     * 마지막 에러 정보 저장 및 접근
     */
    override suspend fun saveLastErrorInfo(code: String?, desc: String?) {
        nidOAuthLocalDataSource.save(DataStoreKey.LAST_ERROR_CODE.key, code)
        nidOAuthLocalDataSource.save(DataStoreKey.LAST_ERROR_DESC.key, desc)
    }

    override fun getLastErrorInfo(): LastErrorInfo {
        val errorCodeStr = nidOAuthLocalDataSource.load(DataStoreKey.LAST_ERROR_CODE.key, "")
        val errorCode = NidOAuthErrorCode.INSTANCE.fromString(errorCodeStr)
        val desc = nidOAuthLocalDataSource.load(DataStoreKey.LAST_ERROR_DESC.key, null)
        return LastErrorInfo(errorCode, desc)
    }

    /**
     * clientId 저장 및 접근
     */
    override suspend fun saveClientId(clientId: String?) =
        nidOAuthLocalDataSource.save(DataStoreKey.CLIENT_ID.key, clientId)

    override fun getClientId(): String? =
        nidOAuthLocalDataSource.load(DataStoreKey.CLIENT_ID.key, null)

    /**
     * clientSecret 저장 및 접근
     */

    override suspend fun saveClientSecret(clientSecret: String?) =
        nidOAuthLocalDataSource.save(DataStoreKey.CLIENT_SECRET.key, clientSecret)

    override fun getClientSecret(): String? =
        nidOAuthLocalDataSource.load(DataStoreKey.CLIENT_SECRET.key, null)

    /**
     * clientName 저장 및 접근
     */

    override suspend fun saveClientName(clientName: String?) =
        nidOAuthLocalDataSource.save(DataStoreKey.CLIENT_NAME.key, clientName)

    override fun getClientName(): String? =
        nidOAuthLocalDataSource.load(DataStoreKey.CLIENT_NAME.key, null)

    /**
     * callbackUrl 저장 및 접근
     */

    override suspend fun saveCallbackUrl(callbackUrl: String?) =
        nidOAuthLocalDataSource.save(DataStoreKey.CALLBACK_URL.key, callbackUrl)

    override fun getCallbackUrl(): String? =
        nidOAuthLocalDataSource.load(DataStoreKey.CALLBACK_URL.key, null)

    /**
     * initState 저장 및 접근
     */
    override suspend fun saveInitState(state: String?) =
        nidOAuthLocalDataSource.save(DataStoreKey.OAUTH_INIT_STATE.key, state)

    override suspend fun getInitState(): String?  = mutex.withLock {
        var state = nidOAuthLocalDataSource.load(DataStoreKey.OAUTH_INIT_STATE.key, null)
        if (state != null) return state

        state = BigInteger(130, SecureRandom()).toString(32)
        try {
            state = URLEncoder.encode(state, "UTF-8")
        } catch (e: Exception) {
            NidLog.e(TAG, "Failed to encode init state: ${e.message}")
        }

        saveInitState(state)
        return state
    }

    /**
     * OAuth code 저장 및 접근
     */
    override suspend fun saveOAuthCode(code: String?) =
        nidOAuthLocalDataSource.save(DataStoreKey.OAUTH_CODE.key, code)

    override fun getOAuthCode(): String? =
        nidOAuthLocalDataSource.load(DataStoreKey.OAUTH_CODE.key, null)

    /**
     * OAuth state 저장 및 접근
     */
    override suspend fun saveOAuthState(state: String?) =
        nidOAuthLocalDataSource.save(DataStoreKey.OAUTH_CHECK_STATE.key, state)

    override fun getOAuthState(): String? =
        nidOAuthLocalDataSource.load(DataStoreKey.OAUTH_CHECK_STATE.key, null)

    companion object {
        private const val TAG = "NidOAuthRepository"
    }
}