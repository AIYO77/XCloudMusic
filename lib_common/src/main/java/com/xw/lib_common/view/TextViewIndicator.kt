package com.xw.lib_common.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.xw.lib_common.R
import com.xw.lib_common.ext.getColor
import com.xw.lib_common.ext.getDrawable
import com.xw.lib_common.utils.AnimationUtils

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 首页头部指示器简单实现
 */
class TextViewIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var textMe: AppCompatTextView
    private var textHome: AppCompatTextView
    private var textVideo: AppCompatTextView

    //默认选中
    private var curSelctIndex = 1

    private var onChildClickListener: OnChildClickListener? = null

    init {
        val view = View.inflate(context, R.layout.lib_common_text_indicator, this)
        textMe = view.findViewById(R.id.me)
        textMe.setOnClickListener(this)
        textHome = view.findViewById(R.id.home)
        textHome.setOnClickListener(this)
        textVideo = view.findViewById(R.id.video)
        textVideo.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == null) return
        when (v) {
            textMe -> {
                if (curSelctIndex == 0) return
                onChildClickListener?.onChildClick(0)
            }
            textHome -> {
                if (curSelctIndex == 1) return
                onChildClickListener?.onChildClick(1)
            }
            textVideo -> {
                if (curSelctIndex == 2) return
                onChildClickListener?.onChildClick(2)
            }
        }
    }

    fun setIndex(position: Int) {
        when (position) {
            0 -> {
                showTextMe()
            }
            1 -> {
                showTextHome()
            }
            2 -> {
                showTextVideo()
            }
        }
    }

    private fun showTextMe() {
        textViewChange(textMe, true)
        if (curSelctIndex == 1) {
            textViewChange(textHome, false)
        } else if (curSelctIndex == 2) {
            textViewChange(textVideo, false)
        }
        curSelctIndex = 0
    }

    private fun showTextHome() {
        textViewChange(textHome, true)
        if (curSelctIndex == 0) {
            textViewChange(textMe, false)
        } else if (curSelctIndex == 2) {
            textViewChange(textVideo, false)
        }
        curSelctIndex = 1
    }

    private fun showTextVideo() {
        textViewChange(textVideo, true)
        if (curSelctIndex == 0) {
            textViewChange(textMe, false)
        } else if (curSelctIndex == 1) {
            textViewChange(textHome, false)
        }
        curSelctIndex = 2
    }

    private fun textViewChange(textView: AppCompatTextView, isScale: Boolean) {

        if (isScale) {
            textView.apply {
                AnimationUtils.scale(this, 1f, 1.2f)
                setTextColor(getColor(R.color.colorWhite))
            }
        } else {
            textView.apply {
                AnimationUtils.scale(this, 1.2f, 1f)
                setTextColor(getColor(R.color.colorWhiteHalf))
            }
        }
    }

    fun setOnChildClickListener(onChildClickListener: OnChildClickListener) {
        this.onChildClickListener = onChildClickListener
    }

    interface OnChildClickListener {
        fun onChildClick(index: Int)
    }
}