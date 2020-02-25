package com.xw.lib_coremodel.utils

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 *
 * Desc:
 */
object JNIUtils {

    init {
        System.loadLibrary("algorithm-lib")
    }

    external fun getIndex(arr: IntArray, left: Int, right: Int, value: Int): Int

    //除法 保留小数一位
    external fun divson(a:Long,b:Long):Float

}
