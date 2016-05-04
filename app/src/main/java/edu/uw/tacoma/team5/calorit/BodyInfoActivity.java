package edu.uw.tacoma.team5.calorit;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BodyInfoActivity extends AppCompatActivity {

    private static final String BODY_INFO_URL = "http://cssgate.insttech.washington.edu/~_450atm5/bodyinfo.php?";
    private EditText mHeightFeetEditText, mHeightInchesEditText, mWeightEditText,
            mAgeEditText, mGenderEditText, mBMREditText;
    private Button mSaveBodyInfoButton;
    private int mHeightFeet, mHeightInches, mWeight, mAge, mBmr;
    private String mGender;
    private Intent mIntent;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_info);

        mSharedPreferences = getSharedPreferences(getString(R.string.login_prefs),
                Context.MODE_PRIVATE);

        mHeightFeetEditText = (EditText) findViewById(R.id.height_feet_edit_text);
        mHeightInchesEditText = (EditText) findViewById(R.id.height_inches_edit_text);
        mWeightEditText = (EditText) findViewById(R.id.weight_edit_text);
        mAgeEditText = (EditText) findViewById(R.id.age_edit_text);
        mGenderEditText = (EditText) findViewById(R.id.gender_edit_text);
        mBMREditText = (EditText) findViewById(R.id.bmr_edit_text);

        mSaveBodyInfoButton = (Button) findViewById(R.id.save_body_info_button);

        mSaveBodyInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSaveBodyInfo();
            }
        });
    }

    public void gatherInformation(){
        if(validateInput()) {
            mHeightFeet = Integer.parseInt(mHeightFeetEditText.getText().toString());
            mHeightInches = Integer.parseInt(mHeightInchesEditText.getText().toString());
            mWeight = Integer.parseInt(mWeightEditText.getText().toString());
            mAge = Integer.parseInt(mAgeEditText.getText().toString());
            mBmr = Integer.parseInt(mBMREditText.getText().toString());
            mGender = mGenderEditText.getText().toString();
        } else{
            Toast.makeText(this, "Something wrong with input",
                    Toast.LENGTH_LONG).show();
        }
    }


    private boolean validateInput(){
        if(mHeightFeetEditText.getText().toString().equals("") ||
                mHeightInchesEditText.getText().toString().equals("")||
                mWeightEditText.getText().toString().equals("")||
                mAgeEditText.getText().toString().equals("")||
                mGenderEditText.getText().toString().equals("")||
                mBMREditText.getText().toString().equals("")){
            return false;
        }

        try {

            Integer.parseInt(mHeightFeetEditText.getText().toString());
            Integer.parseInt(mHeightInchesEditText.getText().toString());
            Integer.parseInt(mWeightEditText.getText().toString());
            Integer.parseInt(mAgeEditText.getText().toString());
            Integer.parseInt(mBMREditText.getText().toString());
            String temp = mGenderEditText.getText().toString();

            if(!temp.equalsIgnoreCase("m") && !temp.equalsIgnoreCase("f") &&
                    !temp.equalsIgnoreCase("male") && !temp.equalsIgnoreCase("female")){
                throw new Exception();
            }
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public void processSaveBodyInfo(){
        gatherInformation();
        if(isConnectedToNetwork() && validateInput()){

            BodyInfoTask task = new BodyInfoTask();
            task.execute(buildURL(BODY_INFO_URL));
            mIntent = new Intent(this, HomeActivity.class);
        }
    }

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


    private class BodyInfoTask extends AsyncTask<String, Void, String> {

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

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            Log.v("string result:", result);

            Toast.makeText(getApplicationContext(), "Body information saved"
                    , Toast.LENGTH_LONG).show();
            startActivity(mIntent);
            BodyInfoActivity.this.finish();

        }
    }

}
