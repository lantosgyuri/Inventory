package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.example.android.inventory.data.InventoryContract.InventoryEntry;


/**
 * Created by android on 2018.01.14..
 */

public class ItemClickListener extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = ItemClickListener.class.getSimpleName();

    int mQuantity;
    String memberPrice;
    String mMerchantPrice;
    String mName;
    TextView mTextView;
    long mId;
    Uri productUri;
    Context mContext;



    public ItemClickListener(Context context, long id, int quantity, TextView textView,
                             String name, int price, int mPrice) {
        mQuantity = quantity;
        mTextView = textView;
        mId = id;
        mContext = context;
        productUri = Uri.parse("content://com.example.android.inventory/products/" + mId);
        mName = name;
        memberPrice= Integer.toString(price);
        mMerchantPrice = Integer.toString(mPrice);
    }


    @Override
    public void onClick(View v) {
        if (mQuantity > 0) {
            mQuantity--;
            mTextView.setText("In stock: "+ Integer.toString(mQuantity));
            String quantityString = Integer.toString(mQuantity);

            if(mQuantity != 0) {
                //update the database with the new quantity
                ContentValues contentValuesProduct = new ContentValues();
                contentValuesProduct.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);

                mContext.getContentResolver().update(productUri, contentValuesProduct, null, null);

                //make a ContentValues object with the product details.
                ContentValues contentValuesSummary = new ContentValues();
                contentValuesSummary.put(InventoryEntry.COLUMN_SUMMARY_NAME, mName);
                contentValuesSummary.put(InventoryEntry.COLUMN_SUMMARY_PRICE, memberPrice);
                contentValuesSummary.put(InventoryEntry.COLUMN_SUMMARY_MERCHANT_PRICE, mMerchantPrice);

                //save in the summary table

                mContext.getContentResolver().insert(InventoryEntry.SUMMARY_URI, contentValuesSummary);
                Log.e(LOG_TAG, "Sale is saved");


            }
        }




    }
}
