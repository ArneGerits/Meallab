package com.example.meallab.storing_data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.meallab.Spoonacular.RecipeIngredient;

/**
 * Class that allows for the storage of shopping items.
 */
public class StoredShoppingItem implements Parcelable {
    /**
     * The name of this item.
     */
    public String name;
    /**
     * The amount of this item needed.
     */
    public float amount;
    /**
     * The unit of measurement.
     */
    public String unit;
    /**
     * The Spoonacular item ID.
     */
    public int itemID;
    /**
     * True if the user has checked off this item, false otherwise.
     */
    public boolean isChecked = false;

    // Empty constructor.
    public StoredShoppingItem() {

    }

    public StoredShoppingItem(RecipeIngredient i) {
        // TODO: Swap according to locales.
        this.amount = i.amountMetric;
        this.unit   = i.unitShortMetric;
        this.itemID = i.id;
        this.name   = i.name;
    }
    protected StoredShoppingItem(Parcel in) {
        name = in.readString();
        amount = in.readFloat();
        unit = in.readString();
        itemID = in.readInt();
        isChecked = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(amount);
        dest.writeString(unit);
        dest.writeInt(itemID);
        dest.writeByte((byte) (isChecked ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StoredShoppingItem> CREATOR = new Parcelable.Creator<StoredShoppingItem>() {
        @Override
        public StoredShoppingItem createFromParcel(Parcel in) {
            return new StoredShoppingItem(in);
        }

        @Override
        public StoredShoppingItem[] newArray(int size) {
            return new StoredShoppingItem[size];
        }
    };
}