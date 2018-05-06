package com.kamaltatyana.mypurchases;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kamaltatyana.mypurchases.data.ShopContract;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int SHOP_LOADER = 0;
    TextView priceAl;
    ShopCursorAdapter adapter;
    float price = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView shopListView = (ListView)findViewById(R.id.list);

        priceAl = (TextView)findViewById(R.id.price_all);
        View emptyView = findViewById(R.id.empty_view);
        shopListView.setEmptyView(emptyView);

        adapter = new ShopCursorAdapter(this, null);
        shopListView.setAdapter(adapter);
        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this,  EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(ShopContract.ShopEntry.CONTENT_URI,  id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(SHOP_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            deleteAllShops();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllShops() {

        String[] projection = {
                "SUM("+ ShopContract.ShopEntry.COLUMN_SHOP_PRICE+")",
        };

  //     Cursor rowsDeleteds = getContentResolver().query(ShopContract.ShopEntry.CONTENT_URI , projection, null, null,null);



        int rowsDeleted = getContentResolver().delete(ShopContract.ShopEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ShopContract.ShopEntry._ID,
                ShopContract.ShopEntry.COLUMN_SHOP_NAME,
                ShopContract.ShopEntry.COLUMN_SHOP_COUNT,
                ShopContract.ShopEntry.COLUMN_SHOP_PRICE

        };
        return new CursorLoader(this, ShopContract.ShopEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        price = 0;
        int priceColumnIndex = data.getColumnIndex(ShopContract.ShopEntry.COLUMN_SHOP_PRICE);
        //float prices = data.getFloat(priceColumnIndex);
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                price = price + data.getInt(priceColumnIndex);
            }
            priceAl.setText(Float.toString(price));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}
