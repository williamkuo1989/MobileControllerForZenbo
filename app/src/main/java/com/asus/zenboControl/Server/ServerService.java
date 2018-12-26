package com.asus.zenboControl.Server;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.asus.zenboControl.SearchIP.FindIPSocketManager;
import com.asus.zenboControl.WifiLinkManager;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServerService extends Service implements Server.OnServerConnectListener {
    private String TAG = getClass().getSimpleName();

    private ServerIBinder mIBinder = new ServerIBinder();
    private Server server = null;
    private FindIPSocketManager mFindIPSocketManager;
    private String clientName;
    private long timeInMillis;


    public ServerService() {
        Log.e(TAG, "ServerService " + server);


        if (server == null) {
            try {
                server = new Server(1111);
                server.setControlClient(clientName);
                server.setOnServerConnectListener(this);
                server.start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver();
        WifiLinkManager.getManager(getApplicationContext()).startWifiThread();
        if(mFindIPSocketManager == null){
            mFindIPSocketManager = new FindIPSocketManager(getApplicationContext());
            mFindIPSocketManager.startServer();
        }
    }

    public void setControlClient(String clientName) {

        this.clientName = clientName;
        if (server != null)
            server.setControlClient(clientName);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand " + intent);

        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }
    public void remoteControlBody(String status){
        if (server != null)
            server.remoteControlBody(status);
    }
    public void setOpenFace() {
        if (server != null)
            server.setOpenFace();
    }
    public void setHideFace() {
        if (server != null)
            server.setHideFace();
    }
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        WifiLinkManager.getManager(getApplicationContext()).stopWifiThread();
        if(mFindIPSocketManager != null){
            mFindIPSocketManager.stopServer();
        }
        if (server != null)
            server.onDestroy();
        super.onDestroy();
    }

    public class ServerIBinder extends Binder {
        public ServerService getService() {
            return ServerService.this;
        }
    }

    private void registerReceiver() {
        IntentFilter filters = new IntentFilter();
        filters.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
    }

    public void setClientStatus() {
        if (server != null)
            server.setClientStatus();
    }

    public void sendFile(String src) {
        if (server != null)
            server.sendFile(src);
    }

    public void stopSendFile() {
        if (server != null)
            server.stopSendFile();
    }

    public void setEnterStage(int dis,int rotationDegree, int face, int LEDType, int headDegree) {
        if (server != null)
            server.setEnterStage(dis,rotationDegree, face,LEDType,headDegree);
    }

    public void setLeaveStage(int dis,int rotationDegree, int face, int LEDType, int headDegree) {
        if (server != null)
            server.setLeaveStage(dis,rotationDegree,face,LEDType,headDegree);
    }

    public void setAppIdAndOpen(String appId) {
        if (server != null)
            server.setAppIdAndOpen(appId);
    }

    public void openAppAtTime(ArrayList<String> appId, long timeInMillis, long readyWaitTime) {
        this.timeInMillis = timeInMillis;
        if (server != null)
            server.openAppAtTime(appId, timeInMillis, readyWaitTime);
    }

    public void stopApp() {
        if (server != null)
            server.stopApp();
    }

    public void requestSyncTime() {
        if (server != null)
            server.requestSyncTime();
    }

    public void setFace(boolean setFace) {
        if (server != null)
            server.setFace(setFace);
    }

    public void setEvent(int eventValue) {
        if (server != null)
            server.setEvent(eventValue);
    }

    public void broadcastZbaEvent(String eventName) {
        if (server != null)
            server.broadcastZbaEvent(eventName);
    }

    public void setRunPlanB(int faceNum, String tts) {
        if (server != null)
            server.setRunPlanB(faceNum, tts);
    }

    public long getTimeInMillis() {

        return timeInMillis;
    }

    public void setTtsVoiceStatus(boolean eventBoolean) {
        if (server != null)
            server.setTtsVoiceStatus(eventBoolean);
    }

    public void setMotionAvoidanceStatus(boolean setOpen){
        if (server != null)
            server.setMotionAvoidanceStatus(setOpen);
    }

    public void setCapSensorStatus(boolean setOpen){
        if (server != null)
            server.setCapSensorStatus(setOpen);
    }



    public void enableVoiceTrigger(){
        server.enableVoiceTrigger();
    }
    public void disableVoiceTrigger(){
        server.disableVoiceTrigger();
    }

    public void randomFace(){
        if (server != null)
            server.randomFace();
    }


}
