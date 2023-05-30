package com.navercorp.nid.util.legacy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.navercorp.nid.log.NidLog;

public class NetworkState {
    private static final String TAG = "NetworkState";
    private static boolean shown = false;

    /**
     * Check network connect
     * @param context Application context
     * @return True if network connect or occurred error. otherwise false.
     *
     */
    public static boolean isDataConnected(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();

            return info != null && manager.getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            NidLog.e(TAG, e);
        }
        return true;
    }

    private static boolean isConnected(Context context, int connectType) {
        try {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {

                if (Build.VERSION.SDK_INT < 23) {
                    @SuppressWarnings("deprecation")
                    NetworkInfo ni = manager.getNetworkInfo(connectType);
                    if (ni.isConnected()) {
                        return (true);
                    }

                } else {
                    Network[] allNetwork = manager.getAllNetworks();

                    for (Network network : allNetwork) {
                        NetworkInfo info = manager.getNetworkInfo(network);
                        if (null != info) {
                            if (connectType == info.getType()
                                    && info.isConnected()) {
                                return true;
                            }
                        }
                    }

                }

            }
        } catch (Exception e) {
            NidLog.e(TAG, e);
        }
        return (false);
    }

    public static boolean is3GConnected(Context context) {
        return isConnected(context, ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isWifiConnected(Context context) {
        return isConnected(context, ConnectivityManager.TYPE_WIFI);
    }



    public static String getNetworkState(Context context) {
        String network = "other";

        if (NetworkState.is3GConnected(context)) {
            network = "cell";
        } else if (NetworkState.isWifiConnected(context)) {
            network = "wifi";
        }

        return network;
    }
}
