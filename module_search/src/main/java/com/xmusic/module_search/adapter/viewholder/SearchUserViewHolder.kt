package com.xmusic.module_search.adapter.viewholder

import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.R
import com.xmusic.module_search.databinding.ItemSearchUserBinding
import com.xmusic.module_search.utils.blackColor
import com.xmusic.module_search.utils.getKeywordsSpanner
import com.xmusic.module_search.utils.grayColor
import com.xmusic.module_search.utils.keywordsColor
import com.xw.lib_common.ext.getDrawable
import com.xw.lib_common.ext.gone
import com.xw.lib_common.ext.show
import com.xw.lib_coremodel.model.bean.UserInfo

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchUserViewHolder(private val binding: ItemSearchUserBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(userInfo: UserInfo, keywords: String) {
        with(binding) {
            user = userInfo
            userName.text =
                getKeywordsSpanner(userInfo.nickname, keywords, blackColor, keywordsColor, 13)
            userName.setCompoundDrawables(
                null, null, if (userInfo.gender == 1) {
                    getDrawable(R.drawable.icon_man)
                } else {
                    getDrawable(R.drawable.icon_woman)
                }, null
            )
            var des = ""
            var tag: Drawable? = null
            when (userInfo.userType) {
                2, 10 -> {
                    des = userInfo.description
                    tag = getDrawable(R.drawable.icon_user_tag_vip)
                }
                4 -> {
                    des = "网易云音乐人"
                    tag = getDrawable(R.drawable.icon_user_tag_musicer)
                }
            }
            if (des.isNotEmpty()) {
                userDes.show()
                userDes.text = getKeywordsSpanner(des, keywords, grayColor, keywordsColor, 10)
            } else {
                userDes.gone()
            }
            if (tag != null) {
                userTag.show()
                userTag.setImageDrawable(tag)
            } else {
                userTag.gone()
            }
            focusTv.setOnClickListener {

            }
            executePendingBindings()
        }
    }
}