package edu.uw.tacoma.team5.calorit;

import android.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.TextView;

import edu.uw.tacoma.team5.calorit.model.MealLog;

public class HomeActivity extends AppCompatActivity
        implements MealLogFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().add(R.id.home_activity,
                        new HomeFragment()).commit();
            }
        }, 3000);

    }

    public void startMealLog(){
        MealLogFragment mealLogFragment = new MealLogFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_home, mealLogFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

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
            dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkMealLog(View v){
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().add(R.id.home_activity,
                        new MealLogFragment()).commit();
            }
        }, 3000);
    }

    @Override
    public void onListFragmentInteraction(MealLog mealLog) {

    }
}


