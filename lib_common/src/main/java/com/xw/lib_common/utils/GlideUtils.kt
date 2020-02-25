package com.xw.lib_common.utils

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.xw.lib_common.R
import com.xw.lib_common.ext.dip2px
import com.xw.lib_common.ext.specifyLoad
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object GlideUtils {

    fun displayCircle(imageView: ImageView?, imageUrl: String?, dp: Int) {
        if (imageUrl == null || imageView == null) return

        GlideApp.with(imageView.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(
                CenterCrop(), RoundedCornersTransformation(
                    dp.toFloat().dip2px(), 0,
                    RoundedCornersTransformation.CornerType.ALL
                )
            )
            .into(imageView)
    }


    fun loadImage(imageView: ImageView, url: String?) {
        GlideApp.with(imageView.context)
            .load(url)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.color.black_fifty_percent)
            .into(imageView)
    }

    fun loadImageRadius(
        imageView: ImageView,
        url: String,
        radius: Int = 0,
        defaultsPic: Int = R.color.color_eeeeee
    ) {
        val requestOptions = getRequestOptions(defaultsPic)
            .transform(CenterCrop(), RoundedCornersTransformation(radius, 0))
        loadBitmapPic(imageView, url, requestOptions)
    }

    fun loadImageCircleCrop(
        context: Context,
        view: ImageView,
        url: String?,
        @DrawableRes holder: Int? = null
    ) {
        GlideApp.with(context)
            .load(url)
            .apply(RequestOptions.circleCropTransform()).apply {
                if (holder != null) {
                    placeholder(holder)
                        .error(holder)
                }
            }
            .into(view)
    }

    fun loadImageCircleCrop(
        context: Context,
        view: ImageView,
        url: String?,
        width: Int,
        height: Int,
        @DrawableRes holder: Int
    ) {
        GlideApp.with(context)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(holder)
            .override(width, height)
            .error(holder)
            .into(view)
    }

    fun loadGaussian(imageView: ImageView, url: String, radius: Int = 80, duration: Int = 500) {
        displayGaussian(imageView.context, url, imageView, radius, duration)
    }

    private fun loadBitmapPic(imageView: ImageView, url: String, requestOptions: RequestOptions) {
        GlideApp.with(imageView.context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(requestOptions)
            .into(imageView)
    }


    private fun getRequestOptions(defaultsPic: Int): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(defaultsPic)
            .error(defaultsPic)
    }

    /**
     * 显示高斯模糊效果
     */
    private fun displayGaussian(
        context: Context,
        url: String,
        imageView: ImageView,
        radius: Int = 80,
        duration: Int = 500
    ) {
        GlideApp.with(context)
            .load(url)
            .apply(RequestOptions().centerCrop())
            .error(R.drawable.stackblur_default)
            .placeholder(R.drawable.stackblur_default)
            .transition(DrawableTransitionOptions.withCrossFade(duration))
            .transform(BlurTransformation(radius, 8))
            .into(imageView)
    }


}