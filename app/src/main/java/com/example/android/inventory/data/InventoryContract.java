package com.example.android.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by android on 2018.01.06..
 */

public class InventoryContract {

    private InventoryContract() {}

    //link
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    //base URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //path table
    public static final String PATH_PRODUCTS = "products";


    public static final class InventoryEntry implements BaseColumns {

        //acces the content URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        //MIME TYPE basic URI
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //MIME TYPE content URI
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //table name
        public final static String TABLE_NAME = "products";

        //id column
        public final static String _ID = BaseColumns._ID;

        //product name column
        public final static String COLUMN_PRODUCT_NAME = "name";

        //description column
        public final static String COLUMN_PRODUCT_DESCRIPTION = "description";

        //price column
        public final static String COLUMN_PRODUCT_PRICE= "price";

        //photo column
        public final static String COLUMN_PRODUCT_PHOTO= "photo";

        //quantity column
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        //merchant price column
        public final static String COLUMN_PRODUCT_MERCHANT_PRICE= "mprice";



    }




}
