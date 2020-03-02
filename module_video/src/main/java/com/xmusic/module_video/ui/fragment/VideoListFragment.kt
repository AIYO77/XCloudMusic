package com.xmusic.module_video.ui.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.orhanobut.logger.Logger
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.xmusic.module_video.R
import com.xmusic.module_video.databinding.FragmentListVideoBinding
import com.xmusic.module_video.ui.adapter.VideoListAdapter
import com.xw.lib_common.base.BaseApplication
import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_common.ext.onScrolled
import com.xw.lib_coremodel.model.bean.NetworkState
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.video.VideoListViewModel
import kotlinx.android.synthetic.main.fragment_list_video.*
import java.util.concurrent.Executors

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class VideoListFragment : BaseModelFragment<FragmentListVideoBinding, VideoListViewModel>() {

    private var videoListAdapter: VideoListAdapter? = null
    private val NETWORK_IO = Executors.newFixedThreadPool(BaseApplication.CORE_POOL_SIZE)

    private var mId: String? = null

    private var mNeedPause = true

    override val viewModel: VideoListViewModel by viewModels {
        InjectorUtils.provideVideoModelFactory(requireContext(), NETWORK_IO)
    }

    override val layoutId: Int get() = R.layout.fragment_list_video

    override fun initView() {
        super.initView()
        videoListAdapter = VideoListAdapter({ viewModel.retry() }, { data, videoView ->
            mNeedPause = false
            videoView.toDetailAct(data)
        })
        videoListRv.adapter = videoListAdapter
        val itemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                setDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.video_list_item_decoration
                    )!!
                )
            }
        videoListRv.addItemDecoration(itemDecoration)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        videoListRv.layoutManager = linearLayoutManager
        videoListRv.onScrolled { _, _, _ ->
            val firstVisibleItem: Int = linearLayoutManager.findFirstVisibleItemPosition()
            val lastVisibleItem: Int = linearLayoutManager.findLastVisibleItemPosition()
            //大于0说明有播放
            if (GSYVideoManager.instance().playPosition >= 0) {
                //当前播放的位置
                val position = GSYVideoManager.instance().playPosition
                //对应的播放列表TAG
                if (GSYVideoManager.instance().playTag == videoListAdapter?.currentList?.get(
                        position
                    )?.data?.urlInfo?.url
                    && (position < firstVisibleItem || position > lastVisibleItem)
                ) {
                    GSYVideoManager.releaseAllVideos()
                    videoListAdapter?.notifyDataSetChanged()
                }
            }
        }
        swipeRefresh.setOnRefreshListener {
            GSYVideoManager.releaseAllVideos()
            videoListAdapter?.notifyDataSetChanged()
            viewModel.refresh()
        }
    }

    override fun initData() {
        mId = arguments?.getString(ID_VIDEO)
        mId?.apply {
            viewModel.showVideoGroup(this)
        }
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.refreshState.observe(this, Observer {
            swipeRefresh.isRefreshing = it == NetworkState.LOADING
        })
        viewModel.posts.observe(this, Observer {
            videoListAdapter?.submitList(it)
        })
        viewModel.networkState.observe(this, Observer {
            swipeRefresh.isRefreshing = false
            videoListAdapter?.setNetworkState(it)
        })
        viewModel.loginUser?.observe(this, Observer {
            initData()
        })
    }

    fun onTabUnselected() {
        if (GSYVideoManager.instance().playPosition >= 0) {
            GSYVideoManager.releaseAllVideos()
            videoListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        val playPosition = GSYVideoManager.instance().playPosition
        if (mNeedPause.not()) {
            videoListAdapter?.currentList?.get(playPosition)?.change()
            GSYVideoManager.releaseAllVideos()
            videoListAdapter?.notifyDataSetChanged()
        }
        if (playPosition >= 0 && mNeedPause) {
            GSYVideoManager.onResume(true)
        }
        mNeedPause = true
    }

    override fun onPause() {
        super.onPause()
        if (GSYVideoManager.instance().playPosition >= 0 && mNeedPause) {
            GSYVideoManager.onPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }

    companion object {
        private const val ID_VIDEO = "id_video"
        fun newInstance(id: Long): VideoListFragment {
            return VideoListFragment().apply {
                arguments = Bundle().apply {
                    putString(ID_VIDEO, id.toString())
                }
            }
        }
    }
}