package com.example.meallab.storing_data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.meallab.Nutrients.Nutrient;
import com.example.meallab.Spoonacular.Recipe;
import com.example.meallab.Spoonacular.SpoonacularDiet;
import com.example.meallab.Spoonacular.SpoonacularIntolerance;
import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class acts as a facade for sharedPreferences.
 */
public class UserPreferences {

    // Constant keys
    private static final String C_PREFERENCES     = "MY_PREF";
    // ---
    private static final String C_MEALS_DAY       = "_meals_day";
    private static final String C_DIETS           = "diets";
    private static final String C_INTOLERANCES    = "intolerances";
    private static final String C_NUTRIENTS       = "nutrients";
    private static final String C_SHOPPING        = "shopping_list_day_";
    private static final String C_FIRST_TIME      = "shopping_list_day_";

    private static final String C_MACRO_FILENAME      = "Default_Macronutrients.json";
    private static final String C_MICRO_FILENAME      = "Default_Micronutrients.json";
    private static final String C_ADDITIONAL_FILENAME = "Default_Additionalnutrients.json";

    private Context context;
    private SharedPreferences pref;
    private Gson gson = new Gson();

    public UserPreferences(Context c) {
        this.context = c;
        this.pref = this.context.getSharedPreferences(C_PREFERENCES, Context.MODE_PRIVATE);
    }

    // Getters

    /**
     * Get the users current diets.
     * @return The current diets the user follows.
     */
    public SpoonacularDiet[] getDiets() {
        // Get the JSON
        String result = this.pref.getString(C_DIETS,"{}");
        // Convert to objects.
        SpoonacularDiet[] d = this.gson.fromJson(result, SpoonacularDiet[].class);

        return d;
    }
    /**
     * Get the users current intolerances.
     * @return The current intolerances the user has.
     */
    public SpoonacularIntolerance[] getIntolerances() {
        // Get the JSON
        String result = this.pref.getString(C_INTOLERANCES,"{}");
        // Convert to objects.
        return this.gson.fromJson(result, SpoonacularIntolerance[].class);
    }
    /**
     * Get the nutrients the user tracks.
     * @return The current nutrients the user tracks.
     */
    public Nutrient[] getTrackedNutrients() {

        // Get the JSON
        String result = this.pref.getString(C_NUTRIENTS,"[]");
        // Convert to objects.
        Nutrient[] n = this.gson.fromJson(result, Nutrient[].class);

        // If it does NOT contain the 4 macros we must add them.
        if (n.length == 0) {

            // Get the default macros
            Nutrient[] macros = this.getDefaultMacroNutrients();
            // Get the default micros but only take 5.
            Nutrient[] micros = this.getDefaultMicroNutrients();//Arrays.copyOf(this.getDefaultMicroNutrients(),5);
            // Concat the macros and micros.
            Nutrient[] total = Arrays.copyOf(macros, macros.length + micros.length);
            System.arraycopy(micros, 0, total, macros.length, micros.length);

            n = total;
        }
        return n;
    }
    /**
     * Get the meals the user wants to eat every day.
     * @return The meals the user wants to eat.
     */
    public SpoonacularMealType[] getMealsPerDay() {
        // The default meals are breakfast lunch and dinner.
        String defaultMeals = "[BREAKFAST,LUNCH,DINNER]";
        String json = this.pref.getString(C_MEALS_DAY, defaultMeals);
        return this.gson.fromJson(json, SpoonacularMealType[].class);
    }
    /**
     * Get the full list of macronutrients that can be tracked.
     * @return Array of macronutrients that can be tracked.
     */
    public Nutrient[] getDefaultMacroNutrients() {
        return readDefaultFile(C_MACRO_FILENAME);
    }
    /**
     * Get the full list of micronutrients that can be tracked.
     * @return Array of micronutrients that can be tracked.
     */
    public Nutrient[] getDefaultMicroNutrients() {
        return readDefaultFile(C_MICRO_FILENAME);
    }
    /**
     * Get the full list of additional nutrients that can be tracked.
     * @return Array of additional nutrients that can be tracked.
     */
    public Nutrient[] getDefaultAdditionalNutrients() {
        return readDefaultFile(C_ADDITIONAL_FILENAME);
    }

    /**
     * Gets the structure
     * @return The structure of the shopping list day selection.
     */
    public boolean[] getShoppingListSelectionStructure() {
        // 7 Days.
        boolean[] struct = new boolean[7];

        for (int i = 0; i < struct.length; i++) {

            // Default is 5 selected 2 not.
            struct[i] = this.pref.getBoolean(C_SHOPPING + i, (i < 4));
        }
        return struct;
    }

    /**
     * Get the first time key.
     * @return True if it is the first time, false otherwise.
     */
    public boolean getIsFirstTime() {
        return this.pref.getBoolean(C_FIRST_TIME, true);
    }

    /**
     * Set the first time key.
     * @param isFirstTime True if it is the first time, false otherwise.
     */
    public void setIsFirstTime(boolean isFirstTime) {
        SharedPreferences.Editor e = this.pref.edit();
        e.putBoolean(C_FIRST_TIME, isFirstTime);
        e.apply();
    }
    /**
     * Sets the structure.
     * @param structure The structure of the shopping list day selection.
     */
    public void setShoppingListSelectionStructure(boolean[] structure) {
        SharedPreferences.Editor e = this.pref.edit();

        // Save the array
        for (int i = 0; i < structure.length; i++) {
            e.putBoolean(C_SHOPPING + i, structure[i]);
        }
        e.apply();
    }
    // Setters

    public void setDiets(SpoonacularDiet[] d) {
        // Create the JSON
        String json = this.gson.toJson(d);

        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString(C_DIETS, json);
        editor.apply();
    }
    public void setIntolerances(SpoonacularIntolerance[] i) {
        // Create the JSON
        String json = this.gson.toJson(i);

        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString(C_INTOLERANCES, json);
        editor.apply();
    }
    public void setTrackedNutrients(Nutrient[] n) {
        // Create the JSON
        String json = this.gson.toJson(n);

        SharedPreferences.Editor editor = this.pref.edit();
        editor.putString(C_NUTRIENTS, json);
        editor.apply();
    }
    public void setMealsPerDay(SpoonacularMealType[] meals) {
        SharedPreferences.Editor editor = this.pref.edit();

        String json = gson.toJson(meals);
        editor.putString(json, C_MEALS_DAY);
        editor.apply();
    }

    // ---- Private Methods ----

    private Nutrient[] readDefaultFile(String filename) {
        StringBuilder sb = new StringBuilder();

        try {
            InputStream is = this.context.getAssets().open(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("USERPrefs IOEXecption critical");
        }
        Nutrient[] macros = this.gson.fromJson(sb.toString(), Nutrient[].class);

        return macros;
    }
}
