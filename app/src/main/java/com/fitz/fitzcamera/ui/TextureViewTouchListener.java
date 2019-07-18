package com.fitz.fitzcamera.ui;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import com.fitz.fitzcamera.CamManager;

public class TextureViewTouchListener implements View.OnTouchListener {

    private String TAG = "TextureViewTouchListener";
    private TextureView textureView;
    private int mode;

    private float distance;
    private float preDistance;
    private float lastDistance;

    private PointF mid;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private float defZoomLevel = 1f;
    private float zoomLevel = 1f;
    private float lastZoomLevel = 0f;

    private CamManager mCamManager;

    public TextureViewTouchListener(CamManager camManager) {
        mCamManager = camManager;
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
        textureView = (TextureView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //单个手指触摸
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                mode = 1;
                break;
            //两指触摸
            case MotionEvent.ACTION_POINTER_DOWN:
                preDistance = getDistance(event);
                Log.d(TAG, "ACTION_POINTER_DOWN," + "preDistance:" + preDistance);
                //当两指间距大于10时，计算两指中心点
                if (preDistance > 10f) {

                    mode = 2;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                mode = 0;
                zoomLevel = defZoomLevel;
                lastZoomLevel = 0f;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "ACTION_POINTER_UP");
                mode = 0;
                zoomLevel = defZoomLevel;
                lastZoomLevel = 0f;
                break;
            case MotionEvent.ACTION_MOVE:
                //当两指缩放，计算缩放比例
                if (mode == 2) {
                    distance = getDistance(event);
                    float deltaDistance = distance - preDistance;
                    Log.d(TAG, "ACTION_MOVE," + " deltaDistance:" + deltaDistance);
                    if (deltaDistance > 1f) {
                        //两指间有滑动，需要zoom
                        mCamManager.zoomIn();
                    }else if(deltaDistance < -1f){
                        mCamManager.zoomOut();
                    }
                    preDistance = distance;
                }
                break;
        }
        //进行缩放
        Log.d(TAG, "do zoom");

        return true;
    }

    /*获取两指之间的距离*/
    private float getDistance(MotionEvent event) {
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        float distance = (float) Math.sqrt(x * x + y * y);//两点间的距离
        return distance;
    }


}
