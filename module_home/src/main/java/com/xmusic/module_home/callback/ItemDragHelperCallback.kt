package com.xmusic.module_home.callback

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xmusic.module_home.adapter.TagsManagerAdapter

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class ItemDragHelperCallback : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags: Int
        //针对特定标记不能拖拽
        //针对特定标记不能拖拽
        if (viewHolder is TagsManagerAdapter.MyTagViewHolder) {
            if (viewHolder.isResident) {
                return 0
            }
        }
        val manager = recyclerView.layoutManager
        dragFlags = if (manager is GridLayoutManager || manager is StaggeredGridLayoutManager) {
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        } else {
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }
        val swipeFlags = 0
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // 不同Type之间不可移动
        // 不同Type之间不可移动
        if (viewHolder.itemViewType != target.itemViewType) {
            return false
        }

        //针对特定标记不能拖拽
        //针对特定标记不能拖拽
        if (target is TagsManagerAdapter.MyTagViewHolder) {
            if (target.isResident) {
                return false
            }
        }

        if (recyclerView.adapter is OnItemMoveListener) {
            val listener = recyclerView.adapter as OnItemMoveListener?
            listener!!.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        }
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        // 不在闲置状态
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is OnDragVHListener) {
                val itemViewHolder = viewHolder as OnDragVHListener
                itemViewHolder.onItemSelected()
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is OnDragVHListener) {
            val itemViewHolder = viewHolder as OnDragVHListener
            itemViewHolder.onItemFinish()
        }
        super.clearView(recyclerView, viewHolder)
    }

    /**
     * 该方法返回true时，表示支持长按拖动，即长按ItemView后才可以拖动，我们遇到的场景一般也是这样的。默认是返回true。
     */
    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    /**
     * 针对swipe状态，是否允许swipe(滑动)操作
     * 该方法返回true时，表示如果用户触摸并左右滑动了View，那么可以执行滑动删除操作，即可以调用到onSwiped()方法。默认是返回true
     */
    override fun isItemViewSwipeEnabled(): Boolean { // 不支持滑动功能
        return false
    }

}