package com.example.meallab.Spoonacular;

import android.graphics.Bitmap;

import com.example.meallab.Nutrients.Nutrient;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

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

    public String imageName; // The name of the recipe ingredientName, used to compute the URL.
    public String imageType; // The type of the ingredientName.

    // Base url of all recipe images.
    private final String IMAGE_BASE_URL = "https://spoonacular.com/recipeImages/";

    // ------ Timing data ------

    public int prepMinutes; // The amount of minutes needed to prepare this recipe.
    public int cookingMinutes; // The amount of minutes needed to cook this recipe.
    public int readyInMinutes; // The amount of time it takes ready the whole dish (prep + cooking)

    // ------ Nutritional data ------

    // List of all macro + micro nutrients, the first 4 will be:
    // Calories, Carbohydrates, Fats, Proteins.
    public Nutrient[] nutrients;

    // ------- Instructions -------

    public RecipeStep[] instructions = new RecipeStep[0]; // The instruction steps to cook this recipe.

    // ------- Ingredients -------

    public RecipeIngredient[] ingredients = new RecipeIngredient[0]; // The ingredients needed to cook this recipe.

    // ------ Hidden Data ------

    public String sourceURL; // The source url of this recipe.
    public int id; // The ID of this recipe.
    public SpoonacularMealType type; // The type of this recipe.
    private RecipeCost cost = RecipeCost.UNKNOWN; // The cost of this recipe.

    // ------ Constructor ------

    /**
     * Creates a new recipe object with given json object and type, note: does not initialize
     * variable: ingredients
     *
     * @param json The top level json object
     * @param type The type of recipe this is
     */
    public Recipe(JSONObject json, SpoonacularMealType type) throws JSONException{
        this.type = type;

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
        this.cost = this.computeCost(this.pricePerServing,this.type);

        this.popular = json.optBoolean("veryPopular",false);


        // Setting the nutritional properties.

        // Nutrients can be nested in 2 ways.
        JSONArray nutrition = json.optJSONArray("nutrition");
        if (nutrition == null) {
            nutrition = json.getJSONObject("nutrition").getJSONArray("nutrients");
        }
        this.setNutrients(nutrition);

        JSONArray instructions = json.optJSONArray("analyzedInstructions");
        this.storeInstructions(instructions);

        JSONArray ingredients = json.optJSONArray("extendedIngredients");
        if (ingredients != null) {
            this.addIngredientInfo(ingredients);
        }
    }
    // ------ Private Methods ------

    // Computes the cost of a recipe based on the price per serving and meal type.
    private RecipeCost computeCost(float pricePerServing, SpoonacularMealType type) {

        // If the price per serving is unknown the cost is unknown.
        if (pricePerServing == -1) {
            return RecipeCost.UNKNOWN;
        }

        // Breakfast prices
        float LOW    = 200;
        float MEDIUM = 400;

        if (this.type == SpoonacularMealType.LUNCH) {
            // Lunch prices
            LOW = 300;
            MEDIUM = 500;
        }
        if (this.type == SpoonacularMealType.DINNER) {
            // Dinner prices
            LOW = 400;
            MEDIUM = 600;
        }


        if (pricePerServing < LOW) {
            return RecipeCost.LOW;
        } else if (pricePerServing < MEDIUM) {
            return RecipeCost.MEDIUM;
        } else {
            return RecipeCost.HIGH;
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
    public void setNutrients(JSONArray nutrients) {
        try {
            // Input can be null.
            if (nutrients == null) {
                return;
            }
            ArrayList<Nutrient> nuts = new ArrayList<>(nutrients.length());

            for (int i = 0; i < nutrients.length(); i++) {
                // Get the JSON object representing the recipe.
                JSONObject val = nutrients.getJSONObject(i);

                float value = (float) Double.valueOf(val.optString("amount", "-1")).doubleValue();
                String unit = val.optString("unit","");
                String title = val.optString("title","");

                // Create a new nutrient.
                Nutrient n = new Nutrient();
                n.name = title;
                n.amount = value;
                n.unit = unit;

                nuts.add(i,n);
            }
            // Perform reordering swaps.
            for (int i = 0; i < nuts.size(); i++) {
                Nutrient n = nuts.get(i);
                if (n.name.equals("Calories")) {
                    Collections.swap(nuts,0,i);
                } else if(n.name.equals("Carbohydrates")) {
                    Collections.swap(nuts,1,i);
                } else if(n.name.equals("Fat")) {
                    Collections.swap(nuts,2,i);
                } else if(n.name.equals("Protein")) {
                    Collections.swap(nuts,3,i);
                }
            }
            // Convert arraylist to array.
            this.nutrients = new Nutrient[nuts.size()];
            this.nutrients = nuts.toArray(this.nutrients);

            System.out.println("Recipe nutrients added:");
            for (Nutrient n : this.nutrients) {
                System.out.println("nutreint: " + n.name);
            }
        } catch (JSONException e) {

        }
    }
    public String getImageURLForSize(SpoonacularImageSize size) {
        return IMAGE_BASE_URL + this.id + "-" + size.getValue() + "." + this.imageType;
    }

    public RecipeCost getCost() {
        return cost;
    }

    public RecipeEquipment[] getAllEquipment() {
        ArrayList<RecipeEquipment> all = new ArrayList<>();

        for (RecipeStep step : this.instructions) {
            for (RecipeEquipment eq : step.equipment) {
                if (!all.contains(eq)) {
                    all.add(eq);
                }
            }
        }
        RecipeEquipment[] eqNeeded = new RecipeEquipment[all.size()];
        all.toArray(eqNeeded);

        return eqNeeded;
    }
}