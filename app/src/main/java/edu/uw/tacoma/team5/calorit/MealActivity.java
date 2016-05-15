package edu.uw.tacoma.team5.calorit;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.uw.tacoma.team5.calorit.model.FoodItem;

public class MealActivity extends AppCompatActivity implements FoodItemFragment.OnFoodItemListener {

    public static String KEY = "edu.uw.tacoma.team5.calorit.MealActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.meal_activity_layout, new MealFragment()).commit();
    }

    public void categorySelected(String category) {
        FragmentManager manager = getSupportFragmentManager();
        FoodItemFragment fragment = new FoodItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, category);
        fragment.setArguments(bundle);
        manager.beginTransaction().replace(R.id.meal_activity_layout, fragment).
                addToBackStack(null).commit();
    }

    @Override
    public void onFoodItemClick(FoodItem item, int amount) {

    }
}
