package com.example.myaddressplus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class About extends AppCompatActivity {

    private static final String TAG = "MyAddressPlus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"Valentina Derksen 153803184. About Activity Started");
        setContentView(R.layout.activity_about);
    }
}