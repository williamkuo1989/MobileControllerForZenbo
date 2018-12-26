package com.asus.zenboControl.Event;

public class ConnectStatusEvent {

    private boolean isConnected;

    public void setConnectStatus(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean getConnectStatus() {
        return isConnected;
    }
}
