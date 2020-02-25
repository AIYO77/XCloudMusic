package com.xmusic.module_search.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class BaseSearchAdapter<T>(DIFF: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, RecyclerView.ViewHolder>(DIFF) {
    var keywords: String = ""
}