package com.example.android.inventory;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity  {

    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView quantityView;
    private TextView priceView;
    private TextView mPriceTextView;
    private ImageView imageView;
    private Button saveButton;
    private Bitmap imageBitmap;
    private boolean editing = false;

    public static final int GALLERY_REQUEST = 1;

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameTextView = findViewById(R.id.product_name_text);
        descriptionTextView = findViewById(R.id.product_description_text);
        quantityView = findViewById(R.id.product_quantity_text);
        priceView = findViewById(R.id.product_price_text);
        mPriceTextView = findViewById(R.id.product_mPrice_text);
        imageView = findViewById(R.id.image_view);
        saveButton = findViewById(R.id.save_button);

        nameTextView.setOnTouchListener(mTouchListener);
        descriptionTextView.setOnTouchListener(mTouchListener);
        quantityView.setOnTouchListener(mTouchListener);
        priceView.setOnTouchListener(mTouchListener);
        mPriceTextView.setOnTouchListener(mTouchListener);
        imageView.setOnTouchListener(mTouchListener);
        saveButton.setOnTouchListener(mTouchListener);

    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            editing = true;
            return false;
        }
    };



    //action an save Button
    public void saveProduct(View view) {

        //we don't need the "Unsaved" changes dialog screen,
        editing = false;

        String nameString = nameTextView.getText().toString().trim();
        //check that the name is valid or not
        if( nameString.length() == 0 ) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_name), R.style.Warning_Toast).show();
            return;
        }

        String descriptionString = descriptionTextView.getText().toString();
        //check the quantity
        String quantityString = quantityView.getText().toString().trim();
        if (quantityString.length() == 0) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_quantity), R.style.Warning_Toast).show();
            return;
        }

        int quantityInt = Integer.valueOf(quantityView.getText().toString());
        if( quantityInt <= 0 ) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_quantity), R.style.Warning_Toast).show();
            return;
        }

        String priceString = priceView.getText().toString().trim();
        //check the price
        if (priceString.length() == 0) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_price), R.style.Warning_Toast).show();
            return;
        }
        int priceInt = Integer.valueOf(priceView.getText().toString());
        if( priceInt <= 0 ) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_price), R.style.Warning_Toast).show();
            return;
        }

        String mPriceString = mPriceTextView.getText().toString().trim();
        //check the mPrice
        if (mPriceString.length() == 0) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_mPrice), R.style.Warning_Toast).show();
            return;
        }
        int mPriceInt = Integer.valueOf(mPriceTextView.getText().toString());
        if( mPriceInt <= 0 ) {
            StyleableToast.makeText(this, getString(R.string.toast_valid_mPrice), R.style.Warning_Toast).show();
            return;
        }


        //getting the image and convert into byte array
        ByteArrayOutputStream imageByte = new ByteArrayOutputStream();

        //make a basic bitmap when the user save, without uploading a photo
        if(imageBitmap == null){
            imageBitmap = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.greyhound);
        }

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, imageByte);

        byte[] image = imageByte.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_DESCRIPTION, descriptionString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_MERCHANT_PRICE, mPriceString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_PHOTO, image);

        //insert pet
        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, contentValues);

        if(newUri != null) {
            StyleableToast.makeText(this, getString(R.string.toast_succes_insert), R.style.Success_Toast).show();
        } else
            StyleableToast.makeText(this, getString(R.string.toast_not_succes_insert), R.style.Warning_Toast).show();

        nameTextView.setText("");
        descriptionTextView.setText("");
        quantityView.setText("");
        priceView.setText("");
        mPriceTextView.setText("");
        imageView.setImageResource(R.drawable.greyhound);

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
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check it was success  or not
        if (resultCode == RESULT_OK) {
            //if was success than check the request code --- now do not need switch statement
            if (requestCode == GALLERY_REQUEST) {
                //The uri of the image
                Uri imageUri = data.getData();

                //new inputStream object to read the image
                InputStream inputStream;

                //try to convert it
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    //get bitmap from the stream
                    imageBitmap = BitmapFactory.decodeStream(inputStream);


                    //show image
                    imageView.setImageBitmap(imageBitmap);
                    Log.e(LOG_TAG, "The image have been converted");

                // catch if did not load
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // inform the user
                    Toast.makeText(this, R.string.toast_unable_image_open, Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        // the product does not edited
        if (!editing) {
            super.onBackPressed();
            return;
        }

        // Otherwise the product editing has started
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // The discard button is clicked
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    //create unsaved changes warning
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // set up the positive negative buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message_unsaved);
        builder.setPositiveButton(R.string.dialog_discard_button, discardButtonClickListener);
        builder.setNegativeButton(R.string.dialog_editing_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // user clicked the keep editing, so dismiss
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

   //on back pressed in the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!editing) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Discard button clicked, back to parent activity
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);

        }



}
