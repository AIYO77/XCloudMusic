package com.xw.lib_common.base.view.fragment

import com.orhanobut.logger.Logger
import com.xw.lib_common.base.view.activity.AutoServerActivity
import com.xw.lib_common.listener.MusicStateListener
import com.xw.lib_coremodel.model.bean.LrcAdnTlyRic

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
open class StateListenerFragment : AutoDisposeFragment(), MusicStateListener {

    override fun onResume() {
        super.onResume()
        if (requireActivity() is AutoServerActivity) {
            (requireActivity() as AutoServerActivity).setMusicStateListenerListener(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (requireActivity() is AutoServerActivity) {
            (requireActivity() as AutoServerActivity).removeMusicStateListenerListener(this)
        }
    }

    override fun updateTrackInfo() {
        Logger.i("updateTrackInfo")
    }

    override fun updateTrack() {
        Logger.i("updateTrack")
    }

    override fun updateQueue() {
        Logger.i("updateQueue")

    }

    override fun updateLrc(lrc: LrcAdnTlyRic) {
    }
}