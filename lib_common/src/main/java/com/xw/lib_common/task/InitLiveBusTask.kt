package com.xw.lib_common.task

import com.jeremyliao.liveeventbus.LiveEventBus
import com.masterxing.launchstarter.task.Task

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class InitLiveBusTask : Task() {

    override fun needWait(): Boolean {
        return true
    }

    override fun run() {
        LiveEventBus.config()
            .lifecycleObserverAlwaysActive(true)
            .autoClear(true)
            .enableLogger(true)
    }
}