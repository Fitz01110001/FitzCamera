package com.example.zoomprogressview.zoomRuler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.zoomprogressview.R;

import java.util.Locale;

import static android.view.MotionEvent.ACTION_SCROLL;

public class ZoomScaleViewGroup extends FrameLayout {

    private String TAG = "ZoomScaleViewGroup";

    private ZoomScaleRuler mZoomScaleRuler;//刻度尺
    private ZoomCircleIndicator mZoomCircleIndicator;//指示器

    private int mScaleColor;//刻度以及文字颜色
    private int mStartScale;//开始刻度
    private int mTotalScale;//总刻度
    private int mInitScale;//初始化时移动的刻度(用来显示初始值)
    private int mTextSize;//初始化时移动的刻度
    private final int DELAY_ON_TOUCH_UP = 2000;
    private final int DELAY_ON_UPDATE = 5000;
    private ZoomScaleRuler.RulerOnTouchListener mRulerOnTouchListener;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mZoomCircleIndicator.setOnZoomState(false);
            mZoomScaleRuler.setVisibility(GONE);
        }
    };
    private ZoomRulerListener zoomRulerListener;

    private final String STRING_W_LC = "w";
    private final String STRING_W_C = "W";
    private final String STRING_X_LC = "x";
    private final String STRING_X1 = "x1";
    private final String STRING_X4 = "x4";


    public ZoomScaleViewGroup(@NonNull Context context) {
        super(context);
    }

    public ZoomScaleViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomScaleViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new Handler();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZoomRuler);
        mScaleColor = array.getColor(R.styleable.ZoomRuler_scaleColor, Color.WHITE);
        mStartScale = array.getInteger(R.styleable.ZoomRuler_startScale, 0);
        mTotalScale = array.getInteger(R.styleable.ZoomRuler_scaleTotalCount, 10);
        mInitScale = array.getInteger(R.styleable.ZoomRuler_initScrollX, 50);
        mTextSize = (int) array.getDimension(R.styleable.ZoomRuler_rulerTextSize, 30);
        init(context);
        drawRuler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(Context context) {
        Log.d(TAG, "init");
        LayoutInflater.from(context).inflate(R.layout.zoom_ruler, this, true);
        //绘制指示器
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        mZoomCircleIndicator = new ZoomCircleIndicator(context);
        addView(mZoomCircleIndicator, lp);

        mZoomCircleIndicator.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "ACTION_DOWN:");
                        showZoom();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "ACTION_UP:");
                        mHandler.postDelayed(mRunnable, DELAY_ON_TOUCH_UP);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "ACTION_MOVE:");
                        mHandler.removeCallbacks(mRunnable);
                        break;
                }
                mRulerOnTouchListener.onTouchEvent(event);
                return true;
            }
        });
    }

    private void showZoom(){
        mHandler.removeCallbacks(mRunnable);
        mZoomCircleIndicator.setOnZoomState(true);
        mZoomScaleRuler.setVisibility(VISIBLE);
    }

    private void drawRuler() {
        //绘制刻度尺
        mZoomScaleRuler = findViewById(R.id.zoom_ScaleRuler);
        mRulerOnTouchListener = mZoomScaleRuler.getRulerOnTouchListener();
        mZoomScaleRuler.setScaleTotalCount(mTotalScale);//设置总刻度
        //mZoomScaleRuler.setStartScale(mStartScale);//设置第一个刻度值

        mZoomScaleRuler.setInitScrollX(mInitScale);//设置初始化时滚动的刻度
        mZoomScaleRuler.setTextSize(mTextSize);//设置文字的大小
        mZoomScaleRuler.setScaleColor(mScaleColor);//设置文字和刻度的颜色
        mZoomScaleRuler.requestLayout();//重新测量
        mZoomScaleRuler.postInvalidate();//重新绘制
        mZoomScaleRuler.setScrollCallback(new ZoomScaleRuler.ScrollCallback() {

            @Override
            public void onScale(float scale) {
                if (scale < 1.0f) {
                    mZoomCircleIndicator.setText(STRING_W_C);
                } else {
                    if(zoomRulerListener != null){
                        zoomRulerListener.normalZoom(scale);
                    }
                    mZoomCircleIndicator.setText(STRING_X_LC + scale);
                }
            }

            @Override
            public void upAtWide() {
                zoomRulerListener.switchToWide();
            }

            @Override
            public void upAtDef() {
                zoomRulerListener.switchToBack();
            }

        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setZoomRulerListener(ZoomRulerListener zoomRulerListener) {
        this.zoomRulerListener = zoomRulerListener;
    }

    public interface ZoomRulerListener{
        void normalZoom(float scale);
        void switchToWide();
        void switchToBack();
    }

    public void updateRatio(String ratio){
        Log.d(TAG, "updateRatio:" + ratio);
        String[] s = ratio.split(STRING_X_LC);
        float f = Float.parseFloat(s[1]);
        if(s.length == 2){
            mZoomCircleIndicator.setText(ratio);
            showZoom();
            mZoomScaleRuler.updateRulerScale(f);
            mHandler.postDelayed(mRunnable, DELAY_ON_UPDATE);
        }
    }

    public void setZoomRuler2Wide(){
        mZoomCircleIndicator.setText(STRING_W_C);
        mZoomScaleRuler.updateRulerScale(0);
    }

    public void setZoomRuler2BackDef(){
        mZoomCircleIndicator.setText(STRING_X1);
        mZoomScaleRuler.updateRulerScale(1);
    }

    public void setZoomRuler2BackMax(){
        mZoomCircleIndicator.setText(STRING_X4);
        mZoomScaleRuler.updateRulerScale(4);
    }
}
