package com.asus.zenboControl.Client;

import android.os.SystemClock;
import android.util.Log;

import com.asus.zenboControl.BaseMethod;
import com.asus.zenboControl.Event.ConnectStatusEvent;
import com.asus.zenboControl.Event.SyncTimeEvent;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.URI;
import java.nio.ByteBuffer;

import static android.R.id.message;

/**
 * Created by Kenny on 2016/10/31.
 */

public class Client extends WebSocketClient {

    private OnClientListener mOnClientListener;
    private boolean isConnected;

    private long requestTime;
    private long requestTicks;

    // system time computed from NTP server response
    private long mNtpTime;
    // value of SystemClock.elapsedRealtime() corresponding to mNtpTime
    private long mNtpTimeReference;
    // round trip time in milliseconds
    private long mRoundTripTime;
    private long mDiffNtpTime;
    private int appIdPosition = -1;

    public interface OnClientListener {
        void setReconnection(boolean isReconnection);

        void Reconnection();

        void setOpenFace();

        void setHideFace();

        void remoteControl(String controlInf);

        void getPcmFileStart(JSONObject json);

        void getPcmFile(ByteBuffer blob);

        void getPcmFileEnd();

        void openZbaFromAppId(String appId);

        void openApp(String action, String appId, long timeInMillis, long readyWaitTime);

        void stopApp();

        void setFace(boolean setFace);

        void setNtpTime(long ntpTime);

        void setDiffNtpTime(long diffNtpTime);

        void setEvent(int eventValue);

        void broadcastZbaEvent(String eventName, long transmitTime, long delayTime);

        void doMotionOnStage(String type, int dis, int rotationDegree,int facenum, int LEDType, int headdegree);

        void enableVoiceTrigger();

        void disableVoiceTrigger();

        void randomFace();

        void doLED(String side, String type, String color, int ledNumber, int brightness, int duration);

        void setTtsVoiceStatus(boolean isNeedClose);

        void syncTimeLED();

        void runPlanB(int faceNum ,String tts);

        void setMotionAvoidanceStatus(boolean setOpen);

        void setCapSensorStatus(boolean setOpen);
    }

    public void setOnClientListener(OnClientListener listener) {
        mOnClientListener = listener;
    }

