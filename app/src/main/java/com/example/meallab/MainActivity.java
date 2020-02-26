package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.content.SharedPreferences;

import java.util.ArrayList;
import android.content.Intent;

import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.example.meallab.Spoonacular.SpoonacularDiet;
import com.example.meallab.Spoonacular.SpoonacularIntolerance;
import com.example.meallab.Spoonacular.SpoonacularMealType;

import static android.view.View.*;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    //vars
    private ArrayList<String> mIntolerances = new ArrayList<>();
    private ArrayList<String> mDiets = new ArrayList<>();
    private TextView mTextView;
    private Button saveAndContinue;
    SharedPreferences sharedPreferences;
    public boolean firstTime;
    public ArrayList<String> allergies = new ArrayList<>();
    public ArrayList<String> diets = new ArrayList<>();
    public static final String mypreference = "mypref";
    public static String firstTimeKey = "firstTimeKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this boolean will cause the activity under test to be launched on startup
        boolean testing = true;

        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        firstTime = sharedPreferences.getBoolean(firstTimeKey, true);
        if(testing){
            goToActivityUnderTest();
        }else{
        if (firstTime) {
            goToInitialStartupActivity();
        } else {
            goToSecondActivity();
        }}

    }

    private void goToInitialStartupActivity() {

        Intent intent = new Intent(this, InitialStartupActivity.class);

        startActivity(intent);

    }

    private void goToSecondActivity() {

        Intent intent = new Intent(this, SecondActivity.class);

        startActivity(intent);

    }

    private void goToActivityUnderTest() {

        /*
        Intent intent = new Intent(this, RecipeSelectionActivity.class);
        intent.putExtra("mealType", SpoonacularMealType.BREAKFAST.toString());
        startActivity(intent);*/

       // Intent intent = new Intent(this, DayOverviewActivity.class);
        Intent intent = new Intent(this,RecipeSelectionActivity.class);
        startActivity(intent);

    }
}
