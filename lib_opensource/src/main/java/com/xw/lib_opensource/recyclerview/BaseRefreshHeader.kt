package com.xw.lib_opensource.recyclerview

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
interface BaseRefreshHeader {

    fun onMove(delta: Float)

    fun releaseAction(): Boolean

    fun refreshComplate()

    fun getVisiableHeight(): Int

}