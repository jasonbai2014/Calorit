package edu.uw.tacoma.team5.calorit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jason bai on 5/11/2016.
 */
public class FoodItemDB {

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "Calories.db";

    private static final String TABLE_NAME = "fooditem";

    private FoodItemDBHelper mFoodItemDBHelper;

    private SQLiteDatabase mSQLiteDatabase;

    public FoodItemDB(Context context) {
        mFoodItemDBHelper = new FoodItemDBHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mFoodItemDBHelper.getWritableDatabase();
    }

//    public List<FoodItem> getFoodItems(String category) {
//        List<FoodItem> list = new ArrayList<FoodItem>();
//        String[] columns = {"foodName", "calories"};
//        String[] whereArgs = {category};
//
//        Cursor c = mSQLiteDatabase.query(TABLE_NAME, columns, "category = ?", whereArgs, null, null, null);
//        c.moveToFirst();
//
//        for (int i = 0; i < c.getCount(); i++) {
//            // TO-DO : need to create food items and add them into the list once FoodItem model class is done
//            c.moveToNext();
//        }
//
//        return list;
//    }

    public boolean insertFoodItem(String foodName, String category, int calories) {
        ContentValues values = new ContentValues();
        values.put("foodName", foodName);
        values.put("calories", calories);
        values.put("category", category);

        long rows = mSQLiteDatabase.insert(TABLE_NAME, null, values);

        return rows != -1;
    }

    public void deleteFoodItems() {
        mSQLiteDatabase.delete(TABLE_NAME, null, null);
    }

    public void closeDB() {
        mSQLiteDatabase.close();
    }

    private class FoodItemDBHelper extends SQLiteOpenHelper {

        private static final String CREATE_FOODITEM_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (foodName VARCHAR(40) PRIMARY KEY, calories INT, category VARCHAR(20))";

        private static final String DROP_FOODITEM_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public FoodItemDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_FOODITEM_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_FOODITEM_SQL);
            onCreate(db);
        }
    }
}