package edu.uw.tacoma.team5.calorit.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is a model class for body information table.
 * Qing Bai
 * 2016/04/25
 */
public class BodyInfo {
    /**
     * The keys that are used to get height, weight, age, gender, and bmr from JSON
     */
    public static final String HEIGHT_FEET = "heightFeet", HEIGHT_INCHES = "heightInches",
            WEIGHT = "weight", AGE = "age", GENDER = "gender", BMR = "bmr";

    /**
     * Feet portion of user's height
     */
    private int mHeightFeet;

    /**
     * Inches portion of user's height
     */
    private int mHeightInches;

    /**
     * User's weight
     */
    private int mWeight;

    /**
     * User's age
     */
    private int mAge;

    /**
     * User's gender
     */
    private String mGender;

    /**
     * User's basal metabolic rate
     */
    private int mBmr;

    /**
     * This is constructor of this class.
     *
     * @param mHeightFeet is feet portion of the user's height
     * @param mHeightInches is inches portion of the user's height
     * @param mWeight is weight of the user
     * @param mAge is age of the user
     * @param mGender is gender of the user
     * @param mBmr is basal metabolic rate of the user
     */
    public BodyInfo(int mHeightFeet, int mHeightInches, int mWeight, int mAge, String mGender, int mBmr) {
        this.mHeightFeet = mHeightFeet;
        this.mHeightInches = mHeightInches;
        this.mWeight = mWeight;
        this.mAge = mAge;
        this.mGender = mGender;
        this.mBmr = mBmr;
    }

    /**
     * This is getter for mHeightFeet
     *
     * @return feet portion of the user's height
     */
    public int getHeightFeet() {
        return mHeightFeet;
    }

    /**
     * This is setter for mHeightFeet
     *
     * @param mHeightFeet is feet portion of the user's height
     */
    public void setHeightFeet(int mHeightFeet) {
        this.mHeightFeet = mHeightFeet;
    }

    /**
     * This is getter for mHeightInches
     *
     * @return inches portion of the user's height
     */
    public int getHeightInches() {
        return mHeightInches;
    }

    /**
     * This is setter for mHeightInches
     *
     * @param mHeightInches is inches portion of user's height
     */
    public void setHeightInches(int mHeightInches) {
        this.mHeightInches = mHeightInches;
    }

    /**
     * This is getter of mWeight
     *
     * @return weight of the user
     */
    public int getWeight() {
        return mWeight;
    }

    /**
     * This is setter of mWeight
     *
     * @param mWeight is weight of the user
     */
    public void setWeight(int mWeight) {
        this.mWeight = mWeight;
    }

    /**
     * This is getter of mAge
     *
     * @return age of the user
     */
    public int getAge() {
        return mAge;
    }

    /**
     * This is setter of mAge
     *
     * @param mAge is age of the user
     */
    public void setAge(int mAge) {
        this.mAge = mAge;
    }

    /**
     * This is getter for mGender
     *
     * @return gender of the user
     */
    public String getGender() {
        return mGender;
    }

    /**
     * This is setter for mGender
     *
     * @param mGender is gender of the user
     */
    public void setGender(String mGender) {
        this.mGender = mGender;
    }

    /**
     * This is getter for BMR
     *
     * @return BMR of the user
     */
    public int getBmr() {
        return mBmr;
    }

    /**
     * This is setter for BMR
     *
     * @param mBmr is BMR of the user
     */
    public void setBmr(int mBmr) {
        this.mBmr = mBmr;
    }

    /**
     * This parses a given json string and uses it to create a BodyInfo instance.
     * @param bodyInfoJSON is the given JSON string
     * @return a BodyInfo instance created from the JSON string
     */
    public static BodyInfo parseBodyInfoJSON(String bodyInfoJSON) {
        BodyInfo bodyInfo = null;

        if (bodyInfoJSON != null) {
            try {
                JSONObject bodyInfoJsonObj = new JSONObject(bodyInfoJSON);
                bodyInfo = new BodyInfo(Integer.valueOf(bodyInfoJsonObj.getString(BodyInfo.HEIGHT_FEET)),
                        Integer.valueOf(bodyInfoJsonObj.getString(BodyInfo.HEIGHT_INCHES)),
                        Integer.valueOf(bodyInfoJsonObj.getString(BodyInfo.WEIGHT)),
                        Integer.valueOf(bodyInfoJsonObj.getString(BodyInfo.AGE)),
                        bodyInfoJsonObj.getString(BodyInfo.GENDER),
                        Integer.valueOf(bodyInfoJsonObj.getString(BodyInfo.BMR)));
            } catch (JSONException e) {
                Log.e("BodyInfo", "Unable to parse data, Reason: " + e.getMessage());
            }
        }

        return bodyInfo;
    }
}
