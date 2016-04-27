package edu.uw.tacoma.team5.calorit;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.model.BodyInfo;


/**
 * A simple {@link Fragment} subclass.
 */
public class BodyInfoFragment extends Fragment {

    private static final String COURSE_URL = "http://cssgate.insttech.washington.edu/~_450atm5 .... cmd=BodyInformation"; //incomplete URL


    private RecyclerView mRecyclerView;
    private int mColumnCount = 1;

    public BodyInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_body_info, container, false);

        if(view instanceof RecyclerView){
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        DownloadCoursesTask task = new DownloadCoursesTask();
        task.execute(new String[]{COURSE_URL});

        return view;
    }




    private class DownloadCoursesTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to download the BodyInformation, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

  /*      @Override protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            List<BodyInfo> bodyInfoList = new ArrayList<BodyInfo>();
            result = BodyInfo.parseBodyInfoJSON(result); // parseinfo is not returning a string...
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            // Everything is good, show the list of courses.
            if (!bodyInfoList.isEmpty()) {
                mRecyclerView.setAdapter(new MyMealLogRecyclerViewAdapter(bodyInfoList, mListener));

                if (mCourseDB == null) {
                    mCourseDB = new CourseDB(getActivity());
                }
                // Delete old data so that you can refresh the local
                // database with the network data.
                mCourseDB.deleteCourses();

                // Also, add to the local database
                for (int i=0; i<bodyInfoList.size(); i++) {
                    Course course = bodyInfoList.get(i);
                    mCourseDB.insertCourse(course.getmCourseId(),
                            course.getmShortDescription(),
                            course.getmLongDescription(),
                            course.getmPrereqs());
                }
            }
        }*/
    }


}
