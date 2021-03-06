package edu.uw.tacoma.team5.calorit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.data.BodyInfoDB;
import edu.uw.tacoma.team5.calorit.model.BodyInfo;

/**
 * This class handles the interaction of editing or entering body information about the user.
 * The user can enter all the information asked for and if it is their first time (ie. they just
 * created an account) then it will insert a new row in the database. If they are editing their
 * information it will update their row in the database.
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/04
 */
public class BodyInfoActivity extends AppCompatActivity {
    /**
     * String representation of the server's php file to interact with the BodyInfo table in the database.
     */
    private static final String BODY_INFO_URL = "http://cssgate.insttech.washington.edu/~_450atm5/bodyinfo.php?";

    /**
     * Button used to submit the user's information to the database.
     */
    private Button mSaveBodyInfoButton;

    /**
     * These are spinners used to get user's body information
     */
    private Spinner mHeightFeetSpinner, mHeightInchesSpinner, mWeightSpinner, mAgeSpinner;

    /**
     * This is a radio button for user's gender information
     */
    private RadioGroup mGenderRadioGroup;

    /**
     * Integer fields used to store the user's information that they enter.
     */
    private int mHeightFeet, mHeightInches, mWeight, mAge, mBmr;

    /**
     * String field to store the user's gender information that they enter.
     */
    private String mGender;

    /**
     * Intent object used to move between relevant acitivities.
     */
    private Intent mIntent;

    /**
     * SharedPreferences object used to get the current user's email address to build a URL.
     */
    private SharedPreferences mSharedPreferences;

    /**
     * This is user's email address
     */
    private String mCurrentUser;

    /**
     * Assigns all the UI elements to their relative fields in the class. Also sets onClickListener
     * for the save button to begin the process of saving body info.
     *
     * @param savedInstanceState is a bundle used to pass through the saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_info);

        mSharedPreferences = getSharedPreferences(getString(R.string.login_prefs),
                Context.MODE_PRIVATE);
        mCurrentUser = mSharedPreferences.getString(getString(R.string.loggedin_email), null);

        mHeightFeetSpinner = (Spinner) findViewById(R.id.height_ft_spinner);
        mHeightFeetSpinner.setAdapter(createArrayAdapter(7));
        mHeightInchesSpinner = (Spinner) findViewById(R.id.height_in_spinner);
        mHeightInchesSpinner.setAdapter(createArrayAdapter(11));
        mWeightSpinner = (Spinner) findViewById(R.id.weight_spinner);
        mWeightSpinner.setAdapter(createArrayAdapter(300));
        mAgeSpinner = (Spinner) findViewById(R.id.age_spinner);
        mAgeSpinner.setAdapter(createArrayAdapter(100));

        mGenderRadioGroup = (RadioGroup) findViewById(R.id.gender_radio_group);

        mSaveBodyInfoButton = (Button) findViewById(R.id.save_body_info_button);
        mSaveBodyInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSaveBodyInfo();
            }
        });
    }

    /**
     * This creates an array adapter for the spinners
     *
     * @param num determines how many numbers are in the adapter
     * @return array adapter
     */
    private ArrayAdapter createArrayAdapter(int num) {
        List<Integer> list = new ArrayList<Integer>();

        for (int i = 1; i <= num; i++) {
            list.add(i);
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, list);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        return adapter;
    }

    /**
     * This gets selected number from the spinners
     *
     * @param spinner is spinner
     * @return an integer selected by user
     */
    private int parseSpinnerData(Spinner spinner) {
        TextView view = (TextView) spinner.getSelectedView();
        return Integer.valueOf(view.getText().toString());
    }

    /**
     * Calculates the user's bmr, and sends the user back to the HomeActivity. Also starts the
     * background task for updating the database and builds a URL to do so.
     */
    public void processSaveBodyInfo(){

        if(isConnectedToNetwork()){
            mHeightFeet = parseSpinnerData(mHeightFeetSpinner);
            mHeightInches = parseSpinnerData(mHeightInchesSpinner);
            mWeight = parseSpinnerData(mWeightSpinner);
            mAge = parseSpinnerData(mAgeSpinner);
            int selectedBtnId = mGenderRadioGroup.getCheckedRadioButtonId();
            mGender = ((RadioButton) findViewById(selectedBtnId)).getText().toString();

            if (mGender.equalsIgnoreCase("F")) {
                mBmr = (int) (655 + 4.35 * mWeight + 4.7 * (mHeightFeet * 12 + mHeightInches) - 4.7 * mAge);
             } else {
                mBmr = (int) (66 + 6.23 * mWeight + 12.7 * (mHeightFeet * 12 + mHeightInches) - 6.8 * mAge);
            }

            BodyInfoDB infoDB = new BodyInfoDB(this);
            infoDB.upsertBodyInfo(mCurrentUser, mHeightFeet, mHeightInches, mWeight, mAge, mGender, mBmr);
            mIntent = new Intent(this, HomeActivity.class);
            UpdateBodyInfoTask task = new UpdateBodyInfoTask();
            task.execute(buildURL(BODY_INFO_URL));
            infoDB.closeDB();
        }
    }

    /**
     * Checks to see if there is a network connection. If not there is a toast message that pops
     * up saying so.
     *
     * @returns true if there is a network connection, false otherwise.
     */
    private boolean isConnectedToNetwork() {
        boolean result = false;

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            result = true;
        } else {
            Toast.makeText(this, "No network connection available. Cannot provide services",
                    Toast.LENGTH_LONG).show();
        }

        return result;
    }

    /**
     * Appends the body information to the end of the url that is passed in.
     *
     * @param url is the location of the php file on the server that is used to interact with the database.
     * @returns String representation of the URL with the appended information.
     */
    private String buildURL(String url) {
        StringBuilder query = new StringBuilder(url);

        try {
            query.append("email=");
            query.append(URLEncoder.encode(mSharedPreferences.getString("loggedin_email", null), "UTF-8"));
            query.append("&heightFeet=");
            query.append(URLEncoder.encode(String.valueOf(mHeightFeet), "UTF-8"));
            query.append("&heightInches=");
            query.append(URLEncoder.encode(String.valueOf(mHeightInches), "UTF-8"));
            query.append("&weight=");
            query.append(URLEncoder.encode(String.valueOf(mWeight), "UTF-8"));
            query.append("&age=");
            query.append(URLEncoder.encode(String.valueOf(mAge), "UTF-8"));
            query.append("&gender=");
            query.append(URLEncoder.encode(String.valueOf(mGender), "UTF-8"));
            query.append("&bmr=");
            query.append(URLEncoder.encode(String.valueOf(mBmr), "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return query.toString();
    }

    /**
     * Class used for starting an asynchronous task to communicate with the server. This will allow
     * the user to keep using the app without having to wait for a potentially slow network connection.
     */
    private class UpdateBodyInfoTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(BodyInfoActivity.this, "Please wait", "Processing", true, false);
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
         * This pops up a toast showing whether or not user's body info has been saved in the server.
         *
         * @param result is a string from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Unable to")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");

                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), jsonObject.get("message").toString()
                            , Toast.LENGTH_LONG).show();
                    startActivity(mIntent);
                    BodyInfoActivity.this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed! " + jsonObject.get("error")
                            , Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data " +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }

            dialog.dismiss();
        }
    }

}
