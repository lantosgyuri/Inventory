package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;

import java.io.ByteArrayInputStream;

/**
 * Created by android on 2018.01.18..
 */

public class ProductDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri CurrentProductUri;
    int PRODUCT_LOADER = 1;

    TextView nameTextView;
    ImageView ImageView;
    TextView priceTextView;
    TextView quantityTextView;
    TextView sPhoneNumberTextView;
    TextView sNameTextView;
    Button increaseButton;
    Button decreaseButton;
    Button deleteButton;
    Button orderMoreButton;

    String name;
    int quantity;
    String sPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //get the incoming Uri
        Intent intent = getIntent();
        CurrentProductUri = intent.getData();
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        nameTextView = findViewById(R.id.detail_name_textView);
        ImageView = findViewById(R.id.detail_imageView);
        priceTextView = findViewById(R.id.price_descripton_textView);
        quantityTextView = findViewById(R.id.quantity_descripton_textView);
        sNameTextView = findViewById(R.id.sName_descripton_textView);
        sPhoneNumberTextView = findViewById(R.id.sPhoneNumber_descripton_textView);

        deleteButton = findViewById(R.id.delete_button);
        decreaseButton = findViewById(R.id.decrase_button);
        increaseButton = findViewById(R.id.increase_button);
        orderMoreButton = findViewById(R.id.order_more_button);

        //set the listener for the buttons
        orderMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderMore();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseSupply();
            }
        });

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseSupply();
            }
        });


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //get the description
        String[] projection = {
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,
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
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int sNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int sPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);


            name = cursor.getString(nameColumnIndex);
            String productDescription = cursor.getString(descriptionColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            sPhone = cursor.getString(sPhoneColumnIndex);


            byte[] image = cursor.getBlob(imageColumnIndex);
            //get the image
            ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);

            if (productDescription.length() != 0) nameTextView.setText(productDescription);
            setTitle(getString(R.string.pop_up_header) + name);
            ImageView.setImageBitmap(theImage);
            priceTextView.setText(price + " €");
            quantityTextView.setText(getString(R.string.supply_description) + quantity);
            sNameTextView.setText(sName);
            sPhoneNumberTextView.setText(sPhone);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        nameTextView.setText("");
        priceTextView.setText("");
        quantityTextView.setText("");
        sNameTextView.setText("");
        sPhoneNumberTextView.setText("");

    }

    public void orderMore() {

        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", sPhone , null));
        startActivity(callIntent);
    }

    public void deleteProduct() {

        DialogInterface.OnClickListener deleteButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContentResolver().delete(CurrentProductUri, null, null);

                        finish();
                    }
                };

        showDeleteDialog(deleteButtonClickListener);

    }


    private void showDeleteDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // set up the positive /negative buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message_delete);
        builder.setPositiveButton(R.string.diaog_button_delete, discardButtonClickListener);
        builder.setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user clicked "no", so dismiss
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void decreaseSupply() {
        if (quantity > 0) {
            quantity--;
            quantityTextView.setText(getString(R.string.supply_description) + quantity);
            String quantityString = Integer.toString(quantity);

            if (quantity != 0) {
                //update the database with the new quantity
                ContentValues contentValuesProduct = new ContentValues();
                contentValuesProduct.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);

                getContentResolver().update(CurrentProductUri, contentValuesProduct, null, null);
            }

        }
    }

    private void increaseSupply() {
            quantity++;
            quantityTextView.setText(getString(R.string.supply_description) + quantity);
            String quantityString = Integer.toString(quantity);

                ContentValues contentValuesProduct = new ContentValues();
                contentValuesProduct.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);

                getContentResolver().update(CurrentProductUri, contentValuesProduct, null, null);
            }



}