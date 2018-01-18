package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


import com.example.android.inventory.data.InventoryContract.InventoryEntry;

/**
 * Created by android on 2018.01.07..
 */

public class InventoryProvider extends ContentProvider {

    // Log message tag
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    //URI matcher code for the table
    private static final int PRODUCTS = 1;

    //URI matcher for one single product
    private static final int PRODUCTS_ID = 2;

    //URI matcher for Summary table
    private static final int SUMMARY = 3;

    //URI matcher object to match content URI with code
    private static final UriMatcher sURiMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer (runs first, every time when this lass called)
    // for all content uri pattern
    static{
        // URI pattern for the products table
        sURiMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);

        // URi pattern for one single row (# - place for the number of the ID)
        sURiMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);

        //URI pattern for the summary table
        sURiMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_SUMMARY, SUMMARY);

    }

    //database helper object
    private InventoryDbHelper mInvetnoryDbHelper;


    @Override
    public boolean onCreate() {
        mInvetnoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    //query
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){

        //get database
        SQLiteDatabase database = mInvetnoryDbHelper.getReadableDatabase();

        //Cursor for the result
        Cursor cursor;

        // Match the URI
        int match = sURiMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                //could contain multiple rows
                cursor = database.query(InventoryEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case PRODUCTS_ID:
                //contain only one row,  get the ID --- ? for ID the number
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(InventoryEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case SUMMARY:
                //for multiple rows
                cursor = database.query(InventoryEntry.SUMMARY_TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknow URI " + uri);

        }

        //notification for the auto update
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    //insert a product
    public Uri insert(Uri uri, ContentValues contentValues){

        final int match = sURiMatcher.match(uri);
        // for insert we need just the table
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);

            case SUMMARY:
                return insertSale(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insert is not supported for: " +uri);
        }
    }

    //method for insertProduct
    private Uri insertProduct(Uri uri, ContentValues contentValues){

        Log.e(LOG_TAG, "meghivta az insert profuktot");

        //check that all of the data is valid

       String name = contentValues.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);

           if (name.equals("") || name == null) throw new IllegalArgumentException("You have to give a valid price");

           Integer price = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_PRICE);
           if (price == null || price <= 0) throw new IllegalArgumentException("You have to give a valid price");

           Integer quantity = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
           if (quantity == null || quantity <= 0) throw new IllegalArgumentException("You have to give a valid quantity");

           Integer mPrice = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_MERCHANT_PRICE);
           if (mPrice == null || mPrice <= 0) throw new IllegalArgumentException("You have to give a valid merchant price");


        // get database (writable)
        SQLiteDatabase database = mInvetnoryDbHelper.getWritableDatabase();
        Log.e(LOG_TAG, "meghivta az irandó adatbázist");

        // insert product
        long id = database.insert(InventoryEntry.TABLE_NAME, null, contentValues);

        //check the insert was success
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert " + uri);
            return null;
        }

        //send notify for all listener
        getContext().getContentResolver().notifyChange(uri,null);

        Log.e(LOG_TAG, "Vissza megy az urika");

        //return the uri with the new product ID
        return ContentUris.withAppendedId(uri, id);
    }

    public Uri insertSale(Uri uri, ContentValues contentValues){

        SQLiteDatabase database = mInvetnoryDbHelper.getWritableDatabase();

        long summaryId = database.insert(InventoryEntry.SUMMARY_TABLE_NAME, null, contentValues);

        //check the insert was success
        if (summaryId == -1){
            Log.e(LOG_TAG, "Failed to insert Summary " + uri);
            return null;
        }

        //send notify for all listener
        getContext().getContentResolver().notifyChange(uri,null);

        Log.e(LOG_TAG, "Vissza megy az urika SUMMARY");

        //return the uri with the new product ID
        return ContentUris.withAppendedId(uri, summaryId);
    }


    @Override
    public int update (Uri uri, ContentValues contentValues, String selection,
                       String[] selectionArgs){
        final int match = sURiMatcher.match(uri);

        switch (match){

            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);

            case PRODUCTS_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                return updateProduct(uri, contentValues, selection, selectionArgs);

            default: throw new IllegalArgumentException("Update is not supported for this uri: " +uri);
        }
    }

    public int updateProduct(Uri uri, ContentValues contentValues, String selection,
                             String[] selectionArgs ){

        //check for no values
        if (contentValues.size() == 0) return 0;

        //check that the data what we have, and check the data is valid or not

        if (contentValues.containsKey(InventoryEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
            if (name == null ||name.equals("")) throw new IllegalArgumentException("You have to give a name");
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price <= 0)
                throw new IllegalArgumentException("You have to give a valid price");
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity <= 0)
                throw new IllegalArgumentException("You have to give a valid quantity");
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_PRODUCT_MERCHANT_PRICE)) {
            Integer mPrice = contentValues.getAsInteger(InventoryEntry.COLUMN_PRODUCT_MERCHANT_PRICE);
            if (mPrice == null || mPrice <= 0)
                throw new IllegalArgumentException("You have to give a valid merchant price");
        }

        //get database
        SQLiteDatabase databse = mInvetnoryDbHelper.getWritableDatabase();

        int rowsUpdated = databse.update(InventoryEntry.TABLE_NAME, contentValues,
                selection, selectionArgs);

        //send notify for listener
        if (rowsUpdated != 0) getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){

        //get databse
        SQLiteDatabase database = mInvetnoryDbHelper.getWritableDatabase();

        //number for the deleted rows
        int rowsDeleted;

        final int match = sURiMatcher.match(uri);

        switch(match){

            case PRODUCTS:
                //delete mutliple rows
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;

            case PRODUCTS_ID:
                //delete single row
                selection = InventoryEntry._ID +"=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;

            default: throw new IllegalArgumentException("Delete cant be done with uri: " +uri);
        }

        //set notify for the listener
        if (rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sURiMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return InventoryEntry.CONTENT_LIST_TYPE;

            case PRODUCTS_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;

            case SUMMARY:
                return InventoryEntry.SUMMARY_LIST_TYPE;

            default: throw new IllegalArgumentException(" Unknow uri: " + uri + "with match" + match);
        }
    }

}
