package com.example.meallab;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterIngredients extends RecyclerView.Adapter<RecyclerViewAdapterIngredients.ViewHolder>{

    private static final String TAG = "RecyclervToggle";

    private ArrayList<String> mIngredientNames = new ArrayList<>();
    private ArrayList<String> mIngredientQuantities = new ArrayList<>();
    private Context mContext;
    SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    int count = 0;

    public RecyclerViewAdapterIngredients(Context mContext, ArrayList<String> mIngredientNames, ArrayList<String> mIngredientQuantities) {

        this.mIngredientQuantities = mIngredientQuantities;
        this.mIngredientNames = mIngredientNames;
        this.mContext = mContext;
        this.sharedPreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_shopping_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        System.out.println(position + " " + (holder == null)  + " " +(holder.ingredientQuantity == null)+ " " +(holder.ingredientName == null) + "  YOLOLOL");
        holder.ingredientQuantity.setText(mIngredientQuantities.get(position));
        holder.ingredientName.setText(mIngredientNames.get(position));



    }

    @Override
    public int getItemCount() {
        return mIngredientNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView ingredientName;
        public TextView ingredientQuantity;
        public RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientName = itemView.findViewById(R.id.list_shopping_list_item);
            ingredientQuantity = itemView.findViewById(R.id.list_shopping_list_quantity);
            parentLayout = itemView.findViewById(R.id.parent_layout);



        }
    }


}
