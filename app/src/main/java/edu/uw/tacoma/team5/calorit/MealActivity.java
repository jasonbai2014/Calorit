package edu.uw.tacoma.team5.calorit;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.model.FoodItem;

/**
 * This is an activity that handles functions allowing a user to select foods and upload data
 * to a server
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/17
 */
public class MealActivity extends AppCompatActivity implements FoodItemFragment.OnFoodItemListener {

    /**
     * This is a key used to get selected category from a bundle
     */
    public static String CATEGORY_KEY = "edu.uw.tacoma.team5.calorit.MealActivity_category";

    /**
     * This is a list that holds all food selected by a user
     */
    private List<FoodItem> mSelectedFood;

    /**
     * This is a list that contains amount of each food item selected by a user
     */
    private List<Integer> mFoodAmount;

    /**
     * This starts this activity with MealFragment
     *
     * @param savedInstanceState is savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        mSelectedFood = new ArrayList<FoodItem>();
        mFoodAmount = new ArrayList<Integer>();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.meal_activity_layout, new MealFragment()).commit();
    }

    /**
     * This allows a user to select food from a selected food category
     *
     * @param category is selected food category
     */
    public void categorySelected(String category) {
        FragmentManager manager = getSupportFragmentManager();
        FoodItemFragment fragment = new FoodItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORY_KEY, category);
        fragment.setArguments(bundle);
        manager.beginTransaction().replace(R.id.meal_activity_layout, fragment).
                addToBackStack(null).commit();
    }

    /**
     * This brings user to a fragment UI where the user can confirm food items he/she has selected
     */
    public void confirmFoodEntry() {
        if (mFoodAmount.size() > 0) {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.meal_activity_layout, new ConfirmFragment()).commit();
        } else {
            Toast.makeText(this, "You haven't selected any items", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This adds selected food to the list containing selected food items
     *
     * @param item is selected food item
     * @param amount is amount of food item in gram
     */
    @Override
    public void onFoodItemClick(FoodItem item, int amount) {
        if (!mSelectedFood.contains(item)) {
            mSelectedFood.add(item);
            mFoodAmount.add(amount);
            Toast.makeText(this, "You have add " + amount + " g of " + item.getmFoodName().
                    replace(" (100g)", "") + " into the log", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You have selected " + item.getmFoodName().
                    replace(" (100g)", ""), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This clear the lists that contain food items and their amounts
     */
    public void reset() {
        mSelectedFood.clear();
        mFoodAmount.clear();
    }

    /**
     * This returns a copy of the list of food items
     *
     * @return a copy of the list
     */
    public List<FoodItem> getSelectedFoodItems() {
        List<FoodItem> copy = new ArrayList<FoodItem>();
        FoodItem selectedItem = null;

        for (int i = 0; i < mSelectedFood.size(); i++) {
            selectedItem = mSelectedFood.get(i);
            copy.add(new FoodItem(selectedItem.getmFoodName(), selectedItem.getmCategory(),
                    selectedItem.getmCalories()));
        }

        return copy;
    }

    /**
     * This returns a copy of the list of food amounts
     *
     * @return a copy of the list
     */
    public List<Integer> getSelectedAmounts() {
        List<Integer> copy = new ArrayList<Integer>();

        for (int i = 0; i < mFoodAmount.size(); i++) {
            copy.add(mFoodAmount.get(i));
        }

        return copy;
    }
}
