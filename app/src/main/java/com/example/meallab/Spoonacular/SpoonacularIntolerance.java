package com.example.meallab.Spoonacular;

/**
 * This enum is used to specify intelorance available in the Spoonacular API.
 */
public enum SpoonacularIntolerance {

    // Every intolerance option available in Spoonacular.
    DAIRY("diary"),
    EGG("egg"),
    GLUTEN("gluten"),
    GRAIN("grain"),
    PEANUT("peanut"),
    SEAFOOD("seafood"),
    SESAME("sesame"),
    SHELLFISH("shellfish"),
    SOY("soy"),
    SULFITE("sulfite"),
    TREE_NUT("tree nut"),
    WHEAT("wheat");

    // The actual string value of the enum
    private String value;

    // Getter method for value
    public String getValue()
    {
        return this.value;
    }

    // The constructor takes a string argument.
    private SpoonacularIntolerance(String value)
    {
        this.value = value;
    }
}
