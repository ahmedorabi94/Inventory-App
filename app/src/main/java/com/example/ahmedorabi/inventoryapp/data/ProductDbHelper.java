package com.example.ahmedorabi.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ahmedorabi.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Ahmed Orabi on 5/23/2018.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 2;


    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                 + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                 + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                 + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL, "
                 + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER, "
                 + ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT, "
                 + ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + " TEXT, "
                 + ProductEntry.COLUMN_PRODUCT_IMAGE + " BLOB );";


         db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
            onCreate(db);
    }
}
