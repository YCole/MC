package com.gm.internal.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.calendar.R;

/**
 * Created by zhenjie on 2017/9/11.
 */

public class OverflowMenuButton extends FrameLayout {
    private static final int ICON_ALPHA_NORMAL_STATE = 255;
    private static final int ICON_ALPHA_DISABLE_STATE = 100;

    private CharSequence mTitle;
    private Drawable mIcon;
    private TextView mTitleView;
    private ImageView mIconView;

    public OverflowMenuButton(@NonNull Context context) {
        this(context, null);
    }

    public OverflowMenuButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverflowMenuButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OverflowMenuButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (TextView)findViewById(R.id.action_title);
        mIconView = (ImageView)findViewById(R.id.action_icon);
    }

    public void setActionItemEnable(boolean enable){
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

    public void setTitle(CharSequence title) {
        mTitle = title;
        if(mTitleView != null){
            mTitleView.setText(mTitle);
        }
        updateTextAndIconButtonVisibility();
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
        if(mIconView !=null && mIcon != null){
            mIconView.setImageDrawable(mIcon);
        }
        updateTextAndIconButtonVisibility();
    }

    public boolean hasText() {
        return !TextUtils.isEmpty(mTitleView.getText());
    }
}
