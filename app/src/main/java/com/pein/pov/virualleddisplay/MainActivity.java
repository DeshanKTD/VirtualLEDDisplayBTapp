package com.pein.pov.virualleddisplay;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    public static Object lock = new Object();
    public static boolean timeDisp = false;

    //difining interface elements
    Switch setTimeSwitch;
    Button sendTextButton,sendSpeedButton,connectDeviceButton,resetButton;
    EditText patternText,speedText;
    TextView connectionStatus;


    //keypad hide
    InputMethodManager inputManager;
    //for state change listner
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //defining elements in interface
        sendTextButton = (Button) findViewById(R.id.sendText);
        sendSpeedButton = (Button) findViewById(R.id.changeSpeed);
        resetButton = (Button) findViewById(R.id.reset);
        setTimeSwitch = (Switch) findViewById(R.id.setTime);
        connectDeviceButton = (Button) findViewById(R.id.btconnect);

        patternText = (EditText) findViewById(R.id.patternText);
        speedText = (EditText) findViewById(R.id.speedText);

        connectionStatus = (TextView) findViewById(R.id.btconnectstate);

        // add listner to pop up toast messages as BT state changes
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(dReceiver, filter);

        //timer thrad
        Time time = new Time();
        time.start();


        //check whether eit bluetooth adaptor
        checkBluetoothAvailable();
        Log.i("BTStats","Available checked");

        //button click actions
        onSendPatternButtonClick();
        onSendSpeedButtonClick();
        //onSetTimeButtonClick();
        onSetConnectButtionClick();
        onSetTimeSwitchChange();
        onResetButtonClick();
    }


    //listener class to change bluetooth state
    private final BroadcastReceiver dReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Toast msg;
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_ON:
                        msg = Toast.makeText(getBaseContext(),"Bluetooth On",Toast.LENGTH_LONG);
                        msg.show();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        msg = Toast.makeText(getBaseContext(),"Bluetooth Off",Toast.LENGTH_LONG);
                        msg.show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        msg = Toast.makeText(getBaseContext(),"Bluetooth is turning off",Toast.LENGTH_LONG);
                        msg.show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        msg = Toast.makeText(getBaseContext(),"Bluetooth is Turning on",Toast.LENGTH_LONG);
                        msg.show();
                }
            }
        }
    };


    //check for bluetooth connection and turn on
    private void checkBluetoothAvailable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // check whether bluetooth adaptor is available
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast msg = Toast.makeText(getBaseContext(),"No Bluetooth Adaptor found!",Toast.LENGTH_LONG);
            msg.show();
        }
        else{
            //check whether bluetooth is turned on
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

        }
    }

    /*
     * Button click operations
     */

    private  void onSendPatternButtonClick(){
        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = patternText.getText().toString();
                text = text + "#";
                Toast msg = Toast.makeText(getBaseContext(),"Pattern Sent",Toast.LENGTH_LONG);

                //send text??????????????????????????????????
                BluetoothHandle.write(text);

                //hide keypad
                try {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                catch(Exception e){
                    Log.e("Keypad","KeyPad closed at initial");
                }

                msg.show();
                Log.i("TextSend",text);
            }

        });
    }

    private void onSendSpeedButtonClick(){
        sendSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = speedText.getText().toString();
                int val=1000;
                try {
                    val = Integer.parseInt(text);
                }
                catch (Exception e){
                    Log.e("InputVal","Field is empty");
                }

                text = "@s" + text + "#";

                //hide keypad
                try {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                catch (Exception e){
                    Log.e("Keypad","KeyPad closed at initial");
                }

                if(val<999) {
                    //send speed??????????????????????????????????
                    BluetoothHandle.write(text);
                    Toast msg = Toast.makeText(getBaseContext(),"Speed Updated",Toast.LENGTH_LONG);
                    msg.show();
                    Log.i("SpeedUpdate", "Speed Changed to " + text);
                }
                else{
                    Toast msg = Toast.makeText(getBaseContext(),"Value should less than 1000",Toast.LENGTH_LONG);
                    msg.show();
                    Log.e("SpeedUpdate", "Value > 1000");
                }

            }
        });
    }

//    private void onSetTimeButtonClick(){
//        setTimeSwitch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            //    String timeText = makeTimeString();
//                timeText = "@t"+timeText+"#";
//
//                //send time ?????????????????????????????????????????????
//                BluetoothHandle.write(timeText);
//                Toast msg = Toast.makeText(getBaseContext(),"Time Update",Toast.LENGTH_LONG);
//                msg.show();
//                Log.i("Display Mode", "Changed to Time Display");
//            }
//        });
//    }

    private void onSetTimeSwitchChange(){
        setTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sendTextButton.setEnabled(false);
                    synchronized (lock) {
                        timeDisp = true;
                    }
                }
                else{
                    sendTextButton.setEnabled(true);
                    synchronized (lock) {
                        timeDisp = false;
                    }
                }
            }
        });
    }

    private void onResetButtonClick(){
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "@reset#";
                Toast msg = Toast.makeText(getBaseContext(),"Resetted",Toast.LENGTH_LONG);
                BluetoothHandle.write(text);
                msg.show();
                Log.i("Display","resetted");
            }
        });
    }


    private void onSetConnectButtionClick(){
        connectDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,DeviceList.class));
            }
        });
    }


}