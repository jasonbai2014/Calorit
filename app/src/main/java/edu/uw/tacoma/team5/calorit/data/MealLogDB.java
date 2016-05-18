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
 * Created by jason bai on 5/9/2016.
 */
public class MealLogDB {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "Calories.db";

    private static final String TABLE_NAME = "meallog";

    private MealLogDBHelper mMealLogDBHelper;

    private SQLiteDatabase mSQLiteDatabase;

    public MealLogDB(Context context) {
        mMealLogDBHelper = new MealLogDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mMealLogDBHelper.getWritableDatabase();
        mMealLogDBHelper.onCreate(mSQLiteDatabase);
    }

    public boolean insertMealLog(String logDate, String caloriesConsumed, String caloriesBurned, String email) {
        ContentValues values = new ContentValues();
        values.put("logDate", logDate);
        values.put("caloriesConsumed", caloriesConsumed);
        values.put("caloriesBurned", caloriesBurned);
        values.put("email", email);

        long rows = mSQLiteDatabase.insert(TABLE_NAME, null, values);

        return rows != -1;
    }

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

    public void deleteMealLog() {
        mSQLiteDatabase.delete(TABLE_NAME, null, null);
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    class MealLogDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_MEALLOG_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(id INT PRIMARY KEY, logDate DATE, caloriesConsumed INT, caloriesBurned INT, email VARCHAR(40))";

        private static final String DROP_MEALLOG_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public MealLogDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_MEALLOG_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_MEALLOG_SQL);
            onCreate(db);
        }
    }

}
