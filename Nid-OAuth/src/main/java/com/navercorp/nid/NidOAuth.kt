package com.navercorp.nid

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.navercorp.nid.core.data.datastore.NidDataMigrationManager
import com.navercorp.nid.core.data.datastore.NidOAuthInitializingCallback
import com.navercorp.nid.core.data.datastore.NidOAuthLocalDataSource
import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.core.log.NidLog
import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.oauth.activity.NidOAuthBridgeActivity
import com.navercorp.nid.oauth.domain.enum.LoginBehavior
import com.navercorp.nid.oauth.domain.enum.NidOAuthLoginState
import com.navercorp.nid.oauth.domain.exception.NidOAuthException
import com.navercorp.nid.oauth.domain.usecase.Disconnect
import com.navercorp.nid.oauth.domain.usecase.GetClientInfo
import com.navercorp.nid.oauth.domain.usecase.GetOAuthInfo
import com.navercorp.nid.oauth.domain.usecase.Logout
import com.navercorp.nid.oauth.domain.usecase.SetUpOAuthInfo
import com.navercorp.nid.oauth.util.NidOAuthCallback
import com.navercorp.nid.profile.domain.usecase.FetchUserProfile
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.domain.vo.NidProfileMap
import com.navercorp.nid.profile.util.NidProfileCallback
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 네아로SDK를 초기화하고, 사용하기 위한 매니저 클래스
 */
object NidOAuth {
    private const val TAG = "NidOAuth"
    private val oauthRepository by lazy {
        NidServiceLocator.provideOAuthRepository()
    }
    private val userProfileRepository by lazy {
        NidServiceLocator.provideUserProfileRepository()
    }
    private val logout by lazy {
        Logout(oauthRepository)
    }
    private val disconnect by lazy {
        Disconnect(oauthRepository)
    }
    private val fetchUserProfile by lazy {
        FetchUserProfile(userProfileRepository)
    }
    private val getClientInfo by lazy {
        GetClientInfo(oauthRepository)
    }
    private val getOAuthInfo by lazy {
        GetOAuthInfo(oauthRepository)
    }
    private val setUpOauthInfo by lazy {
        SetUpOAuthInfo(oauthRepository)
    }

