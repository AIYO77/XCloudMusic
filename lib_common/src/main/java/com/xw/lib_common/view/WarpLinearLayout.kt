package com.xw.lib_common.view

import android.R.attr
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.xw.lib_common.R


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 自动换行的ViewGroup
 */
class WarpLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var mType: Type

    private var mWarpLineGroup: MutableList<WarpLine>? = null

    init {
        mType = Type(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val withMode = MeasureSpec.getMode(widthMeasureSpec)
        val withSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var with = 0
        var height: Int
        val childCount = childCount

        measureChildren(widthMeasureSpec, heightMeasureSpec)
        when (withMode) {
            MeasureSpec.EXACTLY -> {
                with = withSize
            }
            MeasureSpec.AT_MOST -> {
                for (i in 0 until childCount) {
                    if (i != 0) {
                        with += mType.mHorizontalSpace.toInt()
                    }
                    with += getChildAt(i).measuredWidth
                }
                with += paddingLeft + paddingRight
                with = if (with > withSize) {
                    withSize
                } else {
                    width
                }
            }
            MeasureSpec.UNSPECIFIED -> {
                for (i in 0 until childCount) {
                    if (i != 0) {
                        with += mType.mHorizontalSpace.toInt()
                    }
                    with += getChildAt(i).measuredWidth
                }
                with += paddingLeft + paddingRight
            }
            else -> {
                with = withSize
            }
        }
        var warpLine = WarpLine()
        mWarpLineGroup = mutableListOf()
        for (i in 0 until childCount) {
            if (warpLine.lineWidth + getChildAt(i).measuredWidth + mType.mHorizontalSpace > with) {
                if (warpLine.lineView.size == 0) {
                    warpLine.addView(getChildAt(i))
                    mWarpLineGroup!!.add(warpLine)
                    warpLine = WarpLine()
                } else {
                    mWarpLineGroup!!.add(warpLine)
                    warpLine = WarpLine()
                    warpLine.addView(getChildAt(i))
                }
            } else {
                warpLine.addView(getChildAt(i))
            }
        }

        if (warpLine.lineView.size > 0 && mWarpLineGroup!!.contains(warpLine).not()) {
            mWarpLineGroup!!.add(warpLine)
        }

        height = paddingTop + paddingBottom
        for (i in 0 until mWarpLineGroup!!.size) {
            if (i != 0) {
                height += mType.mVerticalSpace.toInt()
            }
            height += mWarpLineGroup!![i].height
        }

        when (heightMode) {
            MeasureSpec.EXACTLY -> {
                height = heightSize
            }
            MeasureSpec.AT_MOST -> {
                height = if (height > heightSize) heightSize else height
            }
            MeasureSpec.UNSPECIFIED -> {
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, top: Int, r: Int, b: Int) {
        var t = paddingTop
        for (i in 0 until mWarpLineGroup!!.size) {
            var left = paddingLeft
            val warpLine = mWarpLineGroup!![i]
            val lastWidth = measuredWidth - warpLine.lineWidth
            for (j in 0 until warpLine.lineView.size) {
                val view = warpLine.lineView[j]
                if (isFull()) {
                    view.layout(
                        left,
                        t,
                        left + view.measuredWidth + lastWidth / warpLine.lineView.size,
                        t + view.measuredHeight
                    )
                    left += view.measuredWidth + mType.mHorizontalSpace.toInt() + lastWidth / warpLine.lineView.size
                } else {
                    when (getGrivate()) {
                        0 -> view.layout(
                            attr.left + lastWidth,
                            t,
                            attr.left + lastWidth + view.measuredWidth,
                            t + view.measuredHeight
                        )
                        2 -> view.layout(
                            attr.left + lastWidth / 2,
                            t,
                            attr.left + lastWidth / 2 + view.measuredWidth,
                            t + view.measuredHeight
                        )
                        else -> view.layout(
                            attr.left,
                            t,
                            attr.left + view.measuredWidth,
                            t + view.measuredHeight
                        )
                    }
                    left += view.measuredWidth + mType.mHorizontalSpace.toInt()
                }
            }
            t += warpLine.height + mType.mVerticalSpace.toInt()
        }
    }

    /**
     * 用于存放一行子View
     */
    private inner class WarpLine {
        val lineView = mutableListOf<View>()
        /**
         * 当前行中所需要占用的宽度
         */
        var lineWidth = paddingLeft + paddingRight
        /**
         * 该行View中所需要占用的最大高度
         */
        var height = 0

        fun addView(view: View) {
            if (lineView.size != 0) {
                lineWidth += mType.mHorizontalSpace.toInt()
            }
            height = if (height > view.measuredHeight) height else view.measuredHeight
            lineWidth += view.measuredWidth
            lineView.add(view)
        }
    }

    fun getGrivate(): Int {
        return mType.mGrivate
    }

    fun getHorizontal_Space(): Float {
        return mType.mHorizontalSpace
    }

    fun getVertical_Space(): Float {
        return mType.mVerticalSpace
    }

    fun isFull(): Boolean {
        return mType.mIsFull
    }

    fun setGrivate(grivate: Int) {
        mType.mGrivate = grivate
    }

    fun setHorizontal_Space(horizontal_Space: Float) {
        mType.mHorizontalSpace = horizontal_Space
    }

    fun setVertical_Space(vertical_Space: Float) {
        mType.mVerticalSpace = vertical_Space
    }

    fun setIsFull(isFull: Boolean) {
        mType.mIsFull = isFull
    }


    companion object {
        private const val RIGHT = 0
        private const val LEFT = 1
        private const val CENTER = 2

        class Type(context: Context, attrs: AttributeSet?) {
            /**
             * 对齐方式 right 0，left 1，center 2
             */
            var mGrivate: Int = 0
            /**
             * 水平间距,单位px
             */
            var mHorizontalSpace: Float = 0f
            /**
             * 垂直间距,单位px
             */
            var mVerticalSpace: Float = 0f
            /**
             * 是否自动填满
             */
            var mIsFull: Boolean = false

            init {
                if (attrs != null) {
                    val typedArray =
                        context.obtainStyledAttributes(attrs, R.styleable.WarpLinearLayout)
                    mGrivate = typedArray.getInt(R.styleable.WarpLinearLayout_gravite, mGrivate)
                    mHorizontalSpace = typedArray.getDimension(
                        R.styleable.WarpLinearLayout_horizontal_Space,
                        mHorizontalSpace
                    )
                    mVerticalSpace = typedArray.getDimension(
                        R.styleable.WarpLinearLayout_vertical_Space,
                        mVerticalSpace
                    )
                    mIsFull = typedArray.getBoolean(R.styleable.WarpLinearLayout_isFull, mIsFull)
                    typedArray.recycle()
                }
            }
        }
    }
}