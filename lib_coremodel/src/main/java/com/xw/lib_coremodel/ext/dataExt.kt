package com.xw.lib_coremodel.ext

import android.app.Activity
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.JsonParseException
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.CoreApplication
import com.xw.lib_coremodel.NetContacts
import com.xw.lib_coremodel.data.AppDatabase
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.bean.BaseHttpResponse
import com.xw.lib_coremodel.model.bean.search.Composite
import com.xw.lib_coremodel.model.bean.search.CompositeCommon
import com.xw.lib_coremodel.provider.RouterPath
import com.xw.lib_coremodel.utils.PreferencesUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONException
import retrofit2.HttpException
import java.lang.reflect.Method
import java.net.ConnectException
import java.util.*
import javax.net.ssl.SSLHandshakeException

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */


suspend fun executeResponse(
    response: BaseHttpResponse, successBlock: suspend CoroutineScope.() -> Unit,
    errorBlock: suspend CoroutineScope.() -> Unit
) {
    coroutineScope {
        if (response.isSuccess()) {
            successBlock()
        } else {
            Logger.e("code = ${response.code}  msg = ${response.getResponseMsg()}")
            errorBlock()
        }
    }
}

fun Activity.onNetError(e: Throwable, func: (e: Throwable) -> Unit) {
    var msg = ""
    if (e is HttpException) {
        when (e.code()) {
            NetContacts.UNAUTHORIZED,
            NetContacts.FORBIDDEN,
            NetContacts.NOT_FOUND,
            NetContacts.REQUEST_TIMEOUT,
            NetContacts.GATEWAY_TIMEOUT,
            NetContacts.INTERNAL_SERVER_ERROR,
            NetContacts.BAD_GATEWAY,
            NetContacts.SERVICE_UNAVAILABLE -> {
                msg = "网络错误"
                func(e)
            }
            NetContacts.NEED_LOGIN -> {
                msg = "需要登陆"
                RouterPath.module_login.PATH_LOGIN.navigation()
                clearLoginUserInfo()
            }
            else -> {
                msg = e.message()
            }
        }
    } else if (e is JsonParseException || e is JSONException) {
        msg = "解析错误"
    } else if (e is ConnectException) {
        msg = "连接失败"
        func(e)
    } else if (e is SSLHandshakeException) {
        msg = "证书验证失败"
    } else {
        msg = "未知错误 $e"
    }
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun clearLoginUserInfo() {
    GlobalScope.launch {
        AppDatabase.getInstance(CoreApplication.CONTEXT).loginUserDao().deleteUser()
        PreferencesUtility.getInstance(CoreApplication.CONTEXT).clearToken()
    }
}


/*
    顶级函数，判断是否登录
 */
fun isLogined(): Boolean {
    return PreferencesUtility.getInstance(CoreApplication.CONTEXT).getToken().isNotEmpty()
}

/*
    如果已经登录，进行传入的方法处理
    如果没有登录，进入登录界面
 */
fun afterLogin(method: () -> Unit) {
    if (isLogined()) {
        method()
    } else {
        goLogin()
    }
}

fun goLogin(){
    RouterPath.module_login.PATH_LOGIN.navigation()
}

fun Any.getFieldValueByName(fieldName: String): Any? {
    return try {
//        val toString = (this as Map<String, Any>)[fieldName]
//        toString
        val firstLetter = fieldName.substring(0, 1).toUpperCase(Locale.CHINA)
        val getter = "get" + firstLetter + fieldName.substring(1)
        val method: Method = this.javaClass.getMethod(getter)
        method.invoke(this)
    } catch (e: Exception) {
        Logger.e(e.toString())
        null
    }
}

fun correspondKeyName(searchType: SearchType): String {
    return when (searchType.type) {
        1 -> "songs"
        10 -> "albums"
        100 -> "artists"
        1000 -> "playlists"
        1002 -> "userprofiles"
        1009 -> "djRadios"
        1014 -> "videos"
        else -> ""
    }
}

fun Composite.toListComposite(): MutableList<CompositeCommon> {
    val list = mutableListOf<CompositeCommon>()
    list.add(this.song)
    list.add(this.playList)
    list.add(this.video)
    list.add(this.sim_query)
    list.add(this.artist)
    list.add(this.album)
    list.add(this.djRadio)
    list.add(this.user)
    return list
}