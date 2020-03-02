package com.xw.lib_common.base

import android.content.Context
import com.masterxing.launchstarter.LaunchStarter
import com.xw.lib_common.task.InitArouterTask
import com.xw.lib_common.task.InitLiveBusTask
import com.xw.lib_common.task.InitLoggerTask
import com.xw.lib_coremodel.CoreApplication
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

        LaunchStarter.init(this)
        LaunchStarter.createInstance().apply {
            addTask(InitArouterTask())
            addTask(InitLoggerTask())
            addTask(InitLiveBusTask())
            start()
            //有需要等待完成结束的任务
            await()
        }

    }
}