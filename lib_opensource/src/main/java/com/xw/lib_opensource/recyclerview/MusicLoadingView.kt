package com.xw.lib_opensource.recyclerview

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.xw.lib_opensource.R

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class MusicLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var animationDrawable: AnimationDrawable
    private var title: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.x_refresh_header, this)
        val img = findViewById<ImageView>(R.id.img)
        animationDrawable = img.drawable as AnimationDrawable
        if (animationDrawable.isRunning) {
            animationDrawable.stop()
        }
        title = findViewById(R.id.msg)
    }

    fun startLoading() {
        if (animationDrawable.isRunning.not()) {
            animationDrawable.start()
        }
    }

    fun stopLoading() {
        if (animationDrawable.isRunning) {
            animationDrawable.stop()
        }
    }

    fun setMsg(@StringRes resId: Int) {
        title.text = context.getText(resId)
    }
}