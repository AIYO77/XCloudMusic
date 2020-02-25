package com.xw.lib_coremodel

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.gson.Gson
import kotlin.properties.Delegates

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class CoreApplication : MultiDexApplication() {

    companion object {
        var CONTEXT: Context by Delegates.notNull()
        var GSON: Gson by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        CONTEXT = applicationContext
        GSON = Gson()
    }

}