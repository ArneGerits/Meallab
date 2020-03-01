package com.example.meallab;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Adds the ability to scrollView that allows the addition of a 'bound' that cant be scrolled past.
 */
public class CustomScrollView extends ScrollView {

    /**
     * The top bound of this scrollview.
     */
    public int boundYTop;

    public boolean topBoundEnabled = false;
    private boolean isScrolling    = false;

    private int scrollTarget;

    CustomScrollViewListener listener;

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (this.isScrolling && this.scrollTarget == this.getScrollY()) {
            this.isScrolling = false;
        }

        if (!this.isScrolling) {
            if (this.topBoundEnabled && this.getScrollY() < boundYTop) {
                this.setScrollY(boundYTop);
            }
        }
        if (this.listener != null) {
            this.listener.onScrollChanged();
        }
    }

    public void setCustomListener(CustomScrollViewListener listener) {
        this.listener = listener;
    }
    /**
     * The custom smooth scroll to ignores set bounds.
     * @param x
     * @param y
     */
    public void customSmoothScrollTo(int x, int y) {

        this.scrollTarget = y;
        this.isScrolling = true;
        super.smoothScrollTo(x,y);
    }
    public boolean getIsScrolling() {
        return this.isScrolling;
    }

    // -------- INTERFACES --------

    public interface CustomScrollViewListener {
        public void onScrollChanged();
    }
}
