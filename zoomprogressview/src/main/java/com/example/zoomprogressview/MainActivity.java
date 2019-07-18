package com.example.zoomprogressview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.zoomprogressview.custUI.ShutterTouchListener;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private ImageButton mShutterButton;
    private ImageButton mSwitchButton;
    private LinearLayout zoombar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zoombar = findViewById(R.id.zoombar_horizontal);
        mSwitchButton = findViewById(R.id.button_switchCamera);
        mShutterButton = findViewById(R.id.button_shutter);
        mShutterButton.setOnTouchListener(new ShutterTouchListener(this, new ShutterTouchListener.TouchCallBack() {
            @Override
            public void onTouchMove() {
                mSwitchButton.setVisibility(View.GONE);
                zoombar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTouchUP() {
                mSwitchButton.setVisibility(View.VISIBLE);
                zoombar.setVisibility(View.GONE);
            }
        }));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }
}
