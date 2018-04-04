package com.hct.calendar.almanac;

import android.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.calendar.R;
import com.google.gson.Gson;
import com.hct.calendar.domain.AlmanacBody;

public class AlmanacActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almanac);
        getActionBar().hide();
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("almanacInfo");
        AlmanacFragment af = new AlmanacFragment();
        Bundle b = new Bundle();
        b.putString("almanacInfo", jsonString);
        af.setArguments(b);
        getSupportFragmentManager() // manager
                .beginTransaction() // begin
                .replace(R.id.almanac_layout_v, af) // replace
                .commit(); // commit
        AlmanacBody body = new Gson().fromJson(jsonString, AlmanacBody.class);
        // change by zyp for GMOS-5789
        if (null != body && null != body.data) {
            hadnleTitleBar(body.data.date);
        }

    }

    private void hadnleTitleBar(@NonNull String title) {
        findViewById(R.id.almanac_back_arrow).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
        TextView tvTitleTextView = (TextView) findViewById(R.id.almanac_title_tv);
        // change by zyp for GMOS-5789
        if (null == title) {
            return;
        }
        String[] dateStrings = title.split("-");
        tvTitleTextView.setText(dateStrings[0]
                + getResources().getString(R.string.year) + dateStrings[1]
                + getResources().getString(R.string.month) + dateStrings[2]
                + getResources().getString(R.string.event_day));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
