package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView productNameTV = view.findViewById(R.id.product_name);
        TextView productPriceTV = view.findViewById(R.id.product_price);
        TextView productQuantityTV = view.findViewById(R.id.product_quantity);

        String productNameDB = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        String productPriceDB = String.valueOf(cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_PRICE)));
        final int quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
        String productQuantityDB = String.valueOf(quantity);
        final int position = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));

        productNameTV.setText(productNameDB);
        productPriceTV.setText(productPriceDB);
        productQuantityTV.setText(productQuantityDB);

        ImageButton salesButton = view.findViewById(R.id.sale_button);
        salesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, position);

                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    values.put(BookEntry.COLUMN_QUANTITY, newQuantity);
                    context.getContentResolver().update(uri, values, null, null);
                } else {
                    Toast.makeText(context, context.getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
