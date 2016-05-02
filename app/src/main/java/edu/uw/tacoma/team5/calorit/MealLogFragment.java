package edu.uw.tacoma.team5.calorit;

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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.data.MealLogDB;
import edu.uw.tacoma.team5.calorit.model.Account;
import edu.uw.tacoma.team5.calorit.model.MealLog;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MealLogFragment extends Fragment {

    private TextView mLogDateView;
    private TextView mCaloriesConsumedView;
    private RecyclerView mRecyclerView;
    private final String MEAL_LOG_URL = "http://cssgate.insttech.washington.edu/~_450atm5/meallog.php?cmd=MealLog";
    private int mColumnCount = 1;
    private MealLogDB mMealLogDB;
    private List<MealLog> mMealLogList;
    private SharedPreferences mSharedPreferences;

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MealLogFragment() {
    }



    private void getMealLogs(){

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){

            StringBuilder query = new StringBuilder(MEAL_LOG_URL);

            try {
                query.append("email=");
                query.append(URLEncoder.encode(mSharedPreferences.getString("loggedin_email",
                        null), "UTF-8"));
            } catch(UnsupportedEncodingException e){
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.login_prefs),
                Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meallog_list, container, false);

        Log.v("ga", "in oncreateview");

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

        //currentEmail = getArguments().getString("currentEmail");

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadMealLogsTask task1 = new DownloadMealLogsTask();
            task1.execute(new String[]{MEAL_LOG_URL});
        }     else {
            Toast.makeText(view.getContext(),
                    "No network connection available. Cannot display meal logs",
                    Toast.LENGTH_SHORT) .show();

            if (mMealLogDB == null) {
                mMealLogDB = new MealLogDB(getActivity());
            }
            if (mMealLogList == null) {
                mMealLogList = mMealLogDB.getMealLogs();
            }
            mRecyclerView.setAdapter(new MyMealLogRecyclerViewAdapter(mMealLogList, mListener));
        }

        mMealLogDB = new MealLogDB(getActivity());
        mMealLogList = mMealLogDB.getMealLogs();

        mRecyclerView.setAdapter(new MyMealLogRecyclerViewAdapter(mMealLogList, mListener));// may not need this line

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MealLog mealLog);
    }

    private class DownloadMealLogsTask extends AsyncTask<String, Void, String> {

        @Override protected String doInBackground(String... urls) {
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
                    response = "Unable to download Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            mMealLogList = new ArrayList<MealLog>();
            result = MealLog.parseMealLogJSON(result, mMealLogList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            // Everything is good, show the list of meal logs.
            if (!mMealLogList.isEmpty()) {
                mRecyclerView.setAdapter(new MyMealLogRecyclerViewAdapter(mMealLogList, mListener));

                if (mMealLogDB == null) {
                    mMealLogDB = new MealLogDB(getActivity());
                }
                // Delete old data so that you can refresh the local
                // database with the network data.
                mMealLogDB.deleteMealLogs();

                // Also, add to the local database
                for (int i=0; i<mMealLogList.size(); i++) {
                    MealLog mealLog = mMealLogList.get(i);
                    mMealLogDB.insertMealLog(mealLog.getTodaysDate(), mealLog.getCalsConsumed(), 0);
                }
            }
        }
    }

}
