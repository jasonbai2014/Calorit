package edu.uw.tacoma.team5.calorit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.data.MealLogDB;
import edu.uw.tacoma.team5.calorit.model.MealLog;

/**
 * A fragment representing a list of meal log.
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/04
 */
public class MealLogFragment extends Fragment {

    /**
     * This is a url used to query meal log data from the server
     */
    private static final String MEAL_LOG_URL = "http://cssgate.insttech.washington.edu/~_450atm5/meallog.php?";

    /**
     * This is a view showing a list of meal logs
     */
    private RecyclerView mRecyclerView;

    /**
     * This saves user's username
     */
    private SharedPreferences mSharedPerferences;

    /**
     * This is current user's email (username)
     */
    private String mCurrentUser;

    private MealLogDB mMealLogDB;

    private List<MealLog> mLogList;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    /**
     * This creates this fragment and gets current user's email from sharedPreferences
     *
     * @param savedInstanceState is saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mSharedPerferences = getActivity().getSharedPreferences(getString(R.string.login_prefs),
                Context.MODE_PRIVATE);
        mCurrentUser = mSharedPerferences.getString(getString(R.string.loggedin_email), null);
    }

    /**
     * This downloads data from the server and show them in the view.
     *
     * @param inflater is inflater
     * @param container is container
     * @param savedInstanceState is saved state
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meallog_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        if (isConnectedToNetwork()) {
            DownloadMealLogTask task = new DownloadMealLogTask();
            task.execute(buildURL());
        } else {
            if (mMealLogDB == null) {
                mMealLogDB = new MealLogDB(getActivity());
            }

            if (mLogList == null) {
                mLogList = mMealLogDB.getMealLog(mCurrentUser);
                mLogList.add(0, null); // for header of the recycler view
            }

            if (mLogList.size() == 1) {
                Toast.makeText(getActivity(), "No Network Connection, No local data available",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "No Network Connection, Displaying local data",
                        Toast.LENGTH_LONG).show();
            }

            mRecyclerView.setAdapter(new MyMealLogRecyclerViewAdapter(mLogList));
        }

        return view;
    }

    /**
     * This builds a url query sent to the server
     *
     * @return built url
     */
    private String buildURL() {
        StringBuilder query = new StringBuilder(MEAL_LOG_URL);

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
        mMealLogDB.closeDB();
    }

    /**
     * This is a class used to communicate with the server and download meal log
     */
    private class DownloadMealLogTask extends AsyncTask<String, Void, String> {

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

        /**
         * This received data from the server and creates a list of meal logs which is sent to the
         * adapter
         *
         * @param result is a string from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            List<MealLog> mealLog = new ArrayList<MealLog>();
            mealLog.add(0, null); // for header of the recycler view
            result = MealLog.parseMealLogJSON(result, mealLog);
            // Something wrong with the JSON returned.
            if (result != null && mealLog.size() == 1) {
                Toast.makeText(getActivity(), "You don't have any meal logs", Toast.LENGTH_LONG).show();
            } else if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (!mealLog.isEmpty()){
                // Everything is good, show the list of courses.
                mRecyclerView.setAdapter(new MyMealLogRecyclerViewAdapter(mealLog));

                if (mMealLogDB == null) {
                    mMealLogDB = new MealLogDB(getActivity());
                }

                mMealLogDB.deleteMealLog();

                for (int i = 1; i < mealLog.size(); i++) {
                    MealLog log = mealLog.get(i);
                    mMealLogDB.insertCourse(log.getmLogDate(), String.valueOf(log.getmCaloriesConsumed()),
                            String.valueOf(log.getmCaloriesBurned()), mCurrentUser);
                }
            }

            dialog.dismiss();
        }
    }
}
