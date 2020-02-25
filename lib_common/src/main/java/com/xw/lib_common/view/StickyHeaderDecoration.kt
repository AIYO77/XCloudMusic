package com.xw.lib_common.view

import android.graphics.Canvas
import android.graphics.Rect
import android.util.LongSparseArray
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.util.contains
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.xw.lib_common.adapter.StickyHeaderAdapter
import com.xw.lib_common.ext.toast

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class StickyHeaderDecoration(val adapter: StickyHeaderAdapter<RecyclerView.ViewHolder>) :
    RecyclerView.ItemDecoration() {
    companion object {
        private const val NO_HEADER_ID = -1L
    }

    private var headerCache = LongSparseArray<RecyclerView.ViewHolder>()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        var headerHeight = 0

        if (position != RecyclerView.NO_POSITION && hasHeader(position)
            && showHeaderAboveItem(position)
        ) {
            val header: View = getHeader(parent, position).itemView
            headerHeight = getHeaderHeightForLayout(header)
        }

        outRect[0, headerHeight, 0] = 0
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val count = parent.childCount
        var previousHeaderId: Long = -1
        for (layoutPos in 0 until count) {
            val child = parent.getChildAt(layoutPos)
            val adapterPos = parent.getChildAdapterPosition(child)
            if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
                val headerId = adapter.getHeaderId(adapterPos)
                if (headerId != previousHeaderId) {
                    previousHeaderId = headerId
                    val header = getHeader(parent, adapterPos).itemView
                    canvas.save()
                    val left = child.left
                    val top: Int = getHeaderTop(parent, child, header, adapterPos, layoutPos)
                    canvas.translate(left.toFloat(), top.toFloat())
                    header.translationX = left.toFloat()
                    header.translationY = top.toFloat()
                    header.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    private fun getHeaderHeightForLayout(header: View): Int {
        return header.height
    }

    private fun hasHeader(position: Int): Boolean {
        return adapter.getHeaderId(position) != NO_HEADER_ID
    }

    private fun getHeaderTop(
        parent: RecyclerView, child: View,
        header: View, adapterPos: Int, layoutPos: Int
    ): Int {
        val headerHeight = getHeaderHeightForLayout(header)
        var top = child.y.toInt() - headerHeight
        if (layoutPos == 0) {
            val count = parent.childCount
            val currentId = adapter.getHeaderId(adapterPos)
            // find next view with header and compute the offscreen push if needed
            for (i in 1 until count) {
                val adapterPosHere = parent.getChildAdapterPosition(parent.getChildAt(i))
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    val nextId = adapter.getHeaderId(adapterPosHere)
                    if (nextId != currentId) {
                        val next = parent.getChildAt(i)
                        val offset = next.y.toInt() - (headerHeight +
                                getHeader(parent, adapterPosHere).itemView.height)
                        return if (offset < 0) {
                            offset
                        } else {
                            break
                        }
                    }
                }
            }
            top = 0.coerceAtLeast(top)
        }
        return top
    }

    private fun showHeaderAboveItem(itemAdapterPosition: Int): Boolean {
        return if (itemAdapterPosition == 0) {
            true
        } else adapter.getHeaderId(itemAdapterPosition - 1) != adapter.getHeaderId(
            itemAdapterPosition
        )
    }

    private fun getHeader(parent: RecyclerView, position: Int): RecyclerView.ViewHolder {
        val key = adapter.getHeaderId(position)
        return if (headerCache.contains(key)) {
            headerCache[key]!!
        } else {
            val holder = adapter.onCreateHeaderViewHolder(parent, position)
            val header = holder.itemView
            adapter.onBindHeaderViewHolder(holder, position)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredWidth,
                View.MeasureSpec.EXACTLY
            )
            val heightSpec = View.MeasureSpec.makeMeasureSpec(
                parent.measuredHeight,
                View.MeasureSpec.UNSPECIFIED
            )
            val childWidth = ViewGroup.getChildMeasureSpec(
                widthSpec,
                parent.paddingLeft + parent.paddingRight,
                header.layoutParams.width
            )
            val childHeight = ViewGroup.getChildMeasureSpec(
                heightSpec,
                parent.paddingTop + parent.paddingBottom,
                header.layoutParams.height
            )
            header.measure(childWidth, childHeight)
            header.layout(0, 0, header.measuredWidth, header.measuredHeight)
            headerCache[key] = holder
            holder
        }
    }

}

