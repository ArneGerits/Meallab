package com.example.meallab.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.meallab.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeInfoFragment.recipeInfoFragmentListener} interface
 * to handle interaction events.
 * Use the {@link RecipeInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private recipeInfoFragmentListener mListener;
    private boolean isSelected = false;

    private int shortAnimationDuration;

    private boolean hidden = false;

    public RecipeInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeInfoFragment newInstance(String param1, String param2) {
        RecipeInfoFragment fragment = new RecipeInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_recipe_info, container, false);

        ImageView img = root.findViewById(R.id.imageView);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                selected();
            }
        });

        ImageView moreButton = root.findViewById(R.id.moreImageView);
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
        TextView titleView = getView().findViewById(R.id.titleTextView);

        titleView.setText(title);
    }
    /**
     * Toggle between selected and non selected state.
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;

        ImageView s = getView().findViewById(R.id.selectedImageView);

        if (selected) {
            s.setVisibility(View.VISIBLE);
        } else {
            s.setVisibility(View.INVISIBLE);
        }
        //todo: Go into selected or deselected state.
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
            ProgressBar s = getView().findViewById(R.id.spinner);
            s.setAlpha(0.0f);
            s.animate().alpha(1.0f).setDuration(this.shortAnimationDuration);
        }
    }

    /**
     * Shows all data in the view.
     */
    public void show() {

        this.hidden = false;

        ConstraintLayout l = getView().findViewById(R.id.animLayout);
        l.animate().alpha(1.0f).setDuration(this.shortAnimationDuration);

        final ProgressBar s = getView().findViewById(R.id.spinner);
        s.animate().alpha(0.0f).setDuration(this.shortAnimationDuration);
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
        if (mListener != null) {
            mListener.moreInfoFragment(this);
        }
    }

    // --------------

    public void setImage(Bitmap image) {
        ImageView i = getView().findViewById(R.id.imageView);
        i.setImageBitmap(image);
    }
    public RecipeInfoDetailFragment detailFragment() {
        return (RecipeInfoDetailFragment) getChildFragmentManager().findFragmentById(R.id.detailFragment);
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
        void selectedFragment(RecipeInfoFragment fragment, boolean selected);

        /**
         * Called when the user wants more info for this fragment.
         * @param fragment The fragment the user wants more info of.
         */
        void moreInfoFragment(RecipeInfoFragment fragment);
    }
}
