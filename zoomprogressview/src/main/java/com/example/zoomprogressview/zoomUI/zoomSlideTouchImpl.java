package com.example.zoomprogressview.zoomUI;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class zoomSlideTouchImpl implements View.OnTouchListener {

    private String TAG = "zoomSlideTouchImpl";
    private int lastX;
    private int lastY;
    private Context mContext;
    private SlideCallback mSlideCallback;
    private ZoomCircle mZoomCircle;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mZoomCircle.setOnZoomState(false);
            mSlideCallback.OnSlideUp();
        }
    };

    public zoomSlideTouchImpl(Context context,SlideCallback cb) {
        mContext = context;
        mSlideCallback = cb;
        mHandler = new Handler();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mZoomCircle = (ZoomCircle)v;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mZoomCircle.setOnZoomState(true);
                lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                lastY = (int) event.getRawY();
                mSlideCallback.OnSlideDown();
                Log.d(TAG, "ACTION_DOWN,X:" + lastX + " Y:" + lastY);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                mHandler.postDelayed(mRunnable,500);
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();

                mSlideCallback.OnSlideMove(dx);
                break;
        }
        return true;
    }

    public interface SlideCallback{
        void OnSlideDown();
        void OnSlideUp();
        void OnSlideMove(int dx);
    }

}
