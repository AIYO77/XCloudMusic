package com.xmusic.module_home.ui.fragment

import android.icu.util.Calendar
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_common.utils.CustomBannerViewHolder
import com.xw.lib_coremodel.ext.onNetError
import com.xw.lib_coremodel.model.bean.home.Banner
import com.xw.lib_coremodel.model.bean.home.PlayListSimpleInfo
import com.xw.lib_coremodel.utils.InjectorUtils

import com.xmusic.module_home.R
import com.xmusic.module_home.databinding.HomeFragmentBinding
import com.xmusic.module_home.ui.activity.RankActivity
import com.xw.lib_coremodel.viewmodel.home.HomeViewModel
import com.xmusic.module_home.adapter.HomeDataAdapter
import com.xmusic.module_home.ui.activity.PlayListActivity
import com.ms.banner.listener.OnBannerClickListener
import com.xw.lib_common.ext.*
import com.xw.lib_coremodel.ext.afterLogin
import com.xmusic.module_home.ui.activity.PlayListSquareActivity
import com.xmusic.module_home.ui.activity.RecdDailyActivity
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : BaseModelFragment<HomeFragmentBinding, HomeViewModel>(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener, OnBannerClickListener {

    private var banners: List<Banner> = mutableListOf()

    private lateinit var homeDataAdapter: HomeDataAdapter

    override val layoutId: Int
        get() = R.layout.home_fragment

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun initView() {
        bindingView.includeHeader.banner.setAutoPlay(true)
            .setPages(banners, CustomBannerViewHolder())
            .setLoop(true)
            .setDelayTime(5000)
            .setIndicatorRes(R.drawable.banner_red, R.drawable.banner_grey)
            .setViewPagerIsScroll(true)
            .setOnBannerClickListener(this)
            .start()

        bindingView.moreDataRv.setLayoutManager(
            GridLayoutManager(
                context,
                3,
                GridLayoutManager.VERTICAL,
                false
            )
        )
        homeDataAdapter = HomeDataAdapter()
        bindingView.moreDataRv.setAdapter(homeDataAdapter)
        bindingView.includeHeader.layoutFm.setOnClickListener(this)
        bindingView.includeHeader.layoutRecommended.setOnClickListener(this)
        bindingView.includeHeader.layoutRank.setOnClickListener(this)
        bindingView.includeHeader.layoutPlayList.setOnClickListener(this)
        bindingView.includeHeader.layoutRadio.setOnClickListener(this)

        bindingView.refreshLayout.setOnRefreshListener(this)
        bindingView.refreshLayout.isRefreshing = true
        bindingView.refreshLayout.setColorSchemeColors(getColor(R.color.colorPrimary))

        val day: String
        day = if (fromN()) {
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
        } else {
            val simpleDateFormat = SimpleDateFormat("dd")
            val date = Date(System.currentTimeMillis())
            simpleDateFormat.format(date)
        }
        bindingView.includeHeader.txtDate.text = day
    }

    override fun initData() {
        loadData()
    }

    override fun loadData() {
        super.loadData()
        if (mIsPrepared) {
            bindingView.refreshLayout.isRefreshing = true
            viewModel.getBanners()
            viewModel.getHomePlayList()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            bindingView.includeHeader.layoutRecommended -> {
                afterLogin {
                    RecdDailyActivity.launch(requireActivity())
                }
            }
            bindingView.includeHeader.layoutPlayList -> {
                PlayListSquareActivity.launch(requireActivity())
            }
            bindingView.includeHeader.layoutRank -> {
                RankActivity.lunch(context)
            }
        }
    }

    override val viewModel: HomeViewModel by viewModels {
        InjectorUtils.provideHomeViewModelFactory(requireActivity())
    }

    override fun startObserve() {
        super.startObserve()

        viewModel.loginUser?.observe(this, Observer {
            if (isFirstInit.not())
                loadData()
        })

        viewModel.mBanners.observe(this, Observer {
            bindingView.refreshLayout.isRefreshing = false
            it?.let {
                banners = it
                bindingView.includeHeader.banner.update(banners)
            }
        })

        viewModel.mHomeMoreData.observe(this, Observer {
            it?.let {
                homeDataAdapter.submitList(it)
            }
        })
    }

    override fun onBannerClick(datas: MutableList<Any?>?, position: Int) {
        val banner = banners[position]
        when (banner.targetType) {
            1 -> { // 歌曲
                toast(context!!, banner.song!!.name)
            }
            3000 -> { //网页
                toast(context!!, banner.url!!)
            }
            10 -> {  //专辑
                toast(context!!, banner.targetId)
            }
            1000 -> {  //歌单
                PlayListActivity.lunch(
                    context!!,
                    PlayListSimpleInfo(banner.targetId, picUrl = "")
                )
            }
            1004 -> { // MV

            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindingView.includeHeader.banner.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        bindingView.includeHeader.banner.stopAutoPlay()
    }

    override fun onRefresh() {
        loadData()
    }


    override fun onError(e: Throwable) {
        bindingView.refreshLayout.isRefreshing = false
        activity?.onNetError(e) {
            bindingView.moreDataRv.netError()
        }
    }
}
