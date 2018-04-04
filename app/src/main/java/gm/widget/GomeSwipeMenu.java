package gm.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * Created by chenchen on 2017/5/8.
 */

public class GomeSwipeMenu {
    private Context mContext;
    private List<gm.widget.GomeSwipeMenuItem> mItems;
    private int mViewType;

    public GomeSwipeMenu(Context context) {
        mContext = context;
        mItems = new ArrayList<gm.widget.GomeSwipeMenuItem>();
    }

    public Context getContext() {
        return mContext;
    }

    public void addMenuItem(gm.widget.GomeSwipeMenuItem item) {
        mItems.add(item);
    }

    public void removeMenuItem(gm.widget.GomeSwipeMenuItem item) {
        mItems.remove(item);
    }

    public List<gm.widget.GomeSwipeMenuItem> getMenuItems() {
        return mItems;
    }

    public gm.widget.GomeSwipeMenuItem getMenuItem(int index) {
        return mItems.get(index);
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(int viewType) {
        this.mViewType = viewType;
    }
}
