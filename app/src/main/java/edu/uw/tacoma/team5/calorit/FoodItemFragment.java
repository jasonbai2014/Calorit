package edu.uw.tacoma.team5.calorit;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.data.FoodItemDB;
import edu.uw.tacoma.team5.calorit.model.FoodItem;

/**
 * A fragment representing a list of Items.
 *
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FoodItemFragment extends Fragment {

    /**
     * This is a url used to query food item data from the server.
     */
    private static final String FOOD_ITEMS_URL = "http://cssgate.insttech.washington.edu/~_450atm5/fooditems.php?";

    /**
     * This is a recyclerview used to show the list of foods within a selected category.
     */
    private RecyclerView mRecyclerView;

    /**
     * This is the number of columns in the gridlayout manager.
     */
    private int mColumnCount = 1;

    /**
     * This is an instance of the FoodItemDB class used to store local information.
     */
    private FoodItemDB mFoodItemDB;

    /**
     * This is the list that the FoodItems will be stored in that are to be displayed.
     */
    private List<FoodItem> mFoodList;

    /**
     * This is the current user's email address.
     */
    private String mCurrentUser;

    /**
     * This will hold the category that was selected before starting this fragment.
     */
    private String mSelectedCategory;

    /**
     * This is a listener to interaction on the fragment (ie. A FoodItem was clicked on).
     */
    private OnListFragmentInteractionListener mListener;

    /**
     * Button to return back to the categories list.
     */
    private Button mBackToCategoriesBtn;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FoodItemFragment() {
    }

    /**
     * This method calls the super method, passing in the savedInstanceState
     *
     * @param savedInstanceState Bundle passed in to get data about the savedInstanceState.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * This is used to determine what is displayed on the view. It will check for network connectivity
     * then either download data from the server to display or it will grab data from the local
     * SQLite database if there is no network connection.
     *
     * @param inflater inflates the view
     * @param container holds the contents of the view
     * @param savedInstanceState Bundle to get data about the saved instance state.
     * @return the created view to display.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_fooditem_list, container, false);

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
            DownloadFoodItemTask task = new DownloadFoodItemTask();
            task.execute(buildURL());
        } else {
            if (mFoodItemDB == null) {
                mFoodItemDB = new FoodItemDB(getActivity());
            }

            if (mFoodList == null) {
                mFoodList = new ArrayList<FoodItem>(); // mFoodItemDB.getFoodItems();
                mFoodList.add(0, null); // for header of the recycler view
            }

            if (mFoodList.size() == 1) {
                Toast.makeText(getActivity(), "No Network Connection, No local data available",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "No Network Connection, Displaying local data",
                        Toast.LENGTH_LONG).show();
            }

            mRecyclerView.setAdapter(new MyFoodItemRecyclerViewAdapter(mFoodList, mListener));
        }

        mBackToCategoriesBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).enterMeal();
            }
        });

        return view;
    }

    /**
     * This builds a url query sent to the server. It appends the category to the end of the URL
     * to determine what the php file on server will query with.
     *
     * @return URL of the php file's location with the category selected appended on the end.
     */
    private String buildURL() {
        StringBuilder query = new StringBuilder(FOOD_ITEMS_URL);

        try {
            query.append("category=");
            query.append(URLEncoder.encode(mSelectedCategory, "UTF-8"));
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

    /**
     * This method is called when the listener is attached to the fragment.
     *
     * @param context where the fragment is at.
     */
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

    /**
     * This method is called when the listener is detached from the fragment.
     */
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
        //We reach here when a food item was selected.
        void onListFragmentInteraction(FoodItem item);
    }




    /**
     * This is a class used to communicate with the server and download FoodItems
     */
    private class DownloadFoodItemTask extends AsyncTask<String, Void, String> {

        /**
         * dialog used to communicate with the user that work is being done.
         */
        private ProgressDialog dialog;

        /**
         * Shows a dialog to let user know that work is being done and to be patient.
         */
        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(), "Please wait", "Processing", true, false);
        }

        /**
         * This method contains what is done in the background on an Asynchronous task to allow the
         * user to continue using the app without having to sit and wait for these tasks to finish.
         *
         * @param urls the url used to set up a connection to the database on the server.
         * @return either the error message for a failure or the result of the query.
         */
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
                    response = "Unable to download the Food Items, Reason: "
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
         * This received data from the server and creates a list of food items which is sent to the
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

            List<FoodItem> foodItems = new ArrayList<FoodItem>();
            foodItems.add(0, null); // for header of the recycler view
            result = FoodItem.parseFoodItemJSON(result, foodItems);
            // Something wrong with the JSON returned.
            if (result != null && foodItems.size() == 1) {
                Toast.makeText(getActivity(), "No Food Items", Toast.LENGTH_LONG).show();
            } else if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (!foodItems.isEmpty()){
                // Everything is good, show the list of courses.
                mRecyclerView.setAdapter(new MyFoodItemRecyclerViewAdapter(foodItems, mListener));

                if (mFoodItemDB == null) {
                    mFoodItemDB = new FoodItemDB(getActivity());
                }

//                mFoodItemDB.deleteFoodItem();

                for (int i = 1; i < foodItems.size(); i++) {
                    FoodItem foodItem = foodItems.get(i);

                    //insert into SQLite db.

//                    mFoodItemDB.insertCourse(log.getmLogDate(), String.valueOf(log.getmCaloriesConsumed()),
//                            String.valueOf(log.getmCaloriesBurned()), mCurrentUser);
                }
            }

            dialog.dismiss();
        }
    }

}
