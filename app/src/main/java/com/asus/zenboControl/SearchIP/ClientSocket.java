package com.asus.zenboControl.SearchIP;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import static com.asus.zenboControl.SearchIP.FindIPSocketManager.FindIPTAG;

/**
 * Created by Ryan_Chou on 2018/3/19.
 */

public class ClientSocket extends ListenSocketThread {
    HashSet ipHashSet;

    @Override
    public void getMessage(DatagramPacket mPacket) {
        String ipAddress = mPacket.getAddress().getHostAddress().toString();
        String message = new String(mPacket.getData(), mPacket.getOffset(), mPacket.getLength());
        Log.d(FindIPTAG, ipAddress + ":" +  message);

        if(message.equals(FindIPSocketManager.recallServerString)){
            if(ipHashSet != null) {
                ipHashSet.add(ipAddress);
                EventBus.getDefault().post(new UpdateIPEvent(ipHashSet));
            }
        }
    }

    public void send() {
        ipHashSet = new HashSet();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = FindIPSocketManager.askIPString ;
                DatagramSocket s = null;
                try {
                    s = new DatagramSocket();
                } catch (SocketException e) {
                    e.printStackTrace();
                }


                InetAddress local = null;
                try {
                    local = InetAddress.getByName(WifiStatusManager.getBroadcastIP());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                int msg_length = message.length();
                byte[] messageByte = message.getBytes();
                DatagramPacket p = new DatagramPacket(messageByte, msg_length, local,
                        FindIPSocketManager.port);
                try {
                    s.send(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
