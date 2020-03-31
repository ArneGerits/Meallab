package com.example.meallab.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.meallab.R;


/**
 * Shows a recipes info(image, other details) in the RecipeSelectionActivity.
 */
public class RecipeSelectionRow extends Fragment {

    // ----- Outlets -----
    ImageView recipeImage;
    ImageView selectedImageView;
    ProgressBar spinner;
    ImageView moreButton;
    TextView titleTextView;
    // -----

    private recipeInfoFragmentListener mListener;
    private boolean isSelected = false;
    private boolean hidden = false;

    private int shortAnimationDuration;

    public RecipeSelectionRow() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_recipe_info, container, false);

        this.titleTextView = root.findViewById(R.id.titleTextView);
        this.spinner = root.findViewById(R.id.spinner);
        this.selectedImageView = root.findViewById(R.id.selectedImageView);
        this.recipeImage       = root.findViewById(R.id.imageView);
        this.recipeImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selected();
            }
        });

        this.moreButton = root.findViewById(R.id.moreImageView);
        moreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                more();
            }
        });
        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Sets the title of this fragment.
     * @param title
     */
    public void setTitle(String title) {
        this.titleTextView.setText(title);
    }
    /**
     * Toggle between selected and non selected state.
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;

        // TODO: Animate?
        if (selected) {
            this.selectedImageView.setVisibility(View.VISIBLE);
        } else {
            this.selectedImageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Hides all data in the view.
     */
    public void hide(boolean showSpinner) {

        this.hidden = true;

        // Cant be selected if hidden.
        this.setSelected(false);

        ConstraintLayout l = getView().findViewById(R.id.animLayout);
        l.animate().alpha(0.0f).setDuration(this.shortAnimationDuration);

        if (showSpinner) {
            this.spinner.setVisibility(View.VISIBLE);
            this.spinner.setAlpha(0.0f);
            this.spinner.animate().alpha(1.0f).setDuration(this.shortAnimationDuration);
        }
    }

    /**
     * Shows all data in the view.
     */
    public void show() {

        this.hidden = false;

        ConstraintLayout l = getView().findViewById(R.id.animLayout);
        l.animate().alpha(1.0f).setDuration(this.shortAnimationDuration);

        this.spinner.animate().alpha(0.0f).setDuration(this.shortAnimationDuration);
    }
    /**
     * Sets the event listener for this info fragment.
     * @param listener
     */
    public void setListener(recipeInfoFragmentListener listener) {
        this.mListener = listener;
    }
    // ------ Actions --------

    // Called when user presses image view.
    private void selected() {

        // Cant become selected when hidden.
        if (this.hidden) {
            return;
        }

        setSelected(!this.isSelected);

        // Inform listener
        if (mListener != null) {
            mListener.selectedFragment(this, this.isSelected);
        }
    }

    private void more() {
        // Cant show more info if hidden.
        if (this.hidden) {
            return;
        }

        if (mListener != null) {
            mListener.moreInfoFragment(this);
        }
    }

    // --------------

    public void setImage(Bitmap image) {
        this.recipeImage.setImageBitmap(image);
        this.spinner.setVisibility(View.INVISIBLE);
    }
    public RecipeSelectionInfo detailFragment() {
        return (RecipeSelectionInfo) getChildFragmentManager().findFragmentById(R.id.detailFragment);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface recipeInfoFragmentListener {

        /**
         * Called when the user selects this fragment.
         * @param fragment The fragment the user selected.
         * @param selected True if the user selected this fragment, false if it is deselected.
         */
        void selectedFragment(RecipeSelectionRow fragment, boolean selected);

        /**
         * Called when the user wants more info for this fragment.
         * @param fragment The fragment the user wants more info of.
         */
        void moreInfoFragment(RecipeSelectionRow fragment);
    }
}
