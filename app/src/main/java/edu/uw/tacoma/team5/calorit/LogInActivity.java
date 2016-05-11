package edu.uw.tacoma.team5.calorit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

/**
 * This is the activity that holds signin and signup fragment
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/04
 */
public class LogInActivity extends AppCompatActivity {
    /**
     * This is a URL used to communicate with login php file
     */
    private static final String LOGIN_URL = "http://cssgate.insttech.washington.edu/~_450atm5/login.php?";

    /**
     *  This is a URL used to communicate with signup php file
     */
    private static final String SIGNUP_URL = "http://cssgate.insttech.washington.edu/~_450atm5/signup.php?";

    /**
     * This is an intent that determines where this activity heads to when sign in or create account button is clicked
     */
    private Intent mIntent;

    /**
     * This is used to save data such as user's account or whether a user has logged in
     */
    private SharedPreferences mSharedPreferences;

    /**
     * This sets up this activity's initial UI
     *
     * @param savedInstanceState has saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mSharedPreferences = getSharedPreferences(getString(R.string.login_prefs),
                Context.MODE_PRIVATE);

        if (mSharedPreferences.getBoolean(getString(R.string.loggedin), false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else if (savedInstanceState == null){
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction().add(R.id.login_activity,
                            new SignInFragment()).commit();
                }
            }, 3000);
        }
    }

    /**
     * This allows user to shift from the sign in fragment to sign up fragment when sign up button
     * is clicked.
     */
    public void switchToSignUpFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.login_activity,
                new SignUpFragment()).addToBackStack(null).commit();
    }

    /**
     * This allows user to sign in
     *
     * @param email is user's email
     * @param password is user's password
     */
    public void signIn(String email, String password) {
        if (isConnectedToNetwork() && credentialCheck(email, password)) {
            mIntent = new Intent(this, HomeActivity.class);
            AccountTask task = new AccountTask();
            task.execute(buildURL(email, password, LOGIN_URL));
        }
    }

    /**
     * This allows user to sign up an account
     *
     * @param email is user's email
     * @param password is user's password
     */
    public void signUp(String email, String password) {
        if (isConnectedToNetwork() && credentialCheck(email, password)) {
            mIntent = new Intent(this, BodyInfoActivity.class);
            AccountTask task = new AccountTask();
            task.execute(buildURL(email, password, SIGNUP_URL));
        }
    }

    /**
     * This builds a URL that is used in the async task
     *
     * @param email is user's email
     * @param password is user's password
     * @param url is one of the url constants
     * @return a url query with the email and password
     */
    private String buildURL(String email, String password, String url) {
        StringBuilder query = new StringBuilder(url);

        try {
            query.append("email=");
            query.append(URLEncoder.encode(email, "UTF-8"));

            query.append("&pwd=");
            query.append(URLEncoder.encode(password, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return query.toString();
    }

    /**
     * Checks whether or not this app connects to the Internet
     *
     * @return true if it connects to the Internet. Otherwise, false
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
     * Checks whether or not user's inputs are valid
     *
     * @param email is user's email
     * @param password is user's password
     * @return true if everything is valid. Otherwise, false
     */
    private boolean credentialCheck(String email, String password) {
        boolean result = true;

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email.", Toast.LENGTH_LONG).show();
            result = false;
        } else if (!email.contains("@")) {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_LONG).show();
            result = false;
        } else if (email.length() > 40) {
            Toast.makeText(this, "Please enter a valid email (less than fourty characters).",
                    Toast.LENGTH_LONG).show();
            result = false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password.", Toast.LENGTH_LONG).show();
            result = false;
        } else if (password.length() > 20) {
            Toast.makeText(this, "Please enter a valid password (less than 20 characters).",
                    Toast.LENGTH_LONG).show();
            result = false;
        } else if (password.length() < 10) {
            Toast.makeText(this, "Please enter a valid password (more than 10 characters).",
                    Toast.LENGTH_LONG).show();
            result = false;
        }

        return result;
    }

    /**
     * This is a class used to communicate with the server
     */
    private class AccountTask extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(LogInActivity.this, "Please wait", "Processing", true, false);
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
                    response = "Unable to log in, Reason: " + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }

            return response;
        }

        /**
         * This shows message from the server and switches to another activity from this activity
         *
         * @param result is result from doInBackground()
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
                    mSharedPreferences.edit().putBoolean(getString(R.string.loggedin), true).commit();
                    mSharedPreferences.edit().putString(getString(R.string.loggedin_email),
                            jsonObject.get("email").toString()).commit();
                    startActivity(mIntent);
                    LogInActivity.this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed! " + jsonObject.get("error")
                            , Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }

            dialog.dismiss();
        }
    }
}
