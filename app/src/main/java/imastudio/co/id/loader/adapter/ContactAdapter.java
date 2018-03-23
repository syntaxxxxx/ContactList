package imastudio.co.id.loader.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import imastudio.co.id.loader.R;

//TODO 1 Implement && Create Constructor
public class ContactAdapter extends CursorAdapter {
    public ContactAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    //TODO 2 Call layout item_contact
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
    }

    //TODO 3 Manipulasi Layout To Objek View
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            TextView tvName = view.findViewById(R.id.tv_item_name);
            CircleImageView imgUser = view.findViewById(R.id.img_user);
            RelativeLayout relativeLayout = view.findViewById(R.id.rv_item);
            imgUser.setImageResource(R.drawable.ic_account_circle_black_24dp);
            tvName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));

            if (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)) != null) {
                imgUser.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow
                        (ContactsContract.Contacts.PHOTO_URI))));
            } else {
                imgUser.setImageResource(R.drawable.ic_account_circle_black_24dp);
            }
        }
    }
}
