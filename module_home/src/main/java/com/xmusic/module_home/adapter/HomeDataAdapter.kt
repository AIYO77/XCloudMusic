package com.xmusic.module_home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xw.lib_common.ext.getString
import com.xw.lib_common.view.BaseTitleView
import com.xw.lib_coremodel.model.bean.home.AlbumsAndSongs
import com.xw.lib_coremodel.model.bean.home.HomeMoreItem
import com.xw.lib_coremodel.model.bean.home.PlayListSimpleInfo
import com.xw.lib_coremodel.viewmodel.home.HomeViewModel.Companion.TYPE_PLAY_LIST
import com.xw.lib_coremodel.viewmodel.home.HomeViewModel.Companion.TYPE_TITLE
import com.xw.lib_coremodel.viewmodel.home.HomeViewModel.Companion.TYPE_TITLE_ALBUM_SONG
import com.xmusic.module_home.R
import com.xmusic.module_home.databinding.ItemPlayListNormalBinding
import com.xmusic.module_home.ui.activity.PlayListActivity
import com.xmusic.module_home.ui.activity.PlayListSquareActivity
import com.xmusic.module_home.view.HomeSwitchView

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class HomeDataAdapter :
    ListAdapter<HomeMoreItem, RecyclerView.ViewHolder>(HOME_DATA_DIFF_CALL_BACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_TITLE -> {
                return TitleViewHolder(BaseTitleView(parent.context))
            }
            TYPE_PLAY_LIST -> {
                return PlayListViewHolder(
                    ItemPlayListNormalBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
            else -> {
                return SwitchViewHolder(HomeSwitchView(parent.context))
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val item = getItem(position)
        when (viewType) {
            TYPE_PLAY_LIST -> {
                if (holder is PlayListViewHolder) {
                    item.playListSimpleInfo?.let {
                        holder.bind(createOnClickListener(it), it)
                    }
                }
            }
            TYPE_TITLE_ALBUM_SONG -> {
                if (holder is SwitchViewHolder) {
                    item.albumAndSong?.let {
                        holder.setData(it)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.type) {
            TYPE_TITLE -> {
                TYPE_TITLE
            }
            TYPE_PLAY_LIST -> {
                TYPE_PLAY_LIST
            }
            TYPE_TITLE_ALBUM_SONG -> {
                TYPE_TITLE_ALBUM_SONG
            }
            else -> {
                super.getItemViewType(position)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (getItemViewType(position)) {
                        TYPE_TITLE, TYPE_TITLE_ALBUM_SONG -> 3
                        else -> 1
                    }
                }
            }
        }
    }

    inner class TitleViewHolder(view: BaseTitleView) : RecyclerView.ViewHolder(view) {
        init {
            view.setTitle(getString(R.string.title_comm_play_list))
                .setRightTxt(getString(R.string.label_play_list_market))
                .setClickListener(object : BaseTitleView.ClickListener {
                    override fun titleClick() {
                    }

                    override fun rightClick() {
                        PlayListSquareActivity.launch(view.context)
                    }
                })
        }
    }

    inner class SwitchViewHolder(private val homeSwitchView: HomeSwitchView) :
        RecyclerView.ViewHolder(homeSwitchView) {
        fun setData(albumsAndSongs: AlbumsAndSongs) {
            homeSwitchView.setData(albumsAndSongs)
        }
    }

    inner class PlayListViewHolder(private val binding: ItemPlayListNormalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mOnClickListener: View.OnClickListener, playListSimpleInfo: PlayListSimpleInfo) {
            with(binding) {
                clickListener = mOnClickListener
                if (playListSimpleInfo.playCount == 0L) {
                    playListSimpleInfo.playCount = playListSimpleInfo.playcount
                }
                playList = playListSimpleInfo
                executePendingBindings()
            }
        }
    }

    private fun createOnClickListener(simpleInfo: PlayListSimpleInfo): View.OnClickListener {
        return View.OnClickListener {
            PlayListActivity.lunch(it.context, simpleInfo)
        }
    }

    companion object {
        private val HOME_DATA_DIFF_CALL_BACK = object : DiffUtil.ItemCallback<HomeMoreItem>() {
            override fun areItemsTheSame(oldItem: HomeMoreItem, newItem: HomeMoreItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HomeMoreItem, newItem: HomeMoreItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
