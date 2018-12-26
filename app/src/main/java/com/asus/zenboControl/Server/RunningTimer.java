package com.asus.zenboControl.Server;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ryan_Chou on 2017/3/31.
 */

public class RunningTimer {

    private boolean isRunning = false;
    private long mStartTime;
    private TimerTask mClock;
    private TextView timerView;
    private Handler handler;
    private Timer mTimer;

    public RunningTimer(TextView tv) {
        handler = new Handler();
        timerView = tv;
        isRunning = false;
        mStartTime = 0;
        mClock = null;
        mTimer = new Timer();
    }

    public void StartTimer() {
        if (!isRunning)
        {
            mClock = new TimerTask() {
                public void run()
                {
                    if (mStartTime==0)
                    {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            mStartTime = SystemClock.uptimeMillis();
                        } else {
                            mStartTime =  System.currentTimeMillis();
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            SimpleDateFormat sdf = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                sdf = new SimpleDateFormat("mm:ss.SS");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                String timeStr = sdf.format(
                                        new Date(SystemClock.uptimeMillis()-
                                                mStartTime));
                                timerView.setText(timeStr);
                            } else {
                                long millis = System.currentTimeMillis() - mStartTime;
                                int seconds = (int) (millis / 1000);
                                int minutes = seconds / 60;
                                int m2 = (int)((millis - (seconds*1000)) / 10);
                                seconds     = seconds % 60;
                                String show;
                                if(minutes < 10){
                                    show = "0" + minutes + ":";
                                } else {
                                    show = minutes+":";
                                }

                                if(seconds < 10){
                                    show = show+"0"+seconds+".";
                                } else {
                                    show = show+seconds+".";
                                }

                                if(m2 < 10){
                                    show = show+"0"+m2;
                                } else {
                                    show = show+m2;
                                }

                                timerView.setText(show);
                            }
                        }
                    });
                }
            };
            mStartTime = 0;
            mTimer.schedule(mClock, 0, 30);
            isRunning = true;
        }
    }

    public void stopTimer(){
        mClock.cancel();
        mTimer.purge();
        isRunning = false;
    }
}

