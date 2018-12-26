package com.asus.zenboControl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.asus.ctc.tool.DSAPI_Config;
import com.asus.ctc.tool.DSAPI_Result;
import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.SpeakConfig;
import com.asus.robotframework.API.WheelLights;
import com.asus.zenboControl.listener.OnRobotAPIManagerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static android.content.ContentValues.TAG;
import static com.asus.zenboControl.Provider.StatusDataEditor.getMotionAvoidanceStatus;


public class RobotAPIManager {

    private final static boolean DBG = true;
    private String LogTag = "RobotAPIControl";

    final static int MSG_onFinishInitPModule = 1000;
    final static int MSG_btnStartTTS = 1052;
    final static int MSG_btnStartSLU = 1054;
    final static int MSG_onCsrResult = 1055;
    final static int MSG_btnStartTtsCsrSlu = 1056;
    final static int MSG_onSluAskBack = 1057;

    private final static int DEFAULT_TIMEOUT = 12000;
    private final static int TIMEOUT = 12000;

    private static RobotAPIManager mInstance = null;

    private Context mContext;
    private TpmsHandler mTpmsHandler = null;
    private RobotAPI mRobotAPI;
    private OnRobotAPIManagerListener mOnRobotAPIControlListener;

    private String mDomainValue;


    private IntentionModule.InfoState mInfoState = IntentionModule.InfoState.NORMAL;

    private boolean State_btnStartCSR = false;
    private boolean State_btnStartTtsCsrSlu = false;
    private boolean State_btnStartTTS = false;
    private boolean stateSluResult = true;
    private boolean stateVADResult = true;

    private DefaultRobotAPICallBack mDefaultRobotAPICallBack = new DefaultRobotAPICallBack();
    private RobotFace previousRobotFace;
    private Handler mHandler = new Handler();

    public void setOnRobotAPIManagerListener(OnRobotAPIManagerListener listener) {
        mOnRobotAPIControlListener = listener;
    }

    public static RobotAPIManager getInstance(Context context) {
        //Create singleton to contain all PerceptionModule
        initialize(context);
        return mInstance;
    }

    /**
     * Initialize singleton mInstance with PerceptionModule info
     */
    public static void initialize(Context context) {
        if (mInstance == null) {
            mInstance = new RobotAPIManager(context);
        }
    }

    private RobotAPIManager(Context context) {
        Log.d(LogTag, "RobotAPIManager");
        mContext = context;
        initRobotAPI();
    }

    private void initRobotAPI() {
        if (mTpmsHandler == null) {
            mTpmsHandler = new TpmsHandler();
        }

        if (mRobotAPI == null) {
            mRobotAPI = new RobotAPI(mContext, mDefaultRobotAPICallBack);
            Log.d(LogTag, "RobotAPI Version " + mRobotAPI.getVersion());
            mRobotAPI.robot.setVoiceTrigger(true);
        }
    }

    public void setVoiceTrigger(boolean b) {
        Log.d("Samanna", "setVoiceTrigger " + b);
        mRobotAPI.robot.setVoiceTrigger(b);
    }

    public String getAPIVersion() {
        if (mRobotAPI != null) {
            return mRobotAPI.getVersion();
        }
        return "";
    }

    public static RobotAPIManager getRobotManager() {
        if (mInstance != null) {
            return mInstance;
        }
        return null;
    }

