package com.example.zoomprogressview.zoomRuler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.zoomprogressview.R;

public class ZoomCircleIndicator extends View {

    private String TAG = "ZoomCircleIndicator";
    protected String text;
    protected int bgColor;
    protected int circleColor;
    protected int textColor = Color.WHITE;
    protected int bgAttrColor;
    protected int circleAttrColor;
    private int strokeWidth = 3;
    protected Canvas mCanvas;
    private boolean onZoom = false;

    public ZoomCircleIndicator(Context context) {
        this(context, null);
    }

    public ZoomCircleIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCircleTextView);
        text = typedArray.getString(R.styleable.MyCircleTextView_text);
        bgAttrColor = typedArray.getColor(R.styleable.MyCircleTextView_bgColor, Color.TRANSPARENT);
        circleAttrColor = typedArray.getColor(R.styleable.MyCircleTextView_circleColor, Color.WHITE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Log.d(TAG, "onDraw,onZoom:"+onZoom);
        mCanvas = canvas;
        bgColor = onZoom ? Color.WHITE : bgAttrColor;
        circleColor = onZoom ? Color.WHITE : circleAttrColor;
        textColor = onZoom ? Color.GRAY : Color.WHITE;
        drawBackground();
        drawCircle();
        drawText();
        super.onDraw(canvas);
    }

    private void drawBackground() {
        //绘制灰色背景
        Paint mbgPaint = new Paint();
        mbgPaint.setColor(bgColor);
        //抗锯齿
        mbgPaint.setAntiAlias(true);
        mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2 - strokeWidth * 2, mbgPaint);
    }

    private void drawCircle() {
        //画白色外圈
        Paint mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStrokeWidth((float) strokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setAntiAlias(true);
        mCanvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2 - strokeWidth * 2, mCirclePaint);
    }

    private void drawText() {
        //绘制文字
        if (text == null) {
            text = "x1";
        }
        Paint mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(25);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = getHeight() / 2 + distance;
        //Log.d(TAG, "drawText,baseline:"+baseline);
        mCanvas.drawText(text, getWidth() / 2, baseline, mTextPaint);
    }

    public void setOnZoomState(boolean b) {
        //Log.d(TAG, "setOnZoomState");
        onZoom = b;
        postInvalidate();
    }

    public void setText(String s) {
        //Log.d(TAG, "setText,s:" + s);
        if (s == null) {
            text = "";
        } else {
            text = s;
        }
        postInvalidate();
    }

}
