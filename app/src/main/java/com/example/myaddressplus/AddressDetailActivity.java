package com.example.myaddressplus;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AddressDetailActivity extends Activity {

    private static final String TAG = "MyAddressPlus";

    private Spinner titlesOptions, provincesOptions;
    private EditText fName, lName, address, country, postalCode;
    Button submitBtn;

    private Uri todoUri;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        Log.i(TAG,"Valentina Derksen 153803184. Detail Activity Started");
        setContentView(R.layout.address_edit);

        provincesOptions = (Spinner) findViewById(R.id.SpinnerProvinces);
        titlesOptions = (Spinner) findViewById(R.id.SpinnerTitles);

        fName = (EditText) findViewById(R.id.input_FName);
        lName = (EditText) findViewById(R.id.input_LName);
        address = (EditText) findViewById(R.id.input_address);
        country = (EditText) findViewById(R.id.input_country);
        postalCode = (EditText) findViewById(R.id.input_postal_code);

        submitBtn = (Button) findViewById(R.id.btn_submit);

        Bundle extras = getIntent().getExtras();

        // Check from the saved Instance
        todoUri = (bundle == null) ? null : (Uri) bundle.getParcelable(MyAddressContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            todoUri = extras.getParcelable(MyAddressContentProvider.CONTENT_ITEM_TYPE);
            fillData(todoUri);
        }
        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!isValid()) {
                    makeToast();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        });
    }

    private boolean isValid(){
        if(TextUtils.isEmpty(fName.getText())){
            return false;
        } else if (TextUtils.isEmpty(lName.getText())){
            return false;
        } else if (TextUtils.isEmpty(address.getText())) {
            return false;
        } else if (TextUtils.isEmpty(country.getText())) {
            return false;
        } else if (TextUtils.isEmpty(postalCode.getText())) {
            return false;
        } else return true;
    }

    private void makeToast() {
        Toast.makeText(AddressDetailActivity.this, "Please fill all fileds",Toast.LENGTH_LONG).show();
    }

    private void fillData(Uri uri) {
        String[] projection = { AddressTableHandler.COLUMN_TITLE, AddressTableHandler.COLUMN_FNAME, AddressTableHandler.COLUMN_LNAME,
                                AddressTableHandler.COLUMN_ADDRESS, AddressTableHandler.COLUMN_PROVINCE,
                                AddressTableHandler.COLUMN_COUNTRY, AddressTableHandler.COLUMN_POSTALCODE};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String title = cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_TITLE));
            for (int i = 0; i < titlesOptions.getCount(); i++) {
                String s = (String) titlesOptions.getItemAtPosition(i);
                if (s.equalsIgnoreCase(title)) {
                    titlesOptions.setSelection(i);
                }
            }
            String province = cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_PROVINCE));
            for (int i = 0; i < provincesOptions.getCount(); i++) {
                String s = (String) provincesOptions.getItemAtPosition(i);
                if (s.equalsIgnoreCase(province)) {
                    provincesOptions.setSelection(i);
                }
            }

            fName.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_FNAME)));
            lName.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_LNAME)));
            address.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_ADDRESS)));
            country.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_COUNTRY)));
            postalCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(AddressTableHandler.COLUMN_POSTALCODE)));

            // Always close the cursor
            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(MyAddressContentProvider.CONTENT_ITEM_TYPE, todoUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String titleStr = (String) titlesOptions.getSelectedItem();
        String provinceStr = (String) provincesOptions.getSelectedItem();
        String fnameStr = fName.getText().toString();
        String lnameStr = lName.getText().toString();
        String addressStr = address.getText().toString();
        String countryStr = country.getText().toString();
        String postalCodeStr = postalCode.getText().toString();

        // Only save if either summary or description
        // is available
        if (fnameStr.length() == 0 && lnameStr.length() == 0 && addressStr.length() == 0
                && countryStr.length() == 0 && postalCodeStr.length() == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AddressTableHandler.COLUMN_TITLE, titleStr);
        values.put(AddressTableHandler.COLUMN_PROVINCE, provinceStr);
        values.put(AddressTableHandler.COLUMN_FNAME, fnameStr);
        values.put(AddressTableHandler.COLUMN_LNAME, lnameStr);
        values.put(AddressTableHandler.COLUMN_ADDRESS, addressStr);
        values.put(AddressTableHandler.COLUMN_COUNTRY, countryStr);
        values.put(AddressTableHandler.COLUMN_POSTALCODE, postalCodeStr);

        if (todoUri == null) {
            // New ToDo
            todoUri = getContentResolver().insert(MyAddressContentProvider.CONTENT_URI, values);
        } else {
            // Update ToDo
            getContentResolver().update(todoUri, values, null, null);
        }
    }

}
