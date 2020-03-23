package com.example.meallab.stored_data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Class that allows for the storage of shopping lists.
 */
public class StoredShoppingList implements Parcelable {
    /**
     * The date of this shopping list.
     */
    Date date;
    /**
     * The items on this shopping list.
     */
    StoredShoppingItem[] items;

    // ---- Parcelable ----

    protected StoredShoppingList(Parcel in) {
        date = new Date(in.readLong());
        items = (StoredShoppingItem[]) in.readArray(StoredShoppingItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date.getTime());
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
