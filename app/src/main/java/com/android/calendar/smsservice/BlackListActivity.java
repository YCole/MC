package com.android.calendar.smsservice;

import android.app.ActionBar;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.android.calendar.R;

public class BlackListActivity extends ListActivity {

    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;
    private ListView listView;
    private View doDeleteBtn;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.calendar_title_bg));
        }
        setContentView(R.layout.blacklist);

        BlackListDBHelper helper = new BlackListDBHelper(
                getApplicationContext());

        mCursor = helper.query();
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, mCursor,
                new String[] { "number" }, new int[] { android.R.id.text1 });
        setListAdapter(mAdapter);

        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        doDeleteBtn = findViewById(R.id.do_delete_btn);
        doDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long[] checked_id = listView.getCheckItemIds();
                Log.i("smsreceiver", "id.length = " + checked_id.length);

                BlackListDBHelper helper = new BlackListDBHelper(
                        getApplicationContext());
                for (int i = 0; i < checked_id.length; i++) {
                    Log.i("smsreceiver", "id = " + checked_id[i]);
                    helper.del((int) checked_id[i]);
                }
                mCursor.requery();
                try {
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
