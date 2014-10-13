package com.oyster.lab02.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * stolen from facebook here :
 * https://github.com/facebook/facebook-android-sdk/blob/master/samples/Scrumptious/src/com/facebook/scrumptious/FullListView.java
 * <p/>
 * !!!!!!!!  FUCKING SHIT !!!!!!!!!!!!
 * DO NOT USE RelativeLayout with this class, MeasureSpec.makeMeasureSpec docs
 * <p/>
 * Created by andriybas on 10/6/14.
 */
public class FullListView extends ListView {

    public FullListView(Context context) {
        super(context);
    }

    public FullListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FullListView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = 0;
        ListAdapter adapter = getAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View childView = adapter.getView(i, null, this);
            childView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height += childView.getMeasuredHeight();
        }
        height += getDividerHeight() * (count - 1);
        setMeasuredDimension(width, height);
    }
}
