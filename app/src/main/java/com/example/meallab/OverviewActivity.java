package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.example.meallab.Spoonacular.RecipeIngredient;
import com.example.meallab.Spoonacular.SpoonacularAPI;

import java.util.ArrayList;

import static com.example.meallab.InitialStartupActivity.mypreference;


public class OverviewActivity extends AppCompatActivity  {


    private ArrayList<String> mIngredientNames = new ArrayList<>();
    private ArrayList<String> mIngredientQuantities = new ArrayList<>();
    RecyclerViewAdapterIngredients adapter;


    public static String firstTimeKey = "firstTimeKey";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        System.out.println("started second activity");
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        testRecycler();


    }

    public void testRecycler(){

        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();

        RecipeIngredient ingredient = new RecipeIngredient();
        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient.name = "Egg";
        ingredient.amount = 3;
        ingredient.unitLong = "";
        ingredient.metaInformation = new String[]{};

        for(int i = 0; i < 10 ; i++){
            ingredients.add(ingredient);
        }
        ingredient2.name="pasta";
        ingredient2.amount = 2;
        ingredient2.unitLong = "cups";
        ingredient2.metaInformation = new String[]{"salted", "boiled", "cooled"};
        ingredients.add(ingredient2);



        initRecyclerView(ingredients);


    }

    private void initRecyclerView(ArrayList<RecipeIngredient> ingredients){

        String name;
        String quantity;

        for(RecipeIngredient r : ingredients){

            name = r.name;
            quantity = r.amount + " " + r.unitLong;
            if(r.metaInformation.length != 0){
                for(int i = 0; i < r.metaInformation.length ; i++){
                    quantity = quantity + ", " + r.metaInformation[i];
                }
            }
            mIngredientNames.add(name);
            mIngredientQuantities.add(quantity);
        }
        RecyclerView recyclerViewShoppingList = findViewById(R.id.recyclerv_view_shopping_list_overview);
        adapter = new RecyclerViewAdapterIngredients(this, mIngredientNames, mIngredientQuantities);
        recyclerViewShoppingList.setAdapter(adapter);
        recyclerViewShoppingList.setLayoutManager(new LinearLayoutManager(this));




    }


}
