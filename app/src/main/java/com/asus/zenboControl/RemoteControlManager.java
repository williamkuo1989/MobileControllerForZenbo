package com.asus.zenboControl;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import com.asus.robotframework.API.MotionControl;

/**
 * Created by Ryan_Chou on 2017/9/22.
 */

public class RemoteControlManager {
    private static final String TAG = "RemoteControlManager";

    private static final int remoteControlSpaceTime = 600;
    private static long remoteClickLastTime = 0;


    public static void OnKeyDown(final Context mContext, Handler handler, final KeyEvent event){
        handler.post(new Runnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                boolean canRun = (currentTime - remoteClickLastTime > remoteControlSpaceTime);
                remoteClickLastTime = currentTime;
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            RobotAPIManager.getInstance(mContext).remoteControlMotion(MotionControl.Direction.Body.FORWARD);
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            RobotAPIManager.getInstance(mContext).remoteControlMotion(MotionControl.Direction.Body.BACKWARD);
                            break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            RobotAPIManager.getInstance(mContext).remoteControlMotion(MotionControl.Direction.Body.TURN_LEFT);
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            RobotAPIManager.getInstance(mContext).remoteControlMotion(MotionControl.Direction.Body.TURN_RIGHT);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_STOP:
                            RobotAPIManager.getInstance(mContext).remoteControlMotion(MotionControl.Direction.Body.STOP);
                            break;
                }
                if(canRun) {
                    Log.d(TAG,"key down event:"+KeyEvent.keyCodeToString(event.getKeyCode()));
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_1:
                            RobotAPIManager.getInstance(mContext).remoteControlBodyMove(0, 0, 15, MotionControl.SpeedLevel.Body.L4);
                            break;
                        case KeyEvent.KEYCODE_2:
                            RobotAPIManager.getInstance(mContext).remoteControlBodyMove(0, 0, -15, MotionControl.SpeedLevel.Body.L4);
                            break;
                        case KeyEvent.KEYCODE_4:
                            RobotAPIManager.getInstance(mContext).remoteControlBodyMove(0, 0, 30, MotionControl.SpeedLevel.Body.L4);
                            break;
                        case KeyEvent.KEYCODE_5:
                            RobotAPIManager.getInstance(mContext).remoteControlBodyMove(0, 0, -30, MotionControl.SpeedLevel.Body.L4);
                            break;
                        case KeyEvent.KEYCODE_7:
                            RobotAPIManager.getInstance(mContext).remoteControlBodyMove(0, 0, 90, MotionControl.SpeedLevel.Body.L4);
                            break;
                        case KeyEvent.KEYCODE_8:
                            RobotAPIManager.getInstance(mContext).remoteControlBodyMove(0, 0, -90, MotionControl.SpeedLevel.Body.L4);
                            break;
                        case KeyEvent.KEYCODE_3:
                            break;
                    }
                }
            }
        });
    }
}
