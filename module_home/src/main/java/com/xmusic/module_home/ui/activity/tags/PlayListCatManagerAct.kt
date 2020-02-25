package com.xmusic.module_home.ui.activity.tags

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.xw.lib_common.base.view.activity.BaseActivity
import com.xw.lib_common.ext.dip2px
import com.xw.lib_common.ext.no
import com.xw.lib_common.ext.toast
import com.xw.lib_common.ext.yes
import com.xw.lib_common.utils.StatusBarUtil
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.model.bean.home.TagsEntry
import com.xw.lib_coremodel.utils.ACache
import com.xw.lib_coremodel.utils.DataHolder
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.home.PlayListTagsViewModel
import com.xmusic.module_home.R
import com.xmusic.module_home.adapter.TagsManagerAdapter
import com.xmusic.module_home.callback.ItemDragHelperCallback
import com.xmusic.module_home.utils.TagGridLayoutManager
import com.xmusic.module_home.utils.TagItemDecoration
import com.xmusic.module_home.databinding.ActivityPlayListCatManagerBinding
import kotlinx.android.synthetic.main.activity_play_list_cat_manager.*

class PlayListCatManagerAct :
    BaseActivity<ActivityPlayListCatManagerBinding, PlayListTagsViewModel>(),
    TagsManagerAdapter.PlayListTagItemClickListener {

    private lateinit var managerAdapter: TagsManagerAdapter

    private var aCache: ACache? = null

    private var myTags = mutableListOf<PlayListCat>()
    override val layoutId: Int
        get() = R.layout.activity_play_list_cat_manager

    override val viewModel: PlayListTagsViewModel by viewModels {
        InjectorUtils.providePlayListTagsViewModelFactory(this)
    }

    override fun initView() {
        setSupportActionBar(toolBar)
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        val space = 10f.dip2px()
        val margin = 6f.dip2px()
        val params = tagsRv.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(margin, 0, margin, 0)
        tagsRv.requestLayout()

        val manager = TagGridLayoutManager(this, 4)
        tagsRv.setLayoutManager(manager)
        tagsRv.addItemDecoration(TagItemDecoration(space))
        val callback = ItemDragHelperCallback()
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(tagsRv.getRecycleView())

        managerAdapter = TagsManagerAdapter(this, helper, 4, space, margin)
        managerAdapter.mPlayListTagItemClickListener = this
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType: Int = managerAdapter.getItemViewType(position)
                return if (viewType == TagsManagerAdapter.TYPE_MY || viewType == TagsManagerAdapter.TYPE_OTHER) 1 else 4
            }
        }
        tagsRv.setAdapter(managerAdapter)
    }

    override fun initData() {
        myTags = DataHolder.getInstance().getData(MY_TAGS) as MutableList<PlayListCat>
        aCache = ACache.get(this)
        val data = aCache!!.getAsObject(CACHE_DATA) as? List<TagsEntry>
        data.isNullOrEmpty().yes {
            viewModel.getAllTags()
        }.no {
            managerAdapter.refreshList(mutableListOf<PlayListCat>().apply {
                addAll(myTags)
            }, data!!)
        }
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.allTags.observe(this, Observer { response ->
            val categories = response.categories
            val list = response.sub.toMutableList()
            val entryList = arrayListOf<TagsEntry>()

            categories.keys.forEach { key ->
                val newList = list.filter { key == it.category }
                list.removeAll(newList)
                entryList.add(TagsEntry(key, categories[key] ?: "", tags = newList))
            }
            managerAdapter.refreshList(mutableListOf<PlayListCat>().apply {
                addAll(myTags)
            }, entryList)
            aCache?.put(CACHE_DATA, entryList, ACache.TIME_DAY)
        })
    }

    override fun onTagClick(cat: PlayListCat?, isMyTag: Boolean) {
        cat?.let {
            isMyTag.yes {
                setResult(Activity.RESULT_OK, Intent().putExtra("data", cat))
                finish()
            }.no {
                PlayListCatDetailAct.launch(this, it)
            }
        }
    }

    override fun finishEdit() {
        if (myTags != managerAdapter.getMyTags())
            viewModel.saveMyTag(managerAdapter.getMyTags())
    }

    companion object {
        const val MY_TAGS = "MY_TAGS"
        private const val CACHE_DATA = "cache_data"
         const val REQUEST_CODE_CLICK_MY_TAG = 0x001
        fun launch(context: Activity, myTags: MutableList<PlayListCat>) = context.apply {
            val intent = Intent(this, PlayListCatManagerAct::class.java)
            DataHolder.getInstance().setData(MY_TAGS, myTags)
            startActivityForResult(intent, REQUEST_CODE_CLICK_MY_TAG)
        }
    }

}
