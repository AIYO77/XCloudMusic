package com.xw.lib_common.view

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import com.xw.lib_common.R
import com.xw.lib_common.ext.*
import com.orhanobut.logger.Logger
import kotlin.Exception

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class MusicToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr), View.OnClickListener {

    private val toolbarBg: AppCompatImageView
    private val ivBack: AppCompatImageView
    private val ivLeftOne: AppCompatImageView
    private val ivLeftTwo: AppCompatImageView
    private val txtTitle: AppCompatTextView
    private val txtSubTitle: AppCompatTextView

    private var mTitle: String? = ""
    private var mSubTitle: String? = ""
    private var mLeftOneIconId: Int = R.drawable.ic_more_vertical

    private var mLeftTwoIconId: Int = R.drawable.ic_search

    private var isShowLeftOne = true
    private var isShowLeftTwo = false

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MusicToolbar)
            try {
                mTitle = typedArray.getString(R.styleable.MusicToolbar_title)
                mSubTitle = typedArray.getString(R.styleable.MusicToolbar_subTitle)
                mLeftOneIconId = typedArray.getResourceId(
                    R.styleable.MusicToolbar_leftOneIcon,
                    R.drawable.ic_more_vertical
                )
                mLeftTwoIconId = typedArray.getResourceId(
                    R.styleable.MusicToolbar_leftTwoIcon,
                    R.drawable.ic_search
                )
                isShowLeftOne = typedArray.getBoolean(R.styleable.MusicToolbar_isShowLeftOne, true)
                isShowLeftTwo = typedArray.getBoolean(R.styleable.MusicToolbar_isShowLeftTwo, false)
            } catch (e: Exception) {
                Logger.e(e.localizedMessage)
            } finally {
                typedArray.recycle()
            }

        }
        val view = LayoutInflater.from(context).inflate(R.layout.layout_play_list_toolbar, this)
        ivBack = view.findViewById(R.id.icBack)
        ivBack.setOnClickListener(this)
        txtTitle = view.findViewById(R.id.title)
        txtSubTitle = view.findViewById(R.id.subTitle)
        ivLeftOne = view.findViewById(R.id.leftOne)
        ivLeftTwo = view.findViewById(R.id.leftTwo)

        toolbarBg = view.findViewById(R.id.toolbarBg)

        txtTitle.text = mTitle

        if (mSubTitle.isNullOrEmpty()) {
            txtSubTitle.gone()
        } else {
            txtSubTitle.show()
            txtSubTitle.text = mSubTitle
        }

        if (isShowLeftOne) {
            ivLeftOne.show()
            ivLeftOne.setImageResource(mLeftOneIconId)
            ivLeftOne.setOnClickListener(this)
        } else {
            ivLeftOne.gone()
        }

        if (isShowLeftTwo) {
            ivLeftTwo.show()
            ivLeftTwo.setImageResource(mLeftTwoIconId)
            ivLeftTwo.setOnClickListener(this)
        } else {
            ivLeftTwo.gone()
        }

        if (fromL()) {
            elevation = 0f
        }

    }

    override fun onClick(v: View) {
        when (v) {
            ivBack -> {
                if (context is Activity) {
                    (context as Activity).onBackPressed()
                }
            }
            ivLeftOne -> {
            }
            ivLeftTwo -> {
            }
        }
    }

    fun setToolBarTitle(title: String) {
        txtTitle.text = title
        txtTitle.isSelected = true
    }

    fun setToolBarSubTitle(subTitle: String?) {
        txtSubTitle.text = subTitle
        subTitle.isNullOrEmpty().yes {
            txtSubTitle.gone()
        }.no {
            txtSubTitle.show()
        }
    }

    fun setLeftOneIcon(@DrawableRes id: Int) {
        ivLeftOne.setImageResource(id)
        ivLeftOne.show()
    }

    fun setLeftTwoIcon(@DrawableRes id: Int) {
        ivLeftTwo.setImageResource(id)
        ivLeftTwo.show()
    }

    fun getToolbarBgView(): AppCompatImageView {
        return toolbarBg
    }

    fun loadBg(res: Drawable) {
        toolbarBg.setImageDrawable(res)
    }
}