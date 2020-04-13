package com.example.meallab.customViews;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.meallab.fragments.RecipeCardFragment;

/**
 * Horizontal scrollView subclass that allows the user to scroll between their selected recipes.
 * Recipes are presented as cards using RecipeCardFragments
 */
public class RecipeCardScrollView extends HorizontalScrollView implements RecipeCardFragment.RecipeCardFragmentLayoutListener {

    // ---- Constants ----

    private static final int SWIPE_MIN_DISTANCE = 5;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    // ----

    private RecipeCardScrollViewListener listener;

    // Used for snap-effect
    private GestureDetector mGestureDetector;

    // The recipe cards this scrollview controls
    private RecipeCardFragment[] fragments;

    // The index currently in the center of the screen.
    private int focusIndex = 0;

    // Holds the recipe carrds.
    private LinearLayout holder;

    // Margin between views, default: 20
    private int margin = 20;
    // Width of cards, default: 600
    private int cardWidth = 600;

    // Margin between a the first view and edge.
    private int leftEdgeMargin = 0;
    // Margin between a the last view and edge.
    private int rightEdgeMargin = 0;

    int cardsLayedOut = 0;

    // Smooth scroll to destination.
    int scrollTo;
    // True if currenlty scrolling.
    boolean isScrolling = false;

    RecipeCardFragment fragmentToScrollTo;

    // ---- Constructors ----

