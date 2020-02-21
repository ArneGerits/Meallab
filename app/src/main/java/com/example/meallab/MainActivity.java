package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;

import java.util.ArrayList;
import android.content.Intent;

import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.example.meallab.Spoonacular.VolleySingleton;

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


        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, RecipeSelectionActivity.class);
        intent.putExtra("mealType", SpoonacularMealType.BREAKFAST.toString());
        startActivity(intent);

        /*
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        firstTime = sharedPreferences.getBoolean(firstTimeKey, true);
        if (firstTime) {
            goToInitialStartupActivity();
        } else {
            goToSecondActivity();
        }*/

    }

    private void goToInitialStartupActivity() {

        Intent intent = new Intent(this, InitialStartupActivity.class);

        startActivity(intent);

    }

    private void goToSecondActivity() {

    }
}
