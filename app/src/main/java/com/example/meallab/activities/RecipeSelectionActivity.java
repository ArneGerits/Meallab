package com.example.meallab.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meallab.R;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.meallab.Spoonacular.*;
import com.example.meallab.fragments.RecipeSelectionRowFragment;
import com.google.gson.Gson;

import java.util.Arrays;

/**
 * Allows the user to choose between 3 recipes for each meal type.
 */
public class RecipeSelectionActivity extends AppCompatActivity implements SpoonacularAPI.SpoonacularSimpleRecipeListener, RecipeSelectionRowFragment.recipeInfoFragmentListener {

    // ----- Constants ------
    public static final String RECIPES_SELECTED = "recipes_selected";

    // ----- Recipes -----

    // The recipes loaded (9).
    private Recipe[][] recipesLoaded;

    // The recipes currently being shown (3).
    private Recipe[] recipesShowing;

    // The recipes the user has chosen.
    private Recipe[] recipesChosen;

    // The current offset of the recipes.
    private int[] recipesOffset;

    // ------ Outlets ------

    // The reroll button is positioned in the upper left.
    ImageButton rerollButton;
    TextView titleTextView;
    // The upper right button can either be a 'next' button, to choose next recipe or a 'confirm' button.
    ImageButton upperRightButton;

    RecipeSelectionRowFragment topFragment;
    RecipeSelectionRowFragment middleFragment;
    RecipeSelectionRowFragment bottomFragment;
    // ------

    // Used for api communication with Spoonacular
    private SpoonacularAPI api;

    // The meal types the user has to choose recipes for..
    private SpoonacularMealType[] meals;
    // The current index of the mealtype to choose recipes for.
    private int mealIndex = 0;

