package edu.uw.tacoma.team5.calorit.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.model.MealLog;

/**
 * Created by Levi on 4/27/2016.
 */
public class MealLogDB {
    private static final String MEAL_LOG_TABLE = "MealLog";
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "_450atm5.db";
    private SharedPreferences mSharedPreferences;


    private MealLogDBHelper mMealLogDBHelper;
    private SQLiteDatabase mSQLiteDatabase;


    public MealLogDB(Context context) {
        mMealLogDBHelper = new MealLogDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mMealLogDBHelper.getWritableDatabase();
    }

    /**  * Delete all the data from the MEAL_LOG_TABLE  */
    public void deleteMealLogs() {
        mSQLiteDatabase.delete(MEAL_LOG_TABLE, null, null);
    }

    /**  * Returns the list of MealLogs from the local MealLog table.  * @return list  */
    public List<MealLog> getMealLogs() {
        String[] columns = {
                 "logDate", "caloriesConsumed"
        };

        String selection = "email = ?";

        String[] selectionArgs = {
                "test"
        };

        Cursor c = mSQLiteDatabase.query(
                MEAL_LOG_TABLE,  // The table to query
                columns,    // The columns to return
                selection,   // The columns for the WHERE clause
                selectionArgs,   // The values for the WHERE clause
                null,   // don't group the rows
                null,   // don't filter by row groups
                null    // The sort order
        );
        c.moveToFirst();
        List<MealLog> list = new ArrayList<MealLog>();
        for (int i=0; i<c.getCount(); i++) {
            String date = c.getString(0);
            int calsConsumed = c.getInt(1);
            MealLog mealLog = new MealLog(calsConsumed, date);
            list.add(mealLog);
            c.moveToNext();
        }
        return list;
    }

    /**
     * Inserts the MealLog into the local sqlite table. Returns true if successful, false otherwise.
     * @param date
     * @param calsConsumed
     * @return true or false  */
    public boolean insertMealLog(String date, int calsConsumed, int calsBurned) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", mSharedPreferences.getString("loggedin_email", null));
        contentValues.put("logDate", date);
        contentValues.put("caloriesConsumed", calsConsumed);
        contentValues.put("caloriesBurned", calsBurned);
        long rowId = mSQLiteDatabase.insert("MealLog", null, contentValues);
        return rowId != -1;
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    private class MealLogDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_MEAL_LOG_SQL =
                "CREATE TABLE IF NOT EXISTS MealLog (id INTEGER PRIMARY KEY AUTOINCREMENT, email varchar(40) NOT NULL, logDate varchar(40) NOT NULL, caloriesConsumed int(11) NOT NULL, caloriesBurned int(11) NOT NULL)";
        private static final String DROP_MEAL_LOG_SQL =
                "DROP TABLE IF EXISTS MealLog";

        public MealLogDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_MEAL_LOG_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL(DROP_MEAL_LOG_SQL);
            onCreate(mSQLiteDatabase);
        }
    }
}
