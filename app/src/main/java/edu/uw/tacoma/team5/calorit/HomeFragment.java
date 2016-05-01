package edu.uw.tacoma.team5.calorit;


import android.content.Context;
import android.content.SharedPreferences;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.model.BodyInfo;
import edu.uw.tacoma.team5.calorit.model.MealLog;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private static final String BODY_INFO_URL = "http://cssgate.insttech.washington.edu/~_450atm5/querybodyinfo.php?";

    private BodyInfo mBodyInfo;
    private SharedPreferences mSharedPerferences;
    private String mCurrentUser;

    private TextView mCaloriesTextView;
    private Button mEnterMealBtn;
    private Button mEditBodyInfoBtn;
    private Button mMealLogBtn;

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

        DownloadBodyInfoTask task = new DownloadBodyInfoTask();
        task.execute(buildURL());

        mEnterMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mEditBodyInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                    response = "Unable to download the meal log, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            mBodyInfo = BodyInfo.parseBodyInfoJSON(result);

            if (mBodyInfo == null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            } else {
                mCaloriesTextView.setText(mBodyInfo.getBmr() + " Calories");
            }
        }
    }
}
