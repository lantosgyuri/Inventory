package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.example.android.inventory.data.InventoryContract.InventoryEntry;


/**
 * Created by android on 2018.01.14..
 */

public class ItemClickListener extends AppCompatActivity implements View.OnClickListener {

    int mQuantity;
    TextView mTextView;
    long mId;
    Uri productUri;
    Context mContext;



    public ItemClickListener(Context context, long id, int quantity, TextView textView) {
        mQuantity = quantity;
        mTextView = textView;
        mId = id;
        mContext = context;
        productUri = Uri.parse("content://com.example.android.inventory/" +mId);
    }


    @Override
    public void onClick(View v) {
        if (mQuantity > 0) {
            mQuantity--;
            mTextView.setText("In stock: " + Integer.toString(mQuantity));
            String quantityString = Integer.toString(mQuantity);

            ContentValues contentValues = new ContentValues();
            contentValues.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);

            mContext.getContentResolver().update(productUri, contentValues, null, null);
        }


    }
}
