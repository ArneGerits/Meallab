package com.example.meallab.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class EmptyRecipeCardView extends View {

    //paint for drawing custom view
    private Paint linePaint;

    private DashPathEffect dash;

    private int lineColor;

    private float lineWidth = 15.0f;

    Path line;
    // --- Constructors ----

    public EmptyRecipeCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);

        this.line = new Path();

        dash = new DashPathEffect(new float[]{30.0f, 15.0f}, 0);
    }

    public void setValues(int lineColor, float lineWidth) {
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }
    // ---- Drawing ----

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        // Draw lines around the view.
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setPathEffect(dash);

        int[] Xs = new int[]{0,w,w,0};
        int[] Ys = new int[]{0,0,h,h};

        for (int i = 0; i < Xs.length - 1; i++) {
            int x = Xs[i];
            int y = Ys[i];

            line.moveTo(x,y);
            line.lineTo(Xs[i+1],Ys[i+1]);
        }
        canvas.drawPath(line, linePaint);
    }
}
