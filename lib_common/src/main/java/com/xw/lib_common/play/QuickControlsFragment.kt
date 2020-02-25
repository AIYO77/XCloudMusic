package com.xw.lib_common.play

import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.orhanobut.logger.Logger
import com.xw.lib_common.R
import com.xw.lib_common.base.view.fragment.BaseFragment
import com.xw.lib_common.databinding.LayoutBottomNavBinding
import com.xw.lib_common.lrc.DefaultLrcParser
import com.xw.lib_common.lrc.LrcRow
import com.xw.lib_common.service.MusicPlayer
import com.xw.lib_common.view.AlbumViewPager
import com.xw.lib_coremodel.model.SimpleTrackInfo
import com.xw.lib_coremodel.model.bean.LrcAdnTlyRic
import kotlinx.android.synthetic.main.layout_bottom_nav.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 底部播放快捷栏
 */
class QuickControlsFragment : BaseFragment<LayoutBottomNavBinding>() {

    private var queueDialogFragment: PlayQueueDialogFragment? = null
    private var isNextOrPreSetPage = false //判断viewpager由手动滑动 还是setcruuentitem换页
    private val changeSong = MutableLiveData<Int>()
    private var mAdapter: FragmentAdapter? = null
    override val layoutId: Int
        get() = R.layout.layout_bottom_nav

    private val mUpdateProgress = Runnable {
        val position = MusicPlayer.position()
        val duration = MusicPlayer.duration()
        if (duration in 1..627080715) {
            val int = (1000 * position / duration).toInt()
            playProgressBar.progress = int
        }
        updateProgress()
    }

    private fun updateProgress() {
        if (MusicPlayer.isPlaying()) {
            playProgressBar?.isStop = false
            playProgressBar?.postDelayed(mUpdateProgress, 50)
        } else {
            playProgressBar?.isStop = true
            playProgressBar?.removeCallbacks(mUpdateProgress)
        }
    }

    override fun initView() {

        playListMenu.setOnClickListener {
            queueDialogFragment =
                PlayQueueDialogFragment.haveSong(this::class.java.simpleName, childFragmentManager)
        }
        playListVp.offscreenPageLimit = 2
        mAdapter = FragmentAdapter(childFragmentManager)
        playListVp.adapter = mAdapter

        playListVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) = Unit

            override fun onPageSelected(position: Int) {
                if (position < 1) {
                    MusicPlayer.setQueuePosition(MusicPlayer.getQueue().size)
                    playListVp.setCurrentItem(MusicPlayer.getQueue().size, false)
                    isNextOrPreSetPage = false
                    return
                } else if (position > MusicPlayer.getQueue().size) {
                    MusicPlayer.setQueuePosition(0)
                    playListVp.setCurrentItem(1, false)
                    isNextOrPreSetPage = false
                    return
                } else {
                    if (isNextOrPreSetPage.not()) {
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
            }
        })
        playListVp.setOnSingleTouchListener(object : AlbumViewPager.OnSingleTouchListener {
            override fun onSingleTouch(v: View) {
                PlayingActivity.launch(context)
            }
        })
        val queuePosition = MusicPlayer.getQueuePosition()
        mAdapter?.setData()
        playListVp.setCurrentItem(queuePosition + 1, false)

        playProgressBar.setOnClickListener {
            if (MusicPlayer.getQueueSize() <= 0) {
                return@setOnClickListener
            }
            if (MusicPlayer.isPlaying()) {
                playProgressBar.removeCallbacks(mUpdateProgress)
            } else {
                playProgressBar.postDelayed(mUpdateProgress, 0)
            }
            Handler().postDelayed({
                MusicPlayer.playOrPause()
            }, 60)
        }

        playProgressBar.postDelayed(mUpdateProgress, 0)

    }


    override fun initData() {
        changeSong.observe(this, Observer {
            when (it) {
                NEXT_MUSIC -> {
                    MusicPlayer.next()
                }
                PRE_MUSIC -> {
                    MusicPlayer.pre(context!!, true)
                }
            }
        })
    }

    override fun updateQueue() {
        if (queueDialogFragment != null && queueDialogFragment!!.isCancelable.not()) {
            queueDialogFragment!!.reload()
        }
        if (MusicPlayer.getQueueSize() == 0) {
            MusicPlayer.stop()
            return
        }
        mAdapter?.notifyDataSetChanged()
        playListVp.setCurrentItem(MusicPlayer.getQueuePosition() + 1, false)

    }

    override fun updateTrack() {
        playProgressBar?.progress = 0
    }

    override fun updateTrackInfo() {
        if (MusicPlayer.getQueueSize() <= 0) {
            return
        }

        isNextOrPreSetPage = false
        if (MusicPlayer.getQueuePosition() + 1 != playListVp.currentItem) {
            playListVp.setCurrentItem(MusicPlayer.getQueuePosition() + 1, false)
            isNextOrPreSetPage = true
        }
        updateState()
    }

    private fun updateState() {
        if (MusicPlayer.isPlaying()) {
            playProgressBar?.removeCallbacks(mUpdateProgress)
            playProgressBar?.postDelayed(mUpdateProgress, 50)
        } else {
            playProgressBar?.isStop = true
            playProgressBar?.removeCallbacks(mUpdateProgress)
        }
    }

//    override fun updateLrc(lrc: LrcAdnTlyRic) {
//        super.updateLrc(lrc)
//        listLrc = DefaultLrcParser.getIstance().getLrcRows(lrc.lrc)
//    }

    override fun onStop() {
        super.onStop()
        playProgressBar?.removeCallbacks(mUpdateProgress)
    }

    override fun onResume() {
        super.onResume()
        playProgressBar?.setMax(1000)
        playProgressBar?.removeCallbacks(mUpdateProgress)
        playProgressBar?.postDelayed(mUpdateProgress, 0)
    }

    companion object {
        private const val TIME_DELAY = 500L
        private const val NEXT_MUSIC = 0
        private const val PRE_MUSIC = 1
        fun newInstance() = QuickControlsFragment()
    }

    class FragmentAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private var mChildCount = 0

        private var mQueue = longArrayOf()
        private var mAlbumPathAll = arrayOf<String>()
        private var mTrackNameAll = arrayOf<String>()
        private var mTrackArist = arrayOf<String>()

        override fun getItem(position: Int): Fragment {
            return if (position >= mQueue.size + 1 || position <= 0) {
                QuickItemFragment.newInstance(SimpleTrackInfo())
            } else {
                if (mAlbumPathAll.isNotEmpty() && mTrackNameAll.isNotEmpty()) {
                    QuickItemFragment.newInstance(
                        SimpleTrackInfo(
                            id = mQueue[position - 1],
                            name = mTrackNameAll[position - 1],
                            logo = mAlbumPathAll[position - 1],
                            ar = mTrackArist[position - 1]
                        )
                    )
                } else {
                    QuickItemFragment.newInstance(SimpleTrackInfo())
                }
            }
        }

        override fun getCount(): Int {
            return mQueue.size + 2
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

        fun setData() {
            mQueue = MusicPlayer.getQueue()
            mAlbumPathAll = MusicPlayer.getAlbumPathAll()
            mTrackNameAll = MusicPlayer.getTrackNameAll()
            mTrackArist = MusicPlayer.getTrackAristNameAll()
            notifyDataSetChanged()
        }
    }

}