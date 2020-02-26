package com.example.meallab;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meallab.Spoonacular.Recipe;
import com.example.meallab.Spoonacular.RecipeEquipment;
import com.example.meallab.Spoonacular.RecipeIngredient;
import com.example.meallab.Spoonacular.RecipeStep;

import java.util.ArrayList;
import java.util.Arrays;

public class RecyclerViewAdapterRecipe extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecyclervToggle";

    //the object at index 0 should be an instance of Recipe.class, anything after that will be a recipeStep.class instance
    private ArrayList<Object> mObjects = new ArrayList<>();
    private Context mContext;
    SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    int count = 0;

    public RecyclerViewAdapterRecipe(Context mContext, ArrayList<Object> mObjects) {
        System.out.println("in constructor");
        this.mObjects = mObjects;
        this.mContext = mContext;
        this.sharedPreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType==0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_image,parent,false);
            return (ViewHolder0) new ViewHolder0(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_recipe_overview, parent, false);
            return (ViewHolder1) new ViewHolder1(view);
        } else if (viewType == 2){


            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_recipe_instruction, parent, false);
            return (ViewHolder2) new ViewHolder2(view);

        } else {
            return null;
        }


    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final int itemType = getItemViewType(position);
        switch (itemType) {
            case 0:
                String url = (String) mObjects.get(position);
                Glide.with(mContext)
                        .asBitmap()
                        .load(url)
                        .into(((ViewHolder0)holder).image);

                break;
            case 1:
                System.out.println(position+ " trololol");
                Recipe recipe = (Recipe) mObjects.get(position);
                System.out.println(recipe.sourceURL);
                ((ViewHolder1) holder).recipeName.setText(recipe.title);
                ((ViewHolder1) holder).recipekCalValue.setText(""+(int)Math.round(recipe.calories));
                ((ViewHolder1) holder).recipeFatsValue.setText(""+(int)Math.round(recipe.fats));
                ((ViewHolder1) holder).recipeCarbsValue.setText(""+(int)Math.round(recipe.carbs));
                ((ViewHolder1) holder).recipeProtiensValue.setText(""+(int)Math.round(recipe.protein));
                ((ViewHolder1) holder).recipePrepTimeValue.setText(""+recipe.prepMinutes );
                ((ViewHolder1) holder).recipeCookTimeValue.setText(""+recipe.cookingMinutes);
                ((ViewHolder1) holder).recipeServingsValue.setText(""+recipe.servings);


                ((ViewHolder1) holder).recipeTotalTimeValue.setText(""+recipe.readyInMinutes);
                RecyclerView recyclerViewShoppingList = ((ViewHolder1) holder).ingredientList;
                ArrayList<Object> mIngredients = new ArrayList<>();
                RecipeIngredient[] ingredients = recipe.ingredients;
                ArrayList<String> mIngredientQuantities = new ArrayList<>();

                for(RecipeIngredient r : ingredients){
                    mIngredients.add(r.name);
                    String quantity = r.amountMetric + " " + r.unitLongMetric;
                    if(r.metaInformation.length != 0){
                        for(int i = 0; i < r.metaInformation.length ; i++){
                            quantity = quantity + ", " + r.metaInformation[i];
                        }
                    }
                    mIngredientQuantities.add(quantity);

                }

                RecipeEquipment[] equipment = recipe.getAllEquipment();
                for(int i = 0; i< equipment.length; i++){

                    mIngredients.add(equipment[i]);
                    mIngredientQuantities.add("");
                }


                RecyclerViewAdapterIngredients adapter = new RecyclerViewAdapterIngredients(mContext, mIngredients, mIngredientQuantities);
                recyclerViewShoppingList.setAdapter(adapter);
                recyclerViewShoppingList.setLayoutManager(new LinearLayoutManager(mContext));

                break;

            case 2:
                ViewHolder2 viewHolder1 = (ViewHolder2) holder;
                RecipeStep recipeStep =(RecipeStep) mObjects.get(position);
                System.out.println(recipeStep.stepNumber);
                ((ViewHolder2) holder).recipeInstructionNumber.setText("Step "+ String.format("%d",recipeStep.stepNumber));
                ((ViewHolder2) holder).recipeInstructionNumber.setTypeface(null, Typeface.BOLD);
                ((ViewHolder2) holder).recipeInstruction.setText(recipeStep.instructionString);
                RecyclerView itemlist = ((ViewHolder2) holder).ingredientList;
                ArrayList<Object> objects = new ArrayList<>();
                objects.addAll(Arrays.asList(recipeStep.ingredients));
                objects.addAll(Arrays.asList(recipeStep.equipment));
                RecyclerViewAdapterIngredients adapter2 = new RecyclerViewAdapterIngredients(mContext,objects);
                itemlist.setAdapter(adapter2);
                itemlist.setLayoutManager(new LinearLayoutManager(mContext));



                break;


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }else if(position==1){
            return 1;
        }
        else {
            return 2;
        }

    }


    @Override
    public int getItemCount() {
        return mObjects.size();
    }



    public class ViewHolder0 extends RecyclerView.ViewHolder{
        public ImageView image;
        public RelativeLayout parentLayout;
        public View itemView;

        public ViewHolder0 (@NonNull View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.imageView2);
            this.itemView = itemView;
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {


        public TextView recipeName;
        public TextView recipekCalValue;
        public TextView recipeFatsValue;
        public TextView recipeCarbsValue;
        public TextView recipeProtiensValue;
        public TextView recipePrepTimeValue;
        public TextView recipeCookTimeValue;
        public TextView recipeServingsValue;
        public TextView recipeTotalTimeValue;
        public RecyclerView ingredientList;

        public RelativeLayout parentLayout;
        public View itemView;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            recipeName = itemView.findViewById(R.id.recipeName);
            recipekCalValue = itemView.findViewById(R.id.recipekCalValue);
            recipeFatsValue = itemView.findViewById(R.id.recipeFatsValue);
            recipeCarbsValue = itemView.findViewById(R.id.recipeCarbsValue);
            recipeProtiensValue = itemView.findViewById(R.id.recipeProtiensValue);
            recipePrepTimeValue = itemView.findViewById(R.id.recipePrepTimeValue);
            recipeCookTimeValue = itemView.findViewById(R.id.recipeCookTimeValue);
            recipeServingsValue = itemView.findViewById(R.id.recipeServingsValue);
            recipeTotalTimeValue = itemView.findViewById(R.id.recipeTotalTimeValue);
            ingredientList = itemView.findViewById(R.id.recyclerv_view_shopping_list_recipe_overview);


            parentLayout = itemView.findViewById(R.id.parent_layout);


        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        public TextView recipeInstructionNumber;
        public TextView recipeInstruction;
        public RecyclerView ingredientList;
        public View itemView;
        public RelativeLayout parentLayout;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            recipeInstructionNumber = itemView.findViewById(R.id.recipeInstructionNumber);
            recipeInstruction = itemView.findViewById(R.id.recipeInstruction);
            ingredientList = itemView.findViewById(R.id.recyclerv_view_shopping_list_recipe_step);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }

    }


}




