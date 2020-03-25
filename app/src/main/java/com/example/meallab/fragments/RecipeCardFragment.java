package com.example.meallab.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.meallab.Nutrients.Nutrient;
import com.example.meallab.Nutrients.SimpleNutrientsOverviewFragment;
import com.example.meallab.R;


/**
 * A single recipe card.
 */
public class RecipeCardFragment extends Fragment {

    // the fragment initialization parameters
    private static final String ARG_PARAM1 = "name";
    private static final String ARG_PARAM2 = "cookingMins";
    private static final String ARG_PARAM3 = "servings";
    private static final String ARG_PARAM4 = "pricePerServing";
    private static final String ARG_PARAM5 = "nutrients";

    // Outlets
    private ImageView recipeImageView;
    private SimpleNutrientsOverviewFragment nutrientsOverview;
    private TextView cookingTimeTextView;
    private TextView servingsTextView;
    private TextView costTextView;

    // ----

    private String name;
    private int cookingMins;
    private int servings;
    private float pricePerServing;
    private Nutrient[] nutrients;
    Bitmap recipeImage;

    private RecipeCardFragmentListener listener;

    public RecipeCardFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new recipe card.
     *
     * @param name The title of the recipe.
     * @param cookingMins The amount of minutes too full cook the recipe.
     * @param servings The amount of servings.
     * @param pricePerServing The estimated price per serving.
     * @param nutrients The nutrientional values.
     *
     * @return A new instance of fragment RecipeCardFragment.
     */
    public static RecipeCardFragment newInstance(String name, int cookingMins, int servings, float pricePerServing, Nutrient[] nutrients) {
        RecipeCardFragment fragment = new RecipeCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,name);
        args.putInt(ARG_PARAM2,cookingMins);
        args.putInt(ARG_PARAM3,servings);
        args.putFloat(ARG_PARAM4,pricePerServing);
        args.putParcelableArray(ARG_PARAM5,nutrients);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Sets all values on the fragment and creates the view.
     *
     */
    public void setValues(String name, int cookingMins, int servings, float pricePerServing, Nutrient[] nutrients) {
        RecipeCardFragment fragment = new RecipeCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,name);
        args.putInt(ARG_PARAM2,cookingMins);
        args.putInt(ARG_PARAM3,servings);
        args.putFloat(ARG_PARAM4,pricePerServing);
        args.putParcelableArray(ARG_PARAM5,nutrients);
        fragment.setArguments(args);

        this.name = name;
        this.cookingMins = cookingMins;
        this.servings = servings;
        this.pricePerServing = pricePerServing;
        this.nutrients = nutrients;

        this.loadAllViews();
    }
    public String getName() {
        return name;
    }
    public void setRecipeImage(Bitmap image) {
        this.recipeImage = image;

        this.recipeImageView.setImageBitmap(image);
    }
    public void setListener(RecipeCardFragmentListener listener) {
        this.listener = listener;
    }

    // Loads all view objects.
    private void loadAllViews() {
        // Set the textviews.
        this.cookingTimeTextView.setText("" + this.cookingMins + " min");
        this.servingsTextView.setText("" + this.servings);
        // TODO: Find out how to localize costs
        this.costTextView.setText("" + this.pricePerServing  + "$/serving");

        // Set the nutrients.
        this.nutrientsOverview.setValues(this.nutrients);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.name            = getArguments().getString(ARG_PARAM1);
            this.cookingMins     = getArguments().getInt(ARG_PARAM2);
            this.servings        = getArguments().getInt(ARG_PARAM3);
            this.pricePerServing = getArguments().getFloat(ARG_PARAM4);
            this.nutrients       = (Nutrient[]) getArguments().getParcelableArray(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_card, container, false);
        this.servingsTextView    = v.findViewById(R.id.servingsTextView);
        this.cookingTimeTextView = v.findViewById(R.id.recipeCookingTime);
        this.costTextView        = v.findViewById(R.id.costTextView);
        this.recipeImageView     = v.findViewById(R.id.recipeImageView);

        this.nutrientsOverview = (SimpleNutrientsOverviewFragment) getChildFragmentManager().findFragmentById(R.id.nutrientsFragment);

        if (this.name != null) {
            loadAllViews();
        }

        return v;
    }

    /**
     * Interface that communicates user events with the recipe card.
     */
    public interface RecipeCardFragmentListener {

        /**
         * Called when the user selects this fragment, the implementation of this method
         * should launch the recipe.
         * @param fragment The fragment the user selected.
         */
        void launchFragment(RecipeCardFragment fragment);
    }
}
