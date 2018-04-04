package gm.widget;


import java.util.List;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by chenchen on 2017/5/8.
 */

public class GomeSwipeMenuView extends LinearLayout implements OnClickListener {
    private gm.widget.GomeListView mListView;
    private gm.widget.GomeSwipeMenuLayout mLayout;
    private gm.widget.GomeSwipeMenu mMenu;
    private OnSwipeItemClickListener onItemClickListener;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public GomeSwipeMenuView(gm.widget.GomeSwipeMenu menu, gm.widget.GomeListView listView) {
        super(menu.getContext());
        mListView = listView;
        mMenu = menu;
        List<gm.widget.GomeSwipeMenuItem> items = menu.getMenuItems();
        int id = 0;
        for (gm.widget.GomeSwipeMenuItem item : items) {
            addItem(item, id++);
        }
    }

    private void addItem(gm.widget.GomeSwipeMenuItem item, int id) {
        LayoutParams params = new LayoutParams(item.getWidth(),
                LayoutParams.MATCH_PARENT);
        LinearLayout parent = new LinearLayout(getContext());
        parent.setId(id);
        parent.setGravity(Gravity.CENTER);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        parent.setBackgroundDrawable(item.getBackground());
        parent.setOnClickListener(this);
        addView(parent);

        if (item.getIcon() != null) {
            parent.addView(createIcon(item));
        }
        if (!TextUtils.isEmpty(item.getTitle())) {
            parent.addView(createTitle(item));
        }

    }

    private ImageView createIcon(gm.widget.GomeSwipeMenuItem item) {
        ImageView iv = new ImageView(getContext());
        iv.setImageDrawable(item.getIcon());
        return iv;
    }

    private TextView createTitle(GomeSwipeMenuItem item) {
        TextView tv = new TextView(getContext());
        tv.setText(item.getTitle());
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(item.getTitleSize());
        tv.setTextColor(item.getTitleColor());
        return tv;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null && mLayout.isOpen()) {
            onItemClickListener.onItemClick(this, mMenu, v.getId());
        }
    }

    public OnSwipeItemClickListener getOnSwipeItemClickListener() {
        return onItemClickListener;
    }

    public void setOnSwipeItemClickListener(OnSwipeItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setLayout(GomeSwipeMenuLayout mLayout) {
        this.mLayout = mLayout;
    }

    public static interface OnSwipeItemClickListener {
        void onItemClick(GomeSwipeMenuView view, GomeSwipeMenu menu, int index);
    }
}
