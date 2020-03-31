package com.example.meallab;

import com.example.meallab.Spoonacular.Recipe;

import java.util.Date;

/**
 * The meal plan for a single date.
 * These objects get stored to the users device.
 */
public class DayMealPlan {
    /**
     * The recipes for this date.
     */
    Recipe[] recipes;
    /**
     * The date of this meal plan.
     */
    Date date;

    public DayMealPlan(Recipe[] recipes, Date date){
        this.recipes = recipes;
        this.date = date;
    }
}
