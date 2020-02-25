package com.xw.lib_common.view.loadingdrawable

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.SystemClock

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class LoadingDrawable : Drawable(), Animatable {

    protected var mForegroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    protected var mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private  var mForegroundColorIndex = 0
    protected var mProgress = 0f

    private var mRun = false

    init {
        val bPaint = mBackgroundPaint
        bPaint.style = Paint.Style.STROKE
        bPaint.isAntiAlias = true
        bPaint.isDither = true
        bPaint.strokeWidth = LINE_SIZE.toFloat()
        bPaint.color = 0x32000000

        val fPaint = mForegroundPaint
        fPaint.style = Paint.Style.STROKE
        fPaint.isAntiAlias = true
        fPaint.isDither = true
        fPaint.strokeWidth = LINE_SIZE.toFloat()
        fPaint.color = mForegroundColor[0]
    }

    override fun getIntrinsicHeight(): Int {
        val maxLine = mBackgroundPaint.strokeWidth.coerceAtLeast(mForegroundPaint.strokeWidth)
        return (maxLine * 2).toInt()
    }

    override fun getIntrinsicWidth(): Int {
        val maxLine = mBackgroundPaint.strokeWidth.coerceAtLeast(mForegroundPaint.strokeWidth)
        return (maxLine * 2).toInt()
    }

    open fun setBackgroundLineSize(size: Float) {
        mBackgroundPaint.strokeWidth = size
        onBoundsChange(bounds)
    }

    open fun setForegroundLineSize(size: Float) {
        mForegroundPaint.strokeWidth = size
        onBoundsChange(bounds)
    }

    open fun getBackgroundLineSize(): Float {
        return mBackgroundPaint.strokeWidth
    }

    open fun getForegroundLineSize(): Float {
        return mForegroundPaint.strokeWidth
    }

    open fun setBackgroundColor(color: Int) {
        mBackgroundPaint.color = color
    }

    open fun getBackgroundColor(): Int {
        return mBackgroundPaint.color
    }

    open fun setForegroundColor(colors: IntArray?) {
        if (colors == null) return
        mForegroundColor = colors
        mForegroundColorIndex = -1
        getNextForegroundColor()
    }


    open fun getForegroundColor(): IntArray? {
        return mForegroundColor
    }

    open fun getNextForegroundColor(): Int {
        val colors = mForegroundColor
        val fPaint = mForegroundPaint
        if (colors.size > 1) {
            var index = mForegroundColorIndex + 1
            if (index >= colors.size) index = 0
            fPaint.color = colors[index]
            mForegroundColorIndex = index
        } else {
            fPaint.color = colors[0]
        }
        return fPaint.color
    }

    /**
     * Get the loading progress
     *
     * @return Progress
     */
    open fun getProgress(): Float {
        return mProgress
    }

    /**
     * Set the draw progress
     * The progress include 0~1 float
     * On changed, stop animation draw
     *
     * @param progress Loading progress
     */
    open fun setProgress(progress: Float) {
        mProgress = if (progress < 0) 0f else if (mProgress > 1) 1f else progress
        stop()
        onProgressChange(mProgress)
        invalidateSelf()
    }

      private val  mAnim:Runnable = object :Runnable {
          override fun run() {
              if (mRun) {
                  onRefresh()
                  invalidateSelf()
              } else {
                  unscheduleSelf(this)
              }
          }
    }

    override fun draw(canvas: Canvas) {
        val count = canvas.save()

        val bPaint = mBackgroundPaint
        if (bPaint.color != 0 && bPaint.strokeWidth > 0) drawBackground(canvas, bPaint)

        val fPaint = mForegroundPaint
        if (mRun) {
            if (fPaint.color != 0 && fPaint.strokeWidth > 0) drawForeground(
                canvas,
                fPaint
            )
            // invalidate next call in this
            scheduleSelf(mAnim, SystemClock.uptimeMillis() + FRAME_DURATION)
        } else if (mProgress > 0) {
            if (fPaint.color != 0 && fPaint.strokeWidth > 0) drawForeground(
                canvas,
                fPaint
            )
        }

        canvas.restoreToCount(count)
    }

    override fun setAlpha(alpha: Int) {
        mForegroundPaint.alpha = alpha
    }

    override fun getOpacity(): Int {
        val bPaint = mBackgroundPaint
        val fPaint = mForegroundPaint
        if (bPaint.xfermode == null && fPaint.xfermode == null) {
            val alpha = Color.alpha(fPaint.color)
            if (alpha == 0) {
                return PixelFormat.TRANSPARENT
            }
            if (alpha == 255) {
                return PixelFormat.OPAQUE
            }
        }
        // not sure, so be safe
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(cf: ColorFilter?) {
        var needRefresh = false
        val bPaint = mBackgroundPaint
        if (bPaint.colorFilter !== cf) {
            bPaint.colorFilter = cf
            needRefresh = true
        }

        val fPaint = mForegroundPaint
        if (fPaint.colorFilter !== cf) {
            fPaint.colorFilter = cf
            needRefresh = true
        }

        if (needRefresh) invalidateSelf()
    }

    override fun isRunning(): Boolean {
        return mRun
    }

    override fun start() {
        if (!mRun) {
            mRun = true
            scheduleSelf(mAnim, SystemClock.uptimeMillis() + FRAME_DURATION)
        }
    }

    override fun stop() {
        if (mRun) {
            mRun = false
            unscheduleSelf(mAnim)
            invalidateSelf()
        }
    }

    protected abstract fun onRefresh()

    protected abstract fun drawBackground(
        canvas: Canvas?,
        backgroundPaint: Paint
    )

    protected abstract fun drawForeground(
        canvas: Canvas?,
        foregroundPaint: Paint
    )

    protected abstract fun onProgressChange(progress: Float)

    companion object {
        private const val LINE_SIZE = 4
        private var mForegroundColor = intArrayOf(-0x34000000, -0x1879b, -0x7bdc68)
    }
}