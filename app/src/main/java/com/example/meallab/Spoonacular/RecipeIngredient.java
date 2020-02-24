package com.example.meallab.Spoonacular;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Represents an ingredient needed by a Recipe.
 */
public class RecipeIngredient {

    // ------ Titular Data ------

    public String name; // The name of the ingredient;
    public String originalName; // The original name of the ingredient used by the recipe author.
    public String consistency; // The consistency of the ingredient.
    public String[] metaInformation; // Meta information about the ingredient such as: fresh,chopped

    // ------ Amount Data ------

    public float amount; // The amount needed.
    public String unitShort; // The short representation of the unit of the amount.
    public String unitLong; // The long representation of the unit of the amount.

    // ------ Hidden Data ------

    public int id; // The ID of the ingredient.
    public String imageURL; // The ingredientName URL of the ingredient.

    // ------ Constructor ------

    public RecipeIngredient(){};
    public RecipeIngredient(JSONObject input) {
        this.id = input.optInt("id",-1);
        this.consistency = input.optString("consistency","");
        this.name = input.optString("name","");
        this.imageURL = input.optString("image","");

        this.originalName = input.optString("original","");

        // Get meta information
        JSONArray meta = input.optJSONArray("meta");
        if (meta != null) {
            ArrayList <String> m = new ArrayList<>();

            for (int i = 0; i < meta.length(); i++) {
                String info  = meta.optString(i,"");
                m.add(info);
            }
            metaInformation = new String[m.size()];
            m.toArray(metaInformation);
        }
        // Get measurement data

        JSONObject measureMetric = input.optJSONObject("measures").optJSONObject("metric");

        this.unitShort = measureMetric.optString("unitShort","");
        this.unitLong = measureMetric.optString("unitLong","");
        this.amount = (float) Double.valueOf(measureMetric.optString("amount","-1")).doubleValue();
    }
}
