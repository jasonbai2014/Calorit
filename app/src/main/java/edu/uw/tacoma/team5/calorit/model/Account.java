package edu.uw.tacoma.team5.calorit.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tacoma.team5.calorit.MealLogFragment;

/**
 * This is a model class for user account table.
 * Qing Bai
 * 2016/04/24
 */
public class Account {
    /**
     * The keys that are used to get email and password of an account from JSON
     */
    public static final String EMAIL = "email", PASSWORD = "pwd";

    /**
     * email of the account
     */
    private String mEmail;

    /**
     * password of the account
     */
    private String mPassword;

    /**
     * This is a constructor of the account class
     *
     * @param mEmail is email of the account
     * @param mPassword is password of the account
     */
    public Account(String mEmail, String mPassword) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
    }

    /**
     * This is getter for mEmail
     * @return email address of the account
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * This is a setter for mEmail
     * @param mEmail is used to set email address of the account
     */
    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    /**
     * This is a getter for mPassword
     * @return password of the account
     */
    public String getPassword() {
        return mPassword;
    }

    /**
     * This is a setter for mPassword
     * @param mPassword is used to set password of the account
     */
    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    /**
     * This parses a given json string and uses it to create an account instance.
     * @param accountJSON is the given JSON string
     * @return an account instance created from the JSON string
     */
    public static Account parseAccountJSON(String accountJSON) {
        Account account = null;

        if (accountJSON != null) {
            try {
                JSONArray arr = new JSONArray(accountJSON);
                JSONObject accountJsonObj = arr.getJSONObject(0);

                account =  new Account(accountJsonObj.getString(Account.EMAIL), accountJsonObj.getString(Account.PASSWORD));
            } catch (JSONException e) {
                Log.e("Account", "Unable to parse data, Reason: " + e.getMessage());
            }
        }

        return account;
    }
}

