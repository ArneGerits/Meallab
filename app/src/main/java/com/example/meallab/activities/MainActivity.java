package com.example.meallab.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;

import java.util.ArrayList;
import android.content.Intent;

import com.example.meallab.R;
import com.example.meallab.activities.InitialStartupActivity;
import com.example.meallab.activities.RecipeSelectionActivity;
import com.example.meallab.activities.SecondActivity;

import org.threeten.bp.DayOfWeek;


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

        Intent intent = new Intent(this, DayOverviewActivity.class);

        startActivity(intent);

    }

    private void goToActivityUnderTest() {

        /*
        Intent intent = new Intent(this, RecipeSelectionActivity.class);
        intent.putExtra("mealType", SpoonacularMealType.BREAKFAST.toString());
        startActivity(intent);*/

       // Intent intent = new Intent(this, DayOverviewActivity.class);
        //Intent intent = new Intent(this, RecipeSelectionActivity.class);
        //startActivity(intent);
        //Intent intent = new Intent(this, DayOverviewActivity.class);
        Intent intent = new Intent(this, InitialStartupActivity.class);
        startActivity(intent);
    }
}
