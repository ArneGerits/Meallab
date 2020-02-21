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
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private boolean[] Choices;
    private Context mContext;
    SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    int count = 0;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImageNames) {
        this.mImageNames = mImageNames;
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








        holder.image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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

        holder.imageName.setText(mImageNames.get(position));
        holder.image.setChecked(sharedPreferences.getBoolean(mImageNames.get(position),false));
        System.out.println(mImageNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick : clicked on : " + mImageNames.get(position));

                Toast.makeText(mContext,mImageNames.get(position),Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //CircleImageView image;
        //TO BE CORRECTED: naam is nog image, dit zou moeten worden togglebutton of iets dergelijks
        public ToggleButton image;
        public TextView imageName;
        public RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.toggle);
            imageName = itemView.findViewById(R.id.toggle_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);



        }
    }


}
