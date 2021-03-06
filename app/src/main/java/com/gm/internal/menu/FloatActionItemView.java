package com.gm.internal.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.calendar.R;

/**
 * Created by zhenjie on 2017/6/12.
 */

public class FloatActionItemView extends FrameLayout implements com.gm.internal.menu.MenuView.ItemView, View.OnClickListener {
    private static final String TAG = "FloatActionMenu";

    private com.gm.internal.menu.MenuItemImpl mItemData;
    private CharSequence mTitle;
    private Drawable mIcon;
    private com.gm.internal.menu.MenuBuilder.ItemInvoker mItemInvoker;

    private static final int MAX_ICON_SIZE_WITH_TEXT = 24;
    private static final int MAX_ICON_SIZE_NO_TEXT = 32;
    
    private static final int ICON_ALPHA_NORMAL_STATE = 255;
    private static final int ICON_ALPHA_DISABLE_STATE = 100;
    
    private int mMaxIconSize;
    private int mNoTextMaxIconSize;
    private int mWithTextMaxIconSize;

    private TextView mTitleView;
    private ImageView mIconView;
    public FloatActionItemView(Context context) {
        this(context, null);
    }

    public FloatActionItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatActionItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        final Resources res = context.getResources();
        final float density = res.getDisplayMetrics().density;
        mWithTextMaxIconSize = (int) (MAX_ICON_SIZE_WITH_TEXT * density + 0.5f);
        mNoTextMaxIconSize = (int) (MAX_ICON_SIZE_NO_TEXT * density + 0.5f);
        mMaxIconSize = mWithTextMaxIconSize;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView)findViewById(R.id.action_title);
        mIconView = (ImageView)findViewById(R.id.action_icon);
    }

    @Override
    public void initialize(com.gm.internal.menu.MenuItemImpl itemData, int menuType) {
        Log.d(TAG, "ActionItemView initialize");
        mItemData = itemData;

        setTitle(mItemData.getTitleForItemView(this));
        setIcon(mItemData.getIcon());
        setId(mItemData.getItemId());

        setVisibility(mItemData.isVisible() ? VISIBLE : GONE);
        setActionItemEnable(mItemData.isEnabled());
    }

    private void setActionItemEnable(boolean enable){
        setEnabled(enable);
        if(mTitleView != null){
            mTitleView.setEnabled(enable);
        }
        if(mIconView != null){
            if(enable){
                mIconView.setImageAlpha(ICON_ALPHA_NORMAL_STATE);
            }else {
                mIconView.setImageAlpha(ICON_ALPHA_DISABLE_STATE);
            }
        }
    }
    
    @Override
    public com.gm.internal.menu.MenuItemImpl getItemData() {
        return mItemData;
    }

    private void updateTextAndIconButtonVisibility() {
        boolean visible = !TextUtils.isEmpty(mTitle);
        boolean iconVisible = mIcon != null;
        if(mTitleView == null || mIconView == null){
            return;
        }
        if(visible){
            mTitleView.setVisibility(View.VISIBLE);
        }else {
            mTitleView.setVisibility(View.GONE);
        }
        if(iconVisible){
            mIconView.setVisibility(View.VISIBLE);
        }else{
            mIconView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if(mTitleView != null){
            mTitleView.setText(mTitle);
        }
        updateTextAndIconButtonVisibility();
    }

    @Override
    public void setCheckable(boolean checkable) {

    }

    @Override
    public void setChecked(boolean checked) {

    }

    @Override
    public void setShortcut(boolean showShortcut, char shortcutKey) {

    }

    @Override
    public void setIcon(Drawable icon) {
        if(hasText()){
            mMaxIconSize = mWithTextMaxIconSize;
        }else{
            mMaxIconSize = mNoTextMaxIconSize;
        }
        mIcon = icon;
        if(mIconView !=null && mIcon != null){
            mIconView.setImageDrawable(mIcon);
        }
        updateTextAndIconButtonVisibility();
    }

    public boolean hasText() {
        return !TextUtils.isEmpty(mTitleView.getText());
    }

    @Override
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override
    public boolean showsIcon() {
        return true;
    }

    public void setItemInvoker(com.gm.internal.menu.MenuBuilder.ItemInvoker invoker) {
        mItemInvoker = invoker;
    }

    @Override
    public void onClick(View view) {
        if (mItemInvoker != null) {
            mItemInvoker.invokeItem(mItemData);
        }
    }
}
