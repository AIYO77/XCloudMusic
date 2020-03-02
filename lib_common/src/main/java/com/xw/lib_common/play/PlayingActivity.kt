package com.xw.lib_common.play

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.orhanobut.logger.Logger
import com.xw.lib_common.R
import com.xw.lib_common.base.view.activity.BaseActivity
import com.xw.lib_common.databinding.ActivityPlayingBinding
import com.xw.lib_common.ext.*
import com.xw.lib_common.lrc.DefaultLrcParser
import com.xw.lib_common.service.MediaService
import com.xw.lib_common.service.MusicPlayer
import com.xw.lib_common.utils.GlideUtils
import com.xw.lib_common.view.AlbumViewPager
import com.xw.lib_coremodel.ext.afterLogin
import com.xw.lib_coremodel.ext.isLogined
import com.xw.lib_coremodel.model.bean.LrcAdnTlyRic
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.PlayingViewModel
import kotlinx.android.synthetic.main.activity_playing.*
import kotlinx.coroutines.*
import java.lang.Runnable
import java.lang.ref.WeakReference

class PlayingActivity : BaseActivity<ActivityPlayingBinding, PlayingViewModel>() {

    private lateinit var mAdapter: FragmentAdapter
    private var mNeedleAnim: ObjectAnimator? = null
    private var mRotateAnim: ObjectAnimator? = null
    private var mAnimatorSet: AnimatorSet? = null
    private var bgLaunch: Job? = null
    private var queueDialogFragment: PlayQueueDialogFragment? = null
    private var isNextOrPreSetPage = false //判断viewpager由手动滑动 还是setcruuentitem换页
    private var isFirstScroll = true //判断viewpager第一次滑动
    private var mViewWeakReference: WeakReference<View>? = null
    private var mActiveView: View? = null

    private var isFav = false // 是否已喜欢该歌曲

    private val changeSong = MutableLiveData<Int>()

