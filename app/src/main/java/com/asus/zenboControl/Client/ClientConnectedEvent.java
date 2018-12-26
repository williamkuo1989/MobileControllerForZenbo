package com.asus.zenboControl.Client;

import java.util.ArrayList;

public class ClientConnectedEvent {

    private int clientConnectedCount;
    private ArrayList<String> list;

    public void setClientConnectedCount(int Count) {
        clientConnectedCount = Count;
    }

    public int getClientConnectedCount() {
        return clientConnectedCount;
    }

    public void setClientList(ArrayList<String> list) {
        this.list = list;
    }

}
