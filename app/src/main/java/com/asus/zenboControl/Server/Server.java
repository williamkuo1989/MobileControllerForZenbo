package com.asus.zenboControl.Server;

import android.util.Log;

import com.asus.zenboControl.Client.ClientConnectedEvent;
import com.asus.zenboControl.Event.SendFileEvent;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Server extends WebSocketServer {

    private String TAG = getClass().getSimpleName();
    private OnServerConnectListener mOnServerConnectListener;

    private HashMap<String, WebSocket> clientHashMap = new HashMap<String, WebSocket>();
    private HashMap<String, String> clientNameMap = new HashMap<String, String>();
    private WebSocket clientName;


    public interface OnServerConnectListener {

    }

    public Server(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public void setOnServerConnectListener(OnServerConnectListener listener) {
        mOnServerConnectListener = listener;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        Log.d(TAG, conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");

        ClientConnectedEvent event = new ClientConnectedEvent();
        Collection<WebSocket> con = connections();
        event.setClientConnectedCount(con.size());
        EventBus.getDefault().post(event);


        String message = "from "
                + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":"
                + conn.getRemoteSocketAddress().getPort() + "\n";

        Log.d(TAG, "onOpen :" + message);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d(TAG, "onClose " + " code " + code + " reason " + reason + " remote " + remote);
        setClientStatus();
    }

    public void setClientStatus() {
        ClientConnectedEvent event = new ClientConnectedEvent();
        Collection<WebSocket> con = connections();

        HashMap<String, String> tempClientNameMap = (HashMap<String, String>) clientNameMap.clone();

        clientHashMap.clear();
        clientNameMap.clear();

        if(!con.isEmpty()) {
            for (WebSocket c : con) {
                Log.d(TAG, "onClose " + c.getRemoteSocketAddress().getAddress().getHostAddress() + " " + tempClientNameMap.get(c.getRemoteSocketAddress().getAddress().getHostAddress()));

                String clientName = tempClientNameMap.get(c.getRemoteSocketAddress().getAddress().getHostAddress());
                if (clientName != null) {
                    clientHashMap.put(clientName, c);
                    clientNameMap.put(c.getRemoteSocketAddress().getAddress().getHostAddress(), clientName);
                }
            }
        }

        ArrayList<String> list = new ArrayList<String>(clientHashMap.keySet());
        event.setClientList(list);
        event.setClientConnectedCount(con.size());
        EventBus.getDefault().post(event);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        final long receiveTime = System.currentTimeMillis();

        Log.d(TAG, "onMessage " + conn + ": " + message);

        try {
            JSONObject json = new JSONObject(message);
            String status = json.getString("status");

            switch (status) {

                case "start":

                    break;
                case "clientName":
                    String clientName = json.getString("clientName");

                    ClientConnectedEvent event = new ClientConnectedEvent();
                    Collection<WebSocket> con = connections();

                    clientHashMap.remove(clientNameMap.get(conn.getRemoteSocketAddress().getAddress().getHostAddress()));
                    clientHashMap.put(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " " + clientName, conn);
                    clientNameMap.put(conn.getRemoteSocketAddress().getAddress().getHostAddress(), conn.getRemoteSocketAddress().getAddress().getHostAddress() + " " + clientName);

                    ArrayList<String> list = new ArrayList<String>(clientHashMap.keySet());
                    event.setClientList(list);
                    event.setClientConnectedCount(con.size());
                    EventBus.getDefault().post(event);

                    break;
                case "syncTime":

                    final long originateTime = json.getLong("requestTime");
                    final long transmitTime = System.currentTimeMillis();

                    try {
                        JSONObject request = new JSONObject();
                        request.put("status", "syncTime");
                        request.put("originateTime", originateTime);
                        request.put("receiveTime", receiveTime);
                        request.put("transmitTime", transmitTime);

                        JSONObject sendJson = new JSONObject();
                        sendJson.put("needCheckTime", false);
                        sendJson.put("sendTime", transmitTime);
                        sendJson.put("message", request.toString());
                        conn.send(sendJson.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "request":
                    int filesize = json.getInt("filesize");

                    SendFileEvent eventSendFile = new SendFileEvent(SendFileEvent.ONREQUEST);
                    eventSendFile.setRequestFileCapacity(filesize);

                    EventBus.getDefault().post(eventSendFile);
                    break;
                case "sendend":
                    SendFileEvent mEndeventSendFile = new SendFileEvent(SendFileEvent.ONEND);
                    EventBus.getDefault().post(mEndeventSendFile);
                	break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        parseJSON(conn, message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer blob) {

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.d(TAG, "onError " + ex);
        setClientStatus();
    }

    public void sendToAll(String text) {
        Collection<WebSocket> con = connections();

        JSONObject sendJson = new JSONObject();
        final long sendTime = System.currentTimeMillis();
        try {
            sendJson.put("needCheckTime", true);
            sendJson.put("sendTime", sendTime);
            sendJson.put("message", text);

            synchronized (con) {
                for (WebSocket c : con) {
                    c.send(sendJson.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendToAllNoCheckTime(String text){
        Collection<WebSocket> con = connections();

        JSONObject sendJson = new JSONObject();
        final long sendTime = System.currentTimeMillis();
        try {
            sendJson.put("needCheckTime", false);
            sendJson.put("sendTime", sendTime);
            sendJson.put("message", text);
            synchronized (con) {
                for (WebSocket c : con) {
                    c.send(sendJson.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendToAll(byte[] text) {
        Collection<WebSocket> con = connections();

        synchronized (con) {
            for (WebSocket c : con) {
                c.send(text);
            }
        }
    }

    public void setControlClient(String clientName) {
        this.clientName = clientHashMap.get(clientName);

        Log.e(TAG, "setControlClient clientName " + clientName + " " + this.clientName);
    }

    public void setOpenFace(){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setOpenFace");
            this.sendToAll(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setHideFace(){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setHideFace");
            this.sendToAll(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void remoteControlBody(String status){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "remoteControl");
            request.put("information", status);
            this.sendToAll(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {

        try {
            this.stop();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String src){
        File file = new File(src);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {

            SendFileEvent mStartevent = new SendFileEvent(SendFileEvent.ONSTART);
            mStartevent.setFileTotalCapacity(size);
            EventBus.getDefault().post(mStartevent);

            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(size > 0){
            try {
                JSONObject request = new JSONObject();
                request.put("status", "pcmFile");
                request.put("size", size);
                this.sendToAll(request.toString());

                this.sendToAll(bytes);

                JSONObject requestend = new JSONObject();
                requestend.put("status", "pcmFileEnd");
                this.sendToAll(requestend.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopSendFile() {
        try {
            JSONObject request = new JSONObject();
            request.put("status", "stopSendFile");
            this.sendToAll(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setEnterStage(int dis, int rotationDegree, int face, int LEDType, int headDegree){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setMotionOnStage");
            request.put("information", "enter");
            request.put("distance", dis);
            request.put("rotationDegree", rotationDegree);
            request.put("face", face);
            request.put("LEDtype", LEDType);
            request.put("headdegree",headDegree);
            this.sendToAll(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLeaveStage(int dis, int rotationDegree, int face, int LEDType, int headDegree){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setMotionOnStage");
            request.put("information", "leave");
            request.put("distance", dis);
            request.put("rotationDegree", rotationDegree);
            request.put("face", face);
            request.put("LEDtype", LEDType);
            request.put("headdegree",headDegree);
            this.sendToAll(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setAppIdAndOpen(String appId){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "openZba");
            request.put("appId", appId);

            this.sendToAll(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void openAppAtTime(ArrayList<String> appId, long timeInMillis, long readyWaitTime) {

        try {
            JSONObject request = new JSONObject();
            request.put("status", "openAppList");
            request.put("readyWaitTime", readyWaitTime);

            for (int i = 0; i < appId.size(); i++) {
                request.put("appId_" + i, appId.get(i));
            }

            request.put("timeInMillis", timeInMillis);
            this.sendToAll(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void stopApp() {
        try {
            JSONObject request = new JSONObject();
            request.put("status", "stopApp");
            this.sendToAll(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestSyncTime() {
        try {
            JSONObject request = new JSONObject();
            request.put("status", "requestSyncTime");
            this.sendToAllNoCheckTime(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setFace(boolean setFace) {
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setFace");
            request.put("setFace", setFace);
            this.sendToAll(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setEvent(int eventValue) {
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setEvent");
            request.put("eventValue", eventValue);
            this.sendToAll(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void broadcastZbaEvent(String eventName) {
        Log.e(TAG,"broadcastZbaEvent   eventName:"+eventName);
        final long transmitTime = System.currentTimeMillis();

        try {
            JSONObject request = new JSONObject();
            request.put("status", "broadcastZbaEvent");
            request.put("eventName", eventName);
            request.put("transmitTime", transmitTime);
            this.sendToAll(request.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void enableVoiceTrigger(){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "enableVoiceTrigger");
            this.sendToAll(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void disableVoiceTrigger(){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "disableVoiceTrigger");
            this.sendToAll(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void randomFace(){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "randomFace");
        this.sendToAll(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setTtsVoiceStatus(boolean setStatus){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setTtVoiceStatus");
            request.put("needSet", setStatus);
            this.sendToAll(request.toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void setRunPlanB(int faceNum, String tts){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setRunPlanB");
            request.put("TTSstring", tts);
            request.put("face", faceNum);
            this.sendToAll(request.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMotionAvoidanceStatus(boolean setOpen){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setMotionAvoidanceStatus");
            request.put("setStatus", setOpen);
            this.sendToAll(request.toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void setCapSensorStatus(boolean setStatus){
        try {
            JSONObject request = new JSONObject();
            request.put("status", "setCapSensorStatusStatus");
            request.put("setStatus", setStatus);
            this.sendToAll(request.toString());
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
