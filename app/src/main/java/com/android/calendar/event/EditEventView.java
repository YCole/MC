/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.calendar.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ResourceCursorAdapter;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.calendar.CalendarController;
import com.android.calendar.CalendarEventModel;
import com.android.calendar.CalendarEventModel.Attendee;
import com.android.calendar.CalendarEventModel.ReminderEntry;
import com.android.calendar.EmailAddressAdapter;
import com.android.calendar.EventInfoFragment;
import com.android.calendar.GeneralPreferences;
import com.android.calendar.R;
import com.android.calendar.RecipientAdapter;
import com.android.calendar.RecipientsEditor;
import com.android.calendar.TimezoneAdapter;
import com.android.calendar.TimezoneAdapter.TimezoneRow;
import com.android.calendar.Utils;
import com.android.calendar.event.EditEventHelper.EditDoneRunnable;
import com.android.calendar.recurrencepicker.RecurrencePickerDialog;
import com.android.calendarcommon2.EventRecurrence;
import com.android.common.Rfc822InputFilter;
import com.android.common.Rfc822Validator;
import com.android.ex.chips.AccountSpecifier;
import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.ChipsUtil;
import com.android.timezonepicker.TimeZonePickerUtils;
import com.apkfuns.logutils.LogUtils;
import com.hct.calendar.event.EventItem;
import com.hct.calendar.lbs.LbsServiceHelper;
import com.hct.gios.widget.AlertDialog;
import com.hct.gios.widget.CheckBoxHCT;
import com.hct.gios.widget.DateTimePickerDialogHCT;
import com.hct.gios.widget.DateTimePickerDialogHCT.OnDateSetListener;
import com.hct.gios.widget.DateTimePickerDialogHCT.OnTimeSetListener;
import com.hct.gios.widget.DateTimePickerHCT;
import com.hct.gios.widget.DateTimePickerHCT.OnDateChangedListener;
import com.hct.gios.widget.DateTimePickerHCT.OnTimeChangedListener;
import com.hct.gios.widget.RadioButtonHCT;

