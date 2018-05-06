package com.kamaltatyana.mypurchases;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.kamaltatyana.mypurchases.data.ShopContract.ShopEntry;

public class ShopCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ShopCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ShopCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.product_price);
          TextView countTextView = (TextView) view.findViewById(R.id.count);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ShopEntry.COLUMN_SHOP_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ShopEntry.COLUMN_SHOP_PRICE);
         int countColumnIndex = cursor.getColumnIndex(ShopEntry.COLUMN_SHOP_COUNT);

        // Read the pet attributes from the Cursor for the current pet
        String shopName = cursor.getString(nameColumnIndex);
        String shopBreed = cursor.getString(priceColumnIndex);
        String shopCount = cursor.getString(countColumnIndex);

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(shopName);
        summaryTextView.setText(shopBreed);
        countTextView.setText(shopCount);
    }
}
