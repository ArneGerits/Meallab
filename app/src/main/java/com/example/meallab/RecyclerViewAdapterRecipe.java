package com.example.meallab;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterRecipe extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecyclervToggle";

    //the object at index 0 should be an instance of Recipe.class, anything after that will be a recipeStep.class instance
    private ArrayList<Object> mObjects = new ArrayList<>();
    private Context mContext;
    SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    int count = 0;

    public RecyclerViewAdapterRecipe(Context mContext, ArrayList<Object> mObjects) {

        this.mObjects = this.mObjects;
        this.mContext = mContext;
        this.sharedPreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_recipe_overview, parent, false);
            return (ViewHolder0) new ViewHolder0(view);
        } else if (viewType == 2){


            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_recipe_instruction, parent, false);
            return (ViewHolder1) new ViewHolder1(view);

        } else {
            return null;
        }


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final int itemType = getItemViewType(position);
        switch (itemType) {
            case 0:
                ViewHolder0 viewHolder0 = (ViewHolder0) holder;

                break;

            case 1:
                ViewHolder1 viewHolder1 = (ViewHolder1) holder;

                break;


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }

    }


    @Override
    public int getItemCount() {
        return mObjects.size();
    }


    public class ViewHolder0 extends RecyclerView.ViewHolder {


        public TextView ingredientName;
        public TextView ingredientQuantity;
        public RelativeLayout parentLayout;

        public ViewHolder0(@NonNull View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.list_shopping_list_item);
            ingredientQuantity = itemView.findViewById(R.id.list_shopping_list_quantity);
            parentLayout = itemView.findViewById(R.id.parent_layout);


        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
        }

    }


}




