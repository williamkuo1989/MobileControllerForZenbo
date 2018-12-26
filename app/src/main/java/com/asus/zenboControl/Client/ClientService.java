package com.asus.zenboControl.Client;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotFace;
import com.asus.zenboControl.Event.FaceEvent;
import com.asus.zenboControl.Event.FileGetDoingEvent;
import com.asus.zenboControl.Event.FileGetEndEvent;
import com.asus.zenboControl.Event.FileGetStartEvent;
import com.asus.zenboControl.Event.UpdateMotionAvoidanceStatusEvent;
import com.asus.zenboControl.IntentionModule;
import com.asus.zenboControl.RecordAudio;
import com.asus.zenboControl.RobotAPIManager;
import com.asus.zenboControl.WifiLinkManager;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.drafts.Draft_10;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Random;

import static com.asus.zenboControl.Provider.StatusDataEditor.UpdateMotionAvoidanceStatus;

public class ClientService extends Service implements Client.OnClientListener {
    private String TAG = getClass().getSimpleName();

    public static int rowNumber = -1;

    private ServerIBinder mIBinder = new ServerIBinder();
    private Client client = null;
    private Handler mHandler;
    private Handler mWaitHandler;
    private Handler mConnectionHandler;
    private String ipAddress;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean isReconnection;
    private int appIdPosition = -1;
    private boolean isGetPCMFile = false;

    private long mNtpTime;
    private long mDiffNtpTime;

    private int receivedFileSize;
    private int fileTotalCapacity;
    private WritableByteChannel channel;

    private int[] faceValue = {RobotFace.PROUD.getValue(), RobotFace.EXPECTING.getValue(), RobotFace.CONFIDENT.getValue(),
            RobotFace.ACTIVE.getValue(), RobotFace.PLEASED.getValue(), RobotFace.SHY.getValue(), RobotFace.SINGING.getValue()};


    public ClientService() {
        mBroadcastReceiver = new WifiBroadcastReceiver();
    }

