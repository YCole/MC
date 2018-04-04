package com.hct.gios.widget;

import android.app.Activity;
import android.view.MenuItem;
import android.widget.ActionMenuView;

public class MenuClicker implements ActionMenuView.OnMenuItemClickListener {

    Activity app;

    public MenuClicker(Activity activity) {
        app = activity;
    }

    @Override
    public boolean onMenuItemClick(MenuItem arg0) {
        // TODO Auto-generated method stub
        return app.onOptionsItemSelected(arg0);
    }

}
