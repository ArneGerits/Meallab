package com.example.meallab.activities;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Used to add margin between and around cells.
 */
public class ShoppingListDecorator extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets (Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        final int margin = 15;

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = margin;
        }
        outRect.left   =  margin;
        outRect.right  = margin;
        outRect.bottom = margin;
    }
}

