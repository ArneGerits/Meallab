package com.example.meallab.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meallab.R;
import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.example.meallab.fragments.DateSelectionFragment;
import com.example.meallab.storing_data.PersistentStore;
import com.example.meallab.storing_data.StoredDay;
import com.example.meallab.storing_data.UserPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
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

    // Get this from the user preferences.
    SORT_OPTION currentSort = SORT_OPTION.ALPHABET;

    private PersistentStore store;

    // ----- Outlets ------
    private DateSelectionFragment dateFragment;
    private TextView monthTextView;
    private TextView sTextView;
    private ImageButton doneButton;
    private ImageButton sortButton;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.ItemDecoration mDecorator;
    private RecyclerView.LayoutManager layoutManager;

    // User can sort by date or alphabet.
    public enum SORT_OPTION {
        DATE,
        ALPHABET,
        RECIPE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        // Set the structure.
        this.prefs = new UserPreferences(this);
        this.store = PersistentStore.getSharedInstance();

        // Get the next 7 days from the store.
        // Get the next 6 stored days.
        this.days = new StoredDay[7];

        // Get the stored days.
        LocalDate selectedDate = LocalDate.now();
        for (int i = 0; i < days.length; i++) {
            StoredDay d =  this.store.retrieveDay(selectedDate.plusDays(i));
            days[i]     = d;
        }

        // Set the structure.
        this.structure = this.prefs.getShoppingListSelectionStructure();

        // Setting the outlets
        this.doneButton    = this.findViewById(R.id.doneButton);
        this.sortButton    = this.findViewById(R.id.sortButton);
        this.sTextView     = this.findViewById(R.id.shoppingTextView);
        this.monthTextView = this.findViewById(R.id.monthTextView);
        this.dateFragment  = (DateSelectionFragment) this.getSupportFragmentManager().findFragmentById(R.id.dateFragment);
        this.recyclerView  = this.findViewById(R.id.recyclerView);

        setupMonthTextView();
        setupDateFragment();

        this.dateFragment.setListener(this);
        this.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        this.sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort();
            }
        });

        this.setupRecyclerView(this.recyclerView);
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

    private void setupRecyclerView(RecyclerView v) {
        v.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        v.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new ShoppingListAdapter(this.getHighlightedDays(), this.currentSort);
        v.setAdapter(mAdapter);

        // Specify a decorator
        mDecorator = new ShoppingListDecorator();
        v.addItemDecoration(mDecorator);
    }
    // region Actions

    // Called when the user clicks on the sort button.
    private void sort() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = this.getLayoutInflater().inflate(R.layout.bottom_dialog_sort, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        LinearLayout alphabet = (LinearLayout) sheetView.findViewById(R.id.alphabet_sheet);
        LinearLayout dates = (LinearLayout) sheetView.findViewById(R.id.date_sheet);
        LinearLayout recipes = (LinearLayout) sheetView.findViewById(R.id.recipe_sheet);

        alphabet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDataBy(SORT_OPTION.ALPHABET);
                mBottomSheetDialog.dismiss();
            }
        });
        dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDataBy(SORT_OPTION.DATE);
                mBottomSheetDialog.dismiss();
            }
        });
        recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDataBy(SORT_OPTION.RECIPE);
                mBottomSheetDialog.dismiss();
            }
        });
    }
    private void sortDataBy(SORT_OPTION option) {
        this.currentSort = option;

        // Sort the data in the recyclerview.
        ShoppingListAdapter a = (ShoppingListAdapter) this.mAdapter;
        a.sortBy(option);
    }

    // Called when the user clicks the confirm button.
    private void confirm() {
        // Sync data.
        this.store.synchronize(this);

        // Convert the days to json.
        String days = this.gson.toJson(this.days);
        // Put the chosen days in the intent
        Intent resultIntent = new Intent();
        resultIntent.putExtra(DAYS, days);
        setResult(Activity.RESULT_OK, resultIntent);
        // Finalize.
        finish();
    }

    // endregion

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

        // Updates the entire recyclerview with the correct days.
        ShoppingListAdapter adapter = (ShoppingListAdapter) this.mAdapter;
        adapter.setDays(this.getHighlightedDays());

        // Save the structure to user prefs.
        this.prefs.setShoppingListSelectionStructure(this.structure);
    }
    // endregion
}
