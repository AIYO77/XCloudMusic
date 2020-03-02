package com.xw.lib_coremodel.model.repository.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.NetworkState
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.model.bean.home.TopPlayListsRespose
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListDataSource(
    private val retryExecutor: Executor,
    private val playListCat: PlayListCat
) : ItemKeyedDataSource<String, PlayList>() {
    private var retry: (() -> Any)? = null
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    private var mOffset = 1

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<PlayList>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        mOffset = 1

        val playListsRespose = when (playListCat.name) {
            "精品" -> {
                MusicRetrofitClient.service.getPlaylistHighquality(limit = params.requestedLoadSize)
                    .execute()
            }
            else -> {
                MusicRetrofitClient.service.getTopPlaylist(
                    cat = playListCat.name,
                    limit = params.requestedLoadSize
                ).execute()
            }
        }
        if (playListsRespose.isSuccessful) {
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(playListsRespose.body()?.playlists ?: emptyList())
        } else {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(playListsRespose.body()?.getResponseMsg())
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<PlayList>) {
        networkState.postValue(NetworkState.LOADING)
        when (playListCat.name) {
            "精品" -> {
                MusicRetrofitClient.service.getPlaylistHighquality(
                    limit = params.requestedLoadSize,
                    before = params.key
                ).enqueue(object : retrofit2.Callback<TopPlayListsRespose> {
                    override fun onFailure(call: Call<TopPlayListsRespose>, t: Throwable) {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error(t.message))
                    }

                    override fun onResponse(
                        call: Call<TopPlayListsRespose>,
                        response: Response<TopPlayListsRespose>
                    ) {
                        dealResponse(response,params, callback)
                    }

                })
            }
            else -> {
                MusicRetrofitClient.service.getTopPlaylist(
                    cat = playListCat.name,
                    limit = params.requestedLoadSize,
                    offset = params.requestedLoadSize * mOffset++
                ).enqueue(object : retrofit2.Callback<TopPlayListsRespose> {
                    override fun onFailure(call: Call<TopPlayListsRespose>, t: Throwable) {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error(t.message))
                    }

                    override fun onResponse(
                        call: Call<TopPlayListsRespose>,
                        response: Response<TopPlayListsRespose>
                    ) {
                        dealResponse(response,params, callback)
                    }
                })
            }
        }
    }

    private fun dealResponse(
        response: Response<TopPlayListsRespose>,
        params: LoadParams<String>,
        callback: LoadCallback<PlayList>
    ) {
        if (response.isSuccessful) {
            val body = response.body()
            val success = body?.isSuccess() ?: false
            if (success) {
                val items = response.body()?.playlists ?: emptyList()
                retry = null
                callback.onResult(items)
                networkState.postValue(NetworkState.LOADED)
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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PlayList>) {
    }

    override fun getKey(item: PlayList): String = item.updateTime.toString()
}