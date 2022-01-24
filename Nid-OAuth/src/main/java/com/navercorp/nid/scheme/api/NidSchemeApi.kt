package com.navercorp.nid.scheme.api

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NidSchemeApi {

    fun requestSchemeLog(context: Context, log: String) {
        if (log.isNullOrEmpty()) {
            return
        }
        val bodies: MutableMap<String, Any> = LinkedHashMap()
        bodies["body"] = log

        val service = NidSchemeService.create()

        val schemeLog = service.requestSchemeLog(bodies)
        schemeLog.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                //TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                //TODO("Not yet implemented")
            }

        })
    }
}