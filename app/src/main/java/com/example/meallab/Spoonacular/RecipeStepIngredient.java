package com.example.meallab.Spoonacular;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a single ingredient needed in a recipe step.
 * This object holds less data than the RecipeIngredient Class.
 */
public class RecipeStepIngredient {

    public int id; // The ID of the ingredient.
    public String name; // The name of the ingredient.
    public String imageURL; // The URL of the ingredientName of the ingredient.

    // ------ Constructor ------
    public RecipeStepIngredient(JSONObject jsonIngredient) throws JSONException {
        this.id = jsonIngredient.optInt("id",-1);
        this.name = jsonIngredient.optString("name","");
        this.imageURL = jsonIngredient.optString("image","");
    }
}
