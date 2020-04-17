package com.example.meallab.Spoonacular;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.meallab.activities.RecipeOverviewActivity;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class is used for all Spoonacular related API calls and communication.
 *
 * @author Ewout
 * @version 1.0
 */
public class SpoonacularAPI {

    // API key used for authentication.
    //private final String API_KEY = "17ec74bd49124b45960ffb5d3b2b0b93"; // MAIN KEY
    private final String API_KEY = "45c0adb7abff40c69e59feb277e379aa"; // BACKUP KEY
    // The base URL for searching recipes.
    private final String SEARCH_BASE_URL = "https://api.spoonacular.com/recipes/complexSearch";

    // The front of the base url for getting the info of a recipe.
    private final String RECIPE_BASE_URL_FRONT = "https://api.spoonacular.com/recipes/";
    // The back of the base url for the getting the info of a recipe.
    private final String RECIPE_BASE_URL_BACK = "/information?includeNutrition=true";

    private final String RECIPE_INFO_BASE_URL = "https://api.spoonacular.com/recipes/";

    private final String RECIPE_INFO_BULK_BASE_URL = "https://api.spoonacular.com/recipes/informationBulk?ids=";

    // A comma separated string of diets.
    private String dietString = "";
    // A comma separated string of intelorances.
    private String intoleranceString = "";

    // The activity context.
    private Context context;

    private RequestQueue mainQueue;

    // ------ Constructor ------

    public SpoonacularAPI(Context ctx) {

        context = ctx;
        // Gets the app wide request queue.
        this.mainQueue = VolleySingleton.getInstance(this.context).getRequestQueue();
    }

    // ------ Public methods ------

    /**
     * Updates the diets that will be used in api communication.
     *
     * @param diets An array of diets to adhere to.
     */
    public void setDiets(SpoonacularDiet[] diets) {

        // Make sure the diet string is empty.
        dietString = "";

        // Append the diets.
        for (SpoonacularDiet d : diets) {
            dietString = dietString + d.getValue() + ",";
        }

        if (dietString.length() > 1) {
            // Remove the last comma.
            dietString = dietString.substring(0, dietString.length() - 1);
        }
    }

    /**
     * Updates the diets that will be used in api communication.
     *
     * @param intolerances An array of tolerances to adhere to.
     */
    public void setIntolerances(SpoonacularIntolerance[] intolerances) {

        // Make sure the diet string is empty.
        intoleranceString = "";

        // Append the diets.
        for (SpoonacularIntolerance i : intolerances) {
            intoleranceString = intoleranceString + i.getValue() + ",";
        }

        if (intoleranceString.length() > 1) {
            // Remove the last comma.
            intoleranceString = intoleranceString.substring(0, intoleranceString.length() - 1);
        }
    }

    /**
     * Retrieves recipes that fill the request.
     *
     * @param request
     * @param listener
     */
    public void retrieveRecipes(final RecipeRequest request, final SpoonacularSimpleRecipeListener listener) {

        // Obtain the API url.
        final String url = searchURL(request);

        System.out.println("URL END POINT:" + url);
        // Make the request.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // The top level json object holds an array of json objects, accessed by 'results'
                    JSONArray responseJSONArray = response.getJSONArray("results");

                    ArrayList<Recipe> recipes = new ArrayList<Recipe>();

                    for (int i = 0; i < responseJSONArray.length(); i++) {
                        // Get the JSON object representing the recipe.
                        JSONObject jsonRecipe = responseJSONArray.getJSONObject(i);
                        // Create a recipe object and add it.
                        recipes.add(new Recipe(jsonRecipe, request.type));
                    }

                    Recipe[] r = new Recipe[recipes.size()];
                    recipes.toArray(r);
                    listener.retrievedRecipes(r);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("An error occurred " + error.toString());
            }
        });
        this.mainQueue.add(jsonObjectRequest);
    }

    /**
     * Retrieves additional information for a recipe:
     * 1. An extended list of ingredients.
     * 2. Additional nutritional data.
     * @param recipeIDs The ids of the recipes to retrieve information from.
     * @param rTypes The types of recipes
     */
    public void retrieveRecipeDetailedInfo(final int[] recipeIDs, final SpoonacularMealType[] rTypes, final SpoonacularDetailedRecipeListener listener) {

        // Obtain the API url.
        final String url = recipeBulkInfoURL(recipeIDs);
        // Make the request.
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    Recipe[] recipes = new Recipe[response.length()];

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject recipe = response.getJSONObject(i);
                        recipes[i] = new Recipe(recipe, rTypes[i]);
                        recipes[i].hasLoadedFully = true;
                    }
                    listener.retrievedAdditionalInformation(recipes);
                } catch (JSONException e) {
                    listener.complexSpoonacularErrorHandler();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("An error occurred" + error.toString());
            }
        });
        this.mainQueue.add(jsonObjectRequest);
    }

    // ------- Private Methods -------

    private String recipeInfoURL(String recipeID) {
        return RECIPE_INFO_BASE_URL + recipeID + "/information?includeNutrition=true&apiKey=" + API_KEY;
    }
    private String recipeBulkInfoURL(int[] recipeIDs) {

        StringBuilder ids = new StringBuilder();
        for (int id : recipeIDs) {
            ids.append(id).append(",");
        }
        // Trim the last comma
        String val = ids.substring(0,ids.length() - 1);

        return RECIPE_INFO_BULK_BASE_URL + val + "&includeNutrition=true&apiKey=" + API_KEY;

    }

    // Constructs the search url for given recipe request.
    private String searchURL(RecipeRequest request) {

        return SEARCH_BASE_URL + "?apiKey=" + API_KEY + "&addRecipeInformation=true" + request.toString();
    }

    public void retrieveAdditionalRecipeInformation(Recipe recipe, RecipeOverviewActivity recipeOverviewActivity) {
    }

    // -------- INTERFACES --------

    public interface SpoonacularSimpleRecipeListener {
        /**
         * Gets called when the API has retrieved recipes.
         *
         * @param recipes The recipes retrieved.
         */
        void retrievedRecipes(Recipe[] recipes);

        /**
         * Gets called when the API encounters an error.
         */
        void simpleSpoonacularErrorHandler();
    }

    public interface SpoonacularDetailedRecipeListener {
        /**
         * Gets called when the API has retrieved additional information for the recipe.
         * @param recipe The recipe for which additional information is received and set.
         */
        void retrievedAdditionalInformation(Recipe[] recipe);

        /**
         * Gets called when the API encounters an error.
         */
        void complexSpoonacularErrorHandler();
    }
}
