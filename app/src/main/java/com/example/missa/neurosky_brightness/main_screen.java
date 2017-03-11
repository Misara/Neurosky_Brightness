package com.example.missa.neurosky_brightness;
import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
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


/*
public class main_screen extends AppCompatActivity {
/*  main screen of app, contains brightness slider.

    private SeekBar brightnessBar;
    private ProgressBar meditationBar;
    private int meditationVal;
    private ProgressBar attentionBar;
    private int attentionVal;
    private int brightnessStatus;
    private TextView textView;
    private TgStreamReader tgStreamReader;
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
        tgStreamReader = new TgStreamReader(bluetoothAdapter, tagStreamHandler);
        tgStreamReader.startLog();
        tgStreamReader.connect();

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
                        tgStreamReader.start();
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        System.out.println("broke at tagStreamRader.start()");
                        break;
                    }
                    break;
                case ConnectionStates.STATE_WORKING:
                    // Do something when working
                    tgStreamReader.startRecordRawData();

                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    // Do something when getting data timeout

                    //(9) demo of recording raw data, exception handling
                    tgStreamReader.stopRecordRawData();

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
*/

//////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////
public class main_screen extends Activity {

    private static final String TAG = main_screen.class.getSimpleName();
    private TgStreamReader tgStreamReader;
    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        initView();

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
                brightness = (int) (progress * 2.55);
                android.provider.Settings.System.putInt(main_screen.super.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
                brightnessText.setText("at " + progress + "% of " + brightnessBar.getMax() + "%");
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

        new Thread(new Runnable() {
            public void run() {
                while (attentionVal<100) {
                    // Update the progress bar
                    attentionBar.post(new Runnable() {
                        @Override
                        public void run() {
                            attentionBar.setProgress(attentionVal);
                        }

                    });
                    meditationBar.post(new Runnable() {
                        @Override
                        public void run() {
                            meditationBar.setProgress(meditationVal);
                        }
                    });
                }
            }
        }).start();


        // Create tag stream reader
        tgStreamReader = new TgStreamReader(mBluetoothAdapter, callback);

        tgStreamReader.setGetDataTimeOutTime(6);
        // start logging
        tgStreamReader.startLog();

    }

    private SeekBar brightnessBar;
    private ProgressBar meditationBar;
    private int meditationVal;
    private ProgressBar attentionBar;
    private int attentionVal;
    private Button startButton;
    private Button stopButton;
    private int brightnessStatus;
    private TextView brightnessText;

    private TextView attentionText;
    private TextView meditationText;

    private int badPacketCount = 0;

    private void initView() {
        // initialize variables
        brightnessBar = (SeekBar) findViewById(R.id.brightnessBar);
        brightnessText = (TextView) findViewById(R.id.brightnessText);
        attentionBar = (ProgressBar) findViewById(R.id.attentionBar);
        meditationBar = (ProgressBar) findViewById(R.id.meditationBar);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        attentionText = (TextView) findViewById(R.id.attentionText);
        meditationText = (TextView) findViewById(R.id.meditationText);

        attentionVal = 0;
        meditationVal = 0;

        attentionText.setText("Attention: "+attentionVal);
        meditationText.setText("Meditation: "+meditationVal);
        // start eeg read
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

        stopButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                tgStreamReader.stop();
                tgStreamReader.close();
            }

        });


    }

    // stop eeg read
    public void stop() {
        if(tgStreamReader != null){
            // stops reading from bluetooth device
            tgStreamReader.stop();
            tgStreamReader.close();
        }
    }

    @Override
    protected void onDestroy() {
        //releases resources
        if(tgStreamReader != null){
            tgStreamReader.close();
            tgStreamReader = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop();
    }

    // (7) demo of TgStreamHandler
    private TgStreamHandler callback = new TgStreamHandler() {

        @Override
        public void onStatesChanged(int connectionStates) {
            // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub
            Log.e(TAG,"onRecordFail: " +a);

        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {
            // TODO Auto-generated method stub

            badPacketCount ++;
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_BAD_PACKET;
            msg.arg1 = badPacketCount;
            LinkDetectedHandler.sendMessage(msg);

        }

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            // TODO Auto-generated method stub
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = datatype;
            msg.arg1 = data;
            msg.obj = obj;
            LinkDetectedHandler.sendMessage(msg);
            //Log.i(TAG,"onDataReceived");
        }    };

    private boolean isPressing = false;
    private static final int MSG_UPDATE_BAD_PACKET = 1001;
    private static final int MSG_UPDATE_STATE = 1002;

    int raw;
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

}

