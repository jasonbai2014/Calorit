package edu.uw.tacoma.team5.calorit;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import edu.uw.tacoma.team5.calorit.MealActivity;
import edu.uw.tacoma.team5.calorit.MealFragment;
import edu.uw.tacoma.team5.calorit.R;
import edu.uw.tacoma.team5.calorit.data.MealLogDB;
import edu.uw.tacoma.team5.calorit.model.FoodItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    public static final String MEAL_DATA_KEY = "edu.uw.tacoma.team5.calorit_MEAL_DATA_KEY";

    private static final String MEAL_LOG_URL = "http://cssgate.insttech.washington.edu/~_450atm5/updateMealLog.php?";

    private SharedPreferences mSharedPreferences;

    private String mCurrentUser;

    private List<FoodItem> mSelectedFoods;

    private List<Integer> mSelectedAmounts;

    private String mDate;

    private int mCaloriesBurned;

    private int mCaloriesConsumed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.login_prefs) ,
                Context.MODE_PRIVATE);
        mCurrentUser = mSharedPreferences.getString(getString(R.string.loggedin_email), null);
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
                if (isConnectedToNetwork()) {
                    mCaloriesConsumed = calculateSelectedFoodCalories();

                    if (checkBox.isChecked()) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setMessage("Enter your burned calories");

                        final EditText text = new EditText(dialog.getContext());
                        text.setInputType(InputType.TYPE_CLASS_NUMBER);
                        text.setMaxLines(1);

                        dialog.setView(text, 70, 60, 70, 20);

                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int amount = Integer.valueOf(text.getText().toString());
                                    mCaloriesBurned = amount;
                                    UpdateMealLogTask task = new UpdateMealLogTask();
                                    task.execute(buildURL(MEAL_LOG_URL));
                                } catch (NumberFormatException e) {
                                    Toast.makeText(getActivity(), "Please enter a valid number",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        dialog.show();
                    } else {
                        UpdateMealLogTask task = new UpdateMealLogTask();
                        task.execute(buildURL(MEAL_LOG_URL));
                    }
                }
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

    private int calculateSelectedFoodCalories() {
        double result = 0;

        for (int i = 0; i < mSelectedFoods.size(); i++) {
            result += mSelectedFoods.get(i).getmCalories() * (mSelectedAmounts.get(i) / 100.0);
        }

        return (int) result;
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
            mDate = format.format(c.getTime());
            query.append(URLEncoder.encode(mDate, "UTF-8"));
            query.append("&caloriesConsumed=");
            query.append(URLEncoder.encode(String.valueOf(mCaloriesConsumed), "UTF-8"));
            query.append("&caloriesBurned=");
            query.append(URLEncoder.encode(String.valueOf(mCaloriesBurned), "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return query.toString();
    }

    /**
     * Class used for starting an asynchronous task to communicate with the server. This will allow
     * the user to keep using the app without having to wait for a potentially slow network connection.
     */
    private class UpdateMealLogTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(), "Please wait", "Processing", true, false);
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to send body info, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }

            return response;
        }

        /**
         * This pops up a toast showing whether or not user's meal log has been saved in the server.
         *
         * @param result is a string from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG)
                        .show();
                dialog.dismiss();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                int calConsumed = Integer.valueOf((String) jsonObject.get("calConsumed"));
                int calBurned = Integer.valueOf((String) jsonObject.get("calBurned"));

                if (status.equals("success")) {
                    Toast.makeText(getActivity(), jsonObject.get("message").toString()
                            , Toast.LENGTH_LONG).show();

                    MealLogDB db = new MealLogDB(getActivity());
                    db.updateMealLog(mDate, String.valueOf(calConsumed), String.valueOf(calBurned), mCurrentUser);

                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra(MEAL_DATA_KEY, calConsumed - calBurned);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Failed! " + jsonObject.get("error")
                            , Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Something wrong with the data " +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }

            dialog.dismiss();
        }
    }

}
