package com.xw.lib_common.service

import android.app.Activity
import android.content.*
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.orhanobut.logger.Logger
import com.xw.lib_common.MediaAidlInterface
import com.xw.lib_common.ext.fromO
import com.xw.lib_coremodel.model.bean.info.MusicInfo
import java.util.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 播放音乐管理类 UI和server的中间件
 */
object MusicPlayer {

    private var mConnectionMap: WeakHashMap<Context, ServiceBinder> = WeakHashMap()
    private var sEmptyList: LongArray = longArrayOf(0)
    var mService: MediaAidlInterface? = null
    private var mContentValuesCache: Array<ContentValues>? = null

    fun bindToService(context: Context, callback: ServiceConnection): ServiceToken? {

        var realActivity: Activity? = (context as Activity).parent
        if (realActivity == null) {
            realActivity = context
        }
        val contextWrapper = ContextWrapper(realActivity)
        if (fromO()) {
            contextWrapper.startForegroundService(Intent(contextWrapper, MediaService::class.java))
        } else {
            contextWrapper.startService(Intent(contextWrapper, MediaService::class.java))
        }

        val binder = ServiceBinder(
            callback,
            contextWrapper.applicationContext
        )
        if (contextWrapper.bindService(
                Intent().setClass(contextWrapper, MediaService::class.java), binder, 0
            )
        ) {
            mConnectionMap[contextWrapper] = binder
            return ServiceToken(contextWrapper)
        }
        return null
    }

    fun unbindFromService(token: ServiceToken?) {
        if (token == null) {
            return
        }
        val mContextWrapper = token.mWrappedContext
        val mBinder = mConnectionMap.remove(mContextWrapper) ?: return
        mContextWrapper.unbindService(mBinder)
        if (mConnectionMap.isEmpty()) {
            mService = null
        }
    }

    fun next() {
        try {
            mService?.next()
        } catch (ignored: RemoteException) {
        }
    }

    fun initPlaybackServiceWithSettings(context: Context) {
        setShowAlbumArtOnLockscreen(true)
    }

    private fun setShowAlbumArtOnLockscreen(enabled: Boolean) {
        try {
            mService?.setLockscreenAlbumArt(enabled)
        } catch (ignored: RemoteException) {
        }
    }

    private fun previous(context: Context, force: Boolean) {
        val previous = Intent(context, MediaService::class.java)
        if (force) {
            previous.action = MediaService.PREVIOUS_FORCE_ACTION
        } else {
            previous.action = MediaService.PREVIOUS_ACTION
        }
        context.startService(previous)
    }

