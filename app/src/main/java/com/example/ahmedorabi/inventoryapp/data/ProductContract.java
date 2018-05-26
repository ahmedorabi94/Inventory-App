package com.example.ahmedorabi.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ahmed Orabi on 5/22/2018.
 */

public final class ProductContract {


    public static final String CONTENT_AUTHORITY = "com.example.ahmedorabi.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PRODUCT_PATH="products";



    public static final class ProductEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PRODUCT_PATH);

        public static final String TABLE_NAME="product";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME ="name";
        public static final String COLUMN_PRODUCT_PRICE ="price";
        public static final String COLUMN_PRODUCT_QUANTITY ="quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER ="supplier";
        public static final String COLUMN_PRODUCT_SUPPLIER_EMAIL="supplier_email";
        public static final String COLUMN_PRODUCT_IMAGE ="image";



        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PRODUCT_PATH;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PRODUCT_PATH;


    }


}
