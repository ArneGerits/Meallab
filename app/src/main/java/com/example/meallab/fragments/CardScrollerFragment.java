package com.example.meallab.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.meallab.R;
import com.example.meallab.customViews.RecipeCardScrollView;
import com.example.meallab.stored_data.StoredRecipe;

/**
 * Fragment that allows the user to scroll their recipe cards.
 */
public class CardScrollerFragment extends Fragment implements RecipeCardScrollView.RecipeCardScrollViewListener {

    // Outlets
    RecipeCardScrollView scrollView;
    Button leftButton;
    Button rightButton;
    TextView titleTextView;

    // ----

    static String ARG_PARAM1 = "recipes";

    // The recipes shown by this scroller.
    StoredRecipe[] recipes;
    // The card fragment shown by this scroller.
    RecipeCardFragment[] fragments;

    public CardScrollerFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new card scroller.
     *
     * @param recipes The recipes
     * @return A new instance of fragment CardScrollerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardScrollerFragment newInstance(StoredRecipe[] recipes) {
        CardScrollerFragment fragment = new CardScrollerFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PARAM1,recipes);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Sets all values on the fragment and creates the view.
     *
     */
    public void setValues(StoredRecipe[] recipes) {

        CardScrollerFragment fragment = new CardScrollerFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PARAM1,recipes);
        fragment.setArguments(args);

        this.recipes = recipes;
        loadAllViews();
    }
    // Loads all views
    // @pre recipes must be initialized.
    private void loadAllViews() {

        this.fragments = new RecipeCardFragment[this.recipes.length];
        // Cycle all recipes and create cards, then add the card to the scrollview.
        for (int i = 0; i < this.recipes.length; i++) {
            StoredRecipe r = this.recipes[i];

            // Create a new card.
            RecipeCardFragment f = RecipeCardFragment.newInstance(r.name,r.cookingMins,
                    r.numberOfServings,r.pricePerServing, r.macroNutrients);
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
