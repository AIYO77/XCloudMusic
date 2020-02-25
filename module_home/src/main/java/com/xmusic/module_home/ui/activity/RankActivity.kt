package com.xmusic.module_home.ui.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.xw.lib_common.base.view.activity.BaseActivity
import com.xw.lib_coremodel.model.bean.home.TopListItem
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xmusic.module_home.R
import com.xmusic.module_home.databinding.ActivityRankBinding
import com.xw.lib_coremodel.viewmodel.home.RankViewModel
import com.xmusic.module_home.adapter.RankAdapter
import kotlinx.android.synthetic.main.activity_rank.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 排行榜
 */
class RankActivity : BaseActivity<ActivityRankBinding, RankViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_rank

    override fun initView() {
        setSupportActionBar(bindingView.toolBar)
        bindingView.toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        val adapter = RankAdapter()
        recycle.setLayoutManager(GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false))
        recycle.setAdapter(adapter)
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: RankAdapter) {
        viewModel.mTopList.observe(this, Observer {
            it?.let {
                var mIndex = 0
                it.forEach { topListItem ->
                    if (topListItem.tracks.isNullOrEmpty().not()) {
                        mIndex++
                    }
                }
                val mutableList = it.toMutableList()
                if (mIndex > 0) {
                    mutableList.add(0, TopListItem(name = "官方榜", isTitle = true))
                    mutableList.add(mIndex + 1, TopListItem(name = "全球榜单", isTitle = true))
                } else if (mIndex == 0) {
                    mutableList.add(mIndex, TopListItem(name = "全球榜单", isTitle = true))
                }
                adapter.submitList(mutableList)
            }
        })
    }

    override val viewModel: RankViewModel by viewModels {
        InjectorUtils.provideRankViewModelFactory(this)
    }

    override fun initData() {
        viewModel.getTopList()
    }


    companion object {
        fun lunch(context: Context?) =
            context?.apply {
                startActivity(Intent(this, RankActivity::class.java))
            }
    }
}
