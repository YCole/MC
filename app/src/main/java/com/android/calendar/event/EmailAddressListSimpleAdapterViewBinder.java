package com.android.calendar.event;

import java.util.ArrayList;
import java.util.HashMap;

import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.view.View;
import android.view.ViewParent;
import android.widget.SimpleAdapter;

public class EmailAddressListSimpleAdapterViewBinder implements
        SimpleAdapter.ViewBinder {

    public static final String[] PROJECTION = new String[] { Data._ID, // 0
            Contacts.DISPLAY_NAME, // 1
            Email.DATA, // 2
            Data.RAW_CONTACT_ID
    // "_id",
    // "display_name",
    // "data1"
    };
    public static final String[] DATA_PROJECTION = new String[] { Data._ID, // 0
            Contacts.DISPLAY_NAME, // 1
            Data.DATA11, Data.RAW_CONTACT_ID
    // "_id",
    // "display_name",
    // "data11",
    // "raw_contact_id"
    };
    public static final String[] GROUP_MEMBER_PROJECTION = new String[] {
            Data.RAW_CONTACT_ID, Data.DATA1, Contacts.DISPLAY_NAME,
            Data.MIMETYPE };
    public static final String[] GROUP_PROJECTION = new String[] { Groups._ID, // 0
            Groups.TITLE // 1
    };
    public static final String[] DATA_PROJECTION_EMAIL = new String[] {
            Data._ID, // 0
            Contacts.DISPLAY_NAME, // 1
            Data.DATA11, // 2
            Data.RAW_CONTACT_ID, // 3
            Data.DATA1, // 1 //4
            Data.MIMETYPE // 5
    // "_id",
    // "display_name",
    // "data11",
    // "raw_contact_id"
    };
    public final static String EMAIL_ADDRESS_CHECKED = "checked";
    public final static String EMAIL_ADDRESS_ID = PROJECTION[0];
    public final static String EMAIL_ADDRESS_DISPLAY_NAME = PROJECTION[1];
    public final static String EMAIL_ADDRESS = PROJECTION[2];
    public final static String EMAIL_NAME_PY = "py";

    public final static String GROUP_CHECKED = "checked";
    public final static String GROUP_ID = PROJECTION[0];
    public final static String GROUP_DISPLAY_NAME = PROJECTION[1];
    public final static String GROUP_EMAIL_ADDRESS = PROJECTION[2];

    public static final String SORT_ORDER = Contacts.DISPLAY_NAME + ","
            + EMAIL_ADDRESS;

    private ArrayList<HashMap<String, Object>> mData = null;

    public EmailAddressListSimpleAdapterViewBinder(
            ArrayList<HashMap<String, Object>> listItem) {
        mData = listItem;
    }

    public boolean setViewValue(View view, Object data,
            String textRepresentation) {
        // TODO Auto-generated method stub

        ViewParent parent = view.getParent();
        if (parent != null) {
            EmailAddressItemListItem item = (EmailAddressItemListItem) parent;
            item.setMapData(mData);
        }
        /*
         * if(parent!=null && view instanceof ImageView ) { ImageView
         * selectedView=(ImageView)view; EmailAddressItemListItem
         * item=(EmailAddressItemListItem)parent; if(item.mSelectedIconOn!=null)
         * { if(item.mSelected==true) {
         * selectedView.setImageDrawable(item.mSelectedIconOn); } else {
         * selectedView.setImageDrawable(item.mSelectedIconOff); } return true;
         * } else { return false; } }
         */
        return false;
    }

}
