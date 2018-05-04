package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

public final class BookContract {

    public static final class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";
        public static final String ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_TEL_NUMBER = "supplier_telephone_number";
    }
}
