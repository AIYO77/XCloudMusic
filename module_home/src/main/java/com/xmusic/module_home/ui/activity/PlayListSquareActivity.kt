package com.xmusic.module_home.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayoutMediator
import com.jeremyliao.liveeventbus.LiveEventBus
import com.orhanobut.logger.Logger
import com.xw.lib_common.base.view.activity.BaseActivity
import com.xw.lib_common.ext.loadTransform
import com.xw.lib_common.utils.GlideApp
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.home.PlayListSquareViewModel
import com.xmusic.module_home.R
import com.xmusic.module_home.databinding.ActivityPlayListSquareBinding
import com.xmusic.module_home.ui.activity.tags.PlayListCatManagerAct
import com.xmusic.module_home.ui.fragment.PlayListSquareFragment
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_play_list_square.*
import kotlinx.android.synthetic.main.home_fragment.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 歌单广场
 */
class PlayListSquareActivity :
    BaseActivity<ActivityPlayListSquareBinding, PlayListSquareViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_play_list_square

    private val tabList = arrayListOf<PlayListCat>()

    override val viewModel: PlayListSquareViewModel by viewModels {
        InjectorUtils.providePlayListSquareViewModelFactory(this)
    }

    override fun initView() {
        setSupportActionBar(toolBar)
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            resources.displayMetrics.heightPixels * 2 / 3
        )
        headerImg.layoutParams = params
        coverView.layoutParams = params
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        btnMoreCat.setOnClickListener {
            PlayListCatManagerAct.launch(this, tabList)
        }
        viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return tabList.size
            }

            override fun createFragment(position: Int): Fragment {
                return PlayListSquareFragment.newInstance(tabList[position])
            }
        }
        TabLayoutMediator(catListTab, viewpager) { tab, position ->
            tab.text = tabList[position].name
        }.attach()
    }

    override fun initData() {

    }

    override fun startObserve() {
        super.startObserve()
        LiveEventBus.get("url", String::class.java).observe(this, Observer {
            loadBg(it)
        })

        viewModel.myPLCat.observe(this@PlayListSquareActivity, Observer { it ->
            if (it.isNullOrEmpty().not()) {
                tabList.clear()
                tabList.addAll(it.filter { it.name.length <= 4 })
                viewpager.adapter?.notifyDataSetChanged()
            } else {
                viewModel.getHotCatList()
            }
        })
    }

    private var job: Job? = null

    fun loadBg(url: String?) {
        if (url.isNullOrEmpty()) return
        if (headerImg.tag == url && headerImg.drawable != null) return
        if (job != null && job!!.isActive) {
            job!!.cancel()
        }
        headerImg.tag = url
        Logger.d("加载背景 $url")
        job = launch {
            delay(200)
            loadTransform(url, 500, 80, 8) {
                if (headerImg.tag == url) {
                    runOnUiThread {
                        headerImg.setImageDrawable(it)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewpager.adapter?.notifyItemRangeRemoved(0, tabList.size)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PlayListCatManagerAct.REQUEST_CODE_CLICK_MY_TAG && resultCode == Activity.RESULT_OK) {
            val cat = data?.getParcelableExtra<PlayListCat>("data")
            cat?.apply {
                viewpager.setCurrentItem(tabList.indexOf(this), false)
            }
        }
    }

    companion object {
        fun launch(context: Context) = context.apply {
            startActivity(Intent(this, PlayListSquareActivity::class.java))
        }
    }
}