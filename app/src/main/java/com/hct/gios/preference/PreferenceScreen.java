/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.hct.gios.preference;

import android.annotation.NonNull;
import android.annotation.StyleRes;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.widget.ImageButton;

import com.android.calendar.R;

/**
 * Represents a top-level {@link Preference} that is the root of a Preference
 * hierarchy. A {@link PreferenceActivity} points to an instance of this class
 * to show the preferences. To instantiate this class, use
 * {@link PreferenceManager#createPreferenceScreen(Context)}.
 * <ul>
 * This class can appear in two places:
 * <li>When a {@link PreferenceActivity} points to this, it is used as the root
 * and is not shown (only the contained preferences are shown).
 * <li>When it appears inside another preference hierarchy, it is shown and
 * serves as the gateway to another screen of preferences (either by showing
 * another screen of preferences as a {@link Dialog} or via a
 * {@link Context#startActivity(android.content.Intent)} from the
 * {@link Preference#getIntent()}). The children of this
 * {@link PreferenceScreen} are NOT shown in the screen that this
 * {@link PreferenceScreen} is shown in. Instead, a separate screen will be
 * shown when this preference is clicked.
 * </ul>
 * <p>
 * Here's an example XML layout of a PreferenceScreen:
 * </p>
 * 
 * <pre>
 * &lt;PreferenceScreen
 *         xmlns:android="http://schemas.android.com/apk/res/android"
 *         android:key="first_preferencescreen"&gt;
 *     &lt;CheckBoxPreference
 *             android:key="wifi enabled"
 *             android:title="WiFi" /&gt;
 *     &lt;PreferenceScreen
 *             android:key="second_preferencescreen"
 *             android:title="WiFi settings"&gt;
 *         &lt;CheckBoxPreference
 *                 android:key="prefer wifi"
 *                 android:title="Prefer WiFi" /&gt;
 *         ... other preferences here ...
 *     &lt;/PreferenceScreen&gt;
 * &lt;/PreferenceScreen&gt;
 * </pre>
 * <p>
 * In this example, the "first_preferencescreen" will be used as the root of the
 * hierarchy and given to a {@link PreferenceActivity}. The first screen will
 * show preferences "WiFi" (which can be used to quickly enable/disable WiFi)
 * and "WiFi settings". The "WiFi settings" is the "second_preferencescreen" and
 * when clicked will show another screen of preferences such as "Prefer WiFi"
 * (and the other preferences that are children of the "second_preferencescreen"
 * tag).
 * 
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>
 * For information about building a settings UI with Preferences, read the <a
 * href="{@docRoot}guide/topics/ui/settings.html">Settings</a> guide.
 * </p>
 * </div>
 * 
 * @see PreferenceCategory
 */
