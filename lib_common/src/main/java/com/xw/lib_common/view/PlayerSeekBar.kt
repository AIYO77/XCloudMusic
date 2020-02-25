package com.xw.lib_common.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.SeekBar
import com.xw.lib_common.R
import com.xw.lib_common.ext.getDrawable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayerSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SeekBar(context, attrs, defStyleAttr) {

    private var drawLoading = false
    private var degree = 0
    private val loading =
        BitmapFactory.decodeResource(resources, R.drawable.play_plybar_btn_loading)
    private var drawable: Drawable? = null

    init {
        thumb = getDrawable(R.drawable.icon_play_plybar_btn)
    }

    fun setLoading(loading: Boolean) {
        if (loading) {
            drawLoading = true
            invalidate()
        } else {
            drawLoading = false
        }
    }

    override fun setThumb(thumb: Drawable?) {
        var localRect: Rect? = null
        if (drawable != null) {
            localRect = drawable!!.bounds
        }
        super.setThumb(drawable)
        drawable = thumb
        if (localRect != null && drawable != null) {
            drawable!!.bounds = localRect
        }

    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (drawLoading) {
            canvas.save()
            degree = (degree + 3.0f).toInt()
            degree %= 360
            matrix.reset()
            matrix.postRotate(
                degree.toFloat(),
                (loading.width / 2).toFloat(),
                (loading.height / 2).toFloat()
            )
            canvas.translate(
                (paddingLeft + thumb.bounds.left + drawable!!.intrinsicWidth / 2 - loading.width / 2 - thumbOffset).toFloat(),
                (paddingTop + thumb.bounds.top + drawable!!.intrinsicHeight / 2 - loading.height / 2).toFloat()
            )
            canvas.drawBitmap(loading, matrix, null)
            canvas.restore()
            invalidate()
        }

    }
}