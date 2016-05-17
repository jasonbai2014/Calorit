package edu.uw.tacoma.team5.calorit.model;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import edu.uw.tacoma.team5.calorit.MealActivity;
import edu.uw.tacoma.team5.calorit.MealFragment;
import edu.uw.tacoma.team5.calorit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    private static final String MEAL_LOG_URL = "http://cssgate.insttech.washington.edu/~_450atm5/updateMealLog.php?";

    private SharedPreferences mSharedPreferences;

    List<FoodItem> mSelectedFoods;

    List<Integer> mSelectedAmounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.login_prefs) ,
                Context.MODE_PRIVATE);
        View v = inflater.inflate(R.layout.fragment_confirm, container, false);
        LinearLayout selectedFoodContainer = (LinearLayout) v.findViewById(R.id.selected_food_container);

        mSelectedFoods = ((MealActivity) getActivity()).getSelectedFoodItems();
        mSelectedAmounts = ((MealActivity) getActivity()).getSelectedAmounts();
        TextView nameView = null;
        TextView amountView = null;
        LinearLayout foodDetailLayout = null;
        FoodItem selectedFood = null;
        int selectedAmount = 0;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.
                LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        for (int i = 0; i < mSelectedFoods.size(); i++) {
            selectedFood = mSelectedFoods.get(i);
            selectedAmount = mSelectedAmounts.get(i);

            foodDetailLayout = new LinearLayout(v.getContext(), null, android.R.attr.listSeparatorTextViewStyle);
            foodDetailLayout.setOrientation(LinearLayout.HORIZONTAL);
            foodDetailLayout.setLayoutParams(layoutParams);

            nameView = new TextView(v.getContext(), null, android.R.attr.textAppearanceListItem);
            nameView.setLayoutParams(params);
            nameView.setText(selectedFood.getmFoodName());
            nameView.setGravity(Gravity.CENTER);

            amountView = new TextView(v.getContext(), null, android.R.attr.textAppearanceListItem);
            amountView.setLayoutParams(params);
            amountView.setText(String.valueOf(selectedAmount) + " g");
            amountView.setGravity(Gravity.CENTER);

            foodDetailLayout.addView(nameView);
            foodDetailLayout.addView(amountView);
            selectedFoodContainer.addView(foodDetailLayout);
        }

        Button confirmBtn = (Button) v.findViewById(R.id.confirm_btn);
        Button undoBtn = (Button) v.findViewById(R.id.undo_btn);
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.exercise_checkbox);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    // show a dialog here
                }

                // update meal log to the server here
            }
        });

        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MealActivity) getActivity()).reset();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.meal_activity_layout, new MealFragment())
                        .commit();
            }
        });

        return v;
    }

    /**
     * Checks to see if there is a network connection. If not there is a toast message that pops
     * up saying so.
     *
     * @returns true if there is a network connection, false otherwise.
     */
    private boolean isConnectedToNetwork() {
        boolean result = false;

        ConnectivityManager manager = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            result = true;
        } else {
            Toast.makeText(getContext(), "No network connection available. Cannot upload your log",
                    Toast.LENGTH_LONG).show();
        }

        return result;
    }

    /**
     * Appends the meal log to the end of the url that is passed in.
     *
     * @param url is the location of the php file on the server that is used to interact with the database.
     * @returns String representation of the URL with the appended information.
     */
    private String buildURL(String url) {
        StringBuilder query = new StringBuilder(url);

        try {
            query.append("email=");
            query.append(URLEncoder.encode(mSharedPreferences.getString("loggedin_email", null), "UTF-8"));
            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            query.append("&logDate=");
            query.append(URLEncoder.encode(format.format(c.getTime()), "UTF-8"));
//            query.append("&caloriesConsumed=");
//            query.append(URLEncoder.encode(String.valueOf(mHeightInches), "UTF-8"));
//            query.append("&caloriesBurned=");
//            query.append(URLEncoder.encode(String.valueOf(mWeight), "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return query.toString();
    }

}
