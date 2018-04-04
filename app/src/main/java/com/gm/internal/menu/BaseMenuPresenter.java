package com.gm.internal.menu;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhenjie on 2017/6/12.
 */

public abstract class BaseMenuPresenter implements MenuPresenter {
    private static final String TAG = "FloatActionMenu";
    protected Context mSystemContext;
    protected Context mContext;
    protected com.gm.internal.menu.MenuBuilder mMenu;
    protected LayoutInflater mSystemInflater;
    protected LayoutInflater mInflater;
    private Callback mCallback;

    private int mMenuLayoutRes;
    private int mItemLayoutRes;

    protected com.gm.internal.menu.MenuView mMenuView;

    private int mId;

    /**
     * Construct a new BaseMenuPresenter.
     *
     * @param context       Context for generating system-supplied views
     * @param menuLayoutRes Layout resource ID for the menu container view
     * @param itemLayoutRes Layout resource ID for a single item view
     */
    public BaseMenuPresenter(Context context, int menuLayoutRes, int itemLayoutRes) {
        mSystemContext = context;
        mSystemInflater = LayoutInflater.from(context);
        mMenuLayoutRes = menuLayoutRes;
        mItemLayoutRes = itemLayoutRes;
    }

    @Override
    public void initForMenu(Context context, com.gm.internal.menu.MenuBuilder menu) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mMenu = menu;
    }

    @Override
    public com.gm.internal.menu.MenuView getMenuView(ViewGroup root) {
        if (mMenuView == null) {
            mMenuView = (MenuView) mSystemInflater.inflate(mMenuLayoutRes, root, false);
            mMenuView.initialize(mMenu);
            updateMenuView(true);
        }

        return mMenuView;
    }

    /**
     * Reuses item views when it can
     */
    public void updateMenuView(boolean cleared) {
        Log.d(TAG, "updateMenuView");
        final ViewGroup parent = (ViewGroup) mMenuView;
        if (parent == null) return;

        int childIndex = 0;
        if (mMenu != null) {
            mMenu.flagActionItems();
            ArrayList<com.gm.internal.menu.MenuItemImpl> visibleItems = mMenu.getVisibleItems();
            final int itemCount = visibleItems.size();
            for (int i = 0; i < itemCount; i++) {
                com.gm.internal.menu.MenuItemImpl item = visibleItems.get(i);
                if (shouldIncludeItem(childIndex, item)) {
                    final View convertView = parent.getChildAt(childIndex);
                    final com.gm.internal.menu.MenuItemImpl oldItem = convertView instanceof com.gm.internal.menu.MenuView.ItemView ?
                            ((com.gm.internal.menu.MenuView.ItemView) convertView).getItemData() : null;
                    final View itemView = getItemView(item, convertView, parent);
                    if (item != oldItem) {
                        // Don't let old states linger with new data.
                        itemView.setPressed(false);
                        itemView.jumpDrawablesToCurrentState();
                    }
                    if (itemView != convertView) {
                        addItemView(itemView, childIndex);
                    }
                    childIndex++;
                }
            }
        }

        // Remove leftover views.
        while (childIndex < parent.getChildCount()) {
            if (!filterLeftoverView(parent, childIndex)) {
                childIndex++;
            }
        }
    }

    /**
     * Add an item view at the given index.
     *
     * @param itemView   View to add
     * @param childIndex Index within the parent to insert at
     */
    protected void addItemView(View itemView, int childIndex) {
        final ViewGroup currentParent = (ViewGroup) itemView.getParent();
        if (currentParent != null) {
            currentParent.removeView(itemView);
        }
        ((ViewGroup) mMenuView).addView(itemView, childIndex);
    }

    /**
     * Filter the child view at index and remove it if appropriate.
     *
     * @param parent     Parent to filter from
     * @param childIndex Index to filter
     * @return true if the child view at index was removed
     */
    protected boolean filterLeftoverView(ViewGroup parent, int childIndex) {
        parent.removeViewAt(childIndex);
        return true;
    }

    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    public Callback getCallback() {
        return mCallback;
    }

    /**
     * Create a new item view that can be re-bound to other item data later.
     *
     * @return The new item view
     */
    public com.gm.internal.menu.MenuView.ItemView createItemView(ViewGroup parent) {
        Log.d(TAG, "createItemView");
        return (com.gm.internal.menu.MenuView.ItemView) mSystemInflater.inflate(mItemLayoutRes, parent, false);
    }

    /**
     * Prepare an item view for use. See AdapterView for the basic idea at work here.
     * This may require creating a new item view, but well-behaved implementations will
     * re-use the view passed as convertView if present. The returned view will be populated
     * with data from the item parameter.
     *
     * @param item        Item to present
     * @param convertView Existing view to reuse
     * @param parent      Intended parent view - use for inflation.
     * @return View that presents the requested menu item
     */
    public View getItemView(com.gm.internal.menu.MenuItemImpl item, View convertView, ViewGroup parent) {
        Log.d(TAG, "getItemView");
        com.gm.internal.menu.MenuView.ItemView itemView;
        if (convertView instanceof com.gm.internal.menu.MenuView.ItemView) {
            itemView = (com.gm.internal.menu.MenuView.ItemView) convertView;
        } else {
            itemView = createItemView(parent);
        }
        bindItemView(item, itemView);
        return (View) itemView;
    }

    /**
     * Bind item data to an existing item view.
     *
     * @param item     Item to bind
     * @param itemView View to populate with item data
     */
    public abstract void bindItemView(com.gm.internal.menu.MenuItemImpl item, com.gm.internal.menu.MenuView.ItemView itemView);

    /**
     * Filter item by child index and item data.
     *
     * @param childIndex Indended presentation index of this item
     * @param item       Item to present
     * @return true if this item should be included in this menu presentation; false otherwise
     */
    public boolean shouldIncludeItem(int childIndex, com.gm.internal.menu.MenuItemImpl item) {
        return true;
    }

    public void onCloseMenu(com.gm.internal.menu.MenuBuilder menu, boolean allMenusAreClosing) {
        if (mCallback != null) {
            mCallback.onCloseMenu(menu, allMenusAreClosing);
        }
    }

    public boolean onSubMenuSelected(com.gm.internal.menu.SubMenuBuilder menu) {
        if (mCallback != null) {
            return mCallback.onOpenSubMenu(menu);
        }
        return false;
    }

    public boolean flagActionItems() {
        return false;
    }

    public boolean expandItemActionView(com.gm.internal.menu.MenuBuilder menu, com.gm.internal.menu.MenuItemImpl item) {
        return false;
    }

    public boolean collapseItemActionView(com.gm.internal.menu.MenuBuilder menu, com.gm.internal.menu.MenuItemImpl item) {
        return false;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
