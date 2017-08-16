package com.example.safsaf.inventoryapp.data;

/**
 * Created by Safsaf on 8/16/2017.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.safsaf.inventoryapp.data.ProductContract.productEntry;
/**
 * {@link ContentProvider} for products app.
 */

public class ProductProvider extends ContentProvider {

    /** URI matcher code for the content URI for the products table */
    private static final int PRODUCTS= 100;

    /** URI matcher code for the content URI for a single product in the Products table */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.safsaf.inventoryapp/products" will map to the
        // integer code {@link #PRODUCTS}. This URI is used to provide access to MULTIPLE rows
        // of the products table.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }


    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */

    /** Database helper object*/

    private ProductDbHelper mDbHelper;
    @Override
    public boolean onCreate() {

        mDbHelper =new ProductDbHelper(getContext());
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.


        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor= null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(productEntry.TABLE_NAME, projection, selection, selectionArgs,
                                                null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = productEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(productEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }
    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
                switch (match) {
                    case PRODUCTS:
                        return insertProduct(uri, contentValues);
                    default:
                        throw new IllegalArgumentException("Insertion is not supported for " + uri);
                }
            }

    /**
      * Insert a pet into the database with the given content values. Return the new content URI
      * for that specific row in the database.
      */
            private Uri insertProduct(Uri uri, ContentValues values) {

                // Check that the name is not null
                String name = values.getAsString(productEntry.COLUMN_PRODUCT_NAME);
                if (name == null || name.length()==0) {
                    throw new IllegalArgumentException("Product requires a name");
                }
                // If the weight is provided, check that it's greater than or equal to 0 kg
                Integer price = values.getAsInteger(productEntry.COLUMN_PRODUCT_PRICE);
                if(price==null || price<=0) {
                    throw new IllegalArgumentException("Product requires valid price");
                }
                // If the weight is provided, check that it's greater than or equal to 0 kg
                Integer quantity = values.getAsInteger(productEntry.COLUMN_PRODUCT_QUANTITY);
                if(quantity==null || quantity<=0) {
                    throw new IllegalArgumentException("Product requires valid quantity ");
                }
                byte[] image = values.getAsByteArray(productEntry.COLUMN_PRODUCT_IMAGE);
                if(image==null ) {
                    throw new IllegalArgumentException("Product requires valid image ");
                }
                // Get writeable database
                        SQLiteDatabase database = mDbHelper.getWritableDatabase();

                // Insert the new pet with the given values
                long id = database.insert(productEntry.TABLE_NAME, null, values);
                // If the ID is -1, then the insertion failed. Log an error and return null.
                if (id == -1) {
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                    return null;
                }

                // Return the new URI with the ID (of the newly inserted row) appended at the end
                return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}