package com.asus.zenboControl.Client;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asus.robotframework.API.RobotFace;
import com.asus.zenboControl.EditTextWithBackButton;
import com.asus.zenboControl.Event.ConnectStatusEvent;
import com.asus.zenboControl.Event.FaceEvent;
import com.asus.zenboControl.Event.FileGetDoingEvent;
import com.asus.zenboControl.Event.FileGetEndEvent;
import com.asus.zenboControl.Event.FileGetStartEvent;
import com.asus.zenboControl.Event.SyncTimeEvent;
import com.asus.zenboControl.Event.UpdateMotionAvoidanceStatusEvent;
import com.asus.zenboControl.FullScreenActivity;
import com.asus.zenboControl.IntentionModule;
import com.asus.zenboControl.R;
import com.asus.zenboControl.SearchIP.FindIPSocketManager;
import com.asus.zenboControl.SearchIP.UpdateIPEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

import static com.asus.zenboControl.Provider.StatusDataEditor.getMotionAvoidanceStatus;
import static com.asus.zenboControl.R.id.spinner;


public class ClientActivity extends FullScreenActivity implements EditTextWithBackButton.IOnBackButtonListener {
    private String TAG = getClass().getSimpleName();
//    private String TAG = getClass().getSimpleName();

    private ClientService mService;
    private Button connect;
    private Button openFace;

    private TextView connectStatus;
    private TextView wifiStatus;

    private TextView time;
    private TextView date;
    private TextView tvNtpTime;

    private IntentionModule mIntentionModule;

    private RelativeLayout rootView;

    private Handler mHandler = new Handler();
    private Handler mSendHandler = new Handler();

    private boolean isShowExpression;
    private BroadcastReceiver mBroadcastReceiver;

    private static ClientActivity mActivity;
    ProgressDialog mypDialog;
    private Runnable sendRunnable;
    private Hashtable<String, String> IPList;
    private ArrayAdapter<String> noIPAdapter;

    private String[] row = {"1 Row", "2 Row", "3 Row"};
    private Spinner spinnerRow;

    private Spinner mSpinner;

    private ImageButton mIpEnterChange;
    private boolean isIPEnterByKeyboard;

