package com.asus.zenboControl.Server;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.asus.robotframework.API.RobotFace;
import com.asus.zenboControl.BaseActivity;
import com.asus.zenboControl.R;

import java.util.Random;

public class ServerControllerActivity extends BaseActivity {

    private ServerService mService;

    private ImageButton btnUp;
    private ImageButton btnDown;
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private ImageButton btnStop;
    private Button btnEnterScript;
    private Button btnLeaveScript;
    private Button btnOpenZba;

    private Button BroadCastEventBtn;
    private Button mEnableVoiceTriggerBtn;
    private Button mDisableVoiceTriggerBtn;

    private Button motionAvoidanceOpenBtn;
    private Button motionAvoidanceCloseBtn;

    private Button capSensorOpenBtn;
    private Button capSensorCloseBtn;

    private EditText etAppid;
    private BroadcastReceiver mBroadcastReceiver;
    private String ENTER_SCRIPT = "ENTER SCRIPT";
    private String LEAVE_SCRIPT = "LEAVE SCRIPT";
    private String SCRIPT_NAME = "SCRIPT_NAME";
    private String ENTER_SCRIPT_DATA = "ENTER_SCRIPT_DATA";
    private String LEAVE_SCRIPT_DATA = "LEAVE_SCRIPT_DATA";
    private String MOVEMENT_DISTANCE = "MOVEMENT_DISTANCE";
    private String ROTATION_DEGREE = "ROTATION_DEGREE";
    private String FACE_INDEX = "FACE_INDEX";
    private String LED_TYPE = "LED_TYPE";
    private String HEAD_DEGREE = "HEAD_DEGREE";
    private String scriptName = "SCRIPT NAME";
    private int distanceValue = 0;
    private int rotationValue = 0;
    private int ledIndex = 0;
    private int faceIndex = 0;
    private int headPitchDegree = 0;
    private SharedPreferences sharedPreferences;

    private Button btnOpenFace;
    private Button btnHideFace;

    private Random r;
    private int[] faceValue = {RobotFace.PROUD.getValue(), RobotFace.EXPECTING.getValue(), RobotFace.CONFIDENT.getValue(),
            RobotFace.ACTIVE.getValue(), RobotFace.PLEASED.getValue(), RobotFace.SHY.getValue(), RobotFace.SINGING.getValue()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_controller);

        mService = ServerActivity.getService();
        r =new Random();

        btnUp = (ImageButton) findViewById(R.id.upbtn);
        btnDown = (ImageButton) findViewById(R.id.downbtn);
        btnLeft = (ImageButton) findViewById(R.id.leftbtn);
        btnRight = (ImageButton) findViewById(R.id.rightbtn);
        btnStop = (ImageButton) findViewById(R.id.stopbtn);
        btnEnterScript = (Button) findViewById(R.id.enterBtn);
        btnLeaveScript = (Button) findViewById(R.id.leaveBtn);
        btnOpenZba = (Button) findViewById(R.id.btn_open_zba);

        btnOpenFace = (Button) findViewById(R.id.btn_openface);
        btnHideFace = (Button) findViewById(R.id.btn_hideface);

        mEnableVoiceTriggerBtn = (Button) findViewById(R.id.btnEnableVoiceTrigger);
        mDisableVoiceTriggerBtn = (Button) findViewById(R.id.btnDisableVoiceTrigger);
        BroadCastEventBtn =  (Button) findViewById(R.id.btn_broadcast);

        motionAvoidanceOpenBtn = (Button) findViewById(R.id.btnMotionAvoidanceOpen);
        motionAvoidanceCloseBtn = (Button) findViewById(R.id.btnMotionAvoidanceClose);

        capSensorOpenBtn = (Button) findViewById(R.id.btnCapSensorOpen);
        capSensorCloseBtn = (Button) findViewById(R.id.btnCapSensorClose);

        capSensorOpenBtn.setOnClickListener(clickListener);
        capSensorCloseBtn.setOnClickListener(clickListener);

        motionAvoidanceOpenBtn.setOnClickListener(clickListener);
        motionAvoidanceCloseBtn.setOnClickListener(clickListener);

        mEnableVoiceTriggerBtn.setOnClickListener(clickListener);
        mDisableVoiceTriggerBtn.setOnClickListener(clickListener);
        BroadCastEventBtn.setOnClickListener(clickListener);

