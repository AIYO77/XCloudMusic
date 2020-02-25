package com.xw.lib_common.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.xw.lib_common.R
import com.xw.lib_common.ext.getColor
import com.xw.lib_common.service.MusicPlayer

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

class RoundProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle) {
    /**
     * 画笔对象的引用
     */
    private val paint: Paint = Paint()

    /**
     * 圆环的颜色
     */
    private var circeColor: Int = 0

    /**
     * 暂停bitmap
     */
    private val mStopBitmap: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.icon_stop_small)

    /**
     * 播放bitmap
     */
    private val mStartBitmap: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.icon_play_small)

    /**
     * 圆环进度的颜色
     */
    private var circleProgressColor = getColor(R.color.colorPrimary)

    /**
     * 圆环的宽度
     */
    private var roundWidth: Float = 0.toFloat()

    /**
     * 最大进度
     */
    private var max = 0

    @get:Synchronized
    var isStop: Boolean = MusicPlayer.isPlaying().not()
        @Synchronized set(value) {
            if (field != value) {
                field = value
                postInvalidate()
            }
        }

    /**
     * 当前进度
     * 获取进度.需要同步
     * 设置进度，此为线程安全控件，由于考虑多线程的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     */
    @get:Synchronized
    var progress: Int = 0
        @Synchronized set(progress) {
            var progress = progress
            require(progress >= 0) { "progress not less than 0" }
            if (progress > max) {
                progress = max
            }
            if (progress <= max) {
                field = progress
                postInvalidate()
            }

        }

    init {

        val mTypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.RoundProgressBar
        )

        //获取自定义属性和默认值
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 4f)
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100)
        mTypedArray.recycle()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /**
         * 画最外层的大圆环
         */
        val centre = width / 2 //获取圆心的x坐标
        val radius = (centre - roundWidth / 2).toInt() //圆环的半径
        circeColor = if (isStop) {
            getColor(R.color.black_474747)
        } else {
            getColor(R.color.color_DBDBDB)

        }
        paint.color = circeColor //设置圆环的颜色
        paint.style = Paint.Style.STROKE //设置空心
        paint.strokeWidth = roundWidth //设置圆环的宽度
        paint.isAntiAlias = true  //消除锯齿
        canvas.drawCircle(centre.toFloat(), centre.toFloat(), radius.toFloat(), paint) //画出圆环

        /**
         * 画圆弧 ，画圆环的进度
         */

        paint.color = circleProgressColor  //设置进度的颜色

        val oval = RectF(
            (centre - radius).toFloat(),
            (centre - radius).toFloat(),
            (centre + radius).toFloat(),
            (centre + radius).toFloat()
        )  //用于定义的圆弧的形状和大小的界限
        paint.strokeWidth = roundWidth //设置圆环的宽度
        paint.style = Paint.Style.STROKE
        canvas.drawArc(
            oval,
            -90f,
            (360 * this.progress / max).toFloat(),
            false,
            paint
        )

        /**
         * 画图片 暂停/播放
         */
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0f

        if (isStop) {

            canvas.drawBitmap(
                mStartBitmap,
                (centre - mStartBitmap.width * 1.3 / 3).toFloat(),
                (centre - mStartBitmap.height / 2).toFloat(),
                paint
            )
        } else {
            canvas.drawBitmap(
                mStopBitmap,
                (centre - mStopBitmap.width / 2).toFloat(),
                (centre - mStopBitmap.height / 2).toFloat(),
                paint
            )
        }
    }


    @Synchronized
    fun getMax(): Int {
        return max
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    @Synchronized
    fun setMax(max: Int) {
        require(max >= 0) { "max not less than 0" }
        this.max = max
    }


}