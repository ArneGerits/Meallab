package com.example.meallab.activities;

import com.example.meallab.Nutrients.ComplexNutrientsOverviewFragment;
import com.example.meallab.Nutrients.Nutrient;
import com.example.meallab.R;
import com.example.meallab.RecyclerViewAdapterIngredients;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.example.meallab.Spoonacular.Recipe;
import com.example.meallab.Spoonacular.SpoonacularAPI;
import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.example.meallab.customViews.CustomScrollView;
import com.example.meallab.customViews.DayViewContainer;
import com.example.meallab.customViews.MonthHeader;
import com.example.meallab.fragments.CardScrollerFragment;
import com.example.meallab.storing_data.PersistentStore;
import com.example.meallab.storing_data.StoredDay;
import com.example.meallab.storing_data.StoredRecipe;
import com.example.meallab.storing_data.StoredShoppingList;
import com.example.meallab.storing_data.UserPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.model.OutDateStyle;
import com.kizitonwose.calendarview.model.ScrollMode;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.WeekFields;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.example.meallab.activities.InitialStartupActivity.mypreference;

import com.example.meallab.customViews.DayViewContainer.DayViewContainerListener;

/**
 * This is the most important activity, here the user can see the meal plan for a given day, view
 * meal detail screens, select different dates to plan ahead and access settings.
 */
