package com.xw.lib_coremodel.model.repository.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.ext.executeResponse
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.NetworkState
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import kotlinx.coroutines.*
import java.util.concurrent.Executor

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListDataSource(
    private val coroutineScope: CoroutineScope,
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

        coroutineScope.launch {
            val playListsRespose = when (playListCat.name) {
                "精品" -> {
                    withContext(Dispatchers.IO) {
                        MusicRetrofitClient.service.getPlaylistHighquality(
                            limit = params.requestedLoadSize
                        )
                    }
                }
                else -> {
                    withContext(Dispatchers.IO) {
                        MusicRetrofitClient.service.getTopPlaylist(
                            cat = playListCat.name,
                            limit = params.requestedLoadSize
                        )
                    }
                }
            }

            executeResponse(playListsRespose, {
                retry = null
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                callback.onResult(playListsRespose.playlists)
            }, {
                retry = {
                    loadInitial(params, callback)
                }
                val error = NetworkState.error(playListsRespose.getResponseMsg())
                networkState.postValue(error)
                initialLoad.postValue(error)
            })
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<PlayList>) {
        networkState.postValue(NetworkState.LOADING)
        coroutineScope.launch {
            val playListsRespose = when (playListCat.name) {
                "精品" -> {
                    withContext(Dispatchers.IO) {
                        MusicRetrofitClient.service.getPlaylistHighquality(
                            limit = params.requestedLoadSize,
                            before = params.key
                        )
                    }
                }
                else -> {
                    withContext(Dispatchers.IO) {
                        MusicRetrofitClient.service.getTopPlaylist(
                            cat = playListCat.name,
                            limit = params.requestedLoadSize,
                            offset = params.requestedLoadSize * mOffset++
                        )
                    }
                }
            }

            executeResponse(playListsRespose, {
                retry = null
                callback.onResult(playListsRespose.playlists)
                networkState.postValue(NetworkState.LOADED)
            }, {
                retry = {
                    loadAfter(params, callback)
                }
                networkState.postValue(NetworkState.error(playListsRespose.getResponseMsg()))
            })
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PlayList>) {
    }

    override fun getKey(item: PlayList): String = item.updateTime.toString()
}