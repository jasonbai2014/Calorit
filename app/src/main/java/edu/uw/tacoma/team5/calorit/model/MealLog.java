package edu.uw.tacoma.team5.calorit.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a model class for meal log table.
 * Qing Bai
 * Levi Bingham
 * 2016/05/04
 */
public class MealLog {
    /**
     * The keys that are used to get log data, calories consumed, and calories burned
     */
    public static final String LOG_DATE = "logDate", CALORIES_CONSUMED = "caloriesConsumed",
            CALORIES_BURNED = "caloriesBurned";

    /**
     * String object representing the date that this log happened on.
     */
    private String mLogDate;

    /**
     * The number of calories consumed on this date.
     */
    private int mCaloriesConsumed;

    /**
     * The number of calories burned on this date.
     */
    private int mCaloriesBurned;

    /**
     * Constructs the meal log object for this date.
     * @param mLogDate is the date of the log.
     * @param mCaloriesConsumed is the number of calories consumed on this date.
     * @param mCaloriesBurned is the number of calories burned on this date.
     */
    public MealLog(String mLogDate, int mCaloriesConsumed, int mCaloriesBurned) {
        this.mLogDate = mLogDate;
        this.mCaloriesConsumed = mCaloriesConsumed;
        this.mCaloriesBurned = mCaloriesBurned;
    }

    /**
     * This is a getter for log date
     *
     * @returns this log date.
     */
    public String getmLogDate() {
        return mLogDate;
    }

    /**
     * Sets the date of this log.
     *
     * @param mLogDate the date to set this log to.
     */
    public void setmLogDate(String mLogDate) {
        this.mLogDate = mLogDate;
    }

    /**
     * This is a getter for calories consumed
     *
     * @returns the number of calories consumed this date.
     */
    public int getmCaloriesConsumed() {
        return mCaloriesConsumed;
    }


    /**
     * Sets the number of calories burned for this log.
     *
     * @param mCaloriesConsumed the number to set caloriesConsumed to for this log.
     */
    public void setmCaloriesConsumed(int mCaloriesConsumed) {
        this.mCaloriesConsumed = mCaloriesConsumed;
    }

    /**
     * This is a getter for calories burned
     *
     * @returns the number of calories burned on this date.
     */
    public int getmCaloriesBurned() {
        return mCaloriesBurned;
    }

    /**
     * Sets the number of calories burned on this date.
     *
     * @param mCaloriesBurned the number to set the number of calories burned to, on this date.
     */
    public void setmCaloriesBurned(int mCaloriesBurned) {
        this.mCaloriesBurned = mCaloriesBurned;
    }

    /**
     * Gets list representation of the MealLogs from JSON.
     *
     * @param mealLogJSON is the JSON of the mealLogs.
     * @param logs is the list that will contain the logs found in the mealLogJSON
     * @returns the reason the method failed, if any. NULL = success.
     */
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