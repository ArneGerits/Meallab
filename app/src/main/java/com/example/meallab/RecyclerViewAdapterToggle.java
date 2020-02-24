package com.example.meallab;

import android.content.Context;
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
import android.content.SharedPreferences;

import java.util.ArrayList;

public class RecyclerViewAdapterToggle extends RecyclerView.Adapter<RecyclerViewAdapterToggle.ViewHolder>{

    private static final String TAG = "RecyclervToggle";

    private ArrayList<String> mToggleNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private boolean[] Choices;
    private Context mContext;
    SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    int count = 0;

    public RecyclerViewAdapterToggle(Context mContext, ArrayList<String> mToggleNames) {
        this.mToggleNames = mToggleNames;
        this.mContext = mContext;
        this.Choices = new boolean[getItemCount()];
        this.sharedPreferences = mContext.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_toggle,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public boolean[] getChoices(){
        return this.Choices;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");








        holder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                System.out.println("position:" + position);
                Choices[position] = isChecked;
                System.out.println("position "+ position + "  changed into " + isChecked);
                button = (ToggleButton)button;
                if(isChecked)
                {
                    System.out.println("is checked");
                }
                else
                {
                    System.out.println("not checked");
                }
            }
        });

        holder.toggleName.setText(mToggleNames.get(position));
        holder.toggle.setChecked(sharedPreferences.getBoolean(mToggleNames.get(position),false));
        System.out.println(mToggleNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick : clicked on : " + mToggleNames.get(position));

                Toast.makeText(mContext, mToggleNames.get(position),Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return mToggleNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //CircleImageView ingredientName;
        //TO BE CORRECTED: naam is nog ingredientName, dit zou moeten worden togglebutton of iets dergelijks
        public ToggleButton toggle;
        public TextView toggleName;
        public RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            toggle = itemView.findViewById(R.id.toggle);
            toggleName = itemView.findViewById(R.id.toggle_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);



        }
    }


}
