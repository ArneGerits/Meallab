package com.example.meallab.Spoonacular;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a single piece of equipment needed for a recipe(step).
 */
public class RecipeEquipment {

    public int id; // The ID of the equipment.
    public String name; // The name of the equipment.
    public String imageURL; // The recipeName URL of the equipment.

    // ------ Constructor ------
    public RecipeEquipment(JSONObject jsonIngredient) throws JSONException {
        this.id = jsonIngredient.optInt("id",-1);
        this.name = jsonIngredient.optString("name","");
        this.imageURL = jsonIngredient.optString("image","");
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof RecipeEquipment)) {
            RecipeEquipment equipment = (RecipeEquipment) obj;
            return (this.id == equipment.id);

        } else {
            return false;
        }
    }
}
