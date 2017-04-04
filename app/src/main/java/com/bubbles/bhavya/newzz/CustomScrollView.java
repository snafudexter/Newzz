package com.bubbles.bhavya.newzz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by prabh on 12/22/2015.
 */
public class CustomScrollView extends ScrollView
{
    OnBottomReachedListener mListener;
    //this is some good work

    public CustomScrollView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context) {
        super(context);
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount()-1);
        int diff = (view.getBottom()-(getHeight()+getScrollY()));

        if (diff == 0 && mListener != null) {
            mListener.onBottomReached();
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }



    public OnBottomReachedListener getOnBottomReachedListener() {
        return mListener;
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        mListener = onBottomReachedListener;
    }

    public interface OnBottomReachedListener{
        public void onBottomReached();
    }

}
