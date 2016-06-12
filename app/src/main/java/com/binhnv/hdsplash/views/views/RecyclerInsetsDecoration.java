package com.binhnv.hdsplash.views.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.binhnv.hdsplash.R;

/**
 * Created by sev_user on 1/29/2016.
 */
public class RecyclerInsetsDecoration extends RecyclerView.ItemDecoration{
    private int mInsets;

    public RecyclerInsetsDecoration(Context context) {
        mInsets = context.getResources().getDimensionPixelSize(R.dimen.insets);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        //We can supply forced insets for each item view here in the Rect
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mInsets, mInsets, mInsets, mInsets);
    }
}
