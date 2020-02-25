package com.xw.lib_common.view.loadingdrawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class LoadingCircleDrawable constructor(
    private var mMinSize: Int = DEFAULT_SIZE,
    private var mMaxSize: Int = DEFAULT_SIZE
) : LoadingDrawable() {

    private val mOval = RectF()

    private var mStartAngle = 0f
    private var mSweepAngle = 0f
    private var mAngleIncrement = -3

    init {
        mForegroundPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun getIntrinsicHeight(): Int {
        val maxLine =
            mBackgroundPaint.strokeWidth.coerceAtLeast(mForegroundPaint.strokeWidth)
        val size = (maxLine * 2 + 10).toInt()
        return mMaxSize.coerceAtMost(size.coerceAtLeast(mMinSize))
    }

    override fun getIntrinsicWidth(): Int {
        val maxLine =
            mBackgroundPaint.strokeWidth.coerceAtLeast(mForegroundPaint.strokeWidth)
        val size = (maxLine * 2 + 10).toInt()
        return mMaxSize.coerceAtMost(size.coerceAtLeast(mMinSize))
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        if (bounds!!.left == 0 && bounds.top == 0 && bounds.right == 0 && bounds.bottom == 0) {
            return
        }

        val centerX = bounds.centerX()
        val centerY = bounds.centerY()

        val radius = bounds.height().coerceAtMost(bounds.width()) shr 1
        val maxStrokeRadius =
            (getForegroundLineSize().coerceAtLeast(getBackgroundLineSize()).toInt() shr 1) + 1
        val areRadius = radius - maxStrokeRadius

        mOval[centerX - areRadius.toFloat(), centerY - areRadius.toFloat(), centerX + areRadius.toFloat()] =
            centerY + areRadius.toFloat()
    }

    override fun onRefresh() {
        val angle = ANGLE_ADD.toFloat()
        mStartAngle += angle

        if (mStartAngle > 360) {
            mStartAngle -= 360f
        }

        if (mSweepAngle > MAX_ANGLE_SWEEP) {
            mAngleIncrement = -mAngleIncrement
        } else if (mSweepAngle < MIN_ANGLE_SWEEP) {
            mSweepAngle = MIN_ANGLE_SWEEP.toFloat()
            return
        } else if (mSweepAngle == MIN_ANGLE_SWEEP.toFloat()) {
            mAngleIncrement = -mAngleIncrement
            getNextForegroundColor()
        }
        mSweepAngle += mAngleIncrement.toFloat()
    }

    override fun drawBackground(canvas: Canvas?, backgroundPaint: Paint) {
        canvas?.drawArc(mOval, 0f, 360f, false, backgroundPaint)

    }

    override fun drawForeground(canvas: Canvas?, foregroundPaint: Paint) {
        canvas?.drawArc(mOval, mStartAngle, -mSweepAngle, false, foregroundPaint)
    }

    override fun onProgressChange(progress: Float) {
        mStartAngle = 0f
        mSweepAngle = 360 * progress
    }

    companion object {
        private const val ANGLE_ADD = 5
        private const val MIN_ANGLE_SWEEP = 3
        private const val MAX_ANGLE_SWEEP = 255
        private const val DEFAULT_SIZE = 56
    }
}