package com.asus.zenboControl.Event;

/**
 * Created by Ryan_Chou on 2017/4/18.
 */

public class UpdateIPSpinnerEvent {

    public String deviceName;
    public String deviceIP;

    public UpdateIPSpinnerEvent(String name, String IP){
        deviceName = name;
        deviceIP = IP;
    }
}
