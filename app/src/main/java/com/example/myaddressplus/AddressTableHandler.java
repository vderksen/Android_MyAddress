package com.example.myaddressplus;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressTableHandler {
    // Database table
    public static final String TABLE_ADRESSES = "adresses";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_FNAME = "firstName";
    public static final String COLUMN_LNAME = "lastName";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PROVINCE = "province";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_POSTALCODE = "postalCode";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_ADRESSES
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_FNAME + " text not null, "
            + COLUMN_LNAME + " text not null, "
            + COLUMN_ADDRESS + " text not null, "
            + COLUMN_PROVINCE + " text not null, "
            + COLUMN_COUNTRY + " text not null, "
            + COLUMN_POSTALCODE + " text not null "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(AddressTableHandler.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ADRESSES);
        onCreate(database);
    }
}
