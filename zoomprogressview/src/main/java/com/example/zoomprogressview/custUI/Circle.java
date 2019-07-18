package com.example.zoomprogressview.custUI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class Circle extends View {

    private String TAG = "Circle";
    private Canvas mCanvas;
    private String text;
    private int strokeWidth = 3;
    private int w;
    private int h;


    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public Circle(Context context) {
        super(context);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see #View(Context, AttributeSet, int)
     */
    public Circle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        w = widthMeasureSpec;
        h = heightMeasureSpec;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        Log.d(TAG, "onDraw");
        //绘制灰色背景
        Paint mbgPaint = new Paint();
        mbgPaint.setColor(Color.GRAY);
        //抗锯齿
        mbgPaint.setAntiAlias(true);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2 - strokeWidth * 2, mbgPaint);

        //画白色外圈
        Paint mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.RED);
        mCirclePaint.setStrokeWidth((float) strokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setAntiAlias(true);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2 - strokeWidth * 2, mCirclePaint);

        //绘制文字
        Paint mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(25);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = getHeight() / 2 + distance;
        canvas.drawText(text, getWidth() / 2, baseline, mTextPaint);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {





        return super.onTouchEvent(event);
    }

    public void setText(String s) {
        //Log.d(TAG, "setText:" + s);
        if (s == null) {
            text = "";
        } else {
            text = s;
        }
    }





}
