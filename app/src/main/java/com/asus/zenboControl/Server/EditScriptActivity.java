package com.asus.zenboControl.Server;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.asus.zenboControl.BaseActivity;
import com.asus.zenboControl.R;


public class EditScriptActivity extends BaseActivity {

    TextView tvScriptName;
    Button btnSave;
    EditText etDistance, etRotation, etHeadDegree;
    Spinner face_spinner, led_spinner;
    private String ENTER_SCRIPT = "ENTER SCRIPT";
    private String LEAVE_SCRIPT = "LEAVE SCRIPT";
    private String SCRIPT_NAME = "SCRIPT_NAME";
    private String ENTER_SCRIPT_DATA = "ENTER_SCRIPT_DATA";
    private String LEAVE_SCRIPT_DATA = "LEAVE_SCRIPT_DATA";
    private String MOVEMENT_DISTANCE = "MOVEMENT_DISTANCE";
    private String ROTATION_DEGREE = "ROTATION_DEGREE";
    private String FACE_INDEX = "FACE_INDEX";
    private String LED_TYPE = "LED_TYPE";
    private String HEAD_DEGREE = "HEAD_DEGREE";
    private String scriptName = "SCRIPT NAME";
    private SharedPreferences sharedPreferences;
    private int distanceValue = 0;
    private int rotationValue = 0;
    private int ledIndex = 0;
    private int faceIndex = 0;
    private int headPitchDegree = 0;
    int[] facenum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent getIntent  = getIntent();
        if(getIntent != null) {
            scriptName = getIntent.getStringExtra(SCRIPT_NAME);
        }
        if(scriptName.contains(ENTER_SCRIPT)) {
            sharedPreferences = getSharedPreferences(ENTER_SCRIPT_DATA,0);
            setContentView(R.layout.activity_edit_script_enter);

        } else {
            sharedPreferences = getSharedPreferences(LEAVE_SCRIPT_DATA,0);
            setContentView(R.layout.activity_edit_script_leave);

        }
        initView();


    }

    private void initView() {
        tvScriptName = (TextView) findViewById(R.id.script_name);
        btnSave = (Button) findViewById(R.id.saveBtn);
        etDistance = (EditText) findViewById(R.id.etDistance);
        etRotation = (EditText) findViewById(R.id.etRotation);
        etHeadDegree = (EditText) findViewById(R.id.etHeadDegree);
        face_spinner = (Spinner) findViewById(R.id.face_spinner);
        led_spinner = (Spinner) findViewById(R.id.led_spinner);

        tvScriptName.setText(scriptName);
        final ArrayAdapter<CharSequence> faceList = ArrayAdapter.createFromResource(EditScriptActivity.this,
                R.array.faceArray,
                android.R.layout.simple_spinner_dropdown_item);
        face_spinner.setAdapter(faceList);

        ArrayAdapter<CharSequence> LEDList = ArrayAdapter.createFromResource(EditScriptActivity.this,
                R.array.LEDArray,
                android.R.layout.simple_spinner_dropdown_item);
        led_spinner.setAdapter(LEDList);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etDistance.getText().length() > 0){
                    distanceValue  = Integer.parseInt( etDistance.getText().toString());
                } else {
                    distanceValue = 0;
                }
                if(etRotation.getText().length() > 0){
                    rotationValue  = Integer.parseInt( etRotation.getText().toString());
                } else {
                    rotationValue = 0;
                }
                if(etHeadDegree.getText().length() > 0){
                    headPitchDegree  = Integer.parseInt( etHeadDegree.getText().toString());
                } else {
                    headPitchDegree = 0;
                }
                faceIndex = face_spinner.getSelectedItemPosition();
                ledIndex = led_spinner.getSelectedItemPosition();

                sharedPreferences.edit()
                        .putInt(MOVEMENT_DISTANCE, distanceValue)
                        .putInt(ROTATION_DEGREE, rotationValue)
                        .putInt(FACE_INDEX,faceIndex)
                        .putInt(LED_TYPE, ledIndex)
                        .putInt(HEAD_DEGREE, headPitchDegree)
                        .commit();
                finish();
            }
        });

        distanceValue = sharedPreferences.getInt(MOVEMENT_DISTANCE, 0);
        rotationValue = sharedPreferences.getInt(ROTATION_DEGREE, 0);
        faceIndex = sharedPreferences.getInt(FACE_INDEX, 0);
        ledIndex = sharedPreferences.getInt(LED_TYPE, 0);
        headPitchDegree = sharedPreferences.getInt(HEAD_DEGREE, 0);

        etDistance.setText(distanceValue+"");
        etRotation.setText(rotationValue+"");
        etHeadDegree.setText(headPitchDegree+"");
        face_spinner.setSelection(faceIndex);
        led_spinner.setSelection(ledIndex);

    }
}
