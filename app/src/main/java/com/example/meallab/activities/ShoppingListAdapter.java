package com.example.meallab.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.meallab.R;
import com.example.meallab.Spoonacular.Recipe;
import com.example.meallab.customViews.ShoppingItemRecipeEntryView;
import com.example.meallab.storing_data.StoredDay;
import com.example.meallab.storing_data.StoredRecipe;
import com.example.meallab.storing_data.StoredShoppingItem;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingViewHolder> {


    private ArrayList<StoredDay> days = new ArrayList<>();
    private ArrayList<ShoppingListEntry> mDataset = new ArrayList<>();

    public enum ITEM_STATE {
        NOT_SELECTED,
        SELECTED,
        PARTIALLY_SELECTED
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ShoppingViewHolder extends RecyclerView.ViewHolder {

        // ----- Public ------

        // Contains the name of the product.
        public TextView nameTextView;
        // Contains the amount of product.
        public TextView amountTextView;
        // Changes the button appearance.
        public ITEM_STATE state;

        public TextView unitTextView;

        // ----- Private ------
        // Image view that holds checkbox.
        private ImageView checkImageView;
        // Holds recipe names.
        private LinearLayout recipesHolder;

        private ViewGroup v;

        public ShoppingViewHolder(ViewGroup v) {
            super(v);
            this.v = v;
            this.nameTextView   = v.findViewById(R.id.itemNameTextView);
            this.unitTextView   = v.findViewById(R.id.unitTextView);
            this.amountTextView = v.findViewById(R.id.amountTextView);
            this.recipesHolder  = v.findViewById(R.id.recipesLayout);
            this.checkImageView = v.findViewById(R.id.checkBoxImageView);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle selected state.
                    int location = getLayoutPosition();
                    updateData(location, state);

                    if (state == ITEM_STATE.SELECTED) {
                        setState(ITEM_STATE.NOT_SELECTED);
                    } else {
                        setState(ITEM_STATE.SELECTED);
                    }
                }
            });
        }

        // Resets view to zero state.
        public void clear() {
            this.recipesHolder.removeAllViews();
        }

        public void setState(ITEM_STATE state) {
            this.state = state;

            // TODO: Switch a lot of the view here.
            // TODO: Switch between images.
            if (state == ITEM_STATE.SELECTED) {
                this.checkImageView.setVisibility(View.VISIBLE);

            } else if(state == ITEM_STATE.NOT_SELECTED) {
                this.checkImageView.setVisibility(View.INVISIBLE);
            } else {
                // Set partial here.
            }

            // Reload all sub views.
            if (state == ITEM_STATE.SELECTED || state == ITEM_STATE.NOT_SELECTED) {
                // Loop all children of the holder.
                for (int i = 0; i < this.recipesHolder.getChildCount(); i++) {
                    ShoppingItemRecipeEntryView v = (ShoppingItemRecipeEntryView) this.recipesHolder.getChildAt(i);
                    v.setSelected(state == ITEM_STATE.SELECTED);
                }
            }
        }

        // Adds a new recipe entry.
        public void addRecipeEntry(String name, boolean isSelected, float amount, String unit) {
            ShoppingItemRecipeEntryView v = new ShoppingItemRecipeEntryView(this.v.getContext());
            v.recipeNameTextView.setText(name);
            v.unitTextView.setText(unit);
            v.amountTextView.setText(String.format("%.2f",amount));
            v.setSelected(isSelected);
            this.recipesHolder.addView(v);
        }
    }
    // Updates the data set and the stored day objects with the new data.
    private void updateData(int position, ITEM_STATE state) {
        ShoppingListEntry entry = this.mDataset.get(position);

        // Change the state of every shopping list item.
        for (StoredShoppingItem i : entry.recipeToItem.values()) {
            // There can only be 2 sates here.
            if (state == ITEM_STATE.SELECTED) {
                i.isChecked = true;
            } else if(state == ITEM_STATE.NOT_SELECTED) {
                i.isChecked = false;
            }
        }
        // TODO:? Store this to db?
        // NO -
    }
    public static class ShoppingListEntry {

        // All items
        public HashMap<String, StoredShoppingItem> recipeToItem = new HashMap<>();

        // Unit of the item.
        public String unit;
        // Name of the item.
        public String name;

        public ShoppingListEntry(int itemID, ArrayList<StoredRecipe> recipes) {

            // Get every item from the recipes.
            for (StoredRecipe r : recipes) {
                for (StoredShoppingItem i : r.items) {
                    if (i.itemID == itemID) {
                        this.recipeToItem.put(r.name, i);
                        this.name = i.name;
                        this.unit = i.unit;
                    }
                }
            }
        }
        public float getTotalAmount() {
            float total = 0.0f;

            for (StoredShoppingItem i : recipeToItem.values()) {
                total += i.amount;
            }
            return total;
        }
        // Returns the correct state of this item.
        public ITEM_STATE getState() {
            boolean onlySeenSelected    = true;
            boolean onlySeenNotSelected = true;
            for (StoredShoppingItem i : recipeToItem.values()) {
                if (i.isChecked) {
                    onlySeenNotSelected = false;
                } else {
                    onlySeenSelected = false;
                }
            }
            if (onlySeenSelected && !onlySeenNotSelected) {
                return ITEM_STATE.SELECTED;
            }
            else if (!onlySeenSelected && onlySeenNotSelected) {
                return ITEM_STATE.NOT_SELECTED;
            } else {
                return ITEM_STATE.PARTIALLY_SELECTED;
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)

    /**
     * Creates a new shopping list adapter.
     * @param days The stored days to show data for.
     */
    public ShoppingListAdapter(ArrayList<StoredDay> days) {

        this.days = days;

        HashMap<Integer, ArrayList<StoredRecipe>> mapping = new HashMap<>();

        // Every item ID gets a mapping to the recipes it belongs to.
        for (StoredDay d : days) {
            for (StoredRecipe r : d.recipes) {
                for (StoredShoppingItem i : r.items) {
                    if (!mapping.containsKey(i.itemID)) {
                        mapping.put(i.itemID, new ArrayList<StoredRecipe>());
                    }
                    ArrayList<StoredRecipe> a = mapping.get(i.itemID);
                    a.add(r);
                    mapping.put(i.itemID,a);
                }
            }
        }

        for (Integer itemID : mapping.keySet()) {
            ArrayList<StoredRecipe> r = mapping.get(itemID);
            mDataset.add(new ShoppingListEntry(itemID, r));
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ShoppingListAdapter.ShoppingViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // create a new view from a layout.
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_shopping_list_entry, parent, false);

        return new ShoppingViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ShoppingViewHolder holder, int position) {

        // Clear out all old data from the view.
        holder.clear();

        // Get the data entry.
        ShoppingListEntry e = this.mDataset.get(position);

        // Set the values on the holder.
        holder.amountTextView.setText(String.format("%.2f", e.getTotalAmount()));
        holder.unitTextView.setText(e.unit);
        holder.nameTextView.setText(e.name);

        // Set the sate and recipes on the holder.
        for (String rName : e.recipeToItem.keySet()) {
            StoredShoppingItem item = e.recipeToItem.get(rName);
            holder.addRecipeEntry(rName, item.isChecked, item.amount, item.unit);
            holder.setState(e.getState());
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
