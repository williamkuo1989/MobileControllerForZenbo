package com.asus.zenboControl.Server;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.zenboControl.BaseActivity;
import com.asus.zenboControl.BaseMethod;
import com.asus.zenboControl.EditTextWithBackButton;
import com.asus.zenboControl.Event.SendFileEvent;
import com.asus.zenboControl.R;
import com.asus.zenboControl.RecordAudio;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class ServerTalkingActivity extends BaseActivity implements EditTextWithBackButton.IOnBackButtonListener {

    RunningTimer runningTimer;
    RecordAudio recordAudio;
    private Handler mSendHandler = new Handler();
    private ServerService mService;
    private ProgressDialog sendProgressDialog;

    private final String TAG = "Server_Talking";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_talking);
        BaseMethod.checkPermission(this);

        mService = ServerActivity.getService();


        TextView tv_time = (TextView) findViewById(R.id.tv_timer);
        runningTimer = new RunningTimer(tv_time);
        recordAudio = new RecordAudio();

        recordAudio.setPlayRecordEndCallback(new RecordAudio.PlayRecordEndCallback() {
            @Override
            public void end() {
                runningTimer.stopTimer();
            }
        });

        addClickListener(R.id.btn_playRecord);
        addClickListener(R.id.btn_send);

        findViewById(R.id.btn_recordAndSend).setOnTouchListener(onTouchListener);
		
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    runningTimer.StartTimer();
                    ((Button)view).setText("RECORDING");
                    recordAudio.startRecording();
                    break;
                case MotionEvent.ACTION_UP:
                    ((Button)view).setText("RECORD AND SEND");
                    recordAudio.stopRecording();
                    runningTimer.stopTimer();
                    mService.sendFile(RecordAudio.recodePCMfilesrc);
                    break;
            }

            return false;
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_playRecord:
                    if(runningTimer != null && recordAudio != null){
                        runningTimer.StartTimer();
                        recordAudio.playRecording();
                    }
                    break;
                case R.id.btn_send:
                    mService.sendFile(RecordAudio.recodePCMfilesrc);
                    break;
            }
        }
    };


    public void addClickListener(int vid){
        View v = findViewById(vid);
        if(v != null)
            v.setOnClickListener(onClickListener);
    }


    @Override
    public boolean OnEditTextBackButton() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        mSendHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SendFileEvent event){

        switch (event.mStatus) {
            case SendFileEvent.ONSTART:
                Log.e("[Client Size]", "SendFileEvent on start");
                sendProgressDialog=new ProgressDialog(this);

                sendProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                sendProgressDialog.setTitle("File Send");
                sendProgressDialog.setMax(event.fileTotalCapacity);
                sendProgressDialog.setProgress(0);
                sendProgressDialog.setIndeterminate(false);
                sendProgressDialog.setCancelable(false);
                sendProgressDialog.setCanceledOnTouchOutside(true);
                sendProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.e(TAG, "sendProgressDialog on cancel");
                        mService.stopSendFile();
                    }
                });
                sendProgressDialog.show();
                break;
            case SendFileEvent.ONREQUEST:
                Log.e(TAG, "SendFileEvent on request");
                sendProgressDialog.setProgress(event.requestFileCapacity);
                break;
            case SendFileEvent.ONEND:
                Log.e(TAG, "SendFileEvent on end");
                sendProgressDialog.dismiss();
                break;
        }
    }
}
