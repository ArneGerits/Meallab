package com.example.meallab.Nutrients;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.meallab.R;


/**
 * This fragment shows nutritional info.
 */
public class SimpleNutrientsOverviewFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "progress";

    private Nutrient[] progress;

    // ---- Views/Fragments ----

    MacroNutsPieChart pieChart;
    HorizontalBarChart barChart;

    TextView carbsTextView;
    TextView fatsTextView;
    TextView proteinsTextView;

    // ---- Constructors ----

    public SimpleNutrientsOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param progress The nutritional progress made.
     * @return A new instance of fragment SimpleNutrientsOverviewFragment.
     */
    public static SimpleNutrientsOverviewFragment newInstance(Nutrient[] progress) {
        SimpleNutrientsOverviewFragment fragment = new SimpleNutrientsOverviewFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PARAM1,progress);

        fragment.setArguments(args);
        return fragment;
    }

    // Sets all relevant values on every view.
    @SuppressLint("DefaultLocale")
    private void loadAllViews(Nutrient[] progress) {

        Nutrient calories = progress[0];
        Nutrient fats     = progress[1];
        Nutrient carbs    = progress[2];
        Nutrient proteins = progress[3];

        float total = fats.amount + carbs.amount + proteins.amount;

        // Setting the right values.
        this.pieChart.setPercentCarb(carbs.amount/total);
        this.pieChart.setPercentFat(fats.amount/total);
        this.pieChart.setPercentProtein(proteins.amount/total);

        this.barChart.setPercentProgress(calories.progressToday());
        this.barChart.setLeftText("0");
        this.barChart.setRightText("" + (int)calories.amountDailyTarget);

        this.carbsTextView.setText((int)carbs.amount + "g");
        this.fatsTextView.setText((int)fats.amount + "g");
        this.proteinsTextView.setText((int)proteins.amount + "g");

        // Setting the right colors.
        int cColor = ContextCompat.getColor(this.getContext(),R.color.calories);
        int indicator = ContextCompat.getColor(this.getContext(),R.color.woodBrown);
        this.barChart.getBarView().setBarColor(cColor);
        this.barChart.getBarView().setIndicatorColor(indicator);
        this.barChart.setTitleText((int)calories.amount + " " + calories.unit);

        this.barChart.setLeftText("0");
        this.barChart.setRightText(String.format("%d", (int)calories.amountDailyTarget) + calories.unit);

        this.barChart.setPercentProgress(calories.progressToday());
    }
    /**
     * Sets all values on the fragment and creates the view.
     *
     * @param progress An array of nutriotional progress made,
     *                 In order: calories, carbs, proteins, fats.
     * @return A new instance of fragment SimpleNutrientsOverviewFragment.
     */
    public void setValues(Nutrient[] progress) {

        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PARAM1,progress);

        this.progress = progress;
        this.loadAllViews(this.progress);

        this.setArguments(args);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.progress   = (Nutrient[]) getArguments().getParcelableArray(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_nutrients_simple, container, false);

        this.pieChart = v.findViewById(R.id.pieChart);

        this.proteinsTextView = v.findViewById(R.id.proteinsTextView);
        this.fatsTextView = v.findViewById(R.id.fatsTextView);
        this.carbsTextView = v.findViewById(R.id.carbsTextView);

        FragmentManager f = this.getChildFragmentManager();
        this.barChart = (HorizontalBarChart) f.findFragmentById(R.id.barChart);

        if (this.progress != null) {
            this.loadAllViews(this.progress);
        }
        return v;
    }

}
