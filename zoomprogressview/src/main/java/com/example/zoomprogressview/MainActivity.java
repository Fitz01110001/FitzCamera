package com.example.zoomprogressview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.zoomprogressview.custUI.ShutterTouchListener;
import com.example.zoomprogressview.zoomRuler.ZoomScaleViewGroup;
import com.example.zoomprogressview.zoomUI.ZoomCircle;
import com.example.zoomprogressview.zoomUI.zoomSlideTouchImpl;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String TAG = "ZoomMainActivity";
    private ImageButton mShutterButton;
    private ImageButton mSwitchButton;
    private LinearLayout zoombar;
    private ZoomCircle mZoomCircle;
    private View zoomSlide;
    private ZoomScaleViewGroup mZoomScaleViewGroup;
    private float zoomRulerLastScale = 1.0f;
    private final float WIDESCALE = 0f;

    public static final String PATTERN = "%.1f";

    private Button mButton_add;
    private Button mButton_sub;
    private float f = 1.0f;


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

        mButton_add = findViewById(R.id.bt_add);
        mButton_sub = findViewById(R.id.bt_sub);
        mButton_add.setOnClickListener(mOnClickListener);
        mButton_sub.setOnClickListener(mOnClickListener);

        mZoomScaleViewGroup = findViewById(R.id.zoomScaleGroup);
        mZoomScaleViewGroup.setZoomRulerListener(new ZoomScaleViewGroup.ZoomRulerListener() {
            @Override
            public void normalZoom(float scale) {
                if (scale == zoomRulerLastScale) {
                    return;
                } else if (zoomRulerLastScale == WIDESCALE) {
                    // TODO: 19-8-14 switch to normal
                    Log.d(TAG, "switch to normal,scale:" + scale);
                } else {
                    Log.d(TAG, "normalZoom,scale:" + scale);
                }
                zoomRulerLastScale = scale;
            }

            @Override
            public void switchToWide() {
                Log.d(TAG, "switchToWide" );
            }

            @Override
            public void switchToBack() {
                Log.d(TAG, "switchToBack" );
            }


        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_add:
                    if (f >= 4f) {
                        break;
                    }
                    f = f + 0.1f;
                    break;
                case R.id.bt_sub:
                    if (f <= 1f) {
                        break;
                    }
                    f = f - 0.1f;
                    break;
            }
            mZoomScaleViewGroup.updateRatio(getPatternRatio());
        }
    };

    private String getPatternRatio() {
        return "x" + String.format(Locale.ENGLISH, PATTERN, f);
    }
}
