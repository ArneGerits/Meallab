package com.example.meallab.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.example.meallab.R;

public class ShoppingItemRecipeEntryView extends ConstraintLayout {

    public TextView recipeNameTextView;
    public TextView amountTextView;
    public TextView unitTextView;
    private ImageView checkImage;

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
        amountTextView     = (TextView) findViewById(R.id.amountTextView);
        unitTextView       = (TextView) findViewById(R.id.unitTextView);
        checkImage         = (ImageView) findViewById(R.id.checkImageView);

        // Make sure the text size is scale to fit
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this.recipeNameTextView,12,15,1, TypedValue.COMPLEX_UNIT_SP);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(this.unitTextView,10,12,1, TypedValue.COMPLEX_UNIT_SP);

    }

    public void setSelected(boolean selected) {
        System.out.println("set selected");
        // TODO: Set images here.
        if (selected) {
            this.checkImage.setVisibility(View.VISIBLE);
        } else {
            this.checkImage.setVisibility(View.INVISIBLE);
        }
    }
}
