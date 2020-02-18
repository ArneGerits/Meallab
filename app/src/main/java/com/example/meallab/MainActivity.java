package com.example.meallab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;

import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // ------ Actions ------

    private void goToSecondActivity() {

        Intent intent = new Intent(this, SecondActivity.class);

        startActivity(intent);

    }
}