    fun isTrackLocal(): Boolean {
        try {
            return mService?.isTrackLocal ?: false
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return false
    }

    fun secondPosition(): Int {
        try {
            return mService?.secondPosition() ?: 0
        } catch (ignored: RemoteException) {
        } catch (ex: IllegalStateException) {
        }

        return 0
    }

    fun pre(context: Context, force: Boolean) {
        try {
            if (mService == null) {
                previous(context, force)
            } else {
                mService!!.prev(force)
            }

        } catch (ignored: RemoteException) {
        }
    }

    fun getTrackName(): String {
        try {
            return mService?.trackName ?: ""
        } catch (ignored: RemoteException) {
        }

        return ""
    }

    fun getArtistName(): String {
        try {
            return mService?.artistName ?: ""
        } catch (ignored: RemoteException) {
        }
        return ""
    }

    fun isPlaying(): Boolean {
        return try {
            mService?.isPlaying ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentAudioId(): Long {
        if (mService != null) {
            try {
                return mService!!.audioId
            } catch (ignored: RemoteException) {
            }

        }
        return -1
    }

    fun duration(): Long {
        if (mService != null) {
            try {
                return mService!!.duration()
            } catch (ignored: RemoteException) {
            } catch (ignored: IllegalStateException) {
            }
        }
        return 0
    }

    fun position(): Long {
        try {
            return mService?.position() ?: 0
        } catch (ignored: RemoteException) {
        } catch (ex: IllegalStateException) {
        }

        return 0
    }

    fun seek(position: Long) {
        if (mService != null) {
            try {
                mService!!.seek(position)
            } catch (ignored: RemoteException) {
            }
        }
    }

    fun getQueuePosition(): Int {
        try {
            return mService?.queuePosition ?: 0
        } catch (ignored: RemoteException) {
        }
        return 0
    }


    fun setQueuePosition(position: Int) {
        try {
            mService?.queuePosition = position
        } catch (ignored: RemoteException) {
        }
    }

    fun clearQueue() {
        try {
            mService?.removeTracks(0, Int.MAX_VALUE)
        } catch (ignored: RemoteException) {
        }
    }

    fun removeTrack(id: Long): Int {
        try {
            return mService?.removeTrack(id) ?: 0
        } catch (ignored: RemoteException) {
        }

        return 0
    }

    fun getQueue(): LongArray {
        try {
            return mService?.queue ?: sEmptyList
        } catch (ignored: RemoteException) {
        }
        return sEmptyList
    }

    fun getQueueSize(): Int {
        return try {
            mService?.queueSize ?: 0
        } catch (ignores: RemoteException) {
            0
        }
    }

    fun getAlbumPath(): String {
        return try {
            mService?.albumPath ?: ""
        } catch (ignored: RemoteException) {
            ""
        }
    }

    fun getAlbumPathAll(): Array<String> {
        try {
            return mService?.albumPathtAll ?: arrayOf()
        } catch (ignored: RemoteException) {
            Logger.e(ignored.toString())
        }
        return arrayOf()
    }

    fun getTrackNameAll(): Array<String> {
        return try {
            mService?.trackNameAll ?: arrayOf()
        } catch (ignored: RemoteException) {
            Logger.e(ignored.toString())
            arrayOf()
        }
    }

    fun getTrackAristNameAll(): Array<String> {
        return try {
            mService?.trackArtistNameAll ?: arrayOf()
        } catch (ignored: RemoteException) {
            Logger.e(ignored.toString())
            arrayOf()
        }
    }

    fun playOrPause() {
        try {
            if (mService != null) {
                if (mService!!.isPlaying) {
                    mService!!.pause()
                } else {
                    mService!!.play()
                }
            }
        } catch (ignored: Exception) {
        }

    }

    fun updateLrc() {
        try {
            mService?.updateLrc(mService?.audioId ?: -1)
        } catch (ignored: RemoteException) {
        }
    }

    fun getShuffleMode(): Int {
        if (mService != null) {
            try {
                return mService!!.shuffleMode
            } catch (ignored: RemoteException) {
            }
        }
        return 0
    }

    fun getRepeatMode(): Int {
        return try {
            mService?.repeatMode ?: 0
        } catch (ignored: RemoteException) {
            0
        }
    }


    fun cycleRepeat() {
        try {
            if (mService != null) {
                if (mService!!.shuffleMode == MediaService.SHUFFLE_NORMAL) {
                    mService!!.shuffleMode = MediaService.SHUFFLE_NONE
                    mService!!.repeatMode = MediaService.REPEAT_CURRENT
                    return
                } else {
                    when (mService!!.repeatMode) {
                        MediaService.REPEAT_CURRENT -> mService!!.repeatMode =
                            MediaService.REPEAT_ALL
                        MediaService.REPEAT_ALL -> mService!!.shuffleMode =
                            MediaService.SHUFFLE_NORMAL
                    }
                }
            }
        } catch (ignored: RemoteException) {
        }
    }

    fun getPlayList(): MutableMap<Long, MusicInfo>? {
        try {
            if (mService != null) {
                return mService!!.playinfos as MutableMap<Long, MusicInfo>
            }
        } catch (ignored: RemoteException) {
        }
        return null
    }

    fun stop() {
        try {
            mService?.stop()
        } catch (ignored: RemoteException) {
        }
    }

    fun exitService() {
        try {
            mConnectionMap.clear()
            mService?.exit()
        } catch (e: Exception) {
            Logger.e(e.toString())
        }
    }

    @Synchronized
    fun playAll(
        infos: HashMap<Long, MusicInfo>,
        list: LongArray,
        position: Int,
        forceShuffle: Boolean = false
    ) {
        if (list.isEmpty() || mService == null) {
            return
        }
        try {
            var mPosition = position
            if (forceShuffle) {
                mService!!.shuffleMode = MediaService.SHUFFLE_NORMAL
            }
            val currentId = mService!!.audioId
            val currentQueuePosition = getQueuePosition()
            if (mPosition >= 0) {
                val playlist = getQueue()
                if (list.contentEquals(playlist)) {
                    if (currentQueuePosition == mPosition && currentId == list[mPosition]) {
                        mService!!.play()
                        return
                    } else {
                        mService!!.queuePosition = mPosition
                        return
                    }
                }
            } else {
                mPosition = 0
            }
            Logger.i("infos size = ${infos.size} idList size = ${list.size}")
            mService!!.open(infos, list, if (forceShuffle) -1 else mPosition)
            mService!!.play()
        } catch (e: Exception) {
            Logger.e(e.localizedMessage)
        }
    }

    class ServiceBinder(private val mCallback: ServiceConnection?, private val mContext: Context) :
        ServiceConnection {

        override fun onServiceDisconnected(className: ComponentName?) {
            Logger.d("onServiceDisconnected")
            mCallback?.onServiceDisconnected(className)
            mService = null
        }

        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            Logger.d("onServiceConnected")
            mService = MediaAidlInterface.Stub.asInterface(service)
            mCallback?.onServiceConnected(className, service)
            initPlaybackServiceWithSettings(mContext)
        }

    }

    class ServiceToken(var mWrappedContext: ContextWrapper)
}