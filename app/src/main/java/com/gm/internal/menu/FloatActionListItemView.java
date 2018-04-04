package com.gm.internal.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.calendar.R;

/**
 * Created by zhenjie on 2017/6/13.
 */

public class FloatActionListItemView extends LinearLayout implements com.gm.internal.menu.MenuView.ItemView {
    private static final String TAG = "FloatActionMenu";

    private com.gm.internal.menu.MenuItemImpl mItemData;
    private CharSequence mTitle;
    private Drawable mIcon;

    private boolean mAllowTextWithIcon;
    private boolean mExpandedFormat;

    private static final int MAX_ICON_SIZE = 32; // dp
    private int mMaxIconSize;
    private TextView mTitleView;
    private ImageView mIconView;

    public FloatActionListItemView(Context context) {
        this(context, null);
    }

    public FloatActionListItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatActionListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FloatActionListItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final Resources res = context.getResources();
        final float density = res.getDisplayMetrics().density;
        mMaxIconSize = (int) (MAX_ICON_SIZE * density + 0.5f);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView) findViewById(R.id.title);
        mIconView = (ImageView) findViewById(R.id.icon);
    }

    public com.gm.internal.menu.MenuItemImpl getItemData() {
        return mItemData;
    }

    @Override
    public void initialize(com.gm.internal.menu.MenuItemImpl itemData, int menuType) {
        mItemData = itemData;

        setIcon(itemData.getIcon());
        setTitle(itemData.getTitleForItemView(this)); // Title only takes effect if there is no icon
        setId(itemData.getItemId());

        setVisibility(itemData.isVisible() ? View.VISIBLE : View.GONE);
        setEnabled(itemData.isEnabled());
    }

    public boolean prefersCondensedTitle() {
        return true;
    }

    public void setCheckable(boolean checkable) {
        // TODO Support checkable action items
    }

    public void setChecked(boolean checked) {
        // TODO Support checkable action items
    }

    public void setShowIconWithText(boolean show){
        mAllowTextWithIcon = show;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
        if (icon != null && mAllowTextWithIcon) {
            mIconView.setImageDrawable(mAllowTextWithIcon ? icon : null);

            if (mIconView.getVisibility() != VISIBLE) {
                mIconView.setVisibility(VISIBLE);
            }
        } else {
            mIconView.setVisibility(GONE);
        }
    }

    public void setShortcut(boolean showShortcut, char shortcutKey) {
        // Action buttons don't show text for shortcut keys.
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        if (title != null) {
            mTitleView.setText(title);

            if (mTitleView.getVisibility() != VISIBLE) mTitleView.setVisibility(VISIBLE);
        } else {
            if (mTitleView.getVisibility() != GONE) mTitleView.setVisibility(GONE);
        }
    }

    public boolean showsIcon() {
        return true;
    }


}
