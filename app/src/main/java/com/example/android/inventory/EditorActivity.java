package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity  {

    TextView nameTextView;
    TextView descriptionTextView;
    TextView quantityView;
    TextView priceView;
    TextView mPriceTextView;
    ImageView imageView;
    Button saveButton;
    Bitmap imageBitmap;

    public static final int GALLERY_REQUEST = 1;




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
    }

    //action an save Button
    public void saveProduct(View view) {
        String nameString = nameTextView.getText().toString();
        String descriptionString = descriptionTextView.getText().toString();
        String quantityString = quantityView.getText().toString().trim();
        String priceString = priceView.getText().toString().trim();
        String mPriceString = mPriceTextView.getText().toString().trim();

        //getting the image and convert into byte array
        ByteArrayOutputStream imageByte = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageByte);
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
            Toast.makeText(this, "Insert was success", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Insert was not success", Toast.LENGTH_LONG).show();

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

        // make the uri with galery file path
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

                // catch if did not load
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // inform the user
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }

            }
        }
    }




}
