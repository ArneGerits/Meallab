package com.example.meallab.Nutrients;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores info about a single nutritional value, e.g Fats 13g
 */
public class Nutrient implements Parcelable {

    /**
     * The name of the nutrient.
     */
    public String name;
    /**
     * The unit of measurement of the nutrient.
     */
    public String unit;
    /**
     * The amount of nutrient.
     */
    public float amount;
    /**
     * The amount of nutrient that the user should hit.
     */
    public float amountDailyTarget;

    // --- Constructors ----

    public  Nutrient() {

    }
    public  Nutrient(String name, String unit, float amount, float amountDailyTarget) {
        this.name = name;
        this.unit = unit;
        this.amount = amount;
        this.amountDailyTarget = amountDailyTarget;
    }

    // ---- Public Methods ----

    /**
     * Returns the progress
     * @return this.amount / this.amountDailyTarget
     */
    public float progressToday() {
        return this.amount / this.amountDailyTarget;
    }


    // ---- Parcelable ----

    protected Nutrient(Parcel in) {
        name = in.readString();
        unit = in.readString();
        amount = in.readFloat();
        amountDailyTarget = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(unit);
        dest.writeFloat(amount);
        dest.writeFloat(amountDailyTarget);
    }

    @SuppressWarnings("unused")
    public static final Creator<Nutrient> CREATOR = new Creator<Nutrient>() {
        @Override
        public Nutrient createFromParcel(Parcel in) {
            return new Nutrient(in);
        }

        @Override
        public Nutrient[] newArray(int size) {
            return new Nutrient[size];
        }
    };
}
