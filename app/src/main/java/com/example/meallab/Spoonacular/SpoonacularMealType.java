package com.example.meallab.Spoonacular;

/**
 * This enum is used to specify the type of meal to obtain recipes for.
 */
public enum SpoonacularMealType {

    // We are only interested in breakfast, lunch and dinner
    BREAKFAST("breakfast"), // This is used to obtain 'breakfast' recipes
    BRUNCH_SNACK("snack, fingerfood"), // Snack between breakfast and lunch.
    LUNCH("soup, bread, salad"), // This is used to obtain 'lunch' recipes
    APPETIZER_SNACK("appetizer, side dish"), // Snack between lunch and dinner
    DINNER("main course"), // This is used to obtain 'dinner' recipes
    DESSERT("dessert"); // Used to obtain dessert recipes.

    // The actual string value of the enum
    private String value;

    // Getter method for value
    public String getValue()
    {
        return this.value;
    }

    // The constructor takes a string argument.
    private SpoonacularMealType(String value)
    {
        this.value = value;
    }
}