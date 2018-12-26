package com.asus.zenboControl.Server;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.asus.zenboControl.BaseActivity;
import com.asus.zenboControl.EditTextWithBackButton;
import com.asus.zenboControl.R;

import java.util.ArrayList;

public class AppIdActivity extends BaseActivity {
    EditTextWithBackButton firstAppId;
    EditTextWithBackButton secondAppId;
    EditTextWithBackButton thirdAppId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_id);

        firstAppId = (EditTextWithBackButton) findViewById(R.id.firstAppId);
        secondAppId = (EditTextWithBackButton) findViewById(R.id.secondAppId);
        thirdAppId = (EditTextWithBackButton) findViewById(R.id.thirdAppId);

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> appId = intent.getStringArrayListExtra("appId");

            if (appId != null) {
                firstAppId.setText(appId.get(0));
                secondAppId.setText(appId.get(1));
                thirdAppId.setText(appId.get(2));
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("appId", "onBackPressed ");
        ArrayList<String> appId = new ArrayList<String>();

        String firstId = firstAppId.getText().toString();
        String secondId = secondAppId.getText().toString();
        String thirdId = thirdAppId.getText().toString();

        if (TextUtils.isEmpty(firstId) && TextUtils.isEmpty(secondId) && TextUtils.isEmpty(thirdId)) {
            appId = null;
        } else {
            appId.add(firstId);
            appId.add(secondId);
            appId.add(thirdId);
        }

        Log.e("appId", "appId " + appId);
        Intent intent = new Intent();
        intent.putStringArrayListExtra("appId", appId);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}
