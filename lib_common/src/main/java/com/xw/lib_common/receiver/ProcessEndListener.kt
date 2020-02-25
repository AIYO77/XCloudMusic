package com.xw.lib_common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Process
import com.xw.lib_common.service.MusicPlayer

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object ProcessEndListener : BroadcastReceiver() {

    private var ctx: Context? = null

    private const val actionName = "com.xw.xmusic.android.END_PROCESS"

    fun register(context: Context) {
        context.registerReceiver(ProcessEndListener, IntentFilter(actionName))
        this.ctx = context
    }

    fun send(context: Context) {
        context.sendBroadcast(Intent().apply { action = actionName })
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        ctx?.apply {
            unregisterReceiver(this@ProcessEndListener)
        }
        ctx = null
        MusicPlayer.stop()
        Process.killProcess(Process.myPid())
    }
}