package com.hct.gios.widget;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;

public class ListFragmentHCT extends ListFragment implements
        ActionMenuView.OnMenuItemClickListener {
    ToolBarHCT mToobar = null;
    int mScreenWidth = 0;
    public static int LEFT_BUTTON = 0;
    public static int RIGHT_BUTTON = 1;
    private int mSpiltflag = 0;

    /**
     * Attach to list view once the view hierarchy has been created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public View addBottomBarOptionMenu(View view, int resId) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mToobar = new ToolBarHCT(getActivity(), mScreenWidth);
        mToobar.setBackgroundColor(0xffe2e2e2);
        mToobar.inflateMenu(resId);
        mToobar.setVisibility(View.VISIBLE);
        mToobar.setOnMenuItemClickListener(this);
        ;
        mSpiltflag = 1;
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        // mLayoutParams.bottomMargin=Utils.dpToPx(this, mSplitActionbarHeight);

        LinearLayout.LayoutParams mToobarParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mToobarParams.bottomMargin = 0;

        if (view.getParent() != null) {
            ViewGroup vgroup = (ViewGroup) view.getParent();
            vgroup.removeView(view);
        }
        layout.addView(view, mLayoutParams);
        if (mToobar.getParent() != null) {
            ViewGroup vgroup = (ViewGroup) mToobar.getParent();
            vgroup.removeView(mToobar);
        }
        layout.addView(mToobar, mToobarParams);
        return layout;

    }

    public void SetBottomBarVisible(boolean visible) {
        if (visible) {
            mToobar.setVisibility(View.VISIBLE);

        } else {
            mToobar.setVisibility(View.GONE);

        }
    }

    public void SetBottomButtonVisible(int poistion, boolean visible) {
        if (poistion == LEFT_BUTTON) {
            if (visible) {
                mToobar.btnLeft.setVisibility(View.VISIBLE);
                mToobar.divider.setVisibility(View.VISIBLE);
            } else {
                mToobar.btnLeft.setVisibility(View.GONE);
                mToobar.divider.setVisibility(View.GONE);
            }
        } else {
            if (visible) {
                mToobar.btnRight.setVisibility(View.VISIBLE);
                mToobar.divider.setVisibility(View.VISIBLE);
            } else {
                mToobar.btnRight.setVisibility(View.GONE);
                mToobar.divider.setVisibility(View.GONE);
            }
        }
    }

    public void SetBottomButtonEnable(int poistion, boolean enable) {
        if (poistion == LEFT_BUTTON) {
            if (enable) {
                mToobar.btnLeft.setEnabled(true);

            } else {
                mToobar.btnLeft.setEnabled(false);
            }
        } else {
            if (enable) {
                mToobar.btnRight.setEnabled(true);

            } else {
                mToobar.btnRight.setEnabled(false);
            }
        }
    }

    public void SetBottomButtonText(int poistion, String title) {
        if (poistion == LEFT_BUTTON) {
            (mToobar.btnLeft).setText(title);

        } else {

            (mToobar.btnRight).setText(title);

        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem arg0) {
        // TODO Auto-generated method stub

        return onOptionsItemSelected(arg0);
    }
}
