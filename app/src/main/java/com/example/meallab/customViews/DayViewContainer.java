package com.example.meallab.customViews;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.meallab.R;
import com.example.meallab.Spoonacular.RecipeEquipment;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;

public class DayViewContainer extends ViewContainer {

    public TextView textView;
    public ImageView check;

    // The day of this day view container.
    public CalendarDay day;

    // True when this view contains the current day.
    private boolean isToday = false;
    // True when there is already a day meal plan for this day.
    private boolean hasMealPlan = false;
    // True when this day is an outday in the calendar.
    private boolean isOutDay = false;
    // True when this day is selected.
    private boolean isSelected = false;

    // ----- Resources ------
    Drawable selected;
    Drawable notSelected;
    Drawable currentDate;
    int textGray;

    private DayViewContainerListener listener;

    public DayViewContainer(View view) {
        super(view);
        textView = view.findViewById(R.id.exOneDayText);
        check = view.findViewById(R.id.checkImageView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (day.getOwner() == DayOwner.THIS_MONTH) {
                        listener.selected(day.getDate());
                    } else {
                        listener.selectedInOrOutDate(day.getDate());
                    }
                }
            }
        });
        this.currentDate = ContextCompat.getDrawable(view.getContext(), R.drawable.calendar_current_date);
        this.selected = ContextCompat.getDrawable(view.getContext(), R.drawable.calendar_selected);
        this.notSelected = ContextCompat.getDrawable(view.getContext(), R.drawable.calendar_not_selected);
        this.textGray = ContextCompat.getColor(this.getView().getContext(), R.color.colorCalendarTextOutday);
    }
    public void setListener(DayViewContainerListener listener) {
        this.listener = listener;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
        this.stateChanged();
    }
    public void setIsOutday(boolean isOutDay) {
        this.isOutDay = isOutDay;
        this.stateChanged();
    }
    public void setHasMealPlan(boolean hasMealPlan) {
        this.hasMealPlan = hasMealPlan;
        this.stateChanged();
    }
    public void setIsSelected(boolean isSelected) {

        this.isSelected = isSelected;
        this.stateChanged();
    }

    // Called when the state of the container changes.
    private void stateChanged() {
        // The background of this container can be multiple drawables.
        ArrayList<Drawable> layers = new ArrayList<>();

        layers.add(this.notSelected);

        if (this.isSelected) {
            layers.add(this.selected);
        }
        if (this.isToday) {
            layers.add(this.currentDate);
        }
        // Creating the compound drawable background.
        Drawable[] l = new Drawable[layers.size()];
        layers.toArray(l);
        LayerDrawable compoundDrawable = new LayerDrawable(l);

        if (isOutDay) {
            textView.setTextColor(textGray);
        } else {
            textView.setTextColor(Color.BLACK);
        }
        if (isSelected) {
            textView.setTextColor(Color.WHITE);
        }
        if (hasMealPlan) {
            this.check.setVisibility(View.VISIBLE);
        } else {
            this.check.setVisibility(View.INVISIBLE);
        }

        this.getView().setBackground(compoundDrawable);
    }

    // ------ INTERFACES ------

    public interface DayViewContainerListener {
        public void selected(LocalDate date);

        public void selectedInOrOutDate(LocalDate date);
    }

}