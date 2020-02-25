package com.xmusic.module_video.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.xmusic.module_video.R
import com.xmusic.module_video.ui.utils.SwitchUtil
import com.xw.lib_coremodel.model.bean.video.VideoItemData
import com.xw.lib_coremodel.utils.DataHolder
import kotlinx.android.synthetic.main.activity_video_detail.*


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class VideoDetailActivity : AppCompatActivity() {

    private var orientationUtils: OrientationUtils? = null

    private var videoItemData: VideoItemData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_detail)

        videoItemData = DataHolder.getInstance().getData(VIDEO_DATA) as? VideoItemData
        if (videoItemData == null) {
            throw IllegalArgumentException("video data is null")
        }
        detailPlayer.gonePlayCount()

        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, detailPlayer)
        //初始化不打开外部的旋转
        orientationUtils?.isEnable = false

        SwitchUtil.optionPlayer(
            detailPlayer,
            videoItemData!!.urlInfo.url,
            true,
            videoItemData!!.title
        )
        detailPlayer.backButton.visibility = View.VISIBLE
        SwitchUtil.clonePlayState(detailPlayer)

        detailPlayer.setIsTouchWiget(true)
        detailPlayer.setVideoAllCallBack(object : GSYSampleCallBack() {
            override fun onPrepared(url: String?, vararg objects: Any?) {
                super.onPrepared(url, *objects)
                //开始播放了才能旋转和全屏
                orientationUtils?.isEnable = true
            }

            override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                super.onQuitFullscreen(url, *objects)
                orientationUtils?.backToProtVideo()
            }
        })
        detailPlayer.fullscreenButton.setOnClickListener {
            //直接横屏
            orientationUtils?.resolveByClick()
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            detailPlayer.startWindowFullscreen(this, true, true)
        }
        detailPlayer.setSurfaceToPlay()

        // 这里指定了被共享的视图元素
        ViewCompat.setTransitionName(detailPlayer, OPTION_VIEW)
        if (detailPlayer.isInPlayingState.not())
            detailPlayer.startPlayLogic()
    }

    override fun onBackPressed() {
        orientationUtils?.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()

    }

    private var isPause = false

    override fun onPause() {
        detailPlayer.currentPlayer.onVideoPause()
        super.onPause()
        isPause = true
    }

    override fun onResume() {
        detailPlayer.currentPlayer.onVideoResume(true)
        super.onResume()
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        detailPlayer.gsyVideoManager
            .setListener(detailPlayer.gsyVideoManager.lastListener())
        detailPlayer.gsyVideoManager.setLastListener(null)
        GSYVideoManager.releaseAllVideos()
        if (orientationUtils != null) orientationUtils!!.releaseListener()
        SwitchUtil.release()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (!isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true)
        }
    }

    companion object {
        private const val OPTION_VIEW = "option_view"
        private const val VIDEO_DATA = "data"

        fun run(activity: Activity, transitionView: View, videoItemData: VideoItemData) =
            activity.apply {
                val intent = Intent(this, VideoDetailActivity::class.java)
                DataHolder.getInstance().setData(VIDEO_DATA, videoItemData)
                val optionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this,
                        transitionView,
                        OPTION_VIEW
                    )
                ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle())
            }
    }
}