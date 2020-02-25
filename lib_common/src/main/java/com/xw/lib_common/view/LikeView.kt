package com.xw.lib_common.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.UiThread
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.orhanobut.logger.Logger
import com.xw.lib_common.R
import com.xw.lib_common.ext.formatting
import com.xw.lib_common.ext.getColor
import com.xw.lib_common.ext.getDrawable

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class LikeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var mIsLike = false
    private var mLikeCount: Long = 0

    private var mLikeIV: AppCompatImageView
    private var mLikeCounTxt: AppCompatTextView

    init {
        val view = View.inflate(context, R.layout.view_like_view, this)
        setOnClickListener(this)
        mLikeIV = view.findViewById(R.id.likeImg)
        mLikeCounTxt = view.findViewById(R.id.likeCount)

        refresh(false)
    }

    fun setLiked(like: Boolean) {
        mIsLike = like
        refresh()
    }

    fun setLikeCount(count: Long) {
        mLikeCount = count
        refresh()
    }

    override fun onClick(v: View?) {
        onLikeStateChangedListener?.onLikeChanged(mIsLike)

        mIsLike = mIsLike.not()
        refresh()
    }

    @UiThread
    fun refresh(needAnimation:Boolean = true) {
        if (mIsLike) {
            mLikeCounTxt.setTextColor(getColor(R.color.colorAccent))
            mLikeIV.setImageDrawable(getDrawable(R.drawable.ic_liked))
            if (needAnimation){

            }
        } else {
            mLikeCounTxt.setTextColor(getColor(R.color.color_606060))
            mLikeIV.setImageDrawable(getDrawable(R.drawable.ic_like))
        }
        mLikeCounTxt.text = mLikeCount.formatting()
    }

    var onLikeStateChangedListener: OnLikeStateChangedListener? = null

    interface OnLikeStateChangedListener {
        fun onLikeChanged(isLiked: Boolean)
    }
}