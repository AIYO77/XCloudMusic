package com.xmusic.module_home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.xw.lib_common.databinding.ItemPlayListBinding
import com.xw.lib_common.databinding.ItemRecdDailyListBinding
import com.xw.lib_common.ext.toMusicInfo
import com.xw.lib_common.ext.toast
import com.xw.lib_common.service.MusicPlayer
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.UserInfo
import com.xw.lib_coremodel.model.bean.home.Privilege
import com.xw.lib_coremodel.model.bean.info.MusicInfo
import com.xmusic.module_home.view.SubscribersView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListAdapter constructor(val type: Int = TYPE_ADAPTER_NUMBER) :
    ListAdapter<Song, RecyclerView.ViewHolder>(PLAY_LIST_DIFF_CALLBACK) {

    var mSubscribers: List<UserInfo>? = null
    var mSubscribedCount: Long = 0
    var mSubscribed = false

    private var privileges: List<Privilege> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NORMAL -> {
                if (type == TYPE_ADAPTER_NUMBER) {
                    PlayListViewHolder(
                        ItemPlayListBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    )
                } else {
                    PlayListViewHolder(
                        ItemRecdDailyListBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    )
                }

            }
            else -> {
                ListSubCountViewHolder(SubscribersView(parent.context))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_NORMAL -> {
                getItem(position).let { song ->
                    with(holder as PlayListViewHolder) {
                        bind(
                            song, privileges[position], position,
                            createItemClickListener(position),
                            createVideoClickListener(song.mv),
                            createMoreClickListener(song.id)
                        )
                    }
                }
            }
            else -> {
                with(holder as ListSubCountViewHolder) {
                    view.setData(mSubscribers, mSubscribedCount)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return currentList.size + if (needSubscribers()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        if (position < itemCount - 1 || type == TYPE_ADAPTER_PIC) {
            return TYPE_NORMAL
        }
        return TYPE_SUBSCRIBERS
    }

    inner class PlayListViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            mSong: Song,
            mPrivilege: Privilege,
            mPosition: Int,
            mOnItemClick: View.OnClickListener,
            mOnVideoClick: View.OnClickListener,
            mOnMoreClick: View.OnClickListener
        ) {
            if (binding is ItemPlayListBinding) {
                with(binding) {
                    onItemClick = mOnItemClick
                    includeSongInfo.onVideoClick = mOnVideoClick
                    includeSongInfo.onMoreClick = mOnMoreClick
                    number = (mPosition + 1).toString()
                    includeSongInfo.song = mSong
                    showPlayIv = MusicPlayer.getCurrentAudioId() == mSong.id
                    includeSongInfo.privilege = mPrivilege
                    executePendingBindings()
                }
            } else if (binding is ItemRecdDailyListBinding) {
                with(binding) {
                    onItemClick = mOnItemClick
                    album = mSong.al
                    includeSongInfo.onVideoClick = mOnVideoClick
                    includeSongInfo.onMoreClick = mOnMoreClick
                    showPlayIv = MusicPlayer.getCurrentAudioId() == mSong.id
                    includeSongInfo.song = mSong
                    includeSongInfo.privilege = mPrivilege
                    executePendingBindings()
                }
            }
        }
    }

    class ListSubCountViewHolder(val view: SubscribersView) : RecyclerView.ViewHolder(view)

    private fun createItemClickListener(position: Int): View.OnClickListener {
        return View.OnClickListener {
            val cur = currentList[position]
            if (cur.id != -1L) {
                if (privileges[position].fee == 1) {
                    toast("VIP暂不可以播")
                    return@OnClickListener
                }
                var newPos = position
                val infos = HashMap<Long, MusicInfo>()
                val newList = currentList.filterIndexed { index, song ->
                    val pr = privileges[index]
                    return@filterIndexed pr.fee != 1 && song.id > 0
                }
                val list = LongArray(newList.size)
                newList.forEachIndexed { index, song ->
                    if (cur.id == song.id) {
                        newPos = index
                    }
                    list[index] = song.id
                    infos[list[index]] = song.toMusicInfo()
                }
                MusicPlayer.playAll(infos, list, newPos)
            }
        }
    }

    fun playAll() {
        GlobalScope.launch {
            val infos = HashMap<Long, MusicInfo>()
            val newList = currentList.filterIndexed { index, song ->
                val pr = privileges[index]
                return@filterIndexed pr.fee != 1 && song.id > 0
            }
            val list = LongArray(newList.size)
            newList.forEachIndexed { index, song ->
                list[index] = song.id
                infos[list[index]] = song.toMusicInfo()
            }
            MusicPlayer.playAll(infos, list, 0)
        }
    }

    private fun createVideoClickListener(mvId: Long): View.OnClickListener {
        return View.OnClickListener {

        }
    }

    private fun createMoreClickListener(id: Long): View.OnClickListener {
        return View.OnClickListener {

        }
    }

    private fun needSubscribers(): Boolean {
        return mSubscribers.isNullOrEmpty().not()
    }

    fun setSongList(
        songs: MutableList<Song>,
        privileges: List<Privilege>
    ) {
        this.privileges = privileges
        submitList(songs)
    }

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_SUBSCRIBERS = 1
        //开头是数字编号
        const val TYPE_ADAPTER_NUMBER = 3
        //开头是专辑LOGO
        const val TYPE_ADAPTER_PIC = 4

        private val PLAY_LIST_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Song>() {
            override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                return oldItem == newItem
            }

        }
    }
}
