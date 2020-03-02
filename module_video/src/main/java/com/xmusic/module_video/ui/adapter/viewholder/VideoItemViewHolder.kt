package com.xmusic.module_video.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.xmusic.module_video.R
import com.xmusic.module_video.databinding.ItemVideoItemBinding
import com.xmusic.module_video.ui.utils.SwitchUtil
import com.xmusic.module_video.ui.video.PlayerVideoView
import com.xw.lib_common.ext.*
import com.xw.lib_common.utils.GlideUtils
import com.xw.lib_coremodel.model.bean.video.VideoItemData
import com.xw.lib_coremodel.model.bean.video.VideoItemInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class VideoItemViewHolder(private val itemVideoItemBinding: ItemVideoItemBinding) :
    RecyclerView.ViewHolder(itemVideoItemBinding.root) {

    private var imageView: ImageView? = null

    fun binding(
        itemInfo: VideoItemInfo?,
        position: Int,
        callback: (VideoItemData, PlayerVideoView) -> Unit
    ) {
        itemInfo?.let { video ->
            with(itemVideoItemBinding) {
                videoItemInfo = video
                if (imageView == null) {
                    imageView = ImageView(itemView.context).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        if (parent != null) {
                            val parent = parent as ViewGroup
                            parent.removeView(imageView)
                        }
                    }
                }
                GlideUtils.loadImage(
                    imageView!!,
                    video.data.coverUrl
                )
                if (video.data.videoGroup.isNotEmpty()) {
                    videoTag.show()
                    videoTag.text = video.data.videoGroup[0].name
                    videoTag.setOnClickListener {
                        toast( videoTag.text.toString() )
                    }
                } else {
                    videoTag.invisible()
                }
                val url = video.data.urlInfo.url
                videoView.setPlayCount(video.data.playTime)
                videoView.setTotalTime(video.data.durationms)
                videoView.playTag = url
                videoView.playPosition = position
                SwitchUtil.optionPlayer(
                    videoView,
                    url,
                    true,
                    video.data.title
                )
                videoView.setUpLazy(url, true, null, null, video.data.title)

                videoView.thumbImageView = imageView

                if (GSYVideoManager.instance().playTag == video.data.urlInfo.url
                    && position == GSYVideoManager.instance().playPosition
                ) {
                    videoView.thumbImageViewLayout.visibility = View.GONE
                } else {
                    videoView.thumbImageViewLayout.visibility = View.VISIBLE
                }

                likeView.setLikeCount(video.data.praisedCount)
                likeView.setLiked(video.data.praised)
                videoTitle.setOnClickListener {
                    callback.invoke(video.data, videoView)
                }
                executePendingBindings()
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): VideoItemViewHolder {
            val itemBinding =
                ItemVideoItemBinding.inflate(LayoutInflater.from(parent.context), null, false)
            return VideoItemViewHolder(itemBinding)
        }
    }
}