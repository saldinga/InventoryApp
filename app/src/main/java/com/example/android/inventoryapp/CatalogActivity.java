package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookDbHelper;

import org.apache.commons.lang3.StringUtils;

public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatButton = findViewById(R.id.floatButton);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        displayInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayInfo();
    }

    public Cursor queryData() {

        BookDbHelper bookDbHelper = new BookDbHelper(this);
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();

        String[] projection = {BookEntry.ID, BookEntry.COLUMN_PRODUCT_NAME, BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY, BookEntry.COLUMN_SUPPLIER_NAME, BookEntry.COLUMN_SUPPLIER_TEL_NUMBER};

        return db.query(BookEntry.TABLE_NAME, projection, null, null,
                null, null, null);
    }

    public void displayInfo() {

        TextView info = findViewById(R.id.info_text);
        Cursor cursor = queryData();
        info.setText("Table contains " + cursor.getCount() + " rows.\n\n");

        info.append(StringUtils.join(cursor.getColumnNames(), " - "));
        info.append("\n\n");

        while (cursor.moveToNext()) {

            Log.i("after while", "start while");

            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                int type = cursor.getType(i);
                switch (type) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        stringBuilder.append(cursor.getInt(i));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        stringBuilder.append(cursor.getString(i));
                        break;
                }
                if (i != (cursor.getColumnCount() - 1)) stringBuilder.append(" - ");
            }

            info.append(stringBuilder.toString());
            info.append("\n");
        }
        cursor.close();
    }
}
