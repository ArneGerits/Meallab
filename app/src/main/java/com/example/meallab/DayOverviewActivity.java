package com.example.meallab;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.Layout;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.meallab.Spoonacular.RecipeIngredient;
import com.example.meallab.Spoonacular.SpoonacularAPI;
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
import org.threeten.bp.Year;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.WeekFields;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import com.example.meallab.CustomScrollView;

import static com.example.meallab.InitialStartupActivity.mypreference;


public class DayOverviewActivity extends AppCompatActivity implements DayViewContainerListener {


    private ArrayList<String> mIngredientNames = new ArrayList<>();
    private ArrayList<String> mIngredientQuantities = new ArrayList<>();
    RecyclerViewAdapterIngredients adapter;

    boolean calendarShowing = false;

    public static String firstTimeKey = "firstTimeKey";

    LocalDate selectedDate = LocalDate.now();

    // Animation variables.

    private int topBound;
    private float leftDistance;

    // ------ Views ------
    private CustomScrollView scrollView;
    private CalendarView calendar;

    private TextView dateTextView;
    private TextView yearTextView;
    private TextView monthTextView;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        this.scrollView = (CustomScrollView) findViewById(R.id.scrollView);
        this.calendar = this.findViewById(R.id.calendarView);

        this.setupCalendar(this.calendar);
        this.setupScrollView(this.scrollView);

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        testRecycler();

        this.dateTextView  = this.findViewById(R.id.dateTextView);
        this.yearTextView  = this.findViewById(R.id.yearTextView);
        this.monthTextView = this.findViewById(R.id.monthTextView);

        selected(this.selectedDate);
        this.dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!calendarShowing) {
                    calendarShowing = true;
                    showCalendar();
                }
            }
        });
    }

    private void setupScrollView(final CustomScrollView scrollView) {

        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        scrollView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        testRecycler();

        scrollView.setCustomListener(new CustomScrollViewListener() {
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

    public void selected(LocalDate date) {

        // Update the calendar.
        LocalDate old = this.selectedDate;
        this.selectedDate = date;
        this.calendar.notifyDateChanged(old);
        this.calendar.notifyDateChanged(date);

        // Update the label by formatting the date
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MMMM dd");
        String result = f.format(this.selectedDate);
        this.dateTextView.setText(result);
    }

    private void animateTextViews(float pro) {

        // Provide an S-curved mapping for the progress.
        float progress = 1.0f/ (1.0f + (float)Math.pow(pro/(1 - pro),-1));

        this.dateTextView.setAlpha(progress);
        this.monthTextView.setAlpha(1.0f - progress);
        this.yearTextView.setAlpha(1.0f - progress);

        ConstraintLayout.LayoutParams p = (ConstraintLayout.LayoutParams) this.dateTextView.getLayoutParams();
        p.setMarginEnd((int)((1.0f - progress) * leftDistance));
        this.dateTextView.setLayoutParams(p);
        this.dateTextView.requestLayout();
    }

    private void updateNavigationBar(LocalDate l) {

    }
    private void setupCalendar(final CalendarView v) {
        v.setOrientation(RecyclerView.HORIZONTAL);
        v.setScrollMode(ScrollMode.PAGED);
        v.setOutDateStyle(OutDateStyle.END_OF_GRID);

        Function1<CalendarMonth, Unit> listener = new Function1<CalendarMonth, Unit>() {
            @Override
            public Unit invoke(CalendarMonth cm) {
                System.out.println("invoke bitch");

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
                String t = "" + calendarDay.getDate().getDayOfMonth();
                dayViewContainer.textView.setText(t);

                LocalDate today = LocalDate.now();

                dayViewContainer.setIsToday(calendarDay.getDate().isEqual(today));
                dayViewContainer.setIsOutday(!(calendarDay.getOwner() == DayOwner.THIS_MONTH));
                dayViewContainer.setIsSelected(calendarDay.getDate().isEqual(selectedDate));

                dayViewContainer.day = calendarDay;
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

        v.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("call");
                topBound = v.getHeight();
                leftDistance = v.getWidth() / 2.0f;

                scrollView.boundYTop = topBound;
                scrollView.topBoundEnabled = true;
                scrollView.scrollTo(0,topBound);
            }
        });
    }
    // Gets called when the user chooses a new date.
    // @param date, the date chosen.
    private void chosenDate(GregorianCalendar cal) {

        // The date chosen.
        Date date = cal.getTime();

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        TextView dateText = this.findViewById(R.id.dateTextView);
        dateText.setText(dateFormat.format(date));

    }

    //region Calendar view

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

    public void testRecycler(){

        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();

        RecipeIngredient ingredient = new RecipeIngredient();
        RecipeIngredient ingredient2 = new RecipeIngredient();
        ingredient.name = "Egg";
        ingredient.amount = 3;
        ingredient.unitLong = "";
        ingredient.metaInformation = new String[]{};

        for(int i = 0; i < 10 ; i++){
            ingredients.add(ingredient);
        }
        ingredient2.name="pasta";
        ingredient2.amount = 2;
        ingredient2.unitLong = "cups";
        ingredient2.metaInformation = new String[]{"salted", "boiled", "cooled"};
        ingredients.add(ingredient2);



        initRecyclerView(ingredients);


    }

    private void initRecyclerView(ArrayList<RecipeIngredient> ingredients){

        String name;
        String quantity;

        for(RecipeIngredient r : ingredients){

            name = r.name;
            quantity = r.amount + " " + r.unitLong;
            if(r.metaInformation.length != 0){
                for(int i = 0; i < r.metaInformation.length ; i++){
                    quantity = quantity + ", " + r.metaInformation[i];
                }
            }
            mIngredientNames.add(name);
            mIngredientQuantities.add(quantity);
        }
        RecyclerView recyclerViewShoppingList = findViewById(R.id.recyclerv_view_shopping_list_overview);
        adapter = new RecyclerViewAdapterIngredients(this, mIngredientNames, mIngredientQuantities);
        recyclerViewShoppingList.setAdapter(adapter);
        recyclerViewShoppingList.setLayoutManager(new LinearLayoutManager(this));

    }


}
