package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract;

import java.io.ByteArrayInputStream;

/**
 * Created by android on 2018.01.07..
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter (Context context, Cursor c){
        super (context, c, 0 );

    }

    //make blank view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    //bind the data with the View ---- param the view that earlier returned
    @Override
    public void bindView(View view, Context context, Cursor cursor){
        //find the individual views from the list_item.xml
        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);
        ImageView imageView = view.findViewById(R.id.image);

        //get the column indexes
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PHOTO);

        String productName = cursor.getString(nameColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);
        byte[] image=cursor.getBlob(imageColumnIndex);

        ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
        Bitmap theImage= BitmapFactory.decodeStream(imageStream);

        nameTextView.setText(productName);
        quantityTextView.setText(Integer.toString(productQuantity));
        priceTextView.setText(Integer.toString(productPrice));
        imageView.setImageBitmap(theImage);
    }




}
