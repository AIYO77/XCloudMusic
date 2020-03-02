package com.xmusic.module_video.ui.adapter.viewholder

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_video.R
import com.xmusic.module_video.databinding.ItemVideoDetailHeaderBinding
import com.xw.lib_common.ext.*
import com.xw.lib_coremodel.model.bean.video.VideoItemData

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 视频详情页的头部holder 包含简单信息
 */
class VideoDetailHeaderViewHolder(private val binding: ItemVideoDetailHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(videoItemData: VideoItemData) {
        with(binding) {
            videoInfo = videoItemData
            val playCountTv = AppCompatTextView(itemView.context).apply {
                setTextColor(getColor(R.color.black_979797))
                text = getString(R.string.label_play_count, videoItemData.playTime.formatting())
                textSize = 13f.dip2pxF()
            }
            tagsLayout.addView(playCountTv)
            videoItemData.videoGroup.forEach {
                val textView = AppCompatTextView(itemView.context).apply {
                    height = 23f.dip2px()
                    setBackgroundResource(R.drawable.nor_bg_channel)
                    text = it.name
                    setTextColor(getColor(R.color.black_474747))
                }
                tagsLayout.addView(textView)
            }
        }
    }
}