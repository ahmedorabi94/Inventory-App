package com.example.ahmedorabi.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.ahmedorabi.inventoryapp.data.ProductContract.ProductEntry;
import java.io.ByteArrayOutputStream;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter cursorAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        View empty_view = findViewById(R.id.empty_view);

        listView = findViewById(R.id.list_view);
        listView.setEmptyView(empty_view);

        cursorAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.setData(currentUri);
                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.insert_dummy:
                InsertProduct();
                return true;
            case R.id.delete_all_pets:
                DeleteAllProducts();
                return true;

        }


        return super.onOptionsItemSelected(item);
    }


    private void DeleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("Main Activity", rowsDeleted + "Rows Deleted from Database");
    }


    private void InsertProduct() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mac);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] img = bos.toByteArray();


        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "MacBook");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 200);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 8);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, "todo");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,"ahmed@gmail.com");
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, img);


        Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);


    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] allColumn = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_IMAGE
        };
        // execute query method on background thread
        return new CursorLoader(this, ProductEntry.CONTENT_URI, allColumn, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // new cursor containing updated pet data
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //when the data need to be deleted
        cursorAdapter.swapCursor(null);
    }
}
