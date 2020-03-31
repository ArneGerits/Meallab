package com.example.meallab.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meallab.R;
import com.example.meallab.Spoonacular.SpoonacularAPI.SpoonacularSimpleRecipeListener;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.meallab.Spoonacular.*;
import com.example.meallab.fragments.RecipeSelectionRow;
import com.google.gson.Gson;

import java.util.Arrays;

/**
 * Allows the user to choose between 3 recipes.
 */
public class RecipeSelectionActivity extends AppCompatActivity implements SpoonacularAPI.SpoonacularSimpleRecipeListener, RecipeSelectionRow.recipeInfoFragmentListener {

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
    Button rerollButton;
    // The upper right button can either be a 'next' button, to choose next recipe or a 'confirm' button,
    // to finish the activity.
    Button upperRightButton;

    // ------

    // Used for api communication with Spoonacular
    private SpoonacularAPI api;

    // The meal types the user has to choose recipes for..
    private SpoonacularMealType[] meals;
    // The current index of the mealtype to choose recipes for.
    private int mealIndex = 0;

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_selection);

        String jsonMeals = getIntent().getStringExtra("meals");
        this.meals = gson.fromJson(jsonMeals, SpoonacularMealType[].class);
        this.rerollButton  = this.findViewById(R.id.rerollButton);
        this.upperRightButton = this.findViewById(R.id.confirmButton);

        this.rerollButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reroll();
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

        this.hideFragments(true);

        // Create the arrays.
        this.recipesOffset = new int[this.meals.length];
        this.recipesLoaded = new Recipe[this.meals.length][];
        this.recipesChosen = new Recipe[this.meals.length];

        // Setup the API communication
        api = new SpoonacularAPI(this);
        // Load the recipes.
        loadNewRecipes(this.recipesOffset[this.mealIndex]);
    }

    @Override
    public void onBackPressed() {
        // your code.
        System.out.println("back is pressed homey");

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
            // Load new recipes
            this.loadNewRecipes(this.recipesOffset[this.mealIndex]);
        } else {
            // If were on the first meal index hide the activity.
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, resultIntent);
            finish();
        }

    }

    // Hides all recipe info fragments.
    private void hideFragments(boolean showSpin) {
        int[] ids = {R.id.topInfo,R.id.middleInfo,R.id.bottomInfo};

        for (int i = 0; i < ids.length; i++) {
            RecipeSelectionRow frag = (RecipeSelectionRow)getSupportFragmentManager().findFragmentById(ids[i]);
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

        System.out.println("trying to receive recipes now");
        api.retrieveRecipes(request, this);
    }

    // Sets the correct title on the title label.
    protected  void setTitle(SpoonacularMealType type) {

        //TODO: Localize.
        TextView title = (TextView) findViewById(R.id.titleTextView);
        if (this.meals[this.mealIndex]== SpoonacularMealType.BREAKFAST) {
            title.setText("Choose breakfast");
        } else if (this.meals[this.mealIndex] == SpoonacularMealType.LUNCH) {
            title.setText("Choose lunch");
        } else if (this.meals[this.mealIndex] == SpoonacularMealType.SNACK) {
            title.setText("Choose snack");
        } else {
            title.setText("Choose dinner");
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

            final RecipeSelectionRow frag = (RecipeSelectionRow)getSupportFragmentManager().findFragmentById(ids[i]);

            // Set the data.
            frag.setTitle(currentRecipe.title);
            frag.setListener(this);
            frag.detailFragment().setValues(currentRecipe.nutrients[0].amount, currentRecipe.servings,
                    currentRecipe.cookingMinutes, currentRecipe.getCost());

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
                }
            });
        }
        // Increase the current recipe offset.
        this.recipesOffset[this.mealIndex] += 3;
    }
    //region Actions

    // Called when the user clicks the reroll button.
    private void reroll() {

        int currentOffset = this.recipesOffset[this.mealIndex];

        boolean loadNew = (currentOffset % 9) == 0;

        this.hideFragments(loadNew);
        this.madeChoice(null);

        // Checks if new recipes need to be loaded.
        if (loadNew) {
            // Load new recipes from the network.
            loadNewRecipes(currentOffset);
        } else {
            this.recipesShowing = Arrays.copyOfRange(recipesLoaded[this.mealIndex],currentOffset % 10, (currentOffset + 3) % 10);

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

        // Load new recipes.
        loadNewRecipes(this.recipesOffset[this.mealIndex]);
    }
    //endregion

    //region SpoonacularBatchRecipeListener
    @Override
    public void retrievedRecipes(Recipe[] recipes) {

        this.recipesLoaded[this.mealIndex] = recipes;

        this.recipesShowing = Arrays.copyOfRange(recipes,0,3);

        // Load the 3 images.
        this.loadRecipeData(this.recipesShowing);
    }

    @Override
    public void simpleSpoonacularErrorHandler() {

    }

    //endregion

    private void deselectFragments(int[] fragmentIDs) {
        for (int id: fragmentIDs) {
            RecipeSelectionRow f = (RecipeSelectionRow)getSupportFragmentManager().findFragmentById(id);
            f.setSelected(false);
        }
    }
    // Called when the user selects or deselects a recipe.
    private void madeChoice(Recipe r) {

        this.recipesChosen[this.mealIndex] = r;

        // Enable/Disable the upper right button accordingly.
        this.upperRightButton.setEnabled((r != null));
    }
    //region

    @Override
    public void selectedFragment(RecipeSelectionRow fragment, boolean selected) {

        // If a fragment became deselected invalidate the choice.
        if (!selected) {
            this.madeChoice(null);

            return;
        }
        // Top fragment was selected.
        if (fragment.getId() == R.id.topInfo) {
            // Deselect other fragments.
            this.deselectFragments(new int[]{R.id.middleInfo,R.id.bottomInfo});

            this.madeChoice(this.recipesShowing[0]);
        }
        // Middle fragment was selected.
        else if (fragment.getId() == R.id.middleInfo) {
            // Deselect other fragments.
            this.deselectFragments(new int[]{R.id.topInfo,R.id.bottomInfo});

            this.madeChoice(this.recipesShowing[1]);
        }
        // Bottom fragment was selected.
        else {
            // Deselect other fragments.
            this.deselectFragments(new int[]{R.id.topInfo,R.id.middleInfo});

            this.madeChoice(this.recipesShowing[2]);
        }
    }

    @Override
    public void moreInfoFragment(RecipeSelectionRow fragment) {

        //todo: use circular reveal.
        // Top fragment was selected.
        if (fragment.getId() == R.id.topInfo) {
            Recipe top = this.recipesShowing[0];

            Intent intent = new Intent(this,RecipeOverviewActivity.class);
            intent.putExtra("obj", gson.toJson(top));
            startActivity(intent);

        }
        // Middle fragment was selected.
        else if (fragment.getId() == R.id.middleInfo) {
            Recipe middle = this.recipesShowing[1];

            Intent intent = new Intent(this,RecipeOverviewActivity.class);
            intent.putExtra("obj", gson.toJson(middle));
            startActivity(intent);
        }
        // Bottom fragment was selected.
        else {
            Recipe bottom = this.recipesShowing[2];

            Intent intent = new Intent(this,RecipeOverviewActivity.class);
            intent.putExtra("obj", gson.toJson(bottom));
            startActivity(intent);
        }
    }

}
