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
    private int lastX;
    private int transX;

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
    private final int START_SCLAE = -10;//开始的刻度值
    private int mInitScroll = 0;//初始刻度值
    private final int DEF_SCLAE = 0;//初始刻度值
    private int mWidth;//总宽度
    private int mHeight;//总高度

    private int mTextSize = 30;
    private int mScaleColor;//刻度以及文字的颜色

    private int mCurrentScale;//当前的刻度
    private int mScrollX;//上一次X轴滑动的距离
    private int mLastScrollX;
    private Scroller mScroller;
    private int mOffset;
    private ScrollCallback mScrollCallback;

    private int xa;
    private int xb;
    private int slidDirection = 0;
    private boolean lastStateWide = false;


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
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth, heightSize);
    }

    private void init() {
        Log.d(TAG, "init");
        mScreenScaleCount = 40;
        mScaleMargin = mViewWidth / mScreenScaleCount;
        mScaleTotalCount = 40;
        mWidth = mScaleMargin * mScaleTotalCount;
        mScroller = new Scroller(mContext);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mOffset = mHeight / 7;
        mTextPaint.setTextSize(mOffset);
        //对准初始刻度
        scrollTo(START_SCLAE * mScaleMargin, 0);
        mScrollX = getScrollX();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawScale(canvas);
        updateScale();
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

    private int updateScale() {
        int minScale = START_SCLAE;
        int maxScale = START_SCLAE + mScaleTotalCount;
        int scrollScale = (int) Math.rint((double) getScrollX() /
                (double) mScaleMargin);
        //初始刻度值+滚动刻度值
        mCurrentScale = scrollScale + mScreenScaleCount / 2 + START_SCLAE;
        if (mScrollCallback != null) {
            //超出最大值
            if (mCurrentScale > maxScale) {
                mCurrentScale = maxScale;
            }
            //低于最小值
            else if (mCurrentScale < minScale) {
                mCurrentScale = minScale;
            }
            float f = ((float) mCurrentScale + 10) / 10;
            mScrollCallback.updateScale(f);
        }
        return mCurrentScale;
    }

    public interface ScrollCallback {
        void updateScale(float scale);

        void upAtWide();

        void upAtDef();

    }

    public void setScrollCallback(ScrollCallback scrollCallback) {
        this.mScrollCallback = scrollCallback;
    }

    //设置总刻度
    public void setScaleTotalCount(int scaleTotalCount) {
        this.mScaleTotalCount = scaleTotalCount;
    }

    //设置第一个刻度值
    /*public void setStartScale(int startScale) {
        this.START_SCLAE = startScale;
    }*/

    //设置初始刻度值
    public void setInitScrollX(int initScrollX) {
        this.mInitScroll = initScrollX;
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
            int offSetX = mScaleMargin * (mScreenScaleCount / 2);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                    xa = (int) event.getRawX();
                    if (mScroller != null && mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    mLastScrollX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    xb = (int) event.getRawX();
                    slidDirection = xb - xa;
                    int ddx = (int) event.getRawX() - lastX;
                    int dx = mLastScrollX - x + mScrollX;
                    int scale = updateScale();
                    Log.d(TAG, "lastStateWide:" + lastStateWide + " slidDirection:" + slidDirection+" mCurrentScale:"+mCurrentScale);
                    if ((scale <= START_SCLAE && ddx > 0)) {
                        // Minimum range
                        Log.d(TAG, "停在wide");
                        scrollTo(-offSetX, 0);
                    } else if ((scale >= mScaleTotalCount - Math.abs(START_SCLAE)) && ddx < 0) {
                        // Maximum range
                        Log.d(TAG, "停在max");
                        scrollTo(offSetX, 0);
                    } else if (lastStateWide && slidDirection <=0 && mCurrentScale >= DEF_SCLAE) {
                        // wide to 1x, should stay at 1x
                        scrollTo(START_SCLAE * mScaleMargin, 0);
                    } else {
                        //Log.d(TAG, "dx:" + dx + " ddx:" + ddx + " slidDirection:" + slidDirection + " scale:" + scale);
                        scrollTo(dx, 0);
                    }
                    xa = (int) event.getRawX();
                    return true;
                case MotionEvent.ACTION_UP:
                    mScrollX = getScrollX();
                    lastStateWide = updateScale() < 0;
                    Log.d(TAG, "ACTION_UP,lastStateWide:" + lastStateWide);
                    if (mCurrentScale > -2 && mCurrentScale <= 0) {
                        // wide to 1x, Stay at 1x
                        scrollTo(START_SCLAE * mScaleMargin, 0);
                        postInvalidate();
                        mScrollCallback.upAtDef();
                        return true;
                    } else if (mCurrentScale <= -2) {
                        // 1x to wide, Stay at wide
                        scrollTo(-offSetX, 0);
                        postInvalidate();
                        mScrollCallback.upAtWide();
                        return true;
                    } else {//在范围内
                        transX = getScrollX() % mScaleMargin;
                        if (transX > (mScaleMargin / 2)) {
                            transX = -(mScaleMargin - transX);
                        }
                        mScrollX = getScrollX() - transX;
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

    public void updateRulerScale(float newScale) {
        // mScaleMargin =13;
        float dx = (((newScale - 1.0f) * 10) - Math.abs(START_SCLAE)) * mScaleMargin;
        Log.d(TAG, "updateRulerScale,newScale:" + newScale + " dx:" + dx + " START_SCLAE:" + START_SCLAE);
        scrollTo((int) dx, 0);
        postInvalidate();
    }

}