        btnOpenFace.setOnClickListener(clickListener);
        btnHideFace.setOnClickListener(clickListener);

        etAppid = (EditText) findViewById(R.id.et_appid);
        btnOpenZba.setOnClickListener(clickListener);
        btnUp.setOnClickListener(clickListener);
        btnDown.setOnClickListener(clickListener);
        btnLeft.setOnClickListener(clickListener);
        btnRight.setOnClickListener(clickListener);
        btnStop.setOnClickListener(clickListener);
        btnEnterScript.setOnClickListener(clickListener);
        btnLeaveScript.setOnClickListener(clickListener);
        btnEnterScript.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(ServerControllerActivity.this, EditScriptActivity.class);
                intent.putExtra(SCRIPT_NAME,ENTER_SCRIPT);
                startActivity(intent);
                return false;
            }
        });
        btnLeaveScript.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(ServerControllerActivity.this, EditScriptActivity.class);
                intent.putExtra(SCRIPT_NAME,LEAVE_SCRIPT);
                startActivity(intent);
                return false;
            }
        });
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Vibrator mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
            mVibrator.vibrate(100);

            switch (v.getId()) {
                case R.id.upbtn:
                    mService.remoteControlBody("FORWARD");
                    break;
                case R.id.downbtn:
                    mService.remoteControlBody("BACKWARD");
                    break;
                case R.id.leftbtn:
                    mService.remoteControlBody("TURN_LEFT");
                    break;
                case R.id.rightbtn:
                    mService.remoteControlBody("TURN_RIGHT");
                    break;
                case R.id.stopbtn:
                    mService.remoteControlBody("STOP");
                    break;
                case R.id.enterBtn:
                    sharedPreferences = getSharedPreferences(ENTER_SCRIPT_DATA,0);
                    distanceValue = sharedPreferences.getInt(MOVEMENT_DISTANCE, 0);
                    rotationValue = sharedPreferences.getInt(ROTATION_DEGREE, 0);
                    faceIndex = faceValue[sharedPreferences.getInt(FACE_INDEX, 0)];
                    ledIndex = sharedPreferences.getInt(LED_TYPE, 0);
                    headPitchDegree = sharedPreferences.getInt(HEAD_DEGREE, 0);
                    mService.setEnterStage(distanceValue,rotationValue,faceIndex,ledIndex,headPitchDegree);
                    break;

                case R.id.leaveBtn:
                    sharedPreferences = getSharedPreferences(LEAVE_SCRIPT_DATA,0);
                    distanceValue = sharedPreferences.getInt(MOVEMENT_DISTANCE, 0);
                    rotationValue = sharedPreferences.getInt(ROTATION_DEGREE, 0);
                    faceIndex = faceValue[sharedPreferences.getInt(FACE_INDEX, 0)];
                    ledIndex = sharedPreferences.getInt(LED_TYPE, 0);
                    headPitchDegree = sharedPreferences.getInt(HEAD_DEGREE, 0);
                    mService.setLeaveStage(distanceValue,rotationValue,faceIndex,ledIndex,headPitchDegree);
                    break;
                case R.id.btn_open_zba:
                    mService.setAppIdAndOpen(etAppid.getText().toString());
                    break;
                case R.id.btn_openface:
                    mService.setOpenFace();
                    break;
                case R.id.btn_hideface:
                    mService.setHideFace();
                    break;
                case R.id.btnEnableVoiceTrigger:
                    mService.enableVoiceTrigger();
                    break;
                case R.id.btnDisableVoiceTrigger:
                    mService.disableVoiceTrigger();
                    break;
                case R.id.btn_broadcast:
                    Intent intent = new Intent(ServerControllerActivity.this, BroadcastEventActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnMotionAvoidanceOpen:
                    mService.setMotionAvoidanceStatus(true);
                    break;
                case R.id.btnMotionAvoidanceClose:
                    mService.setMotionAvoidanceStatus(false);
                    break;
                case R.id.btnCapSensorOpen:
                    mService.setCapSensorStatus(true);
                    break;
                case R.id.btnCapSensorClose:
                    mService.setCapSensorStatus(false);
                    break;

                default:
                    break;
            }
        }
    };

}
