package com.xw.lib_common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.xw.lib_common.R
import com.xw.lib_common.ext.getDrawable
import com.xw.lib_common.ext.gone
import com.xw.lib_common.ext.show
import com.orhanobut.logger.Logger

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 通用内容标题栏 简单封装
 */
class BaseTitleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private var titleView: TextView
    private var rightTxt: TextView

    private var clickListener: ClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.item_base_title_view, this)
        titleView = findViewById(R.id.titleTxt)
        rightTxt = findViewById(R.id.rightTxt)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.BaseTitleView)
            try {
                val title = typedArray.getString(R.styleable.BaseTitleView_titleString)
                if (title.isNullOrEmpty()) {
                    titleView.gone()
                } else {
                    setTitleTxt(title)
                }

                val canClick = typedArray.getBoolean(R.styleable.BaseTitleView_titleCanClick, false)
                titleCanClick(canClick)
                val rightString = typedArray.getString(R.styleable.BaseTitleView_rightTxt)
                if (rightString.isNullOrEmpty()) {
                    rightTxt.gone()
                } else {
                    setRight(rightString)
                }

            } catch (e: Exception) {
                Logger.e(e.localizedMessage)
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setTitle(title: String): BaseTitleView {
        setTitleTxt(title)
        return this
    }

    fun setRightTxt(right: String): BaseTitleView {
        setRight(right)
        return this
    }

    fun setTitleCanClick(canClick: Boolean): BaseTitleView {
        titleCanClick(canClick)
        return this
    }

    fun setClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.titleTxt -> {
                clickListener?.titleClick()
            }
            R.id.rightTxt -> {
                clickListener?.rightClick()
            }
        }
    }

    private fun setTitleTxt(title: String) {
        titleView.text = title
        titleView.show()
    }

    private fun setRight(right: String) {
        rightTxt.text = right
        rightTxt.show()
        rightTxt.setOnClickListener(this)
    }

    private fun titleCanClick(canClick: Boolean) {
        if (canClick) {
            titleView.setOnClickListener(this)
            val drawable = getDrawable(R.drawable.ic_more)
            drawable?.let { drawable1 ->
                titleView.setCompoundDrawables(null, null, drawable1, null)
            }
        }
    }

    interface ClickListener {
        fun titleClick()
        fun rightClick()
    }
}