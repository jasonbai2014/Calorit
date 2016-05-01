package edu.uw.tacoma.team5.calorit.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MealLog {
    public static final String LOG_DATE = "logDate", CALORIES_CONSUMED = "caloriesConsumed",
        CALORIES_BURNED = "caloriesBurned";

    private String mLogDate;
    private int mCaloriesConsumed;
    private int mCaloriesBurned;

    public MealLog(String mLogDate, int mCaloriesConsumed, int mCaloriesBruned) {
        this.mLogDate = mLogDate;
        this.mCaloriesConsumed = mCaloriesConsumed;
        this.mCaloriesBurned = mCaloriesBruned;
    }

    public String getmLogDate() {
        return mLogDate;
    }

    public void setmLogDate(String mLogDate) {
        this.mLogDate = mLogDate;
    }

    public int getmCaloriesConsumed() {
        return mCaloriesConsumed;
    }

    public void setmCaloriesConsumed(int mCaloriesConsumed) {
        this.mCaloriesConsumed = mCaloriesConsumed;
    }

    public int getmCaloriesBurned() {
        return mCaloriesBurned;
    }

    public void setmCaloriesBurned(int mCaloriesBruned) {
        this.mCaloriesBurned = mCaloriesBruned;
    }

    public static String parseMealLogJSON(String mealLogJSON, List<MealLog> logs) {
        String reason = null;

        if (mealLogJSON != null) {
            try {
                JSONArray arr = new JSONArray(mealLogJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    MealLog log = new MealLog(obj.getString(MealLog.LOG_DATE), Integer.valueOf(obj.getString(MealLog.CALORIES_CONSUMED)),
                            Integer.valueOf(obj.getString(MealLog.CALORIES_BURNED)));
                    logs.add(log);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }
}
