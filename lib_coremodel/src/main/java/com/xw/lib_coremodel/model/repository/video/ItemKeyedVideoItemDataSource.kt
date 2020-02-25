package com.xw.lib_coremodel.model.repository.video

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.api.MusicService
import com.xw.lib_coremodel.model.bean.NetworkState
import com.xw.lib_coremodel.model.bean.video.VideoItemInfo
import com.xw.lib_coremodel.model.bean.video.VideoListResponse
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class ItemKeyedVideoItemDataSource(
    private val id: String,
    private val retryExecutor: Executor?
) : ItemKeyedDataSource<String, VideoItemInfo>() {


    private var retry: (() -> Any)? = null
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    private val musicService: MusicService = MusicRetrofitClient.service

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor?.execute {
                it.invoke()
            }
        }
    }


    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<VideoItemInfo>
    ) {
        val request = musicService.getVideoList(id)
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        try {
            val response = request.execute()
            if (response.isSuccessful) {
                val items = response.body()?.datas?.filter { it.type == 1 } ?: emptyList()
                retry = null
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                callback.onResult(items)
            } else if (response.code() == 301) {
                networkState.postValue(
                    NetworkState.needLogin("需要登陆")
                )
            }

        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<VideoItemInfo>) {
        networkState.postValue(NetworkState.LOADING)

        musicService.getVideoList(id).enqueue(object : retrofit2.Callback<VideoListResponse> {
            override fun onFailure(call: Call<VideoListResponse>, t: Throwable) {
                retry = {
                    loadAfter(params, callback)
                }
                networkState.postValue(NetworkState.error(t.message ?: "unknown err"))

            }

            override fun onResponse(
                call: Call<VideoListResponse>,
                response: Response<VideoListResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val success = body?.isSuccess() ?: false
                    if (success) {
                        val items = response.body()?.datas?.filter { it.type == 1 } ?: emptyList()
                        retry = null
                        callback.onResult(items)
                        networkState.postValue(NetworkState.LOADED)
                    } else if (body?.code == 301) {
                        networkState.postValue(
                            NetworkState.needLogin("需要登陆")
                        )
                    }

                } else {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(
                        NetworkState.error("error code: ${response.code()}")
                    )
                }
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<VideoItemInfo>) {
    }

    override fun getKey(item: VideoItemInfo): String = item.data.title
}