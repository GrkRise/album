package ru.fegol.album2.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AbsListView;

import ru.fegol.album2.callbacks.OnDetectScrollListener;

/**
 * Created by Андрей on 21.06.2015.
 */
public class DetectScroll implements AbsListView.OnScrollListener{
    private int mLastScrollY;
    private int mPreviousFirstVisibleItem;
    private AbsListView mListView;
    private int mScrollThreshold;
    private OnDetectScrollListener mOnDetectScrollListener;


    public DetectScroll(AbsListView mListView, int mScrollThreshold, OnDetectScrollListener mOnDetectScrollListener) {
        this.mListView = mListView;
        this.mScrollThreshold = mScrollThreshold;
        this.mOnDetectScrollListener = mOnDetectScrollListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(@NonNull AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(totalItemCount == 0) return;
        if (isSameRow(firstVisibleItem)) {
            int newScrollY = getTopItemScrollY();
            boolean isSignificantDelta = Math.abs(mLastScrollY - newScrollY) > mScrollThreshold;
            if (isSignificantDelta) {
                if (mLastScrollY > newScrollY) {
                    mOnDetectScrollListener.onScrollUp();
                } else {
                    mOnDetectScrollListener.onScrollDown();
                }
            }
            mLastScrollY = newScrollY;
        } else {
            if (firstVisibleItem > mPreviousFirstVisibleItem) {
                mOnDetectScrollListener.onScrollUp();
            } else {
                mOnDetectScrollListener.onScrollDown();
            }

            mLastScrollY = getTopItemScrollY();
            mPreviousFirstVisibleItem = firstVisibleItem;
        }
    }

    private boolean isSameRow(int firstVisibleItem) {
        return firstVisibleItem == mPreviousFirstVisibleItem;
    }

    private int getTopItemScrollY() {
        if (mListView == null || mListView.getChildAt(0) == null) return 0;
        View topChild = mListView.getChildAt(0);
        return topChild.getTop();
    }
}
