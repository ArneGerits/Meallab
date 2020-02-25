package com.example.meallab;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

/**
 * This is the starting point of the application.
 */
public class Meallab extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //AndroidThreeTen.init(this);
        AndroidThreeTen.init(this);
    }
}
