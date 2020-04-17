package com.example.meallab.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import android.content.Intent;

import com.example.meallab.R;
import com.example.meallab.storing_data.PersistentStore;
import com.example.meallab.storing_data.UserPreferences;


public class MainActivity extends AppCompatActivity implements PersistentStore.PersistentStoreListener {

    PersistentStore store = PersistentStore.getSharedInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Creating the persistent store singleton.
        store.setListener(this);
        store.initialize(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Sync the store on pause.
        this.store.synchronize(this);
    }

    private void goToInitialStartupActivity() {

        Intent intent = new Intent(this, InitialStartupActivity.class);

        startActivity(intent);

    }

    private void goToDayOverViewActivity() {

        System.out.println("go to day overview.");
        Intent intent = new Intent(this, DayOverviewActivity.class);

        startActivity(intent);
    }

    @Override
    public void initializedSuccessfully(boolean success) {

        UserPreferences prefs = new UserPreferences(this);

        boolean first = prefs.getIsFirstTime();

        if (first) {
            goToInitialStartupActivity();
        } else {
            goToDayOverViewActivity();
        }
    }

    @Override
    public void completedSynchronize(boolean success) {

    }
}