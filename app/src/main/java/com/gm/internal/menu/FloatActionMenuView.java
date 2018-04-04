package com.gm.internal.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.android.calendar.R;

/**
 * Created by zhenjie on 2017/6/12.
 */

public class FloatActionMenuView extends FrameLayout {
    private static final String TAG = "FloatActionMenu";

    private static final int PRESENTER_FLOATMENU_VIEW_ID = 1;
    private static final int DEFAULT_ELEVATION = 4;
    private static final int DEFAULT_TRANSLATION_Z = 4;
    private static final int DEFAULT_MAX_ITEM = 5;
    private static final int MIN_ITEM_SIZE = 1;
    private static final int MAX_ITEM_SIZE = 5;

    private com.gm.internal.menu.MenuBuilder mMenu;

    private OnFloatActionMenuSelectedListener mMenuItemSelectedListener;

    private ActionMenuPresenter mMenuPresenter;
    private MenuInflater mMenuInflater;
    private com.gm.internal.menu.ActionMenuView mMenuView;
    private int mMaxItems;

    public FloatActionMenuView(@NonNull Context context) {
        this(context, null);
    }

    public FloatActionMenuView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatActionMenuView(Context context, AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FloatActionMenuView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mMenuPresenter = new ActionMenuPresenter(context);
        mMenu = new com.gm.internal.menu.MenuBuilder(context);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FloatActionMenuView);
        //set the MaxItems
        String moreItemText = ta.getString(R.styleable.FloatActionMenuView_overflowButtonText);
        Drawable moreItemIconRes = ta.getDrawable(R.styleable.FloatActionMenuView_overflowButtonIcon);
        if(moreItemIconRes != null){
            mMenuPresenter.setOverflowButtonIcon(moreItemIconRes);
        }
        if(!TextUtils.isEmpty(moreItemText)){
            mMenuPresenter.setOverflowButtonText(moreItemText);
        }
        mMaxItems = ta.getInteger(R.styleable.FloatActionMenuView_maxItems, 0);
        if (mMaxItems >= 1) {
            mMenuPresenter.setMaxItems(mMaxItems > DEFAULT_MAX_ITEM ? DEFAULT_MAX_ITEM : mMaxItems);
        }
        //set the overflow menu show icon or not
        boolean listItemShowIcon = ta.getBoolean(R.styleable.FloatActionMenuView_listItemShowIcon, false);
        mMenuPresenter.setListItemShowIcon(listItemShowIcon);
        Log.d(TAG,"FloatActionMenuView forceShow :" + listItemShowIcon);
        mMenu.setCallback(new com.gm.internal.menu.MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(com.gm.internal.menu.MenuBuilder menu, MenuItem item) {
                return mMenuItemSelectedListener != null && mMenuItemSelectedListener.onFloatActionItemSelected(item);
            }

            @Override
            public void onMenuModeChange(com.gm.internal.menu.MenuBuilder menu) {

            }
        });
        //set the float action menu default background
        if (getBackground() == null) {
            Drawable background = getResources().getDrawable(R.drawable.gome_float_menu_bar_background, null);
            setBackground(background);
        }else{

        }
        //set the float action menu default elevation and translationZ
        if (getElevation() == 0 && getTranslationZ() == 0) {
            setElevation(DEFAULT_ELEVATION);
            setTranslationZ(DEFAULT_TRANSLATION_Z);
        }
        mMenuPresenter.setId(PRESENTER_FLOATMENU_VIEW_ID);
        mMenuPresenter.initForMenu(context, mMenu);
        mMenu.addMenuPresenter(mMenuPresenter);
        mMenuView = (ActionMenuView) mMenuPresenter.getMenuView(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mMenuView, layoutParams);
        if (ta.hasValue(R.styleable.FloatActionMenuView_menu)) {
            inflateMenu(ta.getResourceId(R.styleable.FloatActionMenuView_menu, 0));
        }
        ta.recycle();

    }

    /**
     * Inflate a menu resource into this navigation view.
     * <p>
     * <p>Existing items in the menu will not be modified or removed.</p>
     *
     * @param resId ID of a menu resource to inflate
     */
    public void inflateMenu(int resId) {
        Log.d(TAG, "-------- FloatActionMenuView inflateMenu --------");
        mMenu.clearAll();
        getMenuInflater().inflate(resId, mMenu);
        mMenuPresenter.updateMenuView(false);
    }

    private MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new MenuInflater(getContext());
        }
        return mMenuInflater;
    }

    public void invalidateFloatMenu(){
        if(mMenuPresenter != null){
            mMenuPresenter.updateMenuView(false);
        }
    }

    /**
     * set menu item visible by the menu ids
     * @param menuIds the menu item id
     */
    public void setMenuItemsVisible(int... menuIds){
        Log.d(TAG,"setMenuItemsVisible");
        for(int id : menuIds){
            MenuItem item = mMenu.findItem(id);
            if(item != null){
                item.setVisible(true);
            }
        }
        invalidateFloatMenu();
    }
    
    public boolean isMenuItemsVisible(int... menuIds){
        Log.d(TAG,"setMenuItemsVisible");
        boolean visible = false;
        for(int id : menuIds){
            MenuItem item = mMenu.findItem(id);
            if(item != null){
                visible = item.isVisible();
            }
        }

        return visible;
    }

    /**
     * set menu item invisible by the menu ids
     * @param menuIds the menu item id
     */
    public void setMenuItemsInVisible(int... menuIds){
        Log.d(TAG,"setMenuItemsInVisible");
        for(int id : menuIds){
            MenuItem item = mMenu.findItem(id);
            if(item != null){
                item.setVisible(false);
            }
        }
        invalidateFloatMenu();
    }

   //MODIFIED BEGIN BY ZHENJIE.CHANG FOR ENABLE/DISABLE FLOAT_MENU_VIEW AT 2017/07/5
    /**
     * set menu item Enabled by the menu ids
     * @param menuIds the menu item id
     */
    public void setMenuItemsEnable(int... menuIds){
        Log.d(TAG,"setMenuItemsEnable");
        for(int id : menuIds){
            MenuItem item = mMenu.findItem(id);
            if(item != null){
                item.setEnabled(true);
            }
        }
        invalidateFloatMenu();
    }
    /**
     * set menu item Disabled by the menu ids
     * @param menuIds the menu item id
     */
    public void setMenuItemsDisable(int... menuIds){
        Log.d(TAG,"setMenuItemsDisable");
        for(int id : menuIds){
            MenuItem item = mMenu.findItem(id);
            if(item != null){
                item.setEnabled(false);
            }
        }
        invalidateFloatMenu();
    }
    //MODIFIED END BY ZHENJIE.CHANG FOR ENABLE/DISABLE FLOAT_MENU_VIEW AT 2017/07/5

    /**
     * set the maxItems of FloatActionMenu
     * @param maxItems must > 1 && < 6
     */
    public void setMaxItems(int maxItems){
        if(maxItems >= MIN_ITEM_SIZE && maxItems <= MAX_ITEM_SIZE){
            mMenuPresenter.setMaxItems(maxItems);
            invalidateFloatMenu();
        }
    }

    /**
     * set the overflow button icon
     * @param iconRes icon ID
     */
    public void setOverflowButtonIcon(int iconRes){
        if(mMenuPresenter != null){
            Drawable icon = getResources().getDrawable(iconRes);
            mMenuPresenter.setOverflowButtonIcon(icon);
            invalidateFloatMenu();
        }
    }

    /**
     * set the overflow button title
     * @param text title
     */
    public void setOverflowButtonText(String text){
        if(mMenuPresenter != null){
            mMenuPresenter.setOverflowButtonText(text);
            invalidateFloatMenu();
        }
    }

    /**
     *set the overflow button icon and title
     * @param text title
     * @param iconRes icon id
     */
    public void setOverflowButtonRes(String text, int iconRes){
        if(mMenuPresenter != null){
            Drawable icon = getResources().getDrawable(iconRes);
            mMenuPresenter.setOverflowButtonIcon(icon);
            mMenuPresenter.setOverflowButtonText(text);
            invalidateFloatMenu();
        }
    }

    /**
     * reset the overflow button style
     */
    public void resetOverflowButton(){
        if(mMenuPresenter != null){
            mMenuPresenter.resetOverflowButton();
            invalidateFloatMenu();
        }
    }
    
    /**
     * @param enable
     */
    public void setOverflowButtonEnable(boolean enable){
        if(mMenuPresenter != null){
            mMenuPresenter.setOverflowButtonEnable(enable);
            invalidateFloatMenu();
        }
    }

    public void setOnFloatActionMenuSelectedListener(OnFloatActionMenuSelectedListener listener) {
        mMenuItemSelectedListener = listener;
    }

    public interface OnFloatActionMenuSelectedListener {
        boolean onFloatActionItemSelected(@NonNull MenuItem item);
    }
}
