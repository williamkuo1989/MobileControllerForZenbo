package com.asus.zenboControl.SearchIP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Ryan_Chou on 2018/2/5.
 */

public class WifiStatusManager {
    private static String TAG = WifiStatusManager.class.getSimpleName();

    private static boolean isWifiConnect = false;
    private static String wifiSSID;
    private static String wifiIP;
    private static String BroadcastIP;

    public static void setWifiStatic(WifiInfo info){
        if(info != null) {
            isWifiConnect = true;
            wifiSSID = info.getSSID();
            wifiSSID = wifiSSID.replace("\"","");
            int ipAddress = info.getIpAddress();
            wifiIP = String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
            BroadcastIP = String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (0xff));
        } else {
            setWifiStaticDisConnect();
        }
    }

    public static void checkActivityOnCreateWifiStatic(Context mContext){
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            setWifiStaticDisConnect();
            return;
        }

        if (networkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifi = wifiManager.getConnectionInfo();
            setWifiStatic(wifi);
        } else {
            setWifiStaticDisConnect();
        }
    }


    public static void setWifiStaticDisConnect(){
        isWifiConnect = false;
        wifiSSID = "";
        wifiIP = "";
    }

    public static boolean isWifiConnect(){
        return isWifiConnect;
    }

    public static String getWifiSSID(){
        return wifiSSID;
    }

    public static String getWifiIP(){
        return wifiIP;
    }

    public static String getBroadcastIP() {
        return BroadcastIP;
    }
}
