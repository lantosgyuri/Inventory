package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;

public class SummaryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView summaryListView;
    SummaryCursorAdapter summaryCursorAdapter;
    int LOADER = 1;
    int profitSummary = 0;
    int saleSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        summaryListView =  findViewById(R.id.sale_item_listView);

        //setup adapter

        View emptyView = findViewById(R.id.sale_emptyView);
        summaryListView.setEmptyView(emptyView);

        summaryCursorAdapter = new SummaryCursorAdapter(this, null);
        summaryListView.setAdapter(summaryCursorAdapter);

        getLoaderManager().initLoader(LOADER, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //define projection
        String[] projection = {
                InventoryEntry._IDS,
                InventoryEntry.COLUMN_SUMMARY_NAME,
                InventoryEntry.COLUMN_SUMMARY_PRICE,
                InventoryEntry.COLUMN_SUMMARY_MERCHANT_PRICE,
                InventoryEntry.COLUMN_SUMMARY_TIME_STAMP};


        return new CursorLoader(this,
                InventoryEntry.SUMMARY_URI,
                projection,
                null,null,"timestamp DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        summaryCursorAdapter.swapCursor(data);

        // calcuate the profit Summary
        if (data.moveToFirst()){
            do{
                //get the Price and Merchant PRice
                int itemPrice = data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_SUMMARY_PRICE));
                int itemMerchantPrice = data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_SUMMARY_MERCHANT_PRICE));

                //calculate the profit on one item
                int itemProfit = itemPrice - itemMerchantPrice;

                profitSummary += itemProfit;

                //calculate the qauntity of sales
                saleSummary++;

            }while(data.moveToNext());
        }

        TextView summaryProfit = findViewById(R.id.summary_profit_text);
        TextView summarySale =  findViewById(R.id.summary_sale_text);

        summaryProfit.setText("Your profit summary is: " + profitSummary + " €");
        summarySale.setText("You have sold " + saleSummary + " product ");
        }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        summaryCursorAdapter.swapCursor(null);

    }
}