    private EditText mEditIP;
    private TextView MotionStatusTextView;
    private ImageView warningImage;
    private FindIPSocketManager mFindIPSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_client);
        mBroadcastReceiver = new WifiBroadcastReceiver();
        registerReceiver();
        mActivity = ClientActivity.this;

        rootView = (RelativeLayout) findViewById(R.id.activity_clinet);

        mIntentionModule = IntentionModule.getInstance(ClientActivity.this);
        mIntentionModule.disableSound();

        TextView tv_version = (TextView) findViewById(R.id.tv_api_version);
        tv_version.setText("RobotAPI Version:" + mIntentionModule.getAPIVersion());

        MotionStatusTextView = (TextView) findViewById(R.id.tv_motionStatus);
        warningImage = (ImageView) findViewById(R.id.iv_warning);

        updateMotionAvoidanceStatus();

        mEditIP = (EditText) findViewById(R.id.ip_edit);
        mIpEnterChange = (ImageButton) findViewById(R.id.btn_ip_enter_control);
        mIpEnterChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isIPEnterByKeyboard){
                    if(mSpinner != null){
                        mSpinner.setVisibility(View.VISIBLE);
                    }
                    if(mEditIP != null){
                        mEditIP.setVisibility(View.GONE);
                    }
                    isIPEnterByKeyboard = false;
                } else {
                    if(mSpinner != null){
                        mSpinner.setVisibility(View.GONE);
                    }
                    if(mEditIP != null){
                        mEditIP.setVisibility(View.VISIBLE);
                    }
                    isIPEnterByKeyboard = true;
                }
            }
        });

        connect = (Button) findViewById(R.id.connect);
        connectStatus = (TextView) findViewById(R.id.connectStatus);

        connect.setEnabled(false);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendRunnable != null)
                    mSendHandler.removeCallbacks(sendRunnable);
                sendRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String ip = "";

                        if(isIPEnterByKeyboard){
                            if(mEditIP != null && mEditIP.length() > 0){
                                ip = mEditIP.getText().toString();
                            }
                        } else {
                            if (mSpinner != null) {
                                ip = (String) mSpinner.getSelectedItem();
                            }
                        }

                        if (ip != null && ip.length() > 0) {
                            String name = IPList.get(ip);
                            Log.d("[Link]", "name:" + name + " ip:" + ip);
                            mService.connectServer(ip);
                            Toast.makeText(ClientActivity.this, "Connecting", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                mSendHandler.postDelayed(sendRunnable, 250);
            }
        });

        openFace = (Button) findViewById(R.id.openFace);
        openFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExpression(true);
            }
        });

        mFindIPSocketManager = new FindIPSocketManager(this);
        mFindIPSocketManager.sendAskIPMessage();

        mSpinner = (Spinner) findViewById(spinner);
        String[] array = {"Device not found"};
        noIPAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_ip, array);
        mSpinner.setAdapter(noIPAdapter);

        addClickListener(R.id.btn_search);
        IPList = new Hashtable<String, String>();

        spinnerRow = (Spinner) findViewById(R.id.spinnerAppRow);
        final ArrayAdapter<String> lunchList = new ArrayAdapter<>(this, R.layout.spinner_item, row);
        spinnerRow.setAdapter(lunchList);
        spinnerRow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (mService != null) {
                    mService.setAppIdPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        wifiStatus = (TextView) findViewById(R.id.wifiStatus);
        time = (TextView) findViewById(R.id.time);
        date = (TextView) findViewById(R.id.date);
        tvNtpTime = (TextView) findViewById(R.id.ntpTime);

        startService(new Intent(this, ClientService.class));
        getAppIntent(getIntent());
    }

    public void addClickListener(int vid) {
        View v = findViewById(vid);
        if (v != null)
            v.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_search:
                    IPList = new Hashtable<String, String>();
                    mSpinner.setAdapter(noIPAdapter);
                    mFindIPSocketManager.sendAskIPMessage();
                    break;
            }
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e(TAG, "onWindowFocusChanged " + hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume " + mService);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(new Date());
        date.setText(currentDate);

        mHandler.post(setTime);

        if (mIntentionModule != null) {
            mIntentionModule.registerListenRobotAPICallback();
        }

        getWifiInfor();

    }

    private void getWifiInfor(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null) {
            Log.e(TAG, "getWifiInfor info.isConnected() " + info.isConnected() + " info.isAvailable() " + info.isAvailable());
            Log.d(TAG, "getWifiInfor info.isConnectedOrConnecting() " + info.isConnectedOrConnecting() );
            if (info.isConnected()) {
                wifiStatus.setTextColor(Color.WHITE);
                wifiStatus.setText("Wifi Connected");
            } else {
                wifiStatus.setTextColor(Color.RED);
                wifiStatus.setText("Wifi Not Connected");
            }
        } else {
            Log.e(TAG, "onResume info " + info);
            wifiStatus.setTextColor(Color.RED);
            wifiStatus.setText("Wifi Not Connected");
        }

        if (mService != null) {
            setConnectStatus(mService.getConnectStatus());
        } else {
            doBindService();
        }
    }

    private Runnable setTime = new Runnable() {
        @Override
        public void run() {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String currentDateandTime = null;

            if (mService != null) {
                if (mService.getNtpTime() > 0) {
                    long now = mService.getDiffNtpTime() + SystemClock.elapsedRealtime();
                    currentDateandTime = sdf.format(now);
                }
            }

            if (currentDateandTime == null) {
                currentDateandTime = sdf.format(System.currentTimeMillis());
            }

            time.setText(currentDateandTime);
            mHandler.postDelayed(setTime, 200);
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mService != null) {
            getAppIntent(intent);
        }
    }

    private void registerReceiver() {
        IntentFilter filters = new IntentFilter();
        filters.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, filters);
    }

    private class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG, "intent " + intent);

            if (intent.getAction().equals(android.net.ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    Log.e(TAG, "info.isConnected() " + info.isConnected() + " info.isAvailable() " + info.isAvailable());
                    if (info.isConnected()) {
                        wifiStatus.setTextColor(Color.WHITE);
                        wifiStatus.setText("Wifi Connected");
                    } else {
                        wifiStatus.setTextColor(Color.RED);
                        wifiStatus.setText("Wifi Not Connected");
                    }
                } else {
                    Log.e(TAG, "info " + info);
                    wifiStatus.setTextColor(Color.RED);
                    wifiStatus.setText("Wifi Not Connected");
                }
            }
        }
    }

    private void getAppIntent(Intent intent) {
        if (intent != null) {
            final String appId = intent.getStringExtra("appId");
            final long timeInMillis = intent.getLongExtra("timeInMillis", -1);
            final long ntpTime = intent.getLongExtra("ntpTime", 0);
            final long readyWaitTime = intent.getLongExtra("readyWaitTime" , 0);

            Log.e(TAG, "timeInMillis " + timeInMillis + " ntpTime " + ntpTime + " " + intent.getAction());

            if (timeInMillis != -1) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String getTimeInMillis = sdf.format(timeInMillis);
                tvNtpTime.setText("Open:" + getTimeInMillis);
            }

            if ("com.asus.control.action.openAppAtTime".equals(intent.getAction()) || "com.asus.control.action.openApp".equals(intent.getAction())) {
                Log.e(TAG, "startActivity ");
                try {
//                    mService.openZbaFromAppId(appId);
                    Intent sendIntent = new Intent();
                    sendIntent.setAction("com.asus.robot.appbuilder.action.PLAY");
                    sendIntent.putExtra("appId", appId);
                    sendIntent.putExtra("timeInMillis", timeInMillis);
                    sendIntent.putExtra("ntpTime", ntpTime);
                    sendIntent.putExtra("appReplay", false);
                    startActivity(sendIntent);
                    overridePendingTransition(0, 0);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        mHandler.removeCallbacksAndMessages(null);
        mSendHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        if(mFindIPSocketManager != null){
            mFindIPSocketManager.stopClient();
        }
        if(mIntentionModule != null){
            mIntentionModule.onDestroy();
        }
        super.onDestroy();
    }

    private void setConnectStatus(boolean isConnect) {

        if (isConnect) {
            connectStatus.setTextColor(Color.WHITE);
            connectStatus.setText("Server Connected");
            View view = ClientActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        } else {
            connectStatus.setTextColor(Color.RED);
            connectStatus.setText("Server Not Connected");
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ClientService.ServerIBinder binder = (ClientService.ServerIBinder) service;
            mService = binder.getService();
            mService.setAppIdPosition(spinnerRow.getSelectedItemPosition());
//            mService.openWifiThread(ClientActivity.this);
            connect.setEnabled(true);
            setConnectStatus(mService.getConnectStatus());

            if(mIntentionModule != null){
                mIntentionModule.registerListenRobotAPICallback();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
        }
    };

    private void doBindService() {
        Log.i("bind", "begin to bind");
        bindService(new Intent(this, ClientService.class), mConnection, Context.BIND_AUTO_CREATE);
    }


    private void setExpression(boolean isShow) {

        isShowExpression = isShow;

        if (isShow) {
            mIntentionModule.setExpression(RobotFace.DEFAULT);
        } else {
            mIntentionModule.hideEmotion();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConnectStatusEvent event) {
        Log.e(TAG, "onEvent ConnectStatusEvent " + event.getConnectStatus());
        setConnectStatus(event.getConnectStatus());
    }


    @Override
    public void onBackPressed() {
        if (isShowExpression) {
            setExpression(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean OnEditTextBackButton() {
        setFullScreen();
        return false;
    }

    public static ClientActivity getClientActivity(){
        if(mActivity!= null && !mActivity.isDestroyed() && !mActivity.isFinishing())
            return mActivity;

        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateIPEvent event) {
        Set<String>name = event.getIpSet();
        if(!name.isEmpty()){
            ArrayAdapter adapter = new ArrayAdapter(ClientActivity.this, R.layout.spinner_item_ip, name.toArray());
            mSpinner.setAdapter(adapter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FileGetEndEvent event) {
        mypDialog.dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FileGetStartEvent event) {
        mypDialog=new ProgressDialog(this);

        mypDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mypDialog.setTitle("File Send");
        mypDialog.setMax(event.filesize);
        mypDialog.setProgress(0);
        mypDialog.setIndeterminate(false);
        mypDialog.setCancelable(false);
        mypDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FileGetDoingEvent event) {
        if(mypDialog != null){
            mypDialog.setProgress(event.filesize);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SyncTimeEvent event) {
        getWifiInfor();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String getNtpTime = sdf.format(mService.getNtpTime());
        tvNtpTime.setText("Sync:" + getNtpTime);

        final int index = spinnerRow.getSelectedItemPosition();

        ClientService.rowNumber = index;
    }

    @Subscribe
    public void onEvent(FaceEvent event) {
        Log.e(TAG, "onEvent FaceEvent " + event.getFaceStatus());
        isShowExpression = event.getFaceStatus();
    }

    @Subscribe
    public void onEvent(ClientMessageDelayTooLongEvent event) {
        final long delaytime = event.delayTime;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ClientActivity.this, "Message Delay too long, time:"+delaytime, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Subscribe
    public void onEvent(UpdateMotionAvoidanceStatusEvent event){
        updateMotionAvoidanceStatus();
    }

    public void updateMotionAvoidanceStatus(){
        if(MotionStatusTextView == null)
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(getMotionAvoidanceStatus(getApplicationContext())){
                    MotionStatusTextView.setText(getString(R.string.motionAvoidanceOpen));
                    MotionStatusTextView.setTextColor(Color.WHITE);
                    warningImage.setVisibility(View.GONE);
                } else {
                    MotionStatusTextView.setText(getString(R.string.motionAvoidanceClose));
                    MotionStatusTextView.setTextColor(Color.RED);
                    warningImage.setVisibility(View.VISIBLE);
                }
            }
        });

    }

//    @Override
//    public boolean onKeyDown(int keyCode, final KeyEvent event) {
//        RemoteControlManager.OnKeyDown(this, new Handler(), event);
//        return super.onKeyDown(keyCode, event);
//    }
}
