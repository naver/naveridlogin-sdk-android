package com.navercorp.nid.oauth.api

import com.navercorp.nid.util.UserAgentFactory
import okhttp3.Interceptor
import okhttp3.Response

class UserAgentInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val origin = chain.request()
        val request = origin.newBuilder()
            .header("User-Agent", UserAgentFactory.create())
            .build()

        return chain.proceed(request)
    }
}