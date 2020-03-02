package com.xw.lib_coremodel.model.repository.search

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.orhanobut.logger.Logger
import com.xw.lib_coremodel.CoreApplication
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.api.MusicRetrofitClient
import com.xw.lib_coremodel.model.bean.NetworkState
import com.xw.lib_coremodel.model.bean.search.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchResultDataSource<T>(
    private val retryExecutor: Executor,
    private val searchType: SearchType
) : ItemKeyedDataSource<String, T>() {

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
        callback: LoadInitialCallback<T>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        mOffset = 1
        val request = MusicRetrofitClient.service.searchResult(
            searchType.keywords,
            params.requestedLoadSize,
            mOffset,
            searchType.type
        )
        try {
            val response = request.execute()
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            if (response.isSuccessful) {
                val result = response.body()?.string() ?: ""
                val data = getData(result, searchType)
                callback.onResult(data)
            }

        } catch (e: Exception) {
            Logger.e(e.toString())
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<T>) {
        networkState.postValue(NetworkState.LOADING)
        val request = MusicRetrofitClient.service.searchResult(
            searchType.keywords,
            params.requestedLoadSize,
            params.requestedLoadSize * mOffset++,
            searchType.type
        )
        request.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                retry = {
                    loadAfter(params, callback)
                }
                networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()?.string() ?: ""
                    retry = null
                    try {
                        callback.onResult(getData(result, searchType))
                        networkState.postValue(NetworkState.LOADED)

                    } catch (e: Exception) {
                        Logger.e(e.toString())
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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<T>) {
    }

    override fun getKey(item: T): String = ""

    private fun getData(dataString: String, searchType: SearchType): List<T> {
        try {
            when (searchType.type) {
                1 -> {
                    val fromJson = CoreApplication.GSON.fromJson<SearchSongsResultResponse>(
                        dataString,
                        SearchSongsResultResponse::class.java
                    )
                    return try {
                        Logger.i("songs:" + fromJson.result.songs.toString())
                        fromJson.result.songs as List<T>
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emptyList()
                    }

                }
                10 -> {
                    val fromJson = CoreApplication.GSON.fromJson<SearchAlbumsResultResponse>(
                        dataString,
                        SearchAlbumsResultResponse::class.java
                    )
                    Logger.i("albums:" + fromJson.result.albums.toString())

                    return fromJson.result.albums as List<T>
                }
                100 -> {
                    val fromJson = CoreApplication.GSON.fromJson<SearchSingerResultResponse>(
                        dataString,
                        SearchSingerResultResponse::class.java
                    )
                    Logger.i("artists:" + fromJson.result.artists.toString())

                    return fromJson.result.artists as List<T>
                }
                1000 -> {
                    val fromJson = CoreApplication.GSON.fromJson<SearchPlayListResultResponse>(
                        dataString,
                        SearchPlayListResultResponse::class.java
                    )
                    Logger.i("playlists:" + fromJson.result.playlists.toString())

                    return fromJson.result.playlists as List<T>
                }
                1002 -> {
                    val fromJson = CoreApplication.GSON.fromJson<SearchUsersResultResponse>(
                        dataString,
                        SearchUsersResultResponse::class.java
                    )
                    Logger.i("userprofiles:" + fromJson.result.userprofiles.toString())

                    return fromJson.result.userprofiles as List<T>
                }
                1009 -> {
                    val fromJson = CoreApplication.GSON.fromJson<SearchDjResultResponse>(
                        dataString,
                        SearchDjResultResponse::class.java
                    )
                    Logger.i("djRadios:" + fromJson.result.djRadios.toString())

                    return fromJson.result.djRadios as List<T>
                }
                1014 -> {
                    val fromJson = CoreApplication.GSON.fromJson<SearchVideosResultResponse>(
                        dataString,
                        SearchVideosResultResponse::class.java
                    )
                    Logger.i("videos:" + fromJson.result.videos.toString())

                    return fromJson.result.videos as List<T>
                }
                else -> {
                    return emptyList()
                }
            }

        } catch (e: Exception) {
            Logger.e(e.toString())
            return emptyList()
        }

    }
}