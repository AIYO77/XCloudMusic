package com.xw.lib_common.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.xw.lib_common.R
import com.xw.lib_coremodel.model.bean.home.Banner
import com.ms.banner.holder.BannerViewHolder

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class CustomBannerViewHolder : BannerViewHolder<Banner> {
    private var imageView: AppCompatImageView? = null

    private var title: AppCompatTextView? = null

    override fun onBind(context: Context?, position: Int, data: Banner?) {
        if (context == null || imageView == null) return
        GlideUtils.displayCircle(imageView!!, data!!.pic, 10)
        title?.text = data.typeTitle
        title?.setBackgroundResource(ColorUtils.getColorOfName(data.titleColor))
    }

    override fun createView(context: Context?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.banner_item, null)
        imageView = view.findViewById<AppCompatImageView>(R.id.bannerImg)
        title = view.findViewById(R.id.bannerTitle)
        return view
    }

}