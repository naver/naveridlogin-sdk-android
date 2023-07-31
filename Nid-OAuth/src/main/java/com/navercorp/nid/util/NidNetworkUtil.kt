package com.navercorp.nid.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.navercorp.nid.NaverIdLoginSDK

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

    fun getType() = if (isCellularConnected()) {
        "cell"
    } else if (isWifiConnected()) {
        "wifi"
    } else {
        "other"
    }

    fun isAvailable() = if (Build.VERSION.SDK_INT <= AndroidVer.API_24_NOUGAT){
        val connectivityManager = NidApplicationUtil.getConnectivityManager(NaverIdLoginSDK.getApplicationContext())
        val info = connectivityManager.activeNetworkInfo
        info?.isConnected == true
    } else {
        val connectivityManager = NidApplicationUtil.getConnectivityManager(NaverIdLoginSDK.getApplicationContext())
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null
    }

    fun isNotAvailable(): Boolean = !isAvailable()

    private fun isCellularConnected() = if (Build.VERSION.SDK_INT <= AndroidVer.API_24_NOUGAT) {
        isConnected(ConnectivityManager.TYPE_MOBILE)
    } else {
        val connectivityManager = NidApplicationUtil.getConnectivityManager(NaverIdLoginSDK.getApplicationContext())

        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
    }

    private fun isWifiConnected() = if (Build.VERSION.SDK_INT <= AndroidVer.API_24_NOUGAT) {
        isConnected(ConnectivityManager.TYPE_WIFI)
    } else {
        val connectivityManager = NidApplicationUtil.getConnectivityManager(NaverIdLoginSDK.getApplicationContext())

        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    private fun isConnected(connectType: Int): Boolean {
        val context = NaverIdLoginSDK.getApplicationContext()
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