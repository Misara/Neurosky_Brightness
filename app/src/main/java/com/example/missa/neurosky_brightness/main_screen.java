package com.example.missa.neurosky_brightness;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.system.*;
import android.widget.TextView;
import android.widget.Toast;

public class main_screen extends AppCompatActivity {
/*  main screen of app, contains brightness slider.
 */
    private SeekBar brightnessBar;
    private int brightnessStatus;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        initValirables();

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int brightness = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
                brightness = (int) (progress *2.55);
                android.provider.Settings.System.putInt(main_screen.super.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
                textView.setText("at "+progress+"% of "+brightnessBar.getMax()+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initValirables(){
        // initialize variables
        brightnessBar = (SeekBar) findViewById(R.id.brightnessBar);
        textView = (TextView) findViewById(R.id.textView);
    }


}
