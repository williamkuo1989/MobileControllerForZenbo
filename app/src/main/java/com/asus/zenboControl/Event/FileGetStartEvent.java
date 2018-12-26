package com.asus.zenboControl.Event;

/**
 * Created by Ryan_Chou on 2017/4/6.
 */

public class FileGetStartEvent {
    public int filesize;

    public FileGetStartEvent(int size){
        filesize = size;
    }
}
