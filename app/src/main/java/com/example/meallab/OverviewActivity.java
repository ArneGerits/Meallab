package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.Button;

import static com.example.meallab.InitialStartupActivity.mypreference;


public class OverviewActivity extends AppCompatActivity  {


    public static String firstTimeKey = "firstTimeKey";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        System.out.println("started second activity");
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

    }


}
