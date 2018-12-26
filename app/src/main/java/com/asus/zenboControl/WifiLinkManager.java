package com.asus.zenboControl;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.util.List;


public class WifiLinkManager {

    private static String TAG = "WifiLinkManager";


    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    ConnectRunnable connectRunnable;
    private Thread wifiThread;
    Handler mHandler;
    int checkTime = 3*1000;

    public static WifiLinkManager manager;

    public static WifiLinkManager getManager(Context mContext){
        if(manager == null){
            manager = new WifiLinkManager(mContext);
        }

        return manager;
    }

    public WifiLinkManager(Context mContext){
        mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        mHandler = new Handler();
    }

    public void startWifiThread(){
        if(wifiThread != null){
            stopWifiThread();
            wifiThread = null;
            connectRunnable = null;
        }

//        connectRunnable =  new ConnectRunnable("ZenboEU_5G", "asus#1234", 3);
//        wifiThread = new Thread(connectRunnable);
//        wifiThread.start();
    }

    public void stopWifiThread(){
        if(connectRunnable != null) {
            connectRunnable.stopConnectRunnable();
        }

        if(wifiThread != null) {
            wifiThread.interrupt();
        }

        connectRunnable = null;
        wifiThread = null;

    }

    private void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    private void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    private void addNetwork(WifiConfiguration wcg) {
        if(wcg != null) {
            int wcgID = mWifiManager.addNetwork(wcg);
            Log.d(TAG, "addNetwork wcgID:" + wcgID);
            boolean isEnableNetwork = mWifiManager.enableNetwork(wcgID, true);
            Log.d(TAG, "addNetwork enableNetwork:" + isEnableNetwork);
        }
    }


    private WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = SSID;

        WifiConfiguration tempConfig = IsExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if(Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }

    private class ConnectRunnable implements Runnable {
        private String ssid;
        private String password;
        private int type;
        private boolean stopThread;

        public ConnectRunnable(String ssid, String password, int type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
            stopThread = false;
        }

        public void stopConnectRunnable(){
            stopThread = true;
        }

        @Override
        public void run() {
            try {

                if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING &&
                        mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {

                    openWifi();
                    Thread.sleep(200);
                    while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                        try {
                            // 为了避免程序一直while循环，让它睡个100毫秒检测……
                            Thread.sleep(100);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                }

                WifiInfo info = mWifiManager.getConnectionInfo();
                String nowssid = info != null ? info.getSSID() : "null";

                if(nowssid == null || !nowssid.equals("\""+ssid+"\"")) {
                    addNetwork(CreateWifiInfo(ssid, password, type));
                }

                if(!stopThread) {
                    mHandler.postDelayed(this, checkTime);
                }

                } catch(Exception e){
                    // TODO: handle exception
                    e.printStackTrace();
                }
        }
    }

}