public class EditEventView implements View.OnClickListener,
        DialogInterface.OnCancelListener, DialogInterface.OnClickListener,
        OnItemSelectedListener, RecurrencePickerDialog.OnRecurrenceSetListener {

    private static final String TAG = "EditEventView";
    private static final String GOOGLE_SECONDARY_CALENDAR = "calendar.google.com";
    private static final String PERIOD_SPACE = ". ";
    private static final long MAXDATE = 2145887999;
    private static final long MINDATE = 0;

    private static final String FRAG_TAG_RECUR_PICKER = "recurrencePickerDialogFragment";
    private AddressValidator mInternalValidator;
    ArrayList<View> mEditOnlyList = new ArrayList<View>();
    ArrayList<View> mEditViewList = new ArrayList<View>();
    ArrayList<View> mViewOnlyList = new ArrayList<View>();
    TextView mLoadingMessage;
    ScrollView mScrollView;

    // Button mStartDateButton;
    // Button mEndDateButton;
    // Button mStartTimeButton;
    // Button mEndTimeButton;

    Button mStartDateTimeButton;
    Button mEndDateTimeButton;

    Button mTimezoneButton;
    View mColorPickerNewEvent;
    View mColorPickerExistingEvent;
    OnClickListener mChangeColorOnClickListener;
    View mTimezoneRow;
    View mFromRow;
    View mToRow;
    TextView mStartTimeHome;
    TextView mStartDateHome;
    TextView mEndTimeHome;
    TextView mEndDateHome;
    TextView mShowMeAs;
    TextView mPrivacy;
    CheckBoxHCT mAllDayCheckBox;
    Spinner mCalendarsSpinner;
    Spinner mRepeatsSpinner;
    Spinner mAvailabilitySpinner;
    Spinner mAccessLevelSpinner;
    RadioGroup mResponseRadioGroup;
    EditText mTitleTextView;
    AutoCompleteTextView mLocationTextView;
    // EventLocationAdapter mLocationAdapter;
    TextView mDescriptionTextView;
    TextView mWhenView;
    TextView mTimezoneTextView;
    TextView mTimezoneLabel;
    TextView mReminderLabel;
    LinearLayout mRemindersContainer;
    RelativeLayout mReminderAddContainer;
    // MultiAutoCompleteTextView mAttendeesList;
    RecipientsEditor mAttendeesList;
    View mCalendarSelectorGroup;
    View mCalendarSelectorWrapper;
    View mCalendarStaticGroup;
    View mLocationGroup;
    View mDescriptionGroup;
    View mRemindersGroup;
    View mResponseGroup;
    View mOrganizerGroup;
    View mAttendeesGroup;
    View mStartHomeGroup;
    View mEndHomeGroup;

    View mRepeatsGroup;
    View mCalendarGroup;
    View mTimeZoneGroup;
    View mLocationRemindGroup;

    // HCT_MODIFY,lixiange MF3.0 new event
    Button mMoreButton;
    LinearLayout mEditEvent2;
    LinearLayout mMoreLayout;
    LinearLayout mEditEventColor;
    ImageButton mChangeColorRed;
    ImageButton mChangeColorOrange;
    ImageButton mChangeColorYellow;
    ImageButton mChangeColorGreen;
    ImageButton mChangeColorDim;
    ImageButton mChangeColorBlue;
    ImageButton mChangeColorPurple;
    int mEventColor;
    int mEventRed, mEventOrange, mEventYellow, mEventGreen, mEventDim,
            mEventBlue, mEventPurple;
    ImageButton mAddContactButton;
    private AlertDialog viewContactDialog;
    public static boolean isEqualMycalendar;
    private TimezoneAdapter mTimezoneAdapter;
    private Handler mHandler;
    private boolean timeDateSetOK = false;
    // HCT_MODIFY,lixiange MF3.0 new event

    /**
     * HCT_MODIFY start add LBS lixiange MF3.0
     *********************************************/
    LinearLayout mMapContainer;
    ImageButton mDeleteMapButton;
    ImageView mAddLocationButton;
    ImageView mMap = null;
    public static String MAP_DIR;
    /**
     * HCT_MODIFY start add LBS lixiange MF3.0
     *********************************************/

    private CheckBoxHCT mLocationReminderCB;
    private TextView mLocationReminderTI;
    private SeekBar mReminderRangeSB;

    private int[] mOriginalPadding = new int[4];

    public boolean mIsMultipane;
    private ProgressDialog mLoadingCalendarsDialog;
    private AlertDialog mNoCalendarsDialog;
    private AlertDialog mTimezoneDialog;// DialogFragment mTimezoneDialog;
    private Activity mActivity;
    private EditDoneRunnable mDone;
    private View mView;
    private CalendarEventModel mModel;
    private Cursor mCalendarsCursor;
    private AccountSpecifier mAddressAdapter;
    private Rfc822Validator mEmailValidator;

    public boolean mTimeSelectedWasStartTime;
    public boolean mDateSelectedWasStartDate;

    private boolean dateSetOkClicked = false;

    /**
     * Contents of the "minutes" spinner. This has default values from the XML
     * file, augmented with any additional values that were already associated
     * with the event.
     */
    private ArrayList<Integer> mReminderMinuteValues;
    private ArrayList<String> mReminderMinuteLabels;

    /**
     * Contents of the all day "minutes" spinner. This has default values from
     * the XML file, augmented with any additional values that were already
     * associated with the event.
     */
    private ArrayList<Integer> mAlldayReminderMinuteValues;
    private ArrayList<String> mAlldayReminderMinuteLabels;
    /**
     * Contents of the "methods" spinner. The "values" list specifies the method
     * constant (e.g. {@link Reminders#METHOD_ALERT}) associated with the
     * labels. Any methods that aren't allowed by the Calendar will be removed.
     */
    private ArrayList<Integer> mReminderMethodValues;
    private ArrayList<String> mReminderMethodLabels;

    /**
     * Contents of the "availability" spinner. The "values" list specifies the
     * type constant (e.g. {@link Events#AVAILABILITY_BUSY}) associated with the
     * labels. Any types that aren't allowed by the Calendar will be removed.
     */
    private ArrayList<Integer> mAvailabilityValues;
    private ArrayList<String> mAvailabilityLabels;
    private ArrayList<String> mAccessLevelLabels;
    private ArrayList<String> mOriginalAvailabilityLabels;
    private ArrayAdapter<String> mAvailabilityAdapter;
    private ArrayAdapter<String> mAccessLevelAdapter;
    private boolean mAvailabilityExplicitlySet;
    private boolean mAllDayChangingAvailability;
    private int mAvailabilityCurrentlySelected;

    private int mDefaultReminderMinutes;

    private boolean mSaveAfterQueryComplete = false;

    private TimeZonePickerUtils mTzPickerUtils;
    private Time mStartTime;
    private Time mEndTime;
    private String mTimezone;
    private boolean mAllDay = false;
    private int repeatFrequence = 0;
    public int eventDuration = 0;
    private int mModification = EditEventHelper.MODIFY_UNINITIALIZED;

    private EventRecurrence mEventRecurrence = new EventRecurrence();

    private ArrayList<LinearLayout> mReminderItems = new ArrayList<LinearLayout>(
            0);
    private ArrayList<ReminderEntry> mUnsupportedReminders = new ArrayList<ReminderEntry>();
    private String mRrule;

    private static StringBuilder mSB = new StringBuilder(50);
    private static Formatter mF = new Formatter(mSB, Locale.getDefault());

    // HCT_MODIFY lixiange MF3.0 add My Calendar
    private static final String MY_CALNEDAR = "My calendar";
    private ArrayList<Integer> mRecurrenceIndexes = new ArrayList<Integer>(0);

    private int[] mOriginalSpinnerPadding = new int[4];
    // HCT_MODIFY lixiange MF3.0 add My Calendar

    /**
     * HCT_MODIFY start add LBS lixiange MF3.0
     *********************************************/
    private String locationMap = null;
    /** HCT_MODIFY end add LBS lixiange MF3.0 *********************************************/

    /**
     * added by hct.gengbin for location, begin
     */
    private int mEventType = 0;
    private int mLocationRemindRadius = 100;
    private String mMapParam = null;
    private boolean mHasContactsGrant = true;
    private boolean mHasCallLogGrant = true;
    public boolean isdeletemap = false;
    private DateTimePickerDialogHCT_from mDtpdInstall = null;
    private static final double SELECT_EXEMAIL_FROM_CONTACT_VERSION = 4.0;

    public void addMapLocation(Intent data) {
        mMapScreenshotFile = data.getStringExtra("MapScreenshot");
        mMapContainer.setVisibility(View.VISIBLE);
        mMap.setVisibility(View.VISIBLE);
        if (data.getStringExtra("mSuggestionresult") != null) {
            mMapParam = data.getStringExtra("mSuggestionresult");
            String address = null;
            JSONObject jsonObject = null;
            try {
                if (mMapParam != null && !"".equals(mMapParam)) {
                    jsonObject = new JSONObject(mMapParam);
                    address = jsonObject.getString("Address");
                    mLocationTextString = address;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mLocationTextView.setText(address);
            if (LbsServiceHelper.isLbsLocationFenceAvilible(mActivity)) {
                mLocationReminderCB.setVisibility(View.VISIBLE);
                mLocationReminderTI.setVisibility(View.VISIBLE);
            }
            if (mLocationTextString != null) {
                mLocationTextView.setText(mLocationTextString);
                if (Utils.checkAndRequestPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Utils.REQUEST_PERMISSIONS_EXTERNAL_STORAGE)) {
                    setSaveMap(mActivity);
                }
            }
        }
    }

    public void onDestroy() {
    }

    /**
     * added by hct.gengbin for location, begin
     */

    /* This class is used to update the time buttons. */
    private class TimeListener implements OnTimeSetListener {
        private View mView;

        public TimeListener(View view) {
            mView = view;
        }

        @Override
        public void onTimeSet(DateTimePickerHCT view, int hourOfDay, int minute) {
            // Cache the member variables locally to avoid inner class overhead.
            if (!timeDateSetOK)
                return;
            Time startTime = mStartTime;
            Time endTime = mEndTime;

            // Cache the start and end millis so that we limit the number
            // of calls to normalize() and toMillis(), which are fairly
            // expensive.
            long startMillis;
            long endMillis;
            if (mView == mStartDateTimeButton) {
                // The start time was changed.
                int hourDuration = endTime.hour - startTime.hour;
                int minuteDuration = endTime.minute - startTime.minute;

                startTime.hour = hourOfDay;
                startTime.minute = minute;
                startMillis = startTime.normalize(true);

                // Also update the end time to keep the duration constant.
                endTime.hour = hourOfDay + hourDuration;
                endTime.minute = minute + minuteDuration;

                // Update tz in case the start time switched from/to DLS
                populateTimezone(startMillis);
            } else {
                // The end time was changed.
                startMillis = startTime.toMillis(true);
                endTime.hour = hourOfDay;
                endTime.minute = minute;

                // Move to the start time if the end time is before the start
                // time.
                if (endTime.before(startTime)) {
                    endTime.set(startTime);
                }
                // Call populateTimezone if we support end time zone as well
            }

            endTime.set(0, endTime.minute, endTime.hour, endTime.monthDay,
                    endTime.month, endTime.year);
            endMillis = endTime.normalize(true);
            long duration = (endMillis - startMillis);
            long oneday = 24 * 60 * 60 * 1000;
            long startyu = startMillis % oneday;
            long endyu = endMillis % oneday;
            int eventDayNumber = (int) (duration / oneday);
            if (startyu < endyu) {
                eventDayNumber++;
            }
            eventDuration = eventDayNumber;

            if (mAllDayCheckBox.isChecked()) {
                setDate(mEndDateTimeButton, endMillis);
                setDate(mStartDateTimeButton, startMillis);
            } else {
                setDateTime(mEndDateTimeButton, endMillis);
                setDateTime(mStartDateTimeButton, startMillis);
            }
            // setTime(mEndTimeButton, endMillis);
            updateHomeTime();
        }
    }

    /**
     * private class TimeClickListener implements View.OnClickListener { private
     * Time mTime; public TimeClickListener(Time time) { mTime = time; }
     * 
     * @Override public void onClick(View v) { TimePickerDialog dialog = new
     *           TimePickerDialog(mActivity, new TimeListener(v), mTime.hour,
     *           mTime.minute, DateFormat.is24HourFormat(mActivity)); if (v ==
     *           mStartTimeButton) { mTimeSelectedWasStartTime = true;
     *           dialog.setTitle(R.string.edit_event_from_label); /** if
     *           (mStartTimePickerDialog == null) { mStartTimePickerDialog =
     *           TimePickerDialog.newInstance(new TimeListener(v), mTime.hour,
     *           mTime.minute, DateFormat.is24HourFormat(mActivity)); } else {
     *           mStartTimePickerDialog.setStartTime(mTime.hour, mTime.minute);
     *           } dialog = mStartTimePickerDialog;
     */
    /**
     * } else { mTimeSelectedWasStartTime = false;
     * dialog.setTitle(R.string.edit_event_to_label);
     */
    /**
     * if (mEndTimePickerDialog == null) { mEndTimePickerDialog =
     * TimePickerDialog.newInstance(new TimeListener(v), mTime.hour,
     * mTime.minute, DateFormat.is24HourFormat(mActivity)); } else {
     * mEndTimePickerDialog.setStartTime(mTime.hour, mTime.minute); } dialog =
     * mEndTimePickerDialog;
     */
    /**
     * } dialog.setButton(DialogInterface.BUTTON_POSITIVE,
     * mActivity.getText(android.R.string.ok), new
     * DialogInterface.OnClickListener() {
     * 
     * @Override public void onClick(DialogInterface arg0, int arg1) { // TODO
     *           Auto-generated method stub timeDateSetOK = true; } });
     *           dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
     *           mActivity.getText(android.R.string.cancel), new
     *           DialogInterface.OnClickListener() {
     * @Override public void onClick(DialogInterface arg0, int arg1) { // TODO
     *           Auto-generated method stub timeDateSetOK = false; } });
     *           dialog.setCanceledOnTouchOutside(true); dialog.show();
     */
    /**
     * final FragmentManager fm = mActivity.getFragmentManager();
     * fm.executePendingTransactions(); if (dialog != null && !dialog.isAdded())
     * { dialog.show(fm, FRAG_TAG_TIME_PICKER); }
     */
    /**
     * } }
     */

    private class DateListener implements OnDateSetListener {
        View mView;

        public DateListener(View view) {
            mView = view;
        }

        @Override
        public void onDateSet(DateTimePickerHCT view, int year, int month,
                int monthDay) {
            // Cache the member variables locally to avoid inner class overhead.
            if (!timeDateSetOK)
                return;
            Time startTime = mStartTime;
            Time endTime = mEndTime;

            // Cache the start and end millis so that we limit the number
            // of calls to normalize() and toMillis(), which are fairly
            // expensive.
            long startMillis;
            long endMillis;
            if (mView == mStartDateTimeButton) {
                // The start date was changed.
                if (startTime.year > Utils.YEAR_MAX) {
                    startTime.year = Utils.YEAR_MAX;
                } else {
                    if (startTime.year < Utils.YEAR_MIN) {
                        startTime.year = Utils.YEAR_MIN;
                    }
                }
                int yearDuration = endTime.year - startTime.year;
                int monthDuration = endTime.month - startTime.month;
                int monthDayDuration = endTime.monthDay - startTime.monthDay;

                startTime.year = year;
                if (startTime.year > Utils.YEAR_MAX) {
                    startTime.year = Utils.YEAR_MAX;
                } else {
                    if (startTime.year < Utils.YEAR_MIN) {
                        startTime.year = Utils.YEAR_MIN;
                    }
                }
                startTime.month = month;
                startTime.monthDay = monthDay;
                startMillis = startTime.normalize(true);

                // Also update the end date to keep the duration constant.
                endTime.year = year + yearDuration;
                if (endTime.year > Utils.YEAR_MAX) {
                    endTime.year = Utils.YEAR_MAX;
                } else {
                    if (endTime.year < Utils.YEAR_MIN) {
                        endTime.year = Utils.YEAR_MIN;
                    }
                }
                endTime.month = month + monthDuration;
                endTime.monthDay = monthDay + monthDayDuration;
                endMillis = endTime.normalize(true);

                // If the start date has changed then update the repeats.
                populateRepeats();

                // Update tz in case the start time switched from/to DLS
                populateTimezone(startMillis);

                // setDate(mStartDateButton, startMillis);
                // setDate(mEndDateButton, endMillis);
                // In case end time had to be
                // reset
                updateHomeTime();
            } else {
                // The end date was changed.
                startMillis = startTime.toMillis(true);
                endTime.year = year;
                if (endTime.year > Utils.YEAR_MAX) {
                    endTime.year = Utils.YEAR_MAX;
                } else {
                    if (endTime.year < Utils.YEAR_MIN) {
                        endTime.year = Utils.YEAR_MIN;
                    }
                }
                endTime.month = month;
                endTime.monthDay = monthDay;
                endMillis = endTime.normalize(true);

                // Do not allow an event to have an end time before the start
                // time.
                if (endTime.before(startTime)) {
                    endTime.set(startTime);
                    endMillis = startMillis;
                }
                // Call populateTimezone if we support end time zone as well
            }
            endTime.set(0, endTime.minute, endTime.hour, endTime.monthDay,
                    endTime.month, endTime.year);
            endMillis = endTime.normalize(true);
            long duration = (endMillis - startMillis);
            long oneday = 24 * 60 * 60 * 1000;
            long startyu = startMillis % oneday;
            long endyu = endMillis % oneday;
            int eventDayNumber = (int) (duration / oneday);
            if (startyu < endyu) {
                eventDayNumber++;
            }
            eventDuration = eventDayNumber;
            Log.v(TAG, "eventDuration,eventDayNumber=" + eventDuration + " , "
                    + eventDayNumber);

            setDateTime(mStartDateTimeButton, startMillis);
            setDateTime(mEndDateTimeButton, endMillis);
            // setTime(mEndTimeButton, endMillis); // In case end time had to be
            // reset
            updateHomeTime();
        }
    }

    // Fills in the date and time fields
    private void populateWhen() {
        long startMillis = mStartTime.toMillis(false /* use isDst */);
        long endMillis = mEndTime.toMillis(false /* use isDst */);
        setDateTime(mStartDateTimeButton, startMillis);
        setDateTime(mEndDateTimeButton, endMillis);

        /**
         * setTime(mStartTimeButton, startMillis); setTime(mEndTimeButton,
         * endMillis); mStartDateButton.setOnClickListener(new
         * DateClickListener_from( mStartTime));
         * mEndDateButton.setOnClickListener(new
         * DateClickListener_to(mEndTime));
         * mStartTimeButton.setOnClickListener(new
         * TimeClickListener(mStartTime)); mEndTimeButton.setOnClickListener(new
         * TimeClickListener(mEndTime));
         */
        mStartDateTimeButton.setOnClickListener(new DateTimeClickListener_from(
                mStartTime));
        mEndDateTimeButton.setOnClickListener(new DateTimeClickListener_to(
                mEndTime));
    }

    // Implements OnTimeZoneSetListener

    /**
     * @Override public void onTimeZoneSet(TimeZoneInfo tzi) {
     *           setTimezone(tzi.mTzId); updateHomeTime(); }
     */

    private void setTimezone(int i) {// String timeZone) {
        if (i < 0 || i >= mTimezoneAdapter.getCount()) {
            return; // do nothing
        }
        TimezoneRow timezone = mTimezoneAdapter.getItem(i);
        mTimezoneTextView.setText(timezone.toString());
        mTimezoneButton.setText(timezone.toString());
        mTimezone = timezone.mId;
        mStartTime.timezone = mTimezone;
        mStartTime.normalize(true);
        mEndTime.timezone = mTimezone;
        mEndTime.normalize(true);
        mTimezoneAdapter.setCurrentTimezone(mTimezone);
        /**
         * mTimezone = timeZone; mStartTime.timezone = mTimezone; long
         * timeMillis = mStartTime.normalize(true); mEndTime.timezone =
         * mTimezone; mEndTime.normalize(true); populateTimezone(timeMillis);
         */
    }

    private void populateTimezone(long eventStartTime) {
        if (mTzPickerUtils == null) {
            mTzPickerUtils = new TimeZonePickerUtils(mActivity);
        }
        CharSequence displayName = mTzPickerUtils.getGmtDisplayName(mActivity,
                mTimezone, eventStartTime, true);

        mTimezoneTextView.setText(displayName);
        mTimezoneButton.setText(displayName);
    }

    private void showTimezoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final Context alertDialogContext = builder.getContext();
        mTimezoneAdapter = new TimezoneAdapter(alertDialogContext, mTimezone);
        builder.setTitle(R.string.timezone_label);
        builder.setSingleChoiceItems(mTimezoneAdapter,
                mTimezoneAdapter.getRowById(mTimezone), this);
        builder.setPositiveButton(
                mActivity.getResources().getString(R.string.cancel), null);
        mTimezoneDialog = builder.create();

        LayoutInflater layoutInflater = (LayoutInflater) alertDialogContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final TextView timezoneFooterView = (TextView) layoutInflater.inflate(
                R.layout.timezone_footer, null);

        timezoneFooterView.setText(mActivity
                .getString(R.string.edit_event_show_all));
        timezoneFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimezoneDialog.getListView().removeFooterView(
                        timezoneFooterView);
                mTimezoneAdapter.showAllTimezones();
                final int row = mTimezoneAdapter.getRowById(mTimezone);
                // we need to post the selection changes to have them have
                // any effect
                mTimezoneDialog.getListView().post(new Runnable() {
                    @Override
                    public void run() {
                        mTimezoneDialog.getListView().setItemChecked(row, true);
                        mTimezoneDialog.getListView().setSelection(row);
                    }
                });
            }
        });
        mTimezoneDialog.getListView().addFooterView(timezoneFooterView);
        mTimezoneDialog.setCanceledOnTouchOutside(true);
        mTimezoneDialog.show();
    }

    private void populateRepeats() {
        Resources r = mActivity.getResources();
        Time time = mStartTime;

        String[] days = new String[] {
                DateUtils.getDayOfWeekString(Calendar.SUNDAY,
                        DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.MONDAY,
                        DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.TUESDAY,
                        DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.WEDNESDAY,
                        DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.THURSDAY,
                        DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.FRIDAY,
                        DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.SATURDAY,
                        DateUtils.LENGTH_MEDIUM) };
        String[] ordinals = r.getStringArray(R.array.ordinal_labels);
        boolean isCustomRecurrence = isCustomRecurrence();
        boolean isWeekdayEvent = isWeekdayEvent();

        ArrayList<String> repeatArray = new ArrayList<String>(0);
        ArrayList<Integer> recurrenceIndexes = new ArrayList<Integer>(0);

        repeatArray.add(r.getString(R.string.does_not_repeat));
        recurrenceIndexes.add(EditEventHelper.DOES_NOT_REPEAT);

        repeatArray.add(r.getString(R.string.daily));
        recurrenceIndexes.add(EditEventHelper.REPEATS_DAILY);

        if (isWeekdayEvent) {
            repeatArray.add(r.getString(R.string.every_weekday));
            recurrenceIndexes.add(EditEventHelper.REPEATS_EVERY_WEEKDAY);
        }

        String format = r.getString(R.string.weekly);
        repeatArray.add(String.format(format, time.format("%A")));
        recurrenceIndexes.add(EditEventHelper.REPEATS_WEEKLY_ON_DAY);

        int dayNumber = (time.monthDay - 1) / 7;
        format = r.getString(R.string.monthly_on_day_count);
        repeatArray.add(String.format(format, ordinals[dayNumber],
                days[time.weekDay]));
        recurrenceIndexes.add(EditEventHelper.REPEATS_MONTHLY_ON_DAY_COUNT);

        format = r.getString(R.string.monthly_on_day);
        repeatArray.add(String.format(format, time.monthDay));
        recurrenceIndexes.add(EditEventHelper.REPEATS_MONTHLY_ON_DAY);

        long when = time.toMillis(false);
        format = r.getString(R.string.yearly);
        int flags = 0;
        if (DateFormat.is24HourFormat(mActivity)) {
            flags |= DateUtils.FORMAT_24HOUR;
        }
        repeatArray.add(String.format(format,
                DateUtils.formatDateTime(mActivity, when, flags)));
        recurrenceIndexes.add(EditEventHelper.REPEATS_YEARLY);

        if (isCustomRecurrence) {
            repeatArray.add(r.getString(R.string.custom));
            recurrenceIndexes.add(EditEventHelper.REPEATS_CUSTOM);
        }
        mRecurrenceIndexes = recurrenceIndexes;
        LogUtils.e(repeatArray);
        int position = recurrenceIndexes
                .indexOf(EditEventHelper.DOES_NOT_REPEAT);
        if (mModel.mRrule != null && !TextUtils.isEmpty(mModel.mRrule)) {
            if (isCustomRecurrence) {
                position = recurrenceIndexes
                        .indexOf(EditEventHelper.REPEATS_CUSTOM);
            } else {
                switch (mEventRecurrence.freq) {
                case EventRecurrence.DAILY:
                    position = recurrenceIndexes
                            .indexOf(EditEventHelper.REPEATS_DAILY);
                    break;
                case EventRecurrence.WEEKLY:
                    if (mEventRecurrence.repeatsOnEveryWeekDay()) {
                        position = recurrenceIndexes
                                .indexOf(EditEventHelper.REPEATS_EVERY_WEEKDAY);
                    } else {
                        position = recurrenceIndexes
                                .indexOf(EditEventHelper.REPEATS_WEEKLY_ON_DAY);
                    }
                    break;
                case EventRecurrence.MONTHLY:
                    if (mEventRecurrence.repeatsMonthlyOnDayCount()) {
                        position = recurrenceIndexes
                                .indexOf(EditEventHelper.REPEATS_MONTHLY_ON_DAY_COUNT);
                    } else {
                        position = recurrenceIndexes
                                .indexOf(EditEventHelper.REPEATS_MONTHLY_ON_DAY);
                    }
                    break;
                case EventRecurrence.YEARLY:
                    position = recurrenceIndexes
                            .indexOf(EditEventHelper.REPEATS_YEARLY);
                    break;
                }
            }
        } else if (!TextUtils.isEmpty(mModel.mRdate)) {
            // rdate
            if (Utils.isEnableLunarRecurrence(mActivity)) {
                position = recurrenceIndexes
                        .indexOf(EditEventHelper.REPEATS_LUNAR);
            } else {
                position = recurrenceIndexes
                        .indexOf(EditEventHelper.REPEATS_YEARLY);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity,
                R.layout.simple_spinner_item, repeatArray);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);// simple_list_item_single_choice);
        try {
            mRepeatsSpinner.setAdapter(adapter);
        } catch (Exception e) {
            e.getMessage();
        }
        mRepeatsSpinner.setSelection(position);
        if (mModel.mOriginalSyncId != null) {
            mRepeatsSpinner.setEnabled(false);
        }
        /**
         * String repeatString; boolean enabled; if (!TextUtils.isEmpty(mRrule))
         * { repeatString = EventRecurrenceFormatter.getRepeatString(mActivity,
         * r, mEventRecurrence, true); if (repeatString == null) { repeatString
         * = r.getString(R.string.custom); Log.e(TAG,
         * "Can't generate display string for " + mRrule); enabled = false; }
         * else { // TODO Should give option to clear/reset rrule enabled =
         * RecurrencePickerDialog.canHandleRecurrenceRule(mEventRecurrence); if
         * (!enabled) { Log.e(TAG, "UI can't handle " + mRrule); } } } else {
         * repeatString = r.getString(R.string.does_not_repeat); enabled = true;
         * } mRruleButton.setText(repeatString); // Don't allow the user to make
         * exceptions recurring events. if (mModel.mOriginalSyncId != null) {
         * enabled = false; } mRruleButton.setOnClickListener(this);
         * mRruleButton.setEnabled(enabled);
         */
    }

    private class DateTimeClickListener_from implements View.OnClickListener {
        private Time mTime;

        public DateTimeClickListener_from(Time time) {
            mTime = time;
        }

        @Override
        public void onClick(View v) {
            if (!mView.hasWindowFocus()) { //
                /*
                 * Don't do anything if the activity if paused. Since Activity
                 * // doesn't // have a built in way to do this, we would have
                 * to implement // one ourselves and // either cast our Activity
                 * to a specialized activity base class // or implement some //
                 * generic interface that tells us if an activity is paused. //
                 * hasWindowFocus() is close enough if not quite perfect.
                 */

                return;
            }
            if (v == mStartDateTimeButton) {
                mDateSelectedWasStartDate = true;
            } else {
                mDateSelectedWasStartDate = false;
            }
            releaseDtpdInstall();
            mDtpdInstall = new DateTimePickerDialogHCT_from(mActivity,
                    new DateListener(v), new TimeListener(v), mTime.year,
                    mTime.month, mTime.monthDay, mTime.hour, mTime.minute,
                    DateFormat.is24HourFormat(mActivity));

            DateTimePickerHCT picker = mDtpdInstall.getDatePicker();
            picker.setMaxDate(MAXDATE * 1000);
            picker.setMinDate(MINDATE * 1000);

            final Calendar mInitCalendar = Calendar.getInstance();
            mInitCalendar.set(Calendar.YEAR, mTime.year);
            mInitCalendar.set(Calendar.MONTH, mTime.month);
            mInitCalendar.set(Calendar.DAY_OF_MONTH, mTime.monthDay);
            String intititle = DateUtils.formatDateTime(mActivity,
                    mInitCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_ABBREV_MONTH
                            | DateUtils.FORMAT_ABBREV_WEEKDAY);
            mDtpdInstall.setTitle(intititle);
            mDtpdInstall.setColor(mActivity.getResources().getColor(
                    R.color.cale_icon_color));
            mDtpdInstall.setButton(DialogInterface.BUTTON_POSITIVE,
                    mActivity.getText(android.R.string.ok),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            timeDateSetOK = true;
                        }
                    });
            mDtpdInstall.setButton(DialogInterface.BUTTON_NEGATIVE,
                    mActivity.getText(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            timeDateSetOK = false;
                        }
                    });
            timeDateSetOK = false;
            mDtpdInstall.setCanceledOnTouchOutside(true);
            mDtpdInstall.show();

        }
    }

    public class DateTimePickerDialogHCT_from extends DateTimePickerDialogHCT
            implements OnDateChangedListener, OnTimeChangedListener {
        private final Calendar mCalendar;

        public DateTimePickerDialogHCT_from(Context context,
                OnDateSetListener callBack, OnTimeSetListener callBackTime,
                int year, int monthOfYear, int dayOfMount, int hour,
                int minutes, boolean is24HourView) {
            super(context, callBack, callBackTime, year, monthOfYear,
                    dayOfMount, hour, minutes, is24HourView);
            mCalendar = Calendar.getInstance();
        }

        @Override
        public void onDateChanged(DateTimePickerHCT view, int year, int mount,
                int day) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, mount);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            final Context mContext = builder.getContext();
            String title = DateUtils.formatDateTime(mContext,
                    mCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_ABBREV_MONTH
                            | DateUtils.FORMAT_ABBREV_WEEKDAY);
            setTitle(title);
            super.onDateChanged(view, year, mount, day);
        }
    }

    private boolean isCustomRecurrence() {
        if (mEventRecurrence.until != null
                || (mEventRecurrence.interval != 0 && mEventRecurrence.interval != 1)
                || mEventRecurrence.count != 0) {
            return true;
        }

        if (mEventRecurrence.freq == 0) {
            return false;
        }

        switch (mEventRecurrence.freq) {
        case EventRecurrence.DAILY:
            return false;
        case EventRecurrence.WEEKLY:
            if (mEventRecurrence.repeatsOnEveryWeekDay() && isWeekdayEvent()) {
                return false;
            } else if (mEventRecurrence.bydayCount == 1) {
                return false;
            }
            break;
        case EventRecurrence.MONTHLY:
            if (mEventRecurrence.repeatsMonthlyOnDayCount()) {
                return false;
            } else if (mEventRecurrence.bydayCount == 0
                    && mEventRecurrence.bymonthdayCount == 1
                    && mEventRecurrence.bymonthday[0] > 0) {
                return false;
            }
            break;
        case EventRecurrence.YEARLY:
            return false;
        }
        return true;
    }

    private boolean isWeekdayEvent() {
        if (mStartTime.weekDay != Time.SUNDAY
                && mStartTime.weekDay != Time.SATURDAY) {
            return true;
        }
        return false;
    }

    private class DateTimeClickListener_to implements View.OnClickListener {
        private Time mTime;

        public DateTimeClickListener_to(Time time) {
            mTime = time;
        }

        @Override
        public void onClick(View v) {
            if (!mView.hasWindowFocus()) {
                // Don't do anything if the activity if paused. Since Activity
                // doesn't
                // have a built in way to do this, we would have to implement
                // one ourselves and
                // either cast our Activity to a specialized activity base class
                // or implement some
                // generic interface that tells us if an activity is paused.
                // hasWindowFocus() is
                // close enough if not quite perfect.
                return;
            }
            if (v == mStartDateTimeButton) {
                mDateSelectedWasStartDate = true;
            } else {
                mDateSelectedWasStartDate = false;
            }
            releaseDtpdInstall();
            mDtpdInstall = new DateTimePickerDialogHCT_from(mActivity,
                    new DateListener(v), new TimeListener(v), mTime.year,
                    mTime.month, mTime.monthDay, mTime.hour, mTime.minute,
                    DateFormat.is24HourFormat(mActivity));

            DateTimePickerHCT picker = mDtpdInstall.getDatePicker();
            picker.setMaxDate(MAXDATE * 1000);
            picker.setMinDate(MINDATE * 1000);
            mDtpdInstall.setColor(mActivity.getResources().getColor(
                    R.color.cale_icon_color));
            final Calendar mInitCalendar = Calendar.getInstance();
            mInitCalendar.set(Calendar.YEAR, mTime.year);
            mInitCalendar.set(Calendar.MONTH, mTime.month);
            mInitCalendar.set(Calendar.DAY_OF_MONTH, mTime.monthDay);
            String intititle = DateUtils.formatDateTime(mActivity,
                    mInitCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_ABBREV_MONTH
                            | DateUtils.FORMAT_ABBREV_WEEKDAY);
            mDtpdInstall.setTitle(intititle);

            mDtpdInstall.setButton(DialogInterface.BUTTON_POSITIVE,
                    mActivity.getText(android.R.string.ok),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            timeDateSetOK = true;
                        }
                    });
            mDtpdInstall.setButton(DialogInterface.BUTTON_NEGATIVE,
                    mActivity.getText(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            timeDateSetOK = false;
                        }
                    });

            timeDateSetOK = false;
            mDtpdInstall.setCanceledOnTouchOutside(true);
            mDtpdInstall.show();
        }
    }

    /**
     * public class SetTitleDatePickerDialog_from extends DatePickerDialog
     * implements OnDateChangedListener { private final Calendar mCalendar;
     * public SetTitleDatePickerDialog_from(Context context, OnDateSetListener
     * callBack, int year, int monthOfYear, int dayOfMount) { super(context,
     * callBack, year, monthOfYear, dayOfMount); mCalendar =
     * Calendar.getInstance(); }
     * 
     * @Override public void onDateChanged(DatePicker view, int year, int mount,
     *           int day) { mCalendar.set(Calendar.YEAR, year);
     *           mCalendar.set(Calendar.MONTH, mount);
     *           mCalendar.set(Calendar.DAY_OF_MONTH, day); AlertDialog.Builder
     *           builder = new AlertDialog.Builder(mActivity); final Context
     *           mContext = builder.getContext(); String title =
     *           DateUtils.formatDateTime(mContext, mCalendar.getTimeInMillis(),
     *           DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY |
     *           DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH |
     *           DateUtils.FORMAT_ABBREV_WEEKDAY);
     *           setTitle(mContext.getResources().getString(
     *           R.string.edit_event_from_label) + ":" + title); } } public
     *           class SetTitleDatePickerDialog_to extends DatePickerDialog
     *           implements OnDateChangedListener { private final Calendar
     *           mCalendar; public SetTitleDatePickerDialog_to(Context context,
     *           OnDateSetListener callBack, int year, int monthOfYear, int
     *           dayOfMount) { super(context, callBack, year, monthOfYear,
     *           dayOfMount); mCalendar = Calendar.getInstance(); }
     * @Override public void onDateChanged(DatePicker view, int year, int mount,
     *           int day) { mCalendar.set(Calendar.YEAR, year);
     *           mCalendar.set(Calendar.MONTH, mount);
     *           mCalendar.set(Calendar.DAY_OF_MONTH, day); AlertDialog.Builder
     *           builder = new AlertDialog.Builder(mActivity); final Context
     *           mContext = builder.getContext(); String title =
     *           DateUtils.formatDateTime(mContext, mCalendar.getTimeInMillis(),
     *           DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY |
     *           DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH |
     *           DateUtils.FORMAT_ABBREV_WEEKDAY);
     *           setTitle(mContext.getResources().getString(
     *           R.string.edit_event_to_label) + ":" + title); } }
     */

    public static class CalendarsAdapter extends ResourceCursorAdapter {
        public CalendarsAdapter(Context context, int resourceId, Cursor c) {
            super(context, resourceId, c);
            setDropDownViewResource(R.layout.calendars_dropdown_item);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            ImageView icon = (ImageView) view.findViewById(R.id.calendar_icon);
            int colorColumn = cursor
                    .getColumnIndexOrThrow(Calendars.CALENDAR_COLOR);

            int ownerColumn = cursor
                    .getColumnIndexOrThrow(Calendars.CALENDAR_DISPLAY_NAME);
            int nameColumn = cursor
                    .getColumnIndexOrThrow(Calendars.ACCOUNT_NAME);

            Drawable iconimage = (Drawable) context.getResources().getDrawable(
                    R.drawable.widget_agenda_item_color);
            iconimage.setTint(Utils.getDisplayColorFromColor(cursor
                    .getInt(colorColumn)));
            if (icon != null) {
                icon.setImageDrawable(iconimage);
            }

            TextView name = (TextView) view.findViewById(R.id.calendar_name);
            TextView name2 = (TextView) view.findViewById(R.id.account_name);
            if (name != null) {

                String calendarName = cursor.getString(ownerColumn);
                name.setText(calendarName);
                String accountName = cursor.getString(nameColumn);
                name2.setText(accountName);
                // HCT_MODIFY lixiange MF3.0 add My Calendar
                String mycalendar = context.getString(R.string.my_calendar);
                if (MY_CALNEDAR.equals(accountName)) {
                    name.setText(mycalendar);
                }
                // HCT_MODIFY lixiange MF3.0 add My Calendar
            }
        }
    }

    /**
     * Does prep steps for saving a calendar event. This triggers a parse of the
     * attendees list and checks if the event is ready to be saved. An event is
     * ready to be saved so long as a model exists and has a calendar it can be
     * associated with, either because it's an existing event or we've finished
     * querying.
     * 
     * @return false if there is no model or no calendar had been loaded yet,
     *         true otherwise.
     */
    public boolean prepareForSave() {
        if (mModel == null || (mCalendarsCursor == null && mModel.mUri == null)) {
            return false;
        }
        return fillModelFromUI();
    }

    public boolean fillModelFromReadOnlyUi() {
        if (mModel == null || (mCalendarsCursor == null && mModel.mUri == null)) {
            return false;
        }
        mModel.mReminders = EventViewUtils.reminderItemsToReminders(
                mReminderItems, mReminderMinuteValues, mReminderMethodValues);
        mModel.mReminders.addAll(mUnsupportedReminders);
        mModel.normalizeReminders();
        int status = EventInfoFragment
                .getResponseFromButtonId(mResponseRadioGroup
                        .getCheckedRadioButtonId());
        if (status != Attendees.ATTENDEE_STATUS_NONE) {
            mModel.mSelfAttendeeStatus = status;
        }
        return true;
    }

    // This is called if the user clicks on one of the buttons: "Save",
    // "Discard", or "Delete". This is also called if the user clicks
    // on the "remove reminder" button.
    @Override
    public void onClick(View view) {
        if (view == mRepeatsSpinner) {
            Bundle b = new Bundle();
            b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS,
                    mStartTime.toMillis(false));
            b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE,
                    mStartTime.timezone);

            // TODO may be more efficient to serialize and pass in
            // EventRecurrence
            b.putString(RecurrencePickerDialog.BUNDLE_RRULE, mRrule);

            FragmentManager fm = mActivity.getFragmentManager();
            RecurrencePickerDialog rpd = (RecurrencePickerDialog) fm
                    .findFragmentByTag(FRAG_TAG_RECUR_PICKER);
            if (rpd != null) {
                rpd.dismiss();
            }
            rpd = new RecurrencePickerDialog();
            rpd.setArguments(b);
            rpd.setOnRecurrenceSetListener(EditEventView.this);
            rpd.show(fm, FRAG_TAG_RECUR_PICKER);
            return;
        }

        // This must be a click on one of the "remove reminder" buttons
        LinearLayout reminderItem = (LinearLayout) view.getParent();
        LinearLayout parent = (LinearLayout) reminderItem.getParent();
        parent.removeView(reminderItem);
        mReminderItems.remove(reminderItem);
        updateRemindersVisibility(mReminderItems.size());
        EventViewUtils.updateAddReminderButton(mView, mReminderItems,
                mReminderMinuteValues.size());
    }

    @Override
    public void onRecurrenceSet(String rrule) {
        mRrule = rrule;
        if (mRrule != null) {
            mEventRecurrence.parse(mRrule);
        }
        populateRepeats();
    }

    // This is called if the user cancels the "No calendars" dialog.
    // The "No calendars" dialog is shown if there are no syncable calendars.
    @Override
    public void onCancel(DialogInterface dialog) {
        if (dialog == mLoadingCalendarsDialog) {
            mLoadingCalendarsDialog = null;
            mSaveAfterQueryComplete = false;
        } else if (dialog == mNoCalendarsDialog) {
            mDone.setDoneCode(Utils.DONE_REVERT);
            mDone.run();
            return;
        }
    }

    // This is called if the user clicks on a dialog button.
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog == mNoCalendarsDialog) {
            mDone.setDoneCode(Utils.DONE_REVERT);
            mDone.run();
            if (which == DialogInterface.BUTTON_POSITIVE) {
                Intent nextIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                final String[] array = { "com.android.calendar" };
                nextIntent.putExtra(Settings.EXTRA_AUTHORITIES, array);
                nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(nextIntent);
            }
        } else if (dialog == mTimezoneDialog) {
            if (which >= 0 && which < mTimezoneAdapter.getCount()) {
                setTimezone(which);
                updateHomeTime();
                dialog.dismiss();
            }
        } else if (dialog == viewContactDialog) {
            pickAttendeesFromContacts();
        }
    }

    // Goes through the UI elements and updates the model as necessary
    private boolean fillModelFromUI() {
        if (mModel == null) {
            return false;
        }
        if (mAllDayCheckBox.isChecked()) {
            mModel.mReminders = EventViewUtils.reminderItemsToReminders(
                    mReminderItems, mAlldayReminderMinuteValues,
                    mReminderMethodValues);
        } else {
            mModel.mReminders = EventViewUtils.reminderItemsToReminders(
                    mReminderItems, mReminderMinuteValues,
                    mReminderMethodValues);
        }
        mModel.mReminders.addAll(mUnsupportedReminders);
        mModel.normalizeReminders();
        mModel.mHasAlarm = mReminderItems.size() > 0;
        mModel.mTitle = mTitleTextView.getText().toString();
        mModel.mAllDay = mAllDayCheckBox.isChecked();
        mModel.mLocation = mLocationTextView.getText().toString();
        mModel.mDescription = mDescriptionTextView.getText().toString();
        if (TextUtils.isEmpty(mModel.mLocation)) {
            mModel.mLocation = null;
        }
        if (TextUtils.isEmpty(mModel.mDescription)) {
            mModel.mDescription = null;
        }

        /* add by hct.gengbin for lbs, begin */
        if (mEventType == EventItem.VIEW_EVENT_LOCATION) {
            mModel.mEventType = mEventType;
            mModel.mActionData = String.valueOf(mLocationRemindRadius);
        } else {
            mModel.mActionData = null;
        }
        mModel.mActionData1 = mMapParam;
        /* add by hct.gengbin for lbs, end */

        int status = EventInfoFragment
                .getResponseFromButtonId(mResponseRadioGroup
                        .getCheckedRadioButtonId());
        if (status != Attendees.ATTENDEE_STATUS_NONE) {
            mModel.mSelfAttendeeStatus = status;
        }

        if (mAttendeesList != null) {
            // mEmailValidator.setRemoveInvalid(true);
            // mAttendeesList.performValidation();
            mModel.mAttendeesList.clear();
            mModel.addAttendees(mAttendeesList.getText().toString(),
                    mEmailValidator);
            // mEmailValidator.setRemoveInvalid(false);
        }

        // If this was a new event we need to fill in the Calendar information
        if (mModel.mUri == null) {
            mModel.mCalendarId = mCalendarsSpinner.getSelectedItemId();
            int calendarCursorPosition = mCalendarsSpinner
                    .getSelectedItemPosition();
            if (mCalendarsCursor.moveToPosition(calendarCursorPosition)) {
                String defaultCalendar = mCalendarsCursor
                        .getString(EditEventHelper.CALENDARS_INDEX_OWNER_ACCOUNT);
                Utils.setSharedPreference(mActivity,
                        GeneralPreferences.KEY_DEFAULT_CALENDAR,
                        defaultCalendar);
                mModel.mOwnerAccount = defaultCalendar;
                mModel.mOrganizer = defaultCalendar;
                mModel.mCalendarId = mCalendarsCursor
                        .getLong(EditEventHelper.CALENDARS_INDEX_ID);
            }
        }

        if (mModel.mAllDay) {
            // Reset start and end time, increment the monthDay by 1, and set
            // the timezone to UTC, as required for all-day events.
            mTimezone = Time.TIMEZONE_UTC;
            mStartTime.hour = 0;
            mStartTime.minute = 0;
            mStartTime.second = 0;
            mStartTime.timezone = mTimezone;
            mModel.mStart = mStartTime.normalize(true);

            mEndTime.hour = 0;
            mEndTime.minute = 0;
            mEndTime.second = 0;
            mEndTime.timezone = mTimezone;
            // When a user see the event duration as "X - Y" (e.g. Oct. 28 -
            // Oct. 29), end time
            // should be Y + 1 (Oct.30).
            final long normalizedEndTimeMillis = mEndTime.normalize(true)
                    + DateUtils.DAY_IN_MILLIS;
            if (normalizedEndTimeMillis < mModel.mStart) {
                // mEnd should be midnight of the next day of mStart.
                mModel.mEnd = mModel.mStart + DateUtils.DAY_IN_MILLIS;
            } else {
                mModel.mEnd = normalizedEndTimeMillis;
            }
        } else {
            mStartTime.timezone = mTimezone;
            mEndTime.timezone = mTimezone;
            mStartTime.second = 0;
            mModel.mStart = mStartTime.toMillis(true);
            mModel.mEnd = mEndTime.toMillis(true);
        }
        mModel.mTimezone = mTimezone;
        mModel.mAccessLevel = mAccessLevelSpinner.getSelectedItemPosition();
        // TODO set correct availability value
        mModel.mAvailability = mAvailabilityValues.get(mAvailabilitySpinner
                .getSelectedItemPosition());

        // HCT_MODIFY,lixiange MF3.0 new event
        if (canEditEventColor()) {
            mModel.setEventColor(mEventColor);
        } else {
            mModel.setEventColor(mModel.getCalendarColor());
        }
        // HCT_MODIFY,lixiange MF3.0 new event

        // rrrule
        // If we're making an exception we don't want it to be a repeating
        // event.
        int selection;
        if (mModification == EditEventHelper.MODIFY_SELECTED) {
            // mModel.mRrule = null;
            selection = EditEventHelper.DOES_NOT_REPEAT;
        } else {
            // mModel.mRrule = mRrule;
            int position = mRepeatsSpinner.getSelectedItemPosition();
            selection = mRecurrenceIndexes.get(position);
        }

        EditEventHelper.updateRecurrenceRuleAndRdate(mActivity, selection,
                mModel, Utils.getFirstDayOfWeek(mActivity) + 1);

        mModel.mRepeatFrequence = EditEventHelper.frequence;

        if (!mModel.mAllDay) {
            mTimezoneAdapter.saveRecentTimezone(mTimezone);
        }

        return true;
    }

    public EditEventView(final Activity activity, View view,
            EditDoneRunnable done, boolean timeSelectedWasStartTime,
            boolean dateSelectedWasStartDate, int eventColor) {
        mActivity = activity;
        mView = view;
        mDone = done;
        final Resources resources = activity.getResources();
        // cache top level view elements
        mLoadingMessage = (TextView) view.findViewById(R.id.loading_message);
        mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);

        // cache all the widgets
        mCalendarsSpinner = (Spinner) view.findViewById(R.id.calendars_spinner);
        mTitleTextView = (EditText) view.findViewById(R.id.title);
        mLocationTextView = (AutoCompleteTextView) view
                .findViewById(R.id.location);
        mDescriptionTextView = (TextView) view.findViewById(R.id.description);
        mTimezoneLabel = (TextView) view.findViewById(R.id.timezone_label);
        // mStartDateButton = (Button) view.findViewById(R.id.start_date);
        // mEndDateButton = (Button) view.findViewById(R.id.end_date);
        mStartDateTimeButton = (Button) view.findViewById(R.id.start_date_time);
        mEndDateTimeButton = (Button) view.findViewById(R.id.end_date_time);
        mWhenView = (TextView) mView.findViewById(R.id.when);
        mTimezoneTextView = (TextView) mView
                .findViewById(R.id.timezone_textView);
        // mStartTimeButton = (Button) view.findViewById(R.id.start_time);
        // mEndTimeButton = (Button) view.findViewById(R.id.end_time);
        mTimezoneButton = (Button) view.findViewById(R.id.timezone_button);

        // HCT_MODIFY,lixiange MF3.0 new event
        mMoreButton = (Button) view.findViewById(R.id.more_button);
        mEditEvent2 = (LinearLayout) view.findViewById(R.id.attendees_group);
        mMoreLayout = (LinearLayout) view.findViewById(R.id.more);

        mEditEventColor = (LinearLayout) view
                .findViewById(R.id.edit_event_color);
        mShowMeAs = (TextView) view.findViewById(R.id.presence_label);
        mPrivacy = (TextView) view.findViewById(R.id.privacy_label);
        mChangeColorRed = (ImageButton) view
                .findViewById(R.id.change_color_red);
        mChangeColorOrange = (ImageButton) view
                .findViewById(R.id.change_color_orange);
        mChangeColorYellow = (ImageButton) view
                .findViewById(R.id.change_color_yellow);
        mChangeColorGreen = (ImageButton) view
                .findViewById(R.id.change_color_green);
        mChangeColorDim = (ImageButton) view
                .findViewById(R.id.change_color_dim);
        mChangeColorBlue = (ImageButton) view
                .findViewById(R.id.change_color_blue);
        mChangeColorPurple = (ImageButton) view
                .findViewById(R.id.change_color_purple);
        mEventRed = resources.getColor(R.color.event_color_red);
        mEventOrange = resources.getColor(R.color.event_color_orange);
        mEventYellow = resources.getColor(R.color.event_color_yellow);
        mEventGreen = resources.getColor(R.color.event_color_green);
        mEventDim = resources.getColor(R.color.event_color_dim);
        mEventBlue = resources.getColor(R.color.event_color_blue);
        mEventPurple = resources.getColor(R.color.event_color_purple);

        int red_color_int = resources.getInteger(R.integer.red_color_int);
        int orange_color_int = resources.getInteger(R.integer.orange_color_int);
        int yellow_color_int = resources.getInteger(R.integer.yellow_color_int);
        int green_color_int = resources.getInteger(R.integer.green_color_int);
        int dim_color_int = resources.getInteger(R.integer.dim_color_int);
        int blue_color_int = resources.getInteger(R.integer.blue_color_int);
        int purple_color_int = resources.getInteger(R.integer.purple_color_int);
        final Drawable d_color = resources.getDrawable(R.drawable.icon_choosen);

        if (eventColor != -1) {
            mEventColor = eventColor;
            if (red_color_int == mEventColor) {
                mChangeColorRed.setImageDrawable(d_color);
            } else if (orange_color_int == mEventColor) {
                mChangeColorOrange.setImageDrawable(d_color);
            } else if (yellow_color_int == mEventColor) {
                mChangeColorYellow.setImageDrawable(d_color);
            } else if (green_color_int == mEventColor) {
                mChangeColorGreen.setImageDrawable(d_color);
            } else if (dim_color_int == mEventColor) {
                mChangeColorDim.setImageDrawable(d_color);
            } else if (blue_color_int == mEventColor) {
                mChangeColorBlue.setImageDrawable(d_color);
            } else if (purple_color_int == mEventColor) {
                mChangeColorPurple.setImageDrawable(d_color);
            } else {
                mChangeColorBlue.setImageDrawable(d_color);
            }
        } else {
            Log.d(TAG, "lxg EditEventView eventColor = " + eventColor);
            mEventColor = resources.getColor(R.color.event_color_blue);
            mChangeColorBlue.setImageDrawable(d_color);
        }
        mChangeColorRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeColorRed.setImageDrawable(d_color);
                mChangeColorOrange.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_orange_icon));
                mChangeColorYellow.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_yellow_icon));
                mChangeColorGreen.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_green_icon));
                mChangeColorDim.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_dim_icon));
                mChangeColorBlue.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_blue_icon));
                mChangeColorPurple.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_purple_icon));
                mEventColor = mEventRed;
            }
        });
        mChangeColorOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeColorRed.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_red_icon));
                mChangeColorOrange.setImageDrawable(d_color);
                mChangeColorYellow.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_yellow_icon));
                mChangeColorGreen.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_green_icon));
                mChangeColorDim.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_dim_icon));
                mChangeColorBlue.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_blue_icon));
                mChangeColorPurple.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_purple_icon));
                mEventColor = mEventOrange;
            }
        });
        mChangeColorYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeColorRed.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_red_icon));
                mChangeColorOrange.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_orange_icon));
                mChangeColorYellow.setImageDrawable(d_color);
                mChangeColorGreen.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_green_icon));
                mChangeColorDim.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_dim_icon));
                mChangeColorBlue.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_blue_icon));
                mChangeColorPurple.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_purple_icon));
                mEventColor = mEventYellow;
            }
        });
        mChangeColorGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeColorRed.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_red_icon));
                mChangeColorOrange.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_orange_icon));
                mChangeColorYellow.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_yellow_icon));
                mChangeColorGreen.setImageDrawable(d_color);
                mChangeColorDim.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_dim_icon));
                mChangeColorBlue.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_blue_icon));
                mChangeColorPurple.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_purple_icon));
                mEventColor = mEventGreen;
            }
        });
        mChangeColorDim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeColorRed.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_red_icon));
                mChangeColorOrange.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_orange_icon));
                mChangeColorYellow.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_yellow_icon));
                mChangeColorGreen.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_green_icon));
                mChangeColorDim.setImageDrawable(d_color);
                mChangeColorBlue.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_blue_icon));
                mChangeColorPurple.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_purple_icon));
                mEventColor = mEventDim;
            }
        });
        mChangeColorBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeColorRed.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_red_icon));
                mChangeColorOrange.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_orange_icon));
                mChangeColorYellow.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_yellow_icon));
                mChangeColorGreen.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_green_icon));
                mChangeColorDim.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_dim_icon));
                mChangeColorBlue.setImageDrawable(d_color);
                mChangeColorPurple.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_purple_icon));
                mEventColor = mEventBlue;
            }
        });
        mChangeColorPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChangeColorRed.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_red_icon));
                mChangeColorOrange.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_orange_icon));
                mChangeColorYellow.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_yellow_icon));
                mChangeColorGreen.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_green_icon));
                mChangeColorDim.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_dim_icon));
                mChangeColorBlue.setImageDrawable(resources
                        .getDrawable(R.drawable.change_color_blue_icon));
                mChangeColorPurple.setImageDrawable(d_color);
                mEventColor = mEventPurple;
            }
        });

        mAddContactButton = (ImageButton) view.findViewById(R.id.add_contact);
        mAddContactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            mActivity);
                    builder.setTitle(R.string.view_contact)
                            // .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setMessage(R.string.view_contact_message)
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton(android.R.string.ok,
                                    EditEventView.this);
                    viewContactDialog = builder.show();
                } else {
                    mHasContactsGrant = false;
                    mHasCallLogGrant = false;
                    if (Utils.checkAndRequestPermission(mActivity,
                            Manifest.permission.READ_CONTACTS,
                            Utils.REQUEST_PERMISSIONS_CONTACTS_ADD)) {
                        mHasContactsGrant = true;
                        pickAttendeesFromContacts();
                    }
                    if (Utils.checkAndRequestPermission(mActivity,
                            Manifest.permission.READ_CALL_LOG,
                            Utils.REQUEST_PERMISSIONS_CALLLOG)) {
                        mHasCallLogGrant = true;
                        pickAttendeesFromContacts();
                    }
                }
            }
        });
        // HCT_MODIFY,lixiange MF3.0 new event
        mTimezoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimezoneDialog();
            }
        });
        mFromRow = view.findViewById(R.id.from_row);
        mToRow = view.findViewById(R.id.to_row);
        mTimezoneRow = view.findViewById(R.id.timezone_button_row);
        mStartTimeHome = (TextView) view.findViewById(R.id.start_time_home_tz);
        mStartDateHome = (TextView) view.findViewById(R.id.start_date_home_tz);
        mEndTimeHome = (TextView) view.findViewById(R.id.end_time_home_tz);
        mEndDateHome = (TextView) view.findViewById(R.id.end_date_home_tz);
        mAllDayCheckBox = (CheckBoxHCT) view.findViewById(R.id.is_all_day);
        mRepeatsSpinner = (Spinner) view.findViewById(R.id.rrule);
        mAvailabilitySpinner = (Spinner) view.findViewById(R.id.availability);
        mAccessLevelSpinner = (Spinner) view.findViewById(R.id.visibility);
        mCalendarSelectorGroup = view
                .findViewById(R.id.calendar_selector_group);
        mCalendarSelectorWrapper = view
                .findViewById(R.id.calendar_selector_wrapper);
        mCalendarStaticGroup = view.findViewById(R.id.calendar_group);
        mRemindersGroup = view.findViewById(R.id.reminders_row);
        mRepeatsGroup = view.findViewById(R.id.repeats_row);
        mCalendarGroup = view.findViewById(R.id.calendar_selector_group);
        mTimeZoneGroup = view.findViewById(R.id.timezone_button_row);
        mLocationRemindGroup = view.findViewById(R.id.location_remind_row);
        mReminderLabel = (TextView) view
                .findViewById(R.id.reminders_group_label);

        mResponseGroup = view.findViewById(R.id.response_row);
        mOrganizerGroup = view.findViewById(R.id.organizer_row);
        mAttendeesGroup = view.findViewById(R.id.add_attendees_row);
        mLocationGroup = view.findViewById(R.id.where_row);
        mDescriptionGroup = view.findViewById(R.id.description_row);
        mStartHomeGroup = view.findViewById(R.id.from_row_home_tz);
        mEndHomeGroup = view.findViewById(R.id.to_row_home_tz);
        mAttendeesList = (RecipientsEditor) view.findViewById(R.id.attendees);

        mColorPickerNewEvent = view.findViewById(R.id.change_color_new_event);
        mColorPickerExistingEvent = view
                .findViewById(R.id.change_color_existing_event);

        // final View whenRowView = view.findViewById(R.id.when_row);
        final View allDayView = view.findViewById(R.id.all_day_row);

        // add by hct.gengbin, begin
        mLocationReminderCB = (CheckBoxHCT) view
                .findViewById(R.id.remind_enabled);
        mLocationReminderTI = (TextView) view.findViewById(R.id.remind_title);
        mLocationReminderCB.SetColor(resources
                .getColor(R.color.cale_checkbox_color));
        mReminderRangeSB = (SeekBar) view.findViewById(R.id.remind_range);
        mReminderRangeSB.setMax(1000);
        mReminderRangeSB
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar arg0) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar arg0) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onProgressChanged(SeekBar arg0, int arg1,
                            boolean arg2) {
                        mLocationRemindRadius = arg1;
                    }
                });
        mLocationReminderCB
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        setVisibilityForLocationRemind(isChecked);
                        mEventType = isChecked ? 1 : 0;
                    }
                });
        // add by hct.gengbin, end

        mTitleTextView.setTag(mTitleTextView.getBackground());
        mLocationTextView.setTag(mLocationTextView.getBackground());
        // mLocationAdapter = new EventLocationAdapter(activity);
        // mLocationTextView.setAdapter(mLocationAdapter);
        mLocationTextView
                .setOnEditorActionListener(new OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                            KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            // Dismiss the suggestions dropdown. Return false so
                            // the other
                            // side effects still occur (soft keyboard going
                            // away, etc.).
                            mLocationTextView.dismissDropDown();
                        }
                        return false;
                    }
                });

        /** HCT_MODIFY start add LBS lixiange MF3.0 *********************************************/
        MAP_DIR = mActivity.getFilesDir().getPath() + "/" + "calendarmap";
        Log.d(TAG, "MAP_DIR =" + MAP_DIR);
        mMap = (ImageView) view.findViewById(R.id.map);
        mDeleteMapButton = (ImageButton) view.findViewById(R.id.delete_map);
        mMapContainer = (LinearLayout) view.findViewById(R.id.map_row);
        mAddLocationButton = (ImageView) view.findViewById(R.id.add_location);

        if (Utils.isAbroadBranch(mActivity)
                || (!LbsServiceHelper.isLbsAppAvilible(mActivity))) {
            mAddLocationButton.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = mLocationTextView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mLocationTextView.setLayoutParams(params);
        }
        mAddLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mActivity != null) {
                    hideInputAndclearFoucs();
                    if (LbsServiceHelper.isLbsAppAvilible(mActivity)) {
                        LbsServiceHelper.startHctLBSAPPForLocation(mActivity,
                                mLocationTextView.getText().toString(),
                                mMapParam);
                    } else {
                        Log.w(TAG, "No avilible LBS App for location!");
                    }
                }
            }
        });
        mMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mActivity != null
                        && LbsServiceHelper.isLbsAppAvilible(mActivity)) {
                    LbsServiceHelper.startHctLBSAPPForView(mActivity,
                            mLocationTextView.getText().toString(), mMapParam);
                } else {
                    Log.w(TAG, "No avilible LBS App for view!");
                }
            }
        });
        mDeleteMapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMapContainer.setVisibility(View.GONE);
                String locationMap = EditEventView.MAP_DIR + "/"
                        + mLocationTextString + ".jpg";
                if (locationMap == null) {
                    Log.e(TAG, "what,locationMap is null");
                    return;
                }
                File targetFile = new File(locationMap);
                if (targetFile.exists()) {
                    targetFile.delete();
                }
            }
        });
        /** HCT_MODIFY start add LBS lixiange MF3.0 *********************************************/

        mAvailabilityExplicitlySet = false;
        mAllDayChangingAvailability = false;
        mAvailabilityCurrentlySelected = -1;
        mAvailabilitySpinner
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                            View view, int position, long id) {
                        if (mAvailabilityCurrentlySelected == -1) {
                            mAvailabilityCurrentlySelected = position;
                        }

                        if (mAvailabilityCurrentlySelected != position
                                && !mAllDayChangingAvailability) {
                            mAvailabilityExplicitlySet = true;
                        } else {
                            mAvailabilityCurrentlySelected = position;
                            mAllDayChangingAvailability = false;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
        Point realSize = new Point();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getRealSize(realSize);
        final int height = realSize.y;
        mHandler = new Handler();
        mDescriptionTextView.setTag(mDescriptionTextView.getBackground());
        mAttendeesList.setTag(mAttendeesList.getBackground());
        mOriginalPadding[0] = mLocationTextView.getPaddingLeft();
        mOriginalPadding[1] = mLocationTextView.getPaddingTop();
        mOriginalPadding[2] = mLocationTextView.getPaddingRight();
        mOriginalPadding[3] = mLocationTextView.getPaddingBottom();
        mOriginalSpinnerPadding[0] = mRepeatsSpinner.getPaddingLeft();
        mOriginalSpinnerPadding[1] = mRepeatsSpinner.getPaddingTop();
        mOriginalSpinnerPadding[2] = mRepeatsSpinner.getPaddingRight();
        mOriginalSpinnerPadding[3] = mRepeatsSpinner.getPaddingBottom();
        mEditViewList.add(mTitleTextView);
        mEditViewList.add(mLocationTextView);
        mEditViewList.add(mDescriptionTextView);
        mEditViewList.add(mAttendeesList);

        mViewOnlyList.add(view.findViewById(R.id.when_row));
        mViewOnlyList.add(view.findViewById(R.id.timezone_textview_row));

        mEditOnlyList.add(view.findViewById(R.id.all_day_row));
        // mEditOnlyList.add(view.findViewById(R.id.availability_row));
        // mEditOnlyList.add(view.findViewById(R.id.visibility_row));
        mEditOnlyList.add(view.findViewById(R.id.from_row));
        mEditOnlyList.add(view.findViewById(R.id.to_row));
        mEditOnlyList.add(mTimezoneRow);
        mEditOnlyList.add(mStartHomeGroup);
        mEditOnlyList.add(mEndHomeGroup);

        mResponseRadioGroup = (RadioGroup) view
                .findViewById(R.id.response_value);
        mRemindersContainer = (LinearLayout) view
                .findViewById(R.id.reminder_items_container);
        mReminderAddContainer = (RelativeLayout) view
                .findViewById(R.id.reminder_add_container);

        mTimezone = Utils.getTimeZone(activity, null);
        mIsMultipane = activity.getResources().getBoolean(R.bool.tablet_config);
        mStartTime = new Time(mTimezone);
        mEndTime = new Time(mTimezone);
        mEmailValidator = new Rfc822Validator(null);
        initMultiAutoCompleteTextView((RecipientsEditor) mAttendeesList);

        // Display loading screen
        setModel(null);

        FragmentManager fm = activity.getFragmentManager();
        RecurrencePickerDialog rpd = (RecurrencePickerDialog) fm
                .findFragmentByTag(FRAG_TAG_RECUR_PICKER);
        if (rpd != null) {
            rpd.setOnRecurrenceSetListener(this);
        }

        mTimezoneAdapter = new TimezoneAdapter(mActivity, mTimezone);

    }

    // add by hct.gengbin
    private void setVisibilityForLocationRemind(boolean isLocationRemind) {
        mMoreLayout.setVisibility(View.GONE);
        setMoreEventVisibility(true);
        View allDayView = mView.findViewById(R.id.all_day_row);
        if (isLocationRemind) {
            // whenRowView.setVisibility(View.GONE);
            allDayView.setVisibility(View.GONE);
            mFromRow.setVisibility(View.GONE);
            mToRow.setVisibility(View.GONE);
            mStartDateTimeButton.setVisibility(View.GONE);
            mEndDateTimeButton.setVisibility(View.GONE);
            mDescriptionGroup.setVisibility(View.VISIBLE);
            mEditEventColor.setVisibility(View.GONE);
            mRemindersGroup.setVisibility(View.GONE);
            mRepeatsGroup.setVisibility(View.GONE);
            mCalendarGroup.setVisibility(View.GONE);
            mTimeZoneGroup.setVisibility(View.GONE);
            mRemindersContainer.setVisibility(View.GONE);
            mReminderAddContainer.setVisibility(View.GONE);
            mReminderLabel.setVisibility(View.GONE);
            mLocationRemindGroup.setVisibility(View.VISIBLE);

            // mCalendarStaticGroup.setVisibility(View.GONE);
        } else {
            // whenRowView.setVisibility(View.GONE);
            allDayView.setVisibility(View.GONE);
            mFromRow.setVisibility(View.VISIBLE);
            mToRow.setVisibility(View.VISIBLE);
            mStartDateTimeButton.setVisibility(View.VISIBLE);
            mEndDateTimeButton.setVisibility(View.VISIBLE);
            // mDescriptionGroup.setVisibility(View.GONE);
            showEditEventColor();
            mRemindersGroup.setVisibility(View.VISIBLE);
            mRepeatsGroup.setVisibility(View.VISIBLE);
            mCalendarGroup.setVisibility(View.VISIBLE);
            mTimeZoneGroup.setVisibility(View.VISIBLE);
            mLocationRemindGroup.setVisibility(View.GONE);
            // mCalendarStaticGroup.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Loads an integer array asset into a list.
     */
    private static ArrayList<Integer> loadIntegerArray(Resources r, int resNum) {
        int[] vals = r.getIntArray(resNum);
        int size = vals.length;
        ArrayList<Integer> list = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            list.add(vals[i]);
        }

        return list;
    }

    protected int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Loads a String array asset into a list.
     */
    private static ArrayList<String> loadStringArray(Resources r, int resNum) {
        String[] labels = r.getStringArray(resNum);
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(labels));
        return list;
    }

    private void prepareAvailability() {
        Resources r = mActivity.getResources();

        mAvailabilityValues = loadIntegerArray(r, R.array.availability_values);
        mAvailabilityLabels = loadStringArray(r, R.array.availability);
        mAccessLevelLabels = loadStringArray(r, R.array.visibility);
        // Copy the unadulterated availability labels for all-day toggling.
        mOriginalAvailabilityLabels = new ArrayList<String>();
        mOriginalAvailabilityLabels.addAll(mAvailabilityLabels);

        if (mModel.mCalendarAllowedAvailability != null) {
            EventViewUtils.reduceMethodList(mAvailabilityValues,
                    mAvailabilityLabels, mModel.mCalendarAllowedAvailability);
        }

        mAvailabilityAdapter = new ArrayAdapter<String>(mActivity,
                R.layout.simple_spinner_item, mAvailabilityLabels);
        mAvailabilityAdapter
                .setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mAvailabilitySpinner.setAdapter(mAvailabilityAdapter);

        mAccessLevelAdapter = new ArrayAdapter<String>(mActivity,
                R.layout.simple_spinner_item, mAccessLevelLabels);
        mAccessLevelAdapter
                .setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mAccessLevelSpinner.setAdapter(mAccessLevelAdapter);
    }

    /**
     * Prepares the reminder UI elements.
     * <p>
     * (Re-)loads the minutes / methods lists from the XML assets, adds/removes
     * items as needed for the current set of reminders and calendar properties,
     * and then creates UI elements.
     */
    private void prepareReminders() {
        CalendarEventModel model = mModel;
        Resources r = mActivity.getResources();
        isEqualMycalendar = isMycalendar();
        // Load the labels and corresponding numeric values for the minutes and
        // methods lists
        // from the assets. If we're switching calendars, we need to clear and
        // re-populate the
        // lists (which may have elements added and removed based on calendar
        // properties). This
        // is mostly relevant for "methods", since we shouldn't have any
        // "minutes" values in a
        // new event that aren't in the default set.
        mReminderMinuteValues = loadIntegerArray(r,
                R.array.reminder_minutes_values);
        mReminderMinuteLabels = loadStringArray(r,
                R.array.reminder_minutes_labels);
        mReminderMethodValues = loadIntegerArray(r,
                R.array.reminder_methods_values);
        mReminderMethodLabels = loadStringArray(r,
                R.array.reminder_methods_labels);
        mAlldayReminderMinuteValues = loadIntegerArray(r,
                R.array.reminder_allday_minutes_values);
        if (DateFormat.is24HourFormat(mActivity)) {
            mAlldayReminderMinuteLabels = loadStringArray(r,
                    R.array.reminder_allday_minutes_labels_24);
        } else {
            mAlldayReminderMinuteLabels = loadStringArray(r,
                    R.array.reminder_allday_minutes_labels_12);
        }
        // Remove any reminder methods that aren't allowed for this calendar. If
        // this is
        // a new event, mCalendarAllowedReminders may not be set the first time
        // we're called.
        if (mModel.mCalendarAllowedReminders != null) {
            EventViewUtils.reduceMethodList(mReminderMethodValues,
                    mReminderMethodLabels, mModel.mCalendarAllowedReminders);
        }

        int numReminders = 0;
        if (model.mHasAlarm) {
            ArrayList<ReminderEntry> reminders = model.mReminders;
            numReminders = reminders.size();
            // Insert any minute values that aren't represented in the minutes
            // list.
            for (ReminderEntry re : reminders) {
                if (mReminderMethodValues.contains(re.getMethod())) {
                    if (model.mAllDay) {
                        EventViewUtils.addMinutesToList(mActivity,
                                mAlldayReminderMinuteValues,
                                mAlldayReminderMinuteLabels, re.getMinutes());
                    } else {
                        EventViewUtils.addMinutesToList(mActivity,
                                mReminderMinuteValues, mReminderMinuteLabels,
                                re.getMinutes());
                    }
                }
            }

            // Create a UI element for each reminder. We display all of the
            // reminders we get
            // from the provider, even if the count exceeds the calendar
            // maximum. (Also, for
            // a new event, we won't have a maxReminders value available.)
            mUnsupportedReminders.clear();
            // for (ReminderEntry re : reminders) {
            for (int i = reminders.size() - 1; i >= 0; i--) {
                ReminderEntry re = reminders.get(i);
                if (mReminderMethodValues.contains(re.getMethod())
                        || re.getMethod() == Reminders.METHOD_DEFAULT) {
                    if (mAllDayCheckBox.isChecked() || model.mAllDay) {
                        EventViewUtils.addReminder(mActivity, mScrollView,
                                this, mReminderItems,
                                mAlldayReminderMinuteValues,
                                mAlldayReminderMinuteLabels,
                                mReminderMethodValues, mReminderMethodLabels,
                                re, Integer.MAX_VALUE, null);
                    } else {
                        EventViewUtils.addReminder(mActivity, mScrollView,
                                this, mReminderItems, mReminderMinuteValues,
                                mReminderMinuteLabels, mReminderMethodValues,
                                mReminderMethodLabels, re, Integer.MAX_VALUE,
                                null);
                    }
                } else {
                    // TODO figure out a way to display unsupported reminders
                    mUnsupportedReminders.add(re);
                }
            }
        }

        updateRemindersVisibility(numReminders);
        EventViewUtils.updateAddReminderButton(mView, mReminderItems,
                mReminderMinuteValues.size());
    }

    // hct _modify lixiange MF3.0
    public boolean isMycalendar() {
        if (mModel == null || (mCalendarsCursor == null && mModel.mUri == null)) {
            return false;
        }

        if (MY_CALNEDAR.equals(mModel.mOwnerAccount)) {
            return true;
        }

        int calendarCursorPosition = mCalendarsSpinner
                .getSelectedItemPosition();
        if (mCalendarsCursor != null
                && mCalendarsCursor.moveToPosition(calendarCursorPosition)) {
            String defaultCalendar = mCalendarsCursor
                    .getString(EditEventHelper.CALENDARS_INDEX_OWNER_ACCOUNT);
            if (MY_CALNEDAR.equals(defaultCalendar)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTextViewNull(TextView view) {
        return ((view.getText() == null) || (view.getText().length() == 0));
    }

    public boolean isEmpty() {
        return (isTextViewNull(mTitleTextView)
                && isTextViewNull(mLocationTextView) && isTextViewNull(mDescriptionTextView));
    }

    // hct _modify lixiange MF3.0

    /**
     * Fill in the view with the contents of the given event model. This allows
     * an edit view to be initialized before the event has been loaded. Passing
     * in null for the model will display a loading screen. A non-null model
     * will fill in the view's fields with the data contained in the model.
     * 
     * @param model
     *            The event model to pull the data from
     */
    public void setModel(CalendarEventModel model) {
        mModel = model;

        // Need to close the autocomplete adapter to prevent leaking cursors.
        if (mAddressAdapter != null
                && mAddressAdapter instanceof EmailAddressAdapter) {
            ((EmailAddressAdapter) mAddressAdapter).close();
            mAddressAdapter = null;
        }

        if (model == null) {
            // Display loading screen
            mLoadingMessage.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
            return;
        }
        boolean canRespond = EditEventHelper.canRespond(model);

        long begin = model.mStart;
        long end = model.mEnd;
        mTimezone = model.mTimezone; // this will be UTC for all day events

        // Set up the starting times
        if (begin > 0) {
            mStartTime.timezone = mTimezone;
            mStartTime.set(begin);
            mStartTime.normalize(true);
        }
        if (end > 0) {
            mEndTime.timezone = mTimezone;
            mEndTime.set(end);
            mEndTime.normalize(true);
        }

        mRrule = model.mRrule;
        if (!TextUtils.isEmpty(mRrule)) {
            mEventRecurrence.parse(mRrule);
        }

        if (mEventRecurrence.startDate == null) {
            mEventRecurrence.startDate = mStartTime;
        }

        // If the user is allowed to change the attendees set up the view and
        // validator
        Boolean compMFVersion = Utils.getBuildMFVVersion(mActivity
                .getApplicationContext()) >= SELECT_EXEMAIL_FROM_CONTACT_VERSION;
        if (!model.mHasAttendeeData || isMycalendar()) {
            mAttendeesGroup.setVisibility(View.GONE);
        } else if (!compMFVersion) {
            mAttendeesGroup.setVisibility(View.GONE);
            mAddContactButton.setVisibility(View.GONE);
        }

        // hct _modify lixiange MF3.0
        if (isMycalendar()) {
            mShowMeAs.setVisibility(View.GONE);
            mPrivacy.setVisibility(View.GONE);
            mAvailabilitySpinner.setVisibility(View.GONE);
            mAccessLevelSpinner.setVisibility(View.GONE);
        }
        // hct _modify lixiange MF3.0

        mAllDayCheckBox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        setAllDayViewsVisibility(isChecked);
                        clearAllReminders();
                    }
                });

        boolean prevAllDay = mAllDayCheckBox.isChecked();
        mAllDay = false; // default to false. Let setAllDayViewsVisibility
        // update it as needed
        if (model.mAllDay) {
            mAllDayCheckBox.setChecked(true);
            // put things back in local time for all day events
            mTimezone = Utils.getTimeZone(mActivity, null);
            mStartTime.timezone = mTimezone;
            mEndTime.timezone = mTimezone;
            mEndTime.normalize(true);
        } else {
            mAllDayCheckBox.setChecked(false);
        }
        // On a rotation we need to update the views but onCheckedChanged
        // doesn't get called
        if (prevAllDay == mAllDayCheckBox.isChecked()) {
            setAllDayViewsVisibility(prevAllDay);
        }

        mTimezoneAdapter = new TimezoneAdapter(mActivity, mTimezone);
        if (mTimezoneDialog != null) {
            try {
                mTimezoneDialog.getListView().setAdapter(mTimezoneAdapter);
            } catch (Exception e) {
                e.getMessage();
            }
        }

        populateTimezone(mStartTime.normalize(true));

        SharedPreferences prefs = GeneralPreferences
                .getSharedPreferences(mActivity);
        String defaultReminderString = prefs.getString(
                GeneralPreferences.KEY_DEFAULT_REMINDER,
                GeneralPreferences.NO_REMINDER_STRING);
        mDefaultReminderMinutes = Integer.parseInt(defaultReminderString);

        prepareReminders();
        prepareAvailability();

        View reminderAddButton = mView
                .findViewById(R.id.reminder_add_container);
        View.OnClickListener addReminderOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences isUserAddReminder = mActivity
                        .getSharedPreferences("isUserAddReminder", 0);
                SharedPreferences.Editor isUserAddReminderEditor = isUserAddReminder
                        .edit();
                isUserAddReminderEditor.putBoolean("isChanged", true);
                isUserAddReminderEditor.commit();
                addReminder();
            }
        };
        reminderAddButton.setOnClickListener(addReminderOnClickListener);

        if (!mIsMultipane) {
            mView.findViewById(R.id.is_all_day_label).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAllDayCheckBox.setChecked(!mAllDayCheckBox
                                    .isChecked());
                        }
                    });
        }

        if (model.mTitle != null) {
            mTitleTextView.setTextKeepState(model.mTitle);
        }

        if (model.mIsOrganizer || TextUtils.isEmpty(model.mOrganizer)
                || model.mOrganizer.endsWith(GOOGLE_SECONDARY_CALENDAR)) {
            mView.findViewById(R.id.organizer_label).setVisibility(View.GONE);
            mView.findViewById(R.id.organizer).setVisibility(View.GONE);
            mOrganizerGroup.setVisibility(View.GONE);
        } else {
            ((TextView) mView.findViewById(R.id.organizer))
                    .setText(model.mOrganizerDisplayName);
        }

        if (model.mLocation != null) {
            mLocationTextView.setTextKeepState(model.mLocation);
        }

        if (model.mDescription != null) {
            mDescriptionTextView.setTextKeepState(model.mDescription);
        }

        int availIndex = mAvailabilityValues.indexOf(model.mAvailability);
        if (availIndex != -1) {
            mAvailabilitySpinner.setSelection(availIndex);
        }
        mAccessLevelSpinner.setSelection(model.mAccessLevel);

        View responseLabel = mView.findViewById(R.id.response_label);
        if (canRespond) {
            int buttonToCheck = EventInfoFragment
                    .findButtonIdForResponse(model.mSelfAttendeeStatus);
            mResponseRadioGroup.check(buttonToCheck); // -1 clear all radio
            // buttons
            mResponseRadioGroup.setVisibility(View.VISIBLE);
            responseLabel.setVisibility(View.VISIBLE);
        } else {
            responseLabel.setVisibility(View.GONE);
            mResponseRadioGroup.setVisibility(View.GONE);
            mResponseGroup.setVisibility(View.GONE);
        }

        if (model.mUri != null) {
            // This is an existing event so hide the calendar spinner
            // since we can't change the calendar.
            View calendarGroup = mView
                    .findViewById(R.id.calendar_selector_group);
            calendarGroup.setVisibility(View.GONE);
            EditText tv = (EditText) mView.findViewById(R.id.calendar_textview);
            String nameedit = model.mCalendarDisplayName;
            String mycalendar = mActivity.getString(R.string.my_calendar);
            if (nameedit.equals(MY_CALNEDAR)) {
                nameedit = mycalendar;
            }
            tv.setText(nameedit);
            // tv = (TextView)
            // mView.findViewById(R.id.calendar_textview_secondary);
            // tv.setText(mycalendar);
            if (tv != null) {
                // tv.setText(model.mOwnerAccount);
                if (MY_CALNEDAR.equals(model.mOwnerAccount)) {
                    // tv.setText(mycalendar);
                    calendarGroup.setVisibility(View.GONE);
                    // tv.setVisibility(View.GONE);
                    mShowMeAs.setVisibility(View.GONE);
                    mPrivacy.setVisibility(View.GONE);
                    mAvailabilitySpinner.setVisibility(View.GONE);
                    mAccessLevelSpinner.setVisibility(View.GONE);
                }
            }
        } else {
            View calendarGroup = mView.findViewById(R.id.calendar_group);
            calendarGroup.setVisibility(View.GONE);
        }
        /**
         * if (model.isEventColorInitialized()) { updateHeadlineColor(model,
         * model.getEventColor()); }
         */
        Drawable c_color = mActivity.getResources().getDrawable(
                R.drawable.icon_choosen);
        c_color.setTint(0xffffffff);
        if (model.getEventColor() != -1) {
            if (model.getEventColor() == Utils
                    .getDisplayColorFromColor(mEventRed)) {
                mChangeColorRed.setImageDrawable(c_color);
            } else if (model.getEventColor() == Utils
                    .getDisplayColorFromColor(mEventOrange)) {
                mChangeColorOrange.setImageDrawable(c_color);
            } else if (model.getEventColor() == Utils
                    .getDisplayColorFromColor(mEventYellow)) {
                mChangeColorYellow.setImageDrawable(c_color);
            } else if (model.getEventColor() == Utils
                    .getDisplayColorFromColor(mEventGreen)) {
                mChangeColorGreen.setImageDrawable(c_color);
            } else if (model.getEventColor() == Utils
                    .getDisplayColorFromColor(mEventDim)) {
                mChangeColorDim.setImageDrawable(c_color);
            } else if (model.getEventColor() == Utils
                    .getDisplayColorFromColor(mEventBlue)) {
                mChangeColorBlue.setImageDrawable(c_color);
            } else if (model.getEventColor() == Utils
                    .getDisplayColorFromColor(mEventPurple)) {
                mChangeColorPurple.setImageDrawable(c_color);
            }
        }

        // HCT_MODIFY,lixiange MF3.0 new event
        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add permission
                if (isMycalendar()
                        || (Utils.checkAndRequestPermission(mActivity,
                                Manifest.permission.READ_CONTACTS,
                                Utils.REQUEST_PERMISSIONS_CONTACTS_EDIT))) {
                    setMoreEventVisibility(true);
                }
                mMoreLayout.setVisibility(View.GONE);
            }
        });
        // HCT_MODIFY,lixiange MF3.0 new event

        populateWhen();
        populateRepeats();
        updateAttendees(model.mAttendeesList);

        updateView();
        mScrollView.setVisibility(View.VISIBLE);
        mLoadingMessage.setVisibility(View.GONE);
        sendAccessibilityEvent();

        if (model.mEventType == EventItem.VIEW_EVENT_LOCATION) {
            setVisibilityForLocationRemind(true);
            String radius = model.mActionData;
            if (!TextUtils.isEmpty(radius)) {
                radius = radius.trim();
                try {
                    mLocationRemindRadius = Integer.parseInt(radius);
                } catch (NumberFormatException exception) {
                    mLocationRemindRadius = 0;
                    Log.e(TAG,
                            "radius parse to int error"
                                    + exception.getMessage());
                }
            }
            mReminderRangeSB.setProgress(mLocationRemindRadius);
        }
        mMapParam = model.mActionData1;
        mEventType = model.mEventType;

        // show map in edit window
        if (model.mLocation != null) {
            mLocationTextString = model.mLocation;
            if (!TextUtils.isEmpty(mLocationTextString)) {
                String locationMap = EditEventView.MAP_DIR + "/"
                        + mLocationTextString + ".jpg";
                Log.d(TAG, "locationMap= " + locationMap);
                File targetFile = new File(locationMap);
                if (targetFile.exists()) {
                    bitmaps = getLoacalBitmap(locationMap);
                    if (bitmaps != null) {
                        BitmapDrawable bmpDraw = new BitmapDrawable(bitmaps);
                        mMap.setImageDrawable(bmpDraw);
                        mMapContainer.setVisibility(View.VISIBLE);
                    }
                }
            }

        }

    }

    public void updateHeadlineColor(CalendarEventModel model, int displayColor) {
        if (model.mUri != null) {
            if (mIsMultipane) {
                mView.findViewById(R.id.calendar_textview_with_colorpicker)
                        .setBackgroundColor(displayColor);
            } else {
                mView.findViewById(R.id.calendar_group).setBackgroundColor(
                        displayColor);
            }
        } else {
            // setSpinnerBackgroundColor(displayColor);
        }
    }

    private void setSpinnerBackgroundColor(int displayColor) {
        if (mIsMultipane) {
            mCalendarSelectorWrapper.setBackgroundColor(displayColor);
        } else {
            mCalendarSelectorGroup.setBackgroundColor(displayColor);
        }
    }

    private void sendAccessibilityEvent() {
        AccessibilityManager am = (AccessibilityManager) mActivity
                .getSystemService(Service.ACCESSIBILITY_SERVICE);
        if (!am.isEnabled() || mModel == null) {
            return;
        }
        StringBuilder b = new StringBuilder();
        addFieldsRecursive(b, mView);
        CharSequence msg = b.toString();

        AccessibilityEvent event = AccessibilityEvent
                .obtain(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        event.setClassName(getClass().getName());
        event.setPackageName(mActivity.getPackageName());
        event.getText().add(msg);
        event.setAddedCount(msg.length());

        am.sendAccessibilityEvent(event);
    }

    private void addFieldsRecursive(StringBuilder b, View v) {
        if (v == null || v.getVisibility() != View.VISIBLE) {
            return;
        }
        if (v instanceof TextView) {
            CharSequence tv = ((TextView) v).getText();
            if (!TextUtils.isEmpty(tv.toString().trim())) {
                b.append(tv + PERIOD_SPACE);
            }
        } else if (v instanceof RadioGroup) {
            RadioGroup rg = (RadioGroup) v;
            int id = rg.getCheckedRadioButtonId();
            if (id != View.NO_ID) {
                b.append(((RadioButtonHCT) (v.findViewById(id))).getText()
                        + PERIOD_SPACE);
            }
        } else if (v instanceof Spinner) {
            Spinner s = (Spinner) v;
            if (s.getSelectedItem() instanceof String) {
                String str = ((String) (s.getSelectedItem())).trim();
                if (!TextUtils.isEmpty(str)) {
                    b.append(str + PERIOD_SPACE);
                }
            }
        } else if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            int children = vg.getChildCount();
            for (int i = 0; i < children; i++) {
                addFieldsRecursive(b, vg.getChildAt(i));
            }
        }
    }

    /**
     * Creates a single line string for the time/duration
     */
    protected void setWhenString() {
        String when;
        int flags = DateUtils.FORMAT_SHOW_DATE;
        String tz = mTimezone;
        if (mModel.mAllDay) {
            flags |= DateUtils.FORMAT_SHOW_WEEKDAY;
            tz = Time.TIMEZONE_UTC;
        } else {
            flags |= DateUtils.FORMAT_SHOW_TIME;
            if (DateFormat.is24HourFormat(mActivity)) {
                flags |= DateUtils.FORMAT_24HOUR;
            }
        }
        long startMillis = mStartTime.normalize(true);
        long endMillis = mEndTime.normalize(true);
        mSB.setLength(0);
        if (mModel.mAllDay) {
            mWhenView.setText(R.string.edit_event_all_day_label);
        } else {
            when = DateUtils.formatDateRange(mActivity, mF, startMillis,
                    endMillis, flags, tz).toString();
            mWhenView.setText(when);
        }
    }

    /**
     * Configures the Calendars spinner. This is only done for new events,
     * because only new events allow you to select a calendar while editing an
     * event.
     * <p>
     * We tuck a reference to a Cursor with calendar database data into the
     * spinner, so that we can easily extract calendar-specific values when the
     * value changes (the spinner's onItemSelected callback is configured).
     */
    public void setCalendarsCursor(Cursor cursor, boolean userVisible,
            long selectedCalendarId) {
        // If there are no syncable calendars, then we cannot allow
        // creating a new event.
        mCalendarsCursor = cursor;
        if (cursor == null || cursor.getCount() == 0) {
            // Cancel the "loading calendars" dialog if it exists
            if (mSaveAfterQueryComplete) {
                mLoadingCalendarsDialog.cancel();
            }
            if (!userVisible) {
                return;
            }

            mNoCalendarsDialog = null;
            return;
        }

        int selection;
        if (selectedCalendarId != -1) {
            selection = findSelectedCalendarPosition(cursor, selectedCalendarId);
        } else {
            selection = findDefaultCalendarPosition(cursor);
        }

        // populate the calendars spinner
        CalendarsAdapter adapter = new CalendarsAdapter(mActivity,
                R.layout.calendars_spinner_item, cursor);
        // adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mCalendarsSpinner.setAdapter(adapter);
        mCalendarsSpinner.setOnItemSelectedListener(this);
        mCalendarsSpinner.setSelection(selection);

        if (mSaveAfterQueryComplete) {
            mLoadingCalendarsDialog.cancel();
            if (prepareForSave() && fillModelFromUI()) {
                int exit = userVisible ? Utils.DONE_EXIT : 0;
                mDone.setDoneCode(Utils.DONE_SAVE | exit);
                mDone.run();
            } else if (userVisible) {
                mDone.setDoneCode(Utils.DONE_EXIT);
                mDone.run();
            } else if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG,
                        "SetCalendarsCursor:Save failed and unable to exit view");
            }
            return;
        }
    }

    /**
     * Updates the view based on {@link #mModification} and {@link #mModel}
     */
    public void updateView() {
        if (mModel == null) {
            return;
        }
        if (EditEventHelper.canModifyEvent(mModel)) {
            setViewStates(mModification);
        } else {
            setViewStates(Utils.MODIFY_UNINITIALIZED);
        }
    }

    private void setViewStates(int mode) {
        // Extra canModify check just in case
        if (mode == Utils.MODIFY_UNINITIALIZED
                || !EditEventHelper.canModifyEvent(mModel)) {
            setWhenString();

            for (View v : mViewOnlyList) {
                v.setVisibility(View.VISIBLE);
            }
            for (View v : mEditOnlyList) {
                v.setVisibility(View.GONE);
            }
            for (View v : mEditViewList) {
                v.setEnabled(false);
                v.setBackgroundDrawable(null);
            }
            mCalendarSelectorGroup.setVisibility(View.GONE);
            mCalendarStaticGroup.setVisibility(View.GONE);
            mRepeatsSpinner.setEnabled(false);
            if (EditEventHelper.canAddReminders(mModel)) {
                mRemindersGroup.setVisibility(View.VISIBLE);
            } else {
                mRemindersGroup.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(mLocationTextView.getText())) {
                mLocationGroup.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(mDescriptionTextView.getText())) {
                mDescriptionGroup.setVisibility(View.GONE);
            }
        } else {
            for (View v : mViewOnlyList) {
                v.setVisibility(View.GONE);
            }
            for (View v : mEditOnlyList) {
                v.setVisibility(View.VISIBLE);
            }
            for (View v : mEditViewList) {
                v.setEnabled(true);
                if (v.getTag() != null) {
                    v.setBackgroundDrawable((Drawable) v.getTag());
                    v.setPadding(mOriginalPadding[0], mOriginalPadding[1],
                            mOriginalPadding[2], mOriginalPadding[3]);
                }
            }
            if (mModel.mUri == null) {
                mCalendarSelectorGroup.setVisibility(View.GONE);
                mCalendarStaticGroup.setVisibility(View.GONE);
            } else {
                mCalendarSelectorGroup.setVisibility(View.GONE);
                mCalendarStaticGroup.setVisibility(View.GONE);
            }
            mRepeatsSpinner.setPadding(mOriginalSpinnerPadding[0],
                    mOriginalSpinnerPadding[1], mOriginalSpinnerPadding[2],
                    mOriginalSpinnerPadding[3]);
            boolean edit = mActivity.getIntent().getBooleanExtra(
                    CalendarController.EVENT_EDIT_ON_LAUNCH, false);
            if (mModel.mOriginalSyncId == null) {
                mRepeatsSpinner.setEnabled(/* !edit */true);
            } else {
                mRepeatsSpinner.setEnabled(false);
                // mRepeatsSpinner.setBackgroundDrawable(null);
            }
            mRemindersGroup.setVisibility(View.VISIBLE);

            mLocationGroup.setVisibility(View.VISIBLE);
            mDescriptionGroup.setVisibility(View.VISIBLE);
        }
        setAllDayViewsVisibility(mAllDayCheckBox.isChecked());
    }

    public void setModification(int modifyWhich) {
        mModification = modifyWhich;
        updateView();
        updateHomeTime();
    }

    private int findSelectedCalendarPosition(Cursor calendarsCursor,
            long calendarId) {
        if (calendarsCursor.getCount() <= 0) {
            return -1;
        }
        int calendarIdColumn = calendarsCursor
                .getColumnIndexOrThrow(Calendars._ID);
        int position = 0;
        calendarsCursor.moveToPosition(-1);
        while (calendarsCursor.moveToNext()) {
            if (calendarsCursor.getLong(calendarIdColumn) == calendarId) {
                return position;
            }
            position++;
        }
        return 0;
    }

    // Find the calendar position in the cursor that matches calendar in
    // preference
    private int findDefaultCalendarPosition(Cursor calendarsCursor) {
        if (calendarsCursor.getCount() <= 0) {
            return -1;
        }

        String defaultCalendar = Utils.getSharedPreference(mActivity,
                GeneralPreferences.KEY_DEFAULT_CALENDAR, (String) null);

        int calendarsOwnerIndex = calendarsCursor
                .getColumnIndexOrThrow(Calendars.OWNER_ACCOUNT);
        int accountNameIndex = calendarsCursor
                .getColumnIndexOrThrow(Calendars.ACCOUNT_NAME);
        int accountTypeIndex = calendarsCursor
                .getColumnIndexOrThrow(Calendars.ACCOUNT_TYPE);
        int position = 0;
        calendarsCursor.moveToPosition(-1);
        while (calendarsCursor.moveToNext()) {
            String calendarOwner = calendarsCursor
                    .getString(calendarsOwnerIndex);
            if (defaultCalendar == null) {
                // There is no stored default upon the first time running. Use a
                // primary
                // calendar in this case.
                if (calendarOwner != null
                        && calendarOwner.equals(calendarsCursor
                                .getString(accountNameIndex))
                        && !CalendarContract.ACCOUNT_TYPE_LOCAL
                                .equals(calendarsCursor
                                        .getString(accountTypeIndex))) {
                    return position;
                }
            } else if (defaultCalendar.equals(calendarOwner)) {
                // Found the default calendar.
                return position;
            }
            position++;
        }
        return 0;
    }

    private void updateAttendees(HashMap<String, Attendee> attendeesList) {
        if (attendeesList == null || attendeesList.isEmpty()) {
            return;
        }
        mAttendeesList.setText(null);
        for (Attendee attendee : attendeesList.values()) {

            // TODO: Please remove separator when Calendar uses the chips MR2
            // project

            // Adding a comma separator between email addresses to prevent a
            // chips MR1.1 bug
            // in which email addresses are concatenated together with no
            // separator.
            mAttendeesList.append(attendee.mEmail + ", ");
        }
    }

    private void updateRemindersVisibility(int numReminders) {
        if (numReminders == 0) {
            mRemindersContainer.setVisibility(View.GONE);
        } else {
            mRemindersContainer.setVisibility(View.VISIBLE);
        }
    }

    /*
     * Add Contact Email Address to Attendee list
     */
    public void addContactEmailAddress(Intent data) {
        ArrayList<String> selectData = data.getStringArrayListExtra(data
                .getAction());
        processSelectAddress(selectData);
    }

    private void processSelectAddress(ArrayList<String> selectData) {
        String address = mAttendeesList.getText().toString();
        Log.i(TAG, "addContactEmailAddress, String address =" + address);
        String[] addressList = address.split(",|;");
        String checkAddress = null;
        String addresstoadd = "";

        boolean find = false;

        for (int i = 0; i < selectData.size(); i++) {
            find = false;

            Log.i(TAG, "addContactEmailAddress, selectData.get(" + i + ") ="
                    + selectData.get(i));

            if (selectData.get(i).indexOf('<') != -1) {
                int end = selectData.get(i).indexOf('>');
                if (end == -1) {
                    checkAddress = selectData.get(i).substring(
                            selectData.get(i).indexOf('<') + 1);
                } else {
                    checkAddress = selectData.get(i).substring(
                            selectData.get(i).indexOf('<') + 1, end);
                }
            } else {
                checkAddress = selectData.get(i);
            }

            find = findSameAddress(checkAddress, addressList, addresstoadd);
            if (find == false) {
                addresstoadd = addresstoadd + checkAddress + ",";
                mAttendeesList.append(selectData.get(i));
                Log.i(TAG, "addContactEmailAddress, String appended ="
                        + mAttendeesList.getText().toString());
            }
        }

        mAttendeesList.setError(null);
        Log.i(TAG, "addContactEmailAddress, String appended1 ="
                + mAttendeesList.getText().toString());
        mAttendeesList.requestFocus();
        Log.i(TAG, "addContactEmailAddress, String appended2 ="
                + mAttendeesList.getText().toString());
    }

    public void addEmailAddressFromContact(Intent data) {
        String addressListStr = data
                .getStringExtra("com.android.contacts.extra.MFV_EMAILS_DATA");
        if (!TextUtils.isEmpty(addressListStr)) {
            String[] addressArr = addressListStr.split(";");
            if (addressArr != null && addressArr.length > 0) {
                ArrayList<String> addressList = new ArrayList<String>(
                        addressArr.length);
                for (int i = 0; i < addressArr.length; i++) {
                    addressList.add(addressArr[i]);
                }
                processSelectAddress(addressList);
            }
        }
    }

    /*
     * Check the same address
     */
    private boolean findSameAddress(String addressItem, String[] addressList,
            String addresstoadd) {
        boolean find = false;

        if (addressItem == null || addressList == null
                || addressList.length == 0) {
            return false;
        }

        if (addresstoadd.indexOf(addressItem) != -1) {
            return true;
        }

        if (!Regex.EMAIL_ADDRESS_PATTERN.matcher(addressItem).find()
                && !Pattern.compile("[0-9]{11}").matcher(addressItem).find()) {
            Log.i(TAG, "findSameAddress, return false");
            return false;
        }

        addressItem = addressItem.replace(",", " ");
        addressItem = addressItem.replace(";", " ");

        // check the same address
        String existAddress = null;
        for (int j = 0; j < addressList.length; j++) {
            if (addressList[j].indexOf('<') != -1) {
                existAddress = addressList[j].substring(
                        addressList[j].indexOf('<') + 1,
                        addressList[j].indexOf('>'));
            } else {
                existAddress = addressList[j];
            }

            if (addressItem.equalsIgnoreCase(existAddress)) {
                find = true;
                break;
            }
        }

        return find;
    }

    /**
     * HCT_MODIFY start add LBS lixiange MF3.0
     *********************************************/
    String mMapScreenshotFile = null;
    String mLocationTextString = null;
    public Bitmap bitmaps;

    public void addLocation(Intent data, Context context) {
        mMapScreenshotFile = data.getStringExtra("MapScreenshot");
        mMapParam = data.getStringExtra("MapParam");
        Log.i(TAG, "addLocation, mapParam = " + mMapParam);
        try {
            JSONObject jsonObject = new JSONObject(mMapParam);
            String mapCenter = jsonObject.getString("MapCenter");
            int mapZoom = jsonObject.getInt("MapZoom");

            if (jsonObject.has("Marker")) {
                JSONObject jsonMarker = jsonObject.getJSONObject("Marker");
                String mame = (jsonMarker.has("name")) ? jsonMarker
                        .getString("name") : null;
                String address = (jsonMarker.has("addr")) ? jsonMarker
                        .getString("addr") : null;
                if (address != null) {
                    mLocationTextString = address;
                }
                String city = (jsonMarker.has("city")) ? jsonMarker
                        .getString("city") : null;
                String uid = (jsonMarker.has("uid")) ? jsonMarker
                        .getString("uid") : null;
                String location = jsonMarker.getString("location");
            }

            if (jsonObject.has("Route")) {
                JSONObject route = jsonObject.getJSONObject("Route");
                JSONObject jsonStart = route.getJSONObject("Start");
                JSONObject jsonEnd = route.getJSONObject("End");
                Log.i(TAG, "addLocation, jsonStart = " + jsonStart.toString()
                        + "\r\n" + "jsonEnd" + jsonEnd.toString());
                String routeDes = route.getString("Description");
                String routeStartEnd = jsonStart.getString("addr") + "--"
                        + jsonEnd.getString("addr");
                if (routeStartEnd.length() > 2) {
                    mLocationTextString = routeStartEnd;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "lxg locationTextString = " + mLocationTextString
                + ",mapScreenshotFile=" + mMapScreenshotFile);
        if (mLocationTextString != null) {
            mLocationTextView.setText(mLocationTextString);
            if (Utils.checkAndRequestPermission(mActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Utils.REQUEST_PERMISSIONS_EXTERNAL_STORAGE)) {
                setSaveMap(context);
            }
        }
    }

    private Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean saveMap(Bitmap mBitmap, Context context, String address) {
        String mTargetDirectory = MAP_DIR;
        Log.i(TAG, "saveMap mTargetDirectory = " + mTargetDirectory);
        File directory = new File(mTargetDirectory);
        if (!(directory.exists() && directory.isDirectory() && directory
                .canRead()) && !directory.mkdirs()) {
            return false;
        }
        mTargetDirectory = mTargetDirectory + "/" + address + ".jpg";
        File targetFile = new File(mTargetDirectory);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        // TODO Auto-generated method stub
        try {
            OutputStream outputStream = new FileOutputStream(targetFile, false);
            try {
                // byte[] buffer = mBitmap.getBytes("UTF-8");
                Log.i(TAG, "saveMap mBitmap = " + (mBitmap == null)
                        + " outputStream = " + (outputStream == null));
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "saveMap, compressFile IOException: " + e);
                return false;
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "saveMap, OpenFile FileNotFoundException: " + e);
            return false;
        }
        return true;
    }

    /** HCT_MODIFY end add LBS lixiange MF3.0 *********************************************/

    /**
     * Add a new reminder when the user hits the "add reminder" button. We use
     * the default reminder time and method.
     */
    private void addReminder() {
        // TODO: when adding a new reminder, make it different from the
        // last one in the list (if any).
        ArrayList<Integer> reminderValues = mReminderMinuteValues;
        ArrayList<String> reminderLabels = mReminderMinuteLabels;
        int maxReminders = mModel.mCalendarMaxReminders;
        if (mAllDay || mAllDayCheckBox.isChecked()) {
            reminderValues = mAlldayReminderMinuteValues;
            reminderLabels = mAlldayReminderMinuteLabels;
            maxReminders = mAlldayReminderMinuteValues.size();
        } else {
            reminderValues = mReminderMinuteValues;
            reminderLabels = mReminderMinuteLabels;
            maxReminders = mReminderMinuteValues.size() - 2;
        }
        if (mDefaultReminderMinutes == GeneralPreferences.NO_REMINDER) {
            EventViewUtils.addReminder(mActivity, mScrollView, this,
                    mReminderItems, reminderValues, reminderLabels,
                    mReminderMethodValues, mReminderMethodLabels, ReminderEntry
                            .valueOf(GeneralPreferences.REMINDER_DEFAULT_TIME),
                    maxReminders, null);
        } else {
            EventViewUtils.addReminder(mActivity, mScrollView, this,
                    mReminderItems, reminderValues, reminderLabels,
                    mReminderMethodValues, mReminderMethodLabels,
                    ReminderEntry.valueOf(mDefaultReminderMinutes),
                    maxReminders, null);
        }
        updateRemindersVisibility(mReminderItems.size());
        EventViewUtils.updateAddReminderButton(mView, mReminderItems,
                maxReminders);
    }

    // From com.google.android.gm.ComposeActivity
    private MultiAutoCompleteTextView initMultiAutoCompleteTextView(
            RecipientsEditor list) {
        if (ChipsUtil.supportsChipsUi()) {
            mAddressAdapter = new RecipientAdapter(mActivity);
            list.setAdapter((BaseRecipientAdapter) mAddressAdapter);
            list.setOnFocusListShrinkRecipients(false);
        } else {
            mAddressAdapter = new EmailAddressAdapter(mActivity);
            list.setAdapter((EmailAddressAdapter) mAddressAdapter);
        }
        // list.setTokenizer(new Rfc822Tokenizer());
        // list.setValidator(mEmailValidator);
        boolean bSmartReminder = Utils.getConfigBool(mActivity,
                R.bool.smart_reminder);
        if (bSmartReminder) {
            mInternalValidator = new AddressValidator();
            list.setValidator(mInternalValidator);
        } else {
            mEmailValidator = new Rfc822Validator(null);
            list.setValidator(mEmailValidator);
        }
        // NOTE: assumes no other filters are set
        // list.setFilters(sRecipientFilters);

        return list;
    }

    private class AddressValidator implements Validator {
        public CharSequence fixText(CharSequence invalidText) {
            return invalidText;
        }

        public boolean isValid(CharSequence text) {
            Log.i(TAG, "AddressValidator.isValid, text = " + text);
            Rfc822Validator EmailValidator = new Rfc822Validator(null);
            if (EmailValidator.isValid(text)) {
                return true;
            }

            String address = text.toString();

            Log.i(TAG, "AddressValidator.isValid, address = " + address);

            int start = address.indexOf('<');
            int end = address.indexOf('>');

            if (start != -1 && end != -1 && start < end) {
                address = address.substring(start + 1, end);
                Log.i(TAG, "AddressValidator.isValid, address in <> = "
                        + address);
            }

            address = address.replaceAll("\\+86", "");

            if (address.length() != 11) {
                return false;
            }

            String mobile_num[] = { "13", "15", "180", "185", "186", "187",
                    "188", "189" };

            Pattern p1 = Pattern.compile("[0-9]{11}");
            Matcher matcher1 = p1.matcher(address);

            if (matcher1.find()) {
                for (int i = 0; i < mobile_num.length; i++) {
                    Pattern p = Pattern.compile(mobile_num[i] + "[0-9]?");
                    Matcher matcher = p.matcher(address);

                    if (matcher.find()) {
                        Log.i(TAG, "AddressValidator.isValid, matcher" + i
                                + " find, black_list = " + mobile_num[i]);
                        return true;
                    }
                }
            }

            return false;
        }
    }

    /**
     * From com.google.android.gm.ComposeActivity Implements special address
     * cleanup rules: The first space key entry following an "@" symbol that is
     * followed by any combination of letters and symbols, including one+ dots
     * and zero commas, should insert an extra comma (followed by the space).
     */
    private static InputFilter[] sRecipientFilters = new InputFilter[] { new Rfc822InputFilter() };

    private void setDateTime(TextView view, long millis) {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH
                | DateUtils.FORMAT_ABBREV_WEEKDAY;

        // Unfortunately, DateUtils doesn't support a timezone other than the
        // default timezone provided by the system, so we have this ugly hack
        // here to trick it into formatting our time correctly. In order to
        // prevent all sorts of craziness, we synchronize on the TimeZone class
        // to prevent other threads from reading an incorrect timezone from
        // calls to TimeZone#getDefault()
        // TODO fix this if/when DateUtils allows for passing in a timezone
        String dateString;
        synchronized (TimeZone.class) {
            TimeZone.setDefault(TimeZone.getTimeZone(mTimezone));
            dateString = DateUtils.formatDateTime(mActivity, millis, flags);
            // setting the default back to null restores the correct behavior
            TimeZone.setDefault(null);
        }

        int flagsTime = DateUtils.FORMAT_SHOW_TIME;
        flagsTime |= DateUtils.FORMAT_CAP_NOON_MIDNIGHT;
        if (DateFormat.is24HourFormat(mActivity)) {
            flagsTime |= DateUtils.FORMAT_24HOUR;
        }

        // Unfortunately, DateUtils doesn't support a timezone other than the
        // default timezone provided by the system, so we have this ugly hack
        // here to trick it into formatting our time correctly. In order to
        // prevent all sorts of craziness, we synchronize on the TimeZone class
        // to prevent other threads from reading an incorrect timezone from
        // calls to TimeZone#getDefault()
        // TODO fix this if/when DateUtils allows for passing in a timezone
        String timeString;
        synchronized (TimeZone.class) {
            TimeZone.setDefault(TimeZone.getTimeZone(mTimezone));
            timeString = DateUtils.formatDateTime(mActivity, millis, flagsTime);
            TimeZone.setDefault(null);
        }
        view.setText(dateString + ", " + timeString);
    }

    private void setDate(TextView view, long millis) {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH
                | DateUtils.FORMAT_ABBREV_WEEKDAY;

        // Unfortunately, DateUtils doesn't support a timezone other than the
        // default timezone provided by the system, so we have this ugly hack
        // here to trick it into formatting our time correctly. In order to
        // prevent all sorts of craziness, we synchronize on the TimeZone class
        // to prevent other threads from reading an incorrect timezone from
        // calls to TimeZone#getDefault()
        // TODO fix this if/when DateUtils allows for passing in a timezone
        String dateString;
        synchronized (TimeZone.class) {
            TimeZone.setDefault(TimeZone.getTimeZone(mTimezone));
            dateString = DateUtils.formatDateTime(mActivity, millis, flags);
            // setting the default back to null restores the correct behavior
            TimeZone.setDefault(null);
        }
        view.setText(dateString);
    }

    private void setTime(TextView view, long millis) {
        int flags = DateUtils.FORMAT_SHOW_TIME;
        flags |= DateUtils.FORMAT_CAP_NOON_MIDNIGHT;
        if (DateFormat.is24HourFormat(mActivity)) {
            flags |= DateUtils.FORMAT_24HOUR;
        }

        // Unfortunately, DateUtils doesn't support a timezone other than the
        // default timezone provided by the system, so we have this ugly hack
        // here to trick it into formatting our time correctly. In order to
        // prevent all sorts of craziness, we synchronize on the TimeZone class
        // to prevent other threads from reading an incorrect timezone from
        // calls to TimeZone#getDefault()
        // TODO fix this if/when DateUtils allows for passing in a timezone
        String timeString;
        synchronized (TimeZone.class) {
            TimeZone.setDefault(TimeZone.getTimeZone(mTimezone));
            timeString = DateUtils.formatDateTime(mActivity, millis, flags);
            TimeZone.setDefault(null);
        }
        view.setText(timeString);
    }

    /**
     * @param isChecked
     */
    protected void setAllDayViewsVisibility(boolean isChecked) {
        if (isChecked) {
            if (mEndTime.hour == 0 && mEndTime.minute == 0) {
                if (mAllDay != isChecked) {
                    mEndTime.monthDay--;
                }

                long endMillis = mEndTime.normalize(true);

                // Do not allow an event to have an end time
                // before the
                // start time.
                if (mEndTime.before(mStartTime)) {
                    mEndTime.set(mStartTime);
                    endMillis = mEndTime.normalize(true);
                }
                setDate(mEndDateTimeButton, endMillis);
                // setTime(mEndTimeButton, endMillis);
            }
            long startMillis = mStartTime.normalize(true);
            long endMillis = mEndTime.normalize(true);
            setDate(mStartDateTimeButton, startMillis);
            setDate(mEndDateTimeButton, endMillis);
            // mStartTimeButton.setVisibility(View.GONE);
            // mEndTimeButton.setVisibility(View.GONE);
            mTimezoneRow.setVisibility(View.GONE);
        } else {
            if (mEndTime.hour == 0 && mEndTime.minute == 0) {
                if (mAllDay != isChecked) {
                    mEndTime.monthDay++;
                }

                long endMillis = mEndTime.normalize(true);
                setDateTime(mEndDateTimeButton, endMillis);
                // setTime(mEndTimeButton, endMillis);
            }
            long startMillis = mStartTime.normalize(true);
            long endMillis = mEndTime.normalize(true);
            setDateTime(mStartDateTimeButton, startMillis);
            setDateTime(mEndDateTimeButton, endMillis);
            // mStartTimeButton.setVisibility(View.VISIBLE);
            // mEndTimeButton.setVisibility(View.VISIBLE);
            mTimezoneRow.setVisibility(View.VISIBLE);
        }

        // If this is a new event, and if availability has not yet been
        // explicitly set, toggle busy/available as the inverse of all day.
        if (mModel.mUri == null && !mAvailabilityExplicitlySet) {
            // Values are from R.arrays.availability_values.
            // 0 = busy
            // 1 = available
            int newAvailabilityValue = isChecked ? 1 : 0;
            if (mAvailabilityAdapter != null && mAvailabilityValues != null
                    && mAvailabilityValues.contains(newAvailabilityValue)) {
                // We'll need to let the spinner's listener know that we're
                // explicitly toggling it.
                mAllDayChangingAvailability = true;

                String newAvailabilityLabel = mOriginalAvailabilityLabels
                        .get(newAvailabilityValue);
                int newAvailabilityPos = mAvailabilityAdapter
                        .getPosition(newAvailabilityLabel);
                mAvailabilitySpinner.setSelection(newAvailabilityPos);
            }
        }

        mAllDay = isChecked;
        updateHomeTime();
    }

    private void clearAllReminders() {
        mReminderItems.clear();
        LinearLayout reminderLayout = (LinearLayout) mScrollView
                .findViewById(R.id.reminder_items_container);
        reminderLayout.removeAllViews();
        View reminderAddButton = mView
                .findViewById(R.id.reminder_add_container);
        reminderAddButton.setVisibility(View.VISIBLE);
        reminderAddButton.setEnabled(true);
        if (mAlldayReminderMinuteValues != null) {
            addReminder();
        }
    }

    public void setColorPickerButtonStates(int[] colorArray) {
        setColorPickerButtonStates(colorArray != null && colorArray.length > 0);
    }

    public void setColorPickerButtonStates(boolean showColorPalette) {
        if (showColorPalette) {
            mColorPickerNewEvent.setVisibility(View.VISIBLE);
            mColorPickerExistingEvent.setVisibility(View.VISIBLE);
        } else {
            mColorPickerNewEvent.setVisibility(View.INVISIBLE);
            mColorPickerExistingEvent.setVisibility(View.GONE);
        }
    }

    public boolean isColorPaletteVisible() {
        return mColorPickerNewEvent.getVisibility() == View.VISIBLE
                || mColorPickerExistingEvent.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        // This is only used for the Calendar spinner in new events, and only
        // fires when the
        // calendar selection changes or on screen rotation
        Cursor c = (Cursor) parent.getItemAtPosition(position);
        if (c == null) {
            // TODO: can this happen? should we drop this check?
            Log.w(TAG, "Cursor not set on calendar item");
            return;
        }

        // Do nothing if the selection didn't change so that reminders will not
        // get lost
        int idColumn = c.getColumnIndexOrThrow(Calendars._ID);
        long calendarId = c.getLong(idColumn);
        int colorColumn = c.getColumnIndexOrThrow(Calendars.CALENDAR_COLOR);
        int color = c.getInt(colorColumn);
        int displayColor = Utils.getDisplayColorFromColor(color);

        // Prevents resetting of data (reminders, etc.) on orientation change.
        if (calendarId == mModel.mCalendarId
                && mModel.isCalendarColorInitialized()
                && displayColor == mModel.getCalendarColor()) {
            return;
        }

        // setSpinnerBackgroundColor(displayColor);
        int ownerAccountColumn = c
                .getColumnIndexOrThrow(Calendars.OWNER_ACCOUNT);
        String ownerAccount = c.getString(ownerAccountColumn);
        // TextView tv = (TextView)
        // mView.findViewById(R.id.calendar_textview_secondary);
        if (MY_CALNEDAR.equals(ownerAccount)) {
            // tv.setVisibility(View.GONE);
            mAttendeesGroup.setVisibility(View.GONE);
            mShowMeAs.setVisibility(View.GONE);
            mPrivacy.setVisibility(View.GONE);
            mAvailabilitySpinner.setVisibility(View.GONE);
            mAccessLevelSpinner.setVisibility(View.GONE);
        } else {
            mAttendeesGroup.setVisibility(View.GONE);
            if (Utils.getBuildMFVVersion(mActivity.getApplicationContext()) >= SELECT_EXEMAIL_FROM_CONTACT_VERSION) {
                mAddContactButton.setVisibility(View.VISIBLE);
            } else {
                mAddContactButton.setVisibility(View.GONE);
            }
            mShowMeAs.setVisibility(View.VISIBLE);
            mPrivacy.setVisibility(View.VISIBLE);
            mAvailabilitySpinner.setVisibility(View.VISIBLE);
            mAccessLevelSpinner.setVisibility(View.VISIBLE);
            // tv.setVisibility(View.VISIBLE);
        }

        mModel.mCalendarId = calendarId;
        mModel.setCalendarColor(displayColor);
        mModel.mCalendarAccountName = c
                .getString(EditEventHelper.CALENDARS_INDEX_ACCOUNT_NAME);
        mModel.mCalendarAccountType = c
                .getString(EditEventHelper.CALENDARS_INDEX_ACCOUNT_TYPE);
        mModel.setEventColor(mModel.getCalendarColor());

        // setColorPickerButtonStates(mModel.getCalendarEventColors());

        // Update the max/allowed reminders with the new calendar properties.
        int maxRemindersColumn = c
                .getColumnIndexOrThrow(Calendars.MAX_REMINDERS);
        mModel.mCalendarMaxReminders = c.getInt(maxRemindersColumn);
        int allowedRemindersColumn = c
                .getColumnIndexOrThrow(Calendars.ALLOWED_REMINDERS);
        mModel.mCalendarAllowedReminders = c.getString(allowedRemindersColumn);
        int allowedAttendeeTypesColumn = c
                .getColumnIndexOrThrow(Calendars.ALLOWED_ATTENDEE_TYPES);
        mModel.mCalendarAllowedAttendeeTypes = c
                .getString(allowedAttendeeTypesColumn);
        int allowedAvailabilityColumn = c
                .getColumnIndexOrThrow(Calendars.ALLOWED_AVAILABILITY);
        mModel.mCalendarAllowedAvailability = c
                .getString(allowedAvailabilityColumn);

        // Discard the current reminders and replace them with the model's
        // default reminder set.
        // We could attempt to save & restore the reminders that have been
        // added, but that's
        // probably more trouble than it's worth.
        mModel.mReminders.clear();
        mModel.mReminders.addAll(mModel.mDefaultReminders);
        mModel.mHasAlarm = mModel.mReminders.size() != 0;

        // Update the UI elements.
        mReminderItems.clear();
        LinearLayout reminderLayout = (LinearLayout) mScrollView
                .findViewById(R.id.reminder_items_container);
        reminderLayout.removeAllViews();
        prepareReminders();
        prepareAvailability();

        showEditEventColor();
    }

    /**
     * Checks if the start and end times for this event should be displayed in
     * the Calendar app's time zone as well and formats and displays them.
     */
    private void updateHomeTime() {
        String tz = Utils.getTimeZone(mActivity, null);
        if (!mAllDayCheckBox.isChecked() && !TextUtils.equals(tz, mTimezone)
                && mModification != EditEventHelper.MODIFY_UNINITIALIZED) {
            int flags = DateUtils.FORMAT_SHOW_TIME;
            boolean is24Format = DateFormat.is24HourFormat(mActivity);
            if (is24Format) {
                flags |= DateUtils.FORMAT_24HOUR;
            }
            long millisStart = mStartTime.toMillis(false);
            long millisEnd = mEndTime.toMillis(false);

            boolean isDSTStart = mStartTime.isDst != 0;
            boolean isDSTEnd = mEndTime.isDst != 0;

            // First update the start date and times
            String tzDisplay = TimeZone.getTimeZone(tz).getDisplayName(
                    isDSTStart, TimeZone.SHORT, Locale.getDefault());
            StringBuilder time = new StringBuilder();

            mSB.setLength(0);
            time.append(
                    DateUtils.formatDateRange(mActivity, mF, millisStart,
                            millisStart, flags, tz)).append(" ")
                    .append(tzDisplay);
            mStartTimeHome.setText(time.toString());

            flags = DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_SHOW_YEAR
                    | DateUtils.FORMAT_SHOW_WEEKDAY;
            mSB.setLength(0);
            mStartDateHome.setText(DateUtils.formatDateRange(mActivity, mF,
                    millisStart, millisStart, flags, tz).toString());

            // Make any adjustments needed for the end times
            if (isDSTEnd != isDSTStart) {
                tzDisplay = TimeZone.getTimeZone(tz).getDisplayName(isDSTEnd,
                        TimeZone.SHORT, Locale.getDefault());
            }
            flags = DateUtils.FORMAT_SHOW_TIME;
            if (is24Format) {
                flags |= DateUtils.FORMAT_24HOUR;
            }

            // Then update the end times
            time.setLength(0);
            mSB.setLength(0);
            time.append(
                    DateUtils.formatDateRange(mActivity, mF, millisEnd,
                            millisEnd, flags, tz)).append(" ")
                    .append(tzDisplay);
            mEndTimeHome.setText(time.toString());

            flags = DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_SHOW_YEAR
                    | DateUtils.FORMAT_SHOW_WEEKDAY;
            mSB.setLength(0);
            mEndDateHome.setText(DateUtils.formatDateRange(mActivity, mF,
                    millisEnd, millisEnd, flags, tz).toString());

            mStartHomeGroup.setVisibility(View.GONE);
            mEndHomeGroup.setVisibility(View.GONE);
        } else {
            mStartHomeGroup.setVisibility(View.GONE);
            mEndHomeGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void pickAttendeesFromContacts() {
        if (mHasContactsGrant && mHasCallLogGrant) {
            mAttendeesList.requestFocus();
            Intent intent = new Intent();
            String address = mAttendeesList.getText().toString();
            List<Rfc822Token> outList = new ArrayList<Rfc822Token>();
            Rfc822Tokenizer.tokenize(address, outList);
            try {
                intent.setType(Email.CONTENT_TYPE)
                        .setAction(
                                "com.android.contacts.action.GET_MULTIPLE_EMAILS")
                        .setClassName("com.android.contacts",
                                "com.hct.contacts.multiSelect.DataMultiSelectionActivity");
                if (outList.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < outList.size(); i++) {
                        sb.append(outList.get(i).getAddress()).append(";");
                    }
                    intent.putExtra(
                            "com.android.contacts.extra.MFV_EMAILS_DATA",
                            sb.toString());
                }
                // .setAction(Intent.ACTION_PICK)
                // .setType("vnd.android.cursor.dir/email_v2");
                try {
                    mActivity.startActivityForResult(intent, 101);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "PickAttendeesFromContacts1:" + e.getMessage());
                }
            } catch (Exception e) {
                Log.e(TAG, "PickAttendeesFromContacts2:" + e.getMessage());
            }
        }
    }

    public boolean hasContactsGrant() {
        return mHasContactsGrant;
    }

    public void setContactsGrant(boolean hasContactsGrant) {
        this.mHasContactsGrant = hasContactsGrant;
    }

    public boolean hasCallLogGrant() {
        return mHasCallLogGrant;
    }

    public void setCallLogGrant(boolean hasCallLogGrant) {
        this.mHasCallLogGrant = hasCallLogGrant;
    }

    public void setMoreEventVisibility(boolean visible) {
        if (visible) {
            mEditEvent2.setVisibility(View.VISIBLE);
            showEditEventColor();
        } else {
            mEditEvent2.setVisibility(View.GONE);
            mEditEventColor.setVisibility(View.GONE);
        }
    }

    private void showEditEventColor() {
        if (canEditEventColor()) {
            mEditEventColor.setVisibility(View.VISIBLE);
        } else {
            mEditEventColor.setVisibility(View.GONE);
        }
    }

    private boolean canEditEventColor() {
        if (!Utils.isAbroadBranch(mActivity)) {
            if (mModel != null
                    && ("My calendar".equals(mModel.mCalendarAccountType) || mModel.mCalendarAccountType == null)) {
                return true;
            }
        }
        return false;
    }

    public DateTimePickerDialogHCT_from getmDtpdInstall() {
        return mDtpdInstall;
    }

    public void releaseDtpdInstall() {
        if (mDtpdInstall != null) {
            mDtpdInstall.dismiss();
            mDtpdInstall = null;
        }
    }

    public void setSaveMap(Context context) {
        saveMap(BitmapFactory.decodeFile(mMapScreenshotFile), context,
                mLocationTextString);
        locationMap = MAP_DIR + "/" + mLocationTextString + ".jpg";
        Log.d(TAG, "locationMap = " + locationMap);
        File targetFile = new File(locationMap);
        if (targetFile.exists()) {
            Bitmap bitmap = getLoacalBitmap(locationMap);
            if (bitmap != null && mMap != null) {
                BitmapDrawable bmpDraw = new BitmapDrawable(bitmap);
                mMap.setImageDrawable(bmpDraw);
            }
        }
        mMapContainer.setVisibility(View.VISIBLE);
    }

    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private void hideInputAndclearFoucs() {
        if (mActivity != null) {
            final View focusedView = mActivity.getCurrentFocus();
            if (focusedView != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) mActivity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        focusedView.getWindowToken(), 0);
                focusedView.clearFocus();
            }
        }
    }
}
