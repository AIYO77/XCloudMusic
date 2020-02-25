package com.xw.lib_common.ext

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SpringSystem
import com.orhanobut.logger.Logger
import com.xw.lib_common.base.BaseApplication

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

fun <T> Boolean.yes(trueValue: () -> T) = TernaryOperator(trueValue, this)

fun <T> TernaryOperator<T>.no(falseValue: () -> T) = if (bool) trueValue() else falseValue()
class TernaryOperator<out T>(val trueValue: () -> T, val bool: Boolean)


fun getColor(@ColorRes id: Int): Int {
    return ContextCompat.getColor(BaseApplication.CONTEXT, id)
}

fun Context.getColorExt(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun getDrawable(@DrawableRes id: Int): Drawable? {
    return ContextCompat.getDrawable(BaseApplication.CONTEXT, id)
}

fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
    if (formatArgs.isNullOrEmpty().not()) {
        return String.format(BaseApplication.CONTEXT.getString(id), formatArgs)
    }
    return BaseApplication.CONTEXT.getString(id)
}

fun Long.formatting(): String {
    return when (this) {
        in 0..99999 -> {
            this.toString()
        }
        in 100000..99999999 -> {
            "${String.format("%.1f", this.toFloat() / 10000.1)}万"
        }
        else -> {
            "${String.format("%.1f", this.toFloat() / 100000000.1)}亿"
        }
    }
}

fun checkPermission(name: String): Boolean {
    return if (fromM()) {
        PackageManager.PERMISSION_GRANTED == BaseApplication.CONTEXT.checkSelfPermission(name)
    } else {
        true
    }
}

fun Activity.hideSoftInput() {
    val imm: InputMethodManager? =
        ContextCompat.getSystemService<InputMethodManager>(
            this,
            InputMethodManager::class.java
        )
    imm?.hideSoftInputFromWindow(
        this.currentFocus?.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

fun Context.getScreenWidth(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun Context.getScreenHeight(): Int {
    return this.resources.displayMetrics.heightPixels
}

fun String.specifyLoad(width: Int, height: Int): String {
    val realWidth = width.toFloat().dip2px()
    val realHeight = height.toFloat().dip2px()
    Logger.d("realWidth = $realWidth realHeight = $realHeight")
    return "$this?param=${realWidth}y${realHeight}"
}

fun fromM() = fromSpecificVersion(Build.VERSION_CODES.M)
fun beforeM() = beforeSpecificVersion(Build.VERSION_CODES.M)
fun fromN() = fromSpecificVersion(Build.VERSION_CODES.N)
fun beforeN() = beforeSpecificVersion(Build.VERSION_CODES.N)
fun fromO() = fromSpecificVersion(Build.VERSION_CODES.O)
fun beforeO() = beforeSpecificVersion(Build.VERSION_CODES.O)

fun fromL() = fromSpecificVersion(Build.VERSION_CODES.LOLLIPOP)
fun beforeL() = beforeSpecificVersion(Build.VERSION_CODES.LOLLIPOP)

fun fromK() = fromSpecificVersion(Build.VERSION_CODES.KITKAT)
fun beforeK() = beforeSpecificVersion(Build.VERSION_CODES.KITKAT)

fun fromSpecificVersion(version: Int): Boolean = Build.VERSION.SDK_INT >= version
fun beforeSpecificVersion(version: Int): Boolean = Build.VERSION.SDK_INT < version