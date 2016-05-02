package edu.uw.tacoma.team5.calorit.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Levi on 4/26/2016.
 */
public class MealLog {

    public static final String CALORIES_CONSUMED = "caloriesConsumed", TODAYS_DATE = "logDate";

    private int calsConsumed;
    private String todaysDate;

    public MealLog(int calsConsumed, String todaysDate) {
        this.calsConsumed = calsConsumed;
        this.todaysDate = todaysDate;
    }

    public int getCalsConsumed() {
        return calsConsumed;
    }

    public void setCalsConsumed(int calsConsumed) {
        this.calsConsumed = calsConsumed;
    }

    public String getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(String todaysDate) {
        this.todaysDate = todaysDate;
    }

    public static String parseMealLogJSON(String mealLogJSON, List<MealLog> mealLogList) {
        String reason = null;

        if (mealLogJSON != null) {
            try {
                JSONArray arr = new JSONArray(mealLogJSON);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    MealLog mealLog = new MealLog(obj.getInt(MealLog.CALORIES_CONSUMED), obj.getString(MealLog.TODAYS_DATE));
                    mealLogList.add(mealLog);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }

        return reason;
    }
}
