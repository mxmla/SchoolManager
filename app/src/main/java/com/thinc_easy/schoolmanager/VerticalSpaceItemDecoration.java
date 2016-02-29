package com.thinc_easy.schoolmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by M on 01.09.2015.
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;
    private final int mVerticalSpaceHeightTop;
    private final int mVerticalSpaceHeightBottom;

    public VerticalSpaceItemDecoration(int mVerticalSpaceHeight, int mVerticalSpaceHeightTop, int mVerticalSpaceHeightBottom) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        this.mVerticalSpaceHeightTop = mVerticalSpaceHeightTop;
        this.mVerticalSpaceHeightBottom = mVerticalSpaceHeightBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = mVerticalSpaceHeightBottom;
        } else if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mVerticalSpaceHeightTop;
            outRect.bottom = mVerticalSpaceHeight;
        } else {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }
}
