package gm.widget;

import gm.widget.GomeListView.OnMenuItemClickListener;
import gm.widget.GomeSwipeMenuView.OnSwipeItemClickListener;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
/**
 * Created by chenchen on 2017/5/8.
 */

public class GomeSwipeMenuAdapter implements WrapperListAdapter, OnSwipeItemClickListener {
    private ListAdapter mAdapter;
    private Context mContext;
    private OnMenuItemClickListener onMenuItemClickListener;

    public GomeSwipeMenuAdapter(Context context, ListAdapter adapter) {
        mAdapter = adapter;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        gm.widget.GomeSwipeMenuLayout layout = null;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            gm.widget.GomeSwipeMenu menuLeft = new gm.widget.GomeSwipeMenu(mContext);
            menuLeft.setViewType(mAdapter.getItemViewType(position));
            gm.widget.GomeSwipeMenu menuRight = new gm.widget.GomeSwipeMenu(mContext);
            menuRight.setViewType(mAdapter.getItemViewType(position));
            createRightMenu(menuRight);
            createLeftMenu(menuLeft);
            gm.widget.GomeSwipeMenuView menuLeftView = new gm.widget.GomeSwipeMenuView(menuLeft, (gm.widget.GomeListView) parent);
            gm.widget.GomeSwipeMenuView menuRightView = new gm.widget.GomeSwipeMenuView(menuRight, (gm.widget.GomeListView) parent);
            menuLeftView.setOnSwipeItemClickListener(this);
            menuRightView.setOnSwipeItemClickListener(this);
            gm.widget.GomeListView listView = (gm.widget.GomeListView) parent;
            layout = new gm.widget.GomeSwipeMenuLayout(contentView, menuLeftView, menuRightView, listView.getCloseInterpolator(), listView.getOpenInterpolator());
            layout.setPosition(position);
        } else {
            layout = (GomeSwipeMenuLayout) convertView;
            layout.closeMenu();
            layout.setPosition(position);
            View view = mAdapter.getView(position, layout.getContentView(), parent);
        }
        return layout;
    }

    public void createMenu(gm.widget.GomeSwipeMenu menu) {
        // Test Code
        gm.widget.GomeSwipeMenuItem item = new gm.widget.GomeSwipeMenuItem(mContext);
        item.setTitle("Item 1");
        item.setBackground(new ColorDrawable(Color.GRAY));
        item.setWidth(300);
        menu.addMenuItem(item);

        item = new gm.widget.GomeSwipeMenuItem(mContext);
        item.setTitle("Item 2");
        item.setBackground(new ColorDrawable(Color.RED));
        item.setWidth(300);
        menu.addMenuItem(item);
    }

    public void createLeftMenu(gm.widget.GomeSwipeMenu menu) {
        // Test Code
        gm.widget.GomeSwipeMenuItem item = new gm.widget.GomeSwipeMenuItem(mContext);
        item.setTitle("Item 1");
        item.setBackground(new ColorDrawable(Color.GRAY));
        item.setWidth(300);
        menu.addMenuItem(item);

        item = new gm.widget.GomeSwipeMenuItem(mContext);
        item.setTitle("Item 2");
        item.setBackground(new ColorDrawable(Color.RED));
        item.setWidth(300);
        menu.addMenuItem(item);
    }

    public void createRightMenu(gm.widget.GomeSwipeMenu menu) {
        // Test Code
        gm.widget.GomeSwipeMenuItem item = new gm.widget.GomeSwipeMenuItem(mContext);
        item.setTitle("Item 1");
        item.setBackground(new ColorDrawable(Color.GRAY));
        item.setWidth(300);
        menu.addMenuItem(item);

        item = new GomeSwipeMenuItem(mContext);
        item.setTitle("Item 2");
        item.setBackground(new ColorDrawable(Color.RED));
        item.setWidth(300);
        menu.addMenuItem(item);
    }

    @Override
    public void onItemClick(GomeSwipeMenuView view, GomeSwipeMenu menu, int index) {
        if (onMenuItemClickListener != null) {
            onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

}
