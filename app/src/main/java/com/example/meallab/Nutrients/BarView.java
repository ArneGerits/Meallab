package com.example.meallab.Nutrients;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.meallab.R;

public class BarView extends View {

    //paint for drawing custom view
    private Paint barPaint;
    private Paint linePaint;
    private Paint textPaint;

    private DashPathEffect dash;

    // Colors
    private int barColor;
    private int indicatorColor;
    private int barBackgroundColor;

    private boolean drawsDottedLines = true;

    // Number representing the progress on the bar has be to at least 0.0f, (NOTE can
    // be higher than 1.0 to present more than 100%)
    private float percentProgress;

    private float indicatorWidth = 12.0f;
    private float vInset;
    private float hInset;

    // ---- Constructors ----

    public BarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        barPaint = new Paint();
        linePaint = new Paint();
        textPaint = new Paint();

        dash = new DashPathEffect(new float[]{30.0f, 15.0f}, 0);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.BarView, 0, 0);

        // Read the attribute values.
        try {
            //get the text and colors specified using the names in attrs.xml
            barColor = a.getInteger(R.styleable.BarView_barColor, 0);
            indicatorColor = a.getInteger(R.styleable.BarView_indicatorColor, 0);
            barBackgroundColor = a.getInteger(R.styleable.BarView_barBackgroundColor, 0);
            drawsDottedLines = a.getBoolean(R.styleable.BarView_drawsDottedLines, false);
            hInset = a.getFloat(R.styleable.BarView_horizontalInset, 0.0f);
        } finally {
            a.recycle();
        }
    }

    // ---- Drawing ----

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        // The vInset is always 10% of the height.
        this.vInset = Math.round(h / 10.0f);
        this.hInset = vInset;

        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setAntiAlias(true);

        // 1. Draw the background.
        barPaint.setColor(barBackgroundColor);

        canvas.drawRect(0 + this.hInset,
                0 + this.vInset,
                (w - this.hInset),
                h - this.vInset, barPaint);

        if (this.percentProgress > 0.0f) {
            // 2.Draw the progress.

            barPaint.setColor(barColor);

            canvas.drawRect(0 + this.hInset,
                    0 + this.vInset,
                    (w - this.hInset) * Math.min(this.percentProgress, 1.0f),
                    h - this.vInset, barPaint);
        }

        // 3. Draw dotted lines.
        if (this.drawsDottedLines && this.percentProgress < 1.0f) {

            float strokeW = Math.round(h / 12.0f);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(barColor);
            linePaint.setStrokeWidth(strokeW);
            linePaint.setPathEffect(dash);

            Path line = new Path();
            float y = this.vInset + 0.5f * strokeW;
            line.moveTo((w - this.hInset) * Math.min(this.percentProgress, 1.0f) + this.hInset, y);
            line.lineTo(w - this.hInset, y);
            canvas.drawPath(line, linePaint);

            y = h - this.vInset - 0.5f * strokeW;
            line.moveTo((w - this.hInset) * Math.min(this.percentProgress, 1.0f) + this.hInset, y);
            line.lineTo(w - this.hInset, y);
            canvas.drawPath(line, linePaint);
        }

        // 4. Draw the indicators.

        barPaint.setColor(indicatorColor);

        float left = (float) Math.floor(this.hInset - 0.5f * indicatorWidth);
        float right = left + indicatorWidth;

        // Left indicator
        canvas.drawRect(left, 0, right, h, barPaint);

        float multiplier = Math.max(this.percentProgress, 1.0f);

        left = ((w - 2 * this.hInset) * 1.0f / multiplier) + this.hInset - 0.5f * indicatorWidth;
        right = left + indicatorWidth;

        // Right indicator
        canvas.drawRect(left, 0, right, h, barPaint);

    }

    public RectF getBarRect() {
        this.vInset = Math.round(getMeasuredHeight() / 10.0f);

        RectF rect = new RectF(0 + this.vInset,
                0 + this.vInset,
                (getMeasuredWidth() - this.vInset) * Math.min(this.percentProgress, 1.0f),
                getMeasuredHeight() - this.vInset);
        return rect;
    }

    public float getLeftIndicatorLocation() {
        this.vInset = Math.round(getMeasuredHeight() / 10.0f);

        return this.vInset;
    }

    public float getRightIndicatorLocation() {
        this.vInset = Math.round(getMeasuredHeight() / 10.0f);

        float multiplier = Math.max(this.percentProgress, 1.0f);

        float left = ((getMeasuredWidth() - 2 * this.vInset) * 1.0f / multiplier) + this.vInset - 0.5f * indicatorWidth;
        float right = left + indicatorWidth;

        // Right indicator
        RectF r = new RectF(left, 0, right, getMeasuredHeight());
        return r.centerX();
    }
    // ---- Getters/Setters ----

    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;

        invalidate();
        requestLayout();
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;

        invalidate();
        requestLayout();
    }

    public float getPercentProgress() {
        return percentProgress;
    }

    public void setPercentProgress(float percentProgress) {
        this.percentProgress = Math.max(percentProgress, 0.0f);

        invalidate();
        requestLayout();
    }

    public int getBarBackgroundColor() {
        return barBackgroundColor;
    }

    public void setBarBackgroundColor(int barBackgroundColor) {
        this.barBackgroundColor = barBackgroundColor;

        invalidate();
        requestLayout();
    }

    public boolean isDrawsDottedLines() {
        return drawsDottedLines;
    }

    public void setDrawsDottedLines(boolean drawsDottedLines) {
        this.drawsDottedLines = drawsDottedLines;

        invalidate();
        requestLayout();
    }

    public float getIndicatorWidth() {
        return indicatorWidth;
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
    }
}