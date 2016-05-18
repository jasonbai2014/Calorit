package edu.uw.tacoma.team5.calorit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.model.MealLog;

/**
 * This is a class that handles meal log in a SQLite database
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/17
 */
public class MealLogDB {

    /**
     * This is database version
     */
    private static final int DB_VERSION = 1;

    /**
     * This is database name
     */
    private static final String DB_NAME = "Calories.db";

    /**
     * This is table name
     */
    private static final String TABLE_NAME = "meallog";

    /**
     * This is a helper class instance of this class
     */
    private MealLogDBHelper mMealLogDBHelper;

    /**
     * This is an instance of the database
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * This is constructor of this class
     *
     * @param context is context
     */
    public MealLogDB(Context context) {
        mMealLogDBHelper = new MealLogDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mMealLogDBHelper.getWritableDatabase();
        mMealLogDBHelper.onCreate(mSQLiteDatabase);
    }

    /**
     * This inserts a meal log into the table
     *
     * @param logDate is log date
     * @param caloriesConsumed is amount of calories that user has consumed
     * @param caloriesBurned is amount of calories that user has burned
     * @param email is user's email
     * @return true if a row is inserted into the table. otherwise, false
     */
    public boolean insertMealLog(String logDate, String caloriesConsumed, String caloriesBurned, String email) {
        ContentValues values = new ContentValues();
        values.put("logDate", logDate);
        values.put("caloriesConsumed", caloriesConsumed);
        values.put("caloriesBurned", caloriesBurned);
        values.put("email", email);

        long rows = mSQLiteDatabase.insert(TABLE_NAME, null, values);

        return rows != -1;
    }

    /**
     * This updates user's meal log data in the table
     *
     * @param logDate is log date
     * @param caloriesConsumed is amount of calories that user has consumed
     * @param caloriesBurned is amount of calories that user has burned
     * @param email is user's email
     */
    public void updateMealLog(String logDate, String caloriesConsumed, String caloriesBurned, String email) {
        String[] columns = {"caloriesConsumed", "caloriesBurned"};
        String[] whereArgs = {email, logDate};

        Cursor c = mSQLiteDatabase.query(TABLE_NAME, columns, "email = ? AND logDate = ?", whereArgs, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            caloriesConsumed = String.valueOf(Integer.valueOf(caloriesConsumed) + Integer.valueOf(c.getString(0)));
            caloriesBurned = String.valueOf(Integer.valueOf(caloriesBurned) + Integer.valueOf(c.getString(1)));

            ContentValues values = new ContentValues();
            values.put("caloriesConsumed", caloriesConsumed);
            values.put("caloriesBurned", caloriesBurned);
            mSQLiteDatabase.update(TABLE_NAME, values, "email = ? AND logDate = ?", whereArgs);
        } else {
            insertMealLog(logDate, caloriesConsumed, caloriesBurned, email);
        }
    }

    /**
     * This gets a meal log for a specific user on a specific date
     *
     * @param email is user's email
     * @param date is a date
     * @return a MealLog instance if there is data for the user on the day. otherwise, null.
     */
    public MealLog getMealLogByDate(String email, String date) {
        String[] columns = {"logDate", "caloriesConsumed", "caloriesBurned"};
        String[] whereArgs = {email};

        Cursor c = mSQLiteDatabase.query(TABLE_NAME, columns, "email = ?", whereArgs, null, null, "logDate");
        c.moveToFirst();

        MealLog result = null;

        if (c.getCount() > 0) {
            result = new MealLog(c.getString(0), Integer.valueOf(c.getString(1)), Integer.valueOf(c.getString(2)));
        }

        return result;
    }

    /**
     * This returns a list of meal logs for a specific user
     *
     * @param email is user's email
     * @return a list of meal logs
     */
    public List<MealLog> getMealLog(String email) {
        List<MealLog> list = new ArrayList<MealLog>();
        String[] columns = {"logDate", "caloriesConsumed", "caloriesBurned"};
        String[] whereArgs = {email};

        Cursor c = mSQLiteDatabase.query(TABLE_NAME, columns, "email = ?", whereArgs, null, null, "logDate");
        c.moveToLast();

        for (int i = c.getCount(); i > 0; i--) {
            String logDate = c.getString(0);
            String caloriesConsumed = c.getString(1);
            String caloriesBurned = c.getString(2);
            list.add(new MealLog(logDate, Integer.valueOf(caloriesConsumed), Integer.valueOf(caloriesBurned)));
            c.moveToPrevious();
        }

        return list;
    }

    /**
     * This deletes all data in the table
     */
    public void deleteMealLog() {
        mSQLiteDatabase.delete(TABLE_NAME, null, null);
    }

    /**
     * This closes the database
     */
    public void closeDB() {
        mSQLiteDatabase.close();
    }

    /**
     * This is a helper class
     */
    class MealLogDBHelper extends SQLiteOpenHelper {

        /**
         * This is a SQL query used to create meallog table
         */
        private static final String CREATE_MEALLOG_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(id INT PRIMARY KEY, logDate DATE, caloriesConsumed INT, caloriesBurned INT, email VARCHAR(40))";

        /**
         * This is a SQl query used to drop meallog table
         */
        private static final String DROP_MEALLOG_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

        /**
         * This is a constructor
         *
         * @param context is context
         * @param name is name of the datbase
         * @param factory is factory
         * @param version is version of the database
         */
        public MealLogDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        /**
         * This is used to create meallog table in the database
         *
         * @param db is an instance of the database
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_MEALLOG_SQL);
        }

        /**
         * This is used to get a new meallog table in the database
         *
         * @param db is an instance of the database
         * @param oldVersion is old version
         * @param newVersion is new version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_MEALLOG_SQL);
            onCreate(db);
        }
    }

}
