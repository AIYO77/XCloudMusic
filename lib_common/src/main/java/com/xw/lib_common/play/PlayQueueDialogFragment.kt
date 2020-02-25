package com.xw.lib_common.play

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xw.lib_common.R
import com.xw.lib_common.adapter.PlayQueueAdapter
import com.xw.lib_common.ext.getDrawable
import com.xw.lib_common.service.MediaService
import com.xw.lib_common.service.MusicPlaybackState
import com.xw.lib_common.service.MusicPlayer
import com.xw.lib_coremodel.ext.afterLogin
import com.xw.lib_coremodel.model.bean.info.MusicInfo
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import java.io.File

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayQueueDialogFragment private constructor() : BottomSheetDialogFragment() {

    private lateinit var playQueueViewModel: PlayQueueViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var playQueueAdapter: PlayQueueAdapter
    private lateinit var modeTextView: AppCompatTextView

    companion object {
        fun haveSong(tag: String, fragmentManager: FragmentManager): PlayQueueDialogFragment? {
            if (MusicPlayer.getQueueSize() > 0) {
                return show(tag, fragmentManager)
            }
            return null
        }

        private fun show(tag: String, fragmentManager: FragmentManager): PlayQueueDialogFragment {
            val oldFragment = fragmentManager.findFragmentByTag(tag)
            if (oldFragment != null) {
                fragmentManager.beginTransaction().remove(oldFragment).commitNowAllowingStateLoss()
            }
            return PlayQueueDialogFragment().apply {
                show(fragmentManager, tag)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context, R.layout.fragment_play_queue, null)
        dialog.setContentView(view)
        dialog.window?.findViewById<View>(R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        val layoutParams = view.layoutParams
        val height = (resources.displayMetrics.heightPixels * 0.6).toInt()
        layoutParams.height = height
        view.layoutParams = layoutParams

        initView(view)
        initData()
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playQueueViewModel = ViewModelProviders.of(this).get(PlayQueueViewModel::class.java)
        playQueueAdapter = PlayQueueAdapter()
    }

    private fun initData() {
        playQueueViewModel.playListData.observe(this, Observer<List<MusicInfo>> {
            playQueueAdapter.submitList(it)
            updateNum(it.size)
        })
        playQueueViewModel.getPlayQueue()
    }

    fun reload() {
        playQueueViewModel.getPlayQueue()
    }

    private fun updateNum(size: Int) {
        if (size <= 0) {
            dismiss()
            return
        }
        var msg = ""
        var drawable: Drawable? = null
        if (MusicPlayer.getShuffleMode() == MediaService.SHUFFLE_NORMAL) {
            drawable = getDrawable(R.drawable.icon_play_shuffle_gray)
            msg = getString(R.string.label_random_play_s, size)
        } else {
            when (MusicPlayer.getRepeatMode()) {
                MediaService.REPEAT_ALL -> {
                    drawable = getDrawable(R.drawable.icon_play_loop_prs_gray)
                    msg = getString(R.string.label_loop_play_s, size)
                }
                MediaService.REPEAT_CURRENT -> {
                    drawable = getDrawable(R.drawable.icon_play_one_gray)
                    msg = getString(R.string.label_play_one_s, size)
                }
            }
        }
        modeTextView.text = msg
        modeTextView.setCompoundDrawables(drawable, null, null, null)
    }

    private fun initView(root: View) {
        recyclerView = root.findViewById(R.id.playListRv)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = playQueueAdapter

        modeTextView = root.findViewById(R.id.changeMode)
        modeTextView.setOnClickListener {
            MusicPlayer.cycleRepeat()
        }

        root.findViewById<TextView>(R.id.collectAll).setOnClickListener {
            afterLogin {

            }
        }
        root.findViewById<ImageButton>(R.id.deleteAll).setOnClickListener {
            MaterialDialog(requireActivity()).show {
                message(R.string.label_confirm_clear_play_queue)
                positiveButton(R.string.label_clear) {
                    clearQueue()
                }
                negativeButton(R.string.label_cancel)
                lifecycleOwner(this@PlayQueueDialogFragment)
            }
        }
    }

    private fun clearQueue() {
        MusicPlayer.clearQueue()
        MusicPlayer.stop()
        val file = File(context?.filesDir?.absolutePath + "playlist")
        if (file.exists()) {
            file.delete()
        }
        MusicPlaybackState.clearQueue()
        dismiss()
    }

    class PlayQueueViewModel : BaseViewModel() {
        val playListData = MutableLiveData<List<MusicInfo>>()
        private val list = arrayListOf<MusicInfo>()
        fun getPlayQueue() {
            launch {
                val playList = MusicPlayer.getPlayList()
                playList?.let { pl ->
                    val queue = MusicPlayer.getQueue()
                    queue.forEach { long ->
                        val musicInfo = pl[long]
                        musicInfo?.let {
                            list.add(it)
                        }
                    }
                }
                playListData.postValue(list)
            }
        }
    }
}