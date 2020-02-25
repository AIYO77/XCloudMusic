package com.xw.lib_common.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import com.xw.lib_common.base.BaseApplication
import com.xw.lib_coremodel.model.api.NetWorkUtils

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 网络连接断开监听
 */
class NetWorkState(private val mContext: Context) : LiveData<Boolean>() {

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            value = NetWorkUtils.isNetworkConnected(BaseApplication.CONTEXT)
        }
    }

    override fun onActive() {
        super.onActive()

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        mContext.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onInactive() {
        super.onInactive()

        mContext.unregisterReceiver(broadcastReceiver)
    }
}