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
import java.util.Collections;

import android.content.Intent;

import com.example.meallab.Nutrients.Nutrient;
import com.example.meallab.R;
import com.example.meallab.RecyclerViewAdapterToggle;
import com.example.meallab.Spoonacular.SpoonacularDiet;
import com.example.meallab.Spoonacular.SpoonacularIntolerance;
import com.example.meallab.storing_data.UserPreferences;
import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.example.meallab.storing_data.UserPreferences;


public class InitialStartupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "InitialStartupActivity";
    private ArrayList<String> mIntolerances = new ArrayList<>();
    private ArrayList<String> mDiets = new ArrayList<>();
    private TextView mTextView;
    private TextView mTextView2;
    private Button saveAndContinue;
    private Button resetDefaults;
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
    UserPreferences userPreferences;
    Nutrient[] microNutrients;
    SpoonacularMealType[] mealTypes;
    CheckBox[] mealTypeCheckBoxes;
    CheckBox[] microNutrientCheckBoxes;
    EditText[] microNutrientAmounts;


    private UserPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_startup);

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        userPreferences = new UserPreferences(this);
        this.prefs = new UserPreferences(this);

        initRecyclerChoices();
        microNutrients = userPreferences.getDefaultMicroNutrients();
        mealTypes = new SpoonacularMealType[]{SpoonacularMealType.BREAKFAST,
                SpoonacularMealType.BRUNCH_SNACK,SpoonacularMealType.LUNCH,
                SpoonacularMealType.APPETIZER_SNACK,SpoonacularMealType.DINNER,
                SpoonacularMealType.DESSERT};

        initLinearLayouts();

        bar = (SeekBar) this.findViewById(R.id.seekBar);
        mTextView = ((TextView) InitialStartupActivity.this.findViewById(R.id.caloriesSelected));
        int progressValue = sharedPreferences.getInt("caloriesDaily", 2000);
        mTextView.setText(String.format("%d",progressValue));
        int progress = progressValue/100-5;
        bar.setProgress(progress);





        saveAndContinue = (Button) InitialStartupActivity.this.findViewById(R.id.SaveAndContinue);
        saveAndContinue.setOnClickListener(this);
        resetDefaults = (Button) InitialStartupActivity.this.findViewById(R.id.resetPreferences);
        resetDefaults.setOnClickListener(this);
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

    //Initialize linear layouts that hold checkboxes for the mealtypes and micronutrients
    private void initLinearLayouts() {
        Nutrient[] nutrients = userPreferences.getTrackedNutrients();
        Nutrient[] defaultMicroNutrients = userPreferences.getDefaultMicroNutrients();
        System.out.println(nutrients.length);
        System.out.println("REEEEEEEEEEEEEEEE");
        for(int i = 0; i < nutrients.length;i++){
            for(int j = 0 ; j < microNutrients.length; j++){
                System.out.println("i = " + i + " and j : " + j);
                if(nutrients[i].name.equals(microNutrients[j].name)){

                    microNutrients[j].amountDailyTarget = nutrients[i].amountDailyTarget;
                }
            }
        }

        SpoonacularMealType[] trackedMealTypes = userPreferences.getMealsPerDay();
        LinearLayout microNutrientLayout = (LinearLayout) this.findViewById(R.id.layout_micro_nutrients);
        LinearLayout mealTypesLayout = (LinearLayout) this.findViewById(R.id.layout_meal_selection);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout listItem;
        mealTypeCheckBoxes = new CheckBox[mealTypes.length];
        microNutrientCheckBoxes = new CheckBox[microNutrients.length];
        microNutrientAmounts = new EditText[microNutrients.length];

        for(int i = 0; i < mealTypeCheckBoxes.length ; i++){
            listItem = (LinearLayout) inflater.inflate(R.layout.mealtypes,null ,false);
            mealTypesLayout.addView(listItem);
            CheckBox mealType = listItem.findViewById(R.id.mealTypeSelection);
            mealTypeCheckBoxes[i] = mealType;
            mealType.setText(mealTypes[i].getValue());


        }

        //micronutrients
        for(int i = 0; i < microNutrients.length;i++){



            listItem = (LinearLayout) inflater.inflate(R.layout.micronutrients,null ,false);
            microNutrientLayout.addView(listItem);
            CheckBox microNutrientName = listItem.findViewById(R.id.checkBox2);
            EditText microNutrientAmount = listItem.findViewById(R.id.microNutrientAmount);
            microNutrientCheckBoxes[i] = microNutrientName;
            microNutrientAmounts[i] = microNutrientAmount;
            float amount = microNutrients[i].amountDailyTarget;

            microNutrientAmount.setText(String.valueOf(amount));


            microNutrientName.setText(microNutrients[i].name);
            TextView microNutrientUnit = listItem.findViewById(R.id.micro_nutrient_unit);
            microNutrientUnit.setText(microNutrients[i].unit);
            TextView microNutrientRecommended = listItem.findViewById(R.id.microNutrientDailyDose);
            microNutrientRecommended.setText("("+defaultMicroNutrients[i].amountDailyTarget+" " + microNutrients[i].unit+ ")");


        }


        for(int i = 0; i < trackedMealTypes.length; i++){
            for(int j = 0 ; j < mealTypes.length; j++){
                if(trackedMealTypes[i].getValue().equals(mealTypes[j].getValue())){
                    mealTypeCheckBoxes[j].setChecked(true);
                }
            }
        }
        for(int i = 0; i < nutrients.length;i++){
            for(int j = 0; j < microNutrients.length; j++){
                if(nutrients[i].name.equals(microNutrients[j].name)){
                    microNutrientCheckBoxes[j].setChecked(true);
                    break;
                }
            }
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
                this.prefs.setIsFirstTime(false);
                // TODO: Make user of UserPreferences.

                //editor.putInt("caloriesDaily",(bar.getProgress()*100 + 500));
                //editor.putInt("mealsDaily",(bar2.getProgress()+1));
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

                ArrayList<SpoonacularMealType> mealChoices = new ArrayList<>();
                for(int i = 0; i < mealTypeCheckBoxes.length; i++){
                    boolean choice = mealTypeCheckBoxes[i].isChecked();
                    if(choice){
                        mealChoices.add(mealTypes[i]);
                    }
                }
                userPreferences.setMealsPerDay( mealChoices.toArray
                        (new SpoonacularMealType[mealChoices.size()]));


                Nutrient[] macroNutrients = userPreferences.getDefaultMacroNutrients();
                ArrayList<Nutrient> microNutrientChoices = new ArrayList<>();
                Collections.addAll(microNutrientChoices, macroNutrients);
                for (int i = 0; i < microNutrients.length;i++){
                    Boolean choice = microNutrientCheckBoxes[i].isChecked();
                    if(choice){
                        try{
                            microNutrients[i].amountDailyTarget = Float.parseFloat(microNutrientAmounts[i].getText().toString());

                        } catch (Exception e){

                        }
                        microNutrientChoices.add(microNutrients[i]);
                }}
                userPreferences.setTrackedNutrients(microNutrientChoices.toArray
                        (new Nutrient[microNutrientChoices.size()]));


                editor.commit();
                System.out.println("clicked in second activity");
                goToSecondActivity();
                break;


            case R.id.resetPreferences:
                Nutrient[] defaultPreferences = new Nutrient[]{};
                userPreferences.setTrackedNutrients(defaultPreferences);
                allergies = adapter.getChoices();
                diets = adapter1.getChoices();
                editor = sharedPreferences.edit();
                editor.putBoolean(firstTimeKey,true);
                editor.putInt("caloriesDaily",2000);
                count = 0;
                for(SpoonacularIntolerance i : SpoonacularIntolerance.values()){
                    editor.putBoolean(i.getValue(),false);
                    count++;
                }
                count = 0;

                for(SpoonacularDiet d : SpoonacularDiet.values()){
                    editor.putBoolean(d.getValue(),false);
                    count++;
                }

                userPreferences.setMealsPerDay( new SpoonacularMealType[]{});
                editor.commit();

                Intent intent = new Intent(this, InitialStartupActivity.class);
                startActivity(intent);
                break;


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




