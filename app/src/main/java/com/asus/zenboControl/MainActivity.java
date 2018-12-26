package com.asus.zenboControl;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.zenboControl.Client.ClientActivity;
import com.asus.zenboControl.Server.ServerActivity;

public class MainActivity extends FullScreenActivity{

    TextView textVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseMethod.checkPermission(this);

      //  IntentionModule.getInstance(MainActivity.this);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            textVersion = (TextView) findViewById(R.id.tv_version);
            if(textVersion != null){
                textVersion.setText("version:"+version);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        Button server = (Button) findViewById(R.id.btn_linkserver);
        Button client = (Button) findViewById(R.id.btn_linkClient);
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClientActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
