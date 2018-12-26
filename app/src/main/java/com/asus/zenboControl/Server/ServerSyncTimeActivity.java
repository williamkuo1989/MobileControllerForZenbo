package com.asus.zenboControl.Server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asus.robotframework.API.RobotFace;
import com.asus.zenboControl.BaseActivity;
import com.asus.zenboControl.BaseMethod;
import com.asus.zenboControl.ButtonCondownRunnable;
import com.asus.zenboControl.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ServerSyncTimeActivity extends BaseActivity {
    private String TAG = getClass().getSimpleName();
    private final String SSID = "ASUS-Davinci";

    private Button openAppAtTime;
    private Button stopApp;
    private Button requestSyncTime;
    private Button setAppId;
    private Button broadcastEvent;
    private Button openFace;
    private Button hideFace;

    private Button mEnableVoiceTriggerBtn;
    private Button mDisableVoiceTriggerBtn;

    private TextView time;
    private TextView date;
    private TextView tvAppId;
    private TextView buttonClickTime;
    private TextView buttonTotalTimer;

    private MaterialNumberPicker hourPicker;
    private MaterialNumberPicker minutePicker;
    private MaterialNumberPicker secondPicker;
    private MaterialNumberPicker readyWaitPicker;

    private ServerService mService;
    private Handler mHandler = new Handler();
    private Handler mSendHandler = new Handler();
    private ButtonCondownRunnable buttonClickRunnable;

    private ArrayList<String> appId;
    private String[] onOff = {"false", "true"};
    private String[] eventName = {"Zenbo低頭", "慢慢抬起頭。期待臉"};
    private int[] eventFaceValue = {RobotFace.PREVIOUS.getValue(), RobotFace.EXPECTING.getValue()};

    private String[] faceName = {
            "PREVIOUS(-1)", "OTHER_USE(0)", "INTERESTED(1)", "DOUBTING(2)", "PROUD(3)",
            "DEFAULT(4)", "HAPPY(5)", "EXPECTING(6)", "SHOCKED(7)", "QUESTIONING(8)",
            "IMPATIENT(9)", "CONFIDENT(10)", "ACTIVE(11)", "PLEASED(12)", "HELPLESS(13)",
            "SERIOUS(14)", "WORRIED(15)", "PRETENDING(16)", "LAZY(17)", "AWARE_RIGHT(18)",
            "TIRED(19)", "SHY(20)", "INNOCENT(21)", "SINGING(22)", "AWARE_LEFT(23)",
            "DEFAULT_STILL(24)", "INTERFACE(0)"};

    private int[] faceValue = {RobotFace.PREVIOUS.getValue(), RobotFace.OTHER_USE.getValue(), RobotFace.INTERESTED.getValue(), RobotFace.DOUBTING.getValue(), RobotFace.PROUD.getValue(),
            RobotFace.DEFAULT.getValue(), RobotFace.HAPPY.getValue(), RobotFace.EXPECTING.getValue(), RobotFace.SHOCKED.getValue(), RobotFace.QUESTIONING.getValue(),
            RobotFace.IMPATIENT.getValue(), RobotFace.CONFIDENT.getValue(), RobotFace.ACTIVE.getValue(), RobotFace.PLEASED.getValue(), RobotFace.HELPLESS.getValue(),
            RobotFace.SERIOUS.getValue(), RobotFace.WORRIED.getValue(), RobotFace.PRETENDING.getValue(), RobotFace.LAZY.getValue(), RobotFace.AWARE_RIGHT.getValue(),
            RobotFace.TIRED.getValue(), RobotFace.SHY.getValue(), RobotFace.INNOCENT.getValue(), RobotFace.SINGING.getValue(), RobotFace.AWARE_LEFT.getValue(),
            RobotFace.DEFAULT_STILL.getValue()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_synctime);

        mService = ServerActivity.getService();
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        initView();
    }

    private void initView() {
        openAppAtTime = (Button) findViewById(R.id.openAppAtTime);
        stopApp = (Button) findViewById(R.id.stopApp);
        requestSyncTime = (Button) findViewById(R.id.requestSyncTime);
        setAppId = (Button) findViewById(R.id.setAppId);
        openFace = (Button) findViewById(R.id.openFace);
        hideFace = (Button) findViewById(R.id.hideFace);
        broadcastEvent = (Button) findViewById(R.id.btnBroadcast);

        mEnableVoiceTriggerBtn = (Button) findViewById(R.id.btnEnableVoiceTrigger);
        mDisableVoiceTriggerBtn = (Button) findViewById(R.id.btnDisableVoiceTrigger);

        time = (TextView) findViewById(R.id.time);
        date = (TextView) findViewById(R.id.date);
        tvAppId = (TextView) findViewById(R.id.tvAppId);
        buttonClickTime = (TextView) findViewById(R.id.tv_button_time);
        buttonTotalTimer = (TextView) findViewById(R.id.tv_total_time);
        float time = BaseMethod.messageMaxDelayTime / 1000;
        buttonTotalTimer.setText(String.format("%.1f", time)+"s");

        hourPicker = (MaterialNumberPicker) findViewById(R.id.hourPicker);
        minutePicker = (MaterialNumberPicker) findViewById(R.id.minutePicker);
        secondPicker = (MaterialNumberPicker) findViewById(R.id.secondPicker);
        readyWaitPicker = (MaterialNumberPicker) findViewById(R.id.readyWaitPicker);

        openAppAtTime.setOnClickListener(OnClickListener);
        stopApp.setOnClickListener(OnClickListener);
        requestSyncTime.setOnClickListener(OnClickListener);
        openFace.setOnClickListener(OnClickListener);
        hideFace.setOnClickListener(OnClickListener);

        mEnableVoiceTriggerBtn.setOnClickListener(OnClickListener);
        mDisableVoiceTriggerBtn.setOnClickListener(OnClickListener);


        broadcastEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServerSyncTimeActivity.this, BroadcastEventActivity.class);
                startActivity(intent);
            }
        });

        setAppId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ServerSyncTimeActivity.this, AppIdActivity.class);
                intent.putStringArrayListExtra("appId", appId);
                startActivityForResult(intent, 1);
            }
        });

        hourPicker.setMaxValue(23);
        hourPicker.setMinValue(0);

        minutePicker.setMaxValue(59);
        minutePicker.setMinValue(0);

        secondPicker.setMaxValue(11);
        secondPicker.setMinValue(0);

        String[] secondValues = new String[12];

        for (int i = 0; i < 60 / 5; i++) {
            secondValues[i] = String.valueOf(i * 5);
        }

        secondPicker.setDisplayedValues(secondValues);

        readyWaitPicker.setMaxValue(60);
        readyWaitPicker.setMinValue(3);
        readyWaitPicker.setValue(40);

        appId = new ArrayList<String>();
        appId.add("556611");
        appId.add("556622");
        appId.add("556633");

        tvAppId.setText("FirstId: " + appId.get(0) + "\nSecondId: " + appId.get(1) + "\nThirdId: " + appId.get(2));
    }

    View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Vibrator mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
            mVibrator.vibrate(100);

            mSendHandler.removeCallbacksAndMessages(null);
            mSendHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (v.getId()) {
                        case R.id.openAppAtTime:

                            final Calendar calendar = Calendar.getInstance();

                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int pickerHour = hourPicker.getValue();

                            int minute = calendar.get(Calendar.MINUTE);
                            int pickerMinute = minutePicker.getValue();

                            int pickerSecond = secondPicker.getValue() * 5;

                            Log.e(TAG, "pickerSecond " + pickerSecond);

                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.set(Calendar.HOUR_OF_DAY, pickerHour);
                            calendar.set(Calendar.MINUTE, pickerMinute);
                            calendar.set(Calendar.SECOND, pickerSecond);

//                            if (pickerHour < hour || (pickerHour == hour && pickerMinute < minute)) {
//                                calendar.add(Calendar.DATE, 1);
//                            }

                            if (calendar.getTimeInMillis() - System.currentTimeMillis() >= (readyWaitPicker.getValue() + 15) * 1000) {
                                if (appId != null && appId.size() > 0) {
                                    callButtonCountDown();
                                    mService.openAppAtTime(appId, calendar.getTimeInMillis(), readyWaitPicker.getValue());
                                } else {
                                    showToast(ServerSyncTimeActivity.this, "App id is empty", Toast.LENGTH_SHORT);
                                    return;
                                }

                                showToast(ServerSyncTimeActivity.this, "Send Open AppAtTime", Toast.LENGTH_SHORT);
                            } else {
                                showToast(ServerSyncTimeActivity.this, "Set time interval must be greater than " + (readyWaitPicker.getValue() + 15) + " seconds", Toast.LENGTH_SHORT);
                            }

                            break;
                        case R.id.stopApp:
                            mService.stopApp();
                            callButtonCountDown();
                            showToast(ServerSyncTimeActivity.this, "Send Stop", Toast.LENGTH_SHORT);
                            break;
                        case R.id.requestSyncTime:
                            mService.requestSyncTime();
                            showToast(ServerSyncTimeActivity.this, "Request SyncTime", Toast.LENGTH_SHORT);
                            break;
                        case R.id.openFace:
                            mService.setFace(true);
                            callButtonCountDown();
                            showToast(ServerSyncTimeActivity.this, "openFace", Toast.LENGTH_SHORT);
                            break;
                        case R.id.hideFace:
                            mService.setFace(false);
                            callButtonCountDown();
                            showToast(ServerSyncTimeActivity.this, "hideFace", Toast.LENGTH_SHORT);
                            break;

                        case R.id.btnEnableVoiceTrigger:
                            mService.enableVoiceTrigger();
                            callButtonCountDown();
                            break;

                        case R.id.btnDisableVoiceTrigger:
                            mService.disableVoiceTrigger();
                            callButtonCountDown();
                            break;
                    }

                }
            }, 250);
        }
    };

    public static void showToast(Context context, String toastMsg, int duration) {
        Toast toast = Toast.makeText(context, toastMsg, duration);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(30);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(new Date());
        date.setText(currentDate);

        mHandler.post(setTime);

        buttonClickTime.setText("0.0 s");

        Calendar calendar = Calendar.getInstance();
        if (mService != null && mService.getTimeInMillis() > 0) {
            calendar.setTimeInMillis(mService.getTimeInMillis());
            hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
            minutePicker.setValue(calendar.get(Calendar.MINUTE));
            secondPicker.setValue(calendar.get(Calendar.SECOND) / 5);
        } else {
            hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
            minutePicker.setValue(calendar.get(Calendar.MINUTE));
            secondPicker.setValue(calendar.get(Calendar.SECOND) / 5);
        }
    }

    private Runnable setTime = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            long now = System.currentTimeMillis();
            String currentDateandTime = sdf.format(now);

            if (mService != null) {
                if (mService.getTimeInMillis() >= now) {
                    long countdownTime = (mService.getTimeInMillis() - now) / 1000;
                    currentDateandTime += "  " + countdownTime;
                    if(countdownTime <= 40){
                        openAppAtTime.setClickable(false);
                    }
                } else {
                    if(!openAppAtTime.isClickable()){
                        openAppAtTime.setClickable(true);
                    }
                }
            }

            time.setText(currentDateandTime);
            mHandler.postDelayed(setTime, 200);
        }
    };

    private void callButtonCountDown(){
        if(buttonClickRunnable != null){
            buttonClickRunnable.isNeedEnd = true;
        }

        buttonClickRunnable = new ButtonCondownRunnable(mHandler,buttonClickTime);

        mHandler.post(buttonClickRunnable);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        mHandler.removeCallbacksAndMessages(null);
        mSendHandler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Log.e(TAG, "data " + data);
            if (data != null) {
                appId = data.getStringArrayListExtra("appId");

                if (appId != null && appId.size() > 0) {
                    tvAppId.setText("FirstId: " + appId.get(0) + "\nSecondId: " + appId.get(1) + "\nThirdId: " + appId.get(2));
                }

                Log.e(TAG, "appId " + appId);
            }
        }
    }
}
