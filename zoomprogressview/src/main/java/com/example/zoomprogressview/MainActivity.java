package com.example.zoomprogressview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.zoomprogressview.custUI.ShutterTouchListener;
import com.example.zoomprogressview.zoomRuler.ZoomScaleViewGroup;
import com.example.zoomprogressview.zoomUI.ZoomCircle;
import com.example.zoomprogressview.zoomUI.zoomSlideTouchImpl;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private ImageButton mShutterButton;
    private ImageButton mSwitchButton;
    private LinearLayout zoombar;
    private ZoomCircle mZoomCircle;
    private View zoomSlide;
    private ZoomScaleViewGroup mZoomScaleViewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zoombar = findViewById(R.id.zoombar_horizontal);
        zoomSlide = findViewById(R.id.zoom_slide);
        mZoomCircle = findViewById(R.id.cur_zoom_circle);
        mZoomCircle.setOnTouchListener(new zoomSlideTouchImpl(this, new zoomSlideTouchImpl.SlideCallback() {
            @Override
            public void OnSlideDown() {
                zoomSlide.setVisibility(View.VISIBLE);
            }

            @Override
            public void OnSlideUp() {
                zoomSlide.setVisibility(View.GONE);
            }

            @Override
            public void OnSlideMove(int dx) {
                int l = zoomSlide.getLeft() + dx;
                int b = zoomSlide.getBottom();
                int r = zoomSlide.getRight() + dx;
                int t = zoomSlide.getTop();
                zoomSlide.layout(l, t, r, b);
                zoomSlide.postInvalidate();
            }
        }));
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


        mZoomScaleViewGroup = findViewById(R.id.zoomScaleGroup);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }
}
