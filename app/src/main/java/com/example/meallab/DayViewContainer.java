package com.example.meallab;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.threeten.bp.LocalDate;

public class DayViewContainer extends ViewContainer {

    public TextView textView;

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

    private DayViewContainerListener listener;

    public DayViewContainer(View view) {
        super(view);
        textView = view.findViewById(R.id.exOneDayText);

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
    }
    public void setListener(DayViewContainerListener listener) {
        this.listener = listener;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;

        if (isToday && !this.isSelected) {
            int color = ContextCompat.getColor(this.getView().getContext(), R.color.colorCalendarCircleGray);
            this.setCircleColor(color);
        }
    }
    public void setIsOutday(boolean isOutDay) {
        this.isOutDay = isOutDay;

        if (isOutDay) {
            int color = ContextCompat.getColor(this.getView().getContext(), R.color.colorCalendarTextOutday);
            textView.setTextColor(color);
        } else {
            textView.setTextColor(Color.parseColor("#000000"));
        }
    }
    public void setHasMealPlan(boolean hasMealPlan) {
        this.hasMealPlan = hasMealPlan;

        if (hasMealPlan) {

        } else {

        }
    }
    public void setIsSelected(boolean isSelected) {

        this.isSelected = isSelected;

        if (isSelected) {
            int color = ContextCompat.getColor(this.getView().getContext(), R.color.colorPrimary);
            this.setCircleColor(color);
        } else {
            if (!this.isToday) {
                this.setCircleColor(Color.WHITE);
            } else {
                int color = ContextCompat.getColor(this.getView().getContext(), R.color.colorCalendarCircleGray);
                this.setCircleColor(color);
            }
        }

    }

    // ------ Private Methods ------
    private void setCircleColor(int color) {

        View circle = this.getView().findViewById(R.id.circleView);
        Drawable background = circle.getBackground();

        Drawable wrappedDrawable = DrawableCompat.wrap(background);
        DrawableCompat.setTint(wrappedDrawable, color);
    }

    // ------ INTERFACES ------

    public interface DayViewContainerListener {
        public void selected(LocalDate date);

        public void selectedInOrOutDate(LocalDate date);
    }

}