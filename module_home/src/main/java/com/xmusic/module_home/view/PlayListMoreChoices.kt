package com.xmusic.module_home.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import com.xw.lib_common.ext.*
import com.xmusic.module_home.R
import kotlinx.android.synthetic.main.view_playlist_more_choices.view.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListMoreChoices @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var multipleView: View? = null

    private var collectTxt: AppCompatTextView? = null
    private var disCollectTxt: AppCompatTextView? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_playlist_more_choices, this)
        isFocusable = true
        isClickable = true
    }

    fun showMultiple(onclick: OnClickListener) {
        try {
            multipleView = multipleVs.inflate()
        } catch (e: Exception) {
            multipleVs.show()
        } finally {
            multipleView?.setOnClickListener(onclick)
        }
    }

    fun setSubscribedCount(
        isCollect: Boolean,
        count: Long,
        onCollectClick: OnClickListener,
        onDisClick: OnClickListener
    ) {
        try {
            collectTxt = collectVs.inflate() as AppCompatTextView?
            collectTxt?.setOnClickListener(onCollectClick)
            disCollectTxt = disCollectVs.inflate() as AppCompatTextView?
            disCollectTxt?.setOnClickListener(onDisClick)
        } catch (e: Exception) {
            isCollect.yes {
                //已经收藏
                disCollectVs?.show()
                collectVs?.gone()
            }.no {
                //未收藏
                disCollectVs?.gone()
                collectVs?.show()
            }
        } finally {
            val subCount = count.formatting()
            disCollectTxt?.text = subCount
            collectTxt?.text = context.getString(R.string.label_add_collect, subCount)
            isCollect.yes {
                disCollectTxt?.show()
                collectTxt?.gone()
            }.no {
                disCollectTxt?.gone()
                collectTxt?.show()
            }
        }
    }

//    fun setCollectClickLi
}