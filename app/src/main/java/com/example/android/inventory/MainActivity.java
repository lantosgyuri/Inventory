package com.example.android.inventory;



import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventory.data.InventoryContract.InventoryEntry;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.net.URI;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //identifier for loader
    private static final int LOADER = 1;

    //adapter for listview
    InventoryCursorAdapter inventoryCursorAdapter;

    ListView productListView;

    public static final String LOG_TAG = MainActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newProduct();
            }
        });

        //listview for populate
       productListView =  findViewById(R.id.list_view_product);

        //setup adapter

        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        inventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        productListView.setAdapter(inventoryCursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // New intent for open EditorActivity
                Intent intent = new Intent(MainActivity.this, EditProductActivity.class);

                //Represent the specific product Uri
                Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                //Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        productListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // New intent for open EditorActivity
                Intent intent = new Intent(MainActivity.this, ItemDescriptionPopUp.class);

                //Represent the specific product Uri
                Uri currentPetUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                //Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                startActivity(intent);

                return true;
            }
        });



        // Kick off the loader
        getLoaderManager().initLoader(LOADER, null, this);

}


    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu options
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            //menu
            switch (item.getItemId()) {

                case R.id.action_new_product:
                    newProduct();

                    return true;
                // Respond to a click on the "Delete all entries" menu option
                case R.id.action_delete_all:
                    deleteAll();
                    makeDeleteToast();
                    return true;

                case R.id.action_show_summary:

                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        public void newProduct(){
            Intent intent = new Intent(this, EditorActivity.class);
            startActivity(intent);


        }

        public void deleteAll(){
            int rowsDeleted = getContentResolver().delete(InventoryEntry.CONTENT_URI, null, null);
            Log.e(LOG_TAG, "törölt sorok: " + rowsDeleted);
        }

        public void makeDeleteToast(){
            StyleableToast.makeText(this, "All items deleted", R.style.Warning_Toast).show();
        }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            //define projection
            String[] projection = {
                    InventoryEntry._ID,
                    InventoryEntry.COLUMN_PRODUCT_NAME,
                    InventoryEntry.COLUMN_PRODUCT_DESCRIPTION,
                    InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                    InventoryEntry.COLUMN_PRODUCT_PRICE,
                    InventoryEntry.COLUMN_PRODUCT_PHOTO };


            return new CursorLoader(this,
                    InventoryEntry.CONTENT_URI,
                    projection,
                    null,null,null);



    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG, "a loader befejezte a feloltest");
           inventoryCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            inventoryCursorAdapter.swapCursor(null);

    }
}
