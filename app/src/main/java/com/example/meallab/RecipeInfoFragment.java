package com.example.meallab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeInfoFragment.OnFragmentInteractionListener} interface
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
    }

    public void setTitle(String title) {
        TextView titleView = getView().findViewById(R.id.titleTextView);

        titleView.setText(title);
    }
    public void setSelected(boolean selected) {
        this.isSelected = selected;

        //todo: Go into selected state.
    }
    public void setListener(recipeInfoFragmentListener listener) {
        this.mListener = listener;
    }
    // ------ Actions --------

    // Called when user presses image view.
    private void selected() {
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_recipe_info, container, false);

        NetworkImageView img = root.findViewById(R.id.recipeImageView);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                selected();
            }
        });

        Button moreButton = root.findViewById(R.id.moreButton);
        moreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
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
     * Returns the network image view used by this fragment.
     * @return The network image view used by this fragment.
     */
    public NetworkImageView networkImageView() {
        return getView().findViewById(R.id.recipeImageView);
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
