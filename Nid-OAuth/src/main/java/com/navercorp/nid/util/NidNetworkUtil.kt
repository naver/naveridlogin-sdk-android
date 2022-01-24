package com.navercorp.nid.util

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build

/**
 *
 * Created on 2021.10.22
 * Updated on 2021.10.22
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * Network 상태를 체크하기 위한 유틸
 */
object NidNetworkUtil {

    fun getType(context: Context): String {
        var networkType = "other"
        if (isConnected(context, ConnectivityManager.TYPE_MOBILE)) {
            networkType = "cell"
        } else if (isConnected(context, ConnectivityManager.TYPE_WIFI)) {
            networkType = "wifi"
        }
        return networkType
    }

    fun isAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var info = manager.activeNetworkInfo
        return info != null && info.isConnected
    }

    fun isNotAvailable(context: Context): Boolean = !isAvailable(context)

    private fun isConnected(context: Context, connectType: Int): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (manager != null) {
            if (Build.VERSION.SDK_INT < AndroidVer.API_23_MARSHMALLOW) {

                val networkInfo = manager.getNetworkInfo(connectType)
                if (networkInfo != null && networkInfo.isConnected) {
                    return true
                }
            } else {
                val networks = manager.allNetworks
                networks.forEach {
                    val info = manager.getNetworkInfo(it)
                    if (info != null && info.type == connectType && info.isConnected) {
                        return true
                    }
                }
            }
        }
        return false
    }
}