package com.navercorp.nid.util.legacy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.navercorp.nid.log.NidLog;
import com.navercorp.nid.oauth.NidOAuthConstants;
import com.nhn.android.naverlogin.OAuthLogin;

import java.util.List;

public class ApplicationUtil {

    private static final String TAG = "ApplicationUtil";

    /**
     * User Agent를 획득한다.
     *
     * @param context
     * @return 네아로SDK를 탑재한 애플리케이션의 정보와 기기의 정보가 포함된 UA값
     */
    public static String getUserAgent(Context context) {
        StringBuilder userAgent = new StringBuilder();

        // Android Version Information
        String versionInfo = "Android/" + android.os.Build.VERSION.RELEASE;
        versionInfo.replaceAll("\\s", "");
        userAgent.append(versionInfo);

        // Android Device Model Name
        String modelInfo = "Model/" + android.os.Build.MODEL;
        modelInfo.replaceAll("\\s", "");
        userAgent.append(" " + modelInfo);

        // Application Information
        String appInfo = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_GIDS | PackageManager.GET_SIGNATURES | PackageManager.GET_META_DATA);

            String appId = "";
            if (packageInfo.applicationInfo.loadDescription(packageManager) != null) {
                appId = ",appId:" + packageInfo.applicationInfo.loadDescription(packageManager);
            }
            appInfo = String.format("%s/%s(%d,uid:%d%s)",
                    context.getPackageName(),
                    packageInfo.versionName,
                    packageInfo.versionCode,
                    packageInfo.applicationInfo.uid,
                    appId);
            appInfo.replaceAll("\\s", "");
        } catch (PackageManager.NameNotFoundException e) {
            appInfo = null;
            NidLog.e(TAG, e);
        }
        if (TextUtils.isEmpty(appInfo)) {
            return userAgent.toString();
        }
        userAgent.append(" " + appInfo);

        // Naver ID Login Module Version
        String loginModuleVersion = "OAuthLoginMod/" + OAuthLogin.getVersion();
        loginModuleVersion.replaceAll("\\s", "");
        userAgent.append(" " + loginModuleVersion);

        return userAgent.toString();
    }

    /**
     * 네이버앱이 설치되어 있는 지 검증한다.
     * @param context
     * @return 네이버앱이 있으면 true, 없으면 false
     */
    public static boolean isExistNaverApp(Context context) {
        return isExistApplication(context, NidOAuthConstants.PACKAGE_NAME_NAVERAPP);
    }

    /**
     * 특정 애플리케이션이 설치되어 있는 지 검증한다.
     * @param context context
     * @param packageName 패키지명 (예: "com.nhn.android.search")
     * @return if 네이버앱 있음, true. else, false
     */
    public static boolean isExistApplication(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        Intent intent = null;
        try {
            intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        } catch (Exception e) {
            return false;
        }

        if (intent == null) {
            return false;
        }

        return true;
    }

    /**
     * 현재 디바이스에 packageName에 IntentFilter가 설정 되어 있는 앱이 있는 지 검색
     *
     * @param context 앱 컨텍스트
     * @param packageName 검색할 패키지 명
     * @param intentName 검색할 필터 명
     * @return 존재 여부
     */
    public static boolean isExistIntentFilter(Context context, String packageName, String intentName) {
        if (TextUtils.isEmpty(intentName)) {
            return false;
        }

        Intent intent = new Intent(intentName);
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_META_DATA);

        if (resolveInfoList.isEmpty()) {
            return false;
        }

        for (ResolveInfo r : resolveInfoList) {
            String pkgName = r.activityInfo.packageName;
            if (pkgName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }


}
