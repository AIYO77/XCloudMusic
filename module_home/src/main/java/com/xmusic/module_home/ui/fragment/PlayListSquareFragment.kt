package com.xmusic.module_home.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeremyliao.liveeventbus.LiveEventBus
import com.orhanobut.logger.Logger
import com.xw.lib_common.base.BaseApplication
import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_common.ext.adapterDataChangeObserver
import com.xw.lib_common.ext.dip2px
import com.xw.lib_common.utils.GridItemDecoration
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.utils.DataHolder
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.home.PlayListFragmentViewModel
import com.xmusic.module_home.R
import com.xmusic.module_home.adapter.PlayListSquareAdapter
import com.xmusic.module_home.databinding.FragmentPlayListBinding
import com.xmusic.module_home.ui.activity.PlayListActivity
import com.xmusic.module_home.ui.activity.PlayListSquareActivity
import kotlinx.android.synthetic.main.fragment_play_list.*
import java.util.concurrent.Executors

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListSquareFragment :
    BaseModelFragment<FragmentPlayListBinding, PlayListFragmentViewModel>() {

    @Suppress("PrivatePropertyName")
    private val NETWORK_IO = Executors.newFixedThreadPool(BaseApplication.CORE_POOL_SIZE)

    private lateinit var adapter: PlayListSquareAdapter

    override val viewModel: PlayListFragmentViewModel by viewModels {
        InjectorUtils.providePlayListFragmentViewModelFactory(this, NETWORK_IO)
    }

    override val layoutId: Int
        get() = R.layout.fragment_play_list

    override fun initView() {
        super.initView()
        initAdapter()
    }

    private fun initAdapter() {
        adapter = PlayListSquareAdapter {
            viewModel.retry()
        }
        playListRv.layoutManager =
            GridLayoutManager(requireContext(), 3)
        playListRv.adapter = adapter
        playListRv.addItemDecoration(GridItemDecoration(mHorizonSpan = 10f.dip2px()))
    }

    override fun initData() {
        super.initData()
        val playListCat: PlayListCat? = arguments?.getParcelable(CAT_DATA)
        if (playListCat == null) {
            requireActivity().finish()
            return
        }
        viewModel.showPlayList(playListCat)
    }

    override fun onResume() {
        super.onResume()
        toActLoadBg()
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.posts.observe(this, Observer {
            adapter.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })


//        viewModel.refreshState.observe(this, Observer {
        //            if (it == NetworkState.LOADING) {
//                showLoading()
//            } else {
//                cancelLoading()
//            }
//        })
    }

    private fun toActLoadBg() {
        val list = adapter.currentList
        if (list.isNullOrEmpty().not()) {
            val coverImgUrl = list!![0]?.coverImgUrl
            val activity = requireActivity()
            if (activity is PlayListSquareActivity) {
                activity.loadBg(coverImgUrl)
            }
        }
    }

    companion object {
        private const val CAT_DATA = "catData"

        fun newInstance(playListCat: PlayListCat): PlayListSquareFragment {
            val data = Bundle().apply {
                putParcelable(CAT_DATA, playListCat)
            }
            return PlayListSquareFragment().apply { arguments = data }
        }
    }
}