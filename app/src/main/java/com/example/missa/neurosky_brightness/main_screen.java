package com.example.missa.neurosky_brightness;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.system.*;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;
import com.neurosky.connection.DataType.MindDataType;

public class main_screen extends AppCompatActivity {
/*  main screen of app, contains brightness slider.
 */
    private SeekBar brightnessBar;
    private ProgressBar meditationBar;
    private int meditationVal;
    private ProgressBar attentionBar;
    private int attentionVal;
    private int brightnessStatus;
    private TextView textView;
    private TgStreamReader tagStreamReader;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        initValirables();


        try {
            // ensure bluetooth is running
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Toast.makeText(
                        this,
                        "Please enable your Bluetooth and re-run this program !",
                        Toast.LENGTH_LONG).show();
                finish();
//				return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        // Example of constructor public TgStreamReader(BluetoothAdapter ba, TgStreamHandler tgStreamHandler)
        tagStreamReader = new TgStreamReader(bluetoothAdapter, tagStreamHandler);
        tagStreamReader.startLog();
        tagStreamReader.connect();

        Log.d("debug", "trying to connect");
        // brightness adjustment bar
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int brightness = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // changes screen brightness with slider
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
                brightness = (int) (progress * 2.55);
                android.provider.Settings.System.putInt(main_screen.super.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
                textView.setText("at " + progress + "% of " + brightnessBar.getMax() + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        }
        );
    }

    // tag stream handler for bluetooth input
    TgStreamHandler tagStreamHandler = new TgStreamHandler() {
        @Override
        // send info to linkdetectedhandler
        public void onDataReceived(int dataType, int data, Object obj) {
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = dataType;
            msg.arg1 = data;
            msg.obj = obj;
            LinkDetectedHandler.sendMessage(msg);
        }

        @Override
        // Display message when state changes
        public void onStatesChanged(int connectionStates) {
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    // Do something when connecting
                    Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    // Do something when connected
                    try {
                        tagStreamReader.start();
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        System.out.println("broke at tagStreamRader.start()");
                        break;
                    }
                    break;
                case ConnectionStates.STATE_WORKING:
                    // Do something when working
                    tagStreamReader.startRecordRawData();

                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    // Do something when getting data timeout

                    //(9) demo of recording raw data, exception handling
                    tagStreamReader.stopRecordRawData();

                    Toast.makeText(getApplicationContext(), "Timeout", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectionStates.STATE_STOPPED:
                    // Do something when stopped
                    // We have to call tgStreamReader.stop() and tgStreamReader.close() much more than
                    // tgStreamReader.connectAndstart(), because we have to prepare for that.
                    Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    // Do something when disconnected
                    Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectionStates.STATE_ERROR:
                    // Do something when you get error message
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    break;
                case ConnectionStates.STATE_FAILED:
                    // Do something when you get failed message
                    // It always happens when open the BluetoothSocket error or timeout
                    // Maybe the device is not working normal.
                    // Maybe you have to try again
                    //Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onChecksumFail(byte[] bytes, int i, int i1) {

        }

        @Override
        public void onRecordFail(int i) {

        }
    };

    private Handler LinkDetectedHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // (8) demo of MindDataType
            switch (msg.what) {
                case MindDataType.CODE_MEDITATION:
                    //Log.d(TAG, "HeadDataType.CODE_MEDITATION " + msg.arg1);
                    //tv_meditation.setText("" +msg.arg1 );
                    //Toast.makeText(getApplicationContext(), "Meditation at"+MindDataType.CODE_MEDITATION, Toast.LENGTH_SHORT).show();
                    break;
                case MindDataType.CODE_ATTENTION:
                    //Log.d(TAG, "CODE_ATTENTION " + msg.arg1);
                    //tv_attention.setText("" +msg.arg1 );
                    break;
                case MindDataType.CODE_POOR_SIGNAL://
                    Toast.makeText(getApplicationContext(), "Poor Signal", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void initValirables(){
        // initialize variables
        brightnessBar = (SeekBar) findViewById(R.id.brightnessBar);
        textView = (TextView) findViewById(R.id.textView);
        attentionBar = (ProgressBar) findViewById(R.id.attentionBar);
        meditationBar = (ProgressBar) findViewById(R.id.meditationBar);
        attentionVal = -1;
        meditationVal = -1;

    }


}
