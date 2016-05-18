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
 * This is a class that handles body information in a SQLite database
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/17
 */
public class BodyInfoDB {

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
    private static final String TABLE_NAME = "bodyinfo";

    /**
     * This is a helper class instance of this class
     */
    private BodyInfoDBHelper mBodyInfoDBHelper;

    /**
     * This is an instance of the database
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * This is constructor of this class
     *
     * @param context is context
     */
    public BodyInfoDB(Context context) {
        mBodyInfoDBHelper = new BodyInfoDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mBodyInfoDBHelper.getWritableDatabase();
    }

    /**
     * This updates or inserts data into the table.
     *
     * @param email is email of a user
     * @param heightFeet is user's height in feet
     * @param heightInches is user's height in inches
     * @param weight is user's weight
     * @param age is user's age
     * @param gender is user's gender
     * @param bmr is user's basal metabolic rate
     * @return true if a row is inserted into the table. otherwise, false.
     */
    public boolean upsertBodyInfo(String email, int heightFeet, int heightInches, int weight,
                              int age, String gender, int bmr) {
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("heightFeet", heightFeet);
        values.put("heightInches", heightInches);
        values.put("weight", weight);
        values.put("age", age);
        values.put("gender", gender);
        values.put("bmr", bmr);

        long rows = mSQLiteDatabase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        return rows != -1;
    }

    /**
     * This gets user's body information from the table
     *
     * @param email is user's email
     * @return a BodyInfo instance containing body information of the user
     */
    public BodyInfo getBodyInfo(String email) {
        BodyInfo info = null;
        String[] columns = {"heightFeet", "heightInches", "weight", "age", "gender", "bmr"};
        String[] whereArgs = {email};

        Cursor c = mSQLiteDatabase.query(TABLE_NAME, columns, "email = ?", whereArgs, null, null, null);
        c.moveToFirst();

        if (c.getCount() == 1) {
            int heightFeet = Integer.valueOf(c.getString(0));
            int heightInches = Integer.valueOf(c.getString(1));
            int weight = Integer.valueOf(c.getString(2));
            int age = Integer.valueOf(c.getString(3));
            String gender = c.getString(4);
            int bmr = Integer.valueOf(c.getString(5));
            info = new BodyInfo(heightFeet, heightInches, weight, age, gender, bmr);
        }


        return info;
    }

    /**
     * This deletes all data in the table
     */
    public void deleteBodyInfo() {
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
    class BodyInfoDBHelper extends SQLiteOpenHelper {

        /**
         * This is a SQL query used to create bodyinfo table
         */
        private static final String CREATE_BODYINFO_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(email VARCHAR(40) PRIMARY KEY, heightFeet INT, heightInches INT, weight INT,"
                + "age INT, gender VARCHAR(1), bmr INT)";

        /**
         * This is a SQl query used to drop bodyinfo table
         */
        private static final String DROP_BODYINFO_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

        /**
         * This is a constructor
         *
         * @param context is context
         * @param name is name of the datbase
         * @param factory is factory
         * @param version is version of the database
         */
        public BodyInfoDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        /**
         * This is used to create bodyinfo table in the database
         *
         * @param db is an instance of the database
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_BODYINFO_SQL);
        }

        /**
         * This is used to get a new bodyinfo table in the database
         *
         * @param db is an instance of the database
         * @param oldVersion is old version
         * @param newVersion is new version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_BODYINFO_SQL);
            onCreate(db);
        }
    }
}
