package imastudio.co.id.loader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import imastudio.co.id.loader.adapter.ContactAdapter;
import imastudio.co.id.loader.permission.PermissionManager;

//TODO 4 Implement Method
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    public static final String TAG = "ContactApp";
    public final int CONTACT_LOAD_ID = 110;
    public final int CONTACT_PHONE_ID = 120;
    private ListView lvContact;
    private ProgressBar progressBar;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvContact = findViewById(R.id.lv_contact);
        progressBar = findViewById(R.id.pb_bar);
        lvContact.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        adapter = new ContactAdapter(MainActivity.this, null, true);
        lvContact.setAdapter(adapter);
        lvContact.setOnItemClickListener(this);

        // permission
        if(PermissionManager.isGranted(this, Manifest.permission.READ_CONTACTS,CONTACT_REQUEST_CODE)) {
            getSupportLoaderManager().initLoader(CONTACT_LOAD_ID, null, this);
            progressBar.setVisibility(View.VISIBLE);

        }else {
            PermissionManager.check(this, Manifest.permission.READ_CONTACTS,CONTACT_REQUEST_CODE);
        }
    }

    //TODO 5 Eksekusi Load Contact
    //background
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader cursorLoader = null;

        // load data contact dari database
        if (id == CONTACT_LOAD_ID) {
            String projectionFields[] = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_URI};
            cursorLoader = new CursorLoader(MainActivity.this,
                    ContactsContract.Contacts.CONTENT_URI,
                    projectionFields, ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                    null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC ");
        }
        // query databse contact dijalankan
        if (id == CONTACT_PHONE_ID) {
            String[] phoneProjectionFields = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            cursorLoader = new CursorLoader(MainActivity.this, ContactsContract.CommonDataKinds
                    .Phone.CONTENT_URI, phoneProjectionFields,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + " AND " +
                            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1",
                    new String[]{bundle.getString("id")}, null);
        }
        return cursorLoader;
    }

    // hasil query dari database dikirim kesini
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "LoadFinished");
        if (loader.getId() == CONTACT_LOAD_ID) {
            if (data.getCount() > 0) {
                lvContact.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adapter.swapCursor(data);
            }
        }
        if (loader.getId() == CONTACT_PHONE_ID) {
            String contactNumber = null;
            if (data.moveToFirst()) {
                contactNumber = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            Intent dialNumber = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactNumber));
            startActivity(dialNumber);
        }
    }

    // implement dari TODO 4
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CONTACT_LOAD_ID) {
            lvContact.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            adapter.swapCursor(null);
            Log.d(TAG, "LoaderReset");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // implement dari TODO 4
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);
        long contactId = cursor.getLong(0);
        Log.d(TAG, "Postition : " + position + "" + contactId);

        //TODO 6 Create Method
        getPhoneNumber(String.valueOf(contactId));
    }

    private void getPhoneNumber(String contact) {
        Bundle bundle = new Bundle();
        bundle.putString("id", contact);
        getSupportLoaderManager().restartLoader(CONTACT_PHONE_ID, bundle, this);
    }

    // request callback dari user
    final int CONTACT_REQUEST_CODE = 100;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CONTACT_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportLoaderManager().initLoader(CONTACT_LOAD_ID, null, this);
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Permission Contact Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Contact Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
