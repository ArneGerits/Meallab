

package com.example.meallab.storing_data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.meallab.Nutrients.Nutrient;

import org.threeten.bp.LocalDate;

/**
 * Allows for the storage of the following data about a single day:
 * - Recipes chosen
 * - Total nutrients
 * - Shopping list for that day.
 * - Date
 */
public class StoredDay implements Parcelable {

    /**
     * The recipe the user has(s)d chosen for this day.
     */
    public StoredRecipe[] recipes;
    /**
     * The total nutritional value for this day (macros and micros).
     */
    public Nutrient[] totalNutrients;
    /**
     * The shopping list for this day.
     */
    public StoredShoppingList shoppingList;
    /**
     * The date.
     */
    public LocalDate date;

    /**
     * Creates a new stored day for given date and nutrient goals.
     * that will be tracked.
     * @param date The date
     */
    public StoredDay(LocalDate date) {
        this.date = date;
        this.recipes = new StoredRecipe[0];
        this.totalNutrients = new Nutrient[0];
        this.shoppingList = new StoredShoppingList(date);
    }

    // ---- Parcelable ----

    protected StoredDay(Parcel in) {
        recipes = (StoredRecipe[]) in.readArray(StoredRecipe.class.getClassLoader());
        totalNutrients = (Nutrient[]) in.readArray(Nutrient.class.getClassLoader());
        shoppingList = (StoredShoppingList) in.readValue(StoredShoppingList.class.getClassLoader());
        date = (LocalDate) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(recipes);
        dest.writeValue(totalNutrients);
        dest.writeValue(shoppingList);
        dest.writeSerializable(date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StoredDay> CREATOR = new Parcelable.Creator<StoredDay>() {
        @Override
        public StoredDay createFromParcel(Parcel in) {
            return new StoredDay(in);
        }

        @Override
        public StoredDay[] newArray(int size) {
            return new StoredDay[size];
        }
    };
}
