package com.inorin.orankarl.radioplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by 王松 on 2016/10/21.
 */

public class LrcView extends View {

    private List<LrcBean> list;
    private TextPaint gPaint;
    private TextPaint hPaint;
    private int width = 0, height = 0;
    private int currentPosition = 0;
    private MediaPlayer player;
    private int lastPosition = 0;
    private int highLineColor;
    private int lrcColor;
    private int mode = 0;
    int lineHeight = 160;
    public final static int KARAOKE = 1;

    public void setHighLineColor(int highLineColor) {
        this.highLineColor = highLineColor;
    }

    public void setLrcColor(int lrcColor) {
        this.lrcColor = lrcColor;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    /**
     * 标准歌词字符串
     *
     * @param lrc
     */
    public void setLrc(String lrc) {
        list = LrcUtil.parseStr2List(lrc);
    }

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LrcView);
        highLineColor = ta.getColor(R.styleable.LrcView_lrcHighlightColor, getResources().getColor(R.color.colorLrc));
        lrcColor = ta.getColor(R.styleable.LrcView_lrcColor, getResources().getColor(android.R.color.darker_gray));
//        mode = ta.getInt(R.styleable.LrcView_lrcMode,mode);
        ta.recycle();
        gPaint = new TextPaint();
        gPaint.setAntiAlias(true);
        gPaint.setColor(lrcColor);
//        gPaint.setTextSize(80);
        gPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lrcTextSize));
        gPaint.setTextAlign(Paint.Align.CENTER);
        gPaint.setFakeBoldText(true);
        gPaint.setShadowLayer(1, 2, 2, R.color.colorBlack);
        hPaint = new TextPaint();
        hPaint.setAntiAlias(true);
        hPaint.setColor(highLineColor);
//        hPaint.setTextSize(80);
        hPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.lrcHighlightTextSize));
        hPaint.setTextAlign(Paint.Align.CENTER);
        hPaint.setFakeBoldText(true);
        hPaint.setShadowLayer(1, 2, 2, R.color.colorBlack);
        Log.d("lrc text size", String.valueOf(getResources().getDimensionPixelSize(R.dimen.lrcTextSize)));

        //set up line height
        Paint.FontMetrics fontMetrics = gPaint.getFontMetrics();
        float height = fontMetrics.descent - fontMetrics.ascent;
        lineHeight = (int)(1.5*height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width == 0 || height == 0) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
        if (list == null || list.size() == 0) {
            canvas.drawText("暂无歌词", width / 2, height / 2, gPaint);
            return;
        }

        getCurrentPosition();

//        drawLrc1(canvas);
        int currentMillis = player.getCurrentPosition();
        drawLrc2(canvas, currentMillis);
        long start = list.get(currentPosition).getStart();

        float v = (currentMillis - start) > 500 ? currentPosition * lineHeight : lastPosition * lineHeight + (currentPosition - lastPosition) * lineHeight * ((currentMillis - start) / 500f);
        setScrollY((int) v);
        if (getScrollY() == currentPosition * lineHeight) {
            lastPosition = currentPosition;
        }
        postInvalidateDelayed(50);
    }

    private void drawLrc2(Canvas canvas, int currentMillis) {
        if (mode == 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i == currentPosition) {
                    canvas.drawText(list.get(i).getLrc(), width / 2, height / 2 + lineHeight * i, hPaint);
                } else {
                    canvas.drawText(list.get(i).getLrc(), width / 2, height / 2 + lineHeight * i, gPaint);
                }
            }
        }else{
            for (int i = 0; i < list.size(); i++) {
                canvas.drawText(list.get(i).getLrc(), width / 2, height / 2 + lineHeight * i, gPaint);
            }
            String highLineLrc = list.get(currentPosition).getLrc();
            int highLineWidth = (int) gPaint.measureText(highLineLrc);
            int leftOffset = (width - highLineWidth) / 2;
            LrcBean lrcBean = list.get(currentPosition);
            long start = lrcBean.getStart();
            long end = lrcBean.getEnd();
            int i = (int) ((currentMillis - start) * 1.0f / (end - start) * highLineWidth);
            if (i > 0) {
                Bitmap textBitmap = Bitmap.createBitmap(i, lineHeight, Bitmap.Config.ARGB_8888);
                Canvas textCanvas = new Canvas(textBitmap);
                textCanvas.drawText(highLineLrc, highLineWidth / 2, lineHeight, hPaint);
                canvas.drawBitmap(textBitmap, leftOffset, height / 2 + lineHeight * (currentPosition - 1), null);
            }
        }
    }

    public void init() {
        currentPosition = 0;
        lastPosition = 0;
        setScrollY(0);
        invalidate();
    }

    private void drawLrc1(Canvas canvas) {
        String text = list.get(currentPosition).getLrc();
        canvas.drawText(text, width / 2, height / 2, hPaint);

        for (int i = 1; i < 10; i++) {
            int index = currentPosition - i;
            if (index > -1) {
                canvas.drawText(list.get(index).getLrc(), width / 2, height / 2 - lineHeight * i, gPaint);
            }
        }
        for (int i = 1; i < 10; i++) {
            int index = currentPosition + i;
            if (index < list.size()) {
                canvas.drawText(list.get(index).getLrc(), width / 2, height / 2 + lineHeight * i, gPaint);
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
            if (currentMillis > list.get(list.size() - 1).getStart()) {
                currentPosition = list.size() - 1;
                return;
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

    public void setLrcTextSize(int textSize) {
        gPaint.setTextSize(textSize);
        hPaint.setTextSize(textSize);
    }
}
