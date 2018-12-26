package com.asus.zenboControl.listener;

import org.json.JSONObject;

/**
 * Created by Kenny on 2016/3/28.
 */
public interface OnRobotAPIManagerListener {

    void onSluResult(JSONObject app_semantic, String error_code);

    void onRetry(String askBack);

    void onResultMsg(String resultMsg);

    void onTimeOut();
}