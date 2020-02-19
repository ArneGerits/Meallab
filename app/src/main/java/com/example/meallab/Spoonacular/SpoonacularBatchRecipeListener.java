package com.example.meallab.Spoonacular;

public interface SpoonacularBatchRecipeListener {
    /**
     * Gets called when the API has retrieved recipes.
     * @param recipes The recipes retrieved.
     */
    void retrievedRecipes(Recipe[] recipes);

    /**
     * Gets called when the API encounters an error.
     */
    void batchRecipesErrorHandler();
}
