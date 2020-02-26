package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.meallab.Spoonacular.*;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class RecipeOverviewActivity extends AppCompatActivity implements SpoonacularBatchRecipeListener,SpoonacularSingleRecipeListener {
    public static final String mypreference = "mypref";
    SharedPreferences sharedPreferences;
    Recipe testRecipe;
    Gson gson;
    RecyclerViewAdapterRecipe adapter;
    ArrayList<Object> mObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_overview);
        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerv_view_recipe_overview);
        view.setFocusableInTouchMode(true);
        view.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        createTestRecipe();
    }

    public void createTestRecipe() {

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("testRecipe", "");
        gson = new Gson();

        if(json.equals("")){
            //retrieve recipe.
            SpoonacularAPI api = new SpoonacularAPI(this);
            RecipeRequest recipeRequest = new RecipeRequest(SpoonacularMealType.SNACK);
            recipeRequest.offset=0;
            api.retrieveRecipes(recipeRequest,this);

            //retrieved recipe


        }else{

            testRecipe = gson.fromJson(json,Recipe.class);
            prepareRecyclerView();
        }

    }

    @Override
    public void retrievedRecipes(Recipe[] recipes) {
        SpoonacularAPI api = new SpoonacularAPI(this);
        api.retrieveAdditionalRecipeInformation(recipes[0],this);

    }

    @Override
    public void batchRecipesErrorHandler() {

    }

    @Override
    public void retrievedAdditionalInformation(Recipe recipe) {
        testRecipe = recipe;
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String json = gson.toJson(testRecipe);
        editor.putString("testRecipe",json);
        editor.commit();
        System.out.println(json);

        testRecipe = gson.fromJson(json,Recipe.class);
        prepareRecyclerView();

    }

    @Override
    public void singleRecipeErrorHandler() {

    }

    private void initRecyclerView(ArrayList<Object> mObjects){
        System.out.println(testRecipe.id+ " DASDASJDASDKASD AHSDASDas fake");
        RecyclerView recyclerViewRecipeOverview = findViewById(R.id.recyclerv_view_recipe_overview);
        adapter = new RecyclerViewAdapterRecipe(this, mObjects);
        recyclerViewRecipeOverview.setAdapter(adapter);
        recyclerViewRecipeOverview.setLayoutManager(new LinearLayoutManager(this));
    }

    private void prepareRecyclerView(){
        mObjects.add(testRecipe.getImageURLForSize(SpoonacularImageSize.S_636x393));
        mObjects.add(testRecipe);
        mObjects.addAll(Arrays.asList(testRecipe.instructions));
        System.out.println("now running initRecyclerView(mObjects); BROLOOO");
        initRecyclerView(mObjects);

    }
}
