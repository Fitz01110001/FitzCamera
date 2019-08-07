package com.example.zoomprogressview.zoomUI;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;


public class ZoomProgressLayout extends LinearLayout {

    private String TAG = "ZoomProgressLayout";


    public ZoomProgressLayout(Context context) {
        super(context);
    }

    public ZoomProgressLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomProgressLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ZoomProgressLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG,"changed:"+changed);
    }
}
