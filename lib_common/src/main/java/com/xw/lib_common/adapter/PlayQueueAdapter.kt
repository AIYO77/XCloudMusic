package com.xw.lib_common.adapter

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xw.lib_common.R
import com.xw.lib_common.databinding.ItemPlayQueueBinding
import com.xw.lib_common.ext.getColor
import com.xw.lib_common.ext.gone
import com.xw.lib_common.ext.show
import com.xw.lib_common.service.MusicPlayer
import com.xw.lib_coremodel.model.bean.info.MusicInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayQueueAdapter :
    ListAdapter<MusicInfo, PlayQueueAdapter.PlayQueueViewHolder>(PlayQueueDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayQueueViewHolder {
        return PlayQueueViewHolder(
            ItemPlayQueueBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PlayQueueViewHolder, position: Int) {
        val musicInfo = getItem(position)
        with(holder.item) {
            val currentAudioId = MusicPlayer.getCurrentAudioId()
            val name = "${musicInfo.musicName} - ${musicInfo.artist}"
            val spannableString = SpannableString(name).apply {
                setSpan(
                    RelativeSizeSpan(0.8f),
                    musicInfo.musicName.length + 1,
                    name.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (currentAudioId == musicInfo.songId) {
                playState.show()
                nameAndAr.setTextColor(getColor(R.color.color_c23323))
            } else {
                playState.gone()
                spannableString.setSpan(
                    ForegroundColorSpan(getColor(R.color.black_2d2d2d)),
                    0,
                    musicInfo.musicName.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    ForegroundColorSpan(getColor(R.color.color_7c7c7c)),
                    musicInfo.musicName.length,
                    name.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            nameAndAr.text = spannableString

            deleteItem.setOnClickListener {
                notifyItemRemoved(position)
                MusicPlayer.removeTrack(musicInfo.songId)
                if (currentList.isNullOrEmpty()) {
                    MusicPlayer.stop()
                }
                if (MusicPlayer.isPlaying() && currentAudioId == musicInfo.songId) {
                    MusicPlayer.next()
                }
            }

            root.setOnClickListener {
                if (position >= 0) {
                    val oldPosition = MusicPlayer.getQueuePosition()
                    MusicPlayer.setQueuePosition(position)
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(position)
                }
            }
        }
    }

    class PlayQueueViewHolder(val item: ItemPlayQueueBinding) : RecyclerView.ViewHolder(item.root)
}

class PlayQueueDiffCallback : DiffUtil.ItemCallback<MusicInfo>() {
    override fun areItemsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
        return oldItem.songId == newItem.songId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
        return oldItem == newItem
    }
}