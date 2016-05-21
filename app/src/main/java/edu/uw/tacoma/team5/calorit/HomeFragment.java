package edu.uw.tacoma.team5.calorit;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.uw.tacoma.team5.calorit.data.BodyInfoDB;
import edu.uw.tacoma.team5.calorit.data.MealLogDB;
import edu.uw.tacoma.team5.calorit.model.BodyInfo;
import edu.uw.tacoma.team5.calorit.model.MealLog;

/**
 * This is a fragment for home UI.
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/04
 */
public class HomeFragment extends Fragment {
    /**
     * Location of php file on server for querying body info
     */
    private static final String BODY_INFO_URL = "http://cssgate.insttech.washington.edu/~_450atm5/querybodyinfo.php?";

    /**
     * This is a String for the Email subject.
     */
    private final String EMAIL_SUBJECT = "Calorit Alerts";

    /**
     * This is a String for the email message
     */
    private final String EMAIL_BODY = "You've reached your daily calorie limit!";

    /**
     * SharedPreferences object used for knowing if the user is logged in and it also has user's
     * email.
     */
    private SharedPreferences mSharedPerferences;

    /**
     * String used to store the current user's email for URL building & email sending
     */
    private String mCurrentUser;

    /**
     * TextView used to show how many calories the user has left for the day.
     */
    private TextView mCaloriesTextView;

    /**
     * Button used to enter a meal that the user has eaten or is about to eat.
     */
    private Button mEnterMealBtn;


    /**
     * Button used to access the EditBodyInfoActivity to edit the user's information.
     */
    private Button mEditBodyInfoBtn;

    /**
     * Button used to view the user's log of meals.
     */
    private Button mMealLogBtn;

    /**
     * This is a SQLite database instance used to handle body information
     */
    private BodyInfoDB mBodyInfoDB;


    /**
     * Gets the user's email and stores it in the mCurrentUser field. Assigns the fragment's UI elements to the
     * relevant fields. Also starts a background task after building a URL to download this user's body information.
     * Also assigns onClickListeners to the UI buttons.
     * @param inflater used to inflate the fragment
     * @param container used to pass into the inflater
     * @param savedInstanceState is a bundle used to determine what the saved instance state is.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSharedPerferences = getActivity().getSharedPreferences(getString(R.string.login_prefs),
                Context.MODE_PRIVATE);
        mCurrentUser = mSharedPerferences.getString(getString(R.string.loggedin_email), null);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mCaloriesTextView = (TextView) view.findViewById(R.id.calories_textview);
        mEnterMealBtn = (Button) view.findViewById(R.id.enter_meal_btn);
        mEditBodyInfoBtn = (Button) view.findViewById(R.id.edit_body_info_btn);
        mMealLogBtn = (Button) view.findViewById(R.id.meal_log_btn);
        mBodyInfoDB = new BodyInfoDB(getActivity());

        //if the user has reached or exceeded their daily calorie limit.
        if(Integer.parseInt(mCaloriesTextView.getText().toString()) <= 0){
            sendMail(mCurrentUser, EMAIL_SUBJECT, EMAIL_BODY);
        }

        if (isConnectedToNetwork()) {
            DownloadBodyInfoTask task = new DownloadBodyInfoTask();
            task.execute(buildURL());
        } else {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            MealLog log = new MealLogDB(getActivity()).getMealLogByDate(mCurrentUser,
                    format.format(c.getTime()));
            BodyInfo info = mBodyInfoDB.getBodyInfo(mCurrentUser);

            if (log != null) {
                mCaloriesTextView.setText(info.getBmr() - log.getmCaloriesConsumed()
                        + log.getmCaloriesBurned() + " Calories");
            } else {
                mCaloriesTextView.setText(info.getBmr() + " Calories");
            }
        }

        mEnterMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).enterMeal();
            }
        });

        mEditBodyInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).editBodyInfo();

            }
        });

        mMealLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).viewMealLog();
            }
        });

        return view;
    }

    /**
     * Appends the user's email to the URL of the server's php file for querying bodyinfo table in the database.
     * @returns String representation of the URL of the server's php file with the user's email appended to the end.
     */
    private String buildURL() {
        StringBuilder query = new StringBuilder(BODY_INFO_URL);

        try {
            query.append("email=");
            query.append(URLEncoder.encode(mCurrentUser, "UTF-8"));
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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

        ConnectivityManager manager = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            result = true;
        }

        return result;
    }

    @Override
    public void onStop() {
        super.onStop();
        mBodyInfoDB.closeDB();
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("tcss450ateam5@gmail.com", "team5password");
            }
        });
    }

    private Message createMessage(String email, String subject, String messageBody, Session session)
            throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("tcss450ateam5@gmail.com", "Team 5"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * Class for background task of downloading body info on a different thread.
     */
    private class DownloadBodyInfoTask extends AsyncTask<String, Void, String> {
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
                    response = "Unable to download the body info, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * Saves the downloaded body information into a BodyInfo object and sets the calories remaining view
         * to the user's bmr.
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            BodyInfo info = BodyInfo.parseBodyInfoJSON(result);

            if (info == null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            } else {
                Bundle bundle = getArguments();

                if (bundle != null) {
                    int mealData = bundle.getInt(ConfirmFragment.MEAL_DATA_KEY, 0);
                    mCaloriesTextView.setText((info.getBmr() - mealData) + " Calories");
                } else {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    MealLog log = new MealLogDB(getActivity()).getMealLogByDate(mCurrentUser,
                            format.format(c.getTime()));

                    if (log != null) {
                        mCaloriesTextView.setText(info.getBmr() - log.getmCaloriesConsumed()
                                + log.getmCaloriesBurned() +  " Calories");
                    } else {
                        mCaloriesTextView.setText(info.getBmr() + " Calories");
                    }
                }

                mBodyInfoDB.upsertBodyInfo(mCurrentUser, info.getHeightFeet(),
                        info.getHeightInches(),info.getWeight(),
                        info.getAge(), info.getGender(), info.getBmr());
            }
        }
    }


    private class SendMailTask extends AsyncTask<Message, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
