package com.example.meallab.Spoonacular;

public enum SpoonacularCuisine {
    AMERICAN,
    BRITISH,
    CAJUN,
    CARIBBEAN,
    AFRICAN,
    CHINESE,
    EASTERN_EUROPEAN,
    EUROPEAN,
    FRENCH,
    GERMAN,
    GREEK,
    INDIAN,
    IRISH,
    ITALIAN,
    JAPANESE,
    JEWISH,
    KOREAN,
    LATIN_AMERICAN,
    MEDITERRANEAN,
    MEXICAN,
    MIDDLE_EASTERN,
    NORDIC,
    SOUTHERN,
    SPANISH,
    THAI,
    VIETNAMESE;

    /**
     * Returns the value for this enum that is used by Spoonacular.
     * @return The string value for this enum that is used by Spoonacular.
     */
    public String apiValue() {

        String l = this.toString().toLowerCase();
        return l.replace("_"," ");
    }

    /**
     * Returns a value for this enum that is readable by the user.
     * @return A string value for this enum that is readable by the user.
     */
    public String readableValue() {
        String s = this.apiValue();

        // These 2 lines make the first character upper case.
        String first = s.substring(0,1).toUpperCase();
        return first + s.substring(1,s.length() - 1);
    }
}
