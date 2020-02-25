package com.xw.lib_common.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.databinding.BindingAdapter
import com.xw.lib_common.R
import com.xw.lib_common.base.BaseApplication
import com.xw.lib_common.ext.*
import com.xw.lib_common.utils.GlideUtils
import com.xw.lib_coremodel.model.bean.home.AlbumItemInfo
import com.xw.lib_coremodel.model.bean.home.ArtistInfo
import com.xw.lib_coremodel.model.bean.home.Privilege
import com.xw.lib_coremodel.model.bean.home.TopListTracks
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */


@BindingAdapter(value = ["imageFromUrl", "radius"])
fun bindImageFromUrl(
    view: ImageView,
    imageUrl: String?,
    radius: Int
) {
    if (!imageUrl.isNullOrEmpty()) {
        GlideUtils.displayCircle(view, imageUrl, radius)
    }
}

@BindingAdapter(value = ["loadAvatarUrl", "loadWidth", "loadHeight"])
fun bindLoadAvatarUrl(
    imageView: ImageView,
    url: String,
    loadWidth: Int,
    loadHeight: Int
) {
    GlideUtils.loadImageCircleCrop(
        imageView.context,
        imageView,
        url.specifyLoad(loadWidth, loadHeight),
        R.drawable.icon_user_circle
    )
}

@BindingAdapter("imgGaussian")
fun bindImgGaussian(view: ImageView, url: String?) {
    if (url.isNullOrEmpty().not())
        GlideUtils.loadGaussian(view, url!!)
}

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    if (isGone) {
        view.gone()
    } else {
        view.show()
    }
}

@BindingAdapter("isInvisible")
fun bindIsInvisible(view: View, isInvisible: Boolean) {
    if (isInvisible) {
        view.invisible()
    } else {
        view.show()
    }
}

@BindingAdapter("onLongClick")
fun bindOnLongClick(view: View, onLongClickListener: View.OnLongClickListener) {
    view.setOnLongClickListener(onLongClickListener)
}

@BindingAdapter(value = ["titleColor", "titleSize", "showMoreIcon"])
fun bindTitleView(view: TextView, titleColor: Int, titleSize: Float, showMoreIcon: Boolean) {
    view.setTextColor(titleColor)
    view.textSize = titleSize
    if (showMoreIcon) {
        val drawable = ContextCompat.getDrawable(BaseApplication.CONTEXT, R.drawable.ic_more)
        view.setCompoundDrawables(null, null, drawable, null)
        view.compoundDrawablePadding = 10f.dip2px()
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter(value = ["num", "tracks"])
fun bindSongAndName(view: TextView, num: Int, tracks: List<TopListTracks>?) {
    if (tracks.isNullOrEmpty()) return

    when (num) {
        1 -> {
            if (tracks.isNullOrEmpty().not())
                view.text = "$num.${tracks[0].first}-${tracks[0].second}"
        }
        2 -> {
            if (tracks.size > 1)
                view.text = "$num.${tracks[1].first}-${tracks[1].second}"
        }
        3 -> {
            if (tracks.size > 2)
                view.text = "$num.${tracks[2].first}-${tracks[2].second}"
        }
        else -> view.gone()
    }

}

@BindingAdapter("playCount")
fun bindPlayCount(textView: TextView, count: Long) {
    textView.text = count.formatting()
}

@BindingAdapter("commentCount")
fun bindCommentCount(textView: TextView, count: Long) {
    textView.text = count.formatting()
}

@SuppressLint("SetTextI18n", "SimpleDateFormat")
@BindingAdapter("updateTime")
fun bindUpdate(textView: TextView, update: Long) {
    val result: String
    if (update < 0) return
    val format = SimpleDateFormat("MM月dd日")
    result = format.format(Date(update))

    textView.text = "最近更新:$result"
}

@SuppressLint("SetTextI18n")
@BindingAdapter(value = ["ar", "al"])
fun bindArAndAl(textView: TextView, ar: List<ArtistInfo>, al: AlbumItemInfo) {
    val reduce = ar.map { it.name }.reduce { acc, s -> "$acc/$s" }
    textView.text = "$reduce-${al.name}"
}

@BindingAdapter(value = ["songName", "alia"])
fun bindSongAlia(textView: TextView, name: String, alia: List<String>?) {
    val stringBuilder = SpannableStringBuilder()
        .color(
            ContextCompat.getColor(
                textView.context,
                R.color.black_2d2d2d
            )
        ) { append(name) }
    if (alia.isNullOrEmpty().not()) {
        val reduce = alia!!.reduce { acc, s -> "$acc/$s" }
        stringBuilder.color(
            ContextCompat.getColor(
                textView.context,
                R.color.black_979797
            )
        ) { append("($reduce)") }
    }
    textView.text = stringBuilder
}

@BindingAdapter("songCount")
fun bindPlayAllTxt(textView: TextView, count: Int) {
    textView.text = SpannableStringBuilder()
        .color(
            ContextCompat.getColor(
                textView.context,
                R.color.black_979797
            )
        ) { append("(共${count}首）") }
}

@BindingAdapter("artists")
fun bindArtistsName(textView: TextView, artists: List<ArtistInfo>) {
    textView.text = artists.map { it.name }.reduce { acc, s -> "$acc/$s" }
}

@BindingAdapter("privilege")
fun bindPrivilegeTag(imageView: ImageView, privilege: Privilege?) {
    if (privilege == null) {
        imageView.gone()
    } else {
        var sqBitmap: Bitmap? = null
        var vipBitmap: Bitmap? = null
        var djBitmap: Bitmap? = null
        if (privilege.maxbr == 999000) {
            sqBitmap =
                BitmapFactory.decodeResource(BaseApplication.CONTEXT.resources, R.drawable.icon_sq)
        }
        if (privilege.fee == 1) {
            vipBitmap =
                BitmapFactory.decodeResource(BaseApplication.CONTEXT.resources, R.drawable.icon_vip)
        }
        if (privilege.fee != 1 && privilege.fee != 8) {
            djBitmap =
                BitmapFactory.decodeResource(BaseApplication.CONTEXT.resources, R.drawable.icon_dj)
        }

        if (sqBitmap != null || vipBitmap != null || djBitmap != null) {
            val sqWidth: Int = sqBitmap?.width ?: 0
            val sqHeight: Int = sqBitmap?.height ?: 0
            val vipWidth: Int = vipBitmap?.width ?: 0
            val vipHeight: Int = vipBitmap?.height ?: 0
            val djWidth: Int = djBitmap?.width ?: 0
            val djHeight: Int = djBitmap?.height ?: 0
            val bitmap = Bitmap.createBitmap(
                sqWidth + vipWidth + djWidth,
                sqHeight.coerceAtLeast(vipHeight).coerceAtLeast(djHeight),
                Bitmap.Config.ARGB_8888
            )
            try {
                val canvas = Canvas(bitmap)
                vipBitmap?.let {
                    canvas.drawBitmap(it, (sqWidth + djWidth).toFloat(), 0f, null)
                }
                sqBitmap?.let {
                    canvas.drawBitmap(it, djWidth.toFloat(), 0f, null)
                }
                djBitmap?.let {
                    canvas.drawBitmap(it, 0f, 0f, null)
                }
                canvas.save()
                canvas.restore()
                imageView.show()
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                imageView.gone()
            }

        } else {
            imageView.gone()
        }

    }

}



