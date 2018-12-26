package com.asus.zenboControl.Server;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asus.zenboControl.BaseActivity;
import com.asus.zenboControl.Client.ClientConnectedEvent;
import com.asus.zenboControl.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerActivity extends BaseActivity {
    private static ServerActivity mServerActivity;
    private String TAG = getClass().getSimpleName();

    private TextView ipAddress;
    private ServerService mService;
    private BroadcastReceiver mBroadcastReceiver;
    private Handler mHandler = new Handler();
    private Handler mSendHandler = new Handler();

    private boolean isserverConnect = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        mServerActivity = this;

        mBroadcastReceiver = new WifiBroadcastReceiver();
        registerReceiver();
        initView();

        startService(new Intent(this, ServerService.class));
        doBindService();

    }

    private void initView() {
        addClickListener(R.id.btn_case1);
        addClickListener(R.id.btn_case2);
        addClickListener(R.id.btn_case3);

        ipAddress = (TextView) findViewById(R.id.ipAddress);
    }

    public void addClickListener(int vid){
        View v = findViewById(vid);
        if(v != null)
            v.setOnClickListener(clickListener);
    }

    public static ServerService getService(){
        if(mServerActivity != null) {
            if(mServerActivity.mService != null)
             return mServerActivity.mService;
        }

        return null;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Vibrator mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
            mVibrator.vibrate(100);

            mSendHandler.removeCallbacksAndMessages(null);
            mSendHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (v.getId()) {
                        case R.id.btn_case1:
                            if(mService != null){
                                Intent intent = new Intent(ServerActivity.this, ServerTalkingActivity.class);
                                mServerActivity.startActivity(intent);
                            }
                            break;
                        case R.id.btn_case2:
                            if(mService != null){
                                Intent intent = new Intent(ServerActivity.this, ServerControllerActivity.class);
                                mServerActivity.startActivity(intent);
                            }
                            break;
                        case R.id.btn_case3:
                            if(mService != null){
                                Intent intent = new Intent(ServerActivity.this, ServerSyncTimeActivity.class);
                                mServerActivity.startActivity(intent);
                            }
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

        ipAddress.setText(getIpAddress());
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null) {
            Log.e(TAG, "onResume info.isConnected() " + info.isConnected() + " info.isAvailable() " + info.isAvailable());
            if (info.isConnected()) {
                ipAddress.setText(getIpAddress());
            } else {

            }
        } else {
            Log.e(TAG, "onResume info");
        }
    }

    private void registerReceiver() {
        IntentFilter filters = new IntentFilter();
        filters.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver, filters);
    }

    private class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG, "intent " + intent);

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    Log.e(TAG, "info.isConnected() " + info.isConnected() + " info.isAvailable() " + info.isAvailable());
                    if (info.isConnected()) {
                        ipAddress.setText(getIpAddress());
                    }
                }
            }
        }
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
        super.onDestroy();
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server Ip: " + inetAddress.getHostAddress() + "\n";
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServerService.ServerIBinder binder = (ServerService.ServerIBinder) service;
            mService = binder.getService();
            isserverConnect = true;
            mService.setClientStatus();
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private void doBindService() {
        Log.i("bind", "begin to bind");
        bindService(new Intent(this, ServerService.class), mConnection, Context.BIND_AUTO_CREATE);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClientConnectedEvent event) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
