package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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

    //If not null, certain book
    private Uri mCurrentBook;

    //
    private boolean mBookHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mCurrentBook = getIntent().getData();

        if (mCurrentBook == null) {
            setTitle(R.string.action_insert);
        } else {
            setTitle(R.string.action_edit);
        }

        mTitle = findViewById(R.id.book_title_edittext);
        mPrice = findViewById(R.id.book_price_edittext);
        mQuantity = findViewById(R.id.quantity_edittext);
        mSupplierName = findViewById(R.id.supplier_name_edittext);
        mSuppTelNumber = findViewById(R.id.supplier_tel_edittext);

        mTitle.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSuppTelNumber.setOnTouchListener(mTouchListener);


        if (mCurrentBook != null) {
            getLoaderManager().initLoader(0, null, this);
        }

        Button decreaseQuantity = findViewById(R.id.decrease_quantity);
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantity.getText().toString();
                if (!quantityString.equals("")) {
                    int newQuantity = Integer.parseInt(quantityString);
                    if (newQuantity > 0) {
                        newQuantity--;
                        mQuantity.setText(String.valueOf(newQuantity));
                    } else {
                        Toast.makeText(EditorActivity.this, getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button increaseQuantity = findViewById(R.id.increase_quantity);
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String quantityString = mQuantity.getText().toString();
                if (!quantityString.equals("")) {
                    int newQuantity = Integer.parseInt(quantityString);
                    newQuantity++;
                    mQuantity.setText(String.valueOf(newQuantity));
                }
            }
        });

        Button callSupplier = findViewById(R.id.call_supplier);
        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String telNumber = mSuppTelNumber.getText().toString();
                if (!telNumber.equals("")) {
                    intent.setData(Uri.parse("tel:" + telNumber));
                    startActivity(intent);
                }
            }
        });
    }

    public boolean insertData() {

        boolean success = true;

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, mTitle.getText().toString().trim());

        String priceString = mPrice.getText().toString().trim();
        if (priceString.equals("")) {
            priceString = null;
            values.put(BookEntry.COLUMN_PRICE, priceString);
        } else {
            values.put(BookEntry.COLUMN_PRICE, Integer.parseInt(priceString));
        }

        String quantityString = mQuantity.getText().toString().trim();
        if (quantityString.equals("")) {
            quantityString = null;
            values.put(BookEntry.COLUMN_QUANTITY, quantityString);
        } else {
            values.put(BookEntry.COLUMN_QUANTITY, Integer.parseInt(quantityString));
        }

        values.put(BookEntry.COLUMN_SUPPLIER_NAME, mSupplierName.getText().toString().trim());
        values.put(BookEntry.COLUMN_SUPPLIER_TEL_NUMBER, mSuppTelNumber.getText().toString().trim());

        Uri uri = null;
        int rows = 0;
        if (mCurrentBook != null) {
            Log.i("mCurrentBook", "" + mCurrentBook);
            rows = getContentResolver().update(mCurrentBook, values, null, null);
        } else {
            uri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
        }

        if (uri != null) {
            Toast.makeText(this, getString(R.string.insert_message_success), Toast.LENGTH_SHORT).show();
        } else if (rows > 0) {
            Toast.makeText(this, getString(R.string.update_message_success), Toast.LENGTH_SHORT).show();
        } else if (mCurrentBook != null) {
            Toast.makeText(this, getString(R.string.update_message_fail), Toast.LENGTH_SHORT).show();
            success = false;
        } else {
            Toast.makeText(this, getString(R.string.insert_message_fail), Toast.LENGTH_SHORT).show();
            success = false;
        }

        return success;
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
                boolean success = insertData();
                if (success) finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentBook == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (mCurrentBook != null) {
            int rows = getContentResolver().delete(mCurrentBook, null, null);
            if (rows > 0) {
                Toast.makeText(this, R.string.delete_message_success, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.delete_message_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {BookEntry.ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_TEL_NUMBER};
        return new CursorLoader(this, mCurrentBook, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (((cursor != null) && (cursor.getCount() > 0))) {
            cursor.moveToFirst();
            Log.i("COUNT", String.valueOf(cursor.getColumnCount()));

            mTitle.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME)));
            mQuantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY))));
            mPrice.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_PRICE))));
            mSupplierName.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME)));
            mSuppTelNumber.setText(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_TEL_NUMBER)));

            cursor.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitle.getText().clear();
        mQuantity.getText().clear();
        mPrice.getText().clear();
        mSupplierName.getText().clear();
        mSuppTelNumber.getText().clear();
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mBookHasChanged boolean to true.

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
