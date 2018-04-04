/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hct.gios.widget.overlaydrawer;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

/**
 * This class provides a handy way to tie together the functionality of
 * {@link android.support.v4.widget.DrawerLayout} and the framework
 * <code>ActionBar</code> to implement the recommended design for navigation
 * drawers.
 * 
 * <p>
 * To use <code>ActionBarDrawerToggle</code>, create one in your Activity and
 * call through to the following methods corresponding to your Activity
 * callbacks:
 * </p>
 * 
 * <ul>
 * <li>
 * {@link android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
 * onConfigurationChanged}
 * <li>{@link android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
 * onOptionsItemSelected}</li>
 * </ul>
 * 
 * <p>
 * Call {@link #syncState()} from your <code>Activity</code>'s
 * {@link android.app.Activity#onPostCreate(android.os.Bundle) onPostCreate} to
 * synchronize the indicator with the state of the linked DrawerLayout after
 * <code>onRestoreInstanceState</code> has occurred.
 * </p>
 * 
 * <p>
 * <code>ActionBarDrawerToggle</code> can be used directly as a
 * {@link android.support.v4.widget.DrawerLayout.DrawerListener}, or if you are
 * already providing your own listener, call through to each of the listener
 * methods from your own.
 * </p>
 * 
 * <p>
 * You can customize the the animated toggle by defining the
 * {@link android.support.v7.appcompat.R.styleable#DrawerArrowToggle
 * drawerArrowStyle} in your ActionBar theme.
 */
public class ActionBarDrawerToggle implements
        MenuDrawer.OnDrawerStateChangeListener {

    private final MenuDrawer mDrawer;

    private DrawerToggle mSlider;
    private Drawable mHomeAsUpIndicator;
    private boolean mDrawerIndicatorEnabled = true;
    private boolean mHasCustomUpIndicator;
    // private final int mOpenDrawerContentDescRes;
    // private final int mCloseDrawerContentDescRes;
    // used in toolbar mode when DrawerToggle is disabled
    private View.OnClickListener mToolbarNavigationClickListener;

    public ActionBarDrawerToggle(MenuDrawer drawer, Activity activity) {
        // int openDrawerContentDescRes,
        // int closeDrawerContentDescRes) {
        mDrawer = drawer;
        // mOpenDrawerContentDescRes = openDrawerContentDescRes;
        // mCloseDrawerContentDescRes = closeDrawerContentDescRes;
        mSlider = new DrawerArrowDrawableToggle(activity);
        mDrawer.setOnDrawerStateChangeArrowListener(this);
        syncState();
    }

    public void setColor(int color) {
        if (mSlider != null)
            ((DrawerArrowDrawableToggle) mSlider).setColor(color);

    }

    /**
     * Synchronize the state of the drawer indicator/affordance with the linked
     * DrawerLayout.
     * 
     * <p>
     * This should be called from your <code>Activity</code>'s
     * {@link Activity#onPostCreate(android.os.Bundle) onPostCreate} method to
     * synchronize after the DrawerLayout's instance state has been restored,
     * and any other time when the state may have diverged in such a way that
     * the ActionBarDrawerToggle was not notified. (For example, if you stop
     * forwarding appropriate drawer events for a period of time.)
     * </p>
     */
    public void syncState() {
        if (mDrawer.getDrawerState() == MenuDrawer.STATE_OPEN) {
            mSlider.setPosition(1);
        } else if (mDrawer.getDrawerState() == MenuDrawer.STATE_CLOSED) {
            mSlider.setPosition(0);
        }
        if (mDrawerIndicatorEnabled) {
            setActionBarUpIndicator((Drawable) mSlider);
            // (mDrawer.isMenuVisible()) ?
            // mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
        }
    }

    /**
     * This method should always be called by your <code>Activity</code>'s
     * {@link Activity#onConfigurationChanged(android.content.res.Configuration)
     * onConfigurationChanged} method.
     * 
     * @param newConfig
     *            The new configuration
     */
    public void onConfigurationChanged(Configuration newConfig) {
        // Reload drawables that can change with configuration

        syncState();
    }

    /**
     * This method should be called by your <code>Activity</code>'s
     * {@link Activity#onOptionsItemSelected(android.view.MenuItem)
     * onOptionsItemSelected} method. If it returns true, your
     * <code>onOptionsItemSelected</code> method should return true and skip
     * further processing.
     * 
     * @param item
     *            the MenuItem instance representing the selected menu item
     * @return true if the event was handled and further processing should not
     *         occur
     */

    private void toggle() {
        if ((mDrawer.isMenuVisible())) {
            mDrawer.closeMenu();
        } else {
            mDrawer.openMenu();
            ;
        }
    }

    /**
     * Returns the fallback listener for Navigation icon click events.
     * 
     * @return The click listener which receives Navigation click events from
     *         Toolbar when drawer indicator is disabled.
     * @see #setToolbarNavigationClickListener(android.view.View.OnClickListener)
     * @see #setDrawerIndicatorEnabled(boolean)
     * @see #isDrawerIndicatorEnabled()
     */
    public View.OnClickListener getToolbarNavigationClickListener() {
        return mToolbarNavigationClickListener;
    }

    /**
     * When DrawerToggle is constructed with a Toolbar, it sets the click
     * listener on the Navigation icon. If you want to listen for clicks on the
     * Navigation icon when DrawerToggle is disabled (
     * {@link #setDrawerIndicatorEnabled(boolean)}, you should call this method
     * with your listener and DrawerToggle will forward click events to that
     * listener when drawer indicator is disabled.
     * 
     * @see #setDrawerIndicatorEnabled(boolean)
     */
    public void setToolbarNavigationClickListener(
            View.OnClickListener onToolbarNavigationClickListener) {
        mToolbarNavigationClickListener = onToolbarNavigationClickListener;
    }

    void setActionBarUpIndicator(Drawable upDrawable) {// , int contentDescRes)
                                                       // {
        mDrawer.setSlideDrawable(upDrawable);
    }

    static class DrawerArrowDrawableToggle extends DrawerArrowDrawable
            implements DrawerToggle {

        private final Activity mActivity;

        public DrawerArrowDrawableToggle(Activity activity) {
            super(activity);
            mActivity = activity;
        }

        public void setPosition(float position) {
            if (position == 1f) {
                setVerticalMirror(true);
            } else if (position == 0f) {
                setVerticalMirror(false);
            }
            super.setProgress(position);
        }

        @Override
        boolean isLayoutRtl() {
            return false;
        }

        public float getPosition() {
            return super.getProgress();
        }
    }

    /**
     * Interface for toggle drawables. Can be public in the future
     */
    static interface DrawerToggle {

        public void setPosition(float position);

        public float getPosition();
    }

    @Override
    public void onDrawerStateChange(int oldState, int newState) {
        // TODO Auto-generated method stub
        if (newState == OverlayDrawer.STATE_CLOSED) {
            mSlider.setPosition(0);

        } else if (newState == OverlayDrawer.STATE_CLOSING) {

        } else if (newState == OverlayDrawer.STATE_OPENING) {

        } else if (newState == OverlayDrawer.STATE_OPEN) {
            mSlider.setPosition(1);

        }

    }

    @Override
    public void onDrawerSlide(float openRatio, int offsetPixels) {
        // TODO Auto-generated method stub

        mSlider.setPosition(Math.min(1f, Math.max(0,
                (float) ((float) offsetPixels / (float) mDrawer.mMenuSize))));

    }
}
