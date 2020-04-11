package com.example.meallab.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.meallab.R;
import com.example.meallab.Spoonacular.Recipe;
import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.example.meallab.customViews.RecipeCardScrollView;
import com.example.meallab.storing_data.StoredRecipe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Fragment that allows the user to scroll their recipe cards.
 */
public class CardScrollerFragment extends Fragment implements RecipeCardScrollView.RecipeCardScrollViewListener,
        RecipeCardFragment.RecipeCardFragmentListener {

    // Outlets
    private RecipeCardScrollView scrollView;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private TextView titleTextView;
    private TextView typeTextView;

    // ----

    private static String ARG_PARAM1 = "recipes";
    private static String ARG_PARAM2 = "indices";

    // The recipes shown by this scroller.
    private StoredRecipe[] recipes;
    private boolean[] empties;

    // The card fragment shown by this scroller.
    private RecipeCardFragment[] fragments;

    private CardScrollerFragmentListener listener;

    // Maps recipe ids to values.
    private HashMap<Integer, RecipeCardFragment> recipeIDtoFragment = new HashMap<>();
    // Maps recipe ids to images.
    private HashMap<Integer, Bitmap> recipeIDtoImage = new HashMap<>();

    private int currentIndex = 0;

    public CardScrollerFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new card scroller.
     *
     * @param recipes The recipes
     * @param emptyIndices Indices where an empty slot should be.
     * @return A new instance of fragment CardScrollerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardScrollerFragment newInstance(StoredRecipe[] recipes, boolean[] emptyIndices) {
        CardScrollerFragment fragment = new CardScrollerFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PARAM1,recipes);
        args.putBooleanArray(ARG_PARAM2, emptyIndices);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Sets all values on the fragment and creates the view.
     * @param recipes The recipes to create cards for.
     * @param structure The structure of the cards, specify true, if a card should be empty, false otherwise.
     */
    public void setValues(StoredRecipe[] recipes, boolean[] structure) {

        CardScrollerFragment fragment = new CardScrollerFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PARAM1,recipes);
        args.putBooleanArray(ARG_PARAM2, structure);
        fragment.setArguments(args);

        this.recipes = recipes;
        this.empties = structure;
        loadAllViews();
    }
    // Loads all views
    // @pre recipes must be initialized.
    private void loadAllViews() {

        this.fragments = new RecipeCardFragment[this.empties.length];

        // Used to get the stored recipes.
        Iterator<StoredRecipe> it = Arrays.asList(this.recipes).iterator();

        // Cycle all recipes and create cards, then add the card to the scrollview.
        for (int i = 0; i < this.empties.length; i++) {

            RecipeCardFragment f;

            // Create a new card.
            if (this.empties[i]) {
                f = RecipeCardFragment.newEmptyInstance(i);
                f.setListener(this);
            } else {
                StoredRecipe r = it.next();
                f = RecipeCardFragment.newInstance(r.name,r.cookingMins,
                        r.numberOfServings,r.pricePerServing, r.getMacroNutrients(), i);
                f.setListener(this);
                this.recipeIDtoFragment.put(r.recipeID, f);
                // Get the image.
                Bitmap image = this.recipeIDtoImage.get(r.recipeID);
                if (image != null) {
                    f.setRecipeImage(image);
                }
            }

            // Add it to the fragments.
            this.fragments[i] = f;
        }
        // Add the cards to the scrollview.
        this.scrollView.setFragments(this.fragments);
       // this.scrollView.focusFragment(this.fragments[this.currentIndex],false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.recipes = (StoredRecipe[]) getArguments().getParcelableArray(ARG_PARAM1);
            this.empties = getArguments().getBooleanArray(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_card_scroller, container, false);

        // Set the outlets
        this.leftButton    = v.findViewById(R.id.leftButton);
        this.leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to previous fragment
                scrollView.focusFragment(fragments[currentIndex - 1], true);
            }
        });
        this.rightButton   = v.findViewById(R.id.rightButton);
        this.rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to next fragment
                scrollView.focusFragment(fragments[currentIndex + 1], true);
            }
        });

        this.titleTextView = v.findViewById(R.id.titleTextView);
        this.typeTextView = v.findViewById(R.id.typeLabel);
        this.scrollView    = v.findViewById(R.id.recipeCardScrollView);
        this.scrollView.setLayout(40,800);
        this.scrollView.setListener(this);

        if (this.recipes != null) {
            loadAllViews();
        }
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this.titleTextView,16,26,1, TypedValue.COMPLEX_UNIT_SP);

        return v;
    }

    public void setImageOnRecipe(Bitmap image, StoredRecipe recipe) {

        // Store the image.
        this.recipeIDtoImage.put(recipe.recipeID, image);
        // Get the fragment and set the image.
        RecipeCardFragment f = this.recipeIDtoFragment.get(recipe.recipeID);
        if (f != null) {
            f.setRecipeImage(image);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // region Recipe card listeners

    @Override
    public void scrolledToCard(RecipeCardFragment card) {

        // Hide show paging buttons.
        this.currentIndex = card.getIndex();
        if (this.currentIndex > 0) {
            this.leftButton.setVisibility(View.VISIBLE);
        } else {
            this.leftButton.setVisibility(View.INVISIBLE);
        }
        if (this.currentIndex < (this.fragments.length - 1)) {
            this.rightButton.setVisibility(View.VISIBLE);
        } else {
            this.rightButton.setVisibility(View.INVISIBLE);
        }
        //TODO: Localize
        if (!card.getIsEmpty()) {
            StoredRecipe recipe = this.recipes[this.currentIndex];
            this.titleTextView.setText(recipe.name);
            if (recipe.mealType == SpoonacularMealType.BREAKFAST) {
                this.typeTextView.setText("Breakfast");
            } else if (recipe.mealType == SpoonacularMealType.LUNCH) {
                this.typeTextView.setText("Lunch");
            } else if (recipe.mealType == SpoonacularMealType.DINNER) {
                this.typeTextView.setText("Dinner");
            } else {
                this.typeTextView.setText("Snack");
            }
        } else {
            this.typeTextView.setText("No meal(s) planned!");
            this.titleTextView.setText("");
        }
    }

    @Override
    public void clickedOnFragment(RecipeCardFragment fragment) {
        // Determine if the fragment is an 'add' fragment or recipe.
        int index = fragment.getIndex();
        boolean isEmpty = this.empties[index];

        if (fragment.getIndex() != this.currentIndex) {
            this.scrollView.focusFragment(fragment, true);
        } else {
            if (this.listener != null) {
                if (isEmpty) {
                    // Listener should create a new fragment.
                    this.listener.selectedNewRecipeForIndex(index);
                } else {
                    this.listener.selectedShowDetailForIndex(fragment.getIndex());
                }
            }
        }
    }

    @Override
    public void editFragment(RecipeCardFragment fragment) {
        // Relay to the listener.
        if (this.listener != null) {
            this.listener.selectedNewRecipeForIndex(fragment.getIndex());
        }
    }
    // endregion

    public void setListener(CardScrollerFragmentListener listener) {
        this.listener = listener;
    }
    public interface CardScrollerFragmentListener {
        /**
         * Called when the user presses the recipe card to show more details.
         * @param index The index of the card, set in setValues().
         */
        void selectedShowDetailForIndex(int index);

        /**
         * Called when the user wants to add a new recipe.
         * @param index The index of the card, set in setValues().
         */
        void selectedNewRecipeForIndex(int index);

    }
}