    public void connectServer(String ip) {
        Log.e(TAG, "ip " + ip);
        this.ipAddress = ip;
        if (client != null) {
            client.close();
        }

        try {
            client = new Client(new URI("ws://" + ip + ":1111"), new Draft_10()); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
            client.setOnClientListener(this);
            client.connect();
            client.setAppIdPosition(appIdPosition);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void registerReceiver() {
        IntentFilter filters = new IntentFilter();
        filters.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, filters);
    }

    public void setAppIdPosition(int position) {

        Log.e(TAG, "setAppIdPosition " + position);

        appIdPosition = position;
        if (client != null) {
            client.setAppIdPosition(position);
        }
    }

    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if (info != null) {
                Log.e(TAG, "info.isConnected() " + info.isConnected() + " info.isAvailable() " + info.isAvailable());
            }

            if (info != null) {
                mConnectionHandler.removeCallbacksAndMessages(null);
                if (!info.isConnected()) {
                    if (client != null) {
                        client.close();
                        client.setConnectStatus(false);
                    }
                } else {
                    Reconnection();
                }
            }
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setNtpTime(long ntpTime) {
        mNtpTime = ntpTime;
    }

    public void setDiffNtpTime(long diffNtpTime) {
        mDiffNtpTime = diffNtpTime;
    }

    public long getNtpTime() {
        return mNtpTime;
    }

    public long getDiffNtpTime() {
        return mDiffNtpTime;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind " + intent);
        return mIBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mWaitHandler = new Handler();
        mConnectionHandler = new Handler();
        WifiLinkManager.getManager(getApplicationContext()).startWifiThread();
        registerReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand " + intent);

//        return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        Log.e(TAG, "onDestroy");

        if (client != null)
            client.onDestroy();

        WifiLinkManager.getManager(getApplicationContext()).stopWifiThread();
        mHandler.removeCallbacksAndMessages(null);
        mWaitHandler.removeCallbacksAndMessages(null);

//        if (waitTimer != null)
//            waitTimer.cancel();

        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private float angleToArc(float angle) {
        return (float) (Math.PI * (angle / 180));
    }

    @Override
    public void setReconnection(boolean isReconnection) {
        if (isReconnection) {
            mConnectionHandler.removeCallbacksAndMessages(null);
        }
        if (!this.isReconnection) {
        }
        this.isReconnection = isReconnection;
    }

    @Override
    public void Reconnection() {
        if (isReconnection) {
            mConnectionHandler.removeCallbacksAndMessages(null);
            mConnectionHandler.post(Connection);
        }
    }

    @Override
    public void setOpenFace() {
        RobotAPIManager.getInstance(ClientService.this).setExpression(RobotFace.DEFAULT);
    }

    @Override
    public void setHideFace() {
        RobotAPIManager.getInstance(ClientService.this).setExpression(RobotFace.HIDEFACE);
    }

    @Override
    public void remoteControl(String controlInf) {
        switch (controlInf) {
            case "FORWARD":
                RobotAPIManager.getInstance(ClientService.this).remoteControlBody(MotionControl.Direction.Body.FORWARD);
                break;
            case "BACKWARD":
                RobotAPIManager.getInstance(ClientService.this).remoteControlBody(MotionControl.Direction.Body.BACKWARD);
                break;
            case "TURN_LEFT":
                RobotAPIManager.getInstance(ClientService.this).remoteControlBody(MotionControl.Direction.Body.TURN_LEFT);
                break;
            case "TURN_RIGHT":
                RobotAPIManager.getInstance(ClientService.this).remoteControlBody(MotionControl.Direction.Body.TURN_RIGHT);
                break;
            case "STOP":
                RobotAPIManager.getInstance(ClientService.this).remoteControlBody(MotionControl.Direction.Body.STOP);
                RobotAPIManager.getInstance(ClientService.this).stopAction();
                break;
        }
    }

    @Override
    public void getPcmFileStart(JSONObject json) {
        isGetPCMFile = true;

        receivedFileSize = 0;
        String fileSize = null;
        try {
            fileSize = json.getString("size");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fileTotalCapacity = Integer.parseInt(fileSize);

        final File f = new File(Environment.getExternalStorageDirectory() + RecordAudio.recodePCMfilesrcNoSdcard);

        File dirs = new File(f.getParent());
        if (!dirs.exists()) {
            Log.d("File saving", "Creating your directories right now");
            dirs.mkdirs();
        }

        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            channel = Channels.newChannel(new FileOutputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //mOnServerConnectListener.receiveFileStart(fileTotalCapacity);

        FileGetStartEvent eventstart = new FileGetStartEvent(fileTotalCapacity);
        EventBus.getDefault().post(eventstart);
    }

    @Override
    public void getPcmFile(ByteBuffer blob) {
        if(isGetPCMFile) {
            try {
                Log.d("File saving", "getPcmFile now");
                channel.write(blob);

                receivedFileSize += blob.capacity();

                FileGetDoingEvent event = new FileGetDoingEvent(receivedFileSize);
                EventBus.getDefault().post(event);

                JSONObject request = new JSONObject();
                try {
                    request.put("status", "request");
                    request.put("filesize", receivedFileSize);
                    client.send(request.toString());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Log.d(TAG, "receivedFileSize " + receivedFileSize);

            } catch (IOException e) {
                // Log.d(WiFiDirectActivity.TAG, e.toString());
                Log.d("Copy Stream Error", e.toString());
            }
        } else {
            getPcmFileEnd();
        }
    }

    @Override
    public void getPcmFileEnd() {
        if(isGetPCMFile) {
            JSONObject request = new JSONObject();
            try {
                request.put("status", "sendend");
                client.send(request.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

             Log.d("File saving","fileTotalCapacity:"+fileTotalCapacity+"  receivedFileSize:"+receivedFileSize);
             if (fileTotalCapacity != 0 && fileTotalCapacity == receivedFileSize) {
                 RobotAPIManager.getInstance(ClientService.this).executeSoundFile();
             }
        }
        FileGetEndEvent event = new FileGetEndEvent();
        EventBus.getDefault().post(event);
        isGetPCMFile = false;
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void openZbaFromAppId(final String appId) {
        final ClientActivity mActivity = ClientActivity.getClientActivity();
        Log.d("ClientActivity","open zeb by appid:"+appId);
        if(mActivity != null){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction("com.asus.robot.appbuilder.action.PLAY");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("appId", appId);
                    intent.putExtra("appReplay", false);
                    startActivity(intent);
                }
            });
        }
    }

    Runnable Connection = new Runnable() {
        @Override
        public void run() {
            if (!TextUtils.isEmpty(ipAddress)) {
                if (client == null || !client.getConnectStatus()) {
                    connectServer(ipAddress);
                    mConnectionHandler.postDelayed(Connection, 3000);
                }
            }
        }
    };

    Runnable LinkWifi = new Runnable() {
        @Override
        public void run() {

        }
    };

    public class ServerIBinder extends Binder {
        public ClientService getService() {
            return ClientService.this;
        }
    }

    public boolean getConnectStatus() {
        if (client != null)
            return client.getConnectStatus();
        return false;
    }

    @Override
    public void stopApp() {
        Log.e(TAG, "stopApp sendBroadcast");

        Intent intent = new Intent();
        intent.setAction("com.asus.robot.appbuilder.action.STOP.NOT_HIDE_EMOTION");
        sendBroadcast(intent);
    }

    @Override
    public void setFace(boolean setFace) {
        if (setFace) {
            IntentionModule.getInstance(this).setExpression(RobotFace.DEFAULT);
        } else {
            IntentionModule.getInstance(this).hideEmotion();
        }

        FaceEvent event = new FaceEvent();
        event.setFaceStatus(setFace);
        EventBus.getDefault().post(event);
    }

    @Override
    public void openApp(final String action, final String appId, final long timeInMillis, final long readyWaitTime) {
        Log.e(TAG, "openApp readyWaitTime " + readyWaitTime);

        if (!TextUtils.isEmpty(appId)) {
            RobotAPIManager.getInstance(ClientService.this).doLedAndKeepBlue("SYNC_BOTH", "blink", "#00FF00", 255, 100, 3);
            RobotAPIManager.getInstance(ClientService.this).moveHead(true, true, angleToArc(0), angleToArc(0), MotionControl.SpeedLevel.Head.valueOf("L2"));

            final long ntpTime = getDiffNtpTime();

            if ("com.asus.control.action.openAppAtTime".equals(action)) {

                long now = ntpTime + SystemClock.elapsedRealtime();
                long waitTime = timeInMillis - now;
                Log.e(TAG, "now " + now + " timeInMillis " + timeInMillis + " waitTime " + waitTime);

                Intent intent = new Intent(ClientService.this, ClientActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                long clientWaitTime = 0;

				if (waitTime > readyWaitTime) {
                    clientWaitTime = waitTime - readyWaitTime;
                }
                Log.e(TAG, "clientWaitTime " + clientWaitTime + "; ");

                if (clientWaitTime > 0) {

                    intent.putExtra("timeInMillis", timeInMillis);

                    mWaitHandler.removeCallbacksAndMessages(null);
                    mWaitHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(ClientService.this, ClientActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(action);
                            intent.putExtra("appId", appId);

                            intent.putExtra("timeInMillis", timeInMillis);
                            intent.putExtra("ntpTime", ntpTime);
                            startActivity(intent);
                        }
                    }, clientWaitTime);
                }

                startActivity(intent);
            } else if ("com.asus.control.action.openApp".equals(action)) {
                Intent intent = new Intent(ClientService.this, ClientActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(action);
                intent.putExtra("appId", appId);
                intent.putExtra("timeInMillis", ntpTime);
                intent.putExtra("ntpTime", ntpTime);
                startActivity(intent);
            }
        }
    }

    @Override
    public void setEvent(int eventValue) {
        Log.e(TAG, "setEvent " + eventValue);

        switch (eventValue) {
            case 0:
                RobotAPIManager.getInstance(ClientService.this).moveHead(true, true, angleToArc(0), angleToArc(-15), MotionControl.SpeedLevel.Head.valueOf("L3"));
                break;
            case 1:
                RobotAPIManager.getInstance(ClientService.this).setExpression(RobotFace.EXPECTING);
                RobotAPIManager.getInstance(ClientService.this).moveHead(true, true, angleToArc(0), angleToArc(25), MotionControl.SpeedLevel.Head.valueOf("L1"));

                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RobotAPIManager.getInstance(ClientService.this).moveHead(true, true, angleToArc(0), angleToArc(0), MotionControl.SpeedLevel.Head.valueOf("L1"));
                    }
                }, 4000);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RobotAPIManager.getInstance(ClientService.this).setExpression(RobotFace.DEFAULT);
                    }
                }, 6000);
                break;
        }
    }

    @Override
    public void broadcastZbaEvent(String eventName, long transmitTime, long delayTime) {
        long now = mDiffNtpTime + SystemClock.elapsedRealtime();
        long tripTime = now - transmitTime;
        transmitTime = System.currentTimeMillis() - delayTime;
        Log.e(TAG, "now " + now + " transmitTime " + transmitTime + " delayTime " + delayTime+ "  event name:"+eventName);
        Intent intent = new Intent();
        intent.putExtra("Broadcast_Event", "event_"+eventName);
        intent.putExtra("transmitTime", transmitTime);
        intent.setAction("com.asus.robot.appbuilder.action.BROADCAST_EVENT");
        sendBroadcast(intent);

    }

    @Override
    public void doMotionOnStage(String type, int dis, int rotationDegree, int facenum, int LEDType, int headdegree) {
        RobotAPIManager.getInstance(ClientService.this).setExpression(RobotFace.getRobotFace(facenum));
        RobotAPIManager.getInstance(ClientService.this).moveHead(false,true,0,(float) (headdegree * (Math.PI / (float) 180)), MotionControl.SpeedLevel.Head.L2);
        if(LEDType == 0)
            RobotAPIManager.getInstance(ClientService.this).ledWheel("SYNC_BOTH", "stable", "#0099ff", 255, 100, 0);
        else if(LEDType == 1)
            RobotAPIManager.getInstance(ClientService.this).ledWheel("SYNC_BOTH", "marques", "#0099ff", 255, 100, 0);
        Log.e(TAG,"type:"+type);
        float distance = (float) dis / 100;
        if(type.contains("enter")) {
            RobotAPIManager.getInstance(ClientService.this).moveBody(false,false,distance,0, 0, MotionControl.SpeedLevel.Body.L4);
            RobotAPIManager.getInstance(ClientService.this).moveBody(false,false,0,0, (float) (rotationDegree * (Math.PI / (float) 180)), MotionControl.SpeedLevel.Body.L4);
        } else {
            RobotAPIManager.getInstance(ClientService.this).moveBody(false,false,0,0, (float) (rotationDegree * (Math.PI / (float) 180)), MotionControl.SpeedLevel.Body.L4);
            RobotAPIManager.getInstance(ClientService.this).moveBody(false,false,distance,0, 0, MotionControl.SpeedLevel.Body.L4);
        }
    }

    @Override
    public void enableVoiceTrigger() {
        RobotAPIManager.getInstance(ClientService.this).setVoiceTrigger(true);
    }

    @Override
    public void disableVoiceTrigger() {
        RobotAPIManager.getInstance(ClientService.this).setVoiceTrigger(false);
    }

    @Override
    public void doLED(String side, String type, String color, int ledNumber, int brightness, int duration) {
        RobotAPIManager.getInstance(ClientService.this).doLedAndKeepBlue(side,type,color,ledNumber,brightness,duration);
    }

    @Override
    public void randomFace() {
        Random r = new Random(System.currentTimeMillis());
        int selectFace = faceValue[r.nextInt(7)];
        RobotAPIManager.getInstance(ClientService.this).setExpression(RobotFace.getRobotFace(selectFace));
    }


    @Override
    public void setTtsVoiceStatus(boolean isNeedClose) {
        AudioManager audiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, 12, 0);

        if(isNeedClose){
            audiomanage.setStreamVolume(9,0,0);
        } else {
            if(rowNumber == 0) {
                audiomanage.setStreamVolume(9, 12, 0);
            }
        }
    }

    @Override
    public void syncTimeLED() {
        IntentionModule.getInstance(this).ledWledWheel("SYNC_BOTH", "blink", "#FFFF00", 255, 100, 3);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rowNumber == 0) {
                    IntentionModule.getInstance(ClientService.this).ledWledWheel("SYNC_BOTH", "stable", "#FFFF00", 255, 100, 0);
                } else if (rowNumber == 1) {
                    IntentionModule.getInstance(ClientService.this).ledWledWheel("SYNC_BOTH", "stable", "#99FFFF", 255, 100, 0);
                } else if (rowNumber == 2) {
                    IntentionModule.getInstance(ClientService.this).ledWledWheel("SYNC_BOTH", "stable", "#3A0088", 255, 100, 0);
                }
            }
        }, 3 * 1000);
    }

    @Override
    public void runPlanB(int faceNum, String tts) {
        IntentionModule.getInstance(ClientService.this).setExpressionAndSpeak(RobotFace.getRobotFace(faceNum), tts);
    }

    @Override
    public void setMotionAvoidanceStatus(boolean setOpen) {
        UpdateMotionAvoidanceStatus(getApplicationContext(), setOpen);
        UpdateMotionAvoidanceStatusEvent eventstart = new UpdateMotionAvoidanceStatusEvent();
        EventBus.getDefault().post(eventstart);
    }

    @Override
    public void setCapSensorStatus(boolean setStatus) {
        RobotAPIManager.getInstance(ClientService.this).setCapSensorStatus(setStatus);
    }
}
