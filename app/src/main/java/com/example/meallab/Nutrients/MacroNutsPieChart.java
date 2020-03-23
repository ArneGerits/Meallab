package com.example.meallab.Nutrients;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.meallab.R;

public class MacroNutsPieChart extends View {

    //circle and text colors
    private int proteinColor;
    private int carbColor;
    private int fatColor;

    private float percentFat     = 0.0f;
    private float percentCarb    = 0.0f;
    private float percentProtein = 0.0f;

    private float inset = 0.0f;

    //paint for drawing custom view
    private Paint circlePaint;

    // ---- Constructors ----

    public MacroNutsPieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        circlePaint = new Paint();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MacroNutsPieChart, 0, 0);

        // Read the attribute values.
        try {
            //get the text and colors specified using the names in attrs.xml
            proteinColor = a.getInteger(R.styleable.MacroNutsPieChart_proteinColor, 0);
            fatColor     = a.getInteger(R.styleable.MacroNutsPieChart_fatColor, 0);
            carbColor    = a.getInteger(R.styleable.MacroNutsPieChart_carbColor, 0);
            inset        = a.getFloat(R.styleable.MacroNutsPieChart_inset, 0.0f);
        } finally {
            a.recycle();
        }
    }

    // ---- On Draw ----

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getMeasuredWidth();
        float h = getMeasuredHeight();

        float vInset = this.inset;
        float hInset = this.inset;

        if (w > h) {
            w = h;
            hInset += (w - h)/2.0f;
        } else {
            h = w;
            vInset += (h - w)/2.0f;
        }
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        // The rect used in drawArc
        RectF target = new RectF(0 + hInset,
                0 + vInset,
                w - hInset,
                h - vInset);

        // Compute the angles.
        float carbAngle    = this.percentCarb * 360.0f;
        float fatAngle     = this.percentFat * 360.0f;
        float proteinAngle = this.percentProtein * 360.0f;

        // Draw
        circlePaint.setColor(carbColor);
        canvas.drawArc(target,0.0f, carbAngle,true, circlePaint);
        circlePaint.setColor(fatColor);
        canvas.drawArc(target, carbAngle, fatAngle,true, circlePaint);
        circlePaint.setColor(proteinColor);
        canvas.drawArc(target, fatAngle + carbAngle, proteinAngle,true, circlePaint);
    }

    public void setValues(float percentCarb, float percentFat, float percentProtein) {
        this.percentCarb    = percentCarb;
        this.percentFat     = percentFat;
        this.percentProtein = percentProtein;

        invalidate();
        requestLayout();
    }

    // ---- Getters/Setters ----

    public int getProteinColor() {
        return proteinColor;
    }
    public int getCarbColor() {
        return carbColor;
    }
    public int getFatColor() {
        return fatColor;
    }

    public void setProteinColor(int newColor) {
        proteinColor = newColor;

        invalidate();
        requestLayout();
    }
    public void setCarbColor(int newColor) {
        carbColor = newColor;

        invalidate();
        requestLayout();
    }
    public void setFatColor(int newColor) {
        fatColor = newColor;

        invalidate();
        requestLayout();
    }

    public float getPercentFat() {
        return percentFat;
    }

    public float getPercentCarb() {
        return percentCarb;
    }

    public float getPercentProtein() {
        return percentProtein;
    }

    public void setPercentFat(float percentFat) {
        this.percentFat = Math.max(Math.min(percentFat,1.0f),0.0f);

        invalidate();
        requestLayout();
    }

    public void setPercentCarb(float percentCarb) {
        this.percentCarb = Math.max(Math.min(percentCarb,1.0f),0.0f);

        invalidate();
        requestLayout();
    }

    public void setPercentProtein(float percentProtein) {
        this.percentProtein = Math.max(Math.min(percentProtein,1.0f),0.0f);

        invalidate();
        requestLayout();
    }
    public float getInset() {
        return inset;
    }

    public void setInset(float inset) {
        this.inset = inset;

        invalidate();
        requestLayout();
    }

}
