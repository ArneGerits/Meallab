package com.example.meallab.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meallab.R;
import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.example.meallab.fragments.DateSelectionFragment;
import com.example.meallab.storing_data.StoredDay;
import com.example.meallab.storing_data.UserPreferences;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.util.ArrayList;
import java.util.Locale;

public class ShoppingListActivity extends AppCompatActivity implements DateSelectionFragment.DateSelectionListener {

    // ----- Constants ------
    public static final String DAYS = "recipes_selected";

    // ----- IVars ------

    // The 7 days this shopping list is for.
    StoredDay[] days;
    // The selection structure.
    boolean structure[];
    // Gson to parse json.
    Gson gson = new Gson();
    // To consult user preferences.
    UserPreferences prefs;

    // ----- Outlets ------
    private DateSelectionFragment dateFragment;
    private TextView monthTextView;
    private TextView sTextView;
    private ImageButton doneButton;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        // Set the days.
        String jsonDays = getIntent().getStringExtra("days");
        this.days = gson.fromJson(jsonDays, StoredDay[].class);

        // Set the structure.
        this.prefs = new UserPreferences(this);

        // TODO: figure out how to structure.
        this.structure = new boolean[]{true,true,true,true,true,false,false};

        // Setting the outlets
        this.doneButton    = this.findViewById(R.id.doneButton);
        this.sTextView     = this.findViewById(R.id.shoppingTextView);
        this.monthTextView = this.findViewById(R.id.monthTextView);
        this.dateFragment  = (DateSelectionFragment) this.getSupportFragmentManager().findFragmentById(R.id.dateFragment);
        this.recyclerView  = this.findViewById(R.id.recyclerView);

        setupMonthTextView();
        setupDateFragment();

        this.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        this.setupRecyclerView();
    }

    // Gets the highlighted days according to the structure.
    private ArrayList<StoredDay> getHighlightedDays() {
        ArrayList<StoredDay> days = new ArrayList<>();
        for (int i = 0; i < this.structure.length; i++) {
            boolean s = this.structure[i];
            if (s) {
                days.add(this.days[i]);
            }
        }
        return days;
    }

    private void setupRecyclerView() {
        this.recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new ShoppingListAdapter(this.getHighlightedDays());
        recyclerView.setAdapter(mAdapter);
    }
    // Called when the user clicks the confirm button.
    private void confirm() {
        // Convert the days to json.
        String days = this.gson.toJson(this.days);
        // Put the chosen days in the intent
        Intent resultIntent = new Intent();
        resultIntent.putExtra(DAYS, days);
        setResult(Activity.RESULT_OK, resultIntent);
        // Finalize.
        finish();
    }
    // Sets up the month text view with the correct month.
    private  void setupMonthTextView() {

        Locale l = Locale.getDefault();
        String month = days[0].date.getMonth().getDisplayName(TextStyle.FULL,l);
        // Set the month.
        this.monthTextView.setText(month);
    }
    // Sets up the correct values on the date fragment.
    private void setupDateFragment() {

        DateTimeFormatter f = DateTimeFormatter.ofPattern("EEE");
        String[] dayTitles  = new String[7];
        String[] dayNumbers = new String[7];

        for (int i = 0; i < this.days.length; i++) {
            StoredDay day = this.days[i];
            dayTitles[i]  = f.format(day.date);
            dayNumbers[i] = "" + day.date.getDayOfMonth();
        }

        this.dateFragment.setValues(dayTitles, dayNumbers, this.structure);
    }
    // region DateSelectionListener
    @Override
    public void selectedDate(int index, boolean isSelected) {
        // Set the structure.
        this.structure[index] = isSelected;

        // Update the list.
        // TODO
    }
    // endregion
}