    public Client(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public Client(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection");
        syncTime();
        setConnectStatusAndReconnection(true);
    }

    @Override
    public void onMessage(ByteBuffer blob) {
        Log.d("File saving", "onMessage ByteBuffer");
        mOnClientListener.getPcmFile(blob);
        super.onMessage(blob);

    }

    @Override
    public void onMessage(String gettext) {
        System.out.println("received: " + message);

        try {
            JSONObject getjson = new JSONObject(gettext);
            String message = getjson.getString("message");
            if( !checkMessageIsTooOld(getjson) && message.length()>0) {
                JSONObject json = new JSONObject(message);
                String status = json.getString("status");

                if(!(status.equals("syncTime") || status.equals("openAppList") || status.equals("requestSyncTime"))){
                    mOnClientListener.doLED("SYNC_BOTH", "blink", "#0099ff", 255, 100, 2);
                }
                switch (status) {
                    case "setOpenFace": {
                        mOnClientListener.setOpenFace();
                        break;
                    }
                    case "setHideFace": {
                        mOnClientListener.setHideFace();
                        break;
                    }
                    case "remoteControl": {
                        String controlInf = json.getString("information");
                        mOnClientListener.remoteControl(controlInf);
                        break;
                    }
                    case "openZba": {
                        String appId = json.getString("appId");
                        mOnClientListener.openZbaFromAppId(appId);
                        break;
                    }
                    case "start":
                        break;
                    case "pcmFile":
                        Log.e("ClientGetMessage", "pcmFile");
                        mOnClientListener.getPcmFileStart(json);
                        break;
                    case "pcmFileEnd":
                        Log.e("ClientGetMessage", "pcmFileEnd");
                        mOnClientListener.getPcmFileEnd();
                        break;
                    case "openAppList": {
                        String appId = json.getString("appId_" + appIdPosition);
                        long timeInMillis = json.getLong("timeInMillis");
                        long readyWaitTime = json.getLong("readyWaitTime");
                        mOnClientListener.openApp("com.asus.control.action.openAppAtTime", appId, timeInMillis, readyWaitTime * 1000);
//                        mOnClientListener.setTtsVoiceStatus(true);
                        break;
                    }
                    case "stopApp":
                        mOnClientListener.stopApp();
                        break;
                    case "requestSyncTime":
                        syncTime();
                        break;
                    case "syncTime": {
                        final long responseTicks = SystemClock.elapsedRealtime();
                        final long responseTime = requestTime + (responseTicks - requestTicks);

                        final long originateTime = json.getLong("originateTime");
                        final long receiveTime = json.getLong("receiveTime");
                        final long transmitTime = json.getLong("transmitTime");
                        long roundTripTime = responseTicks - requestTicks - (transmitTime - receiveTime);
                        long clockOffset = ((receiveTime - originateTime) + (transmitTime - responseTime)) / 2;

                        mNtpTime = responseTime + clockOffset;
                        mNtpTimeReference = responseTicks;
                        mRoundTripTime = roundTripTime;

                        mOnClientListener.setNtpTime(mNtpTime);

                        mDiffNtpTime = mNtpTime - mNtpTimeReference;
                        mOnClientListener.setDiffNtpTime(mDiffNtpTime);

                        EventBus.getDefault().post(new SyncTimeEvent());

                        mOnClientListener.syncTimeLED();
                        break;
                    }
                    case "setFace": {
                        boolean setFace = json.getBoolean("setFace");
                        mOnClientListener.setFace(setFace);
                        break;
                    }
                    case "setEvent":
                        if (appIdPosition == 0) {
                            int eventValue = json.getInt("eventValue");
                            mOnClientListener.setEvent(eventValue);
                        }
                        break;
                    case "stopSendFile":
                        break;
                    case "setMotionOnStage": {
                        String motionType = json.getString("information");
                        int rotationDegree = json.getInt("rotationDegree");
                        int moveDistance = json.getInt("distance");
                        int faceNum = json.getInt("face");
                        int LEDType = json.getInt("LEDtype");
                        int headdegree = json.getInt("headdegree");
                        mOnClientListener.doMotionOnStage(motionType, moveDistance, rotationDegree, faceNum, LEDType, headdegree);
                        break;
                    }
                    case "broadcastZbaEvent": {
                        final long transmitTime = json.getLong("transmitTime");
                        String eventName = json.getString("eventName");
                        long delay = getMessageDelayTime(getjson);
//                        mOnClientListener.setTtsVoiceStatus(false);
                        mOnClientListener.broadcastZbaEvent(eventName, transmitTime, delay);
                        break;

                    }
                    case "enableVoiceTrigger": {
                        mOnClientListener.enableVoiceTrigger();
                        break;
                    }
                    case "disableVoiceTrigger": {
                        mOnClientListener.disableVoiceTrigger();
                        break;
                    }

                    case "randomFace":{
                        mOnClientListener.randomFace();
                        break;
                    }
                    case "setTtVoiceStatus" :{
                        boolean isTTSClose = json.getBoolean("needSet");
//                        mOnClientListener.setTtsVoiceStatus(isTTSClose);
                        break;
                    }
                    case "setRunPlanB" :{
                        String stringTTS = json.getString("TTSstring");
                        int faceNum = json.getInt("face");
                        mOnClientListener.runPlanB(faceNum, stringTTS);
                    }
                    case "setMotionAvoidanceStatus" :{
                        boolean setStatus = json.getBoolean("setStatus");
                        mOnClientListener.setMotionAvoidanceStatus(setStatus);
                    }
                    case "setCapSensorStatusStatus" :{
                        boolean setStatus = json.getBoolean("setStatus");
                        mOnClientListener.setCapSensorStatus(setStatus);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setAppIdPosition(int position) {
        appIdPosition = position;
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println("Connection closed by " + (remote ? "remote peer" : "us"));
        if (remote) {
            setConnectStatusAndReconnection(false);
        }
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();

        System.out.println("onError");

        if(ex instanceof ConnectException){
            Log.d("Client onError", "ConnectException Reconnection");
            mOnClientListener.Reconnection();
        }
        // if the error is fatal then onClose will be called additionally
    }

    public boolean getConnectStatus() {
        return isConnected;
    }

    public void setConnectStatusAndReconnection(boolean isConnected) {
        setConnectStatus(isConnected);
        if (!isConnected) {
            mOnClientListener.Reconnection();
        }
    }

    public void setConnectStatus(boolean isConnected) {
        this.isConnected = isConnected;

        if (isConnected) {
            mOnClientListener.setReconnection(isConnected);
        }

        ConnectStatusEvent event = new ConnectStatusEvent();
        event.setConnectStatus(isConnected);
        EventBus.getDefault().post(event);
    }

    public void onDestroy() {
        this.close();
        isConnected = false;
    }

    public void syncTime() {
        // get current time and write it to the request packet
        requestTime = System.currentTimeMillis();
        requestTicks = SystemClock.elapsedRealtime();

        try {
            JSONObject request = new JSONObject();
            request.put("status", "syncTime");
            request.put("requestTime", requestTime);
            this.send(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private long getMessageDelayTime(JSONObject json){
        try {
            if(json.has("sendTime")) {
                long sendTime = json.getLong("sendTime");
                final long responseTicks = SystemClock.elapsedRealtime();
                final long currentTime = mNtpTime + (responseTicks - mNtpTimeReference);
                long delay = currentTime - sendTime;
                return delay;
            } else {
                return 0;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private boolean checkMessageIsTooOld(JSONObject json){

        try {
            boolean isNeedCheck = json.getBoolean("needCheckTime");
            if(isNeedCheck){
                long delayTime = getMessageDelayTime(json);
                Log.d("Client","Message Delay:"+delayTime);
                if(delayTime > BaseMethod.messageMaxDelayTime){
                    mOnClientListener.doLED("SYNC_BOTH", "blink", "#ff0000", 255, 100, 2);
                    EventBus.getDefault().post(new ClientMessageDelayTooLongEvent(delayTime));
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }


}