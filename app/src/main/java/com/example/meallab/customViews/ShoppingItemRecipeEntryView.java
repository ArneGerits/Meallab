package com.example.meallab.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.meallab.R;

public class ShoppingItemRecipeEntryView extends ConstraintLayout {

    public TextView recipeNameTextView;
    public TextView amountTextView;
    public TextView unitTextView;
    public ImageView checkImage;

    public ShoppingItemRecipeEntryView(Context context) {
        super(context);
        setup(context);
    }

    public ShoppingItemRecipeEntryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);
    }

    public ShoppingItemRecipeEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    private void setup(Context c) {
        View.inflate(c, R.layout.layout_shopping_item_recipe_entry, this);

        recipeNameTextView = (TextView) findViewById(R.id.recipeName);
    }

}
