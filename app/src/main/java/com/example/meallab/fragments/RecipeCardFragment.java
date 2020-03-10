package com.example.meallab.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.meallab.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeCardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "cookingMins";
    private static final String ARG_PARAM2 = "servings";
    private static final String ARG_PARAM3 = "pricePerServing";


    // TODO: Rename and change types of parameters
    private int cookingMins;
    private int servings;
    private float pricePerServing;

    public RecipeCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cookingMins The amount of minutes too full cook the recipe.
     * @param servings The amount of servings.
     * @param pricePerServing The estimated price per serving.
     *
     * @return A new instance of fragment RecipeCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeCardFragment newInstance(int cookingMins, int servings, float pricePerServing) {
        RecipeCardFragment fragment = new RecipeCardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1,cookingMins);
        args.putInt(ARG_PARAM2,servings);
        args.putFloat(ARG_PARAM3,pricePerServing);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.cookingMins     = getArguments().getInt(ARG_PARAM1);
            this.servings        = getArguments().getInt(ARG_PARAM2);
            this.pricePerServing = getArguments().getFloat(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_card, container, false);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface recipeCardFragmentListener {

        /**
         * Called when the user selects this fragment.
         * @param fragment The fragment the user selected.
         * @param selected True if the user selected this fragment, false if it is deselected.
         */
        void selectedFragment(RecipeSelectionRow fragment, boolean selected);

        /**
         * Called when the user wants more info for this fragment.
         * @param fragment The fragment the user wants more info of.
         */
        void moreInfoFragment(RecipeSelectionRow fragment);
    }
}
