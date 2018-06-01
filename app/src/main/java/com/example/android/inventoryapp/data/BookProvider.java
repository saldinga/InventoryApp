package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.BookContract.BookEntry;


public class BookProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;
    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        Log.i("CURSOR", "query");
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.i("CURSOR count", "" + cursor.getCount());
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.insert_uri_fail) + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {

        boolean goodName = checkBookName(values);
        if (!goodName) return null;

        boolean goodPrice = checkBookPrice(values);
        if (!goodPrice) return null;

        boolean goodQuantity = checkBookQuantity(values);
        if (!goodQuantity) return null;

        boolean goodSupplierName = checkSupplierName(values);
        if (!goodSupplierName) return null;

        boolean goodSupplierTel = checkSupplierTel(values);
        if (!goodSupplierTel) return null;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String table = uri.getPath();
        int cut = table.lastIndexOf('/');
        if (cut != -1) {
            table = table.substring(cut + 1);
        }

        long id = database.insert(table, null, values);
        getContext().getContentResolver().notifyChange(uri, null);

        if (id != -1) {
            // Once we know the ID of the new row in the table,
            // return the new URI with the ID appended to the end of it
            return ContentUris.withAppendedId(uri, id);
        } else {
            return null;
        }
    }

    private boolean checkBookName(ContentValues values) {

        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null || name.equals("")) {
                Toast.makeText(getContext(), getContext().getString(R.string.name_fail), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean checkBookPrice(ContentValues values) {

        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (price == null) {
                Toast.makeText(getContext(), getContext().getString(R.string.price_null_fail), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (price < 0) {
                Toast.makeText(getContext(), getContext().getString(R.string.price_negative_fail), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean checkBookQuantity(ContentValues values) {

        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                Toast.makeText(getContext(), getContext().getString(R.string.quantity_null_fail), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (quantity < 0) {
                Toast.makeText(getContext(), getContext().getString(R.string.quantity_negative_fail), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean checkSupplierName(ContentValues values) {

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (name == null || name.equals("")) {
                Toast.makeText(getContext(), getContext().getString(R.string.supplier_name_fail), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean checkSupplierTel(ContentValues values) {

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_TEL_NUMBER)) {
            String name = values.getAsString(BookEntry.COLUMN_SUPPLIER_TEL_NUMBER);
            if (name == null || name.equals("")) {
                Toast.makeText(getContext(), getContext().getString(R.string.sup_tel_fail), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                return database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
            case BOOK:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
            default:
                Toast.makeText(getContext(), getContext().getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
                return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.updated_not_supported) + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        boolean goodName = checkBookName(values);
        if (!goodName) return 0;

        boolean goodPrice = checkBookPrice(values);
        if (!goodPrice) return 0;

        boolean goodQuantity = checkBookQuantity(values);
        if (!goodQuantity) return 0;

        boolean goodSupplierName = checkSupplierName(values);
        if (!goodSupplierName) return 0;

        boolean goodSupplierTel = checkSupplierTel(values);
        if (!goodSupplierTel) return 0;

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri, null);

        return database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
    }
}
