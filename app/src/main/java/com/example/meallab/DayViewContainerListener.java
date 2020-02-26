package com.example.meallab;

import org.threeten.bp.LocalDate;

public interface DayViewContainerListener {
    public void selected(LocalDate date);

    public void selectedInOrOutDate(LocalDate date);
}
