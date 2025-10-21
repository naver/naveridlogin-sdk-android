package com.navercorp.nid.core.data.interceptor

import com.navercorp.nid.core.exception.NoConnectivityException
import com.navercorp.nid.core.util.NidNetworkUtil
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