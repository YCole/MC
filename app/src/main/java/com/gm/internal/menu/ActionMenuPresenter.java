package com.gm.internal.menu;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.calendar.R;

/**
 * Created by zhenjie on 2017/6/12.
 */

public class ActionMenuPresenter extends BaseMenuPresenter {
    private static final String TAG = "FloatActionMenu";
    private Context mContext;
    private LayoutInflater mInflater;
    private OverflowMenuButton mOverflowButton;
    private Drawable mPendingOverflowIcon;
    private boolean mReserveOverflow;
    private boolean mReserveOverflowSet;
    private int mMaxItems;
    private boolean mMaxItemsSet;
    private boolean mListMenuShowIcon;
    private boolean mOverFlowButtonResSet;
    private String mMoreButtonText;
    private Drawable mMoreButtonIconRes;

    private OverflowPopup mOverflowPopup;

    private OpenOverflowRunnable mPostedOpenRunnable;


    public ActionMenuPresenter(Context context) {
        super(context, R.layout.action_menu_layout, R.layout.action_menu_item_layout);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void initForMenu(Context context, com.gm.internal.menu.MenuBuilder menu) {
        super.initForMenu(context, menu);

        final Resources res = context.getResources();

        final com.gm.internal.menu.ActionMenuPolicy abp = com.gm.internal.menu.ActionMenuPolicy.get(context);
        if (!mReserveOverflowSet) {
            mReserveOverflow = abp.showsOverflowMenuButton();
        }

        // Measure for initial configuration
        if (!mMaxItemsSet) {
            mMaxItems = abp.getMaxActionButtons();
        }

        if (mReserveOverflow) {
            if (mOverflowButton == null) {
                mOverflowButton = createOverflowMenuButton(mSystemContext);
                if (mOverFlowButtonResSet) {
                    if(!TextUtils.isEmpty(mMoreButtonText)){
                        mOverflowButton.setTitle(mMoreButtonText);
                    }
                    if(mMoreButtonIconRes != null){
                        mOverflowButton.setIcon(mMoreButtonIconRes);
                    }
                    mOverFlowButtonResSet = false;
                }
            }
        } else {
            mOverflowButton = null;
        }

    }

    public void setMaxItems(int maxItem){
        mMaxItemsSet = true;
        mMaxItems = maxItem;
        if(mMenu != null){
            mMenu.onItemsChanged(true);
        }
    }

    public void setOverflowButtonText(String text){
        Log.d(TAG,"setOverflowButtonText : " + text);
        mOverFlowButtonResSet = true;
        mMoreButtonText = text;
    }

    public void setOverflowButtonIcon(Drawable drawable){
        Log.d(TAG,"setOverflowButtonIcon : " + drawable);
        mOverFlowButtonResSet = true;
        mMoreButtonIconRes = drawable;
    }

    public void resetOverflowButton(){
        mOverFlowButtonResSet = false;
        mOverflowButton = null;
        mMoreButtonIconRes = null;
        mMoreButtonText = null;
    }

    /**
     * set Overflow button enable
     * @param enable
     */
    public void setOverflowButtonEnable(boolean enable){
        if(mOverflowButton != null){
            mOverflowButton.setActionItemEnable(enable);
        }
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
        if (mMenu != null) {
            mMenu.onItemsChanged(true);
        }
    }

    public void setReserveOverflow(boolean reserveOverflow) {
        mReserveOverflow = reserveOverflow;
        mReserveOverflowSet = true;
    }

    @Override
    public com.gm.internal.menu.MenuView getMenuView(ViewGroup root) {
        com.gm.internal.menu.MenuView result = super.getMenuView(root);
        return result;
    }

    @Override
    public View getItemView(final com.gm.internal.menu.MenuItemImpl item, View convertView, ViewGroup parent) {
        View actionView = item.getActionView();
        if (actionView == null || item.hasCollapsibleActionView()) {
            actionView = super.getItemView(item, convertView, parent);
        }
        actionView.setVisibility(item.isActionViewExpanded() ? View.GONE : View.VISIBLE);

        final com.gm.internal.menu.ActionMenuView menuParent = (com.gm.internal.menu.ActionMenuView) parent;
        final ViewGroup.LayoutParams lp = actionView.getLayoutParams();
        if (!menuParent.checkLayoutParams(lp)) {
            actionView.setLayoutParams(menuParent.generateLayoutParams(lp));
        }
        return actionView;
    }

    @Override
    public void bindItemView(com.gm.internal.menu.MenuItemImpl item, com.gm.internal.menu.MenuView.ItemView itemView) {
        itemView.initialize(item, 0);

        final com.gm.internal.menu.ActionMenuView menuView = (com.gm.internal.menu.ActionMenuView) mMenuView;
        final com.gm.internal.menu.FloatActionItemView actionItemView = (com.gm.internal.menu.FloatActionItemView) itemView;
        actionItemView.setItemInvoker(menuView);
    }

    @Override
    public boolean shouldIncludeItem(int childIndex, com.gm.internal.menu.MenuItemImpl item) {
        return item.isActionButton();
    }

    @Override
    public void updateMenuView(boolean cleared) {
        super.updateMenuView(cleared);

        ((View) mMenuView).requestLayout();

        final ArrayList<com.gm.internal.menu.MenuItemImpl> nonActionItems = mMenu != null ?
                mMenu.getNonActionItems() : null;
        boolean hasOverflow = false;
        if (mReserveOverflow && nonActionItems != null) {
            final int count = nonActionItems.size();
            if (count == 1) {
                hasOverflow = !nonActionItems.get(0).isActionViewExpanded();
            } else {
                hasOverflow = count > 0;
            }
        }

        if (hasOverflow) {
            if (mOverflowButton == null) {
                mOverflowButton = createOverflowMenuButton(mSystemContext);
            }
            if(mOverFlowButtonResSet){
                mOverFlowButtonResSet = false;
                if(!TextUtils.isEmpty(mMoreButtonText)){
                    mOverflowButton.setTitle(mMoreButtonText);
                }
                if(mMoreButtonIconRes != null){
                    mOverflowButton.setIcon(mMoreButtonIconRes);
                }
            }
            ViewGroup parent = (ViewGroup) mOverflowButton.getParent();
            if (parent != mMenuView) {
                if (parent != null) {
                    parent.removeView(mOverflowButton);
                }
                com.gm.internal.menu.ActionMenuView menuView = (com.gm.internal.menu.ActionMenuView) mMenuView;
                menuView.addView(mOverflowButton, menuView.generateOverflowButtonLayoutParams());
            }
        } else if (mOverflowButton != null && mOverflowButton.getParent() == mMenuView) {
            ((ViewGroup) mMenuView).removeView(mOverflowButton);
        }

        ((com.gm.internal.menu.ActionMenuView) mMenuView).setOverflowReserved(mReserveOverflow);
    }

    @Override
    public boolean filterLeftoverView(ViewGroup parent, int childIndex) {
        if (parent.getChildAt(childIndex) == mOverflowButton) return false;
        return super.filterLeftoverView(parent, childIndex);
    }

    /**
     * Display the overflow menu if one is present.
     *
     * @return true if the overflow menu was shown, false otherwise.
     */
    public boolean showOverflowMenu() {
        Log.d(TAG, "showOverflowMenu mReserveOverflow :" + mReserveOverflow + ", isShowing :" + isOverflowMenuShowing() + ", mPostRunalbe :" + mPostedOpenRunnable);
        if (mReserveOverflow && mMenu != null && mMenuView != null &&
                mPostedOpenRunnable == null && !mMenu.getNonActionItems().isEmpty()) {
            OverflowPopup popup = new OverflowPopup(mContext, mMenu, mOverflowButton, true);
            mPostedOpenRunnable = new OpenOverflowRunnable(popup);
            Log.d(TAG, "showOverflowMenu post");
            // Post this for later; we might still need a layout for the anchor to be right.
            ((View) mMenuView).post(mPostedOpenRunnable);

            // ActionMenuPresenter uses null as a callback argument here
            // to indicate overflow is opening.
            super.onSubMenuSelected(null);

            return true;
        }
        return false;
    }

    /**
     * Hide the overflow menu if it is currently showing.
     *
     * @return true if the overflow menu was hidden, false otherwise.
     */
    public boolean hideOverflowMenu() {
        if (mPostedOpenRunnable != null && mMenuView != null) {
            ((View) mMenuView).removeCallbacks(mPostedOpenRunnable);
            mPostedOpenRunnable = null;
            return true;
        }

        MenuPopupHelper popup = mOverflowPopup;
        if (popup != null) {
            popup.dismiss();
            return true;
        }
        return false;
    }

    public void setListItemShowIcon(boolean show){
        Log.d(TAG,"ActionMenuPresenter forceShow :" + show);
        mListMenuShowIcon = show;
    }

    @Override
    public boolean onSubMenuSelected(com.gm.internal.menu.SubMenuBuilder menu) {
         Log.d(TAG,"onSubMenuSelected");
         return super.onSubMenuSelected(menu);
    }

    /**
     * Dismiss all popup menus - overflow and submenus.
     *
     * @return true if popups were dismissed, false otherwise. (This can be because none were open.)
     */
    public boolean dismissPopupMenus() {
        boolean result = hideOverflowMenu();
        return result;
    }

    /**
     * @return true if the overflow menu is currently showing
     */
    public boolean isOverflowMenuShowing() {
        return mOverflowPopup != null && mOverflowPopup.isShowing();
    }

    /**
     * @return true if space has been reserved in the action menu for an overflow item.
     */
    public boolean isOverflowReserved() {
        return mReserveOverflow;
    }

    public boolean flagActionItems() {
        final ArrayList<com.gm.internal.menu.MenuItemImpl> visibleItems = mMenu.getVisibleItems();
        final int itemsSize = visibleItems.size();
        int maxActionItems = mMaxItems;
        if(itemsSize > mMaxItems){
            maxActionItems = mMaxItems - 1;
        }else{
            markAllItemsActionButton(visibleItems);
            return true;
        }

        int requireActionItems = 0;
        for(int i = 0; i < itemsSize; i++){
            com.gm.internal.menu.MenuItemImpl item = visibleItems.get(i);
            if(item.requiresActionButton()){
                requireActionItems++;
            }
        }

        if(requireActionItems <= maxActionItems){
            int needMoreActionNum = 0;
            needMoreActionNum = maxActionItems - requireActionItems;
            for (int i = 0; i < itemsSize; i++) {
                com.gm.internal.menu.MenuItemImpl item = visibleItems.get(i);
                if (item.requiresActionButton()) {
                    item.setIsActionButton(true);
                } else {
                    if(needMoreActionNum <= 0){
                        item.setIsActionButton(false);
                    }else{
                        item.setIsActionButton(true);
                        needMoreActionNum--;
                    }
                }
            }
        }else{
            for (int i = 0; i < itemsSize; i++) {
                com.gm.internal.menu.MenuItemImpl item = visibleItems.get(i);
                if (item.requiresActionButton()) {
                    if(i < maxActionItems){
                        item.setIsActionButton(true);
                    }else{
                        item.setIsActionButton(false);
                    }
                } else {
                    item.setIsActionButton(false);
                }
            }
        }

        return true;
    }

    private void markAllItemsActionButton(ArrayList<com.gm.internal.menu.MenuItemImpl> items){
        for(int i = 0; i< items.size(); i++){
            com.gm.internal.menu.MenuItemImpl item = items.get(i);
            item.setIsActionButton(true);
        }
    }

    @Override
    public void onCloseMenu(com.gm.internal.menu.MenuBuilder menu, boolean allMenusAreClosing) {
        dismissPopupMenus();
        super.onCloseMenu(menu, allMenusAreClosing);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Log.d(TAG,"ActionMenuPresenter onSaveInstanceState");
        SavedState state = new SavedState();
        state.showOverflowMenu = isOverflowMenuShowing() ? 1 : 0;
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG,"ActionMenuPresenter onRestoreInstanceState");
        SavedState saved = (SavedState) state;
        if (saved.showOverflowMenu == 1) {
            Log.d(TAG,"should show overflow menu");
        }
    }

