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
public class CardScrollerFragment extends Fragment implements RecipeCardScrollView.RecipeCardScrollViewListener {

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
    private int amountOfCards;

    // The card fragment shown by this scroller.
    private RecipeCardFragment[] fragments;

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
     *
     */
    public void setValues(StoredRecipe[] recipes, boolean[] emptyIndices, int amountOfCards) {

        CardScrollerFragment fragment = new CardScrollerFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PARAM1,recipes);
        args.putBooleanArray(ARG_PARAM2, emptyIndices);
        fragment.setArguments(args);

        this.recipes = recipes;
        this.empties = emptyIndices;
        this.amountOfCards = amountOfCards;
        loadAllViews();
    }
    // Loads all views
    // @pre recipes must be initialized.
    private void loadAllViews() {

        this.fragments = new RecipeCardFragment[this.amountOfCards];

        // Used to get the stored recipes.
        Iterator<StoredRecipe> it = Arrays.asList(this.recipes).iterator();

        System.out.println("what: " + this.recipes.length);
        System.out.println("empties: " + this.empties[0]);

        // Cycle all recipes and create cards, then add the card to the scrollview.
        for (int i = 0; i < this.amountOfCards; i++) {

            RecipeCardFragment f;

            // Create a new card.
            if (this.empties[i]) {
                f = RecipeCardFragment.newEmptyInstance();
            } else {
                System.out.println("loop: " +  i);
                StoredRecipe r = it.next();
                f = RecipeCardFragment.newInstance(r.name,r.cookingMins,
                        r.numberOfServings,r.pricePerServing, r.macroNutrients);
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

    // region RecipeCardScrollViewListener

    @Override
    public void scrolledToCard(RecipeCardFragment card) {
        this.titleTextView.setText(card.getName());
    }
    // endregion
}