public class DayOverviewActivity extends AppCompatActivity implements DayViewContainerListener, PersistentStore.PersistentStoreListener,
        CardScrollerFragment.CardScrollerFragmentListener, SpoonacularAPI.SpoonacularDetailedRecipeListener {

    // ----- Constants ------
    private static final int RECIPE_SELECTION_CODE = 12345;

    private ArrayList<Object> mIngredientNames = new ArrayList<>();
    private ArrayList<String> mIngredientQuantities = new ArrayList<>();
    RecyclerViewAdapterIngredients adapter;

    // True if the calendar is currently showing, False otherwise
    boolean calendarShowing = false;

    // The date selected by the user, on create it is always the current date.
    LocalDate selectedDate = LocalDate.now();

    // ------ Animation variables ------

    // The height of the calendar view.
    private int topBound;
    // The distance between dateTextView and the edge of the screen.
    private float leftDistance;

    // The day this day overview activity shows info for.
    StoredDay currentDay;

    // Used to store objects to disk.
    PersistentStore store;
    UserPreferences preferences;

    // ------ Outlets ------

    private CustomScrollView scrollView;
    private CalendarView calendar;

    private TextView dateTextView;
    private TextView yearTextView;
    private TextView monthTextView;

    private CardScrollerFragment cardsFragment;
    private ComplexNutrientsOverviewFragment nutrientFragment;

    // ------

    SharedPreferences sharedPreferences;
    Gson gson = new Gson();

    // The index of the recipe currently being edited.
    int editingIndex;

    // Used for api communication with Spoonacular
    private SpoonacularAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_day_overview);

        // Init the store, will give callback on completion.
        this.store = new PersistentStore(this,this);
        this.preferences = new UserPreferences(this);

        // Perform view first time setup.
        this.scrollView = findViewById(R.id.scrollView);
        this.calendar   = this.findViewById(R.id.calendarView);

        this.setupCalendar(this.calendar);
        this.setupScrollView(this.scrollView);

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        // Setup the API communication
        api = new SpoonacularAPI(this);

        // Getting the text views of the nav bar.
        this.dateTextView  = this.findViewById(R.id.dateTextView);
        this.yearTextView  = this.findViewById(R.id.yearTextView);
        this.monthTextView = this.findViewById(R.id.monthTextView);

        selected(this.selectedDate);

        ConstraintLayout navBar = this.findViewById(R.id.navBar);
        navBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!calendarShowing) {
                    calendarShowing = true;
                    showCalendar();
                } else {
                    calendarShowing = false;
                    hideCalendar();
                }
            }
        });

        // Setting the fragments.
        this.cardsFragment    = (CardScrollerFragment) getSupportFragmentManager().findFragmentById(R.id.cardsFragment);
        this.cardsFragment.setListener(this);
        this.nutrientFragment = (ComplexNutrientsOverviewFragment) getSupportFragmentManager().findFragmentById(R.id.nutrientsFragment);
    }


    // region Date Selection

    // Called when the user selects a date in the calendar.
    // @pre calendar and dateTextView must be initialized.
    public void selected(LocalDate date) {

        LocalDate old = this.selectedDate;
        this.selectedDate = date;

        // Update the calendar.
        this.calendar.notifyDateChanged(old);
        this.calendar.notifyDateChanged(date);

        this.updateDateTextView(this.selectedDate);

    }
    public void selectedInOrOutDate(LocalDate date) {
        selected(date);

        //this.calendar.smoothScrollToDate(date);
    }
    // Updates the dateTextView with the correct date.
    private void updateDateTextView(LocalDate date) {

        DateTimeFormatter f;
        if (date.getDayOfMonth() > 9) {
            f = DateTimeFormatter.ofPattern("EEE dd MMMM yyyy");
        } else {
            f = DateTimeFormatter.ofPattern("EEE d MMMM yyyy");
        }
        String result = f.format(this.selectedDate);
        this.dateTextView.setText(result);
    }
    //endregion

    //region Animations

    //Animates during the showing/hiding of the calendar.
    private void animateTextViews(float pro) {

        // Provide an S-curved mapping for the progress.
        float progress = 1.0f/ (1.0f + (float)Math.pow(pro/(1 - pro),-1));
        // The alpha value of the date has a steeper curve
        float dateAlpha = 1.0f/ (1.0f + (float)Math.pow(pro/(1 - pro),-3));

        this.monthTextView.setAlpha(1.0f - progress);
        this.yearTextView.setAlpha(1.0f - progress);

        this.dateTextView.setAlpha(dateAlpha);
        ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) this.dateTextView.getLayoutParams();
        p.setMarginEnd((int)((1.0f - progress) * leftDistance));
        this.dateTextView.setLayoutParams(p);
        this.dateTextView.requestLayout();
    }

    //endregion

    //region View Setup

    // Sets up the scroll view.
    private void setupScrollView(final CustomScrollView scrollView) {

        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        scrollView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        scrollView.setCustomListener(new CustomScrollView.CustomScrollViewListener() {
            @Override
            public void onScrollChanged() {

                // Animate the textViews in the navigation bar.
                float top = (float)topBound;
                float y   = (float)scrollView.getScrollY();

                // Calculate the animation progress.
                float progress = Math.min(1.0f,(float) y/top);
                animateTextViews(progress);

                // Only bound the view when the scrollview is not performing a smooth scroll.
                if (scrollView.getScrollY() >= topBound && !scrollView.getIsScrolling() && calendarShowing == true) {
                    scrollView.topBoundEnabled = true;
                    calendarShowing = false;

                    System.out.println("HIDE");

                    DateTimeFormatter f = DateTimeFormatter.ofPattern("MMMM");
                    String result = f.format(selectedDate);
                    monthTextView.setText(result);

                    f = DateTimeFormatter.ofPattern("yyyy");
                    result = f.format(selectedDate);
                    yearTextView.setText(result);

                    calendar.scrollToDate(selectedDate);
                }
            }
        });
    }

    // Sets up the calendar view.
    private void setupCalendar(final CalendarView v) {
        v.setOrientation(RecyclerView.HORIZONTAL);
        v.setScrollMode(ScrollMode.PAGED);
        v.setOutDateStyle(OutDateStyle.END_OF_GRID);

        Function1<CalendarMonth, Unit> listener = new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth cm) {

                // Do something with calendar month
                Locale l = Locale.getDefault();
                String month = cm.getYearMonth().getMonth().getDisplayName(TextStyle.FULL,l);
                int year = cm.getYearMonth().getYear();

                monthTextView.setText(month);
                yearTextView.setText("" + year);

                return kotlin.Unit.INSTANCE;
            }
        };

        v.setMonthScrollListener(listener);

        v.setDayBinder(new DayBinder<DayViewContainer>() {

            @Override
            public void bind(DayViewContainer dayViewContainer, CalendarDay calendarDay) {

                dayViewContainer.day = calendarDay;

                String t = "" + calendarDay.getDate().getDayOfMonth();
                dayViewContainer.textView.setText(t);

                LocalDate today = LocalDate.now();

                dayViewContainer.setIsToday(calendarDay.getDate().isEqual(today));
                dayViewContainer.setIsOutday(!(calendarDay.getOwner() == DayOwner.THIS_MONTH));
                dayViewContainer.setIsSelected(calendarDay.getDate().isEqual(selectedDate));
            }

            @Override
            public DayViewContainer create(View view) {
                DayViewContainer c = new DayViewContainer(view);
                c.setListener(DayOverviewActivity.this);

                return c;
            }
        });
        v.setMonthHeaderBinder(new MonthHeaderFooterBinder<ViewContainer>() {
            @NotNull
            @Override
            public ViewContainer create(@NotNull View view) {
                return new MonthHeader(view);
            }

            @Override
            public void bind(@NotNull ViewContainer viewContainer, @NotNull CalendarMonth calendarMonth) {
                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                MonthHeader header = (MonthHeader)viewContainer;
                header.setFirstDayOfWeek(firstDayOfWeek);
            }
        });
        YearMonth current = YearMonth.now();
        YearMonth first = current.minusMonths(10);
        YearMonth last = current.plusMonths(10);

        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        v.setup(first, last, firstDayOfWeek);
        v.scrollToMonth(current);

        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                topBound = v.getHeight();
                leftDistance = v.getWidth() / 2.0f;

                scrollView.boundYTop = topBound;
                scrollView.topBoundEnabled = true;
                scrollView.scrollTo(0,topBound);
            }
        });
    }

    // Sets up the shopping list view for this day.
    private void setupDayShoppingList(StoredShoppingList list) {

    }
    // Sets up the card scroll view.
    private void setupCardScrollView(StoredRecipe[] recipes) {
        // If there are no recipes chosen yet we present a single empty card.
        if (recipes.length == 0) {
            this.cardsFragment.setValues(recipes, new boolean[]{true});
        } else {
            // If the current day is in the past, we do not care about the user prefs get meals per day.
            if (this.currentDay.date.isBefore(LocalDate.now())) {
                // Set the recipes chosen for that day, no empties.

                boolean[] structure = new boolean[recipes.length];
                this.cardsFragment.setValues(recipes, structure);
            } else {

                SpoonacularMealType[] mealsToEat = this.preferences.getMealsPerDay();
                boolean[] structure = new boolean[mealsToEat.length];
                for (int i = 0; i < structure.length; i++) {
                    SpoonacularMealType meal = mealsToEat[i];
                    structure[i] = true;
                    for (int j = 0; j < recipes.length; j++) {
                        System.out.println("mealtype of recipe: " + recipes[j].mealType.name());
                        if (meal == recipes[j].mealType) {
                            structure[i] = false;
                        }
                    }
                    System.out.println("s: " + structure[i]);
                }
                this.cardsFragment.setValues(recipes, structure);
            }
        }
    }
    // Sets up the nutrients view.
    private void setupNutrientsView(StoredDay day) {

        // 1. Get all tracked nutrients.
        Nutrient[] tracked = this.preferences.getTrackedNutrients();

        // 2. Get the nutrients for the current day.
        Nutrient[] nutrientsToday = day.getTotalNutrients();

        // 3. Cross reference and fill tracked.
        for (Nutrient tr : tracked) {
            for (Nutrient td : nutrientsToday) {
                if (tr.name.equals(td.name)) {
                    tr.amount += td.amount;
                }
            }
        }
        this.nutrientFragment.setValues(tracked);
    }

    //endregion

    //region Calendar view hiding/showing

    // Shows the calendar.
    private void showCalendar() {

        CalendarView v = this.findViewById(R.id.calendarView);
        CustomScrollView scrollView = (CustomScrollView) findViewById(R.id.scrollView);

        scrollView.topBoundEnabled = false;
        // Use the custom smooth scroll, this will set a variable isScrolling.
        scrollView.customSmoothScrollTo(0,0);
    }

    // Hides the calendar.
    private void hideCalendar() {

        CustomScrollView scrollView = (CustomScrollView) findViewById(R.id.scrollView);


        scrollView.smoothScrollTo(0,topBound);
    }

    //endregion

    // ----

    // Initializes all views to the passed day.
    private void switchToDay(StoredDay day) {
        this.currentDay = day;

        // Set views (Calendar, and views for current day).
        this.setupCardScrollView(this.currentDay.recipes);

        this.setupDayShoppingList(day.shoppingList);

        this.setupNutrientsView(day);
    }


    // region Persistent Store Listener

    @Override
    public void initializedSuccessfully(boolean success) {

        StoredDay today = this.store.retrieveDays(new LocalDate[]{this.selectedDate})[0];
        // Load the current day, if no day exists yet a new empty day is created.
        this.switchToDay(this.store.retrieveDays(new LocalDate[]{this.selectedDate})[0]);
    }

    // endregion

    // region Recipe Card Scroller Listener

    @Override
    public void completedSynchronize(boolean success) {

    }

    @Override
    public void selectedShowDetailForIndex(int index) {
        //System.out.println("show detail for " + r.name);
    }

    @Override
    public void selectedEditForIndex(int index) {
        this.editingIndex = index;
    }

    @Override
    public void selectedNewRecipeForIndex(int index) {

        RecipeSelectionActivity recipeSelection = new RecipeSelectionActivity();
        Intent intent = new Intent(this, RecipeSelectionActivity.class);
        SpoonacularMealType[] meals = this.preferences.getMealsPerDay();

        // If there is not a single recipe selected yet, we need to allow the user to select the whole day.
        if (this.currentDay.recipes.length == 0) {
            intent.putExtra("meals", gson.toJson(meals));
        }
        // Only select the recipe for the meal type in the preferences.
        else {
            intent.putExtra("meals", gson.toJson(new SpoonacularMealType[]{meals[index]}));
            this.editingIndex = index;
        }
        // Start the recipe selection.
        startActivityForResult(intent, RECIPE_SELECTION_CODE);
    }

    // Called when a launched activity completes.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (RECIPE_SELECTION_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    // Get the chosen recipes from the activity.
                    String recipesJson = data.getStringExtra(RecipeSelectionActivity.RECIPES_SELECTED);
                    Recipe[] recipesChosen = this.gson.fromJson(recipesJson, Recipe[].class);
                    this.recipesChosen(recipesChosen);
                } else if (resultCode == Activity.RESULT_CANCELED) {

                }
                break;
            }
        }
    }



    // Called when the user has finished choosing recipes in the recipe selection activity.
    // load detailed recipe info.
    private void recipesChosen(Recipe[] recipes) {

        // TODO: Cross ref to avoid loading twice.
        int[] recipeIDS                 = new int[recipes.length];
        SpoonacularMealType[] mealTypes = new SpoonacularMealType[recipes.length];

        // The recipes do not have detailed info yet we need to retrieve it.
        for (int i = 0; i < recipes.length; i++) {
            recipeIDS[i] = recipes[i].id;
            mealTypes[i] = recipes[i].type;
        }
        // Load the data for the recipes chosen.
        this.api.retrieveRecipeDetailedInfo(recipeIDS, mealTypes, this);
    }

    private void addRecipesToDay(Recipe[] recipes) {

        // Check if there was already data in the stored day.
        if (this.currentDay.recipes.length == 0) {
            // Set the entire recipes array.
            this.currentDay.recipes = new StoredRecipe[recipes.length];
            for (int i = 0; i < recipes.length; i++) {
                // Create a stored recipe from the recipe.
                StoredRecipe r = new StoredRecipe(recipes[i]);
                this.currentDay.recipes[i] = r;
            }
        } else {
            // Only set the editing index.
            // There can only be one here.
            Recipe toAdd = recipes[0];

            ArrayList<StoredRecipe> storedRecipes = new ArrayList<StoredRecipe>(Arrays.asList(this.currentDay.recipes));
            if (this.editingIndex >= storedRecipes.size()) {
                storedRecipes.add(new StoredRecipe(toAdd));
            } else {
                storedRecipes.add(this.editingIndex, new StoredRecipe(toAdd));
            }
            StoredRecipe[] arr = new StoredRecipe[storedRecipes.size()];
            arr = storedRecipes.toArray(arr);
            this.currentDay.recipes = arr;
        }
        System.out.println("add recipes to the current day.");
        for (StoredRecipe r : this.currentDay.recipes) {
            System.out.println("TO STORE: " + r.name);
        }
        // Save the changes
        this.store.synchronize();
    }
    // region Spoonacular API callbacks

    @Override
    public void retrievedAdditionalInformation(Recipe[] recipes) {

        System.out.println("Retrieved detailed information fo the recipes");
        // Set the recipe(s) in storedDay, update cardView, update nutrition

        // Add the recipes to storedDay.
        this.addRecipesToDay(recipes);

        // Reload all views.
        this.switchToDay(this.currentDay);
    }

    @Override
    public void complexSpoonacularErrorHandler() {

    }
    // endregion
}
