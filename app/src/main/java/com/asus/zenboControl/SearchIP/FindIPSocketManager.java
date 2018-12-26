package com.asus.zenboControl.SearchIP;

import android.content.Context;
import android.util.Log;

/**
 * Created by Ryan_Chou on 2018/3/19.
 */

public class FindIPSocketManager {
    public static final String FindIPTAG = "FindIPSocketManager";
    public static final int port = 19999;
    public static final String askIPString = "ip";
    public static final String recallServerString = "server";
    boolean isServerOpen = false;
    boolean isClientOpen = false;
    ServerSocket serverThread;
    ClientSocket clientSocket;
    Context mContext;

    public FindIPSocketManager(Context context){
        isServerOpen = false;
        mContext = context;
        WifiStatusManager.checkActivityOnCreateWifiStatic(mContext);
    }

    public void startServer(){
        WifiStatusManager.checkActivityOnCreateWifiStatic(mContext);
        if(WifiStatusManager.getWifiIP().equals("")){
            Log.d(FindIPTAG, "please link wifi");
        } else if(!isServerOpen){
            serverThread = new ServerSocket();
            serverThread.startSocketRun(port);
            isServerOpen = true;
        }
    }

    public void stopServer(){
        if(isServerOpen && serverThread!= null){
            serverThread.stopSocketRun();
        }
        isServerOpen = false;
        serverThread = null;
    }

    public void startClient(){
        WifiStatusManager.checkActivityOnCreateWifiStatic(mContext);
        if(WifiStatusManager.getWifiIP().equals("")){
            Log.d(FindIPTAG, "please link wifi");
        } else if(!isClientOpen){
            clientSocket = new ClientSocket();
            clientSocket.startSocketRun(port);
            isClientOpen = true;
        }
    }

    public void stopClient(){
        if(isClientOpen && clientSocket != null){
            clientSocket.stopSocketRun();
        }
        isClientOpen = false;
        clientSocket = null;
    }

    public void sendAskIPMessage(){
        if(!isClientOpen){
            startClient();
        }

        if(clientSocket != null){
            clientSocket.send();
        }
    }
}
