package com.example.meallab.fragments;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;
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
    private static final String ARG_PARAM6 = "isEmpty";
    private static final String ARG_PARAM7 = "index";

    // ----- Outlets -----
    private ImageView recipeImageView;
    private SimpleNutrientsOverviewFragment nutrientsOverview;
    private TextView cookingTimeTextView;
    private TextView servingsTextView;
    private TextView costTextView;
    public TextView infoTextView;

    private LinearLayout topLayout;
    private ConstraintLayout addLayout;
    private LinearLayout infoLayout;
    private ImageButton editButton;

    private View divider;

    // ----

    private String name;
    private int cookingMins;
    private int servings;
    private float pricePerServing;
    private Nutrient[] nutrients;
    Bitmap recipeImage;
    private boolean isEmpty;

    private int index;

    private RecipeCardFragmentListener listener;
    private RecipeCardFragmentLayoutListener layoutListener;

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
    public static RecipeCardFragment newInstance(String name, int cookingMins, int servings, float pricePerServing, Nutrient[] nutrients, int index) {
        RecipeCardFragment fragment = new RecipeCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,name);
        args.putInt(ARG_PARAM2,cookingMins);
        args.putInt(ARG_PARAM3,servings);
        args.putFloat(ARG_PARAM4,pricePerServing);
        args.putParcelableArray(ARG_PARAM5,nutrients);
        args.putBoolean(ARG_PARAM6,false);
        args.putInt(ARG_PARAM7,index);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new empty recipe card.
     *
     * @return A new instance of fragment RecipeCardFragment.
     */
    public static RecipeCardFragment newEmptyInstance(int index) {
        RecipeCardFragment fragment = new RecipeCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,"");
        args.putInt(ARG_PARAM2,0);
        args.putInt(ARG_PARAM3,0);
        args.putFloat(ARG_PARAM4,0.0f);
        args.putParcelableArray(ARG_PARAM5,new Nutrient[0]);
        args.putBoolean(ARG_PARAM6,true);
        args.putInt(ARG_PARAM7, index);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Sets all values on the fragment and creates the view.
     *
     */
    public void setValues(String name, int cookingMins, int servings, float pricePerServing, Nutrient[] nutrients, int index) {
        RecipeCardFragment fragment = new RecipeCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,name);
        args.putInt(ARG_PARAM2,cookingMins);
        args.putInt(ARG_PARAM3,servings);
        args.putFloat(ARG_PARAM4,pricePerServing);
        args.putParcelableArray(ARG_PARAM5,nutrients);
        args.putBoolean(ARG_PARAM6,false);
        args.putInt(ARG_PARAM7, index);
        fragment.setArguments(args);

        this.name = name;
        this.cookingMins = cookingMins;
        this.servings = servings;
        this.pricePerServing = pricePerServing;
        this.nutrients = nutrients;
    }
    public String getName() {
        return name;
    }

    public void setRecipeImage(Bitmap image) {
        this.recipeImage = image;

        if (this.recipeImageView != null) {
            this.recipeImageView.setImageBitmap(image);
        }
    }
    public void setListener(RecipeCardFragmentListener listener) {
        this.listener = listener;
    }

    public void setLayoutListener(RecipeCardFragmentLayoutListener listener) {
        this.layoutListener = listener;
    }
    // Loads all view objects.
    private void loadAllViews(View v) {
        if (!isEmpty) {

            addLayout.setVisibility(View.GONE);
            this.infoLayout.setVisibility(View.VISIBLE);
            this.recipeImageView.setVisibility(View.VISIBLE);
            this.nutrientsOverview.getView().setVisibility(View.VISIBLE);
            this.divider.setVisibility(View.VISIBLE);

            v.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
            // Set the text views.
            this.cookingTimeTextView.setText("" + this.cookingMins + " min");
            this.servingsTextView.setText("" + this.servings);
            // TODO: Find out how to localize costs
            // Price per servings is given in cents.
            String price = String.format("%.2f$/sv",(this.pricePerServing / 100));
            this.costTextView.setText(price);

            // Set the nutrients.
            this.nutrientsOverview.setValues(this.nutrients);

            if (this.recipeImage != null) {
                this.recipeImageView.setImageBitmap(this.recipeImage);
            }
        } else {
            v.setBackgroundColor(Color.TRANSPARENT);
            // Set all to GONE
            this.infoLayout.setVisibility(View.GONE);
            this.recipeImageView.setVisibility(View.GONE);
            this.nutrientsOverview.getView().setVisibility(View.GONE);
            this.divider.setVisibility(View.GONE);
            addLayout.setVisibility(View.VISIBLE);
        }
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
            this.isEmpty         = getArguments().getBoolean(ARG_PARAM6);
            this.index           = getArguments().getInt(ARG_PARAM7);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_card, container, false);
        this.servingsTextView    = v.findViewById(R.id.servingsTextView);
        this.cookingTimeTextView = v.findViewById(R.id.caloriesSelected);
        this.costTextView        = v.findViewById(R.id.costTextView);
        this.recipeImageView     = v.findViewById(R.id.recipeImageView);
        this.infoLayout          = v.findViewById(R.id.infoContainer);
        this.nutrientsOverview   = (SimpleNutrientsOverviewFragment) getChildFragmentManager().findFragmentById(R.id.nutrientsFragment);
        this.addLayout           = v.findViewById(R.id.addLayout);
        this.topLayout           = v.findViewById(R.id.topLayout);
        this.editButton          = v.findViewById(R.id.editButton);
        this.infoTextView        = v.findViewById(R.id.infoTextView);
        this.divider             = v.findViewById(R.id.dividerBottom);
        this.topLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.clickedOnFragment(RecipeCardFragment.this);
                }
            }
        });
        ImageButton b = v.findViewById(R.id.plusButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.editFragment(RecipeCardFragment.this);
                }
            }
        });
        this.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.editFragment(RecipeCardFragment.this);
                }
            }
        });

        if (this.name != null) {
            loadAllViews(v);
        }
        if (this.layoutListener != null) {
            this.layoutListener.loadedView(v);
        }

        // Make sure the text size is scale to fit
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this.costTextView,1,17,1, TypedValue.COMPLEX_UNIT_SP);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this.cookingTimeTextView,1,17,1, TypedValue.COMPLEX_UNIT_SP);

        return v;
    }

    public int getIndex() {
        return this.index;
    }
    public boolean getIsEmpty() {
        return this.isEmpty;
    }
    /**
     * Interface that communicates user events with the recipe card.
     */
    public interface RecipeCardFragmentListener {

        /**
         * Called when the user selects this fragment.
         * @param fragment The fragment the user selected.
         */
        void clickedOnFragment(RecipeCardFragment fragment);

        /**
         * Called when the user wants to edit this fragment
         * @param fragment The fragment the user wants to edit.
         */
        void editFragment(RecipeCardFragment fragment);
    }
    public interface RecipeCardFragmentLayoutListener {

        /**
         * Called when the given view is loaded.
         * @param v The view that is loaded.
         */
        void loadedView(View v);
    }
}
