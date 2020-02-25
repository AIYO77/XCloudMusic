package com.xmusic.module_home.ui.activity

import android.content.Context
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.xw.lib_common.adapter.StickyHeaderAdapter
import com.xw.lib_common.base.view.activity.BaseActivity
import com.xw.lib_common.ext.dip2px
import com.xw.lib_common.ext.getMmAndDd
import com.xw.lib_common.ext.getSongAndPrivileges
import com.xw.lib_common.ext.show
import com.xw.lib_common.utils.GlideUtils
import com.xw.lib_common.view.StickyHeaderDecoration
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.home.Privilege
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.home.RecdDailyViewModel
import com.xmusic.module_home.R
import com.xmusic.module_home.adapter.PlayListAdapter
import com.xmusic.module_home.databinding.ActivityRecdDailyBinding
import kotlinx.android.synthetic.main.activity_recd_daily.*
import kotlin.math.abs

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 每日推荐 需要登录
 */
class RecdDailyActivity : BaseActivity<ActivityRecdDailyBinding, RecdDailyViewModel>(),
    AppBarLayout.OnOffsetChangedListener {

    lateinit var playListAdapter: PlayListAdapter

    override val layoutId: Int
        get() = R.layout.activity_recd_daily

    override val viewModel: RecdDailyViewModel by viewModels {
        InjectorUtils.provideRecdDailyViewModelFactory(this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun initView() {
        playListAdapter = PlayListAdapter(PlayListAdapter.TYPE_ADAPTER_PIC)
        recdRv.setLayoutManager(LinearLayoutManager(this))
        recdRv.setAdapter(playListAdapter)

        appbar.addOnOffsetChangedListener(this)
//        toolBar.setToolBarTitle(getString(R.string.title_recd_daily))

        matchGaussianBg.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

        val mmAndDd = getMmAndDd()
        dateTv.text = SpannableStringBuilder().also {
            it.append(mmAndDd)
            it.setSpan(AbsoluteSizeSpan(32f.dip2px()), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            it.setSpan(
                AbsoluteSizeSpan(16f.dip2px()),
                2,
                mmAndDd.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        bindingView.includePlayAll.moreChoices.showMultiple(View.OnClickListener {
            //多选
        })
        bindingView.includePlayAll.setOnPlayAllClick {
            playListAdapter.playAll()
        }

    }

    override fun initData() {
        viewModel.getRecdDaily()
    }

    override fun updateTrack() {
        super.updateTrack()
        playListAdapter.notifyDataSetChanged()
    }

    @Suppress("UNCHECKED_CAST")
    override fun startObserve() {
        super.startObserve()
        viewModel.recommend.observe(this, Observer { listSongInfo ->
            GlideUtils.loadImageCircleCrop(
                this,
                matchGaussianBg,
                listSongInfo[0].album.picUrl,
                R.color.colorPrimary
            )
            includePlayAll.show()
            bindingView.includePlayAll.trackSize = listSongInfo.size
            bindingView.includePlayAll.executePendingBindings()

            val songAndPrivileges = listSongInfo.getSongAndPrivileges()
            playListAdapter.setSongList(
                songAndPrivileges[0] as MutableList<Song>,
                songAndPrivileges[1] as List<Privilege>
            )

        })
    }

    companion object {
        fun launch(context: Context) = context.apply {
            startActivity(Intent(this, RecdDailyActivity::class.java))
        }
    }

    override fun onOffsetChanged(p0: AppBarLayout?, verticalOffset: Int) {
        var mVerticalOffset = verticalOffset
        if (mVerticalOffset == 0) {
            // 完全展开
            dateTv.alpha = 1f
        } else {
            // abs 运算
            mVerticalOffset = abs(verticalOffset)
            val totalScrollRange = appbar.totalScrollRange
            if (mVerticalOffset >= totalScrollRange) {
                // 关闭状态
                dateTv.alpha = 0f
            } else {
                //中间状态
                val progress =
                    1 - mVerticalOffset / (totalScrollRange.toFloat() - 55f.dip2px())
                dateTv.alpha = progress
                if (progress > 0.5) {
                    bindingView.toolBar.setToolBarTitle("")
                } else {
                    bindingView.toolBar.setToolBarTitle(getString(R.string.title_recd_daily))
                }
            }
        }
    }


    override fun showNetError() {
        super.showNetError()
        recdRv.netError()
    }
}