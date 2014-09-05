package com.brianco.multiplicationvisualizer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.brianco.multiplicationvisualizer.R;

public class MultiplicationTable extends View {

    public static final int MIN_NUMBER = 2;

    private static final int DEFAULT_NUMBER = 16;

    private int mNumber;
    private OnDrawFinishListener mOnDrawFinishedListener = null;
    private float mWW;
    private float mHH;
    private Paint mWhiteTextPaint;
    private Paint mBlackTextPaint;
    private Paint mBackgroundPaint;

    public MultiplicationTable(Context context) {
        super(context);
        mNumber = DEFAULT_NUMBER;
        init();
    }

    public MultiplicationTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MultiplicationTable,
                0, 0);
        try {
            mNumber = a.getInteger(R.styleable.MultiplicationTable_number, DEFAULT_NUMBER);
            if (mNumber < MIN_NUMBER) {
                throw new RuntimeException("Make the number at least 2");
            }
        } finally {
            a.recycle();
        }
        init();
    }

    public static interface OnDrawFinishListener {
        void onDrawFinished(int number);
    }

    public void setOnDrawFinishedListener(OnDrawFinishListener onDrawFinishedListener) {
        mOnDrawFinishedListener = onDrawFinishedListener;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(final int number) {
        mNumber = number;
        if (mNumber < MIN_NUMBER) {
            throw new RuntimeException("Make the number at least 2");
        }
        invalidate();
        //requestLayout();
    }

    private void init() {
        mWhiteTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWhiteTextPaint.setColor(getResources().getColor(android.R.color.white));

        mBlackTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlackTextPaint.setColor(getResources().getColor(android.R.color.black));

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        mWW = ww;
        mHH = hh;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int number = mNumber;
        final float tileWidth, tileHeight;

        /*mTileWidth = mWW / (number - 1);
        mTileHeight = mHH / (number - 1);*/

        // make them squares
        if (mWW <= mHH) {
            tileWidth = tileHeight = mWW / (number - 1);
        } else {
            tileWidth = tileHeight = mHH / (number - 1);
        }

        mWhiteTextPaint.setTextSize(48f / 72 * tileWidth);
        mBlackTextPaint.setTextSize(48f / 72 * tileWidth);

        float currX = getPaddingLeft();
        float currY = getPaddingTop();

        for (int i = 1; i <= number - 1; i++) {
            mBackgroundPaint.setColor(getBackgroundColor(i, number));
            canvas.drawRect(currX, currY, currX + tileWidth,
                    currY + tileHeight,
                    mBackgroundPaint);
            final String text = String.valueOf(i);
            canvas.drawText(text,
                    currX + tileWidth / 2 - mWhiteTextPaint.measureText(text) / 2,
                    currY + tileHeight * 0.9f, mWhiteTextPaint);
            currX += tileWidth;
        }
        currX = getPaddingLeft();
        currY += tileHeight;

        for (int i = 2; i <= number - 1; i++) {
            for (int j = 1; j <= number - 1; j++) {
                final int num = (j == 1) ? i : (i * j) % number;

                mBackgroundPaint.setColor(getBackgroundColor(num, number));
                canvas.drawRect(currX, currY, currX + tileWidth,
                        currY + tileHeight,
                        mBackgroundPaint);
                final String text = String.valueOf(num);
                if (num == 0) {
                    canvas.drawText(text,
                            currX + tileWidth / 2 - mBlackTextPaint.measureText(text) / 2,
                            currY + tileHeight * 0.9f, mBlackTextPaint);
                } else {
                    canvas.drawText(text,
                            currX + tileWidth / 2 - mWhiteTextPaint.measureText(text) / 2,
                            currY + tileHeight * 0.9f, mWhiteTextPaint);
                }
                currX += tileWidth;
            }
            currX = getPaddingLeft();
            currY += tileHeight;
        }

        if (mOnDrawFinishedListener != null) {
            mOnDrawFinishedListener.onDrawFinished(number);
        }
    }

    private static int getBackgroundColor(final int numCurr, final int numMax) {
        if (numCurr == 0) return Color.WHITE;
        final float redRatio = ((float) (numCurr - 1)) / (numMax - 2);
        final int red = (int) (redRatio * 255);
        final int blue = 255 - red;
        return Color.rgb(red, 0, blue);
    }
}
