package com.xinyang.alienware.gaodeiosstyle.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Alienware on 2017/12/7.
 * 禁止左右滑动的ViewPager
 */

public class NoScrollViewPager extends ViewPager {
    private boolean noScroll = false;



    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public  void setNoScroll(boolean noScroll){
        this.noScroll = noScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (noScroll){
            return super.onTouchEvent(ev);

        }else {
            return  false;

        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (noScroll){

            return super.onInterceptTouchEvent(ev);
        }else {
            return false;

        }
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }
}
