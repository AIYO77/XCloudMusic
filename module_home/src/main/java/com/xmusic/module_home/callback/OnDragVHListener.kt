package com.xmusic.module_home.callback

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
interface OnDragVHListener {
    /**
     * Item被选中时触发
     */
    fun onItemSelected()


    /**
     * Item在拖拽结束/滑动结束后触发
     */
    fun onItemFinish()
}