package edu.uw.tacoma.team5.calorit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_logout){
            SharedPreferences sharedPreferences =
                    getSharedPreferences("edu.uw.tacoma._atm5.Calorit.PREFS", Context.MODE_PRIVATE); //not sure about this PREFS path.
            sharedPreferences.edit().putBoolean("loggedin",false).commit();
            Intent i = new Intent(this, LogInActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Removes the current user's log in credentials, and returns them to the log in screen
     * @param view
     */
    public void logout(View view){
        //This is handled in onOptionsItemSelected above.
    }

    /**
     *
     * @param view
     */
    public void checkMealLog(View view){
        Intent intent = new Intent(HomeActivity.this, MealLogFragment.class);
        startActivity(intent);
    }

    /**
     *
     * @param view
     */
    public void editBodyInfo(View view){

    }

    /**
     *
     * @param view
     */
    public void enterMeal(View view){

    }


}
