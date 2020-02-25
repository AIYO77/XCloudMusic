package com.xw.lib_opensource.recyclerview

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.xw.lib_opensource.R

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class XLoadingMoreFooter @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var mText: TextView
    private var mAnimationDrawable: AnimationDrawable
    private var mIvProgress: ImageView

    companion object {
        const val STATE_LOADING = 0
        const val STATE_COMPLETE = 1
        const val STATE_NOMORE = 2
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.x_refresh_footer, this)
        mText = findViewById<View>(R.id.msg) as TextView
        mIvProgress = findViewById<View>(R.id.iv_progress) as ImageView
        mAnimationDrawable = mIvProgress.drawable as AnimationDrawable
        if (!mAnimationDrawable.isRunning) {
            mAnimationDrawable.start()
        }
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    fun setState(state: Int) {
        when (state) {
            STATE_LOADING -> {
                if (!mAnimationDrawable.isRunning) {
                    mAnimationDrawable.start()
                }
                mIvProgress.visibility = View.VISIBLE
                mText.text = context.getText(R.string.listview_loading)
                this.visibility = View.VISIBLE
            }
            STATE_COMPLETE -> {
                if (mAnimationDrawable.isRunning) {
                    mAnimationDrawable.stop()
                }
                mText.text = context.getText(R.string.listview_loading)
                this.visibility = View.GONE
            }
            STATE_NOMORE -> {
                if (mAnimationDrawable.isRunning) {
                    mAnimationDrawable.stop()
                }
                mText.text = context.getText(R.string.nomore_loading)
                mIvProgress.visibility = View.GONE
                this.visibility = View.VISIBLE
            }
        }
    }

    fun reSet() {
        this.visibility = View.GONE
    }
}