    RobotCallback.Listen mDsServiceListener = new RobotCallback.Listen() {

        @Override
        public void onFinishRegister() {
            mTpmsHandler.sendMessage(mTpmsHandler
                    .obtainMessage(MSG_onFinishInitPModule));
        }

        @Override
        public void onVoiceDetect(JSONObject var) {
            if (DBG)
                Log.i(LogTag, "***--> MSG_onEventTrigger " + var);

            if (stateVADResult) {
                stateVADResult = false;
            } else {
                stateVADResult = true;
            }
        }

        @Override
        public void onSpeakComplete(String sentence, String errorCode) {
            if (DBG)
                Log.d(LogTag, "onFinishTTS: " + sentence + " error_code=" + errorCode);

            if (State_btnStartTTS == true) {
                State_btnStartTTS = false;
            }

            try {
                JSONObject ttsSentence = new JSONObject(sentence);
                String tts = ttsSentence.getString("tts");
                Log.d(LogTag, "onFinishTTS: tts " + tts);

                if (!TextUtils.isEmpty(tts)) {
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onEventUserUtterance(JSONObject var) {
            DSAPI_Result mDSAPI_Result = new DSAPI_Result(
                    var.toString());
            disableSound();
            Log.i(LogTag, "***--> onEVENT_USER_UTTERANCE " + var.toString() + " " + mDSAPI_Result.event_user_utterance.engineType());

            mTpmsHandler.sendMessage(mTpmsHandler
                    .obtainMessage(MSG_onCsrResult,
                            mDSAPI_Result.event_user_utterance
                                    .toJson().toString()));

            // get keyword trigger
            if (mDSAPI_Result.event_user_utterance.engineType() == DSAPI_Config.CSR_TYPE_VoiceTrigger && mDSAPI_Result.event_user_utterance.slu_query_index() != 0) {
                mTpmsHandler.removeCallbacksAndMessages(null);
                startWait();
            } else {

            }
        }

        @Override
        public void onResult(JSONObject var) {
            Log.d(LogTag, "onEVENT_SLU_QUERY " + var.toString());
            disableSound();
        }

        @Override
        public void onRetry(JSONObject var) {
            Log.d(LogTag, "onAskBackProcess " + var.toString());

            if (var != null) {
                DSAPI_Result mDSAPI_Result = new DSAPI_Result(var.toString());
                String askBack = mDSAPI_Result.event_slu_query.question();

                mOnRobotAPIControlListener.onRetry(askBack);

            }

        }
    };

    public void RobotSay(String string) {
        mRobotAPI.robot.speak(string);
    }


    public void setDomain(String domainValue) {
        mDomainValue = domainValue;
    }

    public void registerListenRobotAPICallback() {
        Log.i(LogTag, "ServiceConnect");
        if (mRobotAPI != null) {
            mRobotAPI.robot.registerListenCallback(mDsServiceListener);
        }
    }

    public void onDestroy() {
        Log.i(LogTag, "onDestroy");

        if (mTpmsHandler != null) {
            mTpmsHandler.removeCallbacksAndMessages(null);
            mTpmsHandler = null;
        }

        if (mRobotAPI != null) {
            mRobotAPI.robot.stopSpeakAndListen();
            mRobotAPI.robot.clearAppContext(mDomainValue);
            mRobotAPI.vision.cancelDetectPerson();
            mRobotAPI.release();
            mRobotAPI = null;
        }
        stopWait();
        mInstance = null;

    }

    private class TpmsHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_onFinishInitPModule:
                    if (DBG)
                        Log.i(LogTag, "***--> Msg_onFinishInitPModule");

                    break;

                case MSG_btnStartTTS:

                    Log.d(LogTag, "State_btnStartTTS " + State_btnStartTTS);

                    if (DBG)
                        Log.i(LogTag, "***--> MSG_btnStartTTS");

                    if (mRobotAPI != null) {
                        if (msg.obj != null) {
                            mRobotAPI.robot.speak((String) msg.obj);
                        }
                    }

//                        mSentenceStr = null;
                    State_btnStartTTS = true;
//                    }

                    break;

                case MSG_btnStartSLU:
                    if (DBG)
                        Log.i(LogTag, "***--> MSG_btnStartSLU " + msg.obj + " mDomainValue " + mDomainValue);

                    if (mRobotAPI != null) {
                        if (msg.obj != null) {
                            mRobotAPI.robot.jumpToPlan(mDomainValue, (String) msg.obj);
                        }
                    }

                    break;
                case MSG_btnStartTtsCsrSlu:
                    if (DBG)
                        Log.i(LogTag, "***--> MSG_btnStartTtsCsrSlu " + State_btnStartTtsCsrSlu + " msg.obj " + (String) msg.obj);

                    if (mRobotAPI != null) {

//                        moveSpecificAction(1);
                        mRobotAPI.robot.stopSpeakAndListen();
                        mTpmsHandler.removeCallbacksAndMessages(null);

                        String sentence = "";
                        if (msg.obj != null) {
                            sentence = (String) msg.obj;
                        }

                        stopWait();
                        if (mInfoState == IntentionModule.InfoState.NORMAL) {
                            mRobotAPI.robot.speakAndListen(sentence, DEFAULT_TIMEOUT);
                        } else {
                            mRobotAPI.robot.speakAndListen(sentence, TIMEOUT);
                        }

                        State_btnStartTtsCsrSlu = true;

                    }
                    break;
                case MSG_onCsrResult: {
                    if (DBG)
                        Log.i(LogTag, "***--> MSG_onCsrResult=" + (String) msg.obj);

                    if (State_btnStartCSR == true) {
                        State_btnStartCSR = false;
                    }
                }
                break;

                case MSG_onSluAskBack:
                    if (msg.obj != null) {
                        DSAPI_Result mDSAPI_Result = new DSAPI_Result((String) msg.obj);
                        String askBack = mDSAPI_Result.event_slu_query.question();
                        Log.i(LogTag, "***--> askBack " + askBack + "  error_code=" + mDSAPI_Result.event_slu_query);
                        if (askBack != null) {
//                            StartTTS(askBack);
                        } else {
//                        StartTtsCsrSlu();
                        }
                    }
                    break;
            }
        }

    }

