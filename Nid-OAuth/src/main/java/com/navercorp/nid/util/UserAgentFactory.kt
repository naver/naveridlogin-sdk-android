package com.navercorp.nid.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.log.NidLog

/**
 *
 * Created on 2021.09.16
 * Updated on 2021.09.16
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * 네아로SDK에서 사용할 User-Agent 값을 뽑는다.
 */
object UserAgentFactory {
    private val TAG = "UserAgentFactory"

    /**
     * UserAgent를 생성한다.
     */
    fun create(context: Context): String {
        // Android Version
        val versionInfo = "Android/${Build.VERSION.RELEASE}".refine()

        // Device Model Name
        val modelInfo = "Model/${Build.MODEL}".refine()

        // Application
        val appInfo = generateAppInfo(context)

        if (appInfo.isNullOrEmpty()) {
            return "$versionInfo $modelInfo"
        }

        // 네아로SDK
        val sdkInfo = "OAuthLoginMod/${NaverIdLoginSDK.getVersion()}".refine()
        return "$versionInfo $modelInfo $appInfo $sdkInfo"
    }

    private fun generateAppInfo(context: Context): String? {
        var appInfo: String? = null
        try {
            val packageManger = context.packageManager
            val packageInfo = packageManger.getPackageInfo(
                context.packageName,
                PackageManager.GET_GIDS or PackageManager.GET_SIGNATURES or PackageManager.GET_META_DATA
            )
            var appId: String = ""
            packageInfo.applicationInfo.loadDescription(packageManger)?.let {
                appId = ",appId:${it}"
            }
            appInfo = "${context.packageName}/${packageInfo.versionName}(${packageInfo.versionCode},uid:${packageInfo.applicationInfo.uid}${appId})".refine()
        } catch (e: PackageManager.NameNotFoundException) {
            appInfo = null
            NidLog.e(TAG, e)
        }
        return appInfo
    }
}

fun String.refine(): String = this.replace("\\s".toRegex(), "")