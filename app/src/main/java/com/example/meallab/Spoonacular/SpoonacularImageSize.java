package com.example.meallab.Spoonacular;

public enum SpoonacularImageSize {
    S_90x90("90x90"),
    S_240x150("240x150"),
    S_312x150("312x150"),
    S_312x231("312x231"),
    S_480x360("480x360"),
    S_556x370("556x370"),
    S_636x393("636x393");

    // The actual string value of the enum
    private String value;

    // Getter method for value
    public String getValue()
    {
        return this.value;
    }

    // The constructor takes a string argument.
    private SpoonacularImageSize(String value)
    {
        this.value = value;
    }
}
