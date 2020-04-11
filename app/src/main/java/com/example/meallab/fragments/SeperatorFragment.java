package com.example.meallab.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meallab.R;

public class SeperatorFragment extends Fragment {

    TextView titleTextView;

    public SeperatorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setTitle(String title) {
        this.titleTextView.setText(title.toUpperCase());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_seperator, container, false);

        this.titleTextView = v.findViewById(R.id.nameTextView);
        return v;
    }
}