    /**
     * 에러 핸들러가 포함된 IO 스코프
     */
    fun createErrorHandlingIoScope(
        errorHandle: ((Throwable) -> Unit)? = null,
    ) = CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        NidLog.e(TAG, "CoroutineExceptionHandler got $throwable")
        CoroutineScope(Dispatchers.Main).launch {
            errorHandle?.invoke(throwable)
        }
    })

    /**
     * 네이버앱에 대한 market link 팝업의 노출 여부 결정
     */
    var isShowMarketLink: Boolean = true

    /**
     * InApp Browser에서 하단 탭의 노출 여부 결정
     */
    var isShowBottomTab: Boolean = true

    /**
     * Custom Tabs를 활용한 OAuth에서 재인증 수행 여부 결정
     */
    var isRequiredCustomTabsReAuth: Boolean = false

    /**
     * 네이버앱 호출 시 추가할 FLAG
     */
    var naverappIntentFlag: Int = -1

    /**
     * 로그인 후 실행될 callback
     */
    var oauthLoginCallback: NidOAuthCallback? = null

    /**
     * Application Context
     */
    private lateinit var applicationContext: Context

    private val dataInitializingMutex = Mutex()

    /**
     * 데이터 초기화 진행 중인지 여부 반환
     *
     * @return 데이터 초기화 진행 중인지 여부
     */
    private val _isDataInitializing = AtomicBoolean(false)
    val isDataInitializing: Boolean
        get() = _isDataInitializing.get()

    /**
     * OAuth 인증시 필요한 값들을 preference에 저장
     * 2015년 8월 이후에 등록하여 package name을 넣은 경우 사용
     *
     * @param context shared Preference를 얻어올 때 사용할 context
     * @param clientId OAuth client id 값
     * @param clientSecret OAuth client secret 값
     * @param clientName OAuth client name 값 (네이버앱을 통한 로그인시 보여짐)
     * @param callback 초기화 완료 및 실패 콜백
     */
    fun initialize(
        context: Context,
        clientId: String,
        clientSecret: String,
        clientName: String,
        callback: NidOAuthInitializingCallback? = null,
    ) {
        val appContext = context.applicationContext

        // 1. context 저장
        applicationContext = appContext

        // 2. 데이터 마이그레이션 및 초기화
        initNidOAuthData(
            context = appContext,
            clientId = clientId,
            clientSecret = clientSecret,
            clientName = clientName,
            initCallback = callback,
        )

        // 3. Log Prefix 초기화
        NidLog.setPrefix("NaverIdLogin|${context.packageName}|")
    }

    private fun initNidOAuthData(
        context: Context,
        clientId: String,
        clientSecret: String,
        clientName: String,
        initCallback: NidOAuthInitializingCallback?,
    ) = createErrorHandlingIoScope(
        errorHandle = { throwable ->
            _isDataInitializing.set(false)
            initCallback?.onFailure(throwable as Exception)
        }
    ).launch {
        // 데이터 초기화 진행 세팅
        _isDataInitializing.set(true)

        // 이미 초기화된 경우, 재초기화하지 않음
        dataInitializingMutex.withLock {
            // 1. 데이터 마이그레이션 수행
            // SharedPreferences/EncryptedPreferences -> DataStore
            // 마이그레이션 실패해도 내부적으로 에러 처리, 전파 x
            NidDataMigrationManager.migrateDataFromLegacyStores()

            // 2. 데이터 초기화
            // 마이그레이션 실패 여부와 상관없이 초기 데이터가 새롭게 저장
            // 마이그레이션 성공할 경우 기존 데이터 유지 / 실패할 경우 다시 OAuth 인증 필요
            setUpOauthInfo.initData(
                clientId = clientId,
                clientSecret = clientSecret,
                clientName = clientName,
                callbackUrl = context.packageName,
                lastErrorCode = NidOAuthErrorCode.NONE.code,
                lastErrorDesc = "",
            )
        }

        // 데이터 초기화 완료 세팅
        _isDataInitializing.set(false)
        withContext(Dispatchers.Main) {
            initCallback?.onSuccess()
            registerProcessLifecycleOwner()
        }
    }

    /**
     * 앱의 포그라운드/백그라운드 상태에 따라 DataStore의 StateFlow 공유 시작/중지
     */
    private fun registerProcessLifecycleOwner() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                // 앱이 포그라운드로 진입
                override fun onStart(owner: LifecycleOwner) {
                    NidOAuthLocalDataSource.startSharing()
                }

                // 앱이 백그라운드로 진입
                override fun onStop(owner: LifecycleOwner) {
                    NidOAuthLocalDataSource.stopSharing()
                }
            },
        )
    }

    /**
     * SDK 초기화 여부 확인
     *
     * @return SDK 초기화 여부
     */
    fun isInitialized(): Boolean = ::applicationContext.isInitialized

    /**
     * Application Context 반환
     *
     * @return Application Context
     * @throws NidOAuthException SDK가 초기화되지 않은 경우
     */
    fun getApplicationContext(): Context {
        if (isInitialized()) {
            return applicationContext
        } else {
            throw NidOAuthException(
                message = "Need to call NidOAuth.initialize(context, clientId, clientSecret, clientName) first."
            )
        }
    }

    /**
     * 네아로SDK의 로그 출력 여부 설정
     *
     * @param enabled 로그 출력 여부
     */
    fun setLogEnabled(
        enabled: Boolean
    ) {
        NidLog.setLogEnabled(enabled)
    }

    /**
     * 네아로SDK의 버전 반환
     *
     * @return SDK 버전 문자열
     */
    fun getVersion(): String = NidOAuthConstants.SDK_VERSION

    /**
     * OAuth 2.0 로그인 수행
     * RefreshToken이 존재하는 경우, 이미 연동이 된 것이므로 AccessToken을 갱신
     *
     * @param context authenticate 메서드를 호출한 Activity의 Context
     * @param launcher OAuth 인증을 실행할 ActivityResultLauncher
     */
    fun requestLogin(
        context: Context,
        launcher: ActivityResultLauncher<Intent>
    ) {
        if (!isReadyForOAuth()) return

        oauthLoginCallback = null

        val intent = NidOAuthBridgeActivity.getIntent(context)
        launcher.launch(intent)
    }

    /**
     * OAuth 2.0 로그인 수행
     * RefreshToken이 존재하는 경우, 이미 연동이 된 것이므로 AccessToken을 갱신
     *
     * @param context authenticate 메서드를 호출한 Activity의 Context
     * @param callback 결과값을 받을 콜백
     */
    fun requestLogin(
        context: Context,
        callback: NidOAuthCallback
    ) {
        if (!isReadyForOAuth()) return

        oauthLoginCallback = callback

        val intent = NidOAuthBridgeActivity.getIntent(context)
        context.startActivity(intent)
    }

    /**
     * 재동의 요청
     *
     * @param context authenticate 메서드를 호출한 Activity의 Context
     * @param launcher OAuth 인증을 실행할 ActivityResultLauncher
     */
    fun repromptPermissions(
        context: Context,
        launcher: ActivityResultLauncher<Intent>
    ) {
        if (!isReadyForOAuth()) return

        oauthLoginCallback = null

        val repromptAuthTYpe = "reprompt"
        val intent = NidOAuthBridgeActivity.getIntent(context, repromptAuthTYpe)
        launcher.launch(intent)
    }

    /**
     * 재동의 요청
     *
     * @param context authenticate 메서드를 호출한 Activity의 Context
     * @param launcher OAuth 인증을 실행할 ActivityResultLauncher
     */
    fun repromptPermissions(
        context: Context,
        callback: NidOAuthCallback
    ) {
        if (!isReadyForOAuth()) return

        oauthLoginCallback = callback

        val repromptOAuthType = "reprompt"
        val intent = NidOAuthBridgeActivity.getIntent(context, repromptOAuthType)
        context.startActivity(intent)
    }

    /**
     * 클라이언트에 저장되어 있는 Access token 및 Refresh token 삭제
     *
     * @param callback 로그아웃 결과를 받을 콜백
     */
    fun logout(
        callback: NidOAuthCallback,
    ) = createErrorHandlingIoScope { throwable ->
        val executionError = NidOAuthErrorCode.SDK_EXECUTION_ERROR
        callback.onFailure(executionError.code, throwable.message ?: executionError.description)
    }.launch {
        logout.invoke()
        withContext(Dispatchers.Main) {
            callback.onSuccess()
        }
    }

    /**
     * 지난 로그인 시도가 실패한 경우 Error code 반환
     *
     * @return 마지막 에러 코드
     */
    fun getLastErrorCode(): NidOAuthErrorCode = getOAuthInfo.getLastErrorCode()

    /**
     * 지난 로그인 시도가 실패한 경우 Error description 반환
     *
     * @return 마지막 에러 설명 문자열
     */
    fun getLastErrorDescription(): String? = getOAuthInfo.getLastErrorDesc()

    /**
     * 특정 로그인 모드 저장
     */
    var behavior: LoginBehavior = LoginBehavior.DEFAULT

    /**
     * OAuth Login 이후 획득한 AccessToken 반환
     *
     * @return Access Token 문자열
     */
    fun getAccessToken(): String? = getOAuthInfo.getAccessToken()

    /**
     * OAuth Login 이후 획득한 RefreshToken 반환
     *
     * @return Refresh Token 문자열
     */
    fun getRefreshToken(): String? = getOAuthInfo.getRefreshToken()

    /**
     * AccessToken의 만료 시간 반환
     *
     * @return 토큰 만료 시간
     */
    fun getExpiresAt(): Long = getOAuthInfo.getAccessTokenExpiresAt()

    /**
     * OAuth Login 이후 얻어온 token의 타입 반환
     *
     * @return 토큰 타입 문자열
     */
    fun getTokenType(): String? = getOAuthInfo.getTokenType()

    /**
     * 네아로SDK의 현재 상태 반환
     *
     * @return 현재 OAuth 로그인 상태
     */
    fun getState(): NidOAuthLoginState {
        // 1. SDK 초기화 여부 확인
        if (isInitialized().not()) {
            return NidOAuthLoginState.NEED_INIT
        }

        // 2. 데이터 초기화 진행 중인지 확인
        if (isDataInitializing) {
            return NidOAuthLoginState.OAUTH_DATA_INITIALIZING
        }

        // 3. OAuth 필수 데이터 초기화 여부 확인
        if (!isOAuthClientIdInitialized()) {
            return NidOAuthLoginState.NEED_INIT
        }

        // 4. AccessToken 및 RefreshToken 존재 여부 확인
        val accessToken = getAccessToken()
        val refreshToken = getRefreshToken()

        if (accessToken.isNullOrEmpty()) {
            return if (refreshToken.isNullOrEmpty()) {
                NidOAuthLoginState.NEED_LOGIN
            } else {
                NidOAuthLoginState.NEED_REFRESH_TOKEN
            }
        }
        return NidOAuthLoginState.OK
    }

    private fun isOAuthClientIdInitialized(): Boolean = try {
        val isClientIdValid = !(getClientInfo.getClientId().isNullOrEmpty())
        val isClientSecretValid = !(getClientInfo.getClientSecret().isNullOrEmpty())
        val isClientNameValid = !(getClientInfo.getClientId().isNullOrEmpty())
        val isCallbackUrlValid = !(getClientInfo.getCallbackUrl().isNullOrEmpty())

        isClientNameValid && isClientIdValid && isClientSecretValid && isCallbackUrlValid
    } catch (e: Exception) {
        NidLog.e(TAG, "isOAuthClientIdInitialized() Exception: ${e.message}")
        false
    }

    /**
     * OAuth 요청을 수행할 수 있는 상태인지 반환
     * OAuth 요청을 수행할 수 없다면 로그 출력
     *
     * @return OAuth 요청 가능 여부
     */
    private fun isReadyForOAuth(): Boolean {
        val sdkState = getState()
        if (isOAuthReady(sdkState)) {
            return true
        }

        when (sdkState) {
            NidOAuthLoginState.NEED_INIT -> {
                NidLog.e(TAG, "Need to call NidOAuth.initialize(context, clientId, clientSecret, clientName, migrationCallback) first.")
            }
            NidOAuthLoginState.OAUTH_DATA_INITIALIZING -> {
                NidLog.e(TAG, "SDK is initializing. Please wait a moment.")
            }
            else -> { /* no-op */ }
        }

        return false
    }

    /**
     * OAuth 요청을 수행할 수 있는 상태인지 반환
     *
     * @param sdkState 현재 SDK 상태
     * @return OAuth 요청 가능 여부
     */
    private fun isOAuthReady(sdkState: NidOAuthLoginState): Boolean {
        return when (sdkState) {
            NidOAuthLoginState.NEED_INIT,
            NidOAuthLoginState.OAUTH_DATA_INITIALIZING-> false
            else -> true
        }
    }

    /**
     * 네이버 아이디와 애플리케이션의 연동 해제
     * 클라이언트에 저장된 토큰과 서버에 저장된 토큰이 모두 삭제
     *
     * @param callback 결과값을 받을 콜백
     */
    fun disconnect(
        callback: NidOAuthCallback
    ) = createErrorHandlingIoScope { throwable ->
        val executionError = NidOAuthErrorCode.SDK_EXECUTION_ERROR
        callback.onFailure(executionError.code, throwable.message ?: executionError.description)
    }.launch {
        val disconnectResult = disconnect()
        if (!disconnectResult.isDisconnectSuccess) {
            setUpOauthInfo.setUpLastErrorInfo(
                errorCode = disconnectResult.error.code,
                errorDesc = disconnectResult.errorDescription
            )
            withContext(Dispatchers.Main) {
                callback.onFailure(disconnectResult.error.code, disconnectResult.errorDescription)
            }
        } else {
            logout()
            withContext(Dispatchers.Main) {
                callback.onSuccess()
            }
        }
    }

    /**
     * 사용자의 프로필 정보 반환
     *
     * @param callback 결과값을 받을 콜백
     */
    fun getUserProfile(
        callback: NidProfileCallback<NidProfile>
    ) = createErrorHandlingIoScope { throwable ->
        val executionError = NidOAuthErrorCode.SDK_EXECUTION_ERROR
        callback.onFailure(executionError.code, throwable.message ?: executionError.description)
    }.launch {
        val accessToken = getOAuthInfo.getAccessToken().orEmpty()
        val profileResult = fetchUserProfile.getUserProfile(accessToken)
        withContext(Dispatchers.Main) {
            if (profileResult.isValid) {
                callback.onSuccess(profileResult)
            } else {
                callback.onFailure(profileResult.error.code, profileResult.errorDescription)
            }
        }
    }

    /**
     * 사용자의 프로필 정보를 map 형태로 반환
     *
     * @param callback 결과값을 받을 콜백
     */
    fun getUserProfileMap(
        callback: NidProfileCallback<NidProfileMap>
    ) = createErrorHandlingIoScope { throwable ->
        val executionError = NidOAuthErrorCode.SDK_EXECUTION_ERROR
        callback.onFailure(executionError.code, throwable.message ?: executionError.description)
    }.launch {
        val accessToken = getOAuthInfo.getAccessToken().orEmpty()
        val profileMapResult = fetchUserProfile.getUserProfileMap(accessToken)
        withContext(Dispatchers.Main) {
            if (profileMapResult.isValid) {
                callback.onSuccess(profileMapResult)
            } else {
                callback.onFailure(profileMapResult.error.code, profileMapResult.errorDescription)
            }
        }
    }
}