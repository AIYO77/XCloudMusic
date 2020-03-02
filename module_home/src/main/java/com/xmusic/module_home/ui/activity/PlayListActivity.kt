package com.xmusic.module_home.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.xw.lib_common.base.view.activity.BaseActivity
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.home.PlayListDetailViewModel
import com.xmusic.module_home.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.orhanobut.logger.Logger
import com.xw.lib_common.ext.*
import com.xw.lib_common.utils.GlideApp
import com.xw.lib_common.utils.GlideUtils
import com.xw.lib_coremodel.ext.afterLogin
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xw.lib_coremodel.model.bean.home.PlayListData
import com.xw.lib_coremodel.model.bean.home.PlayListSimpleInfo
import com.xw.lib_coremodel.model.bean.home.Privilege
import com.xw.lib_coremodel.utils.ACache
import com.xw.lib_coremodel.utils.DataHolder
import com.xmusic.module_home.adapter.PlayListAdapter
import com.xmusic.module_home.databinding.ActivityPlayListBinding
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_play_list.*
import kotlinx.android.synthetic.main.include_play_list_header.*
import kotlin.math.abs


/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 歌单
 */
class PlayListActivity : BaseActivity<ActivityPlayListBinding, PlayListDetailViewModel>(),
    AppBarLayout.OnOffsetChangedListener {

    private var mAdapter: PlayListAdapter? = null
    private var songInfo: PlayListSimpleInfo? = null
    private var mPlaylist: PlayList? = null
    private var aCache: ACache? = null

    private var mPlayListId = ""
    private var mPlayName = ""

    override val layoutId: Int
        get() = R.layout.activity_play_list

    override val viewModel: PlayListDetailViewModel by viewModels {
        InjectorUtils.providePlayListViewModelFactory(this)
    }

    override fun initView() {
        songInfo = DataHolder.getInstance().getData(PLAYLIST_SIMPLE_INFO) as? PlayListSimpleInfo
//        songInfo = intent.getSerializableExtra(PLAYLIST_SIMPLE_INFO) as? PlayListSimpleInfo
        if (songInfo == null) {
            mPlaylist = DataHolder.getInstance().getData(PLAYLIST_INFO) as? PlayList
            if (mPlaylist == null) {
                onBackPressed()
                return
            }
        }
        mAdapter = PlayListAdapter()
        playListRcy.setLayoutManager(LinearLayoutManager(this))
        playListRcy.setAdapter(mAdapter!!)
        playListRcy.getRecycleView().smoothScrollToPosition(0)
        playListAppBar.addOnOffsetChangedListener(this)

        songInfo?.let {
            loadBg(it.picUrl)
            toolBar?.setToolBarSubTitle(it.copywriter)
            playListTitle?.text = it.name
            playCount?.text = it.playCount.formatting()
            mPlayListId = it.id
            mPlayName = it.name
        }
        mPlaylist?.let {
            loadBg(it.coverImgUrl)
            loadHeaderUI(it)
            toolBar?.setToolBarSubTitle(it.copywriter)
            playListTitle?.text = it.name
            playCount?.text = it.playCount.formatting()
            mPlayListId = it.id
            mPlayName = it.name
        }
        toolBar.setToolBarTitle(getString(R.string.title_playlist))

        bindingView.headerLayout.executePendingBindings()
        bindingView.includePlayAll.setOnPlayAllClick {
            mAdapter?.playAll()
        }
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.playList.observe(this, Observer<PlayListData> {
            cancelLoading()
            val playlist = it.playlist
            songInfo?.let {
                loadHeaderUI(playlist)
            }
            mAdapter?.apply {
                mSubscribed = playlist.subscribed
                mSubscribedCount = playlist.subscribedCount
                mSubscribers = playlist.subscribers
            }
            // 数量大于1000 tracks可能不全 trackIds是完整的
            if (playlist.tracks.size != playlist.trackIds.size) {
                val ids =
                    playlist.trackIds.map { trackId -> trackId.id }.reduce { acc, l -> "$acc,$l" }
                viewModel.getSongDetail(ids)
            } else {
                notifyAdapter(playlist.tracks, it.privileges)
            }
            isFirst = false
        })

        viewModel.songDetails.observe(this, Observer {
            notifyAdapter(it.songs, it.privileges)
        })

        viewModel.loginUser?.observe(this, Observer {
            if (isFirst.not()) {
                showLoading()
                val curTime = getCurTime()
                getPlayList(curTime)
                saveTime(curTime)
            }
        })

        viewModel.collectResult.observe(this, Observer {
            cancelLoading()
            mAdapter?.apply {
                if (it.result) {
                    mSubscribed = it.msg == "1"
                    if (mSubscribed) {
                        mSubscribedCount++
                    } else {
                        mSubscribedCount--
                    }
                    notifyCollectView(mSubscribedCount)
                    saveTime(getCurTime())
                } else {
                    toast(it.msg)
                }
            }
        })
    }

    private fun loadHeaderUI(playlist: PlayList) {
        bindingView.headerLayout.playList = playlist
        bindingView.headerLayout.executePendingBindings()
    }

    private fun notifyAdapter(tracks: MutableList<Song>, privileges: List<Privilege>) {
        notifyCollectView(mAdapter?.mSubscribedCount ?: 0)
        mAdapter?.setSongList(tracks, privileges)
        bindingView.includePlayAll.trackSize = tracks.size
    }

    private fun notifyCollectView(subCount: Long) {
        includePlayAll.show()
        bindingView.includePlayAll.apply {
            moreChoices.setSubscribedCount(mAdapter?.mSubscribed?:false, subCount,
                View.OnClickListener {
                    onCollect(1)
                }, View.OnClickListener {
                    onCollect(2)
                })
            executePendingBindings()
        }
    }

    private var isFirst = true
    override fun initData() {
        if (aCache == null) {
            aCache = ACache.get(this)
        }
        val time = aCache!!.getAsString(mPlayListId) ?: ""
        getPlayList(time)
    }

    private fun getPlayList(time: String = "") {
        viewModel.getPlayList(mPlayListId, time)
    }

    private fun saveTime(time: String) {
        aCache?.remove(mPlayListId)
        aCache?.put(mPlayListId, time, 2 * ACache.TIME_MINUTE)
    }

    private fun onCollect(t: Int) {
        afterLogin {
            showLoading()
            viewModel.subscribePlayList(mPlayListId, t)
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val toolbar = bindingView.toolBar.getToolbarBgView()
        val header = bindingView.headerLayout.root
        var mVerticalOffset = verticalOffset
        if (mVerticalOffset == 0) {
            // 完全展开
            // toolbar背景全透明
            toolbar.alpha = 0f
            // 头部布局全显示
            header.alpha = 1f
            bindingView.matchGaussianBg.translationY = 0f
        } else {
            // abs 运算
            mVerticalOffset = abs(verticalOffset)
            val totalScrollRange = appBarLayout.totalScrollRange
            if (mVerticalOffset >= totalScrollRange) {
                // 关闭状态
                //显示背景
                toolbar.alpha = 1f
                //头部布局全透明
                header.alpha = 0f
                bindingView.matchGaussianBg.translationY = verticalOffset.toFloat()

            } else {
                //中间状态
                val progress = 1 - mVerticalOffset / totalScrollRange.toFloat()
                toolbar.alpha = 1 - progress
                header.alpha = progress
                bindingView.matchGaussianBg.translationY = verticalOffset.toFloat()
                if (progress > 0.5) {
                    bindingView.toolBar.setToolBarTitle(getString(R.string.title_playlist))
                } else {
                    bindingView.toolBar.setToolBarTitle(mPlayName)
                }
            }
        }
    }

    private fun loadBg(url: String) {
        GlideUtils.loadImageRadius(bindingView.headerLayout.playListLogo, url)

        GlideApp.with(this)
            .load(url)
            .error(R.drawable.stackblur_default)
            .placeholder(R.drawable.stackblur_default)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .transform(BlurTransformation(80, 8))
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    bindingView.matchGaussianBg.setImageDrawable(resource)
                    bindingView.toolBar.loadBg(resource)
                }
            })
    }

    override fun updateTrack() {
        super.updateTrack()
        mAdapter?.notifyDataSetChanged()
    }

    override fun showNetError() {
        super.showNetError()
        playListRcy.netError()
    }

    companion object {
        private const val PLAYLIST_SIMPLE_INFO = "PlayListSimpleInfo"
        const val PLAYLIST_INFO = "playlistInfo"

        fun lunch(context: Context, simpleInfo: PlayListSimpleInfo) =
            context.apply {
                val intent = Intent(this, PlayListActivity::class.java)
//                intent.putExtra(PLAYLIST_SIMPLE_INFO, simpleInfo)
                DataHolder.getInstance().remove(PLAYLIST_SIMPLE_INFO)
                DataHolder.getInstance().remove(PLAYLIST_INFO)
                DataHolder.getInstance().setData(PLAYLIST_SIMPLE_INFO, simpleInfo)
                startActivity(intent)
            }

        fun lunch(context: Context, playlist: PlayList) = context.apply {
            val intent = Intent(this, PlayListActivity::class.java)
            DataHolder.getInstance().remove(PLAYLIST_SIMPLE_INFO)
            DataHolder.getInstance().remove(PLAYLIST_INFO)
            DataHolder.getInstance().setData(PLAYLIST_INFO, playlist)
            startActivity(intent)
        }
    }

}
