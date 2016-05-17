package edu.uw.tacoma.team5.calorit;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.model.ConfirmFragment;
import edu.uw.tacoma.team5.calorit.model.FoodItem;

public class MealActivity extends AppCompatActivity implements FoodItemFragment.OnFoodItemListener {

    public static String CATEGORY_KEY = "edu.uw.tacoma.team5.calorit.MealActivity_category";

    private List<FoodItem> mSelectedFood;

    private List<Integer> mFoodAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        mSelectedFood = new ArrayList<FoodItem>();
        mFoodAmount = new ArrayList<Integer>();

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.meal_activity_layout, new MealFragment()).commit();
    }

    public void categorySelected(String category) {
        FragmentManager manager = getSupportFragmentManager();
        FoodItemFragment fragment = new FoodItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CATEGORY_KEY, category);
        fragment.setArguments(bundle);
        manager.beginTransaction().replace(R.id.meal_activity_layout, fragment).
                addToBackStack(null).commit();
    }

    public void confirmFoodEntry() {
        if (mFoodAmount.size() > 0) {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.meal_activity_layout, new ConfirmFragment()).commit();
        } else {
            Toast.makeText(this, "You haven't selected any items", Toast.LENGTH_LONG).show();
        }
    }

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

    public void reset() {
        mSelectedFood.clear();
        mFoodAmount.clear();
    }

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

    public List<Integer> getSelectedAmounts() {
        List<Integer> copy = new ArrayList<Integer>();

        for (int i = 0; i < mFoodAmount.size(); i++) {
            copy.add(mFoodAmount.get(i));
        }

        return copy;
    }
}
