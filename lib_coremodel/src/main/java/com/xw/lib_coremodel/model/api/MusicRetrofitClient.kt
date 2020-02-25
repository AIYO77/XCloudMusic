package com.xw.lib_coremodel.model.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object MusicRetrofitClient {

    val service by lazy { getService(MusicService::class.java) }

    private fun <S> getService(serviceClass: Class<S>): S {
        return Retrofit.Builder()
            .client(HttpUtils.instance.client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(MusicService.BASE_URL)
            .build().create(serviceClass)
    }


}