package com.example.meallab.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adds the ability to scrollView that allows the addition of a 'bound' that cant be scrolled past.
 * Adds abilitiy for recyclerview to become child of scrollview.
 */
public class CustomScrollView extends NestedScrollView2 {

    /**
     * The top bound of this scrollview.
     */
    public int boundYTop;

    public boolean topBoundEnabled = false;
    private boolean isScrolling    = false;

    private int scrollTarget;

    CustomScrollViewListener listener;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
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


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        final RecyclerView rv = (RecyclerView) target;
        if ((dy < 0 && isRvScrolledToTop(rv)) || (dy > 0 && !isNsvScrolledToBottom(this))) {
            scrollBy(0, dy);
            consumed[1] = dy;
            return;
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type);
    }

    // Note that we no longer need to override onNestedPreFling() here; the
    // new-and-improved nested scrolling APIs give us the nested flinging
    // behavior we want already by default!

    private static boolean isNsvScrolledToBottom(NestedScrollView nsv) {
        return !nsv.canScrollVertically(1);
    }

    private static boolean isRvScrolledToTop(RecyclerView rv) {
        final LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
        return lm.findFirstVisibleItemPosition() == 0
                && lm.findViewByPosition(0).getTop() == 0;
    }
}
