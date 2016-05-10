package edu.uw.tacoma.team5.calorit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import edu.uw.tacoma.team5.calorit.model.BodyInfo;

/**
 * Created by jason bai on 5/9/2016.
 */
public class BodyInfoDB {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "Calories.db";

    private static final String TABLE_NAME = "bodyinfo";

    private BodyInfoDBHelper mBodyInfoDBHelper;

    private SQLiteDatabase mSQLiteDatabase;

    public BodyInfoDB(Context context) {
        mBodyInfoDBHelper = new BodyInfoDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mBodyInfoDBHelper.getWritableDatabase();
    }

    public boolean insertBodyInfo(String email, int heightFeet, int heightInches, int weight,
                              int age, String gender, int bmr) {
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("heightFeet", heightFeet);
        values.put("heightInches", heightInches);
        values.put("weight", weight);
        values.put("age", age);
        values.put("gender", gender);
        values.put("bmr", bmr);

        long rows = mSQLiteDatabase.insert(TABLE_NAME, null, values);

        return rows != -1;
    }

    public boolean updateBodyInfo(String email, int heightFeet, int heightInches, int weight,
                              int age, String gender, int bmr) {
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("heightFeet", heightFeet);
        values.put("heightInches", heightInches);
        values.put("weight", weight);
        values.put("age", age);
        values.put("gender", gender);
        values.put("bmr", bmr);

        String[] whereArgs = {email};

        int rows = mSQLiteDatabase.update(TABLE_NAME, values, "email = ?", whereArgs);

        return rows == 1;
    }

    public BodyInfo getBodyInfo(String email) {
        String[] columns = {"heightFeet", "heightInches", "weight", "age", "gender", "bmr"};
        String[] whereArgs = {email};

        Cursor c = mSQLiteDatabase.query(TABLE_NAME, columns, "email = ?", whereArgs, null, null, null);
        c.moveToFirst();

        int heightFeet = Integer.valueOf(c.getString(0));
        int heightInches = Integer.valueOf(c.getString(1));
        int weight = Integer.valueOf(c.getString(2));
        int age = Integer.valueOf(c.getString(3));
        String gender = c.getString(4);
        int bmr = Integer.valueOf(c.getString(5));

        return new BodyInfo(heightFeet, heightInches, weight, age, gender, bmr);
    }

    public void deleteBodyInfo() {
        mSQLiteDatabase.delete(TABLE_NAME, null, null);
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    private class BodyInfoDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_BODYINFO_SQL = "CREATE TABLE IF NOT EXISTS" + TABLE_NAME +
                "(email VARCHAR(40) PRIMARY KEY, heightFeet INT, heightInches INT, weight INT,"
                + "age INT, gender VARCHAR(1), bmr INT)";

        private static final String DROP_BODYINFO_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public BodyInfoDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_BODYINFO_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_BODYINFO_SQL);
            onCreate(db);
        }
    }
}
