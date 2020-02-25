package com.xmusic.module_video.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.xmusic.module_video.R
import com.xmusic.module_video.databinding.FragmentVideoBinding
import com.xw.lib_common.base.BaseApplication
import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_common.ext.show
import com.xw.lib_coremodel.model.bean.video.VideoType
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.video.VideoViewModel
import kotlinx.android.synthetic.main.fragment_video.*
import java.util.concurrent.Executors

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class HomeVideoFragment : BaseModelFragment<FragmentVideoBinding, VideoViewModel>() {

    private val videoTypes = mutableListOf<VideoType>()
    private val NETWORK_IO = Executors.newFixedThreadPool(BaseApplication.CORE_POOL_SIZE)

    override val viewModel: VideoViewModel by viewModels {
        InjectorUtils.provideVideoModelFactory(requireContext(), NETWORK_IO)
    }

    override val layoutId: Int
        get() = R.layout.fragment_video

    private var mCurFragment: VideoListFragment? = null
    override fun initView() {
        super.initView()
        viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return videoTypes.size
            }

            override fun createFragment(position: Int): Fragment {
                return VideoListFragment.newInstance(videoTypes[position].id)
            }
        }
        videoTypeTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
            override fun onTabUnselected(tab: TabLayout.Tab) {
                mCurFragment =
                    ((viewpager.adapter as FragmentStateAdapter).createFragment(tab.position) as VideoListFragment)
                mCurFragment?.onTabUnselected()
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                mCurFragment =
                    ((viewpager.adapter as FragmentStateAdapter).createFragment(tab.position) as VideoListFragment)
            }
        })

    }

    override fun initData() {
        super.initData()
        if (arguments?.getBoolean(NEED_LOAD_DATA_ONCREATE) == true)
            loadData()
    }

    override fun loadData() {
        super.loadData()
        viewModel.getVideoType()
        TabLayoutMediator(videoTypeTab, viewpager) { tab, position ->
            tab.text = videoTypes[position].name
        }.attach()
    }

    override fun onInvisible() {
        super.onInvisible()
        mCurFragment?.onTabUnselected()
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.videoTypes.observe(this, Observer {
            videoTypes.clear()
            videoTypes.addAll(it)
            videoTypeTab.show()
            viewpager.adapter?.notifyDataSetChanged()
        })
    }

    fun onBackPressed(): Boolean {
        return GSYVideoManager.backFromWindowFull(requireActivity())
    }


    companion object {
        private const val NEED_LOAD_DATA_ONCREATE = "NEED_LOAD_DATA_ONCREATE"
        fun newInstance(onCreateLoadData: Boolean): HomeVideoFragment {
            return HomeVideoFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(NEED_LOAD_DATA_ONCREATE, onCreateLoadData)
                }
            }
        }
    }
}