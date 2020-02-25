package com.xw.lib_coremodel.utils

import java.lang.ref.WeakReference

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class DataHolder {
    private val dataList = hashMapOf<String, Any>()

    companion object {
        @Volatile
        private var instance: DataHolder? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: DataHolder().also { instance = it }
            }
    }

    fun setData(key: String, o: Any) {
        val value = WeakReference(o)
        dataList[key] = value
    }

    fun getData(key: String): Any? {
        val reference = dataList[key] as? WeakReference<*>
        if (reference != null) {
            return reference.get()
        }
        return null
    }

    fun remove(key: String) {
        if (dataList.containsKey(key))
            dataList.remove(key)
    }
}