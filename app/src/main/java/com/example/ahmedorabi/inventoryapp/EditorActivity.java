package com.example.ahmedorabi.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.ahmedorabi.inventoryapp.data.ProductContract.ProductEntry;
import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText ed_productName;
    EditText ed_productPrice;
    EditText ed_productSupplier;
    EditText ed_productQuantity;
    EditText ed_supplier_email;
    ImageButton ib_takeImage;
    ImageView iv_setImage;
    Uri currentUri;
    private static final int PRODUCT_LOADER = 0;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Bitmap bitmap;
    private boolean ProductHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ProductHasChanged=true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        ed_productName = findViewById(R.id.ed_product_name);
        ed_productPrice = findViewById(R.id.ed_product_price);
        ed_productSupplier = findViewById(R.id.ed_supplier);
        ed_supplier_email = findViewById(R.id.ed_supplier_email);
        ed_productQuantity = findViewById(R.id.ed_quantity);
        ib_takeImage = findViewById(R.id.ib_takeImage);
        iv_setImage = findViewById(R.id.iv_setImage);

        ed_productName.setOnTouchListener(mTouchListener);
        ed_productPrice.setOnTouchListener(mTouchListener);
        ed_productSupplier.setOnTouchListener(mTouchListener);
        ed_productQuantity.setOnTouchListener(mTouchListener);
        ed_supplier_email.setOnTouchListener(mTouchListener);

        Intent intent = getIntent();
        currentUri = intent.getData();

        if(currentUri == null){
            setTitle(getString(R.string.add_product));
            //delete option item can be hidden
            invalidateOptionsMenu();
        }else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
            ed_supplier_email.setEnabled(false);
        }






        ib_takeImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                   if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                       requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA_PERMISSION_CODE);
                   }else {
                       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                       startActivityForResult(intent,CAMERA_REQUEST);
                   }
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
         super.onPrepareOptionsMenu(menu);
        if(currentUri ==null){
            MenuItem item = menu.findItem(R.id.delete_product);
            MenuItem item1 = menu.findItem(R.id.order_product);
            item1.setVisible(false);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.save_product:
                SaveProduct();
                finish();
                return true;
            case R.id.delete_product:
                ShowDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if(!ProductHasChanged){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                ShowDialog();
                return true;

            case R.id.order_product:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,"eng.ahmedorabi94@gmail.com");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Order Product");
                emailIntent.putExtra(Intent.EXTRA_TEXT,"i need to make a new order of " + ed_productName.getText().toString());
                startActivity(emailIntent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void SaveProduct(){


        String name = ed_productName.getText().toString();
        String product_price = ed_productPrice.getText().toString();
        String supplier  =ed_productSupplier.getText().toString();
        String product_quantity = ed_productQuantity.getText().toString();
        String supplier_email = ed_supplier_email.getText().toString();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
        byte [] img = bos.toByteArray();


        if(currentUri==null && TextUtils.isEmpty(name)&&TextUtils.isEmpty(product_price)&&TextUtils.isEmpty(supplier)
               &&TextUtils.isEmpty(product_quantity)&&bitmap==null){
               return;
        }

        Integer quantity = Integer.valueOf(product_quantity);
        Double price = Double.valueOf(product_price);


        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME,name);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE,price);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER,supplier);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,supplier_email);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, img);


        if(currentUri == null){
            Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI,values);
               if(uri==null){
                   Toast.makeText(this, R.string.insert_failed,Toast.LENGTH_SHORT).show();
               }else {
                   Toast.makeText(this, R.string.product_saved , Toast.LENGTH_SHORT).show();
               }
        }else {
            int rowsUpdated = getContentResolver().update(currentUri,values,null ,null);
             if(rowsUpdated !=0){
                 Toast.makeText(this, R.string.product_updated , Toast.LENGTH_SHORT).show();
             }else {
                 Toast.makeText(this, R.string.product_update_failed , Toast.LENGTH_SHORT).show();
             }
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            bitmap = (Bitmap) data.getExtras().get("data");
            iv_setImage.setImageBitmap(bitmap);


        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String [] allColumn={
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_IMAGE
        };


        return new CursorLoader(this,currentUri,allColumn,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor ==null || cursor.getCount() < 0){
            return;
        }

        if (cursor.moveToFirst()) {
            ed_productName.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME)));
            ed_productSupplier.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER)));
            ed_supplier_email.setText(cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL)));
            int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            ed_productQuantity.setText(String.valueOf(quantity));
            double price = cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            ed_productPrice.setText(String.valueOf(price));
            byte[] img = cursor.getBlob(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));

            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            iv_setImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        ed_productName.setText("");
        ed_productPrice.setText("");
        ed_productSupplier.setText("");
        ed_supplier_email.setText("");
        ed_productQuantity.setText("");
        iv_setImage.setImageBitmap(null);
    }


    private void ShowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
            }
        });
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                 if(dialog !=null){
                     dialog.dismiss();
                 }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ShowDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_product_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 DeleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(dialog !=null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void DeleteProduct(){
        if(currentUri !=null){
            int row_id = getContentResolver().delete(currentUri,null,null);
            if(row_id != 0){
                Toast.makeText(this, R.string.product_delete_succ,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, R.string.product_delete_failed,Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {

        if(!ProductHasChanged){
            super.onBackPressed();
            return;
        }

        ShowDialog();

    }
}
