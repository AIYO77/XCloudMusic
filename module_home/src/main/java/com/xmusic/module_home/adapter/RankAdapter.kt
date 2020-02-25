package com.xmusic.module_home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xw.lib_common.base.BaseApplication
import com.xw.lib_common.databinding.BaseTitleViewBinding
import com.xw.lib_coremodel.model.bean.BaseTitle
import com.xw.lib_coremodel.model.bean.home.PlayListSimpleInfo
import com.xw.lib_coremodel.model.bean.home.TopListItem
import com.xmusic.module_home.R
import com.xmusic.module_home.databinding.ItemRankBinding
import com.xmusic.module_home.databinding.ItemRankNormalBinding
import com.xmusic.module_home.ui.activity.PlayListActivity

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class RankAdapter : ListAdapter<TopListItem, RecyclerView.ViewHolder>(RANK_DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_ONE -> {
                getItem(position).let { rank ->
                    with(holder) {
                        itemView.tag = rank
                        (holder as OnePicViewHolder).bind(createOnClickListener(rank), rank)
                    }
                }
            }
            TYPE_TITLE -> {
                getItem(position).let { rank ->
                    with(holder) {
                        itemView.tag = rank
                        (holder as TitleViewHolder).setTitle(
                            BaseTitle(
                                title = rank.name, titleColor = ContextCompat.getColor(
                                    BaseApplication.CONTEXT,
                                    R.color.black_161616
                                ), showMoreIcon = true
                            )
                        )
                    }
                }

            }
            TYPE_THREE -> {
                getItem(position).let { rank ->
                    with(holder) {
                        itemView.tag = rank
                        (holder as ThreePicViewHolder).bind(createOnClickListener(rank), rank)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_ONE -> {
                return OnePicViewHolder(
                    ItemRankBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            TYPE_TITLE -> {
                return TitleViewHolder(
                    BaseTitleViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            else -> {
                return ThreePicViewHolder(
                    ItemRankNormalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    private fun createOnClickListener(item: TopListItem): View.OnClickListener {
        return View.OnClickListener {
            PlayListActivity.lunch(
                it.context,
                PlayListSimpleInfo(
                    item.id,
                    picUrl = item.coverImgUrl,
                    name = item.name,
                    playCount = item.playCount
                )
            )
        }
    }


    override fun getItemViewType(position: Int): Int {
        val data = getItem(position)
        return if (data.isTitle) {
            TYPE_TITLE
        } else when (data.tracks.isNullOrEmpty()) {
            true -> {
                TYPE_THREE
            }
            false -> {
                TYPE_ONE
            }
        }
    }

    class OnePicViewHolder(private val binding: ItemRankBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, topListItem: TopListItem) {
            with(binding) {
                clickListener = listener
                rank = topListItem
                executePendingBindings()
            }
        }
    }

    class ThreePicViewHolder(private val binding: ItemRankNormalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, topListItem: TopListItem) {
            with(binding) {
                clickListener = listener
                rank = topListItem
                executePendingBindings()
            }
        }
    }

    class TitleViewHolder(private val binding: BaseTitleViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            val params = itemView.layoutParams as GridLayoutManager.LayoutParams
            params.setMargins(0, 44, 0, 16)
            itemView.layoutParams = params
        }

        fun setTitle(baseTitle: BaseTitle) {
            with(binding) {
                title = baseTitle
                executePendingBindings()
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
                        TYPE_ONE, TYPE_TITLE -> 6
                        else -> 2
                    }
                }
            }
        }
    }

    companion object {
        private const val TYPE_TITLE = 1 // title
        private const val TYPE_ONE = 2// 一张图+右边描述
        private const val TYPE_THREE = 3// 三张图+底部title

        val RANK_DIFF_CALLBACK = object : DiffUtil.ItemCallback<TopListItem>() {
            override fun areItemsTheSame(oldItem: TopListItem, newItem: TopListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TopListItem, newItem: TopListItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}

