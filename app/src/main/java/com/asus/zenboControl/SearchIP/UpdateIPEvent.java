package com.asus.zenboControl.SearchIP;

import java.util.HashSet;

/**
 * Created by Ryan_Chou on 2018/3/19.
 */

public class UpdateIPEvent {
    private HashSet ipSet;

    public UpdateIPEvent(HashSet ips){
        ipSet = ips;
    }

    public HashSet getIpSet(){
        return ipSet;
    }
}
