package com.example.meallab.activities;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.meallab.Nutrients.ComplexNutrientsOverviewFragment;
import com.example.meallab.Nutrients.Nutrient;
import com.example.meallab.R;
import com.example.meallab.RecyclerViewAdapterIngredients;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meallab.Spoonacular.Recipe;
import com.example.meallab.Spoonacular.SpoonacularAPI;
import com.example.meallab.Spoonacular.SpoonacularMealType;
import com.example.meallab.Spoonacular.VolleySingleton;
import com.example.meallab.customViews.CustomScrollView;
import com.example.meallab.customViews.DayViewContainer;
import com.example.meallab.customViews.Divider;
import com.example.meallab.customViews.MonthHeader;
import com.example.meallab.fragments.CardScrollerFragment;
import com.example.meallab.storing_data.PersistentStore;
import com.example.meallab.storing_data.StoredDay;
import com.example.meallab.storing_data.StoredRecipe;
import com.example.meallab.storing_data.UserPreferences;
import com.google.gson.Gson;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import com.example.meallab.customViews.DayViewContainer.DayViewContainerListener;

/**
 * This is the most important activity, here the user can see the meal plan for a given day, view
 * meal detail screens, select different dates to plan ahead and access settings.
 */
public class DayOverviewActivity extends AppCompatActivity implements DayViewContainerListener,
        CardScrollerFragment.CardScrollerFragmentListener, SpoonacularAPI.SpoonacularDetailedRecipeListener {

    // ----- Constants ------

    private static final int RECIPE_SELECTION_CODE = 1;
    private static final int SHOPPING_CODE         = 2;

    // -----

    // Get this from the user preferences.
    ShoppingListActivity.SORT_OPTION currentSort = ShoppingListActivity.SORT_OPTION.ALPHABET;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.ItemDecoration mDecorator;
    private RecyclerView.LayoutManager layoutManager;


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

    private ImageButton settingsButton;
    private ImageButton shoppingButton;

    private RecyclerView ingredients;
    private FrameLayout recyclerParent;
    private Divider ingredientsDivider;
    private boolean atScrollBottom = false;

    private ConstraintLayout navBar;

    // ------

    SharedPreferences sharedPreferences;
    Gson gson = new Gson();

    // The index of the recipe currently being edited.
    int editingIndex;

    // Used for api communication with Spoonacular
    private SpoonacularAPI api;

    // A cache of fully loaded recipes is kept to decrease API calls,
    // it maps recipe IDS to Recipe objects.
    private HashMap<Integer, Recipe> recipesCache = new HashMap<Integer, Recipe>();

    private FrameLayout calendarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_overview);

        // Setting the fragments.
        this.cardsFragment    = (CardScrollerFragment) getSupportFragmentManager().findFragmentById(R.id.cardsFragment);
        this.cardsFragment.setListener(this);
        this.nutrientFragment = (ComplexNutrientsOverviewFragment) getSupportFragmentManager().findFragmentById(R.id.nutrientsFragment);

        // Init the store, will give callback on completion.
        this.store = PersistentStore.getSharedInstance();

        this.preferences = new UserPreferences(this);

        // Perform view first time setup.
        this.scrollView = findViewById(R.id.scrollView);
        this.calendar   = this.findViewById(R.id.calendarView);

        this.calendarContainer = this.findViewById(R.id.calendarContainer);

        this.setupCalendar(this.calendar);
        this.setupScrollView(this.scrollView);

        // Setup the API communication
        api = new SpoonacularAPI(this);

        // Getting the text views of the nav bar.
        this.dateTextView   = this.findViewById(R.id.dateTextView);
        this.yearTextView   = this.findViewById(R.id.yearTextView);
        this.monthTextView  = this.findViewById(R.id.monthTextView);
        this.settingsButton = this.findViewById(R.id.settingsButton);
        this.shoppingButton = this.findViewById(R.id.shoppingButton);

        ConstraintLayout navBar = this.findViewById(R.id.navBar);
        navBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!calendarShowing) {
                    showCalendar();
                } else {
                    hideCalendar();
                }
            }
        });
        this.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSettings();
            }
        });
        this.shoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchShoppingList();
            }
        });
        this.selected(this.selectedDate);

        this.ingredients = this.findViewById(R.id.ingredients);
        this.recyclerParent = this.findViewById(R.id.recyclerParent);
        this.ingredientsDivider = this.findViewById(R.id.ingredientsDivider);
        this.navBar = this.findViewById(R.id.navBar);

        this.setupRecyclerView(this.ingredients);
    }

    // ----- Actions ------

    // Launches the settings activity.
    private void launchSettings() {
            Intent intent = new Intent(this,InitialStartupActivity.class);
            startActivity(intent);

    }
    // Launches the shopping list activity.
    private void launchShoppingList() {

        ShoppingListActivity recipeSelection = new ShoppingListActivity();
        Intent intent = new Intent(this, ShoppingListActivity.class);
        // Start the recipe selection.
        startActivityForResult(intent, SHOPPING_CODE);
    }

    private void setupRecyclerView(RecyclerView v) {
        v.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        v.setLayoutManager(layoutManager);

        ArrayList<StoredDay> day = new ArrayList<>();
        day.add(this.currentDay);

        // specify an adapter
        mAdapter = new ShoppingListAdapter(day, this.currentSort);
        v.setAdapter(mAdapter);

        // Specify a decorator
        mDecorator = new ShoppingListDecorator();
        v.addItemDecoration(mDecorator);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Used to get the height of the screen.
        View content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);

        DisplayMetrics m = getApplication().getResources().getDisplayMetrics();
        System.out.println("density: " + m.density);
        // Get the height of the nav bar and ingredients divider.
        ViewGroup.LayoutParams p1 = this.ingredientsDivider.getLayoutParams();
        ViewGroup.LayoutParams p2 = this.navBar.getLayoutParams();

        System.out.println("p1 + p2: " + (p1.height + p2.height));
        System.out.println("content height: " + content.getHeight());
        System.out.println("total height: " + (content.getHeight() - (p1.height + p2.height)));
        ViewGroup.LayoutParams p = this.recyclerParent.getLayoutParams();
        p.height = (content.getHeight() - (int)(106 * m.density));//(p1.height + p2.height));
        this.recyclerParent.setLayoutParams(p);
    }
    // region Date Selection

    // Called when the user selects a date in the calendar.
    // @pre calendar and dateTextView must be initialized.
    public void selected(LocalDate date) {
        // Set the new selected date.
        this.selectedDate = date;
        // Update the calendar.
        this.calendar.notifyCalendarChanged();
        // Update the text view.
        this.updateDateTextView(this.selectedDate);

        // Ask the persistent store for the date selected, and switch to that day.
        this.switchToDay(this.store.retrieveDay(date));

    }
    public void selectedInOrOutDate(LocalDate date) {
        selected(date);
    }
    // Updates the dateTextView with the correct date.
    private void updateDateTextView(LocalDate date) {

        DateTimeFormatter f;
        if (date.getDayOfMonth() > 9) {
            f = DateTimeFormatter.ofPattern("EEE dd MMMM");
        } else {
            f = DateTimeFormatter.ofPattern("EEE d MMMM");
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
                if (scrollView.getScrollY() >= topBound && !scrollView.getIsScrolling() && calendarShowing) {
                    scrollView.topBoundEnabled = true;
                    calendarShowing = false;

                    DateTimeFormatter f = DateTimeFormatter.ofPattern("MMMM");
                    String result = f.format(selectedDate);
                    monthTextView.setText(result);

                    f = DateTimeFormatter.ofPattern("yyyy");
                    result = f.format(selectedDate);
                    yearTextView.setText(result);

                    calendar.scrollToDate(selectedDate);
                }

                // If the scroll view has reached the maximum value we change
                // the ingredients dividers color.

                // Used to check if the scrollview is at the bottom.
                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                // if diff is zero, then the bottom has been reached
                if (diff == 0) {
                    if (!atScrollBottom) {

                        ingredientsDivider.setHighlighted(true);
                        // Change color to red.
                        atScrollBottom = true;
                    }
                } else {

                    if (atScrollBottom) {
                        ingredientsDivider.setHighlighted(false);
                        // Change color to white.
                        atScrollBottom = false;
                    }
                }

            }
        });
    }

    // Sets up the calendar view.
    private void setupCalendar(final CalendarView v) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        // Calculate the dimensions of a cell, 7 cells need to be next to eachother.

        float dip = 30f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );


        int cellDimension = width / 7;
        int calendarHeight = cellDimension * 5 + (int)px;

        ViewGroup.LayoutParams p =  this.calendarContainer.getLayoutParams();
        p.height = calendarHeight;
        this.calendarContainer.setLayoutParams(p);

        v.setDayHeight(cellDimension);
        v.setDayWidth(cellDimension);
        topBound = calendarHeight;
        leftDistance = width / 2.0f;

        scrollView.boundYTop = topBound;
        scrollView.topBoundEnabled = true;
        scrollView.scrollTo(0,topBound);

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
    }

    // Sets up the shopping list view for this day.
    private void setupDayShoppingList(StoredRecipe[] recipes) {

    }
    // Sets up the card scroll view.
    private void setupCardScrollView(StoredRecipe[] recipes) {

        // If there are no recipes chosen yet we present a single empty card.
        if (recipes.length == 0) {
            this.cardsFragment.setValues(recipes, new boolean[]{true});
        } else {
            boolean[] structure;

            // If the current day is in the past, we do not care about the user prefs get meals per day.
            if (this.currentDay.date.isBefore(LocalDate.now())) {
                // Set the recipes chosen for that day, no empties.
                structure = new boolean[recipes.length];
            } else {
                SpoonacularMealType[] mealsToEat = this.preferences.getMealsPerDay();
                structure = new boolean[mealsToEat.length];
                for (int i = 0; i < structure.length; i++) {
                    SpoonacularMealType meal = mealsToEat[i];
                    structure[i] = true;
                    for (int j = 0; j < recipes.length; j++) {
                        if (meal == recipes[j].mealType) {
                            structure[i] = false;
                        }
                    }
                }
            }

            // Set the amount of calories goal.
            for (StoredRecipe r : recipes) {
                r.nutrients[0].amountDailyTarget = this.preferences.getTrackedNutrients()[0].amountDailyTarget;
            }
            this.cardsFragment.setValues(recipes, structure);

            // Now load the images of the recipes.
            loadAndSetRecipeImages(recipes);
        }
    }
    // Loads the recipe images and sets them on the card view.
    private void loadAndSetRecipeImages(StoredRecipe[] recipes) {
        // Used to load images.
        ImageLoader imageLoader = VolleySingleton.getInstance(this).getImageLoader();
        for (final StoredRecipe r : recipes) {
            // Load the image.
            imageLoader.get(r.imageURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        System.out.println("GOT THE IMAGE for: " + r.name);
                        Bitmap result = response.getBitmap();
                        cardsFragment.setImageOnRecipe(result,r);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //todo: show error screen.
                    System.out.println("VOLLEY ERROR recipe select " +  error.toString());
                }
            });
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
        calendarShowing = true;

        CalendarView v = this.findViewById(R.id.calendarView);
        CustomScrollView scrollView = (CustomScrollView) findViewById(R.id.scrollView);

        scrollView.topBoundEnabled = false;
        // Use the custom smooth scroll, this will set a variable isScrolling.
        scrollView.customSmoothScrollTo(0,0);
    }

    // Hides the calendar.
    private void hideCalendar() {

        CustomScrollView scrollView = (CustomScrollView) findViewById(R.id.scrollView);
        scrollView.customSmoothScrollTo(0,topBound);
    }

    //endregion

    // ----

    // Initializes all views to the passed day.
    private void switchToDay(StoredDay day) {
        this.currentDay = day;

        // Set views (Calendar, and views for current day).
        this.setupCardScrollView(this.currentDay.recipes);

        this.setupDayShoppingList(day.recipes);

        this.setupNutrientsView(day);
    }

    @Override
    public void selectedShowDetailForIndex(int index) {
        System.out.println("show detail");
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
                }
                break;
            }
            case (SHOPPING_CODE) : {
                // After returning from the shopping list reload the recyclerview.
                this.ingredients.getAdapter().notifyDataSetChanged();
                break;
            }
        }
    }


    // Called when the user has finished choosing recipes in the recipe selection activity.
    // load detailed recipe info.
    private void recipesChosen(Recipe[] recipes) {

        // 1. Create a stored day object for each recipe.
        this.addRecipesToDay(recipes);

        // 2. Populate the recipeCardView and nutrients view.
        this.setupCardScrollView(this.currentDay.recipes);
        this.setupNutrientsView(this.currentDay);

        // 3. Compute for which recipes additional data needs to be loaded.

        // Array with recipes to load more data from.
        ArrayList<Recipe> toLoadMore = new ArrayList<>();

        for (Recipe r : recipes) {
            if (!r.hasLoadedFully) {
                // It needs to be loaded more.
                toLoadMore.add(r);
            } else {
                // It is loaded fully, store it in the cache.
                this.recipesCache.put(r.id,r);
            }
        }
        // 4. Load the data from those.

        int[] recipeIDS                 = new int[toLoadMore.size()];
        SpoonacularMealType[] mealTypes = new SpoonacularMealType[toLoadMore.size()];

        // The recipes do not have detailed info yet we need to retrieve it.
        for (int i = 0; i < toLoadMore.size(); i++) {
            recipeIDS[i] = toLoadMore.get(i).id;
            mealTypes[i] = toLoadMore.get(i).type;
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

            ArrayList<StoredRecipe> storedRecipes = new ArrayList<>(Arrays.asList(this.currentDay.recipes));
            if (this.editingIndex >= storedRecipes.size()) {
                storedRecipes.add(new StoredRecipe(toAdd));
            } else {
                storedRecipes.set(this.editingIndex, new StoredRecipe(toAdd));
            }
            StoredRecipe[] arr = new StoredRecipe[storedRecipes.size()];
            arr = storedRecipes.toArray(arr);
            this.currentDay.recipes = arr;
        }

        // Save the changes
        this.store.synchronize(this);
    }
    // region Spoonacular API callbacks

    @Override
    public void retrievedAdditionalInformation(Recipe[] recipes) {

        System.out.println("Retrieved detailed information fo the recipes");

        // Store the recipes in the cache.
        for (Recipe r : recipes) {
            this.recipesCache.put(r.id,r);
        }

        // Add the info about the nutrients to storedDay.
        for (StoredRecipe r : this.currentDay.recipes) {
            for (Recipe res : recipes) {
                if (r.recipeID == res.id) {
                    r.nutrients = res.nutrients;
                    r.setItems(res.ingredients);
                }
            }
        }

        // Sync the store.
        this.store.synchronize(this);

        // Reload the nutrients and shopping list.
        this.setupNutrientsView(this.currentDay);

        this.setupDayShoppingList(this.currentDay.recipes);
    }

    @Override
    public void complexSpoonacularErrorHandler() {

    }
    // endregion
}
