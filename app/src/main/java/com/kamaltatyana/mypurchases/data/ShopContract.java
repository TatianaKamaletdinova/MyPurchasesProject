package com.kamaltatyana.mypurchases.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ShopContract {

    private ShopContract(){}

    /**
     * Building URI
     */
    // authority
    public static final String CONTENT_AUTHORITY = "com.kamaltatyana.mypurchases";
    // base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // path to table name
    public static final String PATH_SHOPS = "shops";

    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single product.
     */

    public static final class  ShopEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of shop.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOPS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single shop.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOPS;

        /**
         * The content URI to access the product data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHOPS);

        /**
         * Name of database table for products
         */
        public final static String TABLE_NAME = "shops";

        /**
         * Unique ID number for the product (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_SHOP_NAME = "name_shop";

        /**
         * Count of the product.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_SHOP_COUNT = "count_shop";

        /**
         * Price of the product.
         * <p>
         * Type: REAL
         */
        public final static String COLUMN_SHOP_PRICE = "price";

        /**
         * Possible values for the grade of the product.
         */
        public static final int COUNT_ONE = 0;
        public static final int COUNT_TWO = 1;
        public static final int COUNT_THREE = 2;

        /**
         * Returns whether or not the given grade is {@link #COUNT_ONE}, {@link #COUNT_TWO},
         * or {@link #COUNT_THREE}.
         */
        public static boolean isValidGrade(int grade) {
            if (grade == COUNT_ONE || grade == COUNT_TWO || grade == COUNT_THREE) {
                return true;
            }
            return false;
        }

    }
}
