package com.android.calendar.event;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.android.calendar.R;
import com.android.calendar.Utils;

//import com.android.emailcommon.provider.EmailContent;

public class SelectEmailAddressActivity extends TabActivity implements
        OnClickListener {
    FrameLayout mFrameLayout = null;
    ListView mEmailListView = null;
    Intent mIntent = null;
    SelectEmailAddressActivity mInstant = null;
    SimpleAdapter mAdapter;
    LoadEmailAddressTask mEmailTask = null;
    EditText mSearchEdit = null;
    ArrayList<HashMap<String, Object>> mDataList = null;
    ArrayList<HashMap<String, Object>> mScrDataList = null;

    /* HCT_MODIFY start longgang for relayout select button */
    // Button mSelectButton=null;
    /* HCT_MODIFY end longgang for relayout select button */

    ListView mCurrentEmailListView = null;
    EditText mCurrentSearchEdit = null;

    /* HCT_MODIFY start longgang for relayout select button */
    // Button mCurrentSelectButton=null;
    /* HCT_MODIFY end longgang for relayout select button */

    ListView mGroupEmailListView = null;
    EditText mGroupSearchEdit = null;

    /* HCT_MODIFY start longgang for relayout select button */
    Button mSelectButton1 = null;
    Button mSelectButton2 = null;
    /* HCT_MODIFY end longgang for relayout select button */

    Button mGroupSelectButton = null;
    LoadGroupTask mLoadGroupTask = null;
    SimpleAdapter mGroupAdapter = null;
    HashMap<String, String> mMemberAddressList = null;
    ArrayList<HashMap<String, Object>> mGroupDataList = null;
    ArrayList<HashMap<String, Object>> mGroupScrDataList = null;

    LoadCurrentEmailAddressTask mCurrentEmailTask = null;
    ArrayList<HashMap<String, Object>> mCurrentDataList = null;
    ArrayList<HashMap<String, Object>> mCurrentScrDataList = null;
    private HashMap<String, Object> mContactsEmailMap = null;
    SimpleAdapter mCurrentAdapter;

    private static int mDefaultTab = 0; // bgin from 0.
    private static int mSrcDataSize = 0;
    private static boolean mSrcSelectFlag[] = new boolean[0];
    private static int mCurDataSize = 0;
    private static boolean mCurSelectFlag[] = new boolean[0];
    private static int mGroupDataSize = 0;
    private static boolean mGroupSelectFlag[] = new boolean[0];

    private static final String BUNDLE_DEFAULTTAB = "SelectEmailAddressActivity.defaultTab";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mInstant = this;
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        TabHost tabHost = getTabHost();
        LayoutInflater.from(this).inflate(
                R.layout.email_address_select_view_layout,
                tabHost.getTabContentView(), true);
        tabHost.addTab(tabHost
                .newTabSpec("tab1")
                .setIndicator(this.getString(R.string.contactsList),
                        getResources().getDrawable(R.drawable.contact))
                .setContent(R.id.select_contact_view));
        tabHost.addTab(tabHost
                .newTabSpec("tab2")
                .setIndicator(this.getString(R.string.groupsLabel),
                        getResources().getDrawable(R.drawable.contact_group))
                .setContent(R.id.select_group_contact_view));
        tabHost.addTab(tabHost
                .newTabSpec("tab3")
                .setIndicator(this.getString(R.string.tab_history),
                        getResources().getDrawable(R.drawable.contact_all))
                .setContent(R.id.select_current_contact_view));
        // setContentView(R.layout.email_address_select_view_layout);
        mIntent = this.getIntent();
        mFrameLayout = (FrameLayout) findViewById(R.id.email_address_select_view);
        mEmailListView = (ListView) findViewById(R.id.emailListView);
        mSearchEdit = (EditText) findViewById(R.id.email_name_address_search);

        mCurrentEmailListView = (ListView) findViewById(R.id.emailListView_current);
        mCurrentSearchEdit = (EditText) findViewById(R.id.email_name_address_search_current);

        /* HCT_MODIFY start longgang for relayout select button */
        // mCurrentSelectButton=(Button)findViewById(R.id.select_email_address_current);
        /* HCT_MODIFY end longgang for relayout select button */

        mContactsEmailMap = new HashMap<String, Object>();
        mScrDataList = new ArrayList<HashMap<String, Object>>();
        mCurrentScrDataList = new ArrayList<HashMap<String, Object>>();

        /* HCT_MODIFY start longgang for relayout select button */
        // mSelectButton=(Button)findViewById(R.id.select_email_address);
        // mSelectButton.setOnClickListener(this);
        // mSelectButton.requestFocus();
        // mCurrentSelectButton.setOnClickListener(this);
        /* HCT_MODIFY end longgang for relayout select button */

        mGroupEmailListView = (ListView) findViewById(R.id.emailListView_group);
        /* HCT_MODIFY start longgang for relayout select button */
        mGroupSearchEdit = (EditText) findViewById(R.id.email_name_address_search_group);
        mSelectButton1 = (Button) findViewById(R.id.select_email_address_1);
        mSelectButton2 = (Button) findViewById(R.id.select_email_address_2);
        mGroupSelectButton = (Button) findViewById(R.id.select_email_address_group);
        mSelectButton1.setOnClickListener(this);
        mSelectButton2.setOnClickListener(this);
        /* HCT_MODIFY end longgang for relayout select button */
        mGroupSelectButton.setOnClickListener(this);
        mMemberAddressList = new HashMap<String, String>();
        mGroupScrDataList = new ArrayList<HashMap<String, Object>>();

        mEmailTask = new LoadEmailAddressTask();
        mEmailTask.execute();

        tabhostSetChangeListener(tabHost);
        tabHost.setCurrentTab(mDefaultTab);
        // mEmailListView.f
        searchEditAddTextChangeListener();

        // mEmailListView.f
        currentSearchEditAddTextChangeListener();
        groupSearchEditChangeListener();

        mEmailListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                EmailAddressItemListItem item = (EmailAddressItemListItem) view;
                item.updateSelected(item);
            }
        });

        mCurrentEmailListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                EmailAddressItemListItem item = (EmailAddressItemListItem) view;
                item.updateSelected(item);
            }
        });
        Button buttonCancle1 = (Button) this
                .findViewById(R.id.cancle_select_action_1);
        Button buttonCancle2 = (Button) this
                .findViewById(R.id.cancle_select_action_2);
        Button buttonCancle_group = (Button) this
                .findViewById(R.id.cancle_select_action_group);
        if (buttonCancle1 != null && buttonCancle2 != null
                && buttonCancle_group != null) {
            buttonCancle2.setOnClickListener(this);
            buttonCancle1.setOnClickListener(this);
            buttonCancle_group.setOnClickListener(this);
        }
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // mLoadGroupTask=new LoadGroupTask();
        // mLoadGroupTask.execute();
    }

    private void groupSearchEditChangeListener() {

        mGroupSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filterGroup(s.toString());
            }

            private void filterGroup(String filter) {
                if (mGroupDataList != null) {
                    mGroupDataList.clear();
                    for (int i = 0; i < mGroupScrDataList.size(); i++) {
                        HashMap<String, Object> map = mGroupScrDataList.get(i);
                        mGroupDataList.add(map);
                    }
                    ArrayList<HashMap<String, Object>> mDelDataList = new ArrayList<HashMap<String, Object>>();
                    String displayName;
                    String filterLow = filter.toLowerCase();

                    Log.i("SMSReceiver", "filterGroup, filterLow =" + filterLow);

                    int check;
                    for (int i = 0; i < mGroupDataList.size(); i++) {
                        HashMap<String, Object> map = mGroupDataList.get(i);
                        displayName = ((String) map
                                .get(EmailAddressListSimpleAdapterViewBinder.GROUP_DISPLAY_NAME))
                                .toLowerCase();
                        Log.i("SMSReceiver", "filterGroup, displayName ="
                                + displayName);
                        check = ((Integer) map
                                .get(EmailAddressListSimpleAdapterViewBinder.GROUP_CHECKED))
                                .intValue();
                        if (check == R.drawable.btn_check_off_normal_holo_light
                                && displayName.contains(filterLow) == false) {
                            mDelDataList.add(map);
                        }
                    }
                    for (int i = 0; i < mDelDataList.size(); i++) {
                        HashMap<String, Object> map = mDelDataList.get(i);
                        mGroupDataList.remove(map);
                    }
                    if (mGroupAdapter != null)
                        try {
                            mGroupAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }
        }

        );
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(BUNDLE_DEFAULTTAB, mDefaultTab);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mDefaultTab = savedInstanceState.getInt(BUNDLE_DEFAULTTAB);
    }

    private void currentSearchEditAddTextChangeListener() {
        mCurrentSearchEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                filterAddressItems(s.toString());
            }

            private void filterAddressItems(String filter) {
                if (mCurrentDataList != null) {
                    mCurrentDataList.clear();
                    for (int i = 0; i < mCurrentScrDataList.size(); i++) {
                        HashMap<String, Object> map = mCurrentScrDataList
                                .get(i);
                        mCurrentDataList.add(map);
                    }
                    ArrayList<HashMap<String, Object>> mDelDataList = new ArrayList<HashMap<String, Object>>();
                    String displayName;
                    String address;
                    String namePY;
                    String filterLow = filter.toLowerCase();
                    int check;
                    for (int i = 0; i < mCurrentDataList.size(); i++) {
                        HashMap<String, Object> map = mCurrentDataList.get(i);
                        displayName = ((String) map
                                .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME))
                                .toLowerCase();
                        address = ((String) map
                                .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS))
                                .toLowerCase();
                        check = ((Integer) map
                                .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED))
                                .intValue();
                        namePY = ((String) map
                                .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY))
                                .toLowerCase();
                        if (check == R.drawable.btn_check_off_normal_holo_light
                                && displayName.contains(filterLow) == false
                                && address.contains(filterLow) == false
                                && namePY.startsWith(filterLow) == false) {
                            mDelDataList.add(map);
                        }
                    }
                    for (int i = 0; i < mDelDataList.size(); i++) {
                        HashMap<String, Object> map = mDelDataList.get(i);
                        mCurrentDataList.remove(map);
                    }
                    if (mCurrentAdapter != null) {
                        try {
                            mCurrentAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }
        });
    }

    private void searchEditAddTextChangeListener() {
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                filterAddressItems(s.toString());
            }

            private void filterAddressItems(String filter) {
                if (mDataList != null) {
                    mDataList.clear();
                    for (int i = 0; i < mScrDataList.size(); i++) {
                        HashMap<String, Object> map = mScrDataList.get(i);
                        mDataList.add(map);
                    }
                    ArrayList<HashMap<String, Object>> mDelDataList = new ArrayList<HashMap<String, Object>>();
                    String displayName;
                    String address;
                    String namePY;
                    String filterLow = filter.toLowerCase();
                    int check;
                    for (int i = 0; i < mDataList.size(); i++) {
                        HashMap<String, Object> map = mDataList.get(i);
                        displayName = ((String) map
                                .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME))
                                .toLowerCase();
                        address = ((String) map
                                .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS))
                                .toLowerCase();
                        check = ((Integer) map
                                .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED))
                                .intValue();
                        namePY = ((String) map
                                .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY))
                                .toLowerCase();
                        if (check == R.drawable.btn_check_off_normal_holo_light
                                && displayName.contains(filterLow) == false
                                && address.contains(filterLow) == false
                                && namePY.startsWith(filterLow) == false) {
                            mDelDataList.add(map);
                        }
                    }
                    for (int i = 0; i < mDelDataList.size(); i++) {
                        HashMap<String, Object> map = mDelDataList.get(i);
                        mDataList.remove(map);
                    }
                    if (mAdapter != null) {
                        try {
                            mAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }
        });
    }

    private void tabhostSetChangeListener(TabHost tabHost) {
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                getWindow()
                        .setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (tabId.equalsIgnoreCase("tab3")) {
                    mDefaultTab = 2;
                    if (mCurrentEmailTask == null) {
                        // mCurrentEmailTask=new LoadCurrentEmailAddressTask();
                        // mCurrentEmailTask.execute();
                    }
                } else if (tabId.equalsIgnoreCase("tab2")) {
                    mDefaultTab = 1;
                } else {
                    mDefaultTab = 0;
                }
            }

        });
    }

    private class LoadEmailAddressTask extends
            AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {
        /**
         * Special constructor to cache some local info
         */
        public LoadEmailAddressTask() {
        }

        @Override
        protected ArrayList<HashMap<String, Object>> doInBackground(
                Void... params) {
            Cursor c = null;
            ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
            // Uri uri =
            // Uri.parse("content://com.android.contacts/data/emails");
            HashMap<String, String> pyMap = new HashMap<String, String>();
            Uri uri = null;
            uri = Uri.parse("content://com.android.contacts/data");
            c = SelectEmailAddressActivity.this.getContentResolver().query(
                    uri,
                    EmailAddressListSimpleAdapterViewBinder.DATA_PROJECTION,
                    "not(" + Data.DATA11 + " is null) and " + Data.DATA11
                            + "<>'' ", null, null);
            while (c.moveToNext()) {
                long id = c.getLong(3);
                String pyData = c.getString(2);
                if (pyData != null)
                    pyMap.put(Long.toString(id), pyData);
            }
            c.close();

            if (Utils.getConfigBool(SelectEmailAddressActivity.this,
                    R.bool.smart_reminder) == true) {
                uri = Uri.parse("content://com.android.contacts/data");
                c = SelectEmailAddressActivity.this
                        .getContentResolver()
                        .query(uri,
                                EmailAddressListSimpleAdapterViewBinder.PROJECTION,
                                Data.MIMETYPE
                                        + "<>'vnd.android.cursor.item/group_membership' and ("
                                        + Data.MIMETYPE + "='"
                                        + Phone.CONTENT_ITEM_TYPE + "' or "
                                        + Data.MIMETYPE + "='"
                                        + Email.CONTENT_ITEM_TYPE + "')",
                                null,
                                EmailAddressListSimpleAdapterViewBinder.SORT_ORDER);
            } else {
                uri = Uri.parse("content://com.android.contacts/data/emails");
                c = SelectEmailAddressActivity.this.getContentResolver().query(
                        uri,
                        EmailAddressListSimpleAdapterViewBinder.PROJECTION,
                        null, null,
                        EmailAddressListSimpleAdapterViewBinder.SORT_ORDER);
            }
            buildData(c, dataList, pyMap);
            // DataList
            HashMap<String, Object> mapI;
            HashMap<String, Object> mapJ;
            String sNamePYi;
            String sNamePYj;

            for (int i = 0; i < dataList.size() - 1; i++) {
                mapI = dataList.get(i);
                sNamePYi = (String) mapI
                        .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY);
                for (int j = i + 1; j < dataList.size(); j++) {
                    mapJ = dataList.get(j);
                    sNamePYj = (String) mapJ
                            .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY);
                    if (sNamePYi.compareToIgnoreCase(sNamePYj) > 0) {
                        dataList.set(i, mapJ);
                        dataList.set(j, mapI);
                        mapI = mapJ;
                        sNamePYi = sNamePYj;
                    }
                }
            }
            c.close();
            return dataList;
        }

        private void buildData(Cursor c,
                ArrayList<HashMap<String, Object>> dataList,
                HashMap<String, String> pyMap) {
            int serialID = 0;
            String disPlayName = "";
            String address = "";
            while (c.moveToNext()) {
                serialID++;
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_ID,
                        serialID);
                map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED,
                        R.drawable.btn_check_off_normal_holo_light);
                disPlayName = c.getString(1);
                if (disPlayName == null)
                    disPlayName = "";
                map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME,
                        disPlayName);
                address = c.getString(2);
                if (address == null)
                    address = "";
                address = address.replaceAll(" ", "");
                map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS,
                        address);
                String id = Long.toString(c.getLong(3));
                mMemberAddressList.put(id, address);
                if (pyMap.containsKey(id)) {
                    map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY,
                            pyMap.get(id));
                } else {
                    map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY,
                            "");
                }
                dataList.add(map);
                try {
                    if (address != null && address.length() > 0)
                        mContactsEmailMap.put(address.toLowerCase(), map);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Object>> dataList) {
            mDataList = dataList;

            for (int i = 0; i < mDataList.size(); i++) {
                HashMap<String, Object> map = mDataList.get(i);
                mScrDataList.add(map);
            }
            mAdapter = new SimpleAdapter(
                    SelectEmailAddressActivity.this,
                    dataList,
                    R.layout.email_address_item_layout,
                    new String[] {
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED,
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME,
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS,
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_ID,
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY },
                    new int[] { R.id.selected, R.id.email_display_name,
                            R.id.email_address, R.id.email_address_id,
                            R.id.email_name_py });
            mAdapter.setViewBinder(new EmailAddressListSimpleAdapterViewBinder(
                    dataList));
            mEmailListView.setItemsCanFocus(false);
            try {
                mEmailListView.setAdapter(mAdapter);
            } catch (Exception e) {
                e.getMessage();
            }
            mEmailListView.setItemsCanFocus(false);

            if (mCurrentEmailTask == null) {
                mCurrentEmailTask = new LoadCurrentEmailAddressTask();
                mCurrentEmailTask.execute();
            }

            if (mLoadGroupTask == null) {
                mLoadGroupTask = new LoadGroupTask();
                mLoadGroupTask.execute();
            }
        }
    }

    private class LoadCurrentEmailAddressTask extends
            AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {
        /**
         * Special constructor to cache some local info
         */
        public LoadCurrentEmailAddressTask() {
        }

        @Override
        protected ArrayList<HashMap<String, Object>> doInBackground(
                Void... params) {
            Cursor c = null;
            ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

            HashMap<String, String> emailAddressMap = new HashMap<String, String>();
            boolean addInList = false;

            c = SelectEmailAddressActivity.this
                    .getContentResolver()
                    .query(
                    // EmailContent.Message.CONTENT_URI,
                    Uri.parse(Uri.parse("content://"
                            + "com.android.email.provider")
                            + "/message"),
                            new String[] {/* EmailContent.MessageColumns.TO_LIST */"toList" },
                            null,
                            null,
                            /* EmailContent.MessageColumns.TIMESTAMP */"timeStamp"
                                    + " DESC");
            if (c != null) {
                buildData(c, dataList, emailAddressMap);
                c.close();
            }

            if (Utils.getConfigBool(SelectEmailAddressActivity.this,
                    R.bool.smart_reminder) == true) {
                c = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                        null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
                int serialID = 100;
                while (c.moveToNext()) {
                    addInList = true;
                    HashMap<String, Object> map = new HashMap<String, Object>();

                    String Number = c.getString(0);
                    String Name = c.getString(14);

                    Log.i("SMSReceiver", "LoadCurrentEmailAddressTask Name = "
                            + Name);
                    Log.i("SMSReceiver",
                            "LoadCurrentEmailAddressTask Number = " + Number);

                    if (Number == null) {
                        continue;
                    }
                    if (Name == null) {
                        Name = "";
                    }
                    Number = Number.replaceAll(" ", "");

                    if (emailAddressMap.containsKey(Number)) {
                        addInList = false;
                    } else {
                        emailAddressMap.put(Number, "hctoma");
                    }

                    map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_ID,
                            serialID);
                    map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED,
                            R.drawable.btn_check_off_normal_holo_light);
                    map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS,
                            Number);
                    map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME,
                            Name);
                    if (addInList) {
                        dataList.add(map);
                        serialID++;
                        if (serialID > 200)
                            break;
                    }
                }
                c.close();
            }

            return dataList;
        }

        private void buildData(Cursor c,
                ArrayList<HashMap<String, Object>> dataList,
                HashMap<String, String> emailAddressMap) {
            boolean addInList;
            int serialID = 0;
            while (c != null && c.moveToNext()) {
                String toList = c.getString(0);
                if (toList != null && toList.length() > 0
                        && toList.contains("@")) {
                    String[] tos = toList.split("\u0001");
                    for (int i = 0; i < tos.length; i++) {
                        addInList = true;
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_ID,
                                serialID);
                        map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED,
                                R.drawable.btn_check_off_normal_holo_light);
                        String[] toItems = tos[i].split("\u0002");
                        map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS,
                                toItems[0]);
                        map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME,
                                toItems[0]);
                        String address = toItems[0].toLowerCase();
                        if (emailAddressMap.containsKey(address)) {
                            addInList = false;
                        } else {
                            emailAddressMap.put(address, "hctoma");
                        }
                        if (mContactsEmailMap.containsKey(address)) {
                            HashMap<String, Object> mapBuffer = (HashMap<String, Object>) mContactsEmailMap
                                    .get(address);
                            map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME,
                                    mapBuffer
                                            .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME));
                            map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY,
                                    mapBuffer
                                            .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY));
                        } else {
                            map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY,
                                    "");
                        }
                        if (addInList) {
                            dataList.add(map);
                            serialID++;
                            if (serialID > 100)
                                break;
                        }
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Object>> dataList) {
            mCurrentDataList = dataList;

            for (int i = 0; i < mCurrentDataList.size(); i++) {
                HashMap<String, Object> map = mCurrentDataList.get(i);
                mCurrentScrDataList.add(map);
            }
            mCurrentAdapter = new SimpleAdapter(
                    SelectEmailAddressActivity.this,
                    dataList,
                    R.layout.email_address_item_layout,
                    new String[] {
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED,
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME,
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS,
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_ID,
                            EmailAddressListSimpleAdapterViewBinder.EMAIL_NAME_PY },
                    new int[] { R.id.selected, R.id.email_display_name,
                            R.id.email_address, R.id.email_address_id,
                            R.id.email_name_py });
            mCurrentAdapter
                    .setViewBinder(new EmailAddressListSimpleAdapterViewBinder(
                            dataList));
            mCurrentEmailListView.setItemsCanFocus(false);
            try {
                mCurrentEmailListView.setAdapter(mCurrentAdapter);
            } catch (Exception e) {
                e.getMessage();
            }
            mCurrentEmailListView.setItemsCanFocus(false);
        }
    }

    private class LoadGroupTask extends
            AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>> {

        @Override
        protected ArrayList<HashMap<String, Object>> doInBackground(
                Void... arg0) {
            ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
            HashMap<String, String> groupAddressList = new HashMap<String, String>();
            long lastGroupId = -1;
            String groupAddress = "";

            Uri uri = null;
            Cursor c = null;
            // uri = Uri.parse("content://com.android.contacts/data");
            // c = SelectEmailAddressActivity.this.getContentResolver()
            // .query(uri,
            // EmailAddressListSimpleAdapterViewBinder.DATA_PROJECTION,
            // "not("+Data.DATA11+" is null) and "+Data.DATA11+"<>'' ",
            // null,null);

            uri = Uri.parse("content://com.android.contacts/data");
            c = SelectEmailAddressActivity.this
                    .getContentResolver()
                    .query(uri,
                            EmailAddressListSimpleAdapterViewBinder.GROUP_MEMBER_PROJECTION,
                            Data.MIMETYPE
                                    + "='vnd.android.cursor.item/group_membership'",
                            null, Data.DATA1);

            while (c.moveToNext()) {
                long id = c.getLong(0);
                long groupId = c.getLong(1);
                if (lastGroupId != groupId) {
                    if (lastGroupId > 0 && groupAddress.length() > 0)
                        groupAddressList.put(Long.toString(lastGroupId),
                                groupAddress);
                    groupAddress = "";
                    lastGroupId = groupId;
                }
                String pName = c.getString(2);
                String pMimeType = c.getString(3);
                String address = getAddressById(id);
                if (address.length() > 0) {
                    if (groupAddress.length() > 0)
                        groupAddress = groupAddress + ",";
                    groupAddress = groupAddress
                            + (new Address(address, pName)).toString();
                }
            }

            if (lastGroupId > 0 && groupAddress.length() > 0)
                groupAddressList.put(Long.toString(lastGroupId), groupAddress);
            c.close();

            uri = Uri.parse("content://com.android.contacts/groups");
            c = SelectEmailAddressActivity.this.getContentResolver().query(uri,
                    EmailAddressListSimpleAdapterViewBinder.GROUP_PROJECTION,
                    null, null, Groups.TITLE);

            while (c.moveToNext()) {
                HashMap<String, Object> dataItem = new HashMap<String, Object>();
                long id = c.getLong(0);
                String key = Long.toString(id);
                if (groupAddressList.containsKey(key)) {
                    String address = groupAddressList.get(key);
                    String title = c.getString(1);
                    dataItem.put(Groups._ID, id);
                    dataItem.put(
                            EmailAddressListSimpleAdapterViewBinder.GROUP_DISPLAY_NAME,
                            title);
                    dataItem.put(
                            EmailAddressListSimpleAdapterViewBinder.GROUP_CHECKED,
                            R.drawable.btn_check_off_normal_holo_light);
                    dataItem.put(
                            EmailAddressListSimpleAdapterViewBinder.GROUP_EMAIL_ADDRESS,
                            address);
                    dataList.add(dataItem);
                }
            }
            c.close();
            return dataList;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Object>> dataList) {
            mGroupDataList = dataList;
            for (int i = 0; i < mGroupDataList.size(); i++) {
                HashMap<String, Object> map = mGroupDataList.get(i);
                mGroupScrDataList.add(map);
            }
            mGroupAdapter = new SimpleAdapter(
                    SelectEmailAddressActivity.this,
                    dataList,
                    R.layout.email_address_item_layout,
                    new String[] {
                            EmailAddressListSimpleAdapterViewBinder.GROUP_CHECKED,
                            EmailAddressListSimpleAdapterViewBinder.GROUP_DISPLAY_NAME,
                            EmailAddressListSimpleAdapterViewBinder.GROUP_EMAIL_ADDRESS,
                            EmailAddressListSimpleAdapterViewBinder.GROUP_ID },
                    new int[] { R.id.selected, R.id.email_display_name,
                            R.id.email_address, R.id.email_address_id });
            mGroupAdapter
                    .setViewBinder(new EmailAddressListSimpleAdapterViewBinder(
                            dataList));
            mGroupEmailListView.setItemsCanFocus(false);
            try {
                mGroupEmailListView.setAdapter(mGroupAdapter);
            } catch (Exception e) {
                e.getMessage();
            }
            mGroupEmailListView.setItemsCanFocus(false);
        }
    }

    private String getAddressById(long id) {
        String address = "";
        String key = Long.toString(id);
        if (mMemberAddressList.containsKey(key)) {
            address = mMemberAddressList.get(key);
        }
        return address;
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
        /* HCT_MODIFY start longgang for relayout select button */
        /*
         * HCT_MODIFY longgang start case R.id.select_email_address:{
         * selectEmailAddressClickProcess(); break; } case
         * R.id.select_email_address_current:{ selectEmailAddressClickProcess();
         * break; }
         */
        case R.id.select_email_address_1:
        case R.id.select_email_address_2:
            /* HCT_MODIFY end longgang for relayout select button */
        case R.id.select_email_address_group: {
            selectEmailAddressClickProcess();
            break;
        }
        case R.id.cancle_select_action_1:
        case R.id.cancle_select_action_2:
        case R.id.cancle_select_action_group: {
            this.finish();
            break;
        }
        }

    }

    private void selectEmailAddressClickProcess() {
        String displayName;
        String address;
        int check;
        ArrayList<String> selectData = new ArrayList<String>();
        for (int i = 0; i < mScrDataList.size(); i++) {
            HashMap<String, Object> map = mScrDataList.get(i);
            try {
                displayName = ((String) map
                        .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME));
            } catch (Exception ex) {
                displayName = "";
            }
            address = ((String) map
                    .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS));
            check = ((Integer) map
                    .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED))
                    .intValue();
            if (check == R.drawable.btn_check_on_normal_holo_light) {
                if (displayName.length() > 0 && address.contains("<") == false) {
                    if (displayName.equalsIgnoreCase(address) == true) {
                        selectData.add("\"" + displayName + "\"" + "<"
                                + address + ">");
                    } else {
                        selectData.add(displayName + "<" + address + ">");
                    }
                } else {
                    selectData.add(address);
                }
            }
        }
        for (int i = 0; i < mCurrentScrDataList.size(); i++) {
            HashMap<String, Object> map = mCurrentScrDataList.get(i);
            displayName = ((String) map
                    .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_DISPLAY_NAME));
            address = ((String) map
                    .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS));
            check = ((Integer) map
                    .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED))
                    .intValue();
            if (check == R.drawable.btn_check_on_normal_holo_light) {
                boolean bDispNameEqualAddress = displayName
                        .equalsIgnoreCase(address);
                if (bDispNameEqualAddress == false
                        && address.contains("<") == false) {
                    selectData.add(displayName + "<" + address + ">");
                } else {
                    if (bDispNameEqualAddress == true) {
                        selectData.add("\"" + displayName + "\"" + "<"
                                + address + ">");
                    } else {
                        selectData.add(address);
                    }
                }
            }
        }

        for (int i = 0; i < mGroupScrDataList.size(); i++) {
            HashMap<String, Object> map = mGroupScrDataList.get(i);
            String groupAddress = ((String) map
                    .get(EmailAddressListSimpleAdapterViewBinder.GROUP_EMAIL_ADDRESS));
            Log.i("Select", "selectEmailAddressClickProcess, groupAddress ="
                    + groupAddress);

            check = ((Integer) map
                    .get(EmailAddressListSimpleAdapterViewBinder.GROUP_CHECKED))
                    .intValue();
            if (check == R.drawable.btn_check_on_normal_holo_light) {
                Address[] addressList = Address.legacyUnpack(groupAddress);
                if (addressList != null && addressList.length > 0) {
                    for (Address adr : addressList) {
                        Log.i("Select", "selectEmailAddressClickProcess, adr ="
                                + adr.toString());
                        selectData.add(adr.toString());
                    }
                }
            }
        }
        mIntent.putExtra(mIntent.getAction(), selectData);
        mInstant.setResult(1011, mIntent);
        mInstant.finish();
    }

    /**
     * We override onDestroy to make sure that the WebView gets explicitly
     * destroyed. Otherwise it can leak native references.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTask(mCurrentEmailTask);
        mCurrentEmailTask = null;
        cancelTask(mEmailTask);
        mEmailTask = null;
        cancelTask(mLoadGroupTask);
        mLoadGroupTask = null;
        mSrcDataSize = 0;
        mCurDataSize = 0;
        mSrcSelectFlag = null;
        mCurSelectFlag = null;
        mGroupDataSize = 0;
        mGroupSelectFlag = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // if(!UiUtilities.useTwoPane(this))
        // {
        try {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { // Horizontal
                                                                                                             // screen
                float scale = getResources().getDisplayMetrics().density;
                int width = (int) (450 * scale + 0.5f);
                int height = (int) (200 * scale + 0.5f);
                mFrameLayout.getLayoutParams().height = height;
                mFrameLayout.getLayoutParams().width = width;
            } else { // Vertical screen
                float scale = getResources().getDisplayMetrics().density;
                int width = (int) (295 * scale + 0.5f);
                int height = (int) (355 * scale + 0.5f);
                mFrameLayout.getLayoutParams().height = height;
                mFrameLayout.getLayoutParams().width = width;
            }
        } catch (Exception ex) {

        }
        // }

    }

    private static void cancelTask(AsyncTask<?, ?, ?> task) {
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }
    }
}