    public void stopSpeakAndListen() {
        mRobotAPI.robot.stopSpeak();
        mRobotAPI.robot.stopSpeakAndListen();
        if (mTpmsHandler != null) {
            mTpmsHandler.removeCallbacksAndMessages(null);
        }
    }

    public void moveHead(boolean isAddCount, boolean isBlocking, float yaw, float pitch, MotionControl.SpeedLevel.Head speedLevel) {
        mDefaultRobotAPICallBack.setBlocking(isBlocking);
        if (isAddCount) {
            setHeadBlockingCount(isBlocking);
        }
        mRobotAPI.motion.moveHead(yaw, pitch, speedLevel);
    }

    public void moveBody(boolean isAddCount, boolean isBlocking, float relativeX, float relativeY, float relativeTheta, MotionControl.SpeedLevel.Body speedLevel) {
        mDefaultRobotAPICallBack.setBlocking(isBlocking);
        if (isAddCount) {
            setBodyBlockingCount(isBlocking);
        }
        mRobotAPI.motion.moveBody(relativeX, relativeY, relativeTheta, speedLevel);
    }

    public void setHeadBlockingCount(boolean isBlocking) {
        if (mDefaultRobotAPICallBack != null) {
            mDefaultRobotAPICallBack.setHeadBlockingCount(isBlocking);
        }
    }

    public void setBodyBlockingCount(boolean isBlocking) {
        if (mDefaultRobotAPICallBack != null) {
            mDefaultRobotAPICallBack.setBodyBlockingCount(isBlocking);
        }
    }

    public void setExpression(RobotFace robotFace) {
        previousRobotFace = robotFace;
        mRobotAPI.robot.setExpression(robotFace);
    }

    public void setExpressionAndSpeak(RobotFace robotFace, String tts){
        previousRobotFace = robotFace;
        mRobotAPI.robot.setExpression(robotFace,tts);
    }
    public void hideEmotion() {
        Log.e(LogTag, "hideEmotion");
        mRobotAPI.robot.setExpression(RobotFace.HIDEFACE);
    }

    public void stopAction() {
        mRobotAPI.motion.stopMoving();
    }
    public void checkRobotAPI(){
        if(mRobotAPI == null && mContext != null){
            mRobotAPI = new RobotAPI(mContext);
        }
    }

