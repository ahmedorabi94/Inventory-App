package com.example.ahmedorabi.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ahmedorabi.inventoryapp.data.ProductContract.ProductEntry;
/**
 * Created by Ahmed Orabi on 5/23/2018.
 */

public class ProductCursorAdapter extends CursorAdapter {

    TextView product_name;
    TextView product_price;
    TextView product_quantity;
    ImageView product_image;
    ImageButton reduce_buttob;
    private Context mContext;
    Uri currentUri;


    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_view_item,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        product_image = view.findViewById(R.id.product_image);
        product_name = view.findViewById(R.id.product_name);
        product_price= view.findViewById(R.id.product_price);
        product_quantity = view.findViewById(R.id.product_quantity);
        reduce_buttob = view.findViewById(R.id.img_reduce_quantity);

        final int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

        String p_name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        byte [] img = cursor.getBlob(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));


        product_name.setText(p_name);
        product_price.setText(String.valueOf(price) + " $");
        product_quantity.setText(String.valueOf(quantity));

        Bitmap bitmap = BitmapFactory.decodeByteArray(img,0,img.length);
        product_image.setImageBitmap(bitmap);



        reduce_buttob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity >0){
                    int newQuantity = quantity -1;
                    currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,id);
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    mContext.getContentResolver().update(currentUri,values,null,null);

                }
            }
        });



    }
}
