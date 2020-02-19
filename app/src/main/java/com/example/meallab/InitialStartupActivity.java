package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
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

import static android.view.View.*;


public class InitialStartupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "InitialStartupActivity";
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
        setContentView(R.layout.activity_initial_startup);

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        initRecyclerChoices();

        final SeekBar bar = (SeekBar) this.findViewById(R.id.seekBar);
        mTextView = ((TextView) InitialStartupActivity.this.findViewById(R.id.textView));
        saveAndContinue = (Button) InitialStartupActivity.this.findViewById(R.id.SaveAndContinue);
        saveAndContinue.setOnClickListener(this);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = 500 + (progress * 100);
                InitialStartupActivity.this.mTextView.setText(String.format("%d", value));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SaveAndContinue:

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(firstTimeKey,false);
                editor.commit();
                System.out.println("clicked in second activity");
                goToSecondActivity();


        }
    }

    private void initRecyclerChoices() {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");

        for (SpoonacularIntolerance i : SpoonacularIntolerance.values()) {
            mIntolerances.add(i.getValue());
        }

        for (SpoonacularDiet d : SpoonacularDiet.values()) {
            mDiets.add(d.getValue());
        }

        initRecyclerView();

    }

    private void initRecyclerView() {

        Log.d(TAG, "initRecyclerView: init recyvlerview");
        RecyclerView recyclerViewAllergies = findViewById(R.id.recyclerv_view_allergies);
        RecyclerView recyclerViewDiets = findViewById(R.id.recyclerv_view_diets);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mIntolerances);
        RecyclerViewAdapter adapter1 = new RecyclerViewAdapter(this, mDiets);
        recyclerViewAllergies.setAdapter(adapter);
        recyclerViewAllergies.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerViewDiets.setAdapter(adapter1);
        recyclerViewDiets.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }



     

    // ------ Actions ------

    private void goToSecondActivity() {

        Intent intent = new Intent(this, SecondActivity.class);

        startActivity(intent);

    }
}


