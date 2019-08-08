package com.example.zoomprogressview.zoomRuler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;


public class ZoomScaleRuler extends View {

    private String TAG = "ZoomScaleRuler";
    private int strokeWidth = 3;
    private final int defaultscale = 10;

    private Context mContext;
    private Paint mScalePaint;
    private Paint mTextPaint;

    private final String WIDESCALE = "W";
    private final String DEFSCALE = "x1";
    private final String MAXSCALE = "x4";

    private final int TRANSPARENT_GREY = 0x80BEBEBE;

    private int mViewWidth;//view宽度
    private int mScreenScaleCount;//一个屏幕内允许显示刻度的个数
    private int mScaleMargin;//刻度的间隔

    private int mScaleTotalCount;//总刻度
    private int mStartScale = -10;//开始的刻度值
    private int mInitScrollX = 0;//初始刻度值
    private int mWidth;//总宽度
    private int mHeight;//总高度

    private int mTextSize = 30;
    private int mScaleHeight = 45;
    private int mScaleColor;//刻度以及文字的颜色

    private int mCurrentScale;//当前的刻度
    private int mScrollX;//上一次X轴滑动的距离
    private int mLastScale;
    private Scroller mScroller;
    private int mOffset;
    private ScrollCallback mScrollCallback;

    private int lastX;
    private int lastY;


    public ZoomScaleRuler(Context context) {
        super(context);
    }

    public ZoomScaleRuler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomScaleRuler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScalePaint = new Paint();
        mScalePaint.setAntiAlias(true);
        mScalePaint.setStrokeWidth(3);
        mScalePaint.setColor(Color.WHITE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        init();
        Log.d(TAG, "onMeasure,mViewWidth:" + mViewWidth);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, heightSize);
    }

    private void init() {
        Log.d(TAG, "init");
        mScreenScaleCount = 40;
        mScaleMargin = mViewWidth / mScreenScaleCount;
        mScaleTotalCount = 40;
        mInitScrollX = mScaleTotalCount / 2;
        mWidth = mScaleMargin * mScaleTotalCount;
        mScroller = new Scroller(mContext);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mScaleHeight = h / 5;
        mOffset = mHeight / 7;
        mTextPaint.setTextSize(mOffset);
        //对准初始刻度
        scrollTo(mStartScale * mScaleMargin, 0);
        mScrollX = getScrollX();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        changeScale();
    }

    private void drawScale(Canvas canvas) {
        for (int i = 0; i <= mScaleTotalCount; i++) {
            if (i == 0) {
                drawPoint(canvas, WIDESCALE, i);
                i = i + 2;
            } else if (i == defaultscale) {
                drawPoint(canvas, DEFSCALE, i);
                i = i + 2;
            } else if (i == mScaleTotalCount) {
                drawPoint(canvas, MAXSCALE, i);
            } else if (i != defaultscale - 1 && i != defaultscale - 2 && i != mScaleTotalCount - 1 && i != mScaleTotalCount - 2) {
                mScalePaint.setStrokeWidth(1);
                mScalePaint.setColor(Color.WHITE);
                canvas.drawCircle(i * mScaleMargin, mHeight / 2, 2, mScalePaint);
            }
        }
    }

    private void drawPoint(Canvas canvas, String text, int i) {
        mScalePaint.setStrokeWidth(6);
        mScalePaint.setColor(TRANSPARENT_GREY);
        mScalePaint.setAntiAlias(true);
        canvas.drawCircle(i * mScaleMargin, getHeight() / 2, getHeight() / 2 - strokeWidth * 2, mScalePaint);

        Paint mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(25);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = getHeight() / 2 + distance;
        canvas.drawText(text, i * mScaleMargin, baseline, mTextPaint);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
    }

    private int changeScale() {
        int minScale = mStartScale;
        int maxScale = mStartScale + mScaleTotalCount;
        int scrollScale = (int) Math.rint((double) getScrollX() /
                (double) mScaleMargin);
        //初始刻度值+滚动刻度值
        mCurrentScale = scrollScale + mScreenScaleCount / 2 + mStartScale;
        int ss = mCurrentScale;
        if (mScrollCallback != null) {
            //超出最大值
            if (ss > maxScale) {
                ss = maxScale;
            }
            //低于最小值
            else if (ss < minScale) {
                ss = minScale;
            }
            float f = ((float) ss + 10) / 10;
            mScrollCallback.setScale(f);
        }
        return mCurrentScale;
    }

    public interface ScrollCallback {
        void setScale(float scale);
    }

    public void setScrollCallback(ScrollCallback scrollCallback) {
        this.mScrollCallback = scrollCallback;
    }

    //设置总刻度
    public void setScaleTotalCount(int scaleTotalCount) {
        this.mScaleTotalCount = scaleTotalCount;
    }

    //设置第一个刻度值
    public void setStartScale(int startScale) {
        this.mStartScale = startScale;
    }

    //设置初始刻度值
    public void setInitScrollX(int initScrollX) {
        this.mInitScrollX = initScrollX;
        mWidth = mScaleMargin * mScaleTotalCount;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
    }

    public void setScaleColor(int scaleColor) {
        this.mScaleColor = scaleColor;
        mScalePaint.setColor(mScaleColor);
        mTextPaint.setColor(mScaleColor);
    }

    private RulerOnTouchListener mRulerOnTouchListener = new RulerOnTouchListener() {

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int transX;
            int offSetX = mScaleMargin * (mScreenScaleCount / 2);
            Log.d(TAG, "offSetX:" + offSetX);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mScroller != null && mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    mLastScale = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = mLastScale - x + mScrollX;
                    changeScale();
                    Log.d(TAG, "dx:" + dx);
                    scrollTo(dx, 0);
                    return true;
                case MotionEvent.ACTION_UP:
                    mScrollX = getScrollX();
                    Log.d(TAG, "mScrollX:" + mScrollX + " mWidth:" + mWidth + " offSetX:" + offSetX);
                    if (mScrollX > mWidth - offSetX) {//超出最大值
                        transX = getScrollX() - mWidth + offSetX;
                        mScrollX = mWidth - offSetX;
                        Log.i(TAG, "大于最大值");
                    } else if (getScrollX() < -offSetX) {//小于最小值
                        transX = getScrollX() + offSetX;
                        mScrollX = -offSetX;
                        Log.i(TAG, "小于最小值");
                    } else {//在范围内
                        transX = getScrollX() % mScaleMargin;
                        if (transX > (mScaleMargin / 2)) {
                            transX = -(mScaleMargin - transX);
                        }
                        mScrollX = getScrollX() - transX;
                        Log.i(TAG, "在范围内");
                    }
                    mScroller.startScroll(getScrollX(), 0, -transX, 0);
                    postInvalidate();
                    return true;
            }
            return true;
        }
    };

    public RulerOnTouchListener getRulerOnTouchListener() {
        return mRulerOnTouchListener;
    }

    public interface RulerOnTouchListener {
        boolean onTouchEvent(MotionEvent event);
    }

}
