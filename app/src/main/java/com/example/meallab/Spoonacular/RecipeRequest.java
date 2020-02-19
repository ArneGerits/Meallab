package com.example.meallab.Spoonacular;

import androidx.annotation.NonNull;

/**
 * A recipe request object represents a single request to search for a recipe that are bound to the
 * set properties.
 */
public class RecipeRequest {

    // Nutritional params.

    public int minCals = 0;
    public int maxCals = Integer.MAX_VALUE;

    public int minCarbs = 0;
    public int maxCarbs = Integer.MAX_VALUE;

    public int minFat = 0;
    public int maxFat = Integer.MAX_VALUE;

    public int minProtein = 0;
    public int maxProtein = Integer.MAX_VALUE;

    public int offset = 0; // Which page of recipes to receive.
    public SpoonacularMealType type; // The type of recipes to receive.
    public int amount = 9; // Amount of recipes to receive.

    // ------ Constructor ------

    // Creates a new recipe request with given type.
    public RecipeRequest(SpoonacularMealType type) {
        this.type = type;
    }
    // Cannot retrieve more that 9 at a time.
    public void setAmount(int amount) {
        amount = Math.min(9,amount);
    }

    @NonNull
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("&minCalories=" + minCals);
        builder.append("&maxCalories=" + maxCals);
        builder.append("&minCarbs=" + minCarbs);
        builder.append("&maxCarbs=" + maxCarbs);
        builder.append("&minFat=" + minFat);
        builder.append("&maxFat=" + maxFat);
        builder.append("&minProtein=" + minProtein);
        builder.append("&maxProtein=" + maxProtein);
        builder.append("&number=" + amount);
        builder.append("&offset=" + offset);
        builder.append("&type=" + type.getValue());

        return builder.toString();
    }
}
