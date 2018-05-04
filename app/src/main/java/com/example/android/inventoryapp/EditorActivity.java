package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;
import com.example.android.inventoryapp.data.BookDbHelper;

public class EditorActivity extends AppCompatActivity {

    //Book title
    private EditText mTitle;

    //Book price
    private EditText mPrice;

    //Book quantity
    private EditText mQuantity;

    //Suppliers name
    private EditText mSupplierName;

    //Supplier phone number
    private EditText mSuppTelNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mTitle = findViewById(R.id.book_title_edittext);
        mPrice = findViewById(R.id.book_price_edittext);
        mQuantity = findViewById(R.id.quantity_edittext);
        mSupplierName = findViewById(R.id.supplier_name_edittext);
        mSuppTelNumber = findViewById(R.id.supplier_tel_edittext);
    }

    public void insertData() {

        BookDbHelper bookDbHelper = new BookDbHelper(this);
        SQLiteDatabase db = bookDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, mTitle.getText().toString().trim());
        values.put(BookEntry.COLUMN_PRICE, Integer.parseInt(mPrice.getText().toString().trim()));
        values.put(BookEntry.COLUMN_QUANTITY, Integer.parseInt(mQuantity.getText().toString().trim()));
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, mSupplierName.getText().toString().trim());
        values.put(BookEntry.COLUMN_SUPPLIER_TEL_NUMBER, mSuppTelNumber.getText().toString().trim());

        long result = db.insert(BookEntry.TABLE_NAME, null, values);

        if (result > -1) {
            Toast.makeText(this, "Book saved with id " + result, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "There was error saving a book", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                insertData();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
