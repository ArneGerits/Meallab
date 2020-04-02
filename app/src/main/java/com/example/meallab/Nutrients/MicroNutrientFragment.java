package com.example.meallab.Nutrients;


import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import com.example.meallab.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MicroNutrientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MicroNutrientFragment extends Fragment {

    private static final String ARG_PARAM1 = "nutrient";

    Nutrient n;

    // ---- Views ----
    HorizontalBarChart chart;
    TextView title;
    TextView amount;

    public MicroNutrientFragment() {
        // Required empty public constructor
    }

    private void loadAllViews(Nutrient n) {

        this.title.setText(n.name);

        this.amount.setText(String.format("%.2f", n.amount) + n.unit);
        this.chart.setLeftText("0");
        this.chart.setRightText(String.format("%.2f", n.amountDailyTarget)+ n.unit);

        this.chart.setPercentProgress(n.progressToday());

        final int cColor = ContextCompat.getColor(this.getContext(),R.color.calories);
        final int indicator = ContextCompat.getColor(this.getContext(),R.color.woodBrown);

        this.chart.getBarView().setIndicatorColor(indicator);
        this.chart.getBarView().setBarColor(cColor);
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MicroNutrientFragment.
     */
    public static MicroNutrientFragment newInstance(Nutrient nutrient) {
        MicroNutrientFragment fragment = new MicroNutrientFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, nutrient);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Sets all values on the fragment and creates the view.
     *
     */
    public void setValues(Nutrient nut) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1,nut);

        this.n = nut;
        this.loadAllViews(nut);

        this.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            n = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_micro_nutrient, container, false);

        this.chart = (HorizontalBarChart) getChildFragmentManager().findFragmentById(R.id.barChart);
        this.title = v.findViewById(R.id.nameTextView);
        this.amount = v.findViewById(R.id.amountTextView);

        // Make sure the text size is scale to fit
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this.amount,1,18,1, TypedValue.COMPLEX_UNIT_SP);

        //this.amount.
        if (this.n != null) {
            loadAllViews(this.n);
        }
        return v;
    }

}
