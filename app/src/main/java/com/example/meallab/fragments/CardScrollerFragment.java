package com.example.meallab.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.meallab.R;
import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.example.meallab.customViews.RecipeCardScrollView;
import com.example.meallab.storing_data.StoredRecipe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Fragment that allows the user to scroll their recipe cards.
 */
public class CardScrollerFragment extends Fragment implements RecipeCardScrollView.RecipeCardScrollViewListener,
        RecipeCardFragment.RecipeCardFragmentListener {

    // Outlets
    private RecipeCardScrollView scrollView;
    private Button leftButton;
    private Button rightButton;
    private TextView titleTextView;

    // ----

    private static String ARG_PARAM1 = "recipes";
    private static String ARG_PARAM2 = "indices";

    // The recipes shown by this scroller.
    private StoredRecipe[] recipes;
    private boolean[] empties;

    // The card fragment shown by this scroller.
    private RecipeCardFragment[] fragments;

    private CardScrollerFragmentListener listener;

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
            }
            // Add it to the fragments.
            this.fragments[i] = f;

        }
        // Add the cards to the scrollview.
        this.scrollView.setFragments(this.fragments);
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
                System.out.println("scroll to left");
            }
        });
        this.rightButton   = v.findViewById(R.id.rightButton);
        this.rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("scroll to right");
            }
        });

        this.titleTextView = v.findViewById(R.id.titleTextView);
        this.scrollView    = v.findViewById(R.id.recipeCardScrollView);
        this.scrollView.setLayout(20,400);
        this.scrollView.setListener(this);

        if (this.recipes != null) {
            loadAllViews();
        }
        return v;
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
        this.titleTextView.setText(card.getName());
    }

    @Override
    public void clickedOnFragment(RecipeCardFragment fragment) {
        System.out.println("CLICKED ON FRAGMENT");
        // Determine if the fragment is an 'add' fragment or recipe.
        int index = fragment.getIndex();
        boolean isEmpty = this.empties[index];
        if (this.listener != null) {
            if (isEmpty) {
                this.listener.selectedNewRecipeForIndex(index);
            } else {
                // Determine the correct offset.
                int offset = 0;
                for (boolean b : this.empties) {
                    if (b) {
                        offset++;
                    }
                }
                this.listener.selectedShowDetailForIndex(index - offset);
            }
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
         * Called when the user wants to edit the recipe.
         * @param index The index of the card, set in setValues().
         */
        void selectedEditForIndex(int index);

        /**
         * Called when the user wants to add a new recipe.
         * @param index The index of the card, set in setValues().
         */
        void selectedNewRecipeForIndex(int index);

    }
}
