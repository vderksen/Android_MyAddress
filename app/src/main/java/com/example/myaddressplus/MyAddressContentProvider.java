package com.example.myaddressplus;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.HashSet;

public class MyAddressContentProvider extends ContentProvider {
    // perform CRUD operations on the SQL database file (located on the emulator)

    // reference to actual database
    private DatabaseHandler database;

    // Used for the UriMacher
    // to access and read individual records in database file
    private static final int ADDRESSES = 10;
    private static final int ADDRESS_ID = 20;

    private static final String AUTHORITY = "com.example.myaddressplus.addresses.contentprovider";

    private static final String BASE_PATH = "addresses";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/addresses";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/address";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, ADDRESSES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ADDRESS_ID);
    }


    @Override
    public boolean onCreate() {
        database = new DatabaseHandler(getContext());
        return false;
    }

    // SELECT all/ or SELECT
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(AddressTableHandler.TABLE_ADRESSES);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ADDRESSES:
                break;
            case ADDRESS_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(AddressTableHandler.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        //int rowsDeleted = 0;
        long id = 0;

        switch (uriType) {
            case ADDRESSES:
                id = sqlDB.insert(AddressTableHandler.TABLE_ADRESSES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case ADDRESSES:
                rowsDeleted = sqlDB.delete(AddressTableHandler.TABLE_ADRESSES, selection, selectionArgs);
                break;
            case ADDRESS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(AddressTableHandler.TABLE_ADRESSES, AddressTableHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(AddressTableHandler.TABLE_ADRESSES, AddressTableHandler.COLUMN_ID + "=" + id
                            + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case ADDRESSES:
                rowsUpdated = sqlDB.update(AddressTableHandler.TABLE_ADRESSES, values, selection, selectionArgs);
                break;
            case ADDRESS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(AddressTableHandler.TABLE_ADRESSES, values,
                            AddressTableHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(AddressTableHandler.TABLE_ADRESSES,
                            values, AddressTableHandler.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { AddressTableHandler.COLUMN_TITLE,
                AddressTableHandler.COLUMN_FNAME, AddressTableHandler.COLUMN_LNAME,
                AddressTableHandler.COLUMN_ADDRESS, AddressTableHandler.COLUMN_PROVINCE,
                AddressTableHandler.COLUMN_COUNTRY, AddressTableHandler.COLUMN_POSTALCODE,
                AddressTableHandler.COLUMN_ID };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
