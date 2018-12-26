package com.asus.zenboControl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by Ryan_Chou on 2017/4/5.
 */

public class BaseMethod {

    public static final long messageMaxDelayTime = 3000;

    public static void checkPermission(Activity at){
        ArrayList<String> stringArrayList = new ArrayList<String>();

        CheckOnePermission(Manifest.permission.READ_EXTERNAL_STORAGE, stringArrayList, at);
        CheckOnePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, stringArrayList, at);
        CheckOnePermission(Manifest.permission.ACCESS_WIFI_STATE, stringArrayList, at);
        CheckOnePermission(Manifest.permission.CHANGE_WIFI_STATE, stringArrayList, at);
        CheckOnePermission(Manifest.permission.WAKE_LOCK, stringArrayList, at);
        CheckOnePermission(Manifest.permission.DISABLE_KEYGUARD, stringArrayList, at);
        CheckOnePermission(Manifest.permission.INTERNET, stringArrayList, at);
        CheckOnePermission(Manifest.permission.ACCESS_NETWORK_STATE, stringArrayList, at);
        CheckOnePermission(Manifest.permission.VIBRATE, stringArrayList, at);
        CheckOnePermission(Manifest.permission.RECORD_AUDIO, stringArrayList, at);

        //if you want your array
        String[] needPermission = stringArrayList.toArray(new String[stringArrayList.size()]);

        if(stringArrayList.size() > 0){
            ActivityCompat.requestPermissions(at,
                    needPermission,
                    100);
        }
    }

    private static void CheckOnePermission(String permission, ArrayList<String> list, Activity at){
        if(ContextCompat.checkSelfPermission(at,
                permission)
                != PackageManager.PERMISSION_GRANTED){
            list.add(permission);
        }
    }
}