    public void setMenuView(com.gm.internal.menu.ActionMenuView menuView) {
        mMenuView = menuView;
        menuView.initialize(mMenu);
    }

    private static class SavedState implements Parcelable {
        public int showOverflowMenu;

        SavedState() {
        }

        SavedState(Parcel in) {
            showOverflowMenu = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(showOverflowMenu);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private class OverflowPopup extends MenuPopupHelper {

        public OverflowPopup(Context context, com.gm.internal.menu.MenuBuilder menu, View anchorView,
                             boolean overflowOnly) {
            super(context, menu, anchorView, overflowOnly, R.attr.gomeFloatMenuOverflowStyle);
            setGravity(Gravity.END);
            setForceShowIcon(mListMenuShowIcon);
        }

        @Override
        public void onDismiss() {
            super.onDismiss();
            mMenu.close();
            mOverflowPopup = null;
        }
    }

    private class OpenOverflowRunnable implements Runnable {
        private OverflowPopup mPopup;

        public OpenOverflowRunnable(OverflowPopup popup) {
            mPopup = popup;
        }

        public void run() {
            mMenu.changeMenuMode();
            final View menuView = (View) mMenuView;
            if (menuView != null && menuView.getWindowToken() != null && mPopup.tryShow()) {
                mOverflowPopup = mPopup;
            }
            mPostedOpenRunnable = null;
        }
    }

    private OverflowMenuButton createOverflowMenuButton(Context context){
        OverflowMenuButton overflowMenuButton = (OverflowMenuButton)mInflater.inflate(R.layout.action_menu_more_item_layout, null);
        overflowMenuButton.setTitle(context.getString(R.string.more_item_label));
        overflowMenuButton.setIcon(context.getDrawable(R.drawable.ic_gome_float_menu_more));
        overflowMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflowMenu();
            }
        });
        return overflowMenuButton;
    }
        
}
