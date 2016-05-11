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
import android.widget.Button;
import android.widget.EditText;
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
     * EditTexts used in the UI for the user to enter information about their body.
     */
    private EditText mHeightFeetEditText, mHeightInchesEditText, mWeightEditText,
            mAgeEditText, mGenderEditText;

    /**
     * Button used to submit the user's information to the database.
     */
    private Button mSaveBodyInfoButton;

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
        mHeightFeetEditText = (EditText) findViewById(R.id.height_feet_edit_text);
        mHeightInchesEditText = (EditText) findViewById(R.id.height_inches_edit_text);
        mWeightEditText = (EditText) findViewById(R.id.weight_edit_text);
        mAgeEditText = (EditText) findViewById(R.id.age_edit_text);
        mGenderEditText = (EditText) findViewById(R.id.gender_edit_text);

        mSaveBodyInfoButton = (Button) findViewById(R.id.save_body_info_button);

        mSaveBodyInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSaveBodyInfo();
            }
        });
    }


    /**
     * Checks to make sure that the input entered is the same type that was expected and that they
     * are within the acceptable ranges. If there is something wrong an error message will pop up
     * in a toast saying so.
     *
     * @returns true if input is same type as expected and within accepted ranges. False otherwise.
     */
    private boolean isInputValid() {
        boolean result = true;

        try {
            mHeightFeet = Integer.parseInt(mHeightFeetEditText.getText().toString());
            mHeightInches = Integer.parseInt(mHeightInchesEditText.getText().toString());
            mWeight = Integer.parseInt(mWeightEditText.getText().toString());
            mAge = Integer.parseInt(mAgeEditText.getText().toString());
            mGender = mGenderEditText.getText().toString();

            if (mHeightFeet < 0 || mHeightFeet > 7) {
                Toast.makeText(this, "Plase enter a valid height in feet", Toast.LENGTH_LONG).show();
                mHeightFeetEditText.requestFocus();
                result = false;
            } else if (mHeightInches < 0 || mHeightInches > 11) {
                Toast.makeText(this, "Please enter a valid height in inches", Toast.LENGTH_LONG).show();
                mHeightInchesEditText.requestFocus();
                result = false;
            } else if (mWeight <= 0) {
                Toast.makeText(this, "Please enter a positive number for weight", Toast.LENGTH_LONG).show();
                mWeightEditText.requestFocus();
                result = false;
            } else if (mAge < 0 && mAge > 100) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_LONG).show();
                mAgeEditText.requestFocus();
                result = false;
            } else if (!(mGender.equalsIgnoreCase("m") || mGender.equalsIgnoreCase("f"))) {
                Toast.makeText(this, "Please enter a valid gender", Toast.LENGTH_LONG).show();
                mGenderEditText.requestFocus();
                result = false;
            }
        } catch (NumberFormatException e) {
            result = false;
            Toast.makeText(this, "Please make sure you enter a number", Toast.LENGTH_LONG).show();
        }

        return result;
    }

    /**
     * Calculates the user's bmr, and sends the user back to the HomeActivity. Also starts the
     * background task for updating the database and builds a URL to do so.
     */
    public void processSaveBodyInfo(){

        if(isConnectedToNetwork() && isInputValid()){

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
