package com.xmusic.module_video.ui.utils

import android.view.View
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener
import com.xmusic.module_video.ui.video.PlayerVideoView

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object SwitchUtil {
    private var sSwitchVideo: PlayerVideoView? = null
    private var sMediaPlayerListener: GSYMediaPlayerListener? = null

    fun optionPlayer(
        gsyVideoPlayer: PlayerVideoView,
        url: String,
        cache: Boolean,
        title: String = ""
    ) { //增加title
        gsyVideoPlayer.titleTextView.visibility = View.GONE
        //设置返回键
        gsyVideoPlayer.backButton.visibility = View.GONE
        //设置全屏按键功能
        gsyVideoPlayer.fullscreenButton?.setOnClickListener {
                gsyVideoPlayer.startWindowFullscreen(
                    gsyVideoPlayer.context,
                    false,
                    true
                )
            }
        //是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
        gsyVideoPlayer.isAutoFullWithSize = true
        //音频焦点冲突时是否释放
        gsyVideoPlayer.isReleaseWhenLossAudio = true
        //全屏动画
        gsyVideoPlayer.isShowFullAnimation = false
        //小屏时不触摸滑动
        gsyVideoPlayer.setIsTouchWiget(false)
        gsyVideoPlayer.setThumbPlay(true)
        gsyVideoPlayer.setSwitchUrl(url)
        gsyVideoPlayer.setSwitchCache(cache)
        gsyVideoPlayer.setSwitchTitle(title)
        gsyVideoPlayer.titleTextView.text = title
    }


    fun savePlayState(switchVideo: PlayerVideoView) {
        sSwitchVideo = switchVideo.saveState()
        sMediaPlayerListener = switchVideo
    }

    fun clonePlayState(switchVideo: PlayerVideoView) {
        sSwitchVideo?.apply {
            switchVideo.cloneState(this)
        }
    }

    fun release() {
        if (sMediaPlayerListener != null) {
            sMediaPlayerListener!!.onAutoCompletion()
        }
        sSwitchVideo = null
        sMediaPlayerListener = null
    }
}