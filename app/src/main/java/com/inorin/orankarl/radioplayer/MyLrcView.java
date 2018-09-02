package com.inorin.orankarl.radioplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextPaint;
import android.util.AndroidException;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class MyLrcView extends View {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private int width = 0, height = 0;
    private List<LrcBean> list;
    private Paint paintNormal, paintHighlight;
    private int highlightColor, lrcColor;
    private MediaPlayer player;
    private int currentPosition = 0, lastPosition = 0;

    public void setHighlightColor(int highlightColor) {
        this.highlightColor = highlightColor;
    }

    public void setLrcColor(int lrcColor) {
        this.lrcColor = lrcColor;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public void setLrc(String lrc) {
        list = LrcUtil.parseStr2List(lrc);
    }

    public MyLrcView(Context context) {
        super(context);
        init(null, 0);
    }

    public MyLrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MyLrcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LrcView, defStyle, 0);

        highlightColor = a.getColor(R.styleable.LrcView_lrcHighlightColor, getResources().getColor(R.color.colorLrcHighlight));
        lrcColor = a.getColor(R.styleable.LrcView_lrcColor, getResources().getColor(R.color.colorLrc));
        a.recycle();

        paintHighlight = new Paint();
        paintHighlight.setAntiAlias(true);
        paintHighlight.setColor(highlightColor);
        paintHighlight.setTextSize(36);
        paintHighlight.setTextAlign(Paint.Align.CENTER);

        paintNormal = new Paint();
        paintNormal.setAntiAlias(true);
        paintNormal.setColor(lrcColor);
        paintNormal.setTextSize(36);
        paintNormal.setTextAlign(Paint.Align.CENTER);

        mExampleString = a.getString(
                R.styleable.LrcView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.LrcView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.LrcView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.LrcView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.LrcView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width == 0 || height == 0) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
        if (list == null || list.size() == 0) {
            canvas.drawText("无歌词", width/2, height/2, paintHighlight);
            return;
        }

        getCurrentPosition();

        int currentMillis = player.getCurrentPosition();

        drawLrc2(canvas, currentMillis);
        long start = list.get(currentPosition).getStart();
        float v = (currentMillis - start) > 500 ? currentPosition * 80 : lastPosition * 80 + (currentPosition - lastPosition) * 80 * ((currentMillis - start) / 500f);
        setScrollY((int) v);
        if (getScrollY() == currentPosition * 80) {
            lastPosition = currentPosition;
        }
        postInvalidateDelayed(100);


//        // TODO: consider storing these as member variables to reduce
//        // allocations per draw cycle.
//        int paddingLeft = getPaddingLeft();
//        int paddingTop = getPaddingTop();
//        int paddingRight = getPaddingRight();
//        int paddingBottom = getPaddingBottom();
//
//        int contentWidth = getWidth() - paddingLeft - paddingRight;
//        int contentHeight = getHeight() - paddingTop - paddingBottom;
//
//        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);
//
//        // Draw the example drawable on top of the text.
//        if (mExampleDrawable != null) {
//            mExampleDrawable.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight);
//            mExampleDrawable.draw(canvas);
//        }
    }

    public void init() {
        currentPosition = 0;
        lastPosition = 0;
        setScrollY(0);
        invalidate();
    }

    private void drawLrc2(Canvas canvas, int currentMillis) {
        for (int i = 0; i < list.size(); i++) {
            if (i == currentPosition) {
                canvas.drawText(list.get(i).getLrc(), width/2, height / 2 + 80*i, paintHighlight);
            } else {
                canvas.drawText(list.get(i).getLrc(), width/2, height/2+80*i, paintNormal);
            }
        }
    }

    private void getCurrentPosition() {
        try {
            int currentMillis = player.getCurrentPosition();
            if (currentMillis < list.get(0).getStart()) {
                currentPosition = 0;
                return;
            }
            if (currentMillis > list.get(list.size()-1).getStart()) {
                currentPosition = list.size() - 1;
            }
            for (int i = 0; i < list.size(); i++) {
                if (currentMillis >= list.get(i).getStart() && currentMillis < list.get(i).getEnd()) {
                    currentPosition = i;
                    return;
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
            postInvalidateDelayed(100);
        }
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
