package com.example.meallab.storing_data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.meallab.Nutrients.Nutrient;
import com.example.meallab.Spoonacular.Recipe;
import com.example.meallab.Spoonacular.SpoonacularImageSize;
import com.example.meallab.Spoonacular.SpoonacularMealType;

/**
 * Class that allows for the storing of recipes.
 */
public class StoredRecipe implements Parcelable {
    /**
     * The name (title of this recipe)
     */
    public String name;
    /**
     * The macro nutrients in this recipe (4 objects).
     */
    public Nutrient[] nutrients;

    /**
     * The amount of minutes it takes to cook this recipe.
     */
    public int cookingMins;
    /**
     * The number of people this recipe serves.
     */
    public int numberOfServings;
    /**
     * The price per serving.
     */
    public float pricePerServing;
    /**
     * The id of the Spoonacular recipe.
     */
    public int recipeID;
    /**
     * True if the user has favorited this recipe, false otherwise.
     */
    public boolean isFavorite;

    /**
     * The image url of this recipe, Highest Quality.
     */
    public String imageURL;

    /**
     * The meal type of this recipe.
     */
    public SpoonacularMealType mealType;

    // Empty constructor.
    public StoredRecipe() {

    }

    /**
     * Creates a new stored recipe from a recipe.
     * @param r
     */
    public StoredRecipe(Recipe r) {
        this.name = r.title;
        this.recipeID = r.id;
        this.imageURL = r.getImageURLForSize(SpoonacularImageSize.S_636x393);
        this.isFavorite = false;
        this.pricePerServing = r.pricePerServing;
        this.cookingMins = r.cookingMinutes;
        this.numberOfServings = r.servings;
        this.nutrients = r.nutrients;
    }
    public Nutrient[] getMacroNutrients() {
        Nutrient[] macros = new Nutrient[4];

        for (int i = 0; i < macros.length; i++) {
            macros[i] = this.nutrients[i];
        }
        return macros;
    }
    // ---- Parcelable ----

    protected StoredRecipe(Parcel in) {
        name = in.readString();
        nutrients = (Nutrient[]) in.readArray(Nutrient.class.getClassLoader());
        cookingMins = in.readInt();
        numberOfServings = in.readInt();
        pricePerServing = in.readFloat();
        recipeID = in.readInt();
        isFavorite = in.readByte() != 0x00;
        imageURL = in.readString();
        mealType = SpoonacularMealType.valueOf(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeArray(nutrients);
        dest.writeInt(cookingMins);
        dest.writeInt(numberOfServings);
        dest.writeFloat(pricePerServing);
        dest.writeInt(recipeID);
        dest.writeByte((byte) (isFavorite ? 0x01 : 0x00));
        dest.writeString(imageURL);
        dest.writeString(mealType.getValue());
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StoredRecipe> CREATOR = new Parcelable.Creator<StoredRecipe>() {
        @Override
        public StoredRecipe createFromParcel(Parcel in) {
            return new StoredRecipe(in);
        }

        @Override
        public StoredRecipe[] newArray(int size) {
            return new StoredRecipe[size];
        }
    };
}