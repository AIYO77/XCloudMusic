package com.xw.lib_common.receiver

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.view.KeyEvent
import androidx.legacy.content.WakefulBroadcastReceiver
import com.xw.lib_common.service.MediaService

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class MediaButtonIntentReceiver : WakefulBroadcastReceiver() {

    companion object {
        private const val MSG_LONGPRESS_TIMEOUT = 1
        private const val MSG_HEADSET_DOUBLE_CLICK_TIMEOUT = 2
        private const val LONG_PRESS_DELAY = 1000
        private const val DOUBLE_CLICK = 800

        fun mCompleteWakefulIntent(intent: Intent?) {
            completeWakefulIntent(intent)
        }
    }

    private var mWakeLock: PowerManager.WakeLock? = null
    private var mClickCounter = 0
    private var mLastClickTime: Long = 0
    private var mDown = false
    private var mLaunched = false

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        /**
         * {@inheritDoc}
         */
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_LONGPRESS_TIMEOUT -> {
                    if (!mLaunched) {
                        val context = msg.obj as Context
                        val i = Intent()
                        i.component = ComponentName("com.xw.xmusic", "com.xw.xmusicy.MainActivity")
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(i)
                        mLaunched = true
                    }
                }

                MSG_HEADSET_DOUBLE_CLICK_TIMEOUT -> {
                    val command: String? = when (msg.arg1) {
                        1 -> MediaService.CMDTOGGLEPAUSE
                        2 -> MediaService.CMDNEXT
                        3 -> MediaService.CMDPREVIOUS
                        else -> null
                    }

                    if (command != null) {
                        val context = msg.obj as Context
                        startService(context, command)
                    }
                }
            }
            releaseWakeLockIfHandlerIdle()
        }
    }

    private fun releaseWakeLockIfHandlerIdle() {
        if (mHandler.hasMessages(MSG_LONGPRESS_TIMEOUT) || mHandler.hasMessages(
                MSG_HEADSET_DOUBLE_CLICK_TIMEOUT
            )
        ) {
            return
        }
        if (mWakeLock != null) {
            mWakeLock!!.release()
            mWakeLock = null
        }
    }

    private fun startService(context: Context?, command: String) {
        val i = Intent(context, MediaService::class.java)
        i.action = MediaService.SERVICECMD
        i.putExtra(MediaService.CMDNAME, command)
        i.putExtra(MediaService.FROM_MEDIA_BUTTON, true)
        startWakefulService(context, i)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val intentAction = intent?.action
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intentAction) {
            startService(context, MediaService.CMDPAUSE)
        } else if (Intent.ACTION_MEDIA_BUTTON == intentAction) {
            val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return
            val keycode = event.keyCode
            val action = event.action
            val eventtime = event.eventTime

            val command: String? = when (keycode) {
                KeyEvent.KEYCODE_MEDIA_STOP -> MediaService.CMDSTOP
                KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> MediaService.CMDTOGGLEPAUSE
                KeyEvent.KEYCODE_MEDIA_NEXT -> MediaService.CMDNEXT
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> MediaService.CMDPREVIOUS
                KeyEvent.KEYCODE_MEDIA_PAUSE -> MediaService.CMDPAUSE
                KeyEvent.KEYCODE_MEDIA_PLAY -> MediaService.CMDPLAY
                else -> null
            }
            command?.let {
                if (action == KeyEvent.ACTION_DOWN) {
                    if (mDown) {
                        if (MediaService.CMDTOGGLEPAUSE == it || MediaService.CMDPLAY == it
                        ) {
                            if (mLastClickTime != 0L && eventtime - mLastClickTime > LONG_PRESS_DELAY) {
                                acquireWakeLockAndSendMessage(
                                    context,
                                    mHandler.obtainMessage(MSG_LONGPRESS_TIMEOUT, context),
                                    0
                                )
                            }
                        }
                    } else if (event.repeatCount == 0) {
                        if (keycode == KeyEvent.KEYCODE_HEADSETHOOK) {
                            if (eventtime - mLastClickTime >= DOUBLE_CLICK) {
                                mClickCounter = 0
                            }

                            mClickCounter++
                            mHandler.removeMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)

                            val msg = mHandler.obtainMessage(
                                MSG_HEADSET_DOUBLE_CLICK_TIMEOUT, mClickCounter, 0, context
                            )

                            val delay = (if (mClickCounter < 3) DOUBLE_CLICK else 0).toLong()
                            if (mClickCounter >= 3) {
                                mClickCounter = 0
                            }
                            mLastClickTime = eventtime
                            acquireWakeLockAndSendMessage(context, msg, delay)
                        } else {
                            startService(context, it)
                        }
                        mLaunched = false
                        mDown = true
                    }

                } else {
                    mHandler.removeMessages(MSG_LONGPRESS_TIMEOUT)
                    mDown = false
                }
                if (isOrderedBroadcast) {
                    abortBroadcast()
                }
                releaseWakeLockIfHandlerIdle()
            }
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun acquireWakeLockAndSendMessage(context: Context?, msg: Message?, delay: Long) {
        if (mWakeLock == null && context != null) {
            val appContext = context.applicationContext
            val pm = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "XMusic headset button")
            mWakeLock?.setReferenceCounted(false)
        }
        // Make sure we don't indefinitely hold the wake lock under any circumstances
        mWakeLock?.acquire(10000)
        mHandler.sendMessageDelayed(msg, delay)
    }
}