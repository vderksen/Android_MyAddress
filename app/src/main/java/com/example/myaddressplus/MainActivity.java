package com.example.myaddressplus;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String TAG = "MyAddressPlus";

        private static final int ACTIVITY_CREATE = 0;
        private static final int ACTIVITY_EDIT = 1;
        private static final int DELETE_ID = Menu.FIRST + 1;
        // private Cursor cursor;
        private SimpleCursorAdapter adapter;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"Valentina Derksen 153803184. Main Activity Started");
        setContentView(R.layout.activity_main);
        this.getListView().setDividerHeight(2);
        fillData(); // populate the list of tasks
        registerForContextMenu(getListView()); // LONG PRESS on a list or summary of item row
        }

// Create the OPTION menu based on the XML defintion
@Override
public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
        }

// Reaction to the menu selection
@Override
public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.insert:
        createTodo();
        return true;

        case R.id.about:
                Intent a = new Intent(this, About.class);
                startActivity(a);
                break;
        }
        return super.onOptionsItemSelected(item);
        }

// Create and manage CONTEXT menu from a LONG PRESS of a list row
@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        }

@Override
public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case DELETE_ID:
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Uri uri = Uri.parse(MyAddressContentProvider.CONTENT_URI + "/" + info.id);
        getContentResolver().delete(uri, null, null);
        fillData();
        return true;
        }
        return super.onContextItemSelected(item);
        }

private void createTodo() {
        Intent i = new Intent(this, AddressDetailActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
        }

// Opens the second activity if an entry is clicked
@Override
protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, AddressDetailActivity.class);
        Uri todoUri = Uri.parse(MyAddressContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(MyAddressContentProvider.CONTENT_ITEM_TYPE, todoUri);

        // Activity returns an result if called with startActivityForResult
        startActivityForResult(i, ACTIVITY_EDIT);
        }

// Called with the result of the other activity
// requestCode was the origin request code send to the activity
// resultCode is the return code, 0 is everything is ok
// intend can be used to get data
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        }

private void fillData() {
        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { AddressTableHandler.COLUMN_FNAME };
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.name };

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.address_row, null, from, to, 0);

        setListAdapter(adapter);
        }

// Creates a new loader after the initLoader () call
// @Overrides
public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { AddressTableHandler.COLUMN_ID, AddressTableHandler.COLUMN_FNAME };
        CursorLoader cursorLoader = new CursorLoader(this, MyAddressContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
        }

// @Override
public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        }

// @Override
public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        }
}