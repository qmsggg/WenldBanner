package com.wenld.wenldbanner.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.wenld.wenldbanner.LoopViewPager;
import com.wenld.wenldbanner.helper.Holder;
import com.wenld.wenldbanner.helper.ViewHolder;

import java.util.LinkedList;
import java.util.List;

/**
 * <p/>
 * Author: 温利东 on 2017/11/2 16:38.
 * blog: http://blog.csdn.net/sinat_15877283
 * github: https://github.com/LidongWen
 */

public class WenldPagerAdapter<T> extends PagerAdapter {
    final String TAG = "WenldPagerAdapter";
    protected List<T> mDatas;
    Holder holderCreator;
    private boolean canLoop = true;
    LoopViewPager wenldViewPager;
    private LinkedList<ViewHolder> mViewHolderCache = null;
    private LinkedList<ViewHolder> mViewHolderUsedCache = null;

    @Override
    public int getCount() {
        return canLoop ? (getRealCount() > 1 ? getRealCount() + 2 : getRealCount()) : getRealCount();
    }

    public int getRealCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(position, container);
        container.addView(view);
        return view;
    }

//    @Override
//    public float getPageWidth(int position) {
//        return 0.7f;
//    }
    //    @Override
//    public void finishUpdate(ViewGroup container) {
//        int position = wenldViewPager.getCurrentItem();
//        Log.e(TAG, String.format("finishUpdate : %s", position));
//
//        position = changedAdapterPostiton(position);
//
//        try {
//            wenldViewPager.setCurrentItem(position, false);
//        } catch (IllegalStateException e) {
//
//        }
//    }

    public int realPostiton2AdapterPostiton(int position) {
        if (canLoop) {
            int realCount = getRealCount();
            if (realCount == 0)
                return 0;
            return position + 1;
        }
        return position;
    }

    /**
     * 返回真实数据的下标
     *
     * @param adapterPosition
     * @return
     */
    public int adapterPostiton2RealDataPosition(int adapterPosition) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;

        if (canLoop) {
            // 如果只有一个数据
            if (realCount == 1) {
                return 0;
            } else {
                // 第0个即最后一个
                if (adapterPosition == 0) {
                    return realCount - 1;
                    // 最后一个 即第0个数据
                } else if (adapterPosition == getCount() - 1) {
                    return 0;
                } else {
                    return adapterPosition - 1;
                }
            }
        }
        int realPosition = adapterPosition % realCount;
        return realPosition;
    }

    /**
     * 头尾下标转换
     *
     * @param position
     * @return
     */
    public int headFootPosition2AdapterPosition(int position) {

        if (canLoop && getRealCount() > 1) {
            if (position == 0) {
                return getRealCount();
            } else if (position == getCount() - 1) {
                return 1;
            }
        }
        return position;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);

        ViewHolder viewHolder = null;
        for (int i = mViewHolderUsedCache.size() - 1; i >= 0; i--) {
            viewHolder = mViewHolderUsedCache.get(i);

            if (viewHolder.getPosition() == position) {
                mViewHolderUsedCache.remove(viewHolder);
                break;
            }
            viewHolder = null;
        }
        if (viewHolder != null) {
            mViewHolderCache.add(viewHolder);
        }
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }

    public void setViewPager(LoopViewPager viewPager) {
        this.wenldViewPager = viewPager;
    }

    public WenldPagerAdapter(Holder holderCreator) {
        this.holderCreator = holderCreator;
        mViewHolderCache = new LinkedList<>();
        mViewHolderUsedCache = new LinkedList<>();
    }

    private WenldPagerAdapter(Holder holderCreator, List<T> datas) {
        this.holderCreator = holderCreator;
        this.mDatas = datas;
    }

    public void setmDatas(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    public List<T> getmDatas() {
        return mDatas;
    }

    public View getView(int position, ViewGroup container) {
        ViewHolder holder = null;
        int realPosition = adapterPostiton2RealDataPosition(position);
        int viewType = holderCreator.getViewType(realPosition);

        for (int i = mViewHolderCache.size() - 1; i >= 0; i--) {
            if (mViewHolderCache.get(i).getViewType() == viewType && mViewHolderCache.get(i).getPosition() == position) {
                holder = mViewHolderCache.get(i);
                mViewHolderCache.remove(holder);
                break;
            }
        }
        if (holder == null) {
            for (int i = mViewHolderCache.size() - 1; i >= 0; i--) {
                if (mViewHolderCache.get(i).getViewType() == viewType) {
                    holder = mViewHolderCache.get(i);
                    mViewHolderCache.remove(holder);
                    break;
                }
            }
        }

        if (holder == null) {
            holder = holderCreator.createView(wenldViewPager.getContext(), container, realPosition);
        }
        mViewHolderUsedCache.add(holder);

        if (mDatas != null && !mDatas.isEmpty()) {
            holderCreator.UpdateUI(container.getContext(), holder, realPosition, mDatas.get(realPosition));
            if (position != holder.getPosition()) {
                // 恢复一下状态
            }
        }
        holder.setPosition(position);
        return holder.getConvertView();
    }
}