    public void ledWheel(String side, String type, String color, int ledNumber, int brightness, int duration) {
        brightness = (brightness == 1) ? 1 : brightness / 2;

        mRobotAPI.wheelLights.setColor(WheelLights.Lights.SYNC_BOTH, 0xff, Color.parseColor(color));
        mRobotAPI.wheelLights.turnOff(WheelLights.Lights.SYNC_BOTH, 0xff);
        mRobotAPI.wheelLights.setBrightness(WheelLights.Lights.SYNC_BOTH, 0xff, 0);
        mRobotAPI.wheelLights.setBrightness(WheelLights.Lights.valueOf(side), ledNumber, brightness);

        if (type.contains("blink")) {
            mRobotAPI.wheelLights.startBlinking(WheelLights.Lights.valueOf(side), ledNumber, 50, 50, duration);
        } else if (type.contains("marques")) {
            mRobotAPI.wheelLights.startMarquee(WheelLights.Lights.valueOf(side), WheelLights.Direction.DIRECTION_FORWARD, 15, 10, 0);
            mRobotAPI.wheelLights.setColor(WheelLights.Lights.valueOf(side), 0x01, 0xff0000);
            mRobotAPI.wheelLights.setColor(WheelLights.Lights.valueOf(side), 0x02, 0xffff7d00);
            mRobotAPI.wheelLights.setColor(WheelLights.Lights.valueOf(side), 0x04, 0xffffff00);
            mRobotAPI.wheelLights.setColor(WheelLights.Lights.valueOf(side), 0x08, 0xff00ff00);
            mRobotAPI.wheelLights.setColor(WheelLights.Lights.valueOf(side), 0x10, 0xff0000ff);
            mRobotAPI.wheelLights.setColor(WheelLights.Lights.valueOf(side), 0x20, 0xff00ffff);
            mRobotAPI.wheelLights.setColor(WheelLights.Lights.valueOf(side), 0x40, 0xffff00ff);
            mRobotAPI.wheelLights.setColor(WheelLights.Lights.valueOf(side), 0x80, 0xffffffff);
        } else if (type.contains("breathing")) {
            mRobotAPI.wheelLights.startBreathing(WheelLights.Lights.valueOf(side), ledNumber, 1, 1, duration);
        } else if (type.contains("charging")) {
            mRobotAPI.wheelLights.startCharging(WheelLights.Lights.valueOf(side), 0, 4, WheelLights.Direction.DIRECTION_FORWARD, 5);
        } else if (type.contains("stable")) {
            mRobotAPI.wheelLights.setBrightness(WheelLights.Lights.valueOf(side), ledNumber, brightness);
        } else {
            mRobotAPI.wheelLights.setBrightness(WheelLights.Lights.valueOf(side), 0xff, brightness);
        }

        if (duration > 0) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new ledRunnable(), duration * 1000);
        }

    }

    private class ledRunnable implements Runnable {

        @Override
        public void run() {
            Log.e(TAG, "ledWaitTime onFinish");
            stopLed();
        }
    }

    public void stopLed() {
        mRobotAPI.wheelLights.turnOff(WheelLights.Lights.SYNC_BOTH, 0xff);
        mRobotAPI.wheelLights.setBrightness(WheelLights.Lights.SYNC_BOTH, 0xff, 0);
    }

    public void remoteControlBody(MotionControl.Direction.Body control) {
        if(getMotionAvoidanceStatus(mContext)){
            mRobotAPI.motion.remoteControlBody(control);
        } else {
            mRobotAPI.hidden.remoteControlBodyWithoutAvoidance(control);
        }
    }

    public void startWait() {
        if (mInfoState == IntentionModule.InfoState.NORMAL) {
            startWait(DEFAULT_TIMEOUT);
        } else {
            startWait(TIMEOUT);
        }
    }

    public void startWait(int timeOut) {
        Log.i(LogTag, "***--> startWait " + timeOut);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mOnRobotAPIControlListener.onTimeOut();
            }
        }, timeOut);
    }

    public void stopWait() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public void executeSoundFile() {
        Log.d("ExecuteSound", "stop listen");
        mRobotAPI.robot.stopSpeakAndListen();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("ExecuteSound", "start execute sound");
                final SpeakConfig sc = new SpeakConfig();
                sc.timeout(10);
                executeSound(RecordAudio.recodePCMfilesrc);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRobotAPI.robot.speakAndListen("", sc);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                disableSound();
                            }
                        }, 200);
                    }
                }, 200);
            }
        }, 500);
    }

    private void executeSound(String uriStr) {
        try {
            Log.d("execute Sound", "executeSound uri:" + uriStr);
            File f = new File(uriStr);
            if (f.canRead()) {
                Log.d("execute Sound", "file can read");
            } else {
                Log.d("execute Sound", "file cannot read");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject cmd = new JSONObject();
        try {
            cmd.put("voicePath", uriStr); //音檔路徑
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setAction("com.asus.simpleplayer.general.player_send");
        intent.putExtra("EnableDebugCsr", cmd.toString());
        mContext.sendBroadcast(intent);
    }

    public void disableSound() {
        Intent intent = new Intent();
        intent.setAction("com.asus.simpleplayer.general.player_send");
        intent.putExtra("DisableDebugCsr", "disable");
        mContext.sendBroadcast(intent);
    }
    public void remoteControlMotion(MotionControl.Direction.Body command){
        mRobotAPI.motion.remoteControlBody(command);
}
    public void remoteControlBodyMove(float relativeX, float relativeY, float relativeTheta, MotionControl.SpeedLevel.Body speedLevel){
        mRobotAPI.motion.moveBody(relativeX, relativeY, angleToArc(relativeTheta), speedLevel);
    }
    private float angleToArc(float angle) {
        return (float) (Math.PI * (angle / 180));
    }
    public void doLedAndKeepBlue(String side, String type, String color, int ledNumber, int brightness, int duration){
        ledWheel(side,type,color,ledNumber,brightness,duration);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ledWheel("SYNC_BOTH", "stable", "#0099ff", 255, 100, 0);
            }
        }, duration*1000);
    }

    public void setCapSensorStatus(boolean setStatus){
        mRobotAPI.robot.setPressOnHeadAction (setStatus);
    }

}
