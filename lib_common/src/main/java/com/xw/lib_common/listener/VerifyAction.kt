package com.xw.lib_common.listener

import androidx.annotation.ColorRes


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
interface VerifyAction {

    /**
     * 设置位数
     */
    fun setFigures(figures: Int)

    /**
     * 设置验证码之间的间距
     */
    fun setVerCodeMargin(margin: Int)

    /**
     * 设置底部选中状态的颜色
     */
    fun setBottomSelectedColor(@ColorRes bottomSelectedColor: Int)

    /**
     * 设置底部未选中状态的颜色
     */
    fun setBottomNormalColor(@ColorRes bottomNormalColor: Int)

    /**
     * 设置底线的高度
     */
    fun setBottomLineHeight(bottomLineHeight: Int)

    /**
     * 设置当验证码变化时候的监听器
     */
    fun setOnVerificationCodeChangedListener(listener: OnVerificationCodeChangedListener)

    /**
     * 验证码变化时候的监听事件
     */
    interface OnVerificationCodeChangedListener {
        /**
         * 当验证码变化的时候
         */
        fun onVerCodeChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        )

        /**
         * 输入完毕后的回调
         */
        fun onInputCompleted(s: CharSequence)
    }

}