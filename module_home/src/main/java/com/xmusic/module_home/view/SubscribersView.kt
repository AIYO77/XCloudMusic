package com.xmusic.module_home.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.orhanobut.logger.Logger
import com.xw.lib_common.ext.*
import com.xw.lib_common.utils.GlideUtils
import com.xw.lib_coremodel.model.bean.UserInfo
import com.xmusic.module_home.R
import kotlinx.android.synthetic.main.layout_subscribers_view.view.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SubscribersView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.layout_subscribers_view, this)
        setOnClickListener {
            toast("全部收藏的人")
        }
    }

    fun setData(users: List<UserInfo>?, count: Long? = 0) {
        if (users.isNullOrEmpty()) return
        Logger.d("users size ${users.size}  count = $count")
        subCount.text = context.getString(R.string.label_sub_count, count!!.formatting())
        if (fiveUserLayout.childCount >= 5) return
        users.subList(0, if (users.size > 5) 5 else users.size).forEachIndexed { index, userInfo ->
            val view = AppCompatImageView(context)
            val params = LinearLayout.LayoutParams(30f.dip2px(), 30f.dip2px()).apply {
                marginStart = 13f.dip2px()
            }
            view.setOnClickListener {
                toast(userInfo.nickname)
            }
            fiveUserLayout.addView(view, index, params)
            GlideUtils.loadImageCircleCrop(
                context,
                view,
                userInfo.avatarUrl,
                R.drawable.icon_user_circle
            )
        }
    }
}