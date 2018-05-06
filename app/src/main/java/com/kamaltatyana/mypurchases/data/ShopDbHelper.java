package com.kamaltatyana.mypurchases.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kamaltatyana.mypurchases.data.ShopContract.ShopEntry;


public class ShopDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ShopDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "shop.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ShopDbHelper}
     *
     * @param context of the app
     */
    public ShopDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_SHOPS_TABLE =  "CREATE TABLE " + ShopEntry.TABLE_NAME + " ("
                + ShopEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ShopEntry.COLUMN_SHOP_NAME + " TEXT NOT NULL, "
                + ShopEntry.COLUMN_SHOP_COUNT + " INTEGER DEFAULT 1, "
                + ShopEntry.COLUMN_SHOP_PRICE + " REAL NOT NULL DEFAULT 0);";

        /**
         * Create table
         */
        db.execSQL(SQL_CREATE_SHOPS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
