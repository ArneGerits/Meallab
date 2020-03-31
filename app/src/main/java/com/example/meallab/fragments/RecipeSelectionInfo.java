package com.example.meallab.fragments;

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
 * A simple {@link Fragment} subclass.
 */
public class RecipeSelectionInfo extends Fragment {

    public RecipeSelectionInfo() {
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
    public void setValues(float kcal, int servings, int readyTime ,RecipeCost cost) {
        TextView kcalText = getView().findViewById(R.id.kcalTextView);
        kcalText.setText("" + (int)Math.round(kcal));

        TextView servText = getView().findViewById(R.id.servingsTextView);
        servText.setText("" + servings);

        TextView timeText = getView().findViewById(R.id.timeTextView);
        timeText.setText("" + readyTime + " mins"); // ReadyTime is always in minutes.

        String imageName = "";

        // todo: image names!
        switch (cost) {

            case LOW:
                imageName = "";
                break;
            case MEDIUM:
                imageName = "";
                break;
            case HIGH:
                imageName = "";
                break;
            case UNKNOWN:
                imageName = "";
                break;
        }
        // todo: load image into the imageView
        ImageView priceImageView = getView().findViewById(R.id.priceImageView);
    }

    // ----- Overrides -----

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_info_detail, container, false);
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
