

package com.example.meallab.stored_data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.meallab.Nutrients.Nutrient;

import java.util.Date;

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
    StoredRecipe[] recipes;
    /**
     * The total nutritional value for this day (macros and micros).
     */
    Nutrient[] totalNutrients;
    /**
     * The shopping list for this day.
     */
    StoredShoppingList shoppingList;
    /**
     * The date.
     */
    Date date;

    // ---- Parceable ----

    protected StoredDay(Parcel in) {
        recipes = (StoredRecipe[]) in.readArray(StoredRecipe.class.getClassLoader());
        totalNutrients = (Nutrient[]) in.readArray(Nutrient.class.getClassLoader());
        shoppingList = (StoredShoppingList) in.readValue(StoredShoppingList.class.getClassLoader());
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
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
        dest.writeLong(date != null ? date.getTime() : -1L);
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
