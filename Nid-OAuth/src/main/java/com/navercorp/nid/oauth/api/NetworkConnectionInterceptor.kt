package com.navercorp.nid.oauth.api

import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.exception.NoConnectivityException
import com.navercorp.nid.util.NidNetworkUtil
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor: Interceptor {

    @Throws(NoConnectivityException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (NidNetworkUtil.isNotAvailable()) {
            throw NoConnectivityException()
        }

        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }
}