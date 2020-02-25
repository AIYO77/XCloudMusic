package com.xw.lib_common.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.xw.lib_common.R
import com.xw.lib_common.ext.dip2pxF

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 底部播放栏中的圆形进度条
 */
class RoundPlayProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr) {

    private var mRadius = 14f.dip2pxF()
    private var mPaintWidth = 1f.dip2pxF()

    private var mUnReachStopColor: Int
    private var mUnReachStartColor: Int
    private var mReachColor: Int

    private val mPaint = Paint()

    private var isStop: Boolean = true

    private var mStopBitmap: Bitmap
    private var mStartBitmap: Bitmap

    private var rectF: RectF

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
        mPaint.isDither = true

        rectF = RectF(-mRadius, -mRadius, mRadius , mRadius )

        mUnReachStopColor = ContextCompat.getColor(context, R.color.black_161616)
        mUnReachStartColor = ContextCompat.getColor(context, R.color.black_979797)
        mReachColor = ContextCompat.getColor(context, R.color.colorPrimary)

        mStopBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon_stop_small)
        mStartBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon_play_small)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 圆形默认四个padding一样
        val expectWidth = mRadius * 2 + paddingLeft + paddingRight

        val width = View.resolveSize(expectWidth.toInt(), widthMeasureSpec)
        val height = View.resolveSize(expectWidth.toInt(), heightMeasureSpec)

        val realWidth = height.coerceAtMost(width)

        setMeasuredDimension(realWidth, realWidth)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(
            paddingLeft.toFloat() - mPaintWidth / 2,
            paddingTop.toFloat() - mPaintWidth / 2
        )
        mPaint.style = Paint.Style.STROKE
        //draw unReach
        if (isStop) {
            mPaint.color = mUnReachStopColor
        } else {
            mPaint.color = mUnReachStartColor
        }
        mPaint.strokeWidth = mPaintWidth
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint)

        // draw reach
        mPaint.color = mReachColor
        mPaint.strokeWidth = mPaintWidth
        val sweep = (progress / max) * 360

        canvas.drawArc(
            rectF,
            -90f,
            sweep.toFloat(),
            false,
            mPaint
        )

        //draw bitmap btn
        mPaint.flags = Paint.ANTI_ALIAS_FLAG
        mPaint.isFilterBitmap = true
        if (isStop) {
            canvas.drawBitmap(
                mStartBitmap,
                mRadius - mStopBitmap.width / 2,
                mRadius - mStopBitmap.height / 2,
                mPaint
            )
        } else {
            canvas.drawBitmap(
                mStopBitmap,
                mRadius - mStartBitmap.width / 2,
                mRadius - mStartBitmap.height / 2,
                mPaint
            )
        }
        canvas.restore()
    }

    override fun setProgress(progress: Int) {
//        super.setProgress(progress)
        invalidate()

    }

    fun stop() {
        isStop = true
        invalidate()
    }

    fun start() {
        isStop = false
        invalidate()
    }
}