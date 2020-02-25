package com.xw.lib_common.ext

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.xw.lib_common.utils.GlideApp
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

fun Context.loadTransform(url: String, duration: Int, radius: Int, sampling: Int,callBack:(Drawable)->Unit) {
    GlideApp.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(duration))
        .transform(BlurTransformation(radius, sampling))
        .into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                callBack.invoke(resource)
            }
        })

}