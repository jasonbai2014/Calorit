package edu.uw.tacoma.team5.calorit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tacoma.team5.calorit.model.FoodItem;

/**
 * This is a class that handles food item data in a SQLite database
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/17
 */
public class FoodItemDB {

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
    private static final String TABLE_NAME = "fooditem";

    /**
     * This is a helper class instance of this class
     */
    private FoodItemDBHelper mFoodItemDBHelper;

    /**
     * This is an instance of the database
     */
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * This is constructor of this class
     *
     * @param context is context
     */
    public FoodItemDB(Context context) {
        mFoodItemDBHelper = new FoodItemDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mFoodItemDBHelper.getWritableDatabase();
        mFoodItemDBHelper.onCreate(mSQLiteDatabase);
    }

    /**
     * This returns a list of food items for a specific food category
     *
     * @param category is the food category
     * @return a list of food items
     */
    public List<FoodItem> getFoodItems(String category) {
        List<FoodItem> list = new ArrayList<FoodItem>();
        String[] columns = {"foodName", "calories"};
        String[] whereArgs = {category};

        Cursor c = mSQLiteDatabase.query(TABLE_NAME, columns, "category=?", whereArgs, null, null, null);
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            list.add(new FoodItem(c.getString(0), category, Integer.valueOf(c.getString(1))));
            c.moveToNext();
        }

        return list;
    }

    /**
     * This inserts food item data into the table
     *
     * @param foodName is food name
     * @param category is food category
     * @param calories is amount of calories in 100 g of the food
     * @return true if a row is inserted into the table. otherwise, false.
     */
    public boolean insertFoodItem(String foodName, String category, int calories) {
        ContentValues values = new ContentValues();
        values.put("foodName", foodName);
        values.put("calories", calories);
        values.put("category", category);

        long rows = mSQLiteDatabase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        return rows != -1;
    }

    /**
     * This deletes all data in the table
     */
    public void deleteFoodItems() {
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
    class FoodItemDBHelper extends SQLiteOpenHelper {

        /**
         * This is a SQL query used to create fooditem table
         */
        private static final String CREATE_FOODITEM_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (foodName VARCHAR(40) PRIMARY KEY, calories INT, category VARCHAR(20));";

        /**
         * This is a SQl query used to drop fooditem table
         */
        private static final String DROP_FOODITEM_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        /**
         * This is a constructor
         *
         * @param context is context
         * @param name is name of the database
         * @param factory is factory
         * @param version is version of the database
         */
        public FoodItemDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        /**
         * This is used to create fooditem table in the database
         *
         * @param db is an instance of the database
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_FOODITEM_SQL);
        }

        /**
         * This is used to get a new fooditem table in the database
         *
         * @param db is an instance of the database
         * @param oldVersion is old version
         * @param newVersion is new version
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_FOODITEM_SQL);
            onCreate(db);
        }
    }
}
