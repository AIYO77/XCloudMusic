package com.xw.lib_coremodel.model.api

import android.content.Context
import android.net.ConnectivityManager

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object NetWorkUtils {

    /**
     * 判断网络是否连通
     */
    fun isNetworkConnected(context: Context?): Boolean {
        return try {
            if (context != null) {
                val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val info = cm.activeNetworkInfo
                info != null && info.isConnected
            } else {
                /**如果context为空，就返回false，表示网络未连接 */
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }


    }

}