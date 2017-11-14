package com.wenld.wenldbanner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.wenld.wenldbanner.adapter.WenldPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Author: 温利东 on 2017/11/2 16:34.
 * blog: http://www.jianshu.com/u/99f514ea81b3
 * github: https://github.com/LidongWen
 */

public class LoopViewPager extends ViewPager {
    final String TAG = "LoopViewPager";
    private List<OnPageChangeListener> mOuterPageChangeListeners;
    private WenldPagerAdapter mAdapter;

    private boolean isTouchScroll = true;
    private boolean canLoop = true;

    public void setAdapter(WenldPagerAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setCanLoop(canLoop);
        mAdapter.setViewPager(this);
        super.setAdapter(mAdapter);

        setCurrentItem(0, false);
    }

    public boolean isTouchScroll() {
        return isTouchScroll;
    }

    public void setTouchScroll(boolean isCanScroll) {
        this.isTouchScroll = isCanScroll;
    }


    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(mAdapter.realPostiton2AdapterPostiton(item));
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(mAdapter.realPostiton2AdapterPostiton(item), smoothScroll);
    }

    public int getSuperCurrentItem() {
        return super.getCurrentItem();
    }

    @Override
    public int getCurrentItem() {
        return mAdapter.adapterPostiton2RealDataPosition(super.getCurrentItem());
    }

    int getRealItem(int position) {
        return mAdapter.adapterPostiton2RealDataPosition(position);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isTouchScroll) {
            return super.onTouchEvent(ev);
        } else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isTouchScroll)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    public WenldPagerAdapter getAdapter() {
        return mAdapter;
    }


    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        getmOuterPageChangeListeners().add(listener);
    }

    List<OnPageChangeListener> getmOuterPageChangeListeners() {
        if (mOuterPageChangeListeners == null) {
            mOuterPageChangeListeners = new ArrayList<>();
        }
        return mOuterPageChangeListeners;
    }

    @Override
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        getmOuterPageChangeListeners().remove(listener);
    }

    public LoopViewPager(Context context) {
        super(context);
        init();
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.addOnPageChangeListener(onPageChangeListener);
    }

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {

            int realPosition = getRealItem(position);
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                MyLog.d(TAG, String.format("onPageSelected mPreviousPosition  %s", mPreviousPosition));
                for (int i = 0; i < getmOuterPageChangeListeners().size(); i++) {
                    getmOuterPageChangeListeners().get(i).onPageSelected(realPosition);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            int realPosition = position;
            realPosition = mAdapter.adapterPostiton2RealDataPosition(realPosition);
            for (int i = 0; i < getmOuterPageChangeListeners().size(); i++) {
                getmOuterPageChangeListeners().get(i).onPageScrolled(realPosition,
                        positionOffset, positionOffsetPixels);
            }
//            if (mOuterPageChangeListener != null) {
//                mOuterPageChangeListener.onPageScrolled(realPosition,
//                        positionOffset, positionOffsetPixels);
//                if (realPosition != mAdapter.getRealCount() - 1) {
//                    mOuterPageChangeListener.onPageScrolled(realPosition,
//                            positionOffset, positionOffsetPixels);
//                } else {
//                    if (positionOffset > .5) {
//                        mOuterPageChangeListener.onPageScrolled(0, 0, 0);
//                    } else {
//                        mOuterPageChangeListener.onPageScrolled(realPosition,
//                                0, 0);
//                    }
//                }
//            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == SCROLL_STATE_IDLE) {
                //如果是首尾 就更换位置
                int currentItem = getSuperCurrentItem();
                int realAdapterPosition = mAdapter.headFootPosition2AdapterPosition(currentItem);
                if (currentItem != realAdapterPosition) {
                    setCurrentItem(getRealItem(currentItem), false);
                }
            }
            for (int i = 0; i < getmOuterPageChangeListeners().size(); i++) {
                getmOuterPageChangeListeners().get(i).onPageScrollStateChanged(state);
            }
        }
    };

    public boolean isCanLoop() {
        return canLoop;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        if (mAdapter == null) return;
        int position = getCurrentItem();

        MyLog.d(TAG, String.format("setCanLoop currentPosition:%s realPosition :%s getScrollX():%s", getSuperCurrentItem(), position, getScrollX()));

        mAdapter.setCanLoop(canLoop);
        mAdapter.myNotify = true;
        mAdapter.notifyDataSetChanged();
        MyLog.d(TAG, String.format("setCanLoop in setCurrentItem currentPosition:%s realPosition :%s,getScrollX():%s", getSuperCurrentItem(), getCurrentItem(), getScrollX()));
        setCurrentItem(position, false);

        MyLog.d(TAG, String.format("setCanLoop setCurrentItem after currentPosition:%s realPosition :%s,getScrollX():%s", getSuperCurrentItem(), getCurrentItem(), getScrollX()));
        mAdapter.myNotify = false;
    }

    PageTransformer transformer;

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        this.transformer = transformer;
        super.setPageTransformer(reverseDrawingOrder, transformer);
    }

//    void test() {
//        if(transformer==null)return;
//        final int childCount = getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            final View child = getChildAt(i);
//            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
////            for (int i = 0; i < mItems.size(); i++) {
////                final ItemInfo ii = mItems.get(i);
////            mPageTransformer.transformPage(child, 0);
//        }
//    }

    public void setOnItemClickListener(OnPageClickListener onItemClickListener) {
        if (mAdapter == null)
            mAdapter.setOnItemClickListener(onItemClickListener);
    }
}
