/*
 * add by hct
 */

package com.hct.gios.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import com.android.calendar.R;

public class HCTPopupWindow extends PopupWindow {
    private static final String TAG = "HCTPopupWindow";

    public CharSequence[] mItems;
    private int mListLayout;
    private ListView mListView;
    private OnItemClickListener mItemClickListener;
    private Context mContext;
    private View mContentView;

    public HCTPopupWindow(Context context) {
        super(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mContext = context;
    }

    public void setItems(CharSequence[] items, OnItemClickListener listener) {
        mItems = items;
        mItemClickListener = listener;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mContentView = inflater.inflate(R.layout.hct_popup_view, null);
        mListView = (ListView) mContentView.findViewById(R.id.hct_popup_list);
        mListView.setAdapter(new ArrayAdapter<CharSequence>(mContext,
                R.layout.hct_popup_list, mItems));
        setContentView(mContentView);

        mListView.setOnItemClickListener(mItemClickListener);

        Log.i(TAG, "gengbin setItems()");
    }

    public void showAtLocation(View item, View parent) {
        setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.menu_dropdown_panel_hct_light));
        setOutsideTouchable(true);
        setTouchable(true);
        setFocusable(true);
        update();
        Point touchPoint = new Point();
        ViewRootImpl mViewRootImpl = item.getViewRootImpl();
        if (mViewRootImpl != null) {
            mViewRootImpl.getLastTouchPoint(touchPoint);
        }

        int listHeight = mContext.getResources().getDimensionPixelSize(
                R.dimen.hct_popup_list_height);
        int listWidth = mContext.getResources().getDimensionPixelSize(
                R.dimen.hct_popup_list_width);
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        int windowHeigh = wm.getDefaultDisplay().getHeight();
        int windowWidth = wm.getDefaultDisplay().getWidth();
        if (!isShowing()) {
            if (touchPoint.y > windowHeigh / 2)
                touchPoint.y = touchPoint.y - listHeight * mItems.length;
            if (touchPoint.x > windowWidth / 2)
                touchPoint.x = touchPoint.x - listWidth;
            Log.i(TAG, "gengbin, touchPoint.y " + touchPoint.y);
            Log.i(TAG, "gengbin,windowHeigh = " + windowHeigh);
            showAtLocation(parent, Gravity.NO_GRAVITY, touchPoint.x,
                    touchPoint.y);
        }
    }

}