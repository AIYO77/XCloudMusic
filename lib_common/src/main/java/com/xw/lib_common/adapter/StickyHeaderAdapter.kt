package com.xw.lib_common.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
interface StickyHeaderAdapter<T : RecyclerView.ViewHolder> {

    fun getHeaderId(position: Int): Long

    fun onCreateHeaderViewHolder(parent: ViewGroup,position: Int): T

    fun onBindHeaderViewHolder(viewHolder: T, position: Int)
}