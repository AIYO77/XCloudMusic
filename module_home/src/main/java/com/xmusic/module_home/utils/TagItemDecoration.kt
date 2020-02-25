package com.xmusic.module_home.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class TagItemDecoration(private val mSpace: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        view.tag = position
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val gridLayoutManager: GridLayoutManager = layoutManager
            val spanSizeLookup = gridLayoutManager.spanSizeLookup
            val spanCount: Int = gridLayoutManager.spanCount
            val spanIndex: Int = spanSizeLookup.getSpanIndex(position, spanCount)
            val spanSize: Int = spanSizeLookup.getSpanSize(position)
            if (spanSize == 1) {
                outRect.top = mSpace
                if (spanIndex == spanCount) { //占满
                    outRect.left = mSpace
                    outRect.right = mSpace
                } else {
                    outRect.left =
                        ((spanCount - spanIndex).toFloat() / spanCount * mSpace).toInt()
                    outRect.right =
                        (mSpace.toFloat() * (spanCount + 1) / spanCount - outRect.left).toInt()
                }
            } else {
                outRect.bottom = -mSpace
            }
        }
    }

}