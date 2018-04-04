package gm.app;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class GomeMaxListView extends ListView {

    private int listViewHeight;

    public int getListViewHeight() {
        return listViewHeight;
    }

    public void setListViewHeight(int listViewHeight) {
        this.listViewHeight = listViewHeight;
    }

    public GomeMaxListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public GomeMaxListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public GomeMaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        if (listViewHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(listViewHeight,
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
