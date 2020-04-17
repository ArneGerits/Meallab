package com.example.meallab.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.example.meallab.R;

import org.w3c.dom.Text;

public class Divider extends LinearLayout {

    TextView title;
    View leftBar;
    View rightBar;

    int colorPrimary;
    int colorPrimaryDark;

    public Divider(Context context) {
        super(context);
        setup(context);
    }

    public Divider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Divider, 0, 0);

        // Read the attribute values.
        try {
            //get the text and colors specified using the names in attrs.xml

            String t = a.getString(R.styleable.Divider_text);
            boolean h = a.getBoolean(R.styleable.Divider_highlighted,false);

            this.title.setText(t);
            this.setHighlighted(h);
        } finally {
            a.recycle();
        }
    }

    public Divider(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Divider, 0, 0);

        // Read the attribute values.
        try {
            //get the text and colors specified using the names in attrs.xml
            String t = a.getString(R.styleable.Divider_text);
            boolean h = a.getBoolean(R.styleable.Divider_highlighted,false );

            this.title.setText(t);
            this.setHighlighted(h);
        } finally {
            a.recycle();
        }
    }

    private void setup(Context c) {
        View.inflate(c, R.layout.layout_divider, this);

        this.title = findViewById(R.id.titleTextView);

        this.leftBar = findViewById(R.id.leftBar);
        this.rightBar = findViewById(R.id.rightBar);

        this.colorPrimary = ContextCompat.getColor(this.getContext(),R.color.colorPrimary);
        this.colorPrimaryDark = ContextCompat.getColor(this.getContext(),R.color.colorPrimaryDark);
    }

    public void setHighlighted(boolean highlighted) {
        if (highlighted) {
            this.setBackgroundColor(this.colorPrimary);
            this.title.setTextColor(Color.WHITE);

            this.leftBar.setBackgroundColor(Color.WHITE);
            this.rightBar.setBackgroundColor(Color.WHITE);
        } else {
            this.setBackgroundColor(Color.TRANSPARENT);
            this.title.setTextColor(this.colorPrimaryDark);

            this.leftBar.setBackgroundColor(this.colorPrimaryDark);
            this.rightBar.setBackgroundColor(this.colorPrimaryDark);
        }
    }
}
