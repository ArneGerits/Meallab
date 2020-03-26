package com.example.meallab.storing_data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.meallab.Nutrients.Nutrient;
import com.example.meallab.Spoonacular.SpoonacularDiet;
import com.example.meallab.Spoonacular.SpoonacularIntolerance;
import com.google.gson.Gson;

/**
 * This class acts as a facade for sharedPreferences.
 */
public class UserPreferences {

    // Constant keys
    private static final String C_PREFERENCES   = "MY_PREF";
    // ---
    private static final String C_NUM_MEALS_DAY = "num_meals_day";
    private static final String C_DIETS         = "diets";
    private static final String C_INTOLERANCES  = "intolerances";
    private static final String C_NUTRIENTS     = "nutrients";

    private Context context;
    private SharedPreferences pref;
    private Gson gson = new Gson();

    public UserPreferences(Context c) {
        this.context = c;
        this.pref = this.context.getSharedPreferences(C_PREFERENCES, Context.MODE_PRIVATE);
    }

    // Getters

    public SpoonacularDiet[] getDiets() {
        // Get the JSON
        String result = this.pref.getString(C_DIETS,"{}");
        // Convert to objects.
        SpoonacularDiet[] d = this.gson.fromJson(result, SpoonacularDiet[].class);

        return d;
    }
    public SpoonacularIntolerance[] getIntolerances() {
        // Get the JSON
        String result = this.pref.getString(C_INTOLERANCES,"{}");
        // Convert to objects.
        SpoonacularIntolerance[] i = this.gson.fromJson(result, SpoonacularIntolerance[].class);

        return i;
    }
    public Nutrient[] getTrackedNutrients() {
        // Get the JSON
        String result = this.pref.getString(C_NUTRIENTS,"{}");
        // Convert to objects.
        Nutrient[] n = this.gson.fromJson(result, Nutrient[].class);

        // If it does NOT contain the 4 macros we must add them.
        if (n.length < 4) {
            // 2000kcal is default for an adult man.
            Nutrient calories = new Nutrient("Calories","kcal",0,2000);

            // We go for a 55/30/15 split between Carbs/Fats/Proteins
            Nutrient carbs    = new Nutrient("Fats","g",0,275);
            Nutrient fats     = new Nutrient("Carbohydrates","g",0,75);
            Nutrient proteins = new Nutrient("Proteins","g",0,75);

            n = new Nutrient[] {calories, carbs, fats, proteins};
        }
        return n;
    }
    public int getMealsPerDay() {
        return this.pref.getInt(C_NUM_MEALS_DAY,3);
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
    public void setMealsPerDay(int meals) {
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putInt(C_NUM_MEALS_DAY, meals);
        editor.apply();
    }
}
