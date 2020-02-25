package com.xw.lib_common.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.xw.lib_common.ext.dip2px


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class LinearItemDecoration(
    @ColorInt val mColor: Int, val height: Float = 0f,
    private val margin: Float = 0f
) : RecyclerView.ItemDecoration() {

    private var paint: Paint? = null

    init {
        paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        paint!!.color = mColor
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            drawHorizontalDecoration(c, parent.getChildAt(i))
        }
    }

    private fun drawHorizontalDecoration(c: Canvas?, childView: View) {
        val rect = Rect(0, 0, 0, 0)
        rect.top = childView.bottom
        rect.bottom = rect.top + height.dip2px()
        rect.left = childView.left + margin.dip2px()
        rect.right = childView.right - margin.dip2px()
        c?.drawRect(rect, paint!!)
    }
}