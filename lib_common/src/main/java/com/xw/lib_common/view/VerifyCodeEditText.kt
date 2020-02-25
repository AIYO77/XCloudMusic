package com.xw.lib_common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodSubtype
import androidx.appcompat.widget.AppCompatEditText
import com.xw.lib_common.R
import com.xw.lib_common.ext.dip2pxF
import com.xw.lib_common.ext.getColor
import com.xw.lib_common.listener.VerifyAction
import com.xw.lib_common.listener.VerifyAction.OnVerificationCodeChangedListener
import com.xw.lib_common.utils.CommonUtils


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class VerifyCodeEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr), VerifyAction, TextWatcher {

    private var mFigures //需要输入的位数
            = 0
    private var mVerCodeMargin //验证码之间的间距
            = 0
    private var mBottomSelectedColor //底部选中的颜色
            = getColor(R.color.black_2a2a2a)
    private var mBottomNormalColor //未选中的颜色
            = getColor(R.color.color_e6e6e6)
    private var mBottomLineHeight //底线的高度
            = 0f

    private var onCodeChangedListener: OnVerificationCodeChangedListener? = null
    private var mCurrentPosition = 0
    private var mEachRectLength = 0 //每个矩形的边长
    private var mBottomSelectedPaint: Paint? = null
    private var mBottomNormalPaint: Paint? = null

    init {
        initAttrs(attrs)
        setBackgroundColor(getColor(android.R.color.transparent)) //防止出现下划线
        initPaint()
        inputType = InputType.TYPE_CLASS_NUMBER
        isFocusableInTouchMode = true
        addTextChangedListener(this)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.VerifyCodeEditText)
            try {
                mFigures = ta.getInteger(R.styleable.VerifyCodeEditText_figures, 4)
                mVerCodeMargin =
                    ta.getDimension(R.styleable.VerifyCodeEditText_verCodeMargin, 0f).toInt()
                mBottomSelectedColor = ta.getColor(
                    R.styleable.VerifyCodeEditText_bottomLineSelectedColor,
                    currentTextColor
                )
                mBottomNormalColor = ta.getColor(
                    R.styleable.VerifyCodeEditText_bottomLineNormalColor,
                    getColor(R.color.color_e6e6e6)
                )
                mBottomLineHeight = ta.getDimension(
                    R.styleable.VerifyCodeEditText_bottomLineHeight,
                    2f.dip2pxF()
                )
            } finally {
                ta.recycle()
            }
        }
        layoutDirection = LAYOUT_DIRECTION_LTR
    }

    private fun initPaint() {
        mBottomSelectedPaint = Paint()
        mBottomNormalPaint = Paint()
        mBottomSelectedPaint?.color = mBottomSelectedColor
        mBottomNormalPaint?.color = mBottomNormalColor
        mBottomSelectedPaint?.strokeWidth = mBottomLineHeight
        mBottomNormalPaint?.strokeWidth = mBottomLineHeight
    }

    override fun setCursorVisible(visible: Boolean) {
        super.setCursorVisible(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthResult: Int
        val heightResult: Int

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        //最终的宽度
        widthResult = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            CommonUtils.getScreenWidth(context)
        }
        //每个矩形形的宽度
        mEachRectLength = ((widthResult - mVerCodeMargin * (mFigures - 1)) / mFigures)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        //最终的高度
        heightResult = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            mEachRectLength
        }
        setMeasuredDimension(widthResult, heightResult)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                requestFocus()
                setSelection(text?.length ?: 0)
                showKeyBoard(context)
                return false
            }
            MotionEvent.ACTION_UP -> {
                performClick()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        mCurrentPosition = text?.length ?: 0
        val width = mEachRectLength - paddingLeft - paddingRight
        val height = measuredHeight - paddingTop - paddingBottom

        //绘制文字
        val value = text.toString()
        for (i in value.indices) {
            canvas!!.save()
            val start = width * i + i * mVerCodeMargin
            val x = start + width / 2.toFloat()
            val paint = paint
            paint.textAlign = Paint.Align.CENTER
            paint.color = currentTextColor
            val fontMetrics = paint.fontMetrics
            val baseline = ((height - fontMetrics.bottom + fontMetrics.top) / 2
                    - fontMetrics.top)
            canvas.drawText(value[i].toString(), x, baseline, paint)
            canvas.restore()
        }
        //绘制底线
        for (i in 0 until mFigures) {
            canvas!!.save()
            val lineY = height - mBottomLineHeight / 2
            val start = width * i + i * mVerCodeMargin
            val end = width + start
            if (i < mCurrentPosition) {
                canvas.drawLine(
                    start.toFloat(),
                    lineY,
                    end.toFloat(),
                    lineY,
                    mBottomSelectedPaint!!
                )
            } else {
                canvas.drawLine(start.toFloat(), lineY, end.toFloat(), lineY, mBottomNormalPaint!!)
            }
            canvas.restore()
        }
    }

    override fun setFigures(figures: Int) {
        mFigures = figures
        postInvalidate()
    }

    override fun setVerCodeMargin(margin: Int) {
        mVerCodeMargin = margin
        postInvalidate()
    }

    override fun setBottomSelectedColor(bottomSelectedColor: Int) {
        mBottomSelectedColor = bottomSelectedColor
        postInvalidate()
    }

    override fun setBottomNormalColor(bottomNormalColor: Int) {
        mBottomNormalColor = bottomNormalColor
        postInvalidate()
    }

    override fun setBottomLineHeight(bottomLineHeight: Int) {
        mBottomLineHeight = bottomLineHeight.toFloat()
        postInvalidate()
    }

    override fun setOnVerificationCodeChangedListener(listener: OnVerificationCodeChangedListener) {
        this.onCodeChangedListener = listener
    }

    override fun afterTextChanged(s: Editable?) {
        mCurrentPosition = text?.length ?: 0
        postInvalidate()
        if (text?.length == mFigures) {
            onCodeChangedListener?.onInputCompleted(text ?: "")
        } else if (text?.length ?: 0 > mFigures) {
            text?.delete(mFigures, text!!.length)
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        mCurrentPosition = text?.length ?: 0
        postInvalidate()
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        mCurrentPosition = text?.length ?: 0
        postInvalidate()
        onCodeChangedListener?.onVerCodeChanged(text ?: "", start, lengthBefore, lengthAfter)
    }


    private fun showKeyBoard(context: Context) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }

}