/*
Code by Marissa Kohan
3-23-2017

This is a small app that uses the NeuroSky headset to adjust phone screen brightness based
on the attention and meditation measures available in the NeuroSky system
 */
package com.example.missa.neurosky_brightness;
import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.View;

import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;
import com.neurosky.connection.EEGPower;
import com.neurosky.connection.DataType.MindDataType;

public class main_screen extends AppCompatActivity {

    private static final String TAG = main_screen.class.getSimpleName();
    private static final int MAX_BRIGHTNESS = 255;
    private static final int BRIGHT_INCREMENT = 10;
    private ContentResolver cResolver;
    private TgStreamReader tgStreamReader;
    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        initView();
        // set intial brightness
        cResolver = getContentResolver();
        brightnessVal = getScreenBrightness();
        brightnessBar.setProgress(brightnessVal);
        brightnessText.setText("Brightness at "+brightnessVal+" of 255");

        try {
            // Make sure that the device supports Bluetooth and Bluetooth is on
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Toast.makeText(
                        getApplicationContext(),
                        "Please enable your Bluetooth and re-run this program !",
                        Toast.LENGTH_LONG).show();
                finish();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "error:" + e.getMessage());
            return;
        }

        // brightness bar handler
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int brightness = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // changes screen brightness with slider
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
                setScreenBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();}
            }
        }
        );


        // Create tag stream reader
        tgStreamReader = new TgStreamReader(mBluetoothAdapter, callback);
        // set up for logging
        tgStreamReader.setGetDataTimeOutTime(6);
        // start logging
        tgStreamReader.startLog();

    }

    private SeekBar brightnessBar;
    private int meditationVal;
    private int attentionVal;
    private Button startButton;
    private Button stopButton;
    private int brightnessVal;
    private TextView brightnessText;

    private TextView attentionText;
    private TextView meditationText;

    private int badPacketCount = 0;

    private void initView() {
        // initialize variables
        brightnessBar = (SeekBar) findViewById(R.id.brightnessBar);
        brightnessText = (TextView) findViewById(R.id.brightnessText);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        attentionText = (TextView) findViewById(R.id.attentionText);
        meditationText = (TextView) findViewById(R.id.meditationText);

        attentionVal = 0;
        meditationVal = 0;

        // set text
        attentionText.setText("Attention: "+attentionVal);
        meditationText.setText("Meditation: "+meditationVal);
        // start EEG read
        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                badPacketCount = 0;

                // if reader is not connected
                if(tgStreamReader != null && tgStreamReader.isBTConnected()){

                    // Prepare for connecting
                    tgStreamReader.stop();
                    tgStreamReader.close();
                }
                tgStreamReader.connect();
            }
        });

        // stop EEG read
        stopButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                tgStreamReader.stop();
                tgStreamReader.close();
            }

        });


    }



    public void stop() {
        // stop NeuroSky bluetooth conection
        if(tgStreamReader != null){
            // stops reading from bluetooth device
            tgStreamReader.stop();
            tgStreamReader.close();
        }
    }

    @Override
    protected void onDestroy() {
        //releases resources on stop
        if(tgStreamReader != null){
            tgStreamReader.close();
            tgStreamReader = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        // on NeuroSky bluetooth start
        super.onStart();
    }

    @Override
    protected void onStop() {
        // on NeuroSky bluetooth stop
        super.onStop();
        stop();
    }

    // tag stream handler handles reads from NeuroSky bluetooth
    private TgStreamHandler callback = new TgStreamHandler() {
        @Override
        public void onStatesChanged(int connectionStates) {
            // state change handler
            // Connected and failed are the most importnant
            Log.d(TAG, "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTED:
                    tgStreamReader.start();
                    break;
                case ConnectionStates.STATE_WORKING:
                    //byte[] cmd = new byte[1];
                    //cmd[0] = 's';
                    //tgStreamReader.sendCommandtoDevice(cmd);

                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    //get data time out
                    break;
                case ConnectionStates.STATE_COMPLETE:
                    //read file complete
                    break;
                case ConnectionStates.STATE_STOPPED:
                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    break;
                case ConnectionStates.STATE_ERROR:
                    Log.d(TAG,"Connect error, Please try again!");
                    break;
                case ConnectionStates.STATE_FAILED:
                    Log.d(TAG,"Connect failed, Please try again!");
                    break;
            }
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_STATE;
            msg.arg1 = connectionStates;
            LinkDetectedHandler.sendMessage(msg);


        }

        @Override
        public void onRecordFail(int a) {
            // log connection failures
            Log.e(TAG,"onRecordFail: " +a);

        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {
            // loc connection failures
            badPacketCount ++;
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_BAD_PACKET;
            msg.arg1 = badPacketCount;
            LinkDetectedHandler.sendMessage(msg);

        }

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            // process link data, direct it to LinkDetectedHandler
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = datatype;
            msg.arg1 = data;
            msg.obj = obj;
            LinkDetectedHandler.sendMessage(msg);
            //Log.i(TAG,"onDataReceived");
        }    };

    private static final int MSG_UPDATE_BAD_PACKET = 1001;
    private static final int MSG_UPDATE_STATE = 1002;

    // handles packets processed by tgStreamHandler
    private Handler LinkDetectedHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // update attention/meditation and monitor signal
            switch (msg.what) {
                case MindDataType.CODE_RAW:
                    break;
                case MindDataType.CODE_MEDITATION:
                    Log.d(TAG, "HeadDataType.CODE_MEDITATION " + msg.arg1);
                    meditationVal = msg.arg1;
                    meditationText.setText("Meditation: "+meditationVal);
                    calcScreenBrightness();
                    break;
                case MindDataType.CODE_ATTENTION:
                    Log.d(TAG, "HeadDataType.CODE_ATTENTION " + msg.arg1);
                    attentionVal = msg.arg1;
                    attentionText.setText("Attention: "+attentionVal);
                    break;
                case MindDataType.CODE_EEGPOWER:
                    EEGPower power = (EEGPower)msg.obj;
                    break;
                case MindDataType.CODE_POOR_SIGNAL://
                    int poorSignal = msg.arg1;
                    Log.d(TAG, "poorSignal:" + poorSignal);
                    //Toast.makeText(getApplicationContext(), "Poor signal", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_UPDATE_BAD_PACKET:
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private int getScreenBrightness() {
        // get current screen brightness
        int brightness = Settings.System.getInt(
                cResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                0);
        return brightness;
    }

    private void setScreenBrightness(int brightness) {
        // set screen brightness
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if(brightness >= 0 && brightness <= 255){
            // set brightness for system as well as window
            Settings.System.putInt(
                    cResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightness
            );
            lp.screenBrightness = brightness/(float) 255;
            getWindow().setAttributes(lp);
            Log.d(TAG, "Setting brightness to: "+brightness);
            brightnessText.setText("Brightness at " + brightness + " of " + brightnessBar.getMax());
            // set brightness bar
            brightnessBar.setProgress(brightness);
            brightnessVal = brightness;
        }
    }

    private void calcScreenBrightness() {
        // calc screen brightness
        int brightness = brightnessVal;

        if(attentionVal>meditationVal){
            if(brightness < attentionVal*2.55) {
                // only increment if brightness < attnetion
                brightness = brightness + BRIGHT_INCREMENT;
            }
        }
        else {
            if(brightness > MAX_BRIGHTNESS-meditationVal*2.55) {
                // only decrement if brightness > meditation
                brightness = brightness - BRIGHT_INCREMENT;
            }
        }
        setScreenBrightness(brightness);
    }
}
