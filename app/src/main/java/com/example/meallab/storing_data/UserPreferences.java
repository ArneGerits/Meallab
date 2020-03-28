package com.example.meallab.storing_data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.meallab.Nutrients.Nutrient;
import com.example.meallab.Spoonacular.SpoonacularDiet;
import com.example.meallab.Spoonacular.SpoonacularIntolerance;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

    private static final String C_MACRO_FILENAME      = "Default_Macronutrients.json";
    private static final String C_MICRO_FILENAME      = "Default_Micronutrients.json";
    private static final String C_ADDITIONAL_FILENAME = "Default_Additionalnutrients.json";

    private Context context;
    private SharedPreferences pref;
    private Gson gson = new Gson();
    /*private static final String C_INVALIDATE_CACHE = "cache";

    // Caching vars
    Nutrient[] trackedNutrientsCache;
    */

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
        SpoonacularIntolerance[] i = this.gson.fromJson(result, SpoonacularIntolerance[].class);

        return i;
    }
    /**
     * Get the nutrients the user tracks.
     * @return The current nutrients the user tracks.
     */
    public Nutrient[] getTrackedNutrients() {

        final long startTime = System.currentTimeMillis();

        /*
        // Return the cache.
        if (this.trackedNutrientsCache != null) {
            return this.trackedNutrientsCache;
        }*/

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
            // 2000kcal is default for an adult man.
            System.out.println("macros" + macros);
            System.out.println("micros: " + micros);
            // Concat the macros and micros.
            Nutrient[] total = Arrays.copyOf(macros, macros.length + micros.length);
            System.arraycopy(micros, 0, total, macros.length, micros.length);

            n = total;
        }
        /*
        // Cache the results.
        this.trackedNutrientsCache = n;

         */
        final long endTime = System.currentTimeMillis();

        System.out.println("get tracked nutrients load time(ms): " + (endTime - startTime));

        return n;
    }
    /**
     * Get the number of meals the user wants to eat per day.
     * @return Number of meals user wants to eat per day.
     */
    public int getMealsPerDay() {
        return this.pref.getInt(C_NUM_MEALS_DAY,3);
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

        // Cache.
        //this.trackedNutrientsCache = n;

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

    /*
    public void invalidateCache() {
        SharedPreferences.Editor editor = this.pref.edit();
        editor.putBoolean(C_INVALIDATE_CACHE, true);
        editor.apply();
    }*/
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
            System.out.println("IOEXecption critical");
        }
        Nutrient[] macros = this.gson.fromJson(sb.toString(), Nutrient[].class);

        return macros;
    }
}
