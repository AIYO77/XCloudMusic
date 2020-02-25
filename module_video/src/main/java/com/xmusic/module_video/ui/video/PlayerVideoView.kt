package com.xmusic.module_video.ui.video

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.xmusic.module_video.R
import com.xmusic.module_video.ui.activity.VideoDetailActivity
import com.xmusic.module_video.ui.utils.SwitchUtil
import com.xw.lib_common.ext.formatting
import com.xw.lib_common.ext.gone
import com.xw.lib_common.ext.makeTimeString
import com.xw.lib_common.ext.show
import com.xw.lib_coremodel.model.bean.video.VideoItemData
import kotlinx.android.synthetic.main.layout_video_view.view.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayerVideoView : StandardGSYVideoPlayer {

    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun init(context: Context?) {
        super.init(context)

        backButton.setOnClickListener {
            (context as? Activity)?.finish()
        }
    }

    override fun prepareVideo() {
        super.prepareVideo()
        gonePlayCount()
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_video_view
    }

    override fun changeUiToCompleteShow() {
        changeUiToNormal()
    }

    fun gonePlayCount() {
        playCountTv.gone()
        totalTimeTv.gone()
    }

    fun setPlayCount(count: Long) {
        playCountTv.text = count.formatting()
    }

    fun setTotalTime(time: Long) {
        totalTimeTv.text = time.makeTimeString()
    }

    fun toDetailAct(data: VideoItemData) {
        SwitchUtil.savePlayState(this)
        gsyVideoManager.setLastListener(this)
        //fixme 页面跳转是，元素共享，效果会有一个中间中间控件的存在
        //fixme 这时候中间控件 CURRENT_STATE_PLAYING，会触发 startProgressTimer
        //FIXME 但是没有cancel
        VideoDetailActivity.run(context as Activity, this, data)
    }

    fun setSwitchUrl(url: String) {
        mUrl = url
        mOriginUrl = url
    }

    fun setSwitchCache(cache: Boolean) {
        mCache = cache
    }

    fun setSwitchTitle(title: String) {
        mTitle = title
    }

    fun setSurfaceToPlay() {
        addTextureView()
        gsyVideoManager.setListener(this)
        checkoutState()
    }

    fun saveState(): PlayerVideoView {
        val videoView = PlayerVideoView(context)
        cloneParams(this, videoView)
        return videoView
    }

    fun cloneState(videoView: PlayerVideoView) {
        cloneParams(videoView, this)
    }
}