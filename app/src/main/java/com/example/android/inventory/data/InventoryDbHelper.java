package com.example.android.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by android on 2018.01.06..
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    // name of the database
    private static final String DATABASE_NAME = "inventory.db";

    //db version number for check the database schema change
    private static final int DATABASE_VERSION = 6;

    //constructor
    public InventoryDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //String for creating the database with SQL
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE "
                + InventoryContract.InventoryEntry.TABLE_NAME + " ("
                + InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_DESCRIPTION + " TEXT, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_PHOTO + " BLOB, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_MERCHANT_PRICE + " INTEGER NOT NULL DEFAULT 0);";

        String SQL_CREATE_SUMMARY_TABLE = "CREATE TABLE "
                + InventoryContract.InventoryEntry.SUMMARY_TABLE_NAME + " ("
                + InventoryContract.InventoryEntry._IDS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.InventoryEntry.COLUMN_SUMMARY_NAME + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_SUMMARY_PRICE + " INTEGER NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_SUMMARY_MERCHANT_PRICE + " INTEGER NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_SUMMARY_TIME_STAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

        //execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SUMMARY_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InventoryContract.InventoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InventoryContract.InventoryEntry.SUMMARY_TABLE_NAME);
        // create new tables
        onCreate(sqLiteDatabase);
    }
}
