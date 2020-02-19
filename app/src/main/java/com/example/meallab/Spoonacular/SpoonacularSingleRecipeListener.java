package com.example.meallab.Spoonacular;

public interface SpoonacularSingleRecipeListener {
    /**
     * Gets called when the API has retrieved additional information for the recipe.
     * @param recipe The recipe for which additional information is received and set.
     */
    void retrievedAdditionalInformation(Recipe recipe);

    /**
     * Gets called when the API encounters an error.
     */
    void singleRecipeErrorHandler();
}
