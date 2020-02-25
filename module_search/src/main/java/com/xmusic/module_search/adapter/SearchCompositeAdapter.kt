package com.xmusic.module_search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xw.lib_coremodel.model.bean.search.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 搜索结果:综合
 */
class SearchCompositeAdapter : BaseSearchAdapter<CompositeCommon>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemViewType(position: Int): Int {
        when (getItem(position)) {
            is CompositeSong -> {
                return TYPE_SONG
            }
            is CompositePlayList -> {
                return TYPE_PLAY_LIST
            }
            is CompositeVideo -> {
                return TYPE_VIDEO
            }
            is CompositeSimQuery -> {
                return TYPE_SIM_QUERY
            }
            is CompositeArtist -> {
                return TYPE_ARTIST
            }
            is CompositeAlbum -> {
                return TYPE_ALBUM
            }
            is CompositeDjRadio -> {
                return TYPE_DJRADIO
            }
            is CompositeUser -> {
                return TYPE_USER
            }
            else -> {
                return super.getItemViewType(position)
            }
        }
    }

    companion object {
        const val TYPE_SONG = 1
        const val TYPE_PLAY_LIST = 2
        const val TYPE_VIDEO = 3
        const val TYPE_SIM_QUERY = 4
        const val TYPE_ARTIST = 5
        const val TYPE_ALBUM = 6
        const val TYPE_DJRADIO = 7
        const val TYPE_USER = 8
        val DIFF = object : DiffUtil.ItemCallback<CompositeCommon>() {
            override fun areItemsTheSame(
                oldItem: CompositeCommon,
                newItem: CompositeCommon
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CompositeCommon,
                newItem: CompositeCommon
            ): Boolean {
                return oldItem.resourceIds == newItem.resourceIds
            }

        }
    }
}