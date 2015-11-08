package com.msrproduction.baseballmanager.plugins;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import com.msrproduction.baseballmanager.R;

public class ListView extends android.widget.ListView {

	private OnScrollListener onScrollListener;
	private OnDetectScrollListener onDetectScrollListener;
	private int mLastScrollY;
	private int mPreviousFirstVisibleItem;
	private int mScrollThreshold = getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold);

	public ListView(Context context) {
		super(context);
		onCreate(context, null, null);
	}

	public ListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate(context, attrs, null);
	}

	public ListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		onCreate(context, attrs, defStyle);
	}

	@SuppressWarnings("UnusedParameters")
	private void onCreate(Context context, AttributeSet attrs, Integer defStyle) {
		setListeners();
	}

	private void setListeners() {
		super.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (onScrollListener != null) {
					onScrollListener.onScrollStateChanged(view, scrollState);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalItemCount == 0) return;
				if (isSameRow(firstVisibleItem)) {
					int newScrollY = 0;
					boolean isSignificantDelta = Math.abs(mLastScrollY - newScrollY) > mScrollThreshold;
					if (isSignificantDelta) {
						if (mLastScrollY > newScrollY) {
							onDetectScrollListener.onDownScrolling();
						} else {
							onDetectScrollListener.onUpScrolling();
						}
					}
					mLastScrollY = newScrollY;
				} else {
					if (firstVisibleItem > mPreviousFirstVisibleItem) {
						onDetectScrollListener.onDownScrolling();
					} else {
						onDetectScrollListener.onUpScrolling();
					}

					mPreviousFirstVisibleItem = firstVisibleItem;
				}
			}

			private boolean isSameRow(int firstVisibleItem) {
				return firstVisibleItem == mPreviousFirstVisibleItem;
			}
		});
	}

	@Override
	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	public void setOnDetectScrollListener(OnDetectScrollListener onDetectScrollListener) {
		this.onDetectScrollListener = onDetectScrollListener;
	}
}