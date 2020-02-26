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

import com.example.meallab.Spoonacular.RecipeEquipment;
import com.example.meallab.Spoonacular.RecipeIngredient;
import com.example.meallab.Spoonacular.RecipeStepIngredient;

import java.util.ArrayList;

public class RecyclerViewAdapterIngredients extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecyclervToggle";

    private ArrayList<Object> mItems = new ArrayList<>();
    private ArrayList<String> mIngredientQuantities = new ArrayList<>();
    private Context mContext;
    SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    int count = 0;
    public boolean firstEquipment = true;


    public RecyclerViewAdapterIngredients(Context mContext, ArrayList<Object> mItems){

        this.mItems = mItems;
        this.mContext = mContext;
        this.sharedPreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);

    }

    public RecyclerViewAdapterIngredients(Context mContext, ArrayList<Object> mItems, ArrayList<String> mIngredientQuantities) {

        this.mIngredientQuantities = mIngredientQuantities;
        this.mItems = mItems;
        this.mContext = mContext;
        this.sharedPreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_shopping_item,parent,false);
            ViewHolder holder = new ViewHolder(view);
            return (ViewHolder) holder;
        } if(viewType==2){
            if (firstEquipment) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_first_equipment,parent,false);
                ViewHolder1 holder = new ViewHolder1(view);
                firstEquipment = false;
                return (ViewHolder1) holder;

            }else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_equipment,parent,false);
                ViewHolder1 holder = new ViewHolder1(view);
                return (ViewHolder1) holder;
            }

        } else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_equipment,parent,false);
            ViewHolder1 holder = new ViewHolder1(view);
            return(ViewHolder1) holder;
        }


    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        if(viewType == 1){
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.ingredientQuantity.setText(mIngredientQuantities.get(position));
            viewHolder.ingredientName.setText((String) mItems.get(position));

        } else if(viewType==2){

            ViewHolder1 viewHolder = (ViewHolder1) holder;
            RecipeEquipment equipment = (RecipeEquipment) mItems.get(position);
            viewHolder.equipmentName.setText(equipment.name);
        } else{

            ViewHolder1 viewHolder = (ViewHolder1) holder;
            Object obj = mItems.get(position);
            if(obj instanceof RecipeStepIngredient){
                RecipeStepIngredient ingredient = (RecipeStepIngredient) obj;
                viewHolder.equipmentName.setText(ingredient.name);
            } else {
                RecipeEquipment equipment = (RecipeEquipment) obj;
                viewHolder.equipmentName.setText(equipment.name);
            }
        }




    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mIngredientQuantities.size()==0){
            return 3;
        } else if (!(mItems.get(position) instanceof RecipeEquipment)) {
            //return 1 if it involves an ingredient
            return 1;
        } else {
            //return 2 if it involves a piece of equipment
            return 2;
        }

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

    public class ViewHolder1 extends RecyclerView.ViewHolder{

        public TextView equipmentName;
        public RelativeLayout parentLayout;

        public ViewHolder1(@NonNull View itemView){
            super(itemView);
            equipmentName = itemView.findViewById(R.id.list_equipment);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


}
