package com.xw.lib_common.listener

import com.xw.lib_coremodel.model.bean.LrcAdnTlyRic

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
interface MusicStateListener {
    /**
     * 更新歌曲状态信息
     */
    fun updateTrackInfo()

    /**
     * 切换歌曲
     */
    fun updateTrack()

    /**
     * 更新播放队列
     */
    fun updateQueue()

    /**
     * 更新歌词
     */
    fun updateLrc(lrc: LrcAdnTlyRic)


}