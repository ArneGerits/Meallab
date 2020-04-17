package com.example.meallab.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.meallab.R;
import com.example.meallab.RecyclerViewAdapterRecipe;
import com.example.meallab.Spoonacular.*;
import com.example.meallab.Spoonacular.SpoonacularAPI.*;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class RecipeOverviewActivity extends AppCompatActivity implements SpoonacularSimpleRecipeListener, SpoonacularDetailedRecipeListener {
    public static final String RECIPE = "recipe";

    public static final String mypreference = "mypref";
    SharedPreferences sharedPreferences;
    Recipe testRecipe;
    Recipe recipe;
    Gson gson = new Gson();
    RecyclerViewAdapterRecipe adapter;
    ArrayList<Object> mObjects = new ArrayList<>();
    private boolean fromDayOverview;
    private SpoonacularMealType mealType;
    private int recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        System.out.println("---------------------------------------------");
        setContentView(R.layout.activity_recipe_overview);
        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerv_view_recipe_overview);
        view.setFocusableInTouchMode(true);
        view.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        this.fromDayOverview = getIntent().getBooleanExtra("from DayOverview",true);
        String strMealType = getIntent().getStringExtra("mealType");
        this.mealType = gson.fromJson(strMealType,SpoonacularMealType.class);
        if(fromDayOverview){
            String strObj = getIntent().getStringExtra("obj");
            this.recipe = gson.fromJson(strObj,Recipe.class);
            setRecipe(this.recipe);
        } else {
            this.recipe = null;
            int id = getIntent().getIntExtra("obj",0);
            this.recipeID = id;
            setRecipe(this.recipe);
        }





    }

    public void createTestRecipe() {

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("testRecipe", "");
        gson = new Gson();

        if(json.equals("")){
            //retrieve recipe.
            SpoonacularAPI api = new SpoonacularAPI(this);
            RecipeRequest recipeRequest = new RecipeRequest(SpoonacularMealType.DINNER);
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
//        api.retrieveAdditionalRecipeInformation(recipes[0],this);

    }

    @Override
    public void simpleSpoonacularErrorHandler() {

    }

    public void batchRecipesErrorHandler() {

    }

    public void retrievedAdditionalInformation(Recipe recipe) {
        System.out.println("retrieved additional");
        this.recipe = recipe;
        prepareRecyclerView();

    }

    public void singleRecipeErrorHandler() {

    }

    private void initRecyclerView(ArrayList<Object> mObjects){
        System.out.println("init recyclerd");
        RecyclerView recyclerViewRecipeOverview = findViewById(R.id.recyclerv_view_recipe_overview);
        adapter = new RecyclerViewAdapterRecipe(this, mObjects);
        recyclerViewRecipeOverview.setAdapter(adapter);
        recyclerViewRecipeOverview.setLayoutManager(new LinearLayoutManager(this));
    }

    private void prepareRecyclerView(){
        mObjects.add(recipe.getImageURLForSize(SpoonacularImageSize.S_636x393));
        mObjects.add(recipe);
        mObjects.addAll(Arrays.asList(recipe.instructions));
        System.out.println("now running initRecyclerView(mObjects); BROLOOO");
        initRecyclerView(mObjects);

    }
    public void setRecipe(Recipe recipe){
        System.out.println("setting recipe");
        if(this.fromDayOverview){
            System.out.println("from dayoverview");
            prepareRecyclerView();
            this.recipe = recipe;
        } else {
            SpoonacularAPI api = new SpoonacularAPI(this);
            // retrieveRecipeDetailedInfo(final int[] recipeIDs, final SpoonacularMealType[] rTypes, final SpoonacularDetailedRecipeListener listener) {
            int[] recipeID = new int[1];
            SpoonacularMealType[] mealtype = new SpoonacularMealType[1];
            recipeID[0] = this.recipeID;
            mealtype[0] = this.mealType;
            api.retrieveRecipeDetailedInfo(recipeID, mealtype, this);
            this.recipe = recipe;
        }

    }

    @Override
    public void retrievedAdditionalInformation(Recipe[] recipe) {
        System.out.println("retrieved additional");
        this.recipe = recipe[0];
        prepareRecyclerView();
    }

    @Override
    public void complexSpoonacularErrorHandler() {

    }
    @Override
    public void onBackPressed(){
        System.out.println("back pressed in recipe overview");
        Intent intent = new Intent();
        intent.putExtra(RECIPE,gson.toJson(this.recipe));
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}