public final class PreferenceScreen extends PreferenceGroup implements
        AdapterView.OnItemClickListener, DialogInterface.OnDismissListener {

    private ListAdapter mRootAdapter;

    private Dialog mDialog;

    private ListView mListView;

    LinearLayout mBackground;
    int mColor;
    int flag = 0;

    /**
     * Do NOT use this constructor, use
     * {@link PreferenceManager#createPreferenceScreen(Context)}.
     * 
     * @hide-
     */
    public PreferenceScreen(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.preferenceScreenStyle);
        mBackground = new LinearLayout(context);
    }

    /**
     * Returns an adapter that can be attached to a {@link PreferenceActivity}
     * or {@link PreferenceFragment} to show the preferences contained in this
     * {@link PreferenceScreen}.
     * <p>
     * This {@link PreferenceScreen} will NOT appear in the returned adapter,
     * instead it appears in the hierarchy above this {@link PreferenceScreen}.
     * <p>
     * This adapter's {@link Adapter#getItem(int)} should always return a
     * subclass of {@link Preference}.
     * 
     * @return An adapter that provides the {@link Preference} contained in this
     *         {@link PreferenceScreen}.
     */
    public ListAdapter getRootAdapter() {
        if (mRootAdapter == null) {
            mRootAdapter = onCreateRootAdapter();
        }

        return mRootAdapter;
    }

    /**
     * Creates the root adapter.
     * 
     * @return An adapter that contains the preferences contained in this
     *         {@link PreferenceScreen}.
     * @see #getRootAdapter()
     */
    protected ListAdapter onCreateRootAdapter() {
        return new PreferenceGroupAdapter(this);
    }

    /**
     * Binds a {@link ListView} to the preferences contained in this
     * {@link PreferenceScreen} via {@link #getRootAdapter()}. It also handles
     * passing list item clicks to the corresponding {@link Preference}
     * contained by this {@link PreferenceScreen}.
     * 
     * @param listView
     *            The list view to attach to.
     */
    public void bind(ListView listView) {
        listView.setOnItemClickListener(this);
        listView.setAdapter(getRootAdapter());

        onAttachedToActivity();
    }

    @Override
    protected void onClick() {
        if (getIntent() != null || getFragment() != null
                || getPreferenceCount() == 0) {
            return;
        }

        showDialog(null);
    }

    private void showDialog(Bundle state) {
        final Context context = getContext();
        if (mListView != null) {
            mListView.setAdapter(null);
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childPrefScreen = inflater.inflate(
                R.layout.preference_list_fragment, null);
        mListView = (ListView) childPrefScreen.findViewById(android.R.id.list);
        bind(mListView);

        // Set the title bar if title is available, else no title bar
        final CharSequence title = getTitle();
        final Dialog dialog = mDialog = new Dialog(context,
                context.getThemeResId());
        if (TextUtils.isEmpty(title)) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } else {
            dialog.setTitle(title);
        }

        if (flag == 1)
        // hct_modify
        {
            /*
             * dialog.getWindow().addFlags(
             * WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); LinearLayout
             * mainLayout = new LinearLayout(context);
             * mainLayout.setOrientation(LinearLayout.VERTICAL); if
             * (mBackground.getParent() != null) { ViewGroup vgroup =
             * (ViewGroup) mBackground.getParent();
             * vgroup.removeView(mBackground); } mainLayout.addView(
             * mBackground, new LinearLayout.LayoutParams(
             * LinearLayout.LayoutParams.MATCH_PARENT, dpToPx( context, 77)));
             * 
             * if (childPrefScreen.getParent() != null) { ViewGroup vgroup =
             * (ViewGroup) childPrefScreen.getParent();
             * vgroup.removeView(childPrefScreen); }
             * mainLayout.addView(childPrefScreen, new
             * LinearLayout.LayoutParams(
             * LinearLayout.LayoutParams.MATCH_PARENT,
             * LinearLayout.LayoutParams.WRAP_CONTENT, 1));
             * mBackground.setBackgroundColor(mColor); //
             * getDialog().getActionBar().setBackgroundDrawable(new //
             * ColorDrawable(0xff004567)); // hct_modify
             */
            dialog.getWindow().setStatusBarColor(mColor);
            dialog.setContentView(childPrefScreen);
        } else {
            dialog.setContentView(childPrefScreen);
        }

        dialog.setOnDismissListener(this);
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }

        getPreferenceManager().addPreferencesScreen(dialog);

        dialog.show();
        if (dialog.getActionBar() != null && flag == 1) {

            dialog.getActionBar().setBackgroundDrawable(
                    new ColorDrawable(mColor));
            dialog.getActionBar().setDisplayHomeAsUpEnabled(true);
            dialog.getActionBar().setDisplayUseLogoEnabled(true);
            int actionbarId = Resources.getSystem().getIdentifier("action_bar",
                    "id", "android");

            View decor = dialog.getWindow().getDecorView();
            if (decor != null) {

                Drawable dr = context.getResources().getDrawable(
                        R.drawable.ic_ab_back_material);
                dr.setTint(context.getResources().getColor(
                        R.color.gos_common_acb_icon));
                dialog.getActionBar().setHomeAsUpIndicator(dr);
                ViewGroup actionBarView = (ViewGroup) decor
                        .findViewById(actionbarId);
                if (actionBarView == null) {
                    return;
                }

                View view = actionBarView.getChildAt(0);
                if (view != null) {
                    if (view instanceof ImageButton) {
                        view.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                dialog.dismiss();
                            }
                        });
                    }
                }
                setAllChildViewsColor(actionBarView, context.getResources()
                        .getColor(R.color.gos_common_acb_icon), context
                        .getResources().getColor(R.color.gos_common_acb_txt));
                // actionBarView.setBackground(new ColorDrawable(0xff00ffff));
                actionBarView
                        .setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {

                            @Override
                            public void onChildViewRemoved(View parent,
                                    View child) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onChildViewAdded(View parent, View child) {

                                setAllChildViewsColor(
                                        child,
                                        context.getResources().getColor(
                                                R.color.gos_common_acb_icon),
                                        context.getResources().getColor(
                                                R.color.gos_common_acb_txt));

                            }
                        });

            }
        }
    }

    public void setAllChildViewsColor(View view, final int color,
            final int textColor) {

        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            int searchId = Resources.getSystem().getIdentifier("search_bar",
                    "id", "android");
            if (searchId == vp.getId()) {
                return;
            }
            vp.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {

                @Override
                public void onChildViewRemoved(View parent, View child) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onChildViewAdded(View parent, View child) {

                    setAllChildViewsColor(child, color, textColor);

                }
            });
            // vp.setBackground(new ColorDrawable(0xff00ff00));

            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                setAllChildViewsColor(viewchild, color, textColor);
            }

        } else {

            if (view instanceof ImageView) {
                Drawable drawable = ((ImageView) view).getDrawable();

                if (drawable != null) {
                    drawable.setTint(color);
                    ((ImageView) view).setImageDrawable(drawable);
                }
                // view.setBackgroundResource(R.drawable.item_background_material);
            } else if (view instanceof TextView) {

                ((TextView) view).setTextColor(textColor);

                Drawable[] dras = ((TextView) view).getCompoundDrawables();
                if (dras[0] != null) {
                    dras[0].setTint(color);
                    ((TextView) view).setCompoundDrawables(dras[0], null, null,
                            null);
                }
            }

            return;
        }

    }

    public int dpToPx(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public void setIndicatorColor(int color) {
        mColor = color;
        flag = 1;

    }

    public void onDismiss(DialogInterface dialog) {
        mDialog = null;
        getPreferenceManager().removePreferencesScreen(dialog);
    }

    /**
     * Used to get a handle to the dialog. This is useful for cases where we
     * want to manipulate the dialog as we would with any other activity or
     * view.
     */
    public Dialog getDialog() {
        return mDialog;
    }

    public void onItemClick(AdapterView parent, View view, int position, long id) {
        // If the list has headers, subtract them from the index.
        if (parent instanceof ListView) {
            position -= ((ListView) parent).getHeaderViewsCount();
        }
        Object item = getRootAdapter().getItem(position);
        if (!(item instanceof Preference))
            return;

        final Preference preference = (Preference) item;
        preference.performClick(this);
    }

    @Override
    protected boolean isOnSameScreenAsChildren() {
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final Dialog dialog = mDialog;
        if (dialog == null || !dialog.isShowing()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = dialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }

    private static class SavedState extends BaseSavedState {
        boolean isDialogShowing;
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            isDialogShowing = source.readInt() == 1;
            dialogBundle = source.readBundle();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isDialogShowing ? 1 : 0);
            dest.writeBundle(dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
