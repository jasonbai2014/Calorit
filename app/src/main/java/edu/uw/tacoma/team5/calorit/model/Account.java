package edu.uw.tacoma.team5.calorit.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * This is a model class for user account.
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
    public String getmEmail() {
        return mEmail;
    }

    /**
     * This is a setter for mEmail
     * @param mEmail is used to set email address of the account
     */
    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    /**
     * This is a getter for mPassword
     * @return password of the account
     */
    public String getmPassword() {
        return mPassword;
    }

    /**
     * This is a setter for mPassword
     * @param mPassword is used to set password of the account
     */
    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

//    /**
//     * This parses a given json string and fills the list with the accounts parsed from the string.
//     * @param accountJSON is the given JSON string
//     * @param accountList is the list to save the accounts from the JSON string
//     */
//    public static void parseAccountJSON(String accountJSON, List<Account> accountList) {
//        String reason = null;
//
//        if (accountJSON != null) {
//            try {
//                JSONArray array = new JSONArray(accountJSON);
//
//                for (int i = 0; i < array.length(); i++) {
//                    accountList.add(new Account(array.get(i)));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
