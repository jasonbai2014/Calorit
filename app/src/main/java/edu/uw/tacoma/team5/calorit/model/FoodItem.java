package edu.uw.tacoma.team5.calorit.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Levi on 5/12/2016.
 *
 * This class is used to model a FoodItem. Instances of this class will represent food items eaten
 * in a given meal. A meal will be represented as a collection of these FoodItems.
 */
public class FoodItem {

    /**
     * The keys that are used to get food name, calories, and category from JSON
     */
    public static final String FOOD_NAME = "foodName", CALORIES = "calories", CATEGORY = "category";

    /**
     * String representation of the food's name.
     */
    private String mFoodName;

    /**
     * String representation of the food's category (ie. Meat).
     */
    private String mCategory;

    /**
     * Number of calories in 100g of this food.
     */
    private int mCalories;

    /**
     * Constructor for FoodItem
     * @param mFoodName the name to assign to this FoodItem.
     * @param mCategory the category that this FoodItem is in.
     * @param mCalories the number of calories 100g of this FoodItem has.
     */
    public FoodItem(String mFoodName, String mCategory, int mCalories) {
        this.mFoodName = mFoodName;
        this.mCategory = mCategory;
        this.mCalories = mCalories;
    }

    /**
     * Getter for the FoodItem's name.
     * @return String representation of the FoodItem's name.
     */
    public String getmFoodName() {
        return mFoodName;
    }

    /**
     * Setter for the FoodItem's name.
     * @param mFoodName String representation that will be assigned to this FoodItem's name.
     */
    public void setmFoodName(String mFoodName) {
        this.mFoodName = mFoodName;
    }

    /**
     * Getter for the FoodItem's category.
     * @return String representation of the FoodItem's category.
     */
    public String getmCategory() {
        return mCategory;
    }

    /**
     * Setter for the FoodItem's category.
     * @param mCategory String representation that will be assigned to the FoodItem's category.
     */
    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    /**
     * Getter for the number of calories 100g of this FoodItem has.
     * @return int representing the number of calories in 100g of this FoodItem.
     */
    public int getmCalories() {
        return mCalories;
    }

    /**
     * Setter for the number of calories 100g of this FoodItem has.
     * @param mCalories integer representing how many calories 100g of this FoodItem has.
     */
    public void setmCalories(int mCalories) {
        this.mCalories = mCalories;
    }

    /**
     * Gets a list representation of FoodItems from JSON
     *
     * @param foodItemJSON JSON representation of the FoodItems
     * @param foodItems List that each FoodItem in the JSON will be added to.
     * @return the reason the method failed, if any. NULL = success.
     */
    public static String parseFoodItemJSON(String foodItemJSON, List<FoodItem> foodItems){
        String reason = null;

        if (foodItemJSON != null) {
            try {
                JSONArray arr = new JSONArray(foodItemJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    FoodItem foodItem = new FoodItem(obj.getString(FoodItem.FOOD_NAME), obj.getString(FoodItem.CATEGORY),
                            Integer.valueOf(obj.getString(FoodItem.CALORIES)));
                    foodItems.add(foodItem);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }

}
