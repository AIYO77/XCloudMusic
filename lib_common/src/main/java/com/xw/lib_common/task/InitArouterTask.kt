package com.xw.lib_common.task

import com.alibaba.android.arouter.launcher.ARouter
import com.masterxing.launchstarter.task.Task
import com.xw.lib_common.base.BaseApplication

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class InitArouterTask : Task() {
    override fun run() {
        //ARouter初始化
        ARouter.openLog()    // 打印日志
        ARouter.openDebug()
        ARouter.init(mContext as BaseApplication)
    }
}