package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.Button;

import static com.example.meallab.InitialStartupActivity.mypreference;


public class SecondActivity extends AppCompatActivity implements View.OnClickListener {


    public static String firstTimeKey = "firstTimeKey";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        System.out.println("started second activity");
        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Button button = (Button) SecondActivity.this.findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(firstTimeKey,true);
                editor.commit();
                System.out.println("clicked in second activity");


        }
    }
}