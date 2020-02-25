package com.xw.lib_common.base

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.xing.launchstarter.TaskDispatcher
import com.xw.lib_common.task.InitArouterTask
import com.xw.lib_common.task.InitLiveBusTask
import com.xw.lib_common.task.InitLoggerTask
import com.xw.lib_coremodel.CoreApplication
import java.util.concurrent.Executors
import kotlin.properties.Delegates

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
open class BaseApplication : CoreApplication() {

    companion object {
        var CONTEXT: Context by Delegates.notNull()

        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        //线程数量
        val CORE_POOL_SIZE = 2.coerceAtLeast((CPU_COUNT - 1).coerceAtMost(4))
    }

    override fun onCreate() {
        super.onCreate()
        CONTEXT = applicationContext

        TaskDispatcher.init(this)
        TaskDispatcher.createInstance().apply {
            addTask(InitArouterTask())
            addTask(InitLoggerTask())
            addTask(InitLiveBusTask())
            start()
        }.await()

    }
}