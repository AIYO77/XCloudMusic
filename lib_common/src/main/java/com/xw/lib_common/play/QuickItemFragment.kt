package com.xw.lib_common.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.orhanobut.logger.Logger
import com.xw.lib_common.R
import com.xw.lib_common.base.view.fragment.StateListenerFragment
import com.xw.lib_common.ext.gone
import com.xw.lib_common.lrc.DefaultLrcParser
import com.xw.lib_common.lrc.LrcRow
import com.xw.lib_common.service.MusicPlayer
import com.xw.lib_common.utils.GlideUtils
import com.xw.lib_coremodel.model.SimpleTrackInfo
import com.xw.lib_coremodel.model.bean.LrcAdnTlyRic
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class QuickItemFragment : StateListenerFragment() {

    companion object {
        private const val INFO = "simple_track_info"

        fun newInstance(simpleTrackInfo: SimpleTrackInfo): QuickItemFragment {
            val bundle = Bundle().apply {
                putParcelable(INFO, simpleTrackInfo)
            }
            return QuickItemFragment().apply { arguments = bundle }
        }
    }

    private var lrcTxt: AppCompatTextView? = null

    private var songName: AppCompatTextView? = null
    private var songLogo: AppCompatImageView? = null

    private lateinit var simpleTrackInfo: SimpleTrackInfo
    val lrc: MutableLiveData<String?> = MutableLiveData()
    private var listLrc: List<LrcRow>? = null
    private var mCurRow = -1

    private val mUpdateProgress = Runnable {
        val position = MusicPlayer.position()
        val duration = MusicPlayer.duration()
        if (duration in 1..627080715) {
            listLrc?.let {
                for (i in it.indices.reversed()) {
                    if (position >= it[i].time) {
                        if (mCurRow != i) {
                            mCurRow = i
                            lrc.postValue(it[mCurRow].content)
                        }
                        break
                    }
                }
            }
        }
        updateProgress()
    }

    private fun updateProgress() {
        if (MusicPlayer.isPlaying() && isResumed) {
            lrcTxt?.removeCallbacks(mUpdateProgress)
            lrcTxt?.postDelayed(mUpdateProgress, 50)
        } else {
            lrc.value = simpleTrackInfo.ar
            lrcTxt?.removeCallbacks(mUpdateProgress)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quick_item, container, false)
        songName = view.findViewById(R.id.songName)
        songLogo = view.findViewById(R.id.songLogo)
        lrcTxt = view.findViewById(R.id.lyric)
        simpleTrackInfo = arguments?.getParcelable(INFO) ?: SimpleTrackInfo()
        initData()
        return view
    }

    override fun updateLrc(lrc: LrcAdnTlyRic) {
        super.updateLrc(lrc)
        listLrc = DefaultLrcParser.getIstance().getLrcRows(lrc.lrc)
        updateProgress()
    }

    override fun updateTrackInfo() {
        super.updateTrackInfo()
        updateProgress()
        if (context != null) {
            GlideUtils.loadImageCircleCrop(
                context!!,
                songLogo!!,
                simpleTrackInfo.logo,
                R.drawable.placeholder_disk_play_100
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (listLrc.isNullOrEmpty()) {
            MusicPlayer.updateLrc()
        } else {
            updateProgress()
        }
    }

    override fun updateTrack() {
        super.updateTrack()
        updateProgress()
    }

    private fun initData() {
        songName!!.text = simpleTrackInfo.name
        upLrcTxt(simpleTrackInfo.ar)
        lrc.observe(this, Observer {
            upLrcTxt(it)
        })
        GlideUtils.loadImageCircleCrop(
            requireContext(),
            songLogo!!,
            simpleTrackInfo.logo,
            R.drawable.placeholder_disk_play_100
        )
    }

    private fun upLrcTxt(lrc: String?) {
        lrc?.let {
            if (it.isNotEmpty())
                lrcTxt?.text = it.trim()
        }
    }
}