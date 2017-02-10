package com.example.missa.neurosky_brightness;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;

public class main_screen extends AppCompatActivity {
/*  main screen of app, contains brightness slider.
 */
    private SeekBar brightnessBar;
    private int brightnessStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        initValirables();

        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int brightness = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initValirables(){
        // initialize variables
        brightnessBar = (SeekBar) findViewById(R.id.brightnessBar);
    }


}
