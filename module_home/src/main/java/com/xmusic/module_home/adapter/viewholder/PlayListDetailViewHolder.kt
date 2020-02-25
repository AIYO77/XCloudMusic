package com.xmusic.module_home.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xmusic.module_home.databinding.ItemPlayListDetailBinding

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListDetailViewHolder(
    val view: ItemPlayListDetailBinding
) :
    RecyclerView.ViewHolder(view.root) {

    fun bind(mPlaylist: PlayList?, onClickListener: View.OnClickListener) {

        with(view) {
            playList = mPlaylist
            clickListener = onClickListener
            executePendingBindings()
        }
    }

    companion object {
        fun create(parent: ViewGroup): PlayListDetailViewHolder {
            val detailBinding = ItemPlayListDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return PlayListDetailViewHolder(detailBinding)
        }
    }
}