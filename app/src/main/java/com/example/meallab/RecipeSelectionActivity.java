package com.example.meallab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.meallab.Spoonacular.*;
import com.example.meallab.fragments.RecipeInfoFragment;

import java.util.Arrays;

/**
 * Allows the user to choose between 3 recipes.
 */
public class RecipeSelectionActivity extends AppCompatActivity implements SpoonacularBatchRecipeListener, RecipeInfoFragment.recipeInfoFragmentListener {

    // The recipes loaded.
    private Recipe[] recipes;
    // Used for api communication with Spoonacular
    private SpoonacularAPI api;

    // The current offset of the recipes.
    private int currentOffset = 0;

    // The meal type of this activity.
    private SpoonacularMealType mealType = SpoonacularMealType.BREAKFAST;

    // The recipe the user has chosen.
    Recipe recipeChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_selection);

        Button reroll  = this.findViewById(R.id.rerollButton);
        Button confirm = this.findViewById(R.id.confirmButton);

        reroll.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reroll();
            }
        });
        confirm.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        this.hideFragments(true);

        // Setup the API communication
        api = new SpoonacularAPI(this);
        // Load the recipes.
        loadNewRecipes(0);
    }

    public void setMealType(SpoonacularMealType mealType) {
        this.mealType = mealType;
        this.setTitle(this.mealType);
    }

    // Hides all recipe info fragments.
    private void hideFragments(boolean showSpin) {
        int[] ids = {R.id.topInfo,R.id.middleInfo,R.id.bottomInfo};

        for (int i = 0; i < ids.length; i++) {
            RecipeInfoFragment frag = (RecipeInfoFragment)getSupportFragmentManager().findFragmentById(ids[i]);
            frag.hide(true);
        }

    }
    // Loads 9 breakfast recipes.
    // @param offset The offset to use in the retrieval of the recipes.
    protected void loadNewRecipes(int offset) {
        // Create a breakfast request.
        RecipeRequest request = new RecipeRequest(this.mealType);
        request.offset        = offset;
        // todo: Intellegently set min/max values.
        request.maxCals = 800;

        api.retrieveRecipes(request, this);
    }

    // Sets the correct title on the title label.
    protected  void setTitle(SpoonacularMealType type) {

        TextView title = (TextView) findViewById(R.id.titleTextView);
        if (this.mealType == SpoonacularMealType.BREAKFAST) {
            title.setText("Choose breakfast");
        } else if (this.mealType == SpoonacularMealType.LUNCH) {
            title.setText("Choose lunch");
        } else if (this.mealType == SpoonacularMealType.SNACK) {
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

            Recipe currentRecipe = recipes[i];
            // Get the url.
            String url = currentRecipe.getImageURLForSize(SpoonacularImageSize.S_636x393);
            // Get the fragment.

            final RecipeInfoFragment frag = (RecipeInfoFragment)getSupportFragmentManager().findFragmentById(ids[i]);

            // Set the data.
            frag.setTitle(currentRecipe.title);
            frag.setListener(this);
            frag.detailFragment().setValues(currentRecipe.calories, currentRecipe.servings,
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
        currentOffset += 3;
    }
    //region Actions

    // Called when the user clicks the reroll button.
    private void reroll() {

        boolean loadNew = (currentOffset % 9) == 0;

        this.hideFragments(loadNew);
        this.madeChoice(null);

        // Checks if new recipes need to be loaded.
        if (loadNew) {
            // Load new recipes from the network.
            loadNewRecipes(currentOffset);
        } else {
            // Load recipe data for recipes that were already retrieved.
            loadRecipeData(Arrays.copyOfRange(recipes,currentOffset % 10, (currentOffset + 3) % 10));
        }

    }
    // Called when the user clicks the confirm button.
    private void confirm() {

    }

    //endregion

    //region SpoonacularBatchRecipeListener
    @Override
    public void retrievedRecipes(Recipe[] recipes) {

        this.recipes = recipes;

        System.out.println("Retrieved the recipes");
        // Load the 3 images.
        this.loadRecipeData(Arrays.copyOfRange(recipes,0,3));
    }
    @Override
    public void batchRecipesErrorHandler() {
        // todo: Handle error here.
    }
    //endregion

    private void deselectFragments(int[] fragmentIDs) {
        for (int id: fragmentIDs) {
            RecipeInfoFragment f = (RecipeInfoFragment)getSupportFragmentManager().findFragmentById(id);
            f.setSelected(false);
        }
    }
    private void madeChoice(Recipe r) {

        this.recipeChosen = r;
        // The confirm button is not enabled when the user has not made a choice.
        Button confirm = this.findViewById(R.id.confirmButton);
        confirm.setEnabled((r != null));
    }
    //region

    @Override
    public void selectedFragment(RecipeInfoFragment fragment, boolean selected) {

        // If a fragment became deselected invalidate the choice.
        if (!selected) {
            this.madeChoice(null);

            return;
        }
        // Top fragment was selected.
        if (fragment.getId() == R.id.topInfo) {
            // Deselect other fragments.
            this.deselectFragments(new int[]{R.id.middleInfo,R.id.bottomInfo});

            this.madeChoice(recipes[(currentOffset % 9)]);
        }
        // Middle fragment was selected.
        else if (fragment.getId() == R.id.middleInfo) {
            // Deselect other fragments.
            this.deselectFragments(new int[]{R.id.topInfo,R.id.bottomInfo});

            this.madeChoice(recipes[(currentOffset % 9) + 1]);
        }
        // Bottom fragment was selected.
        else {
            // Deselect other fragments.
            this.deselectFragments(new int[]{R.id.topInfo,R.id.middleInfo});

            this.madeChoice(recipes[(currentOffset % 9) + 2]);
        }
    }

    @Override
    public void moreInfoFragment(RecipeInfoFragment fragment) {

        //todo: use circular reveal.
        // Top fragment was selected.
        if (fragment.getId() == R.id.topInfo) {
            Recipe top = recipes[currentOffset % 9];
            Intent intent = new Intent(this,RecipeOverviewActivity.class);
            startActivity(intent);

        }
        // Middle fragment was selected.
        else if (fragment.getId() == R.id.middleInfo) {
            Recipe middle = recipes[(currentOffset % 9) + 1];
        }
        // Bottom fragment was selected.
        else {
            Recipe bottom = recipes[(currentOffset % 9) + 2];
        }
    }
}
