package com.xw.lib_coremodel.ext

import com.alibaba.android.arouter.launcher.ARouter

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */

fun String.navigation(){
    ARouter.getInstance().build(this).navigation()
}