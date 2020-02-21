package com.example.meallab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meallab.Spoonacular.RecipeCost;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeInfoDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecipeInfoDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeInfoDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeInfoDetailFragment newInstance(String param1, String param2) {
        RecipeInfoDetailFragment fragment = new RecipeInfoDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        kcalText.setText("" + (int)kcal);

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
