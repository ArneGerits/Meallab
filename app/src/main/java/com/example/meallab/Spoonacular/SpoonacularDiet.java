package com.example.meallab.Spoonacular;

/**
 * This enum is used to specify a diet available in the Spoonacular API.
 */
public enum SpoonacularDiet {

    // Every dietary option available in Spoonacular.
    GLUTEN_FREE("gluten free"),
    KETOGENIC("ketogenic"),
    VEGETARIAN("vegetarian"),
    LACTO_VEGETARIAN("lacto-vegetarian"),
    OVO_VEGETERIAN("ovo-vegetarian"),
    VEGAN("vegan"),
    PESCETARIAN("pescetarian"),
    PALEO("paleo"),
    PRIMAL("primal"),
    WHOLE30("whole30");

    // The actual string value of the enum
    private String value;

    // Getter method for value
    public String getValue()
    {
        return this.value;
    }

    // The constructor takes a string argument.
    private SpoonacularDiet(String value)
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
