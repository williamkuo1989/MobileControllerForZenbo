package com.asus.zenboControl.Client;

/**
 * Created by Ryan_Chou on 2017/9/26.
 */

public class ClientMessageDelayTooLongEvent {
    public long delayTime;

    public ClientMessageDelayTooLongEvent(long time){
        delayTime = time;
    }
}
