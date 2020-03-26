package com.example.meallab.storing_data;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.LocalDate;

/**
 * Class that allows for the storage of shopping lists.
 */
public class StoredShoppingList implements Parcelable {
    /**
     * The date of this shopping list.
     */
    public LocalDate date;
    /**
     * The items on this shopping list.
     */
    public StoredShoppingItem[] items;

    public StoredShoppingList(LocalDate date) {
        this.date = date;
        this.items = new StoredShoppingItem[0];
    }
    // ---- Parcelable ----

    protected StoredShoppingList(Parcel in) {
        date = (LocalDate) in.readSerializable();
        items = (StoredShoppingItem[]) in.readArray(StoredShoppingItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(date);
        dest.writeArray(items);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StoredShoppingList> CREATOR = new Parcelable.Creator<StoredShoppingList>() {
        @Override
        public StoredShoppingList createFromParcel(Parcel in) {
            return new StoredShoppingList(in);
        }

        @Override
        public StoredShoppingList[] newArray(int size) {
            return new StoredShoppingList[size];
        }
    };
}
