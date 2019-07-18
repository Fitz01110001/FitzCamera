package com.example.zoomprogressview.custUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("AppCompatCustomView")
public class ShutterTouchListener implements View.OnTouchListener {

    private String TAG = "ShutterTouchListener";
    private TouchCallBack mTouchCallBack;
    private int lastX;
    private int lastY;
    private Context mContext;
    private int screenWidth;
    private int screenHeight;


    public ShutterTouchListener(Context context, TouchCallBack callBack) {
        mTouchCallBack = callBack;
        mContext = context;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        //int screenHeight = dm.heightPixels - 100;//需要减掉图片的高度
        screenHeight = dm.heightPixels;//需要减掉图片的高度
        Log.d(TAG, "screenWidth:" + screenWidth + " screenHeight:" + screenHeight);
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                lastY = (int) event.getRawY();
                Log.d(TAG, "ACTION_DOWN,X:" + lastX + " Y:" + lastY);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                mTouchCallBack.onTouchUP();
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchCallBack.onTouchMove();
                //event.getRawX();获得移动的位置
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                int l = v.getLeft() + dx;
                int b = v.getBottom();
                int r = v.getRight() + dx;
                int t = v.getTop();

                if (l < 0) {
                    l = 0;
                    r = l + v.getWidth();
                }
                if (r > screenWidth) {
                    r = screenWidth ;
                    l = r - v.getWidth();
                }

                v.layout(l, t, r, b);
                Log.d(TAG, "ACTION_MOVE,dx:" + dx);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                v.postInvalidate();
                break;
        }
        return true;
    }

    public interface TouchCallBack {
        void onTouchMove();

        void onTouchUP();
    }



}
