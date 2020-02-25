package com.xw.lib_common.utils

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xw.lib_common.R
import com.xw.lib_common.ext.getColor


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class GridItemDecoration(
    private val mHorizonSpan: Int = 0,
    private val mVerticalSpan: Int = 0,
    color: Int = getColor(R.color.transparent),
    private val mShowLastLine: Boolean = false
) : RecyclerView.ItemDecoration() {

    private var mDivider: Drawable = ColorDrawable(color)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val spanCount = getSpanCount(parent)
        val childCount: Int = parent.adapter?.itemCount ?: 0
        val itemPosition =
            (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition

        if (itemPosition < 0) {
            return
        }
        val column = itemPosition % spanCount
        val bottom: Int
        val left = column * mVerticalSpan / spanCount
        val right = mVerticalSpan - (column + 1) * mVerticalSpan / spanCount

        bottom = if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
            if (mShowLastLine) {
                mHorizonSpan
            } else {
                0
            }
        } else {
            mHorizonSpan
        }
        outRect[left, bottom, right] = 0

    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            //最后一行底部横线不绘制
            if (isLastRaw(parent, i, getSpanCount(parent), childCount) && !mShowLastLine) {
                continue
            }
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            val left: Int = child.left - params.leftMargin
            val right: Int = child.right + params.rightMargin
            val top: Int = child.bottom + params.bottomMargin
            val bottom: Int = top + mHorizonSpan
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if ((parent.getChildViewHolder(child).adapterPosition + 1) % getSpanCount(parent) == 0) {
                continue
            }
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin + mHorizonSpan
            val left = child.right + params.rightMargin
            var right = left + mVerticalSpan
            //            //满足条件( 最后一行 && 不绘制 ) 将vertical多出的一部分去掉;
            if (i == childCount - 1) {
                right -= mVerticalSpan
            }
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    /**
     * 获取列数
     */
    private fun getSpanCount(parent: RecyclerView): Int { // 列数
        var mSpanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            mSpanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            mSpanCount = layoutManager.spanCount
        }
        return mSpanCount
    }

    /**
     * 是否最后一行
     * @param parent     RecyclerView
     * @param pos        当前item的位置
     * @param spanCount  每行显示的item个数
     * @param childCount child个数
     */
    private fun isLastRaw(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            return getResult(pos, spanCount, childCount)
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager.orientation
            if (orientation == StaggeredGridLayoutManager.VERTICAL) { // StaggeredGridLayoutManager 且纵向滚动
                return getResult(pos, spanCount, childCount)
            } else { // StaggeredGridLayoutManager 且横向滚动
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            }
        }
        return false
    }

    private fun getResult(pos: Int, spanCount: Int, childCount: Int): Boolean {
        val remainCount = childCount % spanCount //获取余数
        //如果正好最后一行完整;
        if (remainCount == 0) {
            if (pos >= childCount - spanCount) {
                return true //最后一行全部不绘制;
            }
        } else {
            if (pos >= childCount - childCount % spanCount) {
                return true
            }
        }
        return false
    }

}