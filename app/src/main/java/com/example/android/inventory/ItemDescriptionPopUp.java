package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.io.ByteArrayInputStream;

/**
 * Created by android on 2018.01.18..
 */

public class ItemDescriptionPopUp  extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri CurrentProductUri;
    int PRODUCT_LOADER = 1;
    TextView popUpTextView;
    ImageView popUpImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_popup_layout);

        //get the incoming Uri
        Intent intent = getIntent();
        CurrentProductUri = intent.getData();
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        //make the size of the pop up window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.6));

        popUpTextView = findViewById(R.id.popUp_textView);
        popUpImageView = findViewById(R.id.popUp_imageView);



    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //get the description
        String[] projection = {
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_DESCRIPTION,
                InventoryEntry.COLUMN_PRODUCT_PHOTO,};


        // this loader execute the query
        return new CursorLoader(this,   // Parent activity context
                CurrentProductUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


        // find the data in the cursor. Cursor should contain just one row
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_DESCRIPTION);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PHOTO);


            String name = cursor.getString(nameColumnIndex);
            String productDescription = cursor.getString(descriptionColumnIndex);

            byte[] image = cursor.getBlob(imageColumnIndex);
            //get the image
            ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);

            if(productDescription.length() != 0 ) popUpTextView.setText(productDescription);
            setTitle(getString(R.string.pop_up_header) + name);
            popUpImageView.setImageBitmap(theImage);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        popUpTextView.setText("");

    }

}