package com.xmusic.module_search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.xmusic.module_search.adapter.viewholder.SearchSongViewHolder
import com.xmusic.module_search.databinding.ItemSearchSongBinding
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.home.SongInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchSongsAdapter : BaseSearchAdapter<Song>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchSongViewHolder(
            ItemSearchSongBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as SearchSongViewHolder) {
            val song = getItem(position)
            Logger.i(song.name)
            bind(song,keywords, createVideoClick(), createMoreClick())
            itemView.setOnClickListener {
            }
        }
    }

    private fun createMoreClick(): View.OnClickListener {
        return View.OnClickListener {

        }
    }

    private fun createVideoClick(): View.OnClickListener {
        return View.OnClickListener {

        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

}
