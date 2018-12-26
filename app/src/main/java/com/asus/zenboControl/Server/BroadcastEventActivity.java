package com.asus.zenboControl.Server;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asus.robotframework.API.RobotFace;
import com.asus.zenboControl.BaseActivity;
import com.asus.zenboControl.BaseMethod;
import com.asus.zenboControl.ButtonCondownRunnable;
import com.asus.zenboControl.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BroadcastEventActivity extends BaseActivity {

    private ServerService mService;
    private String TAG = getClass().getSimpleName();
    ListView listViewEventName;
    ImageView btnAddEventName;
    EditText etEventName;
    TextView buttonTotalTimer;
    TextView buttonTimer;
    Handler mHandler;
    Button randomFaceButton;
    Button planBButton;
    TextView planBTextView;
    boolean planBIsSelect;
    ImageView planBPlayButton;

    List<String> listElements = new ArrayList<String>();
    List<String> listShowElement = new ArrayList<String>();
    ButtonCondownRunnable buttonClickRunnable;

    private static final int broadcastSpaceTime = 1000;
    private static long broadcastClickLastTime = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_event);
        mService = ServerActivity.getService();
        listViewEventName = (ListView) findViewById(R.id.listEventName);
        btnAddEventName = (ImageView) findViewById(R.id.btnAddNewEvent);
        etEventName = (EditText) findViewById(R.id.etEventNameInput);
        buttonTimer = (TextView) findViewById(R.id.tv_button_time);
        buttonTotalTimer = (TextView) findViewById(R.id.tv_total_time);
        randomFaceButton = (Button) findViewById(R.id.btn_randomface);
        planBButton = (Button) findViewById(R.id.btn_planb);
        planBTextView = (TextView) findViewById(R.id.tv_planb);
        planBPlayButton = (ImageView) findViewById(R.id.iv_play_planb);
        planBIsSelect = false;

        setPlanBClickListener();


        randomFaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.randomFace();
            }
        });

        float time = BaseMethod.messageMaxDelayTime / 1000;

        buttonTotalTimer.setText(String.format("%.1f", time)+"s");

        mHandler = new Handler();
////
////      broadcase event list need use other word to show
////      show word set on BroadcastEventShowWord and then open this code
//
//        listShowElement = BroadcastEventShowWord.getStringArrayList();
//
//        for(int i=1; i<=listShowElement.size(); i++) {
//            listElements.add("Act"+new DecimalFormat("00").format(i));
//
//        }
////

        for(int i=1; i<=30; i++) {
            listShowElement.add("Act"+new DecimalFormat("00").format(i));
            listElements.add("Act"+new DecimalFormat("00").format(i));

        }

        final BroadCaseEventAdapter adapter = new BroadCaseEventAdapter(this, listElements, listShowElement, mService){
            @Override
            public void itemRunEvent() {
                super.itemRunEvent();
                if(BroadcastEventActivity.this != null && !BroadcastEventActivity.this.isDestroyed()) {
                    callButtonCountDown();
                }
            }
        };
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
//                (BroadcastEventActivity.this, R.layout.simple_list_item, listElements);
        btnAddEventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etEventName.getText() != null && etEventName.getText().toString() != null &&
                        etEventName.getText().toString().length() > 0) {
                    listElements.add(etEventName.getText().toString());
                    adapter.notifyDataSetChanged();

                    Toast.makeText(BroadcastEventActivity.this,"new event is added",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BroadcastEventActivity.this,"cannot add null name event",Toast.LENGTH_SHORT).show();
                }
            }
        });

        listViewEventName.setAdapter(adapter);
        listViewEventName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.e(TAG, listElements.get(position));
                    adapter.setSelection(position, !adapter.isPositionSelection(position));
                    adapter.setNewChecked(position, true);
            }
        });
    }

    private void callButtonCountDown(){
        if(buttonClickRunnable != null){
            buttonClickRunnable.isNeedEnd = true;
        }

        buttonClickRunnable = new ButtonCondownRunnable(mHandler,buttonTimer);

        mHandler.post(buttonClickRunnable);
    }

    private void setPlanBClickListener(){
        planBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planBIsSelect = false;
                planBTextView.setVisibility(View.VISIBLE);
                planBTextView.setBackgroundColor(Color.parseColor("#FFCCCC"));
                planBPlayButton.setVisibility(View.GONE);
            }
        });

        planBTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(planBIsSelect){
                    planBIsSelect = false;
                    view.setBackgroundColor(Color.parseColor("#FFCCCC"));
                    planBPlayButton.setVisibility(View.GONE);
                } else {
                    planBIsSelect = true;
                    view.setBackgroundColor(Color.parseColor("#96CDCD"));
                    planBPlayButton.setVisibility(View.VISIBLE);
                }
            }
        });

        planBPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planBTextView.setVisibility(View.GONE);
                planBPlayButton.setVisibility(View.GONE);
                mService.setRunPlanB(RobotFace.SHY.getValue(), "我剛剛去補了妝,,,你看我好看嗎?");
            }
        });
    }


}
