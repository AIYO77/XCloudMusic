package com.xw.lib_common.task

import com.masterxing.launchstarter.task.Task
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class InitLoggerTask : Task() {
    override fun needWait(): Boolean {
        return true
    }

    override fun run() {
        val strategy = PrettyFormatStrategy.newBuilder()
            .tag("xing")
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(strategy))
    }
}