    public RecipeCardScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setup();
    }

    public RecipeCardScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setup();
    }

    public RecipeCardScrollView(Context context) {
        super(context);
        this.setup();
    }

    private void setup() {
        setHorizontalScrollBarEnabled(false);

        // Create the horizontal view holder.
        this.holder = new LinearLayout(this.getContext());
        this.holder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        this.holder.setOrientation(LinearLayout.HORIZONTAL);
        this.holder.setGravity(Gravity.CENTER);
        this.holder.setId(View.generateViewId());
        this.addView(this.holder);
    }

    // ---- Public Setup Methods ----

    /**
     * Sets custom layout.
     * @param margin The margin between the cards
     * @param cardWidth The width of the cards.
     */
    public void setLayout(int margin, int cardWidth ) {
        this.margin    = margin;
        this.cardWidth = cardWidth;
    }

    /**
     * Sets the card fragments, loads the view.
     * @param fragments The card fragments to present.
     */
    public void setFragments(final RecipeCardFragment[] fragments) {

        this.fragments = fragments;
        this.cardsLayedOut = 0;
        for (RecipeCardFragment f : fragments) {
            f.setLayoutListener(this);
        }
        this.addFragmentsToHolder(fragments);

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //If the user swipes
                if (mGestureDetector.onTouchEvent(event) || event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                    // Get the view that is closest the center.
                    RecipeCardFragment closest = getClosest();

                    // Center that view.
                    focusFragmentPrivate(closest, true);

                    return true;
                } else {
                    return false;
                }
            }
        });
        class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    // A swipe focuses the next view, swipe right to left.
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                        return true;

                    }
                    // A swipe focuses the next view, swipe left to right.
                    else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                        return true;
                    }
                } catch (Exception e) {

                }
                return false;
            }
        }
        mGestureDetector = new GestureDetector(this.getContext(), new MyGestureDetector());

        setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                adjustFragmentHeights();
            }
        });
    }

    public void setListener(RecipeCardScrollViewListener listener) {
        this.listener = listener;
    }
    // ---- Public Methods ----

    /**
     * Smooth scrolls to put the fragment on screen.
     * @param f The fragment to focus on
     */
    // Center given view using smooth scroll.
    public void focusFragment(RecipeCardFragment f, boolean animated) {

        if (this.isScrolling) {
            return;
        }

        // Scroll if the root view is loaded already, else rember the fragment.
        if (f.getView() != null) {
            this.focusFragmentPrivate(f, animated);
        } else {
            this.fragmentToScrollTo = f;
        }
    }

    private void focusFragmentPrivate(RecipeCardFragment f, boolean animated) {
        View v = f.getView();
        // Get the middle of this scrollview.
        float sv = this.getWidth();
        float middle = (sv / 2.0f);

        // Calculate the distance to scroll.
        float scrollX = (v.getX() + v.getWidth() / 2.0f) - middle;

        // Keep track of where to scroll to.
        this.scrollTo = (int)scrollX;
        this.isScrolling = true;

        if (animated) {
            // Position the view in the center.
            smoothScrollTo((int)scrollX,0);
        } else {
            scrollTo((int)scrollX,0);
        }
        adjustFragmentHeights();
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        this.isScrolling = (this.getScrollX() != this.scrollTo);
    }

    // ---- Private Methods ----

    // Returns the view that is closest to the center.
    private RecipeCardFragment getClosest() {
        float sv = this.getWidth();
        float middle = (sv / 2.0f) + getScrollX();

        float closest    = Float.MAX_VALUE;
        RecipeCardFragment closestFragment = this.fragments[0];

        for (RecipeCardFragment f : this.fragments) {
            View v = f.getView();

            // The x position of a view in the scrollview.
            float viewPosX = v.getX() + (v.getWidth() / 2.0f);

            float distance = Math.abs(viewPosX - middle);
            if (distance < closest) {
                closest = distance;
                closestFragment = f;
            }
        }

        return closestFragment;
    }

    // Adds views and dividers to the holder view.
    private void addFragmentsToHolder(RecipeCardFragment[] fragments) {

        this.holder.removeAllViews();
        // Add the fragments.
        for (int i = 0; i < fragments.length; i++) {
            RecipeCardFragment f = fragments[i];

            FragmentManager fragMan = ((AppCompatActivity) this.getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = fragMan.beginTransaction();

            transaction.add(this.holder.getId(), f , "fragment" + i);
            transaction.commit();
        }
    }

    // region Dividers

    // Creates a new divider for between 2 views.
    private View newDivider() {
        View div = new View(this.getContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(this.margin, LinearLayout.LayoutParams.MATCH_PARENT);
        div.setLayoutParams(p);
        div.setBackgroundColor(Color.TRANSPARENT);
        return div;
    }
    // Creates a new divider that should be on the left.
    private View newLeftDivider() {
        View div = new View(this.getContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(this.leftEdgeMargin, LinearLayout.LayoutParams.MATCH_PARENT);
        div.setLayoutParams(p);
        div.setBackgroundColor(Color.TRANSPARENT);
        return div;
    }
    // Creates a new divider that should be on the right.
    private View newRightDivider() {
        View div = new View(this.getContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(this.rightEdgeMargin, LinearLayout.LayoutParams.MATCH_PARENT);
        div.setLayoutParams(p);
        div.setBackgroundColor(Color.TRANSPARENT);
        return div;
    }

    // endregion

    // This method adds a view tree observer recursively to make sure the layout is done correctly.
    private void finalizeLayout() {

        // Add tree listener to compute edge margins.
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RecipeCardScrollView inner = RecipeCardScrollView.this;

                inner.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // If there are more than 2 cards we need to add spacing in between.
                if (fragments.length >= 2) {
                    for (int i = 0; i < fragments.length; i++) {
                        holder.addView(newDivider(),(i*2) + 1);
                    }
                }

                // Calculate the edge margin.
                int wFirst = inner.fragments[0].getView().getLayoutParams().width;
                int wLast  = inner.fragments[inner.fragments.length - 1].getView().getLayoutParams().width;

                inner.leftEdgeMargin  = (int)((inner.getWidth() - wFirst) / 2.0f);
                inner.rightEdgeMargin = (int)((inner.getWidth() - wLast) / 2.0f);

                // Add the left divider.
                View d1 = newLeftDivider();
                inner.holder.addView(d1,0);

                // Add the right divider.
                View d2 = newRightDivider();
                inner.holder.addView(d2);

                // Inside view tree observer to adjust heights.
                inner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        RecipeCardScrollView inner = RecipeCardScrollView.this;

                        inner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        // TODO: THIS IS OFF?
                        focusFragmentPrivate(fragments[focusIndex], false);
                        if (listener != null) {
                            listener.scrolledToCard(fragments[focusIndex]);
                        }
                    }
                });
            }
        });
    }
    // Adjust the height of all fragments, according to scroll position.
    private void adjustFragmentHeights() {
        float sv = RecipeCardScrollView.this.getWidth();
        float middle = (sv / 2.0f) + getScrollX();

        // Keep track of the card view in focus.
        float largest = 0.0f;
        int focusIndex = 0;
        for (int i = 0; i < fragments.length; i++) {

            RecipeCardFragment f = fragments[i];
            // Adjust the height
            float newH = adjustHeight(f,middle);
            if (newH > largest) {
                largest = newH;
                focusIndex = i;
            }

        }

        // Update the card in focus.
        if (focusIndex != this.focusIndex) {
            this.focusIndex = focusIndex;

            // Inform the listener
            if (this.listener != null) {
                this.listener.scrolledToCard(this.fragments[this.focusIndex]);
            }
        }

    }
    // Adjusts the height of every view based on distance from the middle.
    // @Return the height.
    private float adjustHeight(Fragment f, float middle) {

        View v = f.getView();
        // If the views are this distance they are scaled to 80% height.
        float maxDistance = this.getWidth() / 2.0f;
        // The minimum height of a view.
        float scaleMin    = 0.8f;

        // The x position of a view in the scrollview.
        float viewPosX = v.getX() + (v.getWidth() / 2.0f);
        // Distance from the middle.
        float distance = Math.abs(viewPosX - middle);

        float multiplier = 1.0f - Math.min(distance/maxDistance,1.0f) * (1.0f - scaleMin);
        float newHeight = this.getHeight() * multiplier;

        // Set the new height only if it is different.
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) v.getLayoutParams();
        if (newHeight != p.height) {
            p.height = (int)newHeight;
            v.setLayoutParams(p);
        }
        return newHeight;
    }

    @Override
    public void loadedView(View v) {

        ViewGroup.LayoutParams p = v.getLayoutParams();
        p.width = cardWidth;
        v.setLayoutParams(p);

        this.cardsLayedOut++;
        if (cardsLayedOut == this.fragments.length) {
            finalizeLayout();
        }
    }

    public interface RecipeCardScrollViewListener {
        /**
         * Gets called when the API has retrieved additional information for the recipe.
         * @param card The card the user scrolled to.
         */
        void scrolledToCard(RecipeCardFragment card);
    }
}