    boolean firstLoad = false;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_selection);

        String jsonMeals = getIntent().getStringExtra("meals");
        this.meals = gson.fromJson(jsonMeals, SpoonacularMealType[].class);

        // ----- Setting outlets ------

        this.topFragment = (RecipeSelectionRowFragment) this.getSupportFragmentManager().findFragmentById(R.id.topInfo);
        this.middleFragment = (RecipeSelectionRowFragment) this.getSupportFragmentManager().findFragmentById(R.id.middleInfo);
        this.bottomFragment = (RecipeSelectionRowFragment) this.getSupportFragmentManager().findFragmentById(R.id.bottomInfo);

        this.rerollButton  = this.findViewById(R.id.rerollButton);
        this.upperRightButton = this.findViewById(R.id.nextButton);
        this.titleTextView = this.findViewById(R.id.titleTextView);

        this.rerollButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Increase the current recipe offset.
                recipesOffset[mealIndex] += 3;

                showNextRecipes(recipesOffset[mealIndex] % 9 == 0);
            }
        });
        this.upperRightButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // If the meal index was on the last meal we confirm.
                if (mealIndex == meals.length - 1) {
                    // Finalizes the activity.
                    confirm();
                }
                // We go next.
                else {
                    // Goes to the next meal.
                    nextMeal();
                }
            }
        });

        // ------

        // Create the arrays.
        this.recipesOffset = new int[this.meals.length];
        this.recipesLoaded = new Recipe[this.meals.length][];
        this.recipesChosen = new Recipe[this.meals.length];

        // Setup the API communication
        api = new SpoonacularAPI(this);

        this.showNextRecipes(true);

        this.reloadAllViews();
    }

    @Override
    public void onBackPressed() {
        // your code.

        if (this.mealIndex != 0) {
            // TODO: Decide on this.
            // Nullify the recipe chosen?
            this.recipesChosen[this.mealIndex] = null;
            // Nullify the offset?
            this.recipesOffset[this.mealIndex] = 0;
            // Nullify recipes loaded?
            this.recipesLoaded[this.mealIndex] = null;

            // Go back to the previous meal index.
            this.mealIndex--;

            // Reload every view.
            this.reloadAllViews();

            this.showNextRecipes(false);
        } else {
            // If were on the first meal index hide the activity.
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, resultIntent);
            finish();
        }

    }
    // Restores every view to initial state, called when paging.
    private void reloadAllViews() {

        // Deselect all fragments.
        this.topFragment.hide(true);
        this.middleFragment.hide(true);
        this.bottomFragment.hide(true);

        if (mealIndex == this.meals.length - 1) {
            this.upperRightButton.setVisibility(View.INVISIBLE);
            this.upperRightButton.setImageResource(R.drawable.checkmark);
        } else {
            this.upperRightButton.setVisibility(View.INVISIBLE);
            this.upperRightButton.setImageResource(R.drawable.button_next);
        }
        this.upperRightButton.setEnabled(false);
        this.rerollButton.setEnabled(true);

        // Set a new title
        this.setTitle(this.meals[this.mealIndex]);
    }
    // Hides all recipe info fragments.
    private void hideFragments(boolean showSpin) {
        int[] ids = {R.id.topInfo,R.id.middleInfo,R.id.bottomInfo};

        for (int i = 0; i < ids.length; i++) {
            RecipeSelectionRowFragment frag = (RecipeSelectionRowFragment)getSupportFragmentManager().findFragmentById(ids[i]);
            frag.hide(true);
        }

    }
    // Loads 9 breakfast recipes.
    // @param offset The offset to use in the retrieval of the recipes.
    protected void loadNewRecipes(int offset) {
        // Create a breakfast request.
        RecipeRequest request = new RecipeRequest(this.meals[this.mealIndex]);
        request.offset        = offset;
        // todo: Intellegently set min/max values.
        request.maxCals = 800;

        api.retrieveRecipes(request, this);
    }

    // Sets the correct title on the title label.
    protected  void setTitle(SpoonacularMealType type) {

        //TODO: Localize.
        if (this.meals[this.mealIndex]== SpoonacularMealType.BREAKFAST) {
            this.titleTextView.setText("Breakfast");
        } else if (this.meals[this.mealIndex] == SpoonacularMealType.LUNCH) {
            this.titleTextView.setText("Lunch");
        } else if (this.meals[this.mealIndex] == SpoonacularMealType.DINNER) {
            this.titleTextView.setText("Dinner");
        } else {
            this.titleTextView.setText("Snack");
        }
    }

    // Loads all recipe data into the views,
    // Also loads the images corresponding the recipes.
    protected void loadRecipeData(Recipe[] recipes) {

        // Get the image loader from the Volley singleton.
        ImageLoader imageLoader = VolleySingleton.getInstance(this).getImageLoader();

        int[] ids = {R.id.topInfo,R.id.middleInfo,R.id.bottomInfo};

        for (int i = 0; i < ids.length; i++) {

            final Recipe currentRecipe = recipes[i];
            // Get the url.
            String url = currentRecipe.getImageURLForSize(SpoonacularImageSize.S_636x393);
            // Get the fragment.

            final RecipeSelectionRowFragment frag = (RecipeSelectionRowFragment)getSupportFragmentManager().findFragmentById(ids[i]);

            // Set the data.
            frag.setTitle(currentRecipe.title);
            frag.setListener(this);
            frag.detailFragment().setValues(currentRecipe.nutrients[0].amount, currentRecipe.servings,
                    currentRecipe.readyInMinutes, currentRecipe.getCost());

            // Load the image.
            imageLoader.get(url, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        Bitmap result = response.getBitmap();
                        frag.setImage(result);
                        frag.show();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //todo: show error screen.
                    System.out.println("VOLLEY ERROR recipe select " +  error.toString());
                }
            });
        }
        this.rerollButton.setEnabled(true);
    }
    //region Actions

    // Called when the user clicks the reroll button.
    private void showNextRecipes(boolean loadNew) {

        // Disable the reload button when a reload is occuring.
        this.rerollButton.setEnabled(false);

        int currentOffset = this.recipesOffset[this.mealIndex];

        this.hideFragments(loadNew);
        this.madeChoice(null);

        // Checks if new recipes need to be loaded.
        if (loadNew) {
            // Load new recipes from the network.
            loadNewRecipes(currentOffset);
        } else {
            this.recipesShowing = Arrays.copyOfRange(recipesLoaded[this.mealIndex],currentOffset % 9, (currentOffset % 9) + 3);

            // Load recipe data for recipes that were already retrieved.
            loadRecipeData(this.recipesShowing);
        }

    }
    // Called when the user clicks the confirm button.
    private void confirm() {
        // Convert the chosen recipes to json.
        String chosenRecipes = this.gson.toJson(this.recipesChosen);
        // Put the chosen recipes in the intent
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RECIPES_SELECTED, chosenRecipes);
        setResult(Activity.RESULT_OK, resultIntent);
        // Finalize.
        finish();
    }

    // Called when the user clicks on the next button.
    private void nextMeal() {
        // Move forward a meal index.
        this.mealIndex++;

        this.reloadAllViews();

        this.showNextRecipes(true);
    }
    //endregion

    //region SpoonacularBatchRecipeListener
    @Override
    public void retrievedRecipes(Recipe[] recipes) {

        this.recipesLoaded[this.mealIndex] = recipes;

        this.recipesShowing = Arrays.copyOfRange(recipes,0,3);

        // Load the 3 images.
        this.loadRecipeData(this.recipesShowing);

        // Animate the reroll button on first load.
        if (!this.firstLoad) {
            this.firstLoad = true;


            Animation expandIn = AnimationUtils.loadAnimation(this, R.anim.expand_in);
            expandIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    rerollButton.setVisibility(View.VISIBLE);
                    rerollButton.setEnabled(true);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rerollButton.setScaleX(1.0f);
                    rerollButton.setScaleX(1.0f);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            rerollButton.startAnimation(expandIn);
        }
    }

    @Override
    public void simpleSpoonacularErrorHandler() {

    }

    //endregion

    private void deselectFragments(int[] fragmentIDs) {
        for (int id: fragmentIDs) {
            RecipeSelectionRowFragment f = (RecipeSelectionRowFragment)getSupportFragmentManager().findFragmentById(id);
            f.setSelected(false);
        }
    }
    // Called when the user selects or deselects a recipe.
    private void madeChoice(Recipe r) {

        this.recipesChosen[this.mealIndex] = r;

        if (r == null) {
            if (this.upperRightButton.getVisibility() != View.INVISIBLE) {
                // Animate the hiding of the upper right button.
                Animation expandOut = AnimationUtils.loadAnimation(this, R.anim.expand_out);
                expandOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        upperRightButton.setEnabled(false);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        upperRightButton.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                upperRightButton.startAnimation(expandOut);
            }

        } else {
            if (this.upperRightButton.getVisibility() != View.VISIBLE) {
                Animation expandIn = AnimationUtils.loadAnimation(this, R.anim.expand_in);
                expandIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        upperRightButton.setVisibility(View.VISIBLE);
                        upperRightButton.setEnabled(true);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        upperRightButton.setScaleX(1.0f);
                        upperRightButton.setScaleX(1.0f);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                upperRightButton.startAnimation(expandIn);
            }
        }
    }
    //region

    @Override
    public void selectedFragment(RecipeSelectionRowFragment fragment, boolean selected) {

        // If a fragment became deselected invalidate the choice.
        if (!selected) {
            this.madeChoice(null);

            // Hide the upper right button.

            return;
        }
        // Top fragment was selected.
        if (fragment == this.topFragment) {

            // Deselect other fragments.
            this.middleFragment.setSelected(false);
            this.bottomFragment.setSelected(false);

            this.madeChoice(this.recipesShowing[0]);
        }
        // Middle fragment was selected.
        else if (fragment == middleFragment) {
            // Deselect other fragments.
            this.topFragment.setSelected(false);
            this.bottomFragment.setSelected(false);

            this.madeChoice(this.recipesShowing[1]);
        }
        // Bottom fragment was selected.
        else {
            // Deselect other fragments.
            this.middleFragment.setSelected(false);
            this.topFragment.setSelected(false);

            this.madeChoice(this.recipesShowing[2]);
        }
    }

    @Override
    public void moreInfoFragment(RecipeSelectionRowFragment fragment) {

        //todo: use circular reveal.
        // Top fragment was selected.
        if (fragment.getId() == R.id.topInfo) {
            Recipe top = this.recipesShowing[0];

            Intent intent = new Intent(this,RecipeOverviewActivity.class);
            intent.putExtra("id", top.id);
            intent.putExtra("from DayOverview",false);
            intent.putExtra("mealType",gson.toJson((meals[this.mealIndex])));

            startActivity(intent);

        }
        // Middle fragment was selected.
        else if (fragment.getId() == R.id.middleInfo) {
            Recipe middle = this.recipesShowing[1];

            Intent intent = new Intent(this,RecipeOverviewActivity.class);
            intent.putExtra("id",middle.id);
            intent.putExtra("from DayOverview",false);
            intent.putExtra("mealType",gson.toJson((meals[this.mealIndex])));

            startActivity(intent);
        }
        // Bottom fragment was selected.
        else {
            Recipe bottom = this.recipesShowing[2];

            Intent intent = new Intent(this,RecipeOverviewActivity.class);
            intent.putExtra("obj",bottom.id);
            intent.putExtra("from DayOverview",false);
            intent.putExtra("mealType",gson.toJson((meals[this.mealIndex])));
            startActivity(intent);
        }
    }

}