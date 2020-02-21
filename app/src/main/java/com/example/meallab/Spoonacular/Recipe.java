package com.example.meallab.Spoonacular;

import android.media.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Represents a single recipe, recipes can be created by passing Spoonacular JSON recipe data.
 */
public class Recipe {

    // ------ Titular data ------

    public String title; // The title of this recipe.
    public int servings; // The amount of people this recipe serves.

    // ------ Other data ------
    public boolean popular; // True if this recipe is very popular, false otherwise.
    public float pricePerServing; // The price per serving of this recipe in cents.
    // ------ Image data ------

    public String imageName; // The name of the recipe image, used to compute the URL.
    public String imageType; // The type of the image.
    public Image image; // The image of this recipe.

    // ------ Timing data ------

    public int prepMinutes; // The amount of minutes needed to prepare this recipe.
    public int cookingMinutes; // The amount of minutes needed to cook this recipe.
    public int readyInMinutes; // The amount of time it takes ready the whole dish (prep + cooking)

    // ------ Nutritional data ------

    public float calories = 0.0f; // The amount of calories in this recipe per serving in kCal.
    public float carbs = 0.0f; // The amount of carbohydrates in this recipe per serving in grams.
    public float protein = 0.0f; // The amount of protein in the recipe per serving in grams.
    public float fats = 0.0f; // The amount of fats in this recipe per serving in grams.

    // ------- Instructions -------

    public RecipeStep[] instructions = new RecipeStep[0]; // The instruction steps to cook this recipe.

    // ------- Ingredients -------

    public RecipeIngredient[] ingredients = new RecipeIngredient[0]; // The ingredients needed to cook this recipe.

    // ------ Hidden Data ------

    public String sourceURL; // The source url of this recipe.
    public int id; // The ID of this recipe.
    public SpoonacularMealType type; // The type of this recipe.
    private final String IMAGE_BASE_URL = "https://spoonacular.com/recipeImages/";

    // ------ Constructor ------

    /**
     * Creates a new recipe object with given json object and type, note: does not initialize
     * variable: ingredients
     *
     * @param json The top level json object
     * @param type The type of recipe this is
     */
    public Recipe(JSONObject json, SpoonacularMealType type) throws JSONException {
        this.type = type;


        System.out.println("Recipe for object: " + json.toString());
        // Setting simple recipe properties.

        this.prepMinutes    = json.optInt("preparationMinutes",-1);
        this.cookingMinutes = json.optInt("cookingMinutes",-1);
        this.readyInMinutes = json.optInt("readyInMinutes",-1);

        this.id       = json.optInt("id",-1);
        this.servings = json.optInt("servings",-1);

        this.imageName = json.optString("image","");
        this.imageType = json.optString("imageType","");
        this.sourceURL = json.optString("sourceUrl","");
        this.title     = json.optString("title","");

        this.pricePerServing = (float) Double.valueOf(json.optString("pricePerServing","-1")).doubleValue();
        this.popular = json.optBoolean("veryPopular",false);

        // Setting the nutritional properties.

        JSONArray nutrition = json.optJSONArray("nutrition");
        this.storeNutrition(nutrition);

        JSONArray instructions = json.optJSONArray("analyzedInstructions");
        this.storeInstructions(instructions);
    }
    // ------ Private Methods ------

    // Stores nutrition data
    private void storeNutrition(JSONArray nutrition) throws JSONException {

        // Input can be null.
        if (nutrition == null) {
            return;
        }
        for (int i = 0; i < nutrition.length(); i++) {
            // Get the JSON object representing the recipe.
            JSONObject val = nutrition.getJSONObject(i);

            String title = val.getString("title");
            float value = (float) Double.valueOf(val.optString("amount","-1")).doubleValue();

            if (title.equals("Calories")) {
                this.calories = value;
            } else if (title.equals("Carbohydrates")) {
                this.carbs = value;
            } else if (title.equals("Protein")) {
                this.protein = value;
            } else if (title.equals("Fat")) {
                this.fats = value;
            }
        }

    }

    // Stores instruction data
    private void storeInstructions(JSONArray input) throws JSONException {
        // Input can be null.
        if (input == null) {
            return;
        }
        // There can be an array of instructions
        for (int i = 0; i < input.length(); i++) {
            JSONObject holder = input.getJSONObject(i);

            JSONArray instructions = holder.getJSONArray("steps");

            ArrayList<RecipeStep> steps = new ArrayList<>();

            for (int j = 0; j < instructions.length(); j++) {
                // Get the JSON object representing the recipe.
                JSONObject jsonStep = instructions.getJSONObject(j);

                RecipeStep step = new RecipeStep(jsonStep);
                steps.add(step);
            }
            this.instructions = new RecipeStep[steps.size()];
            steps.toArray(this.instructions);
        }
    }

    public void addIngredientInfo(JSONArray input) {

        try {
            ArrayList <RecipeIngredient> ingredients = new ArrayList<>();

            for (int i = 0; i < input.length(); i++) {
                JSONObject jsonIngredient = input.getJSONObject(i);
                RecipeIngredient ingredient = new RecipeIngredient(jsonIngredient);
                ingredients.add(ingredient);
            }
            this.ingredients = new RecipeIngredient[ingredients.size()];
            ingredients.toArray(this.ingredients);

        } catch (JSONException e) {

        }
    }
    public String getImageURLForSize(SpoonacularImageSize size) {
        return IMAGE_BASE_URL + this.id + "-" + size.getValue() + "." + this.imageType;
    }

    /**
     * Classifies the pricePerServing property into 3.
     * @return 1,2,3 Meaning 1: not expensive, 2 medium expensive, 3 very expensive.
     */
    public int getClassifiedPrice() {

        final float LOW    = 300;
        final float MEDIUM = 500;

        if (pricePerServing < LOW) {
            return 1;
        } else if (pricePerServing < MEDIUM) {
            return 2;
        } else {
            return 3;
        }
    }

}
