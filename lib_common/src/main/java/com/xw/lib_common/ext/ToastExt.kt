package com.xw.lib_common.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.xw.lib_common.base.BaseApplication

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

fun Context.toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.applicationContext, content, duration).apply {
        show()
    }
}
fun Context.toastLong(content: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this.applicationContext, content, duration).apply {
        show()
    }
}

fun Context.toast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(getString(id), duration)
}

fun Context.longToast(content: String) {
    toast(content, Toast.LENGTH_LONG)
}

fun Context.longToast(@StringRes id: Int) {
    toast(id, Toast.LENGTH_LONG)
}

fun Any.toast(context: Context, content: String, duration: Int = Toast.LENGTH_SHORT) {
    context.toast(content, duration)
}

fun toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    BaseApplication.CONTEXT.toast(content, duration)
}

fun Fragment.toast(content: String, duration: Int = Toast.LENGTH_SHORT){
    this.requireActivity().toast(content)
}

fun Any.toast(context: Context, @StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    context.toast(id, duration)
}

fun Any.longToast(context: Context, content: String) {
    context.longToast(content)
}

fun Any.longToast(context: Context, @StringRes id: Int) {
    context.longToast(id)
}
