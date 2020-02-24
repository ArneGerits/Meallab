package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_overview);

        createTestRecipe();
    }

    public void createTestRecipe() {
        /**
         *         this.prepMinutes    = json.optInt("preparationMinutes",-1);
         *         this.cookingMinutes = json.optInt("cookingMinutes",-1);
         *         this.readyInMinutes = json.optInt("readyInMinutes",-1);
         *
         *         this.id       = json.optInt("id",-1);
         *         this.servings = json.optInt("servings",-1);
         *
         *         this.imageName = json.optString("image","");
         *         this.imageType = json.optString("imageType","");
         *         this.sourceURL = json.optString("sourceUrl","");
         *         this.title     = json.optString("title","");
         *
         *         this.pricePerServing = (float) Double.valueOf(json.optString("pricePerServing","-1")).doubleValue();
         *         this.popular = json.optBoolean("veryPopular",false);
         *
         *         JSONArray nutrition = json.optJSONArray("nutrition");
         *         this.storeNutrition(nutrition);
         *
         *         JSONArray instructions = json.optJSONArray("analyzedInstructions");
         *         this.storeInstructions(instructions);
         */
        /*
        try{
            JSONObject recipe = new JSONObject();
            recipe.put("preparationMinutes",1);
            recipe.put("cookingMinutes",6);
            recipe.put("readyInMinutes",7);
            recipe.put("id",9001);
            recipe.put("servings",1);
            recipe.put("image","egg");
            recipe.put("imageType","jpg");
            recipe.put("sourceURL","https://upload.wikimedia.org/wikipedia/commons/0/0e/Egg.jpg");
            recipe.put("title","Soft boiled egg");
            recipe.put("pricePerServing", "0.50");
            recipe.put("VeryPopular",true);


            JSONArray nutrition = new JSONArray();
            JSONObject calories =  new JSONObject();
            JSONObject carbs =  new JSONObject();
            JSONObject protien =  new JSONObject();
            JSONObject fats =  new JSONObject();
            calories.put("title","Calories");
            calories.put("amount","155");
            carbs.put("title","Carbohydrates");
            carbs.put("amount", "1.1");
            protien.put("title","Protien");
            protien.put("amount","13");
            fats.put("title","Fat");
            nutrition.put(fats);
            nutrition.put(protien);

            fats.put("amount", "11");
            nutrition.put(calories);
            nutrition.put(carbs);
            recipe.put("nutrition",nutrition);

            /**
             * public RecipeStep(JSONObject jsonStep) throws JSONException {
             *         this.stepNumber        = jsonStep.optInt("number",-1);
             *         this.instructionString = jsonStep.optString("step","");
             *
             *         JSONArray ingredients = jsonStep.optJSONArray("ingredients");
             *         JSONArray equipment   = jsonStep.optJSONArray("equipment");
             */

            // maak een json array met steps aan

            /*//step 1
            JSONArray instructions = new JSONArray();
            JSONObject stepOne = new JSONObject();
            stepOne.put("number",1);
            stepOne.put("step","Fill a pan with a liter of water, and bring it to a boil");
            JSONArray stepOneIngredients1;


        } catch (JSONException e){

        }
*/

    }
}
