package com.triamatter.mynub.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class EmptyRecyclerView extends RecyclerView {
    private View mEmptyView;
    private View mEmptyViewIcon;

    public EmptyRecyclerView(Context context)
    {
        super(context);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void initEmptyView()
    {
        if (mEmptyView != null)
        {
            Log.i("EMPTYVIEW", "" + getAdapter().getItemCount());
            mEmptyView.setVisibility(getAdapter() == null || getAdapter().getItemCount() == 0 ? VISIBLE : GONE);
//            EmptyRecyclerView.this.setVisibility(
//                    getAdapter() == null || getAdapter().getItemCount() == 0 ? GONE : VISIBLE);
        }
        else
        {
            Log.i("EMPTYVIEW", "Empty!");
        }

        if (mEmptyViewIcon != null)
        {
            mEmptyViewIcon.setVisibility(getAdapter() == null || getAdapter().getItemCount() == 0 ? VISIBLE : GONE);
        }
    }

    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged()
        {
            super.onChanged();
            initEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            super.onItemRangeInserted(positionStart, itemCount);
            initEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            super.onItemRangeRemoved(positionStart, itemCount);
            initEmptyView();
        }
    };

    @Override
    public void setAdapter(Adapter adapter)
    {
        Adapter oldAdapter = getAdapter();
        super.setAdapter(adapter);
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(View view)
    {
        this.mEmptyView = view;
        initEmptyView();
    }

    public void setEmptyViewIcon(View mEmptyViewIcon)
    {
        this.mEmptyViewIcon = mEmptyViewIcon;
        initEmptyView();
    }
}