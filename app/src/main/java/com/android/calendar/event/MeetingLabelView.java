package com.android.calendar.event;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.calendar.R;

/**
 * Created by liaozhenbin on 2017/6/24.
 */

public class MeetingLabelView extends LinearLayout {
    public TextView meetingPersonName;
    public View deleteIcon;

    public MeetingLabelView(Context context) {
        super(context);
        init(context);
    }

    public MeetingLabelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MeetingLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init (final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.meeting_label_view,this);
        meetingPersonName = (TextView) view.findViewById(R.id.tv_meeting_person);
        deleteIcon = view.findViewById(R.id.iv_delete);

    }

}
