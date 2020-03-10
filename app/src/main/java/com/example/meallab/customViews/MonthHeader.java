package com.example.meallab.customViews;

import android.view.View;
import android.widget.TextView;

import com.example.meallab.R;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;

import java.util.Locale;

public class MonthHeader extends ViewContainer {
    public MonthHeader(@NotNull View view) {
        super(view);
    }
    public void setFirstDayOfWeek(DayOfWeek dayOfWeek) {
        int[] ids = {R.id.first, R.id.second, R.id.third,
                 R.id.fourth, R.id.fifth, R.id.sixth, R.id.seventh};

        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];

            TextView tv = getView().findViewById(id);
            String text = dayOfWeek.plus((long)i).getDisplayName(TextStyle.SHORT, Locale.getDefault());
            tv.setText(text);
        }
    }
}
