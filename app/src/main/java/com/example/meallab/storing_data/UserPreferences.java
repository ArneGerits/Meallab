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
import java.util.Collections;

/**
 * This class acts as a facade for sharedPreferences.
 */
public class UserPreferences {

    // Constant keys
    private static final String C_PREFERENCES   = "MY_PREF";
    // ---
    private static final String C_MEALS_DAY     = "_meals_day";
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
            // Get the default micros but only take 4.
            Nutrient[] micros = this.getDefaultMicroNutrients();
            String[] topFourMicroNutrients = new String[]{"Calcium", "Sodium" , "Vitamin C", "Vitamin K" };
            ArrayList<Nutrient> microsList = new ArrayList<>();
            for(int i = 0; i < micros.length ; i++){
                for(int j = 0; j < topFourMicroNutrients.length; j++){
                    if(micros[i].name.equals(topFourMicroNutrients[j])){
                        micros[i].amount = micros[i].amountDailyTarget;
                        microsList.add(micros[i]);


                    }
                }
            }
            Nutrient[] microsNew = new Nutrient[microsList.size()];
            for(int i = 0; i< microsNew.length; i++){
                microsNew[i] = microsList.get(i);
            }


            for(int i = 0; i< microsNew.length; i++){
                System.out.println("DSDASDASDASDASDASDASDASDASDASDASDADASDASDAS");
                System.out.println(microsNew[i]==null);
            }



            // Concat the macros and micros.
            Nutrient[] total = Arrays.copyOf(macros, macros.length + microsNew.length);
            System.arraycopy(microsNew, 0, total, macros.length, microsNew.length);

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
        editor.putString(C_MEALS_DAY,json);
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
