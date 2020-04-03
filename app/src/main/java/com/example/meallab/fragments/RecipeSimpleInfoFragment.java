package com.example.meallab.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meallab.R;
import com.example.meallab.Spoonacular.RecipeCost;

/**
 * Small card that shows the following info about a recipe:
 * 1. Amount of calories
 * 2. Prep time
 * 3. How many servings
 * 4. Recipe cost
 */
public class RecipeSimpleInfoFragment extends Fragment {

    // the fragment initialization parameters
    private static final String ARG_PARAM1 = "kcals";
    private static final String ARG_PARAM2 = "servings";
    private static final String ARG_PARAM3 = "readyTime";
    private static final String ARG_PARAM4 = "cost";

    // ----- Outlets -----

    ImageView costImageView;
    TextView caloriesTextView;
    TextView prepTimeTextView;
    TextView servingsTextView;

    // ----- Variables -----
    // Amount of calories in the recipe
    float amountOfCalories;
    // Amount of servings.
    int servings;
    // Ready time in minutes
    int readyTime;
    // The cost of the recipe.
    RecipeCost cost;

    public RecipeSimpleInfoFragment() {
        // Required empty public constructor
    }

    // ----- Public Methods -----

    /**
     * Puts the passed values into the view.
     * @param kcal The amount of kcal in this recipe.
     * @param servings The amount of people this recipe serves.
     * @param readyTime The time it takes to make recipe in minutes.
     * @param cost The cost of the recipe.
     */
    public void setValues(float kcal, int servings, int readyTime, RecipeCost cost) {
        this.amountOfCalories = kcal;
        this.servings         = servings;
        this.readyTime        = readyTime;
        this.cost             = cost;

        RecipeSimpleInfoFragment fragment = new RecipeSimpleInfoFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_PARAM1,this.amountOfCalories);
        args.putInt(ARG_PARAM2,this.servings);
        args.putInt(ARG_PARAM3,this.readyTime);
        args.putString(ARG_PARAM4,this.cost.name());
        fragment.setArguments(args);

        this.loadAllViews();
    }

    // Loads every view.
    @SuppressLint("DefaultLocale")
    private void loadAllViews() {

        // TODO: Localize
        this.caloriesTextView.setText(String.format("%d", (int)this.amountOfCalories) + " cals");
        this.servingsTextView.setText(String.format("%d", this.servings));
        this.prepTimeTextView.setText(String.format("%d", this.readyTime) + " mins");

        // TODO: HANDLE THE IMAGE FOR THE COST.
    }
    // ----- Overrides -----

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.amountOfCalories  = getArguments().getFloat(ARG_PARAM1);
            this.servings          = getArguments().getInt(ARG_PARAM2);
            this.readyTime         = getArguments().getInt(ARG_PARAM3);
            this.cost              = RecipeCost.valueOf(getArguments().getString(ARG_PARAM4));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_recipe_simple_info, container, false);

        this.caloriesTextView = root.findViewById(R.id.caloriesTextView);
        this.prepTimeTextView = root.findViewById(R.id.caloriesSelected);
        this.servingsTextView = root.findViewById(R.id.servingsTextView);

        this.costImageView    = root.findViewById(R.id.costImageView);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
