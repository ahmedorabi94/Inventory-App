package com.example.ahmedorabi.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.ahmedorabi.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Ahmed Orabi on 5/23/2018.
 */

public class ProductProvider extends ContentProvider {

    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    ProductDbHelper dbHelper;
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID =101;

    private static final UriMatcher sUrimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUrimatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PRODUCT_PATH,PRODUCTS);
        sUrimatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PRODUCT_PATH +"/#",PRODUCT_ID);
    }





    @Override
    public boolean onCreate() {
        dbHelper = new ProductDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUrimatcher.match(uri);
        switch (match){
            case PRODUCTS:
                cursor = db.query(ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                    throw new IllegalArgumentException("Cannot query unknown uri " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
       cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public Uri insert( Uri uri, ContentValues values) {
        int match = sUrimatcher.match(uri);

        switch (match){
            case PRODUCTS:
                return InsertProduct(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri InsertProduct(Uri uri , ContentValues values){

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        String supplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);

        if (name==null){
            throw new IllegalArgumentException("Product requires a name");
        }
        if (supplier==null){
            throw new IllegalArgumentException("Product requires a supplier");
        }
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires valid price");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        long id = database.insert(ProductEntry.TABLE_NAME,null,values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);

    }



    @Override
    public int update( Uri uri,ContentValues values,String selection, String[] selectionArgs) {
        int match = sUrimatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return UpdateProduct(uri,values,selection,selectionArgs);
            case PRODUCT_ID:
                selection= ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return UpdateProduct(uri,values,selection,selectionArgs);
            default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int UpdateProduct(Uri uri , ContentValues values,String selection, String[] selectionArgs ) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        String supplier = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);


            // If there are no values to update, then don't try to update the database
            if (values.size() == 0) {
                return 0;
            }

            int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);


            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                 getContext().getContentResolver().notifyChange(uri,null);
            }


            return rowsUpdated;

    }



    @Override
    public int delete(Uri uri, String selection,String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        int match = sUrimatcher.match(uri);
        switch (match){
            case PRODUCTS:
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;
                default:
             throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;
    }


    @Override
    public String getType( Uri uri) {
        final int match = sUrimatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
