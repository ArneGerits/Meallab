package com.example.meallab.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meallab.R;

import org.w3c.dom.Text;

/**
 * Allows the user to select combinations of 7 dates.
 */
public class DateSelectionFragment extends Fragment {

    private String[] dayNames;
    private String[] dayNumbers;
    private boolean[] structure;

    private boolean notSet = false;

    Drawable numberBackground;

    int[] idsDayNumbers;

    DateSelectionListener mListener;

    public DateSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Sets the values on the fragment.
     * @param dayNames The names of the days, (Sun, Mon etc...) array length 7
     * @param dayNumbers The day numbers, (23,24,25, etc...) array length 7
     * @param structure Boolean array of length 7, set true to mark, false otherwise.
     */
    public void setValues(String[] dayNames, String[] dayNumbers, boolean[] structure) {
        this.dayNames = dayNames;
        this.dayNumbers = dayNumbers;
        this.structure = structure;

        if (this.getView() != null) {
            this.loadAllViews(this.getView());
        } else {
            this.notSet = true;
        }
    }
    public void setListener(DateSelectionListener l) {
        this.mListener = l;
    }
    // Loads all data into the views.
    private void loadAllViews(View v) {
        // Initialize the day names.
        int[] idsDayNames = new int[]{R.id.first,
                R.id.second,
                R.id.third,
                R.id.fourth,
                R.id.fifth,
                R.id.sixth,
                R.id.seventh};
        for (int i = 0; i < idsDayNames.length; i++) {
            int id = idsDayNames[i];
            String text = this.dayNames[i];

            TextView titleText = v.findViewById(id);
            titleText.setText(text);
        }

        // Initialize the day numbers.
        final int[] idsDayNumbers = new int[]{R.id.textView1,
                R.id.textView2,
                R.id.textView3,
                R.id.textView4,
                R.id.textView5,
                R.id.textView6,
                R.id.textView7};
        for (int i = 0; i < idsDayNumbers.length; i++) {
            int id = idsDayNumbers[i];
            String text = this.dayNumbers[i];

            TextView numberText = v.findViewById(id);
            numberText.setText(text);

            boolean isSelected = structure[i];
            if (isSelected) {
                numberText.setBackground(this.numberBackground);
            } else {
                numberText.setBackgroundColor(Color.TRANSPARENT);
            }

            numberText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextView textView = (TextView) v;

                    for (int i = 0; i < idsDayNumbers.length; i++) {
                        int id = idsDayNumbers[i];
                        if (v.getId() == id) {
                            // The index is is i
                            structure[i] = !structure[i];

                            if (structure[i]) {
                                v.setBackground(numberBackground);
                                textView.setTextColor(Color.BLACK);
                            } else {
                                v.setBackgroundColor(Color.TRANSPARENT);
                                textView.setTextColor(Color.WHITE);
                            }
                            if (mListener != null) {
                                mListener.selectedDate(i, structure[i]);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        // Initialize the day numbers.
        this.idsDayNumbers = new int[]{R.id.textView1,
                R.id.textView2,
                R.id.textView3,
                R.id.textView4,
                R.id.textView5,
                R.id.textView6,
                R.id.textView7};
        // Get the background.
        this.numberBackground = ResourcesCompat.getDrawable(getResources(), R.drawable.white_circle, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_date_selection, container, false);

        if (notSet) {
            loadAllViews(v);
        }
        return v;
    }

    public interface DateSelectionListener {
        /**
         * Called when the state of an index is flipped (false->true and vv.)
         * @param index The index of the flip
         */
        void selectedDate(int index, boolean isSelected);

    }
}
