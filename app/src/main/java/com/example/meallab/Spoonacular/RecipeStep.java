package com.example.meallab.Spoonacular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Represents a single instruction step in a recipe.
 */
public class RecipeStep {

    public int stepNumber; // The number of this step.
    public String instructionString; // The instructions for this step.
    public RecipeStepIngredient[] ingredients = new RecipeStepIngredient[0]; // The ingredients needed for this step.
    public RecipeEquipment[] equipment = new RecipeEquipment[0]; // The equipment needed for this step.

    // ------ Constructor ------
    public RecipeStep(JSONObject jsonStep) throws JSONException {
        this.stepNumber        = jsonStep.optInt("number",-1);
        this.instructionString = jsonStep.optString("step","");

        JSONArray ingredients = jsonStep.optJSONArray("ingredients");
        JSONArray equipment   = jsonStep.optJSONArray("equipment");

        this.setIngredients(ingredients);
        this.setEquipment(equipment);
    }

    // ------ Private Methods ------

    // Sets the ingredients.
    private void setIngredients(JSONArray input) throws JSONException {

        if (input == null) {
            return;
        }
        ArrayList <RecipeStepIngredient> ingredients = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            JSONObject jsonIngredient = input.getJSONObject(i);

            RecipeStepIngredient in = new RecipeStepIngredient(jsonIngredient);
            ingredients.add(in);
        }
        this.ingredients = new RecipeStepIngredient[ingredients.size()];
        ingredients.toArray(this.ingredients);
    }
    // Sets the equipment.
    private void setEquipment(JSONArray input) throws JSONException {

        if (input == null) {
            return;
        }
        ArrayList <RecipeEquipment> equipment = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            JSONObject jsonEquipment = input.getJSONObject(i);

            RecipeEquipment eq = new RecipeEquipment(jsonEquipment);
            equipment.add(eq);
        }
        this.equipment = new RecipeEquipment[equipment.size()];
        equipment.toArray(this.equipment);
    }
}
