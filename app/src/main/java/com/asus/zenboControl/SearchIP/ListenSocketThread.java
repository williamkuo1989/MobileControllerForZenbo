package com.asus.zenboControl.SearchIP;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import static com.asus.zenboControl.SearchIP.FindIPSocketManager.FindIPTAG;


/**
 * Created by Ryan_Chou on 2018/3/19.
 */

public class ListenSocketThread {

    private Thread mThread;
    private DatagramSocket mSocket;
    private DatagramPacket mPacket;
    byte[] message = new byte[1024];

    public void startSocketRun(int port){
        try {
            if (mSocket == null) {
                mSocket = new DatagramSocket(null);
                mSocket.setReuseAddress(true);
                mSocket.setBroadcast(true);
                mSocket.bind(new InetSocketAddress(port));
            }
            mPacket = new DatagramPacket(message, message.length);
            mThread = new Thread(runnable);
            mThread.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void stopSocketRun(){
        if (mSocket != null) {
            if (!mSocket.isClosed()){
                mSocket.close();
            }

            mSocket.disconnect();
            mSocket = null;
            mPacket = null;
        }

        if(mThread != null){
            mThread.interrupt();
            mThread = null;
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(mSocket != null && mPacket != null){
                Log.d(FindIPTAG, "socket server is open");
                try {
                    while (!mSocket.isClosed()) {
                        mSocket.receive(mPacket);
                        getMessage(mPacket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void getMessage(DatagramPacket mPacket){
        Log.d(FindIPTAG, mPacket.getAddress()
                .getHostAddress().toString()
                + ":" +  new String(mPacket.getData(), mPacket.getOffset(), mPacket.getLength()));
    }
}
