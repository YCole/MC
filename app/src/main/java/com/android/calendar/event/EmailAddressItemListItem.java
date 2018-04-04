package com.android.calendar.event;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.calendar.R;

public class EmailAddressItemListItem extends RelativeLayout {
    public long mID;
    public String mUserName;
    public String mEmailAddress;
    public boolean mSelected;

    // private boolean mAllowBatch=true;
    private boolean mDownEvent;
    // private boolean mCachedViewPositions;
    // private int mCheckRight;
    // private int mStarLeft;
    ArrayList<HashMap<String, Object>> mData = null;
    private Drawable mSelectedIconOn;
    private Drawable mSelectedIconOff;

    private Drawable mSelector;

    // Padding to increase clickable areas on left & right of each list item
    private final static float CHECKMARK_PAD = 10.0F;
    private final static float STAR_PAD = 10.0F;
    private Resources resources;

    public EmailAddressItemListItem(Context context) {
        super(context);
        updateBackground();
        initSelectIcon();
        resources = context.getResources();
    }

    public EmailAddressItemListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateBackground();
        initSelectIcon();
    }

    public EmailAddressItemListItem(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        updateBackground();
        initSelectIcon();
    }

    /**
     * Overriding this method allows us to "catch" clicks in the checkbox or
     * star and process them accordingly.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        // int touchX = (int) event.getX();

        // if (!mCachedViewPositions) {
        // float paddingScale = resources.getDisplayMetrics().density;
        // int checkPadding = (int) ((CHECKMARK_PAD * paddingScale) + 0.5);
        // int starPadding = (int) ((STAR_PAD * paddingScale) + 0.5);
        // mCheckRight = findViewById(R.id.selected).getRight() +
        // checkPadding+5;
        // mStarLeft = findViewById(R.id.email_address).getRight() -
        // starPadding;
        // mCachedViewPositions = true;
        // }

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mDownEvent = true;
            /* if ((mAllowBatch && touchX < mCheckRight) || touchX > mStarLeft) */{
                handled = true;
            }
            break;

        case MotionEvent.ACTION_CANCEL:
            mDownEvent = false;
            break;

        case MotionEvent.ACTION_UP:
            if (mDownEvent) {
                /* if (mAllowBatch && touchX < mCheckRight) */{

                    try {
                        updateSelected(this);
                    } catch (Exception ex) {
                    }
                    handled = true;
                }
                // else if (touchX > mStarLeft) {
                // mFavorite = !mFavorite;
                // mAdapter.updateFavorite(this, mFavorite);
                // handled = true;
                // }
            }
            break;
        }

        if (handled) {
            postInvalidate();
        } else {
            handled = super.onTouchEvent(event);
        }
        return handled;
    }

    @Override
    public void draw(Canvas canvas) {
        // Update the background, before View.draw() draws it.
        TextView idView = (TextView) this.findViewById(R.id.email_address_id);
        String idStr = idView.getText().toString();
        int id = Integer.parseInt(idStr);
        HashMap<String, Object> map = getDataItemFrommMap(id);
        int selected = (Integer) map
                .get(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED);
        if (R.drawable.btn_check_on_normal_holo_light == selected) {
            setSelected(true);
            mSelected = true;
        } else {
            setSelected(false);
            mSelected = false;
        }
        // setSelected(this.mSelected);
        super.draw(canvas);
    }

    private void updateBackground() {
        mSelector = resources.getDrawable(R.drawable.emailaddresslist_selector);
        setBackgroundDrawable(mSelector);
    }

    /**
     * This is used as a callback from the list items, to set the selected state
     * 
     * @param itemView
     *            the item being changed
     * @param newSelected
     *            the new value of the selected flag (checkbox state)
     */
    public void updateSelected(EmailAddressItemListItem itemView) {
        mSelected = !mSelected;

        initSelectIcon();
        ImageView selectedView = (ImageView) itemView
                .findViewById(R.id.selected);
        TextView idView = (TextView) itemView
                .findViewById(R.id.email_address_id);
        String idStr = idView.getText().toString();
        int id = Integer.parseInt(idStr);
        HashMap<String, Object> map = getDataItemFrommMap(id);

        map.put(EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_CHECKED,
                mSelected ? R.drawable.btn_check_on_normal_holo_light
                        : R.drawable.btn_check_off_normal_holo_light);
        selectedView.setImageDrawable(mSelected ? mSelectedIconOn
                : mSelectedIconOff);

    }

    private void initSelectIcon() {
        if (mSelectedIconOn == null) {
            mSelectedIconOn = resources
                    .getDrawable(R.drawable.btn_check_on_normal_holo_light);
            mSelectedIconOff = resources
                    .getDrawable(R.drawable.btn_check_off_normal_holo_light);
        }
    }

    private HashMap<String, Object> getDataItemFrommMap(int id) {
        HashMap<String, Object> result = null;
        for (int i = 0; i < mData.size(); i++) {
            HashMap<String, Object> map = mData.get(i);
            if (map.get(
                    EmailAddressListSimpleAdapterViewBinder.EMAIL_ADDRESS_ID)
                    .toString().equalsIgnoreCase(Integer.toString(id))) {
                result = map;
                break;
            }
        }
        return result;
    }

    public void setMapData(ArrayList<HashMap<String, Object>> data) {
        mData = data;
    }
}
