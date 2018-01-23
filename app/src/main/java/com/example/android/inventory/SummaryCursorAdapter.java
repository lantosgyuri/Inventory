package com.example.android.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract;

/**
 * Created by android on 2018.01.18..
 */

public class SummaryCursorAdapter extends CursorAdapter {

    public SummaryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.summary_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameText = view.findViewById(R.id.summary_item_name);
        TextView priceText = view.findViewById(R.id.summary_item_price);
        TextView mPriceText = view.findViewById(R.id.summary_item_mPrice);
        TextView profitText = view.findViewById(R.id.summary_profit_view);
        TextView timeText = view.findViewById(R.id.summary_timestamp);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUMMARY_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUMMARY_PRICE);
        int mPriceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUMMARY_MERCHANT_PRICE);
        int timeColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUMMARY_TIME_STAMP);

        String name = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        int mPrice = cursor.getInt(mPriceColumnIndex);
        String time = cursor.getString(timeColumnIndex);

        nameText.setText(name);
        priceText.setText(context.getString(R.string.price) + price);
        mPriceText.setText(context.getString(R.string.merchant_price) + mPrice);
        profitText.setText(context.getString(R.string.summary_your_proft) + (price - mPrice) + " €");
        timeText.setText(context.getString(R.string.summary_sale_time) +time);

    }
}
