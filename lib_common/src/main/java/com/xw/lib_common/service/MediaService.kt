package com.xw.lib_common.service

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.MediaPlayer.SEEK_CLOSEST
import android.media.audiofx.AudioEffect
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.*
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.xw.lib_common.MediaAidlInterface
import com.xw.lib_common.R
import com.xw.lib_common.base.BaseApplication
import com.xw.lib_common.ext.*
import com.xw.lib_common.proxy.utils.MediaPlayerProxy
import com.xw.lib_common.receiver.MediaButtonIntentReceiver
import com.xw.lib_common.utils.GlideApp
import com.xw.lib_coremodel.CoreApplication
import com.xw.lib_coremodel.data.AppDatabase
import com.xw.lib_coremodel.data.RecentHistory
import com.xw.lib_coremodel.data.SongLrc
import com.xw.lib_coremodel.model.bean.LrcAdnTlyRic
import com.xw.lib_coremodel.model.bean.info.MusicInfo
import com.xw.lib_coremodel.model.repository.PlayServiceRepository
import com.xw.lib_coremodel.utils.PreferencesUtility
import kotlinx.coroutines.*
import leakcanary.internal.getBytes
import java.io.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class MediaService : Service() {

    private var appDatabase: AppDatabase? = null

    private val PROJECTION = arrayOf(
        "audio._id AS _id",
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST_ID
    )
    private val mShuffler = Shuffler()
    private val NOTIFY_MODE_NONE = 0
    private val NOTIFY_MODE_FOREGROUND = 1
    private val NOTIFY_MODE_BACKGROUND = 2
    private val PROJECTION_MATRIX = arrayOf(
        "_id",
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST_ID
    )

    private var mHistory = LinkedList<Int>()
    private val mBinder = ServiceStub(this)
    private var mFileToPlay: String? = null
    private var mWakeLock: PowerManager.WakeLock? = null
    private var mAlarmManager: AlarmManager? = null
    private var mShutdownIntent: PendingIntent? = null
    private var mShutdownScheduled: Boolean = false
    private var mNotificationManager: NotificationManager? = null
    private var mCursor: Cursor? = null
    private var mAudioManager: AudioManager? = null
    private lateinit var mPreferences: SharedPreferences
    private var mServiceInUse = false
    private var mIsSupposedToBePlaying = false
    private var mLastPlayedTime: Long = 0
    private var mNotifyMode = NOTIFY_MODE_NONE
    private var mNotificationPostTime: Long = 0
    private var mQueueIsSaveable = true
    private var mPausedByTransientLossOfFocus = false

    private var mPlayer: MultiPlayer? = null

    private var mSession: MediaSession? = null

    private var mUnmountReceiver: BroadcastReceiver? = null
    private var mCardId: Int = 0

    private var mPlayPos = -1

    private var mNextPlayPos = -1

    private var mOpenFailedCounter = 0

    private var mMediaMountedCount = 0

    private var mShuffleMode = SHUFFLE_NONE

    private var mRepeatMode = REPEAT_ALL

    private var mServiceStartId = -1

    private var mPlaylist = arrayListOf<MusicTrack>()

    private var mPlaylistInfo = HashMap<Long, MusicInfo>()

    private lateinit var mPlayerHandler: MusicPlayerHandler
    private var mHandlerThread: HandlerThread? = null
    private var mLastSeekPos: Long = 0
    private var mNotification: Notification? = null
    private val mNotificationId = 1000
    private var mProxy: MediaPlayerProxy? = null
    private lateinit var mUrlHandler: Handler
    //    private lateinit var mLrcHandler: Handler
    private var mIsSending = false

    private var mRequestUrl: RequestPlayUrl? = null

//    private val mLrcThread = Thread(Runnable {
//        Looper.prepare()
//        mLrcHandler = Handler()
//        Looper.loop()
//    })

    private val sendDuration = object : Runnable {
        override fun run() {
            notifyChange(SEND_PROGRESS)
            mPlayerHandler.postDelayed(this, 1000)
        }
    }

    private val mGetUrlThread = Thread(Runnable {
        Looper.prepare()
        mUrlHandler = Handler()
        Looper.loop()
    })

    private val mIntentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handleCommandIntent(intent)
        }
    }

    private val mAudioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        mPlayerHandler.obtainMessage(
            FOCUSCHANGE,
            focusChange,
            0
        ).sendToTarget()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Logger.d("$TAG onBind")
        cancelShutdown()
        mServiceInUse = true
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Logger.d("$TAG onUnbind")
        mServiceInUse = false
        saveQueue(true)
        if (mIsSupposedToBePlaying || mPausedByTransientLossOfFocus) {

            return true
        } else if (mPlaylist.size > 0 || mPlayerHandler.hasMessages(TRACK_ENDED)) {
            scheduleDelayedShutdown()
            return true
        }
        stopSelf(mServiceStartId)
        return true
    }

    override fun onRebind(intent: Intent?) {
        Logger.d("$TAG onRebind")

        cancelShutdown()
        mServiceInUse = true
    }

    override fun onCreate() {
        Logger.d("$TAG onCreate")
        super.onCreate()
//        mLrcThread.start()
        mGetUrlThread.start()
        mProxy = MediaPlayerProxy(this)
        mProxy?.init()
        mProxy?.start()
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mHandlerThread = HandlerThread(
            "MusicPlayerHandler",
            Process.THREAD_PRIORITY_BACKGROUND
        )
        mHandlerThread!!.start()

        mPlayerHandler = MusicPlayerHandler(this, mHandlerThread!!.looper)
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager!!.registerMediaButtonEventReceiver(
            ComponentName(
                packageName,
                MediaButtonIntentReceiver::class.java.name
            )
        )

        setUpMediaSession()

        mPreferences = getSharedPreferences("Service", 0)
        mCardId = getCardId()

        registerExternalStorageListener()

        mPlayer = MultiPlayer(this)
        mPlayer!!.setHandler(mPlayerHandler)

        val filter = IntentFilter().apply {
            addAction(SERVICECMD)
            addAction(TOGGLEPAUSE_ACTION)
            addAction(PAUSE_ACTION)
            addAction(STOP_ACTION)
            addAction(NEXT_ACTION)
            addAction(PREVIOUS_ACTION)
            addAction(PREVIOUS_FORCE_ACTION)
            addAction(REPEAT_ACTION)
            addAction(SHUFFLE_ACTION)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(LOCK_SCREEN)
            addAction(SEND_PROGRESS)
            addAction(SETQUEUE)
        }
        registerReceiver(mIntentReceiver, filter)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name)
        mWakeLock!!.setReferenceCounted(false)

        val shutdownIntent = Intent(this, MediaService::class.java)
        shutdownIntent.action = SHUTDOWN

        mShutdownIntent = PendingIntent.getService(this, 0, shutdownIntent, 0)

        scheduleDelayedShutdown()
        mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        reloadQueueAfterPermissionCheck()
        if (fromO()){
            startForeground(mNotificationId, getNotification())
        }
    }

    private fun registerExternalStorageListener() {
        if (mUnmountReceiver == null) {
            mUnmountReceiver = object : BroadcastReceiver() {

                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    if (action == Intent.ACTION_MEDIA_EJECT) {
                        saveQueue(true)
                        mQueueIsSaveable = false
                        closeExternalStorageFiles()
                    } else if (action == Intent.ACTION_MEDIA_MOUNTED) {
                        mMediaMountedCount++
                        mCardId = getCardId()
                        reloadQueueAfterPermissionCheck()
                        mQueueIsSaveable = true
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_MEDIA_EJECT)
            addAction(Intent.ACTION_MEDIA_MOUNTED)
            addDataScheme("file")
        }
        registerReceiver(mUnmountReceiver, filter)
    }

    private fun reloadQueueAfterPermissionCheck() {
        if (fromM()) {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                reloadQueue()
            }
        } else {
            reloadQueue()
        }

    }

    private fun closeExternalStorageFiles() {
        stop(true)
        notifyChange(QUEUE_CHANGED)
        notifyChange(META_CHANGED)
    }

    private fun getCardId(): Int {
        return if (fromM()) {
            if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                getMCardId()
            } else 0
        } else {
            getMCardId()
        }
    }

    private fun getMCardId(): Int {
        val resolver = contentResolver
        val cursor =
            resolver.query(Uri.parse("content://media/external/fs_id"), null, null, null, null)
        var mCardId = -1
        if (cursor != null && cursor.moveToFirst()) {
            mCardId = cursor.getInt(0)
            cursor.close()
        }
        return mCardId
    }

    private fun setUpMediaSession() {
        if (fromL()) {
            mSession = MediaSession(this, "XMusic")
            mSession?.setCallback(object : MediaSession.Callback() {
                override fun onPause() {
                    pause()
                    mPausedByTransientLossOfFocus = false
                }

                override fun onPlay() {
                    play()
                }

                override fun onSeekTo(pos: Long) {
                    seek(pos)
                }

                override fun onSkipToNext() {
                    gotoNext()
                }

                override fun onSkipToPrevious() {
                    prev(false)
                }

                override fun onStop() {
                    pause()
                    mPausedByTransientLossOfFocus = false
                    seek(0)
                    releaseServiceUiAndStop()
                }
            })
            mSession?.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        }

    }

    @Synchronized
    private fun prev(forcePrevious: Boolean) {
        val goPrevious =
            getRepeatMode() != REPEAT_CURRENT && (position() < REWIND_INSTEAD_PREVIOUS_THRESHOLD || forcePrevious)

        if (goPrevious) {
            val pos = getPreviousPlayPosition(true)
            if (pos < 0) {
                return
            }
            mNextPlayPos = mPlayPos
            mPlayPos = pos
            stop(false)
            openCurrentAndMaybeNext(play = false, openNext = false)
            play(false)
            notifyChange(META_CHANGED)
            notifyChange(MUSIC_CHANGED)
        } else {
            seek(0)
            play(false)
        }
    }

    @Synchronized
    private fun getPreviousPlayPosition(removeFromHistory: Boolean): Int {
        if (mShuffleMode == SHUFFLE_NORMAL) {
            val histsize = mHistory.size
            if (histsize == 0) {
                return -1
            }
            val pos = mHistory[histsize - 1]
            if (removeFromHistory) {
                mHistory.removeAt(histsize - 1)
            }
            return pos
        } else {
            return if (mPlayPos > 0) {
                mPlayPos - 1
            } else {
                mPlaylist.size - 1
            }
        }
    }

    private fun position(): Long {
        if (mPlayer == null) return -1
        if (mPlayer!!.isInitialized() && mPlayer!!.isTrackPrepared()) {
            try {
                return mPlayer!!.position()
            } catch (e: Exception) {
                Logger.e(e.toString())
            }

        }
        return -1
    }

    private fun getSecondPosition(): Int {
        return if (mPlayer?.isInitialized() == true) {
            mPlayer!!.sencondaryPosition
        } else -1
    }

    private fun seek(position: Long): Long {
        var mPosition = position
        if (mPlayer?.isInitialized() == true) {
            if (mPosition < 0) {
                mPosition = 0
            } else if (mPosition > mPlayer!!.duration()) {
                mPosition = mPlayer!!.duration()
            }
            val result = mPlayer!!.seek(mPosition)
            notifyChange(POSITION_CHANGED)
            return result
        }
        return -1
    }

    private fun play() {
        play(true)
    }

    private fun play(createNewNextTrack: Boolean) {
        val status = mAudioManager?.requestAudioFocus(
            mAudioFocusListener,
            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
        )

        if (status != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return
        }

        val intent = Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId())
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
        sendBroadcast(intent)

        mAudioManager?.registerMediaButtonEventReceiver(
            ComponentName(
                packageName,
                MediaButtonIntentReceiver::class.java.name
            )
        )
        if (fromL())
            mSession?.isActive = true
        if (createNewNextTrack) {
            setNextTrack()
        } else {
            setNextTrack(mNextPlayPos)
        }
        if (mPlayer?.isTrackPrepared() == true) {
            val duration = mPlayer!!.duration()
            if (mRepeatMode != REPEAT_CURRENT && duration > 2000
                && mPlayer!!.position() >= duration - 2000
            ) {
                gotoNext()
            }
        }
        mPlayer?.start()
        mPlayerHandler.removeMessages(FADEDOWN)
        mPlayerHandler.sendEmptyMessage(FADEUP)
        setIsSupposedToBePlaying(value = true, notify = true)
        cancelShutdown()
        updateNotification()
        notifyChange(META_CHANGED)
    }

    @Synchronized
    private fun pause() {
        mPlayerHandler.removeMessages(FADEUP)
        if (mIsSupposedToBePlaying) {
            val intent = Intent(
                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION
            )
            intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId())
            intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
            sendBroadcast(intent)

            mPlayer?.pause()
            setIsSupposedToBePlaying(value = false, notify = true)
            notifyChange(META_CHANGED)
        }
    }

    override fun onDestroy() {
        Logger.d("$TAG onDestroy")

        super.onDestroy()
        val audioEffectsIntent = Intent(
            AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION
        ).apply {
            putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId())
            putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
        }
        sendBroadcast(audioEffectsIntent)
        cancelNotification()
        mAlarmManager?.cancel(mShutdownIntent)

        mPlayerHandler.removeCallbacksAndMessages(null)

        mHandlerThread?.quitSafely()

        mPlayer?.release()
        mPlayer = null

        mAudioManager?.abandonAudioFocus(mAudioFocusListener)
        if (fromL()) {
            mSession?.release()
        }
        closeCursor()
        unregisterReceiver(mIntentReceiver)
        mUnmountReceiver?.let {
            unregisterReceiver(it)
            mUnmountReceiver = null
        }

        mWakeLock?.release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mServiceStartId = startId
        if (intent != null) {
            val action = intent.action

            if (SHUTDOWN == action) {
                mShutdownScheduled = false
                releaseServiceUiAndStop()
                return START_NOT_STICKY
            }
            handleCommandIntent(intent)
        }

        scheduleDelayedShutdown()

        if (intent != null && intent.getBooleanExtra(FROM_MEDIA_BUTTON, false)) {
            MediaButtonIntentReceiver.mCompleteWakefulIntent(intent)
        }
        return START_STICKY
    }

    private fun handleCommandIntent(intent: Intent) {
        val action = intent.action
        val command = if (SERVICECMD == action) intent.getStringExtra(CMDNAME) else null
        if (CMDNEXT == command || NEXT_ACTION == action) {
            gotoNext()
        } else if (CMDPREVIOUS == command || PREVIOUS_ACTION == action
            || PREVIOUS_FORCE_ACTION == action
        ) {
            prev(PREVIOUS_FORCE_ACTION == action)
        } else if (CMDTOGGLEPAUSE == command || TOGGLEPAUSE_ACTION == action) {
            if (isPlaying()) {
                pause()
                mPausedByTransientLossOfFocus = false
            } else {
                play()
            }
        } else if (CMDPAUSE == command || PAUSE_ACTION == action) {
            pause()
            mPausedByTransientLossOfFocus = false
        } else if (CMDPLAY == command) {
            play()
        } else if (CMDSTOP == command || STOP_ACTION == action) {
            pause()
            mPausedByTransientLossOfFocus = false
            seek(0)
            releaseServiceUiAndStop()
        } else if (REPEAT_ACTION == action) {
            cycleRepeat()
        } else if (SHUFFLE_ACTION == action) {
            cycleShuffle()
        }
//        else if (Intent.ACTION_SCREEN_OFF == action) {
//        //TODO 桌面
//        }
        else if (SEND_PROGRESS == action) {
            if (isPlaying() && !mIsSending) {
                mPlayerHandler.post(sendDuration)
                mIsSending = true
            } else if (!isPlaying()) {
                mPlayerHandler.removeCallbacks(sendDuration)
                mIsSending = false
            }
        }

    }


    private fun releaseServiceUiAndStop() {
        if (isPlaying()
            || mPausedByTransientLossOfFocus
            || mPlayerHandler.hasMessages(TRACK_ENDED)
        ) {
            return
        }
        cancelNotification()
        mAudioManager?.abandonAudioFocus(mAudioFocusListener)
        if (fromL())
            mSession?.isActive = false
        if (!mServiceInUse) {
            saveQueue(true)
            stopSelf(mServiceStartId)
        }
    }

    private fun cancelNotification() {
        stopForeground(true)
        mNotificationManager?.cancel(mNotificationId)
        mNotificationPostTime = 0
        mNotifyMode = NOTIFY_MODE_NONE
    }

    private fun updateNotification() {
        val newNotifyMode: Int = when {
            isPlaying() -> NOTIFY_MODE_FOREGROUND
            recentlyPlayed() -> NOTIFY_MODE_BACKGROUND
            else -> NOTIFY_MODE_NONE
        }

        if (mNotifyMode != newNotifyMode) {
            if (mNotifyMode == NOTIFY_MODE_FOREGROUND) {
                if (fromL())
                    stopForeground(newNotifyMode == NOTIFY_MODE_NONE)
                else
                    stopForeground(newNotifyMode == NOTIFY_MODE_NONE || newNotifyMode == NOTIFY_MODE_BACKGROUND)
            } else if (newNotifyMode == NOTIFY_MODE_NONE) {
                mNotificationManager?.cancel(mNotificationId)
                mNotificationPostTime = 0
            }
        }
        if (fromO()) {
            val name = "my_package_channel"
            val id = "my_package_channel_1"
            val description = "my_package_first_channel"
            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            mChannel.description = description
            mNotificationManager?.createNotificationChannel(mChannel)
        }

        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            startForeground(mNotificationId, getNotification())
        } else if (newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            mNotificationManager?.notify(mNotificationId, getNotification())
        }
        mNotifyMode = newNotifyMode
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getNotification(): Notification? {
        val remoteViews = RemoteViews(this.packageName, R.layout.notification)
        val PAUSE_FLAG = 0x1
        val NEXT_FLAG = 0x2
        val STOP_FLAG = 0x3
        val albumName = getAlbumName()
        val artistName = getArtistName()
        val isPlaying = isPlaying()
        val text = if (TextUtils.isEmpty(albumName)) artistName else "$artistName - $albumName"
        remoteViews.setTextViewText(R.id.title, getTrackName())
        remoteViews.setTextViewText(R.id.text, text)

        //此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
        val pauseIntent = Intent(TOGGLEPAUSE_ACTION)
        pauseIntent.putExtra("FLAG", PAUSE_FLAG)
        val pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0)
        remoteViews.setImageViewResource(
            R.id.iv_pause,
            if (isPlaying) R.drawable.note_btn_pause else R.drawable.note_btn_play
        )
        remoteViews.setOnClickPendingIntent(R.id.iv_pause, pausePIntent)

        val nextIntent = Intent(NEXT_ACTION)
        nextIntent.putExtra("FLAG", NEXT_FLAG)
        val nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0)
        remoteViews.setOnClickPendingIntent(R.id.iv_next, nextPIntent)

        val preIntent = Intent(STOP_ACTION)
        preIntent.putExtra("FLAG", STOP_FLAG)
        val prePIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0)
        remoteViews.setOnClickPendingIntent(R.id.iv_stop, prePIntent)

        val nowPlayingIntent = Intent()
        nowPlayingIntent.component =
            ComponentName("com.xw.xmusic", "com.xw.lib_common.play.PlayingActivity")
        nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val click =
            PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        GlideApp.with(this)
            .asBitmap()
            .load(getAlbumPath())
            .override(160, 160)
            .placeholder(R.drawable.placeholder_disk_210)
            .fitCenter()
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    remoteViews.setImageViewBitmap(R.id.image, resource)
                }
            })

        if (mNotificationPostTime == 0.toLong()) {
            mNotificationPostTime = System.currentTimeMillis()
        }
        if (mNotification == null) {
            val builder = NotificationCompat.Builder(this, "my_package_channel_1")
                .setContent(remoteViews)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(click)
                .setWhen(mNotificationPostTime)
            builder.setShowWhen(false)
            mNotification = builder.build()
        } else {
            mNotification!!.contentView = remoteViews
        }

        return mNotification
    }


    private fun getRepeatMode(): Int {
        return mRepeatMode
    }

    @Synchronized
    private fun setRepeatMode(repeatmode: Int) {
        mRepeatMode = repeatmode
        setNextTrack()
        saveQueue(false)
        notifyChange(REPEATMODE_CHANGED)
    }

    private fun recentlyPlayed(): Boolean {
        return isPlaying() || System.currentTimeMillis() - mLastPlayedTime < IDLE_DELAY
    }

    private fun setIsSupposedToBePlaying(value: Boolean, notify: Boolean) {
        if (mIsSupposedToBePlaying != value) {
            mIsSupposedToBePlaying = value
            if (!mIsSupposedToBePlaying) {
                scheduleDelayedShutdown()
                mLastPlayedTime = System.currentTimeMillis()
            }

            if (notify) {
                notifyChange(PLAYSTATE_CHANGED)
            }
        }
    }

    private fun cancelShutdown() {
        if (mShutdownScheduled) {
            mAlarmManager?.cancel(mShutdownIntent)
            mShutdownScheduled = false
        }
    }

    private fun saveQueue(full: Boolean) {
        if (!mQueueIsSaveable) {
            return
        }
        val editor = mPreferences.edit()
        if (full) {
            MusicPlaybackState.saveState(
                mPlaylist,
                if (mShuffleMode != SHUFFLE_NONE) mHistory else null
            )
            if (mPlaylistInfo.size > 0) {
                val temp = CoreApplication.GSON.toJson(mPlaylistInfo)
                Logger.i("需要保存的信息: $temp")
                try {
                    RandomAccessFile(
                        File(filesDir.absolutePath + "playlist"),
                        "rws"
                    ).write(temp.getBytes())
                } catch (e: Exception) {
                    Logger.e(e.toString())
                }
                editor.putInt("cardid", mCardId)
            }
        }
        editor.apply {
            putInt("curpos", mPlayPos)
            if (mPlayer?.isInitialized() == true) {
                putLong("seekpos", mPlayer!!.position())
            }
            putInt("repeatmode", mRepeatMode)
            putInt("shufflemode", mShuffleMode)
        }.apply()
    }

    private fun cycleRepeat() {
        if (mRepeatMode == REPEAT_NONE) {
            setRepeatMode(REPEAT_CURRENT)
            if (mShuffleMode != SHUFFLE_NONE) {
                setShuffleMode(SHUFFLE_NONE)
            }
        } else {
            setRepeatMode(REPEAT_NONE)
        }
    }

    private fun cycleShuffle() {
        if (mShuffleMode == SHUFFLE_NONE) {
            setShuffleMode(SHUFFLE_NORMAL)
            if (mRepeatMode == REPEAT_CURRENT) {
                setRepeatMode(REPEAT_ALL)
            }
        } else if (mShuffleMode == SHUFFLE_NORMAL) {
            setShuffleMode(SHUFFLE_NONE)
        }
    }

    private fun scheduleDelayedShutdown() {
        mAlarmManager?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + IDLE_DELAY, mShutdownIntent
        )
        mShutdownScheduled = true
    }

    private class MusicPlayerHandler(service: MediaService, looper: Looper) : Handler(looper) {
        private val mService: WeakReference<MediaService> = WeakReference(service)
        private var mCurrentVolume = 1.0f

        override fun handleMessage(msg: Message) {
            val service = mService.get() ?: return
            synchronized(service) {
                when (msg.what) {
                    FADEDOWN -> {
                        mCurrentVolume -= .05f
                        if (mCurrentVolume > .2f) {
                            sendEmptyMessageDelayed(FADEDOWN, 10)
                        } else {
                            mCurrentVolume = .2f
                        }
                        service.mPlayer?.setVolume(mCurrentVolume)
                    }

                    FADEUP -> {
                        mCurrentVolume += .01f
                        if (mCurrentVolume < 1.0f) {
                            sendEmptyMessageDelayed(FADEUP, 10)
                        } else {
                            mCurrentVolume = 1.0f
                        }
                        service.mPlayer?.setVolume(mCurrentVolume)
                    }

                    SERVER_DIED -> {
                        if (service.isPlaying()) {
                            val info = msg.obj as TrackErrorInfo
                            service.sendErrorMessage(info.mTrackName)
                            service.removeTrack(info.mId)
                        } else {
                            service.openCurrentAndNext()
                        }
                    }

                    TRACK_WENT_TO_NEXT -> {
                        service.setAndRecordPlayPos(service.mNextPlayPos)
                        service.setNextTrack()
                        if (service.mCursor != null) {
                            service.mCursor!!.close()
                            service.mCursor = null
                        }

                        service.updateCursor(service.mPlaylist[service.mPlayPos].mId)
                        service.notifyChange(META_CHANGED)
                        service.notifyChange(MUSIC_CHANGED)
                        service.updateNotification()
                    }
                    TRACK_ENDED -> {
                        if (service.mRepeatMode == REPEAT_CURRENT) {
                            service.seek(0)
                            service.play()
                        } else {
                            service.gotoNext(false)
                        }
                    }
                    RELEASE_WAKELOCK -> {
                        service.mWakeLock?.release()
                    }
                    FOCUSCHANGE -> {
                        when (msg.arg1) {
                            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                                if (service.isPlaying()) {
                                    service.mPausedByTransientLossOfFocus =
                                        msg.arg1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                                }
                                service.pause()
                            }
                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                                removeMessages(FADEUP)
                                sendEmptyMessage(FADEDOWN)
                            }
                            AudioManager.AUDIOFOCUS_GAIN -> if (!service.isPlaying() && service.mPausedByTransientLossOfFocus) {
                                service.mPausedByTransientLossOfFocus = false
                                mCurrentVolume = 0f
                                service.mPlayer?.setVolume(mCurrentVolume)
                                service.play()
                            } else {
                                removeMessages(FADEDOWN)
                                sendEmptyMessage(FADEUP)
                            }
                            else -> {

                            }
                        }
                    }
                    INSERT_RECENT -> {
                        if (service.appDatabase == null) {
                            service.appDatabase = AppDatabase.getInstance(BaseApplication.CONTEXT)
                        }
                        val recentSong =
                            service.appDatabase!!.recentDao().getRecentSong(service.getAudioId())
                        if (recentSong == null) {
                            service.appDatabase!!.recentDao().insertSong(
                                RecentHistory(songId = service.getAudioId())
                            )
                        } else {
                            service.appDatabase!!.recentDao().updateRecent(
                                RecentHistory(songId = recentSong.songId)
                            )
                        }

                    }
                    LRC_DOWNLOADED -> {
                        val lrc = msg.obj as LrcAdnTlyRic
                        service.notifyChange(LRC_UPDATED, lrc)
                    }
                    else -> {
                    }
                }
            }
            super.handleMessage(msg)
        }
    }

    private class ServiceStub(service: MediaService) : MediaAidlInterface.Stub() {
        private val mService = WeakReference(service)

        override fun openFile(path: String?) {
        }

        override fun open(infos: MutableMap<Any?, Any?>?, list: LongArray?, position: Int) {
            mService.get()?.open(
                infos = (infos as HashMap<Long, MusicInfo>),
                list = list!!,
                position = position
            )
        }

        override fun updateLrc(id: Long) {
            mService.get()?.getLrc(id)
        }

        override fun stop() {
            mService.get()?.stop(true)
        }

        override fun pause() {
            mService.get()?.pause()
        }

        override fun play() {
            mService.get()?.play()
        }

        override fun prev(forcePrevious: Boolean) {
            mService.get()?.prev(forcePrevious)
        }

        override fun next() {
            mService.get()?.gotoNext()
        }

        override fun enqueue(list: LongArray?, infos: MutableMap<Any?, Any?>?, action: Int) {
            if (list != null && infos.isNullOrEmpty().not()) {
                mService.get()?.enqueue(list, infos as HashMap<Long, MusicInfo>, action)
            }
        }

        override fun getPlayinfos(): MutableMap<Any?, Any?> {
            return mService.get()?.getPlayinfos()!!.toMutableMap()
        }

        override fun setQueuePosition(index: Int) {
            mService.get()?.setQueuePosition(index)
        }

        override fun setShuffleMode(shufflemode: Int) {
            mService.get()?.setShuffleMode(shufflemode)
        }

        override fun setRepeatMode(repeatmode: Int) {
            mService.get()?.setRepeatMode(repeatmode)
        }

        override fun moveQueueItem(from: Int, to: Int) {
        }

        override fun refresh() {
        }

        override fun playlistChanged() {
        }

        override fun isPlaying(): Boolean {
            return mService.get()?.isPlaying() ?: false
        }

        override fun getQueue(): LongArray {
            return mService.get()?.getQueue() ?: longArrayOf()
        }

        override fun getQueueItemAtPosition(position: Int): Long {
            return mService.get()?.getQueueItemAtPosition(position) ?: -1
        }

        override fun getQueueSize(): Int {
            return mService.get()?.getQueueSize() ?: 0
        }

        override fun getQueuePosition(): Int {
            return mService.get()?.getQueuePosition() ?: -1
        }

        override fun getQueueHistoryPosition(position: Int): Int {
            return -1
        }

        override fun getQueueHistorySize(): Int {
            return -1
        }

        override fun getQueueHistoryList(): IntArray {
            return intArrayOf()
        }

        override fun duration(): Long {
            return mService.get()?.duration() ?: -1
        }

        override fun position(): Long {
            return mService.get()?.position() ?: -1
        }

        override fun secondPosition(): Int {
            return mService.get()?.getSecondPosition() ?: -1
        }

        override fun seek(pos: Long): Long {
            return mService.get()?.seek(pos) ?: -1
        }

        override fun seekRelative(deltaInMs: Long) {
        }

        override fun getAudioId(): Long {
            return mService.get()?.getAudioId() ?: -1
        }

        override fun getCurrentTrack(): MusicTrack {
            return mService.get()?.getCurrentTrack() ?: MusicTrack(-1, -1)
        }

        override fun getTrack(index: Int): MusicTrack {
            return mService.get()?.getTrack(index) ?: MusicTrack(-1, -1)
        }

        override fun getNextAudioId(): Long {
            return -1
        }

        override fun getPreviousAudioId(): Long {
            return -1
        }

        override fun getArtistId(): Long {
            return mService.get()?.getArtistId() ?: -1
        }

        override fun getAlbumId(): Long {
            return mService.get()?.getAlbumId() ?: -1
        }

        override fun getArtistName(): String {
            return mService.get()?.getArtistName() ?: ""
        }

        override fun getTrackName(): String {
            return mService.get()?.getTrackName() ?: ""
        }

        override fun isTrackLocal(): Boolean {
            return false
        }

        override fun getAlbumName(): String {
            return mService.get()?.getAlbumName() ?: ""
        }

        override fun getAlbumPath(): String {
            return mService.get()?.getAlbumPath() ?: ""
        }

        override fun getAlbumPathtAll(): Array<String?> {
            return mService.get()?.getAlbumPathAll() ?: arrayOf()
        }

        override fun getTrackNameAll(): Array<String?> {
            return mService.get()?.getTrackNameAll() ?: arrayOf()
        }

        override fun getTrackArtistNameAll(): Array<String?> {
            return mService.get()?.getTrackArtistNameAll() ?: arrayOf()
        }

        override fun getPath(): String {
            return ""
        }

        override fun getShuffleMode(): Int {
            return mService.get()?.mShuffleMode ?: SHUFFLE_NONE
        }

        override fun removeTracks(first: Int, last: Int): Int {
            return mService.get()?.removeTracks(first, last) ?: -1
        }

        override fun removeTrack(id: Long): Int {
            return mService.get()?.removeTrack(id) ?: -1
        }

        override fun removeTrackAtPosition(id: Long, position: Int): Boolean {
            return false
        }

        override fun getRepeatMode(): Int {
            return mService.get()?.getRepeatMode() ?: REPEAT_ALL
        }

        override fun getMediaMountedCount(): Int {
            return 0
        }

        override fun getAudioSessionId(): Int {
            return 0
        }

        override fun setLockscreenAlbumArt(enabled: Boolean) {

        }

        override fun exit() {
            mService.get()?.exit()
        }

        override fun timing(time: Int) {
            mService.get()?.timing(time)
        }
    }

    private fun exit() {
        Logger.e("Destroying service")
        stop(true)
        cancelNotification()
        mAudioManager?.abandonAudioFocus(mAudioFocusListener)
        if (fromL())
            mSession?.isActive = false
        saveQueue(true)
        stopSelf(mServiceStartId)
    }

    private fun timing(time: Int) {
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, Intent(PAUSE_ACTION),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC, System.currentTimeMillis() + time, pendingIntent)

    }

    @Synchronized
    private fun setShuffleMode(shufflemode: Int) {
        if (mShuffleMode == shufflemode && mPlaylist.isNotEmpty()) return
        mShuffleMode = shufflemode
        setNextTrack()
        saveQueue(false)
        notifyChange(SHUFFLEMODE_CHANGED)
    }

    private fun setNextTrack() {
        setNextTrack(getNextPosition(false))
    }

    private fun setNextTrack(position: Int) {
        mNextPlayPos = position
        if (mNextPlayPos >= 0 && mNextPlayPos < mPlaylist.size) {
            val id = mPlaylist[mNextPlayPos].mId
            val musicInfo = mPlaylistInfo[id]
            if (musicInfo != null) {
                if (musicInfo.islocal) {
                    mPlayer?.setNextDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString() + "/" + id)
                } else {
                    mPlayer?.setNextDataSource(null)
                }

            }
        } else {
            mPlayer?.setNextDataSource(null)
        }
    }

    @Synchronized
    private fun removeTracksInternal(first: Int, last: Int): Int {
        var mFirst = first
        var mLast = last
        when {
            mLast < mFirst -> return 0
            mFirst < 0 -> mFirst = 0
            mLast >= mPlaylist.size -> mLast = mPlaylist.size - 1
        }
        var gotonext = false
        if (mPlayPos in mFirst..mLast) {
            mPlayPos = first
            gotonext = true
        } else if (mPlayPos > mLast) {
            mPlayPos -= mLast - mFirst + 1
        }
        val numToRemove = mLast - mFirst + 1
        if (mFirst == 0 && mLast == mPlaylist.size - 1) {
            mPlayPos = -1
            mNextPlayPos = -1
            mPlaylist.clear()
            mHistory.clear()
        } else {
            for (i in 0 until numToRemove) {
                mPlaylistInfo.remove(mPlaylist[mFirst].mId)
                mPlaylist.removeAt(mFirst)
            }
            val positionIterator = mHistory.listIterator()
            while (positionIterator.hasNext()) {
                val pos = positionIterator.next()
                if (pos in mFirst..mLast) {
                    positionIterator.remove()
                } else if (pos > mLast) {
                    positionIterator.set(pos - numToRemove)
                }
            }
        }

        if (gotonext) {
            if (mPlaylist.isEmpty()) {
                stop(true)
                mPlayPos = -1
                closeCursor()
            } else {
                if (mShuffleMode != SHUFFLE_NONE) {
                    mPlayPos = getNextPosition(true)
                } else if (mPlayPos >= mPlaylist.size) {
                    mPlayPos = 0
                }
                val wasPlaying = isPlaying()
                stop(false)
                openCurrentAndNext()
                if (wasPlaying) {
                    play()
                }
            }
            notifyChange(META_CHANGED)
        }
        return mLast - mFirst + 1
    }

    private fun startProxy() {
        if (mProxy == null) {
            mProxy = MediaPlayerProxy(this)
            mProxy!!.init()
            mProxy!!.start()
        }
    }

    @Synchronized
    private fun open(infos: HashMap<Long, MusicInfo>, list: LongArray, position: Int) {
        mPlaylistInfo.clear()
        mPlaylistInfo = infos
        Logger.i("拿到的数据：${mPlaylistInfo.keys}")
        val oldId = getAudioId()
        val listlength = list.size
        var newlist = true
        if (mPlaylist.size == listlength) {
            newlist = false
            for (i in 0 until listlength) {
                if (list[i] != mPlaylist[i].mId) {
                    newlist = true
                    break
                }
            }
        }

        if (newlist) {
            addToPlayList(list, -1)
            notifyChange(QUEUE_CHANGED)
        }
        mPlayPos = if (position >= 0) {
            position
        } else {
            mShuffler.nextInt(mPlaylist.size)
        }

        mHistory.clear()
        openCurrentAndNextPlay(true)
        if (oldId != getAudioId()) {
            notifyChange(META_CHANGED)
        }
    }

    private fun addToPlayList(list: LongArray, position: Int) {
        Logger.i(" addToPlayList listSize =${list.size} ")
        val addlen = list.size
        var mPosition = position
        if (mPosition < 0) {
            mPlaylist.clear()
            mPosition = 0
        }

        mPlaylist.ensureCapacity(mPlaylist.size + addlen)
        if (mPosition > mPlaylist.size) {
            mPosition = mPlaylist.size
        }

        val arrayList = ArrayList<MusicTrack>(addlen)
        for (i in list.indices) {
            arrayList.add(MusicTrack(list[i], i))
        }

        mPlaylist.addAll(mPosition, arrayList)
        Logger.i("mPlaylist size  = ${mPlaylist.size}")
        if (mPlaylist.size == 0) {
            closeCursor()
            notifyChange(META_CHANGED)
        }
    }

    private fun openCurrentAndNextPlay(play: Boolean) {
        openCurrentAndMaybeNext(play, true)
    }

    private fun openCurrentAndNext() {
        openCurrentAndMaybeNext(play = false, openNext = true)
    }

    @Synchronized
    private fun openCurrentAndMaybeNext(play: Boolean, openNext: Boolean) {
        closeCursor()
        stop(false)
//        var shutdown = false

        if (mPlaylist.isEmpty() || mPlaylistInfo.isEmpty() && mPlayPos >= mPlaylist.size) {
            clearPlayInfos()
            return
        }
        val id = mPlaylist[mPlayPos].mId
        updateCursor(id)
        getLrc(id)
        if (mPlaylistInfo[id] == null) {
            return
        }
        if (mPlaylistInfo[id]!!.islocal.not()) {
            if (mRequestUrl != null) {
                mRequestUrl!!.stop()
                mUrlHandler.removeCallbacks(mRequestUrl)
            }
            mRequestUrl = RequestPlayUrl(id, play)
            mUrlHandler.postDelayed(mRequestUrl, 70)
        }

        if (openNext) {
            setNextTrack()
        }
    }

    @Synchronized
    private fun getLrc(id: Long) {
        Logger.d("------获取歌词------")
        GlobalScope.launch {
            if (appDatabase == null) {
                appDatabase = AppDatabase.getInstance(BaseApplication.CONTEXT)
            }
            val urlAndLrcDao = appDatabase!!.urlAndLrc()
            var song = urlAndLrcDao.getSong(id)
            if (song == null) {
                song = SongLrc(songId = id)
                urlAndLrcDao.insert(song)
            }
            if (song.lrc.isNullOrEmpty()) {
                val songLrc =
                    withContext(Dispatchers.IO) { PlayServiceRepository(baseContext).getLrc(id.toString()) }
                song.lrc = songLrc.lrc?.lyric ?: ""
                song.tlyric = songLrc.tlyric?.lyric ?: ""

                urlAndLrcDao.updateSong(song)
            }
            val message = Message().apply {
                what = LRC_DOWNLOADED
                obj = LrcAdnTlyRic(id = song.songId, lrc = song.lrc, lyric = song.tlyric)
            }
            mPlayerHandler.sendMessage(message)
        }
    }


    private fun updateCursor(trackId: Long) {
        val musicInfo = mPlaylistInfo[trackId]
        if (musicInfo != null) {
            val cursor = MatrixCursor(PROJECTION)
            cursor.addRow(
                arrayOf(
                    musicInfo.songId,
                    musicInfo.artist,
                    musicInfo.albumName,
                    musicInfo.musicName,
                    musicInfo.albumPic,
                    musicInfo.albumId,
                    musicInfo.artistId
                )
            )
            cursor.moveToFirst()
            mCursor = cursor
            cursor.close()
        }
    }

    private fun clearPlayInfos() {
        val file = File(filesDir.absolutePath + "playlist")
        if (file.exists()) {
            file.delete()
        }
        MusicPlaybackState.clearQueue()
    }

    private fun reloadQueue() {
        Logger.d("$TAG reloadQueue")
        GlobalScope.launch {
            var id = mCardId
            if (mPreferences.contains("cardid")) {
                id = mPreferences.getInt("cardid", mCardId.inv())
            }

            if (id == mCardId) {

                mPlaylist = MusicPlaybackState.getQueue()

                try {
                    val file = File(filesDir.absolutePath + "playlist")
                    if (file.exists()) {
                        val text = file.readText()
                        val type = object : TypeToken<HashMap<Long, MusicInfo>>() {}.type
                        val play =
                            CoreApplication.GSON.fromJson<HashMap<Long, MusicInfo>>(text, type)
                        if (play != null && play.size > 0) {
                            mPlaylistInfo = play
                        }
                    }
                } catch (e: Exception) {
                    Logger.e(e.toString())
                }
            }

            if (mPlaylist.size == mPlaylistInfo.size && mPlaylist.size > 0) {
                val pos = mPreferences.getInt("curpos", 0)

                if (pos < 0 || pos >= mPlaylist.size) {
                    mPlaylist.clear()
                    this.cancel()
                    return@launch
                }
                mPlayPos = pos
                Logger.i("mPlayPos = $mPlayPos")
                updateCursor(mPlaylist[mPlayPos].mId)
                if (mCursor == null) {
                    SystemClock.sleep(3000)
                    updateCursor(mPlaylist[mPlayPos].mId)
                }
                synchronized(this) {
                    closeCursor()
                    mOpenFailedCounter = 20
                    openCurrentAndNext()
                }
                val seekpos = mPreferences.getLong("seekpos", 0)
                mLastSeekPos = seekpos
                seek(if (seekpos >= 0 && seekpos < duration()) seekpos else 0)

                var repmode = mPreferences.getInt("repeatmode", REPEAT_ALL)
                if (repmode != REPEAT_ALL && repmode != REPEAT_CURRENT) {
                    repmode = REPEAT_NONE
                }
                mRepeatMode = repmode

                var shufmode = mPreferences.getInt("shufflemode", SHUFFLE_NONE)
                if (shufmode != SHUFFLE_NORMAL) {
                    shufmode = SHUFFLE_NONE
                }
                if (shufmode != SHUFFLE_NONE) {
                    mHistory = MusicPlaybackState.getHistory()
                }
                mShuffleMode = shufmode
            } else {
                clearPlayInfos()
            }

            notifyChange(MUSIC_CHANGED)

            notifyChange(QUEUE_CHANGED)
            notifyChange(META_CHANGED)
        }

    }

    fun duration(): Long {
        return if (mPlayer?.isInitialized() == true && mPlayer?.isTrackPrepared() == true) {
            mPlayer!!.duration()
        } else -1
    }

    private fun getNextPosition(force: Boolean): Int {
        if (mPlaylist.isEmpty()) {
            return -1
        }
        if (!force && mRepeatMode == REPEAT_CURRENT) {
            return if (mPlayPos < 0) {
                0
            } else mPlayPos
        } else if (mShuffleMode == SHUFFLE_NORMAL) {
            val numTracks = mPlaylist.size

            val trackNumPlays = IntArray(numTracks)
            for (i in 0 until numTracks) {
                trackNumPlays[i] = 0
            }

            val numHistory = mHistory.size
            for (i in 0 until numHistory) {
                val idx = mHistory[i]
                if (idx in 0 until numTracks) {
                    trackNumPlays[idx]++
                }
            }
            if (mPlayPos in 0 until numTracks) {
                trackNumPlays[mPlayPos]++
            }

            var minNumPlays = Integer.MAX_VALUE
            var numTracksWithMinNumPlays = 0
            for (i in trackNumPlays.indices) {
                if (trackNumPlays[i] < minNumPlays) {
                    minNumPlays = trackNumPlays[i]
                    numTracksWithMinNumPlays = 1
                } else if (trackNumPlays[i] == minNumPlays) {
                    numTracksWithMinNumPlays++
                }
            }

            if (minNumPlays > 0 && numTracksWithMinNumPlays == numTracks
                && mRepeatMode != REPEAT_ALL && !force
            ) {
                return -1
            }

            var skip = mShuffler.nextInt(numTracksWithMinNumPlays)
            for (i in trackNumPlays.indices) {
                if (trackNumPlays[i] == minNumPlays) {
                    if (skip == 0) {
                        return i
                    } else {
                        skip--
                    }
                }
            }

            return -1
        } else {
            if (mPlayPos >= mPlaylist.size - 1) {
                if (mRepeatMode == REPEAT_NONE && !force) {
                    return -1
                } else if (mRepeatMode == REPEAT_ALL || force) {
                    return 0
                }
                return -1
            } else {
                return mPlayPos + 1
            }
        }
    }

    @Synchronized
    private fun closeCursor() {
        if (mCursor != null) {
            mCursor!!.close()
            mCursor = null
        }
    }

    private inner class RequestPlayUrl(val id: Long, val play: Boolean) :
        java.lang.Runnable {
        private var stop: Boolean = false

        fun stop() {
            stop = true
        }

        override fun run() {
            GlobalScope.launch {
                var url: String?
                url = PreferencesUtility.getInstance(this@MediaService).getPlayLink(id)
                if (url.isNullOrEmpty()) {
                    val urlData =
                        withContext(Dispatchers.IO) { PlayServiceRepository(baseContext).getPlayUrl(id.toString()) }
                    url = urlData.data[0].url
                    PreferencesUtility.getInstance(this@MediaService).setPlayLink(id, url)
                }
                if (url.isNullOrEmpty()) {
                    gotoNext()
                } else {
                    Logger.d("current url = $url")
                }
                if (!stop) {
                    startProxy()
                    var urlEn = url
                    urlEn = mProxy!!.getProxyURL(urlEn)
                    mPlayer?.setDataSource(urlEn)
                }
                if (play && !stop) {
                    play()
                }
            }
        }
    }

    private fun isPlaying(): Boolean {
        return mIsSupposedToBePlaying
    }

    private fun stop(goToIdle: Boolean) {
        if (mPlayer?.isInitialized() == true) {
            mPlayer!!.stop()
        }
        if (getQueueSize() <= 0) {
            cancelNotification()
        }
        mFileToPlay = null
        closeCursor()
        if (goToIdle) {
            setIsSupposedToBePlaying(value = false, notify = false)
        } else {
            if (fromL()) {
                stopForeground(true)
            } else {
                stopForeground(false)
            }
        }
    }

    fun loading(l: Boolean) {
        val intent = Intent(MUSIC_LODING)
        intent.putExtra("isloading", l)
        sendBroadcast(intent)
    }

    @Synchronized
    private fun getQueue(): LongArray {
        val len = mPlaylist.size
        val list = LongArray(len)
        for (i in 0 until len) {
            list[i] = mPlaylist[i].mId
        }
        return list
    }

    @Synchronized
    private fun getQueuePosition(): Int {
        return mPlayPos
    }

    @Synchronized
    private fun getQueueSize(): Int {
        return mPlaylist.size
    }

    @Synchronized
    private fun getQueueItemAtPosition(position: Int): Long {
        if (position >= 0 && position < mPlaylist.size) {
            return mPlaylist[position].mId
        }
        return -1
    }

    @Synchronized
    private fun setQueuePosition(index: Int) {
        stop(false)
        mPlayPos = index
        openCurrentAndNext()
        play()
        notifyChange(META_CHANGED)
//        if (mShuffleMode == SHUFFLE_AUTO) {
//            doAutoShuffleUpdate()
//        }
    }

    @Synchronized
    private fun getPlayinfos(): HashMap<Long, MusicInfo> {
        return mPlaylistInfo
    }

    @Synchronized
    private fun enqueue(list: LongArray, map: HashMap<Long, MusicInfo>, action: Int) {
        mPlaylistInfo.putAll(map)
        if (action == NEXT && mPlayPos + 1 < mPlaylist.size) {
            addToPlayList(list, mPlayPos + 1)
            mNextPlayPos = mPlayPos + 1
            notifyChange(QUEUE_CHANGED)
        } else {
            addToPlayList(list, Integer.MAX_VALUE)
            notifyChange(QUEUE_CHANGED)
        }

        if (mPlayPos < 0) {
            mPlayPos = 0
            openCurrentAndNext()
            play()
            notifyChange(META_CHANGED)
        }
    }

    @Synchronized
    private fun gotoNext(force: Boolean = true) {
        if (mPlaylist.isEmpty()) {
            scheduleDelayedShutdown()
            return
        }

        var pos = mNextPlayPos
        if (pos < 0) {
            pos = getNextPosition(force)
        }

        if (pos < 0) {
            setIsSupposedToBePlaying(value = false, notify = true)
            return
        }

        stop(false)
        setAndRecordPlayPos(pos)
        openCurrentAndNext()
        play()
        notifyChange(META_CHANGED)
        notifyChange(MUSIC_CHANGED)
    }

    @Synchronized
    private fun setAndRecordPlayPos(nextPos: Int) {
        if (mShuffleMode != SHUFFLE_NONE) {
            mHistory.add(mPlayPos)
            if (mHistory.size > MAX_HISTORY_SIZE) {
                mHistory.removeAt(0)
            }
        }

        mPlayPos = nextPos
    }

    private fun notifyChange(what: String, lrc: LrcAdnTlyRic? = null) {
        Logger.d("notifyChange: what = $what")
        if (SEND_PROGRESS == what) {
            val intent = Intent(what)
            intent.putExtra("position", position())
            intent.putExtra("duration", duration())
            sendStickyBroadcast(intent)
            return
        }
        if (LRC_UPDATED == what) {
            val intent = Intent(what).apply {
                putExtra("lrc", lrc)
            }
            sendStickyBroadcast(intent)
            return
        }
        if (fromL()) updateMediaSession(what)

        if (what == POSITION_CHANGED) return

        val intent = Intent(what).apply {
            putExtra("id", getAudioId())
            putExtra("artist", getArtistName())
            putExtra("album", getAlbumName())
            putExtra("track", getTrackName())
            putExtra("playing", isPlaying())
            putExtra("albumuri", getAlbumPath())
            putExtra("islocal", isTrackLocal())
        }
        sendStickyBroadcast(intent)
        val musicIntent = Intent(intent)
        musicIntent.action = what.replace(TIMBER_PACKAGE_NAME, MUSIC_PACKAGE_NAME)
        sendStickyBroadcast(musicIntent)

        if (what == META_CHANGED) {
            val audioId = getAudioId()
            if (audioId > 0) {
                mPlayerHandler.sendEmptyMessage(INSERT_RECENT)
            }
        } else if (what == QUEUE_CHANGED) {
            saveQueue(true)
            if (isPlaying()) {
                if (mNextPlayPos >= 0 && mNextPlayPos < mPlaylist.size
                    && mShuffleMode != SHUFFLE_NONE
                ) {
                    setNextTrack(mNextPlayPos)
                } else {
                    setNextTrack()
                }
            }
        } else {
            saveQueue(false)
        }

        if (what == PLAYSTATE_CHANGED) {
            updateNotification()
        }
    }

    @TargetApi(LOLLIPOP)
    private fun updateMediaSession(what: String) {
        val playState = if (mIsSupposedToBePlaying)
            PlaybackState.STATE_PLAYING
        else
            PlaybackState.STATE_PAUSED
        if (what == PLAYSTATE_CHANGED || what == POSITION_CHANGED) {
            mSession?.setPlaybackState(
                PlaybackState.Builder()
                    .setState(playState, position(), 1.0f)
                    .setActions(
                        PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_PLAY_PAUSE or
                                PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS
                    )
                    .build()
            )
        } else if (what == META_CHANGED || what == QUEUE_CHANGED) {
//            var albumArt: Bitmap? = null
//            if (albumArt != null) {
//                var config: Bitmap.Config? = albumArt.config
//                if (config == null) {
//                    config = Bitmap.Config.ARGB_8888
//                }
//                albumArt = albumArt.copy(config, false)
//            }
            mSession?.setMetadata(
                MediaMetadata.Builder()
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, getArtistName())
                    .putString(MediaMetadata.METADATA_KEY_ALBUM, getAlbumName())
                    .putString(MediaMetadata.METADATA_KEY_TITLE, getTrackName())
                    .putLong(MediaMetadata.METADATA_KEY_DURATION, duration())
                    .putLong(
                        MediaMetadata.METADATA_KEY_TRACK_NUMBER,
                        (getQueuePosition() + 1).toLong()
                    )
                    .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, getQueue().size.toLong())
                    .putBitmap(
                        MediaMetadata.METADATA_KEY_ALBUM_ART, null
                    )
                    .build()
            )

            mSession?.setPlaybackState(
                PlaybackState.Builder()
                    .setState(playState, position(), 1.0f)
                    .setActions(
                        PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE or PlaybackState.ACTION_PLAY_PAUSE or
                                PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS
                    )
                    .build()
            )

        }
    }

    private fun removeTrack(id: Long): Int {
        var numremoved = 0
        synchronized(this) {
            var i = 0
            while (i < mPlaylist.size) {
                if (mPlaylist[i].mId == id) {
                    numremoved += removeTracksInternal(i, i)
                    i--
                }
                i++
            }
            mPlaylistInfo.remove(id)
        }
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED)
        }
        return numremoved
    }

    private fun removeTracks(first: Int, last: Int): Int {
        val numremoved = removeTracksInternal(first, last)
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED)
        }
        return numremoved
    }

    private fun getAudioId(): Long {
        val track = getCurrentTrack()
        return track?.mId ?: -1
    }

    private fun getCurrentTrack(): MusicTrack? {
        return getTrack(mPlayPos)
    }

    @Synchronized
    private fun isTrackLocal(): Boolean {
        val info = mPlaylistInfo[getAudioId()] ?: return false
        return info.islocal
    }


    @Synchronized
    private fun getArtistId(): Long {
        return if (mCursor == null) {
            -1
        } else mCursor!!.getLong(mCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID))
    }

    @Synchronized
    private fun getTrackName(): String {
        return if (mCursor == null) {
            ""
        } else mCursor!!.getString(mCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
    }

    @Synchronized
    private fun getArtistName(): String {
        return if (mCursor == null) {
            ""
        } else mCursor!!.getString(mCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
    }

    @Synchronized
    private fun getAlbumName(): String {
        return if (mCursor == null) {
            ""
        } else mCursor!!.getString(mCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
    }

    @Synchronized
    private fun getAlbumPath(): String {
        return if (mCursor == null) {
            ""
        } else mCursor!!.getString(mCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE))
    }

    @Synchronized
    private fun getAlbumPathAll(): Array<String?> {
        try {
            val len = mPlaylistInfo.size
            val albums = arrayOfNulls<String>(len)
            val queue = getQueue()
            Logger.i("mPlaylistInfo size = $len  queue size=${queue.size}")
            for (i in 0 until len) {
                mPlaylistInfo[queue[i]]?.let {
                    albums[i] = it.albumPic
                }
            }
            return albums
        } catch (e: Exception) {
            Logger.e(e.toString())
        }
        return arrayOf()
    }

    @Synchronized
    private fun getTrackNameAll(): Array<String?> {
        return try {
            val len = mPlaylistInfo.size
            val albums = arrayOfNulls<String>(len)
            val queue = getQueue()
            Logger.i("mPlaylistInfo size = $len  queue size=${queue.size}")
            for (i in 0 until len) {
                mPlaylistInfo[queue[i]]?.let {
                    albums[i] = it.musicName
                }
            }
            albums
        } catch (e: Exception) {
            Logger.e(e.toString())
            arrayOf()
        }
    }

    @Synchronized
    private fun getTrackArtistNameAll(): Array<String?> {
        return try {
            val len = mPlaylistInfo.size
            val artist = arrayOfNulls<String>(len)
            val queue = getQueue()
            for (i in 0 until len) {
                mPlaylistInfo[queue[i]]?.let {
                    artist[i] = it.artist
                }
            }
            artist
        } catch (e: Exception) {
            Logger.e(e.toString())
            arrayOf()
        }
    }

    @Synchronized
    private fun getAlbumId(): Long {
        return if (mCursor == null) {
            -1
        } else mCursor!!.getLong(mCursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
    }

    @Synchronized
    private fun getTrack(index: Int): MusicTrack? {
        return if (index >= 0 && index < mPlaylist.size) {
            mPlaylist[index]
        } else null

    }

    private fun sendUpdateBuffer(progress: Int) {
        val intent = Intent(BUFFER_UP)
        intent.putExtra("progress", progress)
        sendBroadcast(intent)
    }

    private fun sendErrorMessage(trackName: String?) {
        val i = Intent(TRACK_ERROR)
        i.putExtra(TRACK_NAME, trackName)
        sendBroadcast(i)
    }

    private class Shuffler {

        private val mHistoryOfNumbers = LinkedList<Int>()

        private val mPreviousNumbers = TreeSet<Int>()

        private val mRandom = Random()

        private var mPrevious: Int = 0

        fun nextInt(interval: Int): Int {
            var next: Int
            do {
                next = mRandom.nextInt(interval)
            } while (next == mPrevious && interval > 1
                && !mPreviousNumbers.contains(Integer.valueOf(next))
            )
            mPrevious = next
            mHistoryOfNumbers.add(mPrevious)
            mPreviousNumbers.add(mPrevious)
            cleanUpHistory()
            return next
        }


        private fun cleanUpHistory() {
            if (!mHistoryOfNumbers.isEmpty() && mHistoryOfNumbers.size >= MAX_HISTORY_SIZE) {
                for (i in 0 until 1.coerceAtLeast(MAX_HISTORY_SIZE / 2)) {
                    mPreviousNumbers.remove(mHistoryOfNumbers.removeFirst())
                }
            }
        }
    }

    @Synchronized
    private fun getAudioSessionId(): Int {
        return mPlayer?.getAudioSessionId() ?: -1
    }

    private class MultiPlayer(private val service: MediaService) : MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
        private val mService: WeakReference<MediaService> = WeakReference(service)
        private var mCurrentMediaPlayer: MediaPlayer? = MediaPlayer()

        private var mNextMediaPlayer: MediaPlayer? = null

        private var mHandler: Handler? = null

        private var mIsInitialized = false

        private var mNextMediaPath: String? = null

        private var isFirstLoad = true


        var sencondaryPosition = 0

        private val handler = Handler()

        init {
            mCurrentMediaPlayer?.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK)
        }


        fun setDataSource(path: String) {
            mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer!!, path)
            if (mIsInitialized) {
                setNextDataSource(null)
            }
        }

        fun setNextDataSource(path: String?) {
            mNextMediaPath = null
            mIsNextInitialized = false
            try {
                mCurrentMediaPlayer?.setNextMediaPlayer(null)
            } catch (e: IllegalArgumentException) {
                Logger.i("Next media player is current one, continuing")
            } catch (e: IllegalStateException) {
                Logger.e("Media player not initialized!")
                return
            }

            if (mNextMediaPlayer != null) {
                mNextMediaPlayer!!.release()
                mNextMediaPlayer = null
            }
            if (path == null) {
                return
            }
            mNextMediaPlayer = MediaPlayer()
            mNextMediaPlayer!!.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK)
            mNextMediaPlayer!!.audioSessionId = getAudioSessionId()

            if (setNextDataSourceImpl(mNextMediaPlayer!!, path)) {
                mNextMediaPath = path
                mCurrentMediaPlayer?.setNextMediaPlayer(mNextMediaPlayer)
                // mHandler.post(setNextMediaPlayerIfPrepared);

            } else {
                if (mNextMediaPlayer != null) {
                    mNextMediaPlayer!!.release()
                    mNextMediaPlayer = null
                }
            }
        }

        fun setNextDataSourceImpl(player: MediaPlayer, path: String): Boolean {
            mIsNextTrackPrepared = false
            try {
                player.reset()
                player.setAudioStreamType(AudioManager.STREAM_MUSIC)
                if (path.startsWith("content://")) {
                    player.setOnPreparedListener(preparedNextListener)
                    player.setDataSource(BaseApplication.CONTEXT, Uri.parse(path))
                    player.prepare()


                } else {
                    player.setDataSource(path)
                    player.setOnPreparedListener(preparedNextListener)
                    player.prepare()
                    mIsNextTrackPrepared = false
                }

            } catch (todo: IOException) {

                return false
            } catch (todo: IllegalArgumentException) {

                return false
            }

            player.setOnCompletionListener(this)
            player.setOnErrorListener(this)
            return true
        }

        private var mIsTrackPrepared = false
        private var mIsTrackNet = false
        private var mIsNextTrackPrepared = false
        private var mIsNextInitialized = false
        private var mIllegalState = false

        fun setDataSourceImpl(player: MediaPlayer, path: String): Boolean {
            mIsTrackNet = false
            mIsTrackPrepared = false
            try {
                player.reset()
                player.setAudioStreamType(AudioManager.STREAM_MUSIC)
                if (path.startsWith("content://")) {
                    player.setOnPreparedListener(null)
                    player.setDataSource(BaseApplication.CONTEXT, Uri.parse(path))
                    player.prepare()
                    mIsTrackPrepared = true
                    player.setOnCompletionListener(this)

                } else {
                    player.setDataSource(path)
                    player.setOnPreparedListener(preparedListener)
                    player.prepareAsync()
                    mIsTrackNet = true
                }
                if (mIllegalState) {
                    mIllegalState = false
                }

            } catch (e: IOException) {
                return false
            } catch (e: IllegalArgumentException) {
                return false
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                if (!mIllegalState) {
                    mCurrentMediaPlayer = null
                    mCurrentMediaPlayer = MediaPlayer()
                    mCurrentMediaPlayer!!.setWakeMode(
                        mService.get(),
                        PowerManager.PARTIAL_WAKE_LOCK
                    )
                    mCurrentMediaPlayer!!.audioSessionId = getAudioSessionId()
                    setDataSourceImpl(mCurrentMediaPlayer!!, path)
                    mIllegalState = true
                } else {
                    mIllegalState = false
                    return false
                }
            }
            player.setOnErrorListener(this)
            player.setOnBufferingUpdateListener(bufferingUpdateListener)
            return true
        }

        private val preparedNextListener = MediaPlayer.OnPreparedListener {
            mIsNextTrackPrepared = true
        }

        private val preparedListener = MediaPlayer.OnPreparedListener { mp ->
            if (isFirstLoad) {
                val seekpos = mService.get()!!.mLastSeekPos
                seek(if (seekpos >= 0) seekpos else 0)
                isFirstLoad = false
            }
            mService.get()?.notifyChange(META_CHANGED)
            mp?.setOnCompletionListener(this@MultiPlayer)
            mIsTrackPrepared = true
        }

        private val bufferingUpdateListener = MediaPlayer.OnBufferingUpdateListener { _, percent ->
            if (sencondaryPosition != 100)
                mService.get()!!.sendUpdateBuffer(percent)
            sencondaryPosition = percent
        }


        fun setHandler(handler: Handler) {
            mHandler = handler
        }


        fun isInitialized(): Boolean {
            return mIsInitialized
        }

        fun isTrackPrepared(): Boolean {
            return mIsTrackPrepared
        }

        fun start() {
            if (!mIsTrackNet) {
                mService.get()!!.sendUpdateBuffer(100)
                sencondaryPosition = 100
                mCurrentMediaPlayer?.start()
            } else {
                sencondaryPosition = 0
                mService.get()!!.loading(true)
                sendStartMediaPlayerIfPrepared(50)
            }
            mService.get()!!.notifyChange(MUSIC_CHANGED)
        }

        private val startMediaPlayerIfPrepared = Runnable {
            if (mIsTrackPrepared) {
                mCurrentMediaPlayer?.start()
                val duration = duration()
                if (mService.get()!!.mRepeatMode != REPEAT_CURRENT && duration > 2000
                    && position() >= duration - 2000
                ) {
                    mService.get()!!.gotoNext()
                    Logger.d("play to go")
                }
                mService.get()!!.loading(false)
            } else {
                sendStartMediaPlayerIfPrepared(700)
            }
        }

        private fun sendStartMediaPlayerIfPrepared(delayMillis: Long) {
            handler.postDelayed(startMediaPlayerIfPrepared, delayMillis)

        }

        fun stop() {
//            handler.removeCallbacks(setNextMediaPlayerIfPrepared)
            handler.removeCallbacks(startMediaPlayerIfPrepared)
            mCurrentMediaPlayer?.reset()
            mIsInitialized = false
            mIsTrackPrepared = false
        }

        fun release() {
            mCurrentMediaPlayer?.release()
        }


        fun pause() {
            handler.removeCallbacks(startMediaPlayerIfPrepared)
            mCurrentMediaPlayer?.pause()
        }


        fun duration(): Long {
            return if (mIsTrackPrepared) {
                mCurrentMediaPlayer?.duration?.toLong() ?: -1
            } else -1
        }


        fun position(): Long {
            if (mIsTrackPrepared) {
                try {
                    return mCurrentMediaPlayer?.currentPosition?.toLong() ?: -1
                } catch (e: Exception) {
                    Logger.e(e.toString())
                    e.printStackTrace()
                }

            }
            return -1
        }

        fun seek(whereto: Long): Long {
            if (fromO()) {
                mCurrentMediaPlayer?.seekTo(whereto, SEEK_CLOSEST)
            } else {
                mCurrentMediaPlayer?.seekTo(whereto.toInt())
            }
            return whereto
        }

        fun setVolume(vol: Float) {
            try {
                mCurrentMediaPlayer?.setVolume(vol, vol)
            } catch (e: Exception) {
                Logger.e(e.toString())
            }

        }

        fun getAudioSessionId(): Int {
            return mCurrentMediaPlayer?.audioSessionId ?: 0
        }

        override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
            if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                val service = mService.get()
                val errorInfo = TrackErrorInfo(
                    service!!.getAudioId(),
                    service.getTrackName()
                )

                mIsInitialized = false
                mIsTrackPrepared = false
                mCurrentMediaPlayer?.release()
                mCurrentMediaPlayer = MediaPlayer()
                mCurrentMediaPlayer?.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK)
                val msg = mHandler?.obtainMessage(SERVER_DIED, errorInfo)
                mHandler?.sendMessageDelayed(msg, 2000)
                return true
            }
            return false
        }

        override fun onCompletion(mp: MediaPlayer?) {
            if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                mCurrentMediaPlayer?.release()
                mCurrentMediaPlayer = mNextMediaPlayer
                mNextMediaPath = null
                mNextMediaPlayer = null
                mHandler?.sendEmptyMessage(TRACK_WENT_TO_NEXT)
            } else {
                mService.get()?.mWakeLock?.acquire(30000)
                mHandler?.sendEmptyMessage(TRACK_ENDED)
                mHandler?.sendEmptyMessage(RELEASE_WAKELOCK)
            }
        }
    }

    private class TrackErrorInfo(var mId: Long = -1, var mTrackName: String?)

    companion object {
        private const val TAG = "MusicPlaybackService"

        const val PLAYSTATE_CHANGED = "com.xw.xmusic.playstatechanged"
        const val POSITION_CHANGED = "com.xw.xmusic.positionchanged"
        const val META_CHANGED = "com.xw.xmusic.metachanged"
        const val PLAYLIST_ITEM_MOVED = "com.xw.xmusic.mmoved"
        const val QUEUE_CHANGED = "com.xw.xmusic.queuechanged"
        const val PLAYLIST_CHANGED = "com.xw.xmusic.playlistchanged"
        const val REPEATMODE_CHANGED = "com.xw.xmusic.repeatmodechanged"
        const val SHUFFLEMODE_CHANGED = "com.xw.xmusic.shufflemodechanged"
        const val TRACK_ERROR = "com.xw.xmusic.trackerror"
        const val TIMBER_PACKAGE_NAME = "com.xw.xmusic"
        const val MUSIC_PACKAGE_NAME = "com.android.music"
        const val SERVICECMD = "com.xw.xmusic.musicservicecommand"
        const val TOGGLEPAUSE_ACTION = "com.xw.xmusic.togglepause"
        const val PAUSE_ACTION = "com.xw.xmusic.pause"
        const val STOP_ACTION = "com.xw.xmusic.stop"
        const val PREVIOUS_ACTION = "com.xw.xmusic.previous"
        const val PREVIOUS_FORCE_ACTION = "com.xw.xmusic.previous.force"
        const val NEXT_ACTION = "com.xw.xmusic.next"
        const val MUSIC_CHANGED = "com.xw.xmusic.change_music"
        const val REPEAT_ACTION = "com.xw.xmusic.repeat"
        const val SHUFFLE_ACTION = "com.xw.xmusic.shuffle"
        const val FROM_MEDIA_BUTTON = "frommediabutton"
        const val REFRESH = "com.xw.xmusic.refresh"
        const val LRC_UPDATED = "com.xw.xmusic.updatelrc"
        const val UPDATE_LOCKSCREEN = "com.xw.xmusic.updatelockscreen"
        const val CMDNAME = "command"
        const val CMDTOGGLEPAUSE = "togglepause"
        const val CMDSTOP = "stop"
        const val CMDPAUSE = "pause"
        const val CMDPLAY = "play"
        const val CMDPREVIOUS = "previous"
        const val CMDNEXT = "next"
        const val CMDNOTIF = "buttonId"
        const val TRACK_PREPARED = "com.xw.xmusic.prepared"
        const val BUFFER_UP = "com.xw.xmusic.bufferup"
        const val LOCK_SCREEN = "com.xw.xmusic.lock"
        const val SEND_PROGRESS = "com.xw.xmusic.progress"
        const val MUSIC_LODING = "com.xw.xmusic.loading"
        const val SHUTDOWN = "com.xw.xmusic.shutdown"
        const val SETQUEUE = "com.xw.xmusic.setqueue"
        val TRACK_NAME = "trackname"


        const val NEXT = 2
        const val LAST = 3
        const val SHUFFLE_NONE = 0
        const val SHUFFLE_NORMAL = 1
        //        const val SHUFFLE_AUTO = 2
        const val REPEAT_NONE = 2
        const val REPEAT_CURRENT = 1
        const val REPEAT_ALL = 2
        const val MAX_HISTORY_SIZE = 1000

        private const val D = true
        private const val LRC_DOWNLOADED = -10
        private const val IDCOLIDX = 0
        private const val TRACK_ENDED = 1
        private const val TRACK_WENT_TO_NEXT = 2
        private const val RELEASE_WAKELOCK = 3
        private const val SERVER_DIED = 4
        private const val FOCUSCHANGE = 5
        private const val FADEDOWN = 6
        private const val FADEUP = 7
        private const val GET_HISTORY = 8
        private const val GET_QUEUE = 9
        private const val INSERT_RECENT = 10
        private const val IDLE_DELAY = 5 * 60 * 1000
        private const val REWIND_INSTEAD_PREVIOUS_THRESHOLD: Long = 3000

    }
}