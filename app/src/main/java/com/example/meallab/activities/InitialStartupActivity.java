package com.example.meallab.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.SharedPreferences;

import java.util.ArrayList;
import android.content.Intent;

import com.example.meallab.Nutrients.Nutrient;
import com.example.meallab.R;
import com.example.meallab.RecyclerViewAdapterToggle;
import com.example.meallab.Spoonacular.SpoonacularDiet;
import com.example.meallab.Spoonacular.SpoonacularIntolerance;
import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.example.meallab.storing_data.UserPreferences;


public class InitialStartupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "InitialStartupActivity";
    private ArrayList<String> mIntolerances = new ArrayList<>();
    private ArrayList<String> mDiets = new ArrayList<>();
    private TextView mTextView;
    private TextView mTextView2;
    private Button saveAndContinue;
    SharedPreferences sharedPreferences;
    public boolean firstTime;
    public ArrayList<String> allergies = new ArrayList<>();
    public ArrayList<String> diets = new ArrayList<>();
    public static final String mypreference = "mypref";
    public static String firstTimeKey = "firstTimeKey";
    RecyclerViewAdapterToggle adapter;
    RecyclerViewAdapterToggle adapter1;
    public ArrayList<String> allergyNames;
    public ArrayList<String> dietNames;
    private SeekBar bar;
    private SeekBar bar2;
    UserPreferences userPreferences;
    Nutrient[] microNutrients;
    String[] mealTypes;
    CheckBox[] mealTypeCheckBoxes;
    CheckBox[] microNutrientCheckBoxes;
    EditText[] microNutrientAmounts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_startup);

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        initRecyclerChoices();


        bar = (SeekBar) this.findViewById(R.id.seekBar);
        mTextView = ((TextView) InitialStartupActivity.this.findViewById(R.id.timeTextView));
        int progressValue = sharedPreferences.getInt("caloriesDaily", 2000);
        mTextView.setText(String.format("%d",progressValue));
        int progress = progressValue/100-5;
        bar.setProgress(progress);
        progressValue = sharedPreferences.getInt("mealsDaily",3);
        mTextView2.setText(String.format("%d", progressValue));
        progress = progressValue-1;
        bar2.setProgress(progress);

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

        bar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int value = 1 + progress;
                InitialStartupActivity.this.mTextView2.setText(String.format("%d", value));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }

    //Initialize linear layouts that hold checkboxes for the mealtypes and micronutrients
    private void initLinearLayouts() {
        LinearLayout microNutrientLayout = (LinearLayout) this.findViewById(R.id.layout_micro_nutrients);
        LinearLayout mealTypesLayout = (LinearLayout) this.findViewById(R.id.layout_meal_selection);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout listItem;
        mealTypeCheckBoxes = new CheckBox[5];
        microNutrientCheckBoxes = new CheckBox[microNutrients.length];
        microNutrientAmounts = new EditText[microNutrients.length];

        for(int i = 0; i < 5 ; i++){
            listItem = (LinearLayout) inflater.inflate(R.layout.mealtypes,null ,false);
            mealTypesLayout.addView(listItem);
            CheckBox mealType = listItem.findViewById(R.id.mealTypeSelection);
            mealTypeCheckBoxes[i] = mealType;
            mealType.setText(mealTypes[i]);


        }

        //micronutrients
        for(int i = 0; i < microNutrients.length;i++){



            listItem = (LinearLayout) inflater.inflate(R.layout.micronutrients,null ,false);
            microNutrientLayout.addView(listItem);
            CheckBox microNutrientName = listItem.findViewById(R.id.checkBox2);
            EditText microNutrientAmount = listItem.findViewById(R.id.microNutrientAmount);
            microNutrientCheckBoxes[i] = microNutrientName;
            microNutrientAmounts[i] = microNutrientAmount;
            microNutrientName.setText(microNutrients[i].name);
            TextView microNutrientUnit = listItem.findViewById(R.id.micro_nutrient_unit);
            microNutrientUnit.setText(microNutrients[i].unit);
            TextView microNutrientRecommended = listItem.findViewById(R.id.microNutrientDailyDose);
            microNutrientRecommended.setText("("+microNutrients[i].amountDailyTarget+" " + microNutrients[i].unit+ ")");


        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SaveAndContinue:
                boolean allergies[] = adapter.getChoices();
                boolean diets[] = adapter1.getChoices();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(firstTimeKey,false);
                editor.putInt("caloriesDaily",(bar.getProgress()*100 + 500));
                editor.putInt("mealsDaily",(bar2.getProgress()+1));
                int count = 0;
                for(SpoonacularIntolerance i : SpoonacularIntolerance.values()){
                    editor.putBoolean(i.getValue(),allergies[count]);
                    System.out.println(allergies[count]);
                    count++;
                }
                count = 0;

                for(SpoonacularDiet d : SpoonacularDiet.values()){
                    editor.putBoolean(d.getValue(),diets[count]);
                    count++;
                }

                Boolean[] mealChoices = new Boolean[5];
                for(int i = 0; i < mealTypeCheckBoxes.length; i++){
                    mealChoices[i] = mealTypeCheckBoxes[i].isChecked();
                    userPreferences.setMealsPerDay(mealChoices);
                }

                Boolean[] microNutrientChoices = new Boolean[microNutrients.length];
                int[] MicroNutrientAmountChoices = new int[microNutrientAmounts.length];
                for (int i = 0; i < microNutrientChoices.length;i++){
                    Boolean choice = microNutrientCheckBoxes[i].isChecked();
                    microNutrientChoices[i] = choice;
                    if(choice){
                        MicroNutrientAmountChoices[i] = Integer.parseInt(microNutrientAmounts[i].getText().toString());
                    } else {
                        MicroNutrientAmountChoices[i] = 0;
                    }

                }


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
        adapter = new RecyclerViewAdapterToggle(this, mIntolerances);
        adapter1 = new RecyclerViewAdapterToggle(this, mDiets);
        recyclerViewAllergies.setAdapter(adapter);
        recyclerViewAllergies.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerViewDiets.setAdapter(adapter1);
        recyclerViewDiets.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }





    // ------ Actions ------

    private void goToSecondActivity() {

        Intent intent = new Intent(this, DayOverviewActivity.class);
        startActivity(intent);
    }
}


