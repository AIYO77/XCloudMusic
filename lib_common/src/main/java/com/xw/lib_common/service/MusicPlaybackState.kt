package com.xw.lib_common.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import com.xw.lib_common.base.BaseApplication
import com.xw.lib_coremodel.data.AppDatabase
import com.xw.lib_coremodel.data.PlaybackHistory
import com.xw.lib_coremodel.data.PlaybackList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
object MusicPlaybackState {

    @Synchronized
    fun clearQueue() {
        GlobalScope.launch {
            val historyDao = AppDatabase.getInstance(BaseApplication.CONTEXT).playbackHistoryDao()
            val listDao = AppDatabase.getInstance(BaseApplication.CONTEXT).playbackListDao()
            historyDao.deleteAll(historyDao.getHistory())
            listDao.deleteAll(listDao.getPlaybackList())
        }

    }

    @Synchronized
    fun saveState(queue: ArrayList<MusicTrack>, history: LinkedList<Int>?) {
        Logger.i("save queue size = ${queue.size}")
        GlobalScope.launch {
            val historyDao = AppDatabase.getInstance(BaseApplication.CONTEXT).playbackHistoryDao()
            val listDao = AppDatabase.getInstance(BaseApplication.CONTEXT).playbackListDao()
            historyDao.deleteAll(historyDao.getHistory())
            listDao.deleteAll(listDao.getPlaybackList())

            queue.forEach {
                listDao.insertItem(
                    PlaybackList(
                        trackId = it.mId,
                        sourcePosition = it.mSourcePosition
                    )
                )
            }

            history?.run {
                while (iterator().hasNext()) {
                    historyDao.insertItem(PlaybackHistory(iterator().next()))
                }
            }
        }

    }

    fun getQueue(): ArrayList<MusicTrack> {
        val results = ArrayList<MusicTrack>()
        val playbackList =
            AppDatabase.getInstance(BaseApplication.CONTEXT).playbackListDao().getPlaybackList()
        results.ensureCapacity(playbackList.size)
        playbackList.forEach {
            results.add(MusicTrack(it.trackId, it.sourcePosition))
        }
        Logger.i("get queue size =${results.size} ")
        return results
    }

    fun getHistory(): LinkedList<Int> {
        val results = LinkedList<Int>()
        val history =
            AppDatabase.getInstance(BaseApplication.CONTEXT).playbackHistoryDao().getHistory()
        history.forEach {
            results.add(it.position)
        }
        return results
    }
}