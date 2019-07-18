package com.example.zoomprogressview.custUI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;


public class ZoomBar extends LinearLayout {

    private String TAG = "zoombar";

    private View mView1;
    private View mView2;
    private View mView3;
    private View mView4;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public ZoomBar(Context context) {
        super(context);
    }

    public ZoomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(Color.RED);
        canvas.drawLine(100, 100, 200, 500, p);// 画线





    }
}
