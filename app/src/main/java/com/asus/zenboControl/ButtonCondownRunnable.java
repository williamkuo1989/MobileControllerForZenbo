package com.asus.zenboControl;

import android.os.Handler;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import static com.asus.zenboControl.BaseMethod.messageMaxDelayTime;

/**
 * Created by Ryan_Chou on 2017/9/28.
 */

public class ButtonCondownRunnable implements Runnable{
    private long startTime = System.currentTimeMillis();
    public boolean isNeedEnd = false;
    private Handler mHandler;
    private TextView showView;


    public ButtonCondownRunnable(final Handler handler, final TextView text){
        mHandler = handler;
        showView = text;
        isNeedEnd = false;
    }

    @Override
    public void run() {
        if(!isNeedEnd) {
            long now = System.currentTimeMillis();
            long sub = messageMaxDelayTime - (now - startTime);
            if (sub > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("s.S");
                String subTime = sdf.format(sub);
                showView.setText(subTime + " s");
                mHandler.postDelayed(this, 100);
            } else {
                showView.setText("0.0 s");
            }
        }
    }
}

