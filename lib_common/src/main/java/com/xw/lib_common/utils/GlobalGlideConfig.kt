package com.xw.lib_common.utils

import android.content.Context
import androidx.annotation.NonNull
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
@GlideModule
class GlobalGlideConfig: AppGlideModule() {

    private val diskSize = 1024 * 1024 * 100
    private val memorySize = Runtime.getRuntime().maxMemory().toInt() / 8  // 取1/8最大内存作为最大缓存

    override fun applyOptions(@NonNull context: Context, @NonNull builder: GlideBuilder) {
        // 定义缓存大小和位置
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskSize.toLong()))  //内存中

        // 自定义内存和图片池大小
        builder.setMemoryCache(LruResourceCache(memorySize.toLong()))
        builder.setBitmapPool(LruBitmapPool(memorySize.toLong()))

        // 定义图片格式
        builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
    }
}