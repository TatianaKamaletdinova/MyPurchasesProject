package com.kamaltatyana.mypurchases;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kamaltatyana.mypurchases.data.ShopContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_SHOP_LOADER = 0;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText priceEditText;

    /** EditText field to enter the pet's gender */
    private Spinner countEditText;

    private int mCountShop = 0;
    private Uri mCurrentShopUri;

    private boolean mShopHasChanged = false;

    boolean sRequiredValues = false;
    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentShopUri = intent.getData();
        if(mCurrentShopUri == null) {
            setTitle(getString(R.string.add_activity_title_new_shop));
            invalidateOptionsMenu();
        }
        else{
            setTitle(getString(R.string.editor_activity_title_new_shop));
            getLoaderManager().initLoader(EXISTING_SHOP_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        priceEditText = (EditText) findViewById(R.id.edit_shop_price);
        countEditText = (Spinner) findViewById(R.id.shop_count);


        mNameEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        countEditText.setOnTouchListener(mTouchListener);


         setupSpinner();
    }

   //  * Setup the dropdown spinner that allows the user to select the gender of the pet.

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        countEditText.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        countEditText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mCountShop = ShopContract.ShopEntry.COUNT_TWO; // 2
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mCountShop = ShopContract.ShopEntry.COUNT_THREE; // 3
                    } else {
                        mCountShop = ShopContract.ShopEntry.COUNT_ONE; // 1
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCountShop = 0; // Unknown
            }
        });
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
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveShop();
                if (sRequiredValues == true) {
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mShopHasChanged) {
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




    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mShopHasChanged = true;
            return false;
        }
    };

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

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mShopHasChanged) {
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

    private boolean saveShop(){

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        //  int weight = Integer.parseInt(weightString);
        if (mCurrentShopUri == null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(priceString) &&
                mCountShop == ShopContract.ShopEntry.COUNT_ONE) {
              //  TextUtils.isEmpty(weightString) && mCountShop == ShopContract.ShopEntry.GENDER_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            sRequiredValues = true;
            return sRequiredValues;
        }

        ContentValues values = new ContentValues();

        values.put(ShopContract.ShopEntry.COLUMN_SHOP_NAME, nameString);
        values.put(ShopContract.ShopEntry.COLUMN_SHOP_PRICE, priceString);
        // REQUIRED VALUES
        // Validation section
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, getString(R.string.validation_msg_product_name), Toast.LENGTH_SHORT).show();
            return sRequiredValues;
        } else {
            values.put(ShopContract.ShopEntry.COLUMN_SHOP_NAME, nameString);
        }

        float price = 0;
        float priseOneProduct = 0;
        if (!TextUtils.isEmpty(priceString)) {
           /* switch (mCountShop){
                case 0:
                    mCountShop =1;
                    break;
                case 1:
                    mCountShop = 2;
                    break;
                case  2:
                    mCountShop=3;
                    break;
            }*/


            price = Float.parseFloat(priceString);
            priseOneProduct = mCountShop *price;
        }
        values.put(ShopContract.ShopEntry.COLUMN_SHOP_PRICE, priseOneProduct);
        values.put(ShopContract.ShopEntry.COLUMN_SHOP_COUNT, mCountShop);

        if (mCurrentShopUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(ShopContract.ShopEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),  Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentShopUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this,  getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
            }
        }
        sRequiredValues = true;
        return sRequiredValues;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentShopUri == null) {
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
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteShop();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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


    private void deleteShop() {
        int rowsAffected = getContentResolver().delete(mCurrentShopUri, null, null);
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this,  getString(R.string.editor_delete_pet_failed), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_pet_successful), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ShopContract.ShopEntry._ID,
                ShopContract.ShopEntry.COLUMN_SHOP_NAME,
                ShopContract.ShopEntry.COLUMN_SHOP_COUNT,
                ShopContract.ShopEntry.COLUMN_SHOP_PRICE,

            //    ShopContract.ShopEntry.COLUMN_PET_GENDER
        };
        return new CursorLoader(
                this,
                mCurrentShopUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_SHOP_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_SHOP_PRICE);
            int countColumnIndex = cursor.getColumnIndex(ShopContract.ShopEntry.COLUMN_SHOP_COUNT);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int count = cursor.getInt(countColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            priceEditText.setText(Float.toString(price));

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
           switch (count) {
                case ShopContract.ShopEntry.COUNT_TWO:
                    countEditText.setSelection(1);
                    break;
                case ShopContract.ShopEntry.COUNT_THREE:
                    countEditText.setSelection(2);
                    break;
               case ShopContract.ShopEntry.COUNT_ONE:
                    countEditText.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        priceEditText.setText("");
        countEditText.setSelection(0); // Select "Unknown" gender
    }
}