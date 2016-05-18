package edu.uw.tacoma.team5.calorit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * This is a fragment UI showing a list of buttons that allow user to select different food categories.
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/17
 */
public class MealFragment extends Fragment {

    /**
     * This is a list of buttons shown in this fragment
     */
    private Button mCarbBtn, mFruitVeggieBtn, mDairyBtn, mMeatBtn, mSweetBtn, mSnackBtn, mDoneBtn;

    /**
     * This sets up listeners for the buttons
     *
     * @param inflater is inflater
     * @param container is container
     * @param savedInstanceState is savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meal, container, false);
        mCarbBtn = (Button) v.findViewById(R.id.carb_btn);
        mFruitVeggieBtn = (Button) v.findViewById(R.id.fruit_veggie_btn);
        mDairyBtn = (Button) v.findViewById(R.id.dairy_btn);
        mMeatBtn = (Button) v.findViewById(R.id.meat_btn);
        mSweetBtn = (Button) v.findViewById(R.id.sweet_btn);
        mSnackBtn = (Button) v.findViewById(R.id.snack_btn);
        mDoneBtn = (Button) v.findViewById(R.id.done_btn);

        mCarbBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MealActivity) getActivity()).categorySelected(mCarbBtn.getText().toString());
            }
        });

        mFruitVeggieBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MealActivity) getActivity()).categorySelected(mFruitVeggieBtn.getText().toString());
            }
        });

        mDairyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MealActivity) getActivity()).categorySelected(mDairyBtn.getText().toString());
            }
        });

        mMeatBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MealActivity) getActivity()).categorySelected(mMeatBtn.getText().toString());
            }
        });

        mSweetBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MealActivity) getActivity()).categorySelected(mSweetBtn.getText().toString());
            }
        });

        mSnackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MealActivity) getActivity()).categorySelected(mSnackBtn.getText().toString());
            }
        });

        mDoneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MealActivity) getActivity()).confirmFoodEntry();
            }
        });

        return v;
    }

}
