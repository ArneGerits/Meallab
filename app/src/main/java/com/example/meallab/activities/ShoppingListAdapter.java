package com.example.meallab.activities;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.meallab.R;
import com.example.meallab.storing_data.StoredDay;
import com.example.meallab.storing_data.StoredRecipe;
import com.example.meallab.storing_data.StoredShoppingItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingViewHolder> {

    private ArrayList<StoredDay> days = new ArrayList<>();
    private ArrayList<ShoppingListEntry> mDataset = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ShoppingViewHolder extends RecyclerView.ViewHolder {

        // ----- Public ------

        // Contains the name of the product.
        public TextView nameTextView;
        // Contains the amount of product.
        public TextView amountTextView;
        // Changes the button appearance.
        public boolean isSelected = false;

        public TextView unitTextView;

        // ----- Private ------
        private ImageView checkImageView;
        private LinearLayout recipesHolder;

        public ShoppingViewHolder(ViewGroup v) {
            super(v);
            this.nameTextView   = v.findViewById(R.id.itemNameTextView);
            this.unitTextView   = v.findViewById(R.id.unitTextView);
            this.amountTextView = v.findViewById(R.id.amountTextView);
            this.recipesHolder  = v.findViewById(R.id.recipesLayout);
            this.checkImageView = v.findViewById(R.id.checkBoxImageView);
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;

            // TODO: Change appearance.
            if (this.isSelected) {

            } else {

            }
        }
    }
    public static class ShoppingListEntry {

        public ArrayList<StoredShoppingItem> items;

        public ShoppingListEntry(ArrayList<StoredShoppingItem> items) {
            this.items = items;
        }
        public float getTotalAmount() {
            float total = 0.0f;

            for (StoredShoppingItem i : items) {
                total += i.amount;
            }
            return total;
        }
        public String getName() {
            return items.get(0).name;
        }
        public String getUnit() {
            return items.get(0).unit;
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)

    /**
     * Creates a new shopping list adapter.
     * @param days The stored days to show data for.
     */
    public ShoppingListAdapter(ArrayList<StoredDay> days) {

        this.days = days;

        HashMap<Integer, ArrayList<StoredShoppingItem>> mapping = new HashMap<>();

        // Get every stored shopping item, and put it in a dict with the ids as keys.
        for (StoredDay d : days) {
            for (StoredRecipe r : d.recipes) {
                for (StoredShoppingItem i : r.items) {
                    if (!mapping.containsKey(i.itemID)) {
                        mapping.put(i.itemID, new ArrayList<StoredShoppingItem>());
                    }
                    ArrayList<StoredShoppingItem> a = mapping.get(i.itemID);
                    a.add(i);
                    mapping.put(i.itemID,a);
                }
            }
        }

        // Add them to the underlying dataset.
        for (ArrayList<StoredShoppingItem> i : mapping.values()) {
            mDataset.add(new ShoppingListEntry(i));
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

        // Get the data entry.
        ShoppingListEntry e = this.mDataset.get(position);

        // Set the values on the view.
        holder.amountTextView.setText(String.format("%.2f", e.getTotalAmount()));
        holder.unitTextView.setText(e.getUnit());
        holder.nameTextView.setText(e.getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
