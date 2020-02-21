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

    /**
     * Returns a value for this enum that is readable by the user.
     * @return A string value for this enum that is readable by the user.
     */
    public String readableValue() {
        String s = this.getValue();

        // These 2 lines make the first character upper case.
        String first = s.substring(0,1).toUpperCase();
        return first + s.substring(1,s.length() - 1);
    }
}
