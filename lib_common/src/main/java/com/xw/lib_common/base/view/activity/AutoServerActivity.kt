package com.xw.lib_common.base.view.activity

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.xw.lib_common.MediaAidlInterface
import com.xw.lib_common.R
import com.xw.lib_common.autodispose.lifecycle.autoDisposeInterceptor
import com.xw.lib_common.ext.getString
import com.xw.lib_common.ext.toast
import com.xw.lib_common.listener.MusicStateListener
import com.xw.lib_common.service.MediaService
import com.xw.lib_common.service.MusicPlayer
import com.xw.lib_coremodel.model.bean.LrcAdnTlyRic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
abstract class AutoServerActivity : AppCompatActivity(), CoroutineScope, ServiceConnection {

    protected val mMusicListener = arrayListOf<MusicStateListener>()
    private var mToken: MusicPlayer.ServiceToken? = null
    protected var mPlaybackStatus: PlaybackStatus? = null //receiver 接受播放状态变化等

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + autoDisposeInterceptor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("${this::class.java.simpleName} onCreate")
        mToken = MusicPlayer.bindToService(this, this)
        mPlaybackStatus = PlaybackStatus(this)

        val f = IntentFilter().apply {
            addAction(MediaService.PLAYSTATE_CHANGED)
            addAction(MediaService.META_CHANGED)
            addAction(MediaService.QUEUE_CHANGED)
            addAction(MediaService.TRACK_PREPARED)
            addAction(MediaService.LRC_UPDATED)
            addAction(MediaService.BUFFER_UP)
            addAction(MediaService.SHUFFLEMODE_CHANGED)
            addAction(MediaService.REPEATMODE_CHANGED)
            addAction(MediaService.MUSIC_CHANGED)
            addAction(MediaService.MUSIC_LODING)
        }
        registerReceiver(mPlaybackStatus, IntentFilter(f))

    }

    /**
     * 更新歌曲状态信息
     */
    open fun updateTrackInfo() {
        Thread(Runnable {
            synchronized(mMusicListener) {
                mMusicListener.forEach {
                    runOnUiThread {
                        it.updateTrackInfo()
                    }
                }
            }
        }).start()
    }

    /**
     * 更新播放队列
     */
    open fun updateQueue() {
        Thread(Runnable {
            synchronized(mMusicListener) {
                mMusicListener.forEach {
                    runOnUiThread {
                        it.updateQueue()
                    }
                }
            }
        }).start()
    }

    /**
     * 歌曲切换
     */
    open fun updateTrack() {
        Thread(Runnable {
            synchronized(mMusicListener) {
                mMusicListener.forEach {
                    runOnUiThread {
                        it.updateTrack()
                    }
                }
            }
        }).start()
    }

    /**
     * @param p 更新歌曲缓冲进度值，p取值从0~100
     */
    open fun updateBuffer(p: Int) {

    }


    /**
     * @param l 歌曲是否加载中
     */
    open fun loading(l: Boolean) {

    }

    /**
     * 歌词更新
     */
    open fun updateLrc(lrc: LrcAdnTlyRic) {
        Thread(Runnable {
            synchronized(mMusicListener) {
                mMusicListener.forEach {
                    runOnUiThread {
                        it.updateLrc(lrc)
                    }
                }
            }
        }).start()
    }

    /**
     * 播放模式改变
     */
    open fun playModeChange() {

    }

    class PlaybackStatus(activity: AutoServerActivity) : BroadcastReceiver() {
        private val mReference: WeakReference<AutoServerActivity> = WeakReference(activity)

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            val activity = mReference.get()
            activity?.let {
                when (action) {
                    MediaService.META_CHANGED -> {
                        it.updateTrackInfo()
                    }
                    MediaService.PLAYSTATE_CHANGED -> {

                    }
                    MediaService.TRACK_PREPARED -> {

                    }
                    MediaService.BUFFER_UP -> {
                        it.updateBuffer(intent.getIntExtra("progress", 0))
                    }
                    MediaService.MUSIC_LODING -> {
                        it.loading(intent.getBooleanExtra("isloading", false))
                    }
                    MediaService.QUEUE_CHANGED -> {
                        it.updateQueue()
                    }
                    MediaService.TRACK_ERROR -> {
                        toast(
                            getString(
                                R.string.data_error_playing_track,
                                intent.getStringExtra(MediaService.TRACK_NAME)
                            )
                        )
                    }
                    MediaService.MUSIC_CHANGED -> {
                        it.updateTrack()
                    }
                    MediaService.LRC_UPDATED -> {
                        val lrcAdnTlyRic = intent.getParcelableExtra<LrcAdnTlyRic>("lrc")
                        it.updateLrc(lrcAdnTlyRic)
                    }
                    MediaService.REPEATMODE_CHANGED,
                    MediaService.SHUFFLEMODE_CHANGED -> {
                        it.playModeChange()
                    }
                }
            }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        MusicPlayer.mService = null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        MusicPlayer.mService = MediaAidlInterface.Stub.asInterface(service)
    }

    fun setMusicStateListenerListener(status: MusicStateListener?) {
        if (status === this) {
            throw UnsupportedOperationException("Override the method, don't add a listener")
        }
        if (status != null) {
            mMusicListener.add(status)
        }
    }


    fun removeMusicStateListenerListener(status: MusicStateListener?) {
        if (status != null) {
            mMusicListener.remove(status)
        }
    }

    protected fun unbindService() {
        mToken?.let {
            MusicPlayer.unbindFromService(it)
            mToken = null
        }
    }

    override fun onResume() {
        super.onResume()
        //For Android 8.0+: service may get destroyed if in background too long
        if (mToken == null) {
            mToken = MusicPlayer.bindToService(this, this)
        }
    }

    override fun onDestroy() {
        Logger.d("${this::class.java.simpleName} onDestroy")
        super.onDestroy()
        unbindService()
        try {
            unregisterReceiver(mPlaybackStatus)
        } catch (e: Throwable) {
        }
        mMusicListener.clear()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
    }
}