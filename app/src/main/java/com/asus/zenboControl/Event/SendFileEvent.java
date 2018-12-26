package com.asus.zenboControl.Event;

/**
 * Created by Ryan_Chou on 2017/4/7.
 */

public class SendFileEvent {
    public static final int ONSTART = 0;
    public static final int ONREQUEST = 1;
    public static final int ONEND = 2;

    public int mStatus;
    public int fileTotalCapacity;
    public int requestFileCapacity;

    public SendFileEvent(int status){
        mStatus = status;
    }

    public void setFileTotalCapacity(int size){
        fileTotalCapacity = size;
    }

    public void setRequestFileCapacity(int size){
        requestFileCapacity = size;
    }
}
