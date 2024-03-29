package com.example.zoomprogressview.zoomUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.zoomprogressview.R;

public class ZoomCircle extends View {

    private String TAG = "ZoomCircle";
    protected String text;
    protected int bgColor;
    protected int circleColor;
    protected int textColor = Color.WHITE;
    protected int bgAttrColor;
    protected int circleAttrColor;
    private int strokeWidth = 3;
    protected Canvas mCanvas;
    private boolean onZoom = false;


    public ZoomCircle(Context context) {
        super(context);
    }

    public ZoomCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCircleTextView);
        text = typedArray.getString(R.styleable.MyCircleTextView_text);
        bgAttrColor = typedArray.getColor(R.styleable.MyCircleTextView_bgColor, Color.GRAY);
        circleAttrColor = typedArray.getColor(R.styleable.MyCircleTextView_circleColor, Color.GRAY);

        Log.d(TAG, "string:" + text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
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
        mCanvas.drawText(text, getWidth() / 2, baseline, mTextPaint);
    }

    public void setOnZoomState(boolean b) {
        onZoom = b;
        invalidate();
    }

    public void setText(String s) {
        //Log.d(TAG, "setText:" + s);
        if (s == null) {
            text = "";
        } else {
            text = s;
        }
        drawText();
        invalidate();
    }



}
