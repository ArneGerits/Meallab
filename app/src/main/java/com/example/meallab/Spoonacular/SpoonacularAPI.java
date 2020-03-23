package com.example.meallab.Spoonacular;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

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
    private final String API_KEY = "17ec74bd49124b45960ffb5d3b2b0b93";

    // The base URL for searching recipes.
    private final String SEARCH_BASE_URL = "https://api.spoonacular.com/recipes/complexSearch";

    // The front of the base url for getting the info of a recipe.
    private final String RECIPE_BASE_URL_FRONT = "https://api.spoonacular.com/recipes/";
    // The back of the base url for the getting the info of a recipe.
    private final String RECIPE_BASE_URL_BACK = "/information?includeNutrition=true";

    private final String RECIPE_INFO_BASE_URL = "https://api.spoonacular.com/recipes/";

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
    public void retrieveRecipes(final RecipeRequest request, final SpoonacularBatchRecipeListener listener) {

        // Obtain the API url.
        final String url = searchURL(request);

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

    // TODO: Allow loading by ID.
    /**
     * Retrieves additional information for a recipe, mainly an extended list of ingredients.
     *
     * @param recipe The recipe to retrieve additional information from.
     */
    public void retrieveAdditionalRecipeInformation(final Recipe recipe, final SpoonacularSingleRecipeListener listener) {

        // Obtain the API url.
        final String url = recipeInfoURL(recipe);
        // Make the request.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                // The top level json object holds an array of json objects, accessed by 'results'
                JSONArray ingredients = response.optJSONArray("extendedIngredients");

                // todo: Better error handling than this.
                if (ingredients == null) {
                    listener.singleRecipeErrorHandler();
                    return;
                }
                recipe.addIngredientInfo(ingredients);

                listener.retrievedAdditionalInformation(recipe);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("An error occurred");
            }
        });
        this.mainQueue.add(jsonObjectRequest);
    }

    // ------- Private Methods -------

    private String recipeInfoURL(Recipe r) {
        return RECIPE_INFO_BASE_URL + r.id + "/information?includeNutrition=false&apiKey=" + API_KEY;
    }

    // Constructs the search url for given recipe request.
    private String searchURL(RecipeRequest request) {

        return SEARCH_BASE_URL + "?apiKey=" + API_KEY + "&addRecipeInformation=true" + request.toString();
    }

    // -------- INTERFACES --------

    public interface SpoonacularBatchRecipeListener {
        /**
         * Gets called when the API has retrieved recipes.
         *
         * @param recipes The recipes retrieved.
         */
        void retrievedRecipes(Recipe[] recipes);

        /**
         * Gets called when the API encounters an error.
         */
        void batchRecipesErrorHandler();
    }

    public interface SpoonacularSingleRecipeListener {
        /**
         * Gets called when the API has retrieved additional information for the recipe.
         * @param recipe The recipe for which additional information is received and set.
         */
        void retrievedAdditionalInformation(Recipe recipe);

        /**
         * Gets called when the API encounters an error.
         */
        void singleRecipeErrorHandler();
    }
}
