package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.example.meallab.Spoonacular.Recipe;
import com.example.meallab.Spoonacular.RecipeRequest;
import com.example.meallab.Spoonacular.SpoonacularAPI;
import com.example.meallab.Spoonacular.SpoonacularBatchRecipeListener;
import com.example.meallab.Spoonacular.SpoonacularMealType;

public class RecipeSelectionActivity extends AppCompatActivity implements SpoonacularBatchRecipeListener, RecipeInfoFragment.OnFragmentInteractionListener {

    // The recipes loaded.
    private Recipe[] recipes;
    // Used for api communication with Spoonacular
    private final SpoonacularAPI api = new SpoonacularAPI(this);

    // The current offset of the recipes.
    private int currentOffset = 0;

    // The meal type of this activity.
    SpoonacularMealType mealType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_selection);

        // Resolve the meal type.
        //Bundle b      = getIntent().getExtras();
        //this.mealType = SpoonacularMealType.valueOf(b.getString("mealType"));

        //loadRecipes(0);
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

    //region SpoonacularBatchRecipeListener
    @Override
    public void retrievedRecipes(Recipe[] recipes) {
        this.recipes = recipes;
    }

    @Override
    public void batchRecipesErrorHandler() {
        // todo: Handle error here.
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println("interacted with fragment!");
        // Switch fragment to selected state....
        // But how do we know which fragment was selected?
    }
    //endregion
}
