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
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditProductActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //identifier for the loader
    private static final int PRODUCT_LOADER = 0;
    int GALLERY_REQUEST2 = 2;

    Bitmap theImage;

    // Incoming uri
    private Uri CurrentProductUri;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText quantityEditText;
    private EditText priceEditText;
    private EditText mPriceEditText;
    private ImageView imageView;

    private String productName;

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        //get the incoming Uri
        Intent intent = getIntent();
        CurrentProductUri = intent.getData();
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        nameEditText = findViewById(R.id.detail_name_text);
        descriptionEditText = findViewById(R.id.detail_description_text);
        quantityEditText = findViewById(R.id.detail_quantity_text);
        priceEditText = findViewById(R.id.detail_price_text);
        mPriceEditText = findViewById(R.id.detail_mPrice_text);
        imageView = findViewById(R.id.detail_image_view);

        Log.e(LOG_TAG, "Oncreate done");

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // we need all entry from the table (ex ID)
        String[] projection = {
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_DESCRIPTION,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_MERCHANT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_PHOTO,};

        Log.e(LOG_TAG,"cursor oncreate done");

        // this loader execute the query
        return new CursorLoader(this,   // Parent activity context
                CurrentProductUri,         // Query the content URI for the current Product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {


            // find the data in the cursor. Cursor should contain just one row
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_DESCRIPTION);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int mPriceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_MERCHANT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PHOTO);

            productName = cursor.getString(nameColumnIndex);
            String productDescription = cursor.getString(descriptionColumnIndex);
            int productQuantity = cursor.getInt(quantityColumnIndex);
            int productPrice = cursor.getInt(priceColumnIndex);
            int mProductPrice = cursor.getInt(mPriceColumnIndex);

            byte[] image = cursor.getBlob(imageColumnIndex);
            //get the image
            ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
            theImage = BitmapFactory.decodeStream(imageStream);

            nameEditText.setText(productName);
            descriptionEditText.setText(productDescription);
            quantityEditText.setText(Integer.toString(productQuantity));
            priceEditText.setText(Integer.toString(productPrice));
            mPriceEditText.setText(Integer.toString(mProductPrice));
            imageView.setImageBitmap(theImage);


            Log.e(LOG_TAG, "Get all of the data is done");

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        descriptionEditText.setText("");
        quantityEditText.setText("");
        priceEditText.setText("");
        mPriceEditText.setText("");
        imageView.setImageResource(R.drawable.greyhound);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options
        getMenuInflater().inflate(R.menu.edit_product_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //menu
        switch (item.getItemId()) {

            case R.id.action_save_changes:
                updateProduct();
                return true;

            case R.id.action_delete_product:
                deleteProduct();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void updateProduct(){

        String nameString = nameEditText.getText().toString().trim();
        //check that the name is valid or not
        if( nameString.length() == 0 ) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_name), R.style.Warning_Toast).show();
            return;
        }

        String descriptionString = descriptionEditText.getText().toString();
        //check the quantity
        String quantityString = quantityEditText.getText().toString().trim();
        if (quantityString.length() == 0) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_quantity), R.style.Warning_Toast).show();
            return;
        }

        int quantityInt = Integer.valueOf(quantityEditText.getText().toString());
        if( quantityInt <= 0 ) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_quantity), R.style.Warning_Toast).show();
            return;
        }

        String priceString = priceEditText.getText().toString().trim();
        //check the price
        if (priceString.length() == 0) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_price), R.style.Warning_Toast).show();
            return;
        }
        int priceInt = Integer.valueOf(priceEditText.getText().toString());
        if( priceInt <= 0 ) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_price), R.style.Warning_Toast).show();
            return;
        }

        String mPriceString = mPriceEditText.getText().toString().trim();
        //check the mPrice
        if (mPriceString.length() == 0) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_mPrice), R.style.Warning_Toast).show();
            return;
        }
        int mPriceInt = Integer.valueOf(mPriceEditText.getText().toString());
        if( mPriceInt <= 0 ) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_mPrice), R.style.Warning_Toast).show();
            return;
        }


        //getting the image and convert into byte array
        ByteArrayOutputStream imageByte = new ByteArrayOutputStream();
        theImage.compress(Bitmap.CompressFormat.JPEG, 50, imageByte);
        byte[] image = imageByte.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_DESCRIPTION, descriptionString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_MERCHANT_PRICE, mPriceString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_PHOTO, image);

        //insert pet
        int rowsAffected = getContentResolver().update(CurrentProductUri, contentValues, null, null);

        if(rowsAffected > 0) {
            StyleableToast.makeText(this, getString(R.string.toast_succes_insert), R.style.Success_Toast).show();
        } else
            StyleableToast.makeText(this, getString(R.string.toast_not_succes_insert), R.style.Warning_Toast).show();

    }

    public void uploadImage(View view){
        // make intent with Action pick
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // get file path from gallery
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        // make the uri with gallery file path
        Uri data = Uri.parse(pictureDirectoryPath);

        // set data type for intent, i need just photos
        photoPickerIntent.setDataAndType(data, "image/*");

        //start Activity
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check it was success  or not
        if (resultCode == RESULT_OK) {
            //if was success than check the request code --- now do not need switch statement
            if (requestCode == GALLERY_REQUEST2) {
                //The uri of the image
                Uri imageUri = data.getData();

                //new inputStream object to read the image
                InputStream inputStream;

                //try to convert it
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    //get bitmap from the stream
                    theImage = BitmapFactory.decodeStream(inputStream);


                    //show image
                    imageView.setImageBitmap(theImage);
                    Log.e(LOG_TAG, "Image converted");

                    // catch if did not load
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // inform the user
                    Toast.makeText(this, getString(R.string.toast_unable_image_open), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public void deleteProduct(){

        // Otherwise the product editing has started
        DialogInterface.OnClickListener deleteButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContentResolver().delete(CurrentProductUri, null, null);
                        Log.e(LOG_TAG, "Delete success");

                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
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

    public void orderMore (View view) {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"shipping@shipper.com" });
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, " Order more stuff");
        emailIntent.putExtra(Intent.EXTRA_TEXT, " We will order more from these product:\n" +
               productName + "\n " +
                "Quantity: (Please write here your required quantity) \n"+
                " Best regards, \n  PETSHOP TEAM");

        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

}
