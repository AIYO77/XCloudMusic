package com.xw.lib_common.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object AnimationUtils {

    /**
     * 放大动画
     *
     * @param view 要放大的视图
     * @param from 放大起始倍数
     * @param to   放大最终倍数
     */
    fun scale(view: View, from: Float, to: Float) {
        val pvhY = PropertyValuesHolder.ofFloat("scaleX", from, to)
        val pvhZ = PropertyValuesHolder.ofFloat("scaleY", from, to)
        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhY, pvhZ)
        objectAnimator.setDuration(200).start()
    }

//    fun enlargeToShrink(view: View,)

}