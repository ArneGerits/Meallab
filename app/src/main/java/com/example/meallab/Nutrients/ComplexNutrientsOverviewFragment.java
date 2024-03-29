package com.example.meallab.Nutrients;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.meallab.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

// TODO: IF NO MICROS DONT SHOW BUTTON and TITLE.
/**
 * This fragment shows more extensive nutritional info.
 */
public class ComplexNutrientsOverviewFragment extends Fragment {

    private static final String ARG_PARAM1 = "progress";

    private Nutrient[] progress;

    private LinearLayout[] microLayouts;
    private final int kMicroHeight = 175;

    private boolean showingMicros = false;

    private boolean isUpdate = false;

    ArrayList<MicroNutrientFragment> microFrags = new ArrayList<>();

    // ---- Outlets ----
    HorizontalBarChart caloriesBar;
    HorizontalBarChart carbsBar;
    HorizontalBarChart fatsBar;
    HorizontalBarChart proteinsBar;
    LinearLayout microHolder;
    ImageButton showButton;

    public ComplexNutrientsOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param progress The nutritional progress.
     * @return A new instance of fragment ComplexNutrientsOverviewFragment.
     */
    public static ComplexNutrientsOverviewFragment newInstance(Nutrient[] progress) {
        ComplexNutrientsOverviewFragment fragment = new ComplexNutrientsOverviewFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PARAM1, progress);
        fragment.setArguments(args);
        return fragment;
    }

    public void loadAllViews(Nutrient[] progress) {

        // Get the macronutrients.
        Nutrient[] macros = Arrays.copyOfRange(progress,0,4);
        HorizontalBarChart[] macroBars = new HorizontalBarChart[] {this.caloriesBar,this.carbsBar,
        this.fatsBar, this.proteinsBar};

        // Colors
        int cCals     = ContextCompat.getColor(this.getContext(),R.color.calories);
        int cCarbs    = ContextCompat.getColor(this.getContext(),R.color.carb);
        int cFats     = ContextCompat.getColor(this.getContext(),R.color.fat);
        int cProteins = ContextCompat.getColor(this.getContext(),R.color.protein);

        int[] colorIDs = new int[] {cCals,cCarbs,cFats,cProteins};

        int indicator = ContextCompat.getColor(this.getContext(),R.color.woodBrown);


        // Configure the macronutrient bar views.
        for (int i = 0; i < macroBars.length; i++) {
            HorizontalBarChart bar = macroBars[i];
            Nutrient nut = macros[i];

            bar.setPercentProgress(nut.progressToday());
            bar.setLeftText("0");

            bar.setRightText(String.format("%.0f", nut.amountDailyTarget) + nut.unit);
            bar.setTitleText(nut.name + " - " + (int)nut.amount + nut.unit);
            bar.getBarView().setIndicatorColor(indicator);
            bar.getBarView().setBarColor(colorIDs[i]);
        }

        // Now micronutrients.
        Nutrient[] micros = Arrays.copyOfRange(progress,4,progress.length);

        this.microLayouts = new LinearLayout[micros.length];

        int[] cols1 = new int[] {Color.RED, Color.GREEN, Color.BLUE};
        for (int i = 0; i < micros.length; i++) {
            Nutrient micro = micros[i];

            // Create a new layout for each micronutrient.
            LinearLayout microL = newMicroLayout(micro);
            microLayouts[i] = microL;

            this.microHolder.addView(microL);
        }
    }
    private LinearLayout newMicroLayout(final Nutrient micro) {

        final MicroNutrientFragment frag = MicroNutrientFragment.newInstance(micro);
        this.microFrags.add(frag);
        LinearLayout holder = new LinearLayout(this.getContext());
        holder.setId(View.generateViewId());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, kMicroHeight);
        holder.setLayoutParams(p);

        getChildFragmentManager().beginTransaction().add(holder.getId(),frag,"t1").commit();

        return holder;
    }
    /**
     * Sets all values on the fragment and creates the view.
     *
     * @param progress The nutritional progess made, in this order:
     *                 Calories, carbs, fats, proteins, .... all micronutrients.
     * @return A new instance of fragment SimpleNutrientsOverviewFragment.
     */
    public void setValues(Nutrient[] progress) {

        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PARAM1,progress);

        this.progress = progress;

        if (this.isUpdate) {
            this.updateExistingNutrients(progress);
        } else {
            this.loadAllViews(this.progress);
        }

        this.isUpdate = true;
        this.setArguments(args);
    }
    private void updateExistingNutrients(Nutrient[] newNutrients) {

        // 1. Update the macronutrients.
        HorizontalBarChart[] macroBars = new HorizontalBarChart[] {this.caloriesBar,this.carbsBar,
                this.fatsBar, this.proteinsBar};

        for (int i = 0; i < macroBars.length; i++) {
            HorizontalBarChart bar = macroBars[i];
            Nutrient nut = newNutrients[i];
            bar.setPercentProgress(nut.progressToday());
            bar.setRightText(String.format("%.0f", nut.amountDailyTarget) + nut.unit);
            bar.setTitleText(nut.name + " - " + (int)nut.amount + nut.unit);
        }

        // Update micro nutrients.
        Nutrient[] micros = Arrays.copyOfRange(newNutrients,4,newNutrients.length);
        for (MicroNutrientFragment f : this.microFrags) {
            for (Nutrient m : micros) {
                // Update existing values.
                if (m.name.equals(f.n.name)) {
                    f.setValues(m);
                    break;
                }
            }
        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            progress = (Nutrient[]) getArguments().getParcelableArray(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_nutrients_complex, container, false);

        FragmentManager f = this.getChildFragmentManager();
        this.caloriesBar = (HorizontalBarChart) f.findFragmentById(R.id.caloriesBar);
        this.carbsBar    = (HorizontalBarChart) f.findFragmentById(R.id.carbsBar);
        this.fatsBar     = (HorizontalBarChart) f.findFragmentById(R.id.fatsBar);
        this.proteinsBar = (HorizontalBarChart) f.findFragmentById(R.id.proteinsBar);

        this.showButton = v.findViewById(R.id.showButton);

        this.microHolder = v.findViewById(R.id.microHolder);


        this.showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showingMicros = !showingMicros;

                int toValue = 0;
                if (microLayouts != null) {
                    toValue   = kMicroHeight * microLayouts.length;
                }
                int fromValue = 0;

                if (!showingMicros) {
                    fromValue = toValue;
                    toValue   = 0;
                }

                microHolder.setVisibility(View.VISIBLE);

                // Animate the height of the linear layout.
                ValueAnimator va = ValueAnimator.ofInt(fromValue, toValue);
                va.setDuration(400);
                va.setInterpolator(new AccelerateDecelerateInterpolator());
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        ViewGroup.LayoutParams p = microHolder.getLayoutParams();
                        p.height = value;
                        microHolder.setLayoutParams(p);
                        microHolder.requestLayout();
                    }

                });

                final int toVal = toValue;
                va.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (showingMicros) {
                            microHolder.setVisibility(View.VISIBLE);
                            showButton.setImageResource(R.drawable.expand_up);
                        } else {
                            microHolder.setVisibility(View.GONE);
                            showButton.setImageResource(R.drawable.expand_down);
                        }
                    }
                });
                va.start();
            }

        });

        if (this.progress != null) {
            this.loadAllViews(this.progress);
        }

        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                for (MicroNutrientFragment frag : microFrags) {
                    LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) frag.getView().getLayoutParams();
                    p.weight = 1;
                    p.height = LinearLayout.LayoutParams.MATCH_PARENT;
                    p.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    frag.getView().setLayoutParams(p);
                }
            }

        });

        return v;
    }

}
