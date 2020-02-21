package com.example.meallab;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.ImageLoader;
import com.example.meallab.Spoonacular.*;

import java.util.Arrays;

public class RecipeSelectionActivity extends AppCompatActivity implements SpoonacularBatchRecipeListener, RecipeInfoFragment.recipeInfoFragmentListener {

    // The recipes loaded.
    private Recipe[] recipes;
    // Used for api communication with Spoonacular
    private SpoonacularAPI api;

    // The current offset of the recipes.
    private int currentOffset = 0;

    // The meal type of this activity.
    SpoonacularMealType mealType;

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

        // Resolve the meal type.
        Bundle b      = getIntent().getExtras();
        this.mealType = SpoonacularMealType.valueOf(b.getString("mealType"));
        this.setTitle(this.mealType);

        // Setup the API communication
        api = new SpoonacularAPI(this);
        // Load the recipes.
        loadRecipes(0);
    }

    // Loads 9 breakfast recipes.
    // @param offset The offset to use in the retrieval of the recipes.
    protected void loadRecipes(int offset) {
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

            RecipeInfoFragment frag = (RecipeInfoFragment)getSupportFragmentManager().findFragmentById(ids[i]);
            // Load the image.
            frag.networkImageView().setImageUrl(url,imageLoader);
            // Set the data.
            frag.setTitle(currentRecipe.title);
            frag.setListener(this);
            frag.detailFragment().setValues(currentRecipe.calories, currentRecipe.servings,
                    currentRecipe.cookingMinutes, currentRecipe.getCost());
        }
    }
    //region Actions
    // Called when the user clicks the reroll button.
    private void reroll() {

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

    //region

    @Override
    public void selectedFragment(RecipeInfoFragment fragment, boolean selected) {
        // Top fragment was selected.
        if (fragment.getId() == R.id.topInfo) {

        }
        // Middle fragment was selected.
        else if (fragment.getId() == R.id.middleInfo) {

        }
        // Bottom fragment was selected.
        else {

        }
    }

    @Override
    public void moreInfoFragment(RecipeInfoFragment fragment) {
        // Top fragment was selected.
        if (fragment.getId() == R.id.topInfo) {

        }
        // Middle fragment was selected.
        else if (fragment.getId() == R.id.middleInfo) {

        }
        // Bottom fragment was selected.
        else {

        }
    }
}
