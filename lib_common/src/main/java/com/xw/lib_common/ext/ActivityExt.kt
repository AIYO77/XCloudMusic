package com.xw.lib_common.ext

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat.getDisplay
import com.xw.lib_common.view.StatusBarView


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

fun Activity.setStatusBarColor(@ColorInt color: Int, statusBarAlpha: Int){

    if (fromL()) {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.window.statusBarColor = calculateStatusColor(color, statusBarAlpha)
    } else if (fromK()) {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val decorView = this.window.decorView as ViewGroup
        val count = decorView.childCount
        if (count > 0 && decorView.getChildAt(count - 1) is StatusBarView) {
            decorView.getChildAt(count - 1)
                .setBackgroundColor(calculateStatusColor(color, statusBarAlpha))
        } else {
            val statusView = createStatusBarView(this, color, statusBarAlpha)
            decorView.addView(statusView)
        }
        setRootView(this)
    }
}


/**
 * 设置根布局参数
 */
private fun setRootView(activity: Activity) {
    val rootView =
        (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
    rootView.fitsSystemWindows = true
    rootView.clipToPadding = true
}

/**
 * 生成一个和状态栏大小相同的半透明矩形条
 *
 * @param activity 需要设置的activity
 * @param color    状态栏颜色值
 * @param alpha    透明值
 * @return 状态栏矩形条
 */
private fun createStatusBarView(
    activity: Activity, @ColorInt color: Int,
    alpha: Int? = null
): StatusBarView {
    // 绘制一个和状态栏一样高的矩形
    val statusBarView = StatusBarView(activity, null)
    val params = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        getStatusBarHeight(activity)
    )
    statusBarView.layoutParams = params
    if (alpha == null) {
        statusBarView.setBackgroundColor(color)
    } else {
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha))
    }

    return statusBarView
}


/**
 * 计算状态栏颜色
 *
 * @param color color值
 * @param alpha alpha值
 * @return 最终的状态栏颜色
 */
private fun calculateStatusColor(@ColorInt color: Int, alpha: Int): Int {
    val a = 1 - alpha / 255f
    var red = color shr 16 and 0xff
    var green = color shr 8 and 0xff
    var blue = color and 0xff
    red = (red * a + 0.5).toInt()
    green = (green * a + 0.5).toInt()
    blue = (blue * a + 0.5).toInt()
    return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
}


/**
 * 获取状态栏高度
 *
 * @param context mContext
 * @return 状态栏高度
 */
fun getStatusBarHeight(context: Context): Int {
    // 获得状态栏高度
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    return context.resources.getDimensionPixelSize(resourceId)
}
