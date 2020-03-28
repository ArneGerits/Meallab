package com.example.meallab.Nutrients;


import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.meallab.R;

/**
 * A horizontal bar chart is used to show progress data in a horizontal bar.
 */
public class HorizontalBarChart extends Fragment {

    private static final String ARG_PARAM1 = "leftText";
    private static final String ARG_PARAM2 = "rightText";
    private static final String ARG_PARAM3 = "titleText";
    private static final String ARG_PARAM4 = "progress";

    // ---- Views ----

    private BarView barView;
    private TextView progressTextView;
    private TextView leftTextView;
    private TextView rightTextView;
    private TextView titleTextView;

    // ----
    private String leftText;
    private String rightText;
    private float percentageComplete;
    private String titleText;

    // ---- Constructor ----

    public HorizontalBarChart() {
        // Required empty public constructor
    }

    // ---- Public methods ----

    public void setTitleText(String text) {
        this.titleTextView.setVisibility(View.VISIBLE);
        this.titleTextView.setText(text);
    }
    public void setLeftText(String text) {
        this.leftText = text;

        this.leftTextView.setText(text);
    }
    public void setRightText(String text) {
        this.rightText = text;

        this.rightTextView.setText(text);
    }
    public void setPercentProgress(final float percentageComplete) {
        this.percentageComplete = percentageComplete;

        this.barView.setPercentProgress(percentageComplete);
        String formattedString = String.format("%.1f", (percentageComplete * 100.0f));

        this.progressTextView.setText(formattedString + "%");

        // Position the text labels correctly with respect to the bar view.
        this.barView.post(new Runnable() {
            @Override
            public void run() {

                progressTextView.measure(0,0);
                float kMargin = 32.0f;

                RectF r = barView.getBarRect();
                ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams)progressTextView.getLayoutParams();

                if (percentageComplete < 1.0f) {
                    if (progressTextView.getMeasuredWidth() + kMargin < (barView.getRightIndicatorLocation()) - r.right) {
                        // Enough room to place it outside.
                        p.leftMargin = (int)(r.right + kMargin);
                    } else {
                        // Not enough room so place it inside.
                        p.leftMargin = (int)(r.right - progressTextView.getWidth() - kMargin);
                    }
                } else {
                    float loc = barView.getRightIndicatorLocation();
                    p.leftMargin = (int)(r.centerX() - (progressTextView.getWidth() / 2.0f));

                    // Check for overlap.
                    if (loc > p.leftMargin && loc < p.leftMargin + progressTextView.getWidth()) {
                        p.leftMargin = (int)(r.right - kMargin - progressTextView.getWidth());
                    }
                }

                progressTextView.setLayoutParams(p);

                p = (ConstraintLayout.LayoutParams)leftTextView.getLayoutParams();

                p.leftMargin = (int)(barView.getLeftIndicatorLocation() - leftTextView.getWidth() / 2.0f);
                leftTextView.setLayoutParams(p);

                p = (ConstraintLayout.LayoutParams)rightTextView.getLayoutParams();

                // Check for overlap
                float indicatorDistance = barView.getRightIndicatorLocation() - barView.getLeftIndicatorLocation();
                if (indicatorDistance < (rightTextView.getWidth() + leftTextView.getWidth()) / 2.0) {
                    p.rightMargin = (int)(barView.getWidth() - barView.getLeftIndicatorLocation()
                            - leftTextView.getWidth() / 2.0f - rightTextView.getWidth() - kMargin);
                } else {
                    p.rightMargin = (int)(barView.getWidth() - barView.getRightIndicatorLocation() - rightTextView.getWidth() / 2.0f);
                }
                rightTextView.setLayoutParams(p);
            }
        });
    }
    public static HorizontalBarChart newInstance(String leftText, String rightText, String titleText, float percentageComplete) {
        HorizontalBarChart fragment = new HorizontalBarChart();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,leftText);
        args.putString(ARG_PARAM2,rightText);
        args.putString(ARG_PARAM3,titleText);
        args.putFloat(ARG_PARAM4, percentageComplete);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            leftText = getArguments().getString(ARG_PARAM1);
            rightText = getArguments().getString(ARG_PARAM2);
            titleText = getArguments().getString(ARG_PARAM3);
            percentageComplete = getArguments().getFloat(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_horizontal_bar_chart, container, false);

        this.barView          = v.findViewById(R.id.barView);
        this.leftTextView     = v.findViewById(R.id.leftTextView);
        this.rightTextView    = v.findViewById(R.id.rightTextView);
        this.progressTextView = v.findViewById(R.id.percentageTextView);
        this.titleTextView    = v.findViewById(R.id.titleTextView);

        return v;
    }

    public BarView getBarView() {
        return this.barView;
    }
}
