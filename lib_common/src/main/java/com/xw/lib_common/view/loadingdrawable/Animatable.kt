package com.xw.lib_common.view.loadingdrawable

import android.graphics.drawable.Animatable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
interface Animatable : Animatable {
    /**
     * This is drawable animation frame duration
     */
    val FRAME_DURATION: Int
        get() = 16

    /**
     * This is drawable animation duration
     */
    val ANIMATION_DURATION: Int
        get() = 250
}