package com.linyouye.slideswitch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Lin You Ye on 2015/10/9.
 */
public class SlideSwitch extends View {

    public SlideSwitch(Context context) {
        super(context);

        init();

    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private static final boolean D = true;
    private static final String TAG = "lyy-SlideSwitch";

    private int WIDTH;
    private int HEIGHT;
    private int POSITION_LEFT;
    private int POSITION_RIGHT;
    private int SLIDER_RADIUS;
    private int mPosition;

    private boolean mIsChecked = false;
    private boolean mIsSliding = false;
    private boolean mIsPressed = false;

    private int COLOR_CHECKED = 0xff1098ff;
    private int COLOR_UNCHECKED = 0xffcccccc;

    private int COLOR_SLIDER = 0xffffffff;

    private Paint mBgPaint;
    private Paint mSliderPaint;

    private MyTouchListener mListener;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    private void init() {
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mSliderPaint = new Paint();
        mSliderPaint.setColor(COLOR_SLIDER);
        mSliderPaint.setAntiAlias(true);
        mListener = new MyTouchListener();

    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        mOnCheckedChangeListener = l;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        if (mIsChecked == checked) {
            return;
        }
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, checked);
        }
        this.mIsChecked = checked;
        postDelayed(
                new SlideRunnable(checked ? POSITION_RIGHT : POSITION_LEFT), 30);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int midY = getHeight() / 2;
        mBgPaint.setColor(calculateBgColor());
        canvas.drawCircle(POSITION_LEFT, midY, SLIDER_RADIUS, mBgPaint);
        canvas.drawCircle(POSITION_RIGHT, midY, SLIDER_RADIUS, mBgPaint);
        canvas.drawRect(POSITION_LEFT, (getHeight() - HEIGHT) / 2,
                POSITION_RIGHT, (getHeight() + HEIGHT) / 2, mBgPaint);

        canvas.drawCircle(mPosition, midY, SLIDER_RADIUS - 2, mSliderPaint);

        if (mIsPressed) {
            canvas.drawCircle(mPosition, midY, SLIDER_RADIUS / 2, mBgPaint);
            canvas.drawCircle(mPosition, midY, SLIDER_RADIUS / 2 - 2,
                    mSliderPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        WIDTH = getWidth();
        HEIGHT = Math.min(getHeight(), getWidth() / 2);
        SLIDER_RADIUS = HEIGHT / 2;
        POSITION_LEFT = SLIDER_RADIUS;
        POSITION_RIGHT = WIDTH - SLIDER_RADIUS;
        mPosition = POSITION_LEFT;
        if (D)
            Log.i(TAG, "onMeasure");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsSliding) {
            return false;
        } else {
            return mListener.onTouch(this, event);
        }
    }

    class SlideRunnable implements Runnable {

        private static final int DIVIDER = 10;
        private int mTargetPos;
        private int mDivider;

        public SlideRunnable(int targetPos) {
            mTargetPos = targetPos;

            mDivider = (mTargetPos > mPosition) ? DIVIDER : -DIVIDER;

        }

        @Override
        public void run() {

            if (mTargetPos == mPosition) {

                mIsSliding = false;
                postInvalidate();
                return;
            }

            mIsSliding = true;

            if (Math.abs(mPosition - mTargetPos) < DIVIDER) {
                mPosition = mTargetPos;
            } else {
                mPosition += mDivider;
            }
            postInvalidate();
            postDelayed(this, 10);

        }

    }

    class MyTouchListener implements OnTouchListener {

        private float lastX;
        private float startX;
        private long startTime;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    startTime = System.currentTimeMillis();
                    startX = event.getX();
                    lastX = event.getX();
                    mIsPressed = true;
                    break;
                case MotionEvent.ACTION_MOVE:

                    mPosition += event.getX() - lastX;
                    if (D)
                        Log.i(TAG, "pos:" + mPosition);

                    mPosition = Math.max(POSITION_LEFT, mPosition);
                    mPosition = Math.min(POSITION_RIGHT, mPosition);
                    lastX = event.getX();

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    mIsPressed = false;

                    if (System.currentTimeMillis() - startTime < 200
                            && Math.abs(event.getX() - startX) < 10) {
                        setChecked(!mIsChecked);
                    } else {

                        if (mIsChecked == (mPosition > WIDTH / 2)) {
                            postDelayed(new SlideRunnable(
                                            mIsChecked ? POSITION_RIGHT : POSITION_LEFT),
                                    30);
                        } else {
                            setChecked(mPosition > WIDTH / 2);
                        }

                    }

                    break;

            }
            invalidate();

            return true;
        }

    }

    interface OnCheckedChangeListener {
        void onCheckedChanged(View view, boolean isChecked);
    }

    private int calculateBgColor() {

        int r1 = (COLOR_UNCHECKED) >> 16 & 0x000000ff;
        int g1 = (COLOR_UNCHECKED >> 8) & 0x000000ff;
        int b1 = COLOR_UNCHECKED & 0x000000ff;

        int r2 = (COLOR_CHECKED) >> 16 & 0x000000ff;
        int g2 = (COLOR_CHECKED >> 8) & 0x000000ff;
        int b2 = COLOR_CHECKED & 0x000000ff;

        float ratio = 1f * (mPosition - POSITION_LEFT)
                / (POSITION_RIGHT - POSITION_LEFT);

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return 0xff000000 + (r << 16) + (g << 8) + b;

    }
}
