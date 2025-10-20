package com.navercorp.nid.core.base

import com.navercorp.nid.core.base.NidApiResult.Companion.handleException
import com.navercorp.nid.core.base.NidApiResult.Companion.handleHttpError
import com.navercorp.nid.core.base.NidApiResult.Success
import com.navercorp.nid.core.data.errorcode.NidOAuthErrorCode
import com.navercorp.nid.core.exception.NoConnectivityException
import com.navercorp.nid.core.log.NidLog
import retrofit2.Response
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLKeyException
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLProtocolException

/**
 * API 통신 결과를 나타내는 sealed interface
 *
 * - Success: API 통신 성공
 * - Failure: API 통신 실패 (잘못된 결과, HTTP Status Code 4XX, 5XX)
 * - Exception: API 통신 중 예외 발생 (네트워크 오류, 타임아웃 등)
 */

private const val TAG = "NidApiResult"
internal sealed interface NidApiResult<T> {
    class Success<T> (
        val data: T
    ): NidApiResult<T>

    class Failure<T> (
        val nidOAuthErrorCode: NidOAuthErrorCode,
        val nidOAuthErrorDes: String
    ): NidApiResult<T>

    class Exception<T> (
        val nidOAuthErrorCode: NidOAuthErrorCode,
        val nidOAuthErrorDes: String
    ): NidApiResult<T>

    companion object {
        /**
         * 예외 handling
         *
         * @param throwable 발생한 Exception
         */
        fun <T> handleException(
            throwable: Throwable
        ): NidApiResult<T> = when(throwable) {
            is NoConnectivityException, is SocketTimeoutException, is SocketException -> Exception(
                NidOAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR,
                NidOAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR.description
            )

            is SSLPeerUnverifiedException, is SSLProtocolException, is SSLKeyException, is SSLHandshakeException, is SSLException -> Exception(
                NidOAuthErrorCode.CLIENT_ERROR_CERTIFICATION_ERROR,
                NidOAuthErrorCode.CLIENT_ERROR_CERTIFICATION_ERROR.description
            )

            else -> Exception(
                NidOAuthErrorCode.ERROR_NO_CATAGORIZED,
                NidOAuthErrorCode.ERROR_NO_CATAGORIZED.description
            )
        }

        /**
         * 서버 통신 실패 handling
         *
         * @param statusCode HTTP Status Code
         * @param statusMessage HTTP Status Message
         */
        fun <T> handleHttpError(statusCode: Int, statusMessage: String): NidApiResult<T> = when(statusCode) {
            // 4xx 에러의 경우, Failure 처리
            in 400 until 500 -> Failure(
                NidOAuthErrorCode.ERROR_NO_CATAGORIZED,
                statusMessage
            )
            // 5xx 에러의 경우, Exception 처리
            500 -> Exception(
                NidOAuthErrorCode.SERVER_ERROR_SERVER_ERROR,
                NidOAuthErrorCode.SERVER_ERROR_SERVER_ERROR.description
            )

            503 -> Exception(
                NidOAuthErrorCode.SERVER_ERROR_TEMPORARILY_UNAVAILABLE,
                NidOAuthErrorCode.SERVER_ERROR_TEMPORARILY_UNAVAILABLE.description
            )
            else -> Exception(
                NidOAuthErrorCode.ERROR_NO_CATAGORIZED,
                statusMessage
            )
        }
    }
}

/**
 * Retrofit2 Response를 받아 NidApiResult로 변환
 *
 * @param execute Retrofit2 suspend function
 */
internal suspend fun <T> handleApiResult(
    execute: suspend() -> Response<T>,
): NidApiResult<T> {
    return try {
        val response = execute()
        val body = response.body()

        if (response.isSuccessful && body != null) {
            // status code 2XX, 3XX && body != null -> 성공 처리
            Success(body)
        } else {
            // status code 4XX, 5XX || body == null -> 실패 처리
            handleHttpError(response.code(), response.message())
        }
    } catch (e: Exception) {
        NidLog.e(TAG, "handleResult() - catch ${e.javaClass.simpleName}")
        handleException(e)
    }
}