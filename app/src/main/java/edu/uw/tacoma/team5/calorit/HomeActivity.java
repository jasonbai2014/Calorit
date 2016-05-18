package edu.uw.tacoma.team5.calorit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * This is an activity from HomeFragment and MealLogFragment
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/04
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * This sets HomeFragment for its initial UI
     *
     * @param savedInstanceState is saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            int mealData = intent.getIntExtra(ConfirmFragment.MEAL_DATA_KEY, 0);
            HomeFragment homeFragment = new HomeFragment();

            if (mealData != 0) {
                Bundle bundle = new Bundle();
                bundle.putInt(ConfirmFragment.MEAL_DATA_KEY, mealData);
                homeFragment.setArguments(bundle);
            }

            getSupportFragmentManager().beginTransaction().add(R.id.home_activity,
                    homeFragment).commit();
        }
    }

    public void enterMeal() {
        Intent i = new Intent(this, MealActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * This switches from HomeFragment to MealLogFragment.
     */
    public void viewMealLog() {
        MealLogFragment mealLogFragment = new MealLogFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.home_activity,
                mealLogFragment).addToBackStack(null).commit();
    }

    /**
     * This switches from HomeActivity to EditBodyActivity
     */
    public void editBodyInfo() {
        Intent i = new Intent(this, BodyInfoActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    /**
     * This sets up a logout item in the menu and a alert dialog when user tries to log out
     *
     * @param item is the menu item that was selected.
     * @return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage(getString(R.string.logout_dialog_message));

            dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.login_prefs),
                            Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean(getString(R.string.loggedin), false).commit();
                    sharedPreferences.edit().putString(getString(R.string.loggedin_email), null).commit();

                    startActivity(new Intent(HomeActivity.this, LogInActivity.class));
                    HomeActivity.this.finish();
                }
            });

            dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            });

            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


