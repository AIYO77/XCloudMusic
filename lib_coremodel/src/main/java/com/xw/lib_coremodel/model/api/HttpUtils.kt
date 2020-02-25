package com.xw.lib_coremodel.model.api

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.xw.lib_coremodel.BuildConfig
import com.xw.lib_coremodel.CoreApplication
import com.xw.lib_coremodel.utils.PreferencesUtility
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class HttpUtils {


    companion object {

        private const val TIME_OUT = 10

        val instance: HttpUtils by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpUtils()
        }
    }

    val client: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            val logging = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                logging.level = HttpLoggingInterceptor.Level.BODY
            } else {
                logging.level = HttpLoggingInterceptor.Level.BASIC
            }

            builder.addInterceptor(logging)
                .addInterceptor(RequestExtraParamsInterceptor())
                .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .cookieJar(MyCookieJar())

            handleBuilder(builder)

            return builder.build()
        }

    private fun handleBuilder(builder: OkHttpClient.Builder) {
//        builder.addInterceptor(AddCacheInterceptor(CoreApplication.CONTEXT))
    }


    private class RequestExtraParamsInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val builder = when (request.method()) {
                "POST" -> {
                    addDefaultParameterToBody(request)
                }
                else -> {
                    request.newBuilder()
                }
            }
            return chain.proceed(builder.build())
        }

        private fun addDefaultParameterToBody(request: Request): Request.Builder {
            val builder: Request.Builder
            val newRequestBody: RequestBody
            val userId = PreferencesUtility.getInstance(CoreApplication.CONTEXT).getUserId()

            newRequestBody = if (request.body() != null) {
                val baos = ByteArrayOutputStream()
                val sink: Sink = baos.sink()
                val bufferedSink: BufferedSink = sink.buffer()
                request.body()!!.writeTo(bufferedSink)
                bufferedSink.writeString("&uid=$userId", Charset.defaultCharset())
                RequestBody.create(
                    request.body()!!.contentType(),
                    bufferedSink.buffer.readUtf8()
                )
            } else {
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uid", userId)
                    .build()
            }
            builder = request.newBuilder().post(newRequestBody)
            return builder
        }
    }


    private inner class AddCacheInterceptor internal constructor(private val context: Context) :
        Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {

            val cacheBuilder = CacheControl.Builder()
            cacheBuilder.maxAge(0, TimeUnit.SECONDS)
            cacheBuilder.maxStale(365, TimeUnit.DAYS)
            val cacheControl = cacheBuilder.build()
            var request = chain.request()
            if (!NetWorkUtils.isNetworkConnected(context)) {
                request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build()
            }
            val originalResponse = chain.proceed(request)
            return if (NetWorkUtils.isNetworkConnected(context)) {
                // read from cache
                val maxAge = 0
                originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public ,max-age=$maxAge")
                    .build()
            } else {
                // tolerate 1-day stale
                val maxStale = 60 * 60 * 24
                originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .build()
            }
        }
    }

    private inner class MyCookieJar : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
            if (cookies.isNullOrEmpty().not()) {
                val storage = mutableListOf<Cookie>()
                storage.addAll(cookies)
                PreferencesUtility.getInstance(CoreApplication.CONTEXT)
                    .setToken(CoreApplication.GSON.toJson(storage))
            }
        }

        override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
            val cookies: ArrayList<Cookie> = ArrayList()
            val token =
                PreferencesUtility.getInstance(CoreApplication.CONTEXT).getToken()
            if (token.isNotEmpty()) {
                val type = object : TypeToken<ArrayList<Cookie>>() {}.type

                val list =
                    CoreApplication.GSON.fromJson<ArrayList<Cookie>>(token, type)

                cookies.addAll(list)
            }

            return cookies
        }
    }
}