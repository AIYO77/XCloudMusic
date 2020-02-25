package com.xw.lib_common.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class AlbumViewPager(context: Context, attrs: AttributeSet?) : SuperViewPager(context, attrs) {

    private var downPoint = PointF()
    private var onSingleTouchListener: OnSingleTouchListener? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(evt: MotionEvent): Boolean {
        when (evt.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录按下时候的坐标
                downPoint.x = evt.x
                downPoint.y = evt.y
                if (this.childCount > 1) { //有内容，多于1个时
                    // 通知其父控件，现在进行的是本控件的操作，不允许拦截
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_UP ->
                // 在up时判断是否按下和松手的坐标为一个点
                if (PointF.length(
                        evt.x - downPoint.x,
                        evt.y - downPoint.y
                    ) < 5.0.toFloat()
                ) {
                    onSingleTouch(this)
                    return true
                }
        }
        return super.onTouchEvent(evt)
    }

    private fun onSingleTouch(v: View) {
        if (onSingleTouchListener != null) {
            onSingleTouchListener!!.onSingleTouch(v)
        }
    }

    interface OnSingleTouchListener {
        fun onSingleTouch(v: View)
    }

    fun setOnSingleTouchListener(
        onSingleTouchListener: OnSingleTouchListener
    ) {
        this.onSingleTouchListener = onSingleTouchListener
    }
}