    companion object {
        private const val PROGRESS_MAX = 1000
        private const val TIME_DELAY = 500L
        private const val NEXT_MUSIC = 0
        private const val PRE_MUSIC = 1

        fun launch(context: Context?) = context?.apply {
            startActivity(Intent(this, PlayingActivity::class.java))
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_playing

    override val viewModel: PlayingViewModel? by viewModels {
        InjectorUtils.providePlayingViewModelFatory(this)
    }

    override fun isNeedBottomPlay(): Boolean {
        return false
    }

    override fun initView() {
        mNeedleAnim = ObjectAnimator.ofFloat(needle, "rotation", -25f, 0f).apply {
            duration = 200
            repeatCount = 0
            interpolator = LinearInterpolator()
        }

        play_seek.isIndeterminate = false
        play_seek.max = PROGRESS_MAX
        play_seek.progress = 1

        loadOther()
        setViewPager()
        initLrcView()
    }


    override fun initData() {
//        loadBg()
        updatePlayMode(false)
//        isLogined().yes {
//            viewModel?.getLikeList()
//        }.no {
//            Logger.d("未登录")
//        }
    }

    private var mLikeList = mutableListOf<Long>()

    override fun startObserve() {
        super.startObserve()
        changeSong.observe(this, Observer {
            when (it) {
                NEXT_MUSIC -> {
                    MusicPlayer.next()
                }
                PRE_MUSIC -> {
                    MusicPlayer.pre(this, true)
                }
            }
        })

        viewModel?.likeList?.observe(this, Observer {
            mLikeList = it.toMutableList()
            updateFav()
        })

        viewModel?.likeResult?.observe(this, Observer {
            it.yes {
                toast(isFav.yes { "取消收藏音乐成功" }.no { "收藏音乐成功" })
                mLikeList.add(MusicPlayer.getCurrentAudioId())
                isFav = isFav.not()
            }.no {
                Logger.e("收藏音乐失败")
            }
        })

        viewModel?.loginUser?.observe(this, Observer {
            if (it == null) {
                viewModel?.likeList?.postValue(longArrayOf())
            } else {
                viewModel?.getLikeList()
            }
        })
    }

    private fun initLrcView() {
        lrcView.setOnSeekToListener { progress -> MusicPlayer.seek(position = progress.toLong()) }
        lrcView.setOnLrcClickListener {
            if (lrcviewContainer.isShow()) {
                lrcviewContainer.invisible()
                headerView.show()
                music_tool.show()
            }
        }
        view_pager.setOnSingleTouchListener(object : AlbumViewPager.OnSingleTouchListener {
            override fun onSingleTouch(v: View) {
                if (headerView.isShow()) {
                    headerView.invisible()
                    lrcviewContainer.show()
                    music_tool.invisible()
                }
            }
        })

        lrcviewContainer.setOnClickListener {
            if (lrcviewContainer.isShow()) {
                lrcviewContainer.invisible()
                headerView.show()
                music_tool.show()
            }
        }

        targetLrc.setOnClickListener {
            toast("正在重新获取歌词")
            MusicPlayer.updateLrc()
        }

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val v = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val mMaxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        volume_seek.max = mMaxVol
        volume_seek.progress = v
        volume_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    progress,
                    AudioManager.ADJUST_SAME
                )
            }
        })
    }

    private fun setViewPager() {
        view_pager.offscreenPageLimit = 2
        mAdapter = FragmentAdapter(supportFragmentManager)
        view_pager.adapter = mAdapter
        view_pager.setPageTransformer(true, PlaybarPagerTransformer())
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) = Unit

            override fun onPageSelected(position: Int) {
                if (position < 1) {
                    MusicPlayer.setQueuePosition(MusicPlayer.getQueue().size)
                    view_pager.setCurrentItem(MusicPlayer.getQueue().size, false)
                    isNextOrPreSetPage = false
                    return
                } else if (position > MusicPlayer.getQueue().size) {
                    MusicPlayer.setQueuePosition(0)
                    view_pager.setCurrentItem(1, false)
                    isNextOrPreSetPage = false
                    return
                } else {
                    if (isNextOrPreSetPage.not() && isFirstScroll.not()) {
                        Logger.i("position = $position  MusicPlayer.getQueuePosition() = ${MusicPlayer.getQueuePosition()}")
                        if (position < MusicPlayer.getQueuePosition() + 1) {
                            launch {
                                delay(TIME_DELAY)
                                changeSong.postValue(PRE_MUSIC)
                            }
                        } else if (position > MusicPlayer.getQueuePosition() + 1) {
                            launch {
                                delay(TIME_DELAY)
                                changeSong.postValue(NEXT_MUSIC)
                            }
                        }
                    }
                }
                isNextOrPreSetPage = false
                isFirstScroll = false
            }
        })
    }

    private fun loadOther() {
        setSeekBarListener()
        setTools()
    }

    private fun setTools() {
        playing_mode.setOnClickListener {
            MusicPlayer.cycleRepeat()
        }
        playing_pre.setOnClickListener {
            changeSong.value = PRE_MUSIC
        }
        playing_play.setOnClickListener {
            if (MusicPlayer.isPlaying()) {
                playing_play.setImageResource(R.drawable.play_rdi_btn_pause)
            } else {
                playing_play.setImageResource(R.drawable.play_rdi_btn_play)
            }
            if (MusicPlayer.getQueueSize() != 0) {
                MusicPlayer.playOrPause()
            }
        }
        playing_next.setOnClickListener {
            if (mRotateAnim != null) {
                mRotateAnim!!.end()
                mRotateAnim = null
            }
            changeSong.value = NEXT_MUSIC
        }
        playing_playlist.setOnClickListener {
            queueDialogFragment = PlayQueueDialogFragment.haveSong(
                this::class.java.simpleName,
                supportFragmentManager
            )
        }
        playing_more.setOnClickListener {

        }
        playing_fav.setOnClickListener {
            afterLogin {
                isFav.yes {
                    playing_fav.setImageResource(R.drawable.icon_play_love)
                }.no {
                    playing_fav.setImageResource(R.drawable.icon_play_loved)
                }
                viewModel?.likeMusic(MusicPlayer.getCurrentAudioId().toString(), isFav.not())
            }
        }
    }

    private fun updatePlayMode(showToast: Boolean = true) {
        var msg = ""
        if (MusicPlayer.getShuffleMode() == MediaService.SHUFFLE_NORMAL) {
            playing_mode.setImageResource(R.drawable.icon_play_shuffle)
            msg = getString(R.string.label_random_play)
        } else {
            when (MusicPlayer.getRepeatMode()) {
                MediaService.REPEAT_ALL -> {
                    playing_mode.setImageResource(R.drawable.icon_play_loop_prs)
                    msg = getString(R.string.label_loop_play)
                }
                MediaService.REPEAT_CURRENT -> {
                    playing_mode.setImageResource(R.drawable.icon_play_one)
                    msg = getString(R.string.label_play_one)
                }
            }
        }
        if (showToast && msg.isNotEmpty())
            toast(msg)
    }

    private fun setSeekBarListener() {
        play_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val i = (progress * MusicPlayer.duration() / 1000).toInt()
                lrcView.seekTo(i, true, fromUser)
                if (fromUser) {
                    MusicPlayer.seek(i.toLong())
                    music_duration_played.text = progress.makeTimeString()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
    }

    override fun onStart() {
        super.onStart()
        view_pager?.setCurrentItem(MusicPlayer.getQueuePosition() + 1, false)
    }

    override fun onResume() {
        super.onResume()
        MusicPlayer.isTrackLocal().yes {
            updateBuffer(100)
        }.no {
            updateBuffer(MusicPlayer.secondPosition())
        }
    }

    override fun updateTrackInfo() {
        if (MusicPlayer.getQueueSize() <= 0) {
            return
        }
        val fragment = view_pager.adapter?.instantiateItem(view_pager, view_pager.currentItem)
        if (fragment != null && fragment is RoundFragment) {
            val view = fragment.view
            if (mViewWeakReference?.get() != view && view != null) {
                (view as ViewGroup).isAnimationCacheEnabled = false
                mViewWeakReference?.clear()
                mViewWeakReference = WeakReference(view)
                mActiveView = mViewWeakReference!!.get()
            }
        }
        mActiveView?.let {
            mRotateAnim = it.getTag(R.id.tag_animator) as ObjectAnimator
        }
        mAnimatorSet = AnimatorSet().apply { startDelay = 100 }
        MusicPlayer.isPlaying().yes {
            play_seek.removeCallbacks(mUpdateProgress)
            play_seek.postDelayed(mUpdateProgress, 200)
            playing_play.setImageResource(R.drawable.play_rdi_btn_pause)
            if (mAnimatorSet != null && mRotateAnim != null && mRotateAnim!!.isRunning.not()) {
                if (mNeedleAnim == null) {
                    mNeedleAnim = ObjectAnimator.ofFloat(needle, "rotation", -30f, 0f).apply {
                        duration = 200
                        repeatCount = 0
                        interpolator = LinearInterpolator()
                    }
                }
                mAnimatorSet!!.playTogether(mRotateAnim, mNeedleAnim)
                mAnimatorSet!!.start()
            }
        }.no {
            play_seek.removeCallbacks(mUpdateProgress)
            playing_play.setImageResource(R.drawable.play_rdi_btn_play)
            mNeedleAnim?.apply {
                reverse()
                end()
            }

            if (mRotateAnim != null && mRotateAnim!!.isRunning) {
                mRotateAnim!!.cancel()
                val valueAvatar = mRotateAnim!!.animatedValue as Float
                mRotateAnim!!.setFloatValues(valueAvatar, 360f + valueAvatar)
            }
        }
        isNextOrPreSetPage = false
        if (MusicPlayer.getQueuePosition() + 1 != view_pager.currentItem) {
            view_pager.currentItem = MusicPlayer.getQueuePosition() + 1
            isNextOrPreSetPage = true
        }
    }

    private val mUpdateProgress = Runnable {
        play_seek?.let {
            val position = MusicPlayer.position()
            val duration = MusicPlayer.duration()
            if (duration in 1..627080715) {
                it.progress = (1000 * position / duration).toInt()
                music_duration_played.text = position.makeTimeString()
            }
            updateProgress()
        }
    }

    private fun updateProgress() {
        if (MusicPlayer.isPlaying()) {
            play_seek.postDelayed(mUpdateProgress, 200)
        } else {
            play_seek.removeCallbacks(mUpdateProgress)
        }
    }


    override fun updateQueue() {
        super.updateQueue()
        queueDialogFragment?.let {
            if (it.isCancelable.not())
                it.reload()
        }
        if (MusicPlayer.getQueueSize() <= 0) {
            MusicPlayer.stop()
            finish()
            return
        }
        mAdapter.setData(MusicPlayer.getAlbumPathAll())
        mAdapter.notifyDataSetChanged()
        view_pager.setCurrentItem(MusicPlayer.getQueuePosition() + 1, false)
    }

    override fun updateBuffer(p: Int) {
        play_seek.secondaryProgress = p * 10
    }

    override fun loading(l: Boolean) {
        play_seek.setLoading(l)
    }

    override fun updateTrack() {
        loadBg(1500)
        isFav = false
        updateFav()
        toolbar.setToolBarTitle(MusicPlayer.getTrackName())
        toolbar.setToolBarSubTitle(MusicPlayer.getArtistName())
        music_duration.text = (MusicPlayer.duration() / 1000).makeShortTimeString()
    }

    private fun updateFav() {
        val currentId = MusicPlayer.getCurrentAudioId()
        isFav = mLikeList.contains(currentId)
        isFav.yes {
            playing_fav.setImageResource(R.drawable.icon_play_loved)
        }.no {
            playing_fav.setImageResource(R.drawable.icon_play_love)
        }
    }

    override fun playModeChange() {
        updatePlayMode()
    }

    private fun loadBg(delay: Long = 0) {
        if (bgLaunch != null) {
            bgLaunch!!.cancel()
            bgLaunch = null
        }
        bgLaunch = launch {
            delay(delay)
            withContext(Dispatchers.Main) {
                GlideUtils.loadGaussian(albumBg, MusicPlayer.getAlbumPath(), 70, 450)
            }
        }
    }

    override fun updateLrc(lrc: LrcAdnTlyRic) {
        val lrcRows = DefaultLrcParser.getIstance().getLrcRows(lrc.lrc)
        val lyric = DefaultLrcParser.getIstance().getLrcRows(lrc.lyric)
        if (lyric.isNullOrEmpty().not()) {
            lrcRows.forEach { lrcRow ->
                for (it in lyric) {
                    if (lrcRow.timeStr == it.timeStr) {
                        lrcRow.translation = it.content
                        break
                    }
                }
            }
        }
        if (lrcRows.isNullOrEmpty().not()) {
            targetLrc.gone()
            lrcView.setLrcRows(lrcRows!!)
        } else {
            targetLrc.show()
            lrcView.reset()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        play_seek.removeCallbacks(mUpdateProgress)
        stopAnim()
    }

    override fun onDestroy() {
        super.onDestroy()

        play_seek.removeCallbacks(mUpdateProgress)
        stopAnim()
    }


    private fun stopAnim() {
        mActiveView = null
        if (mRotateAnim != null) {
            mRotateAnim?.cancel()
            mRotateAnim = null
        }
        if (mNeedleAnim != null) {
            mNeedleAnim?.cancel()
            mNeedleAnim = null
        }
        if (mAnimatorSet != null) {
            mAnimatorSet?.cancel()
            mAnimatorSet = null
        }
    }

    class FragmentAdapter(fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private var mChildCount = 0
        private val mData = arrayListOf<String>()

        override fun getItem(position: Int): Fragment {
            return if (position == mData.size + 1 || position == 0) {
                RoundFragment.newInstance("")
            } else RoundFragment.newInstance(mData[position - 1])
        }

        override fun getCount(): Int {
            return mData.size + 2
        }

        override fun notifyDataSetChanged() {
            mChildCount = count
            super.notifyDataSetChanged()
        }

        override fun getItemPosition(`object`: Any): Int {
            if (mChildCount > 0) {
                mChildCount--
                return POSITION_NONE
            }
            return super.getItemPosition(`object`)
        }

        fun setData(albumPathAll: Array<String>) {
            mData.clear()
            mData.addAll(albumPathAll)
        }

    }

    inner class PlaybarPagerTransformer : ViewPager.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            Logger.d("position = $position")
            if (position == 0f) {
                if (MusicPlayer.isPlaying()) {
                    mRotateAnim = view.getTag(R.id.tag_animator) as ObjectAnimator?
                    if (mRotateAnim != null && mRotateAnim!!.isRunning.not()
                        && mNeedleAnim != null && mNeedleAnim!!.isRunning.not()
                    ) {
                        mAnimatorSet = AnimatorSet().apply {
                            startDelay = 100
                        }
                        mAnimatorSet!!.playTogether(mRotateAnim, mNeedleAnim)
                        mAnimatorSet!!.start()
                    }
                }
            } else if (position == -1f || position == -2f || position == 1f) {

                mRotateAnim = view.getTag(R.id.tag_animator) as ObjectAnimator
                if (mRotateAnim != null) {
                    mRotateAnim!!.setFloatValues(0f)
                    mRotateAnim!!.end()
                    mRotateAnim = null
                }
            } else {
                mNeedleAnim?.apply {
                    reverse()
                    end()
                }
                mRotateAnim = view.getTag(R.id.tag_animator) as ObjectAnimator
                mRotateAnim?.apply {
                    cancel()
                    val fl = animatedValue as Float
                    setFloatValues(fl, 360f + fl)
                }
            }
        }

    }
}
