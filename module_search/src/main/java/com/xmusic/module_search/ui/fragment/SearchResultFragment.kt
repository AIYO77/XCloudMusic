package com.xmusic.module_search.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.xmusic.module_search.R
import com.xmusic.module_search.databinding.FragmentSearchResultBinding
import com.xw.lib_common.base.view.fragment.BaseModelFragment
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.search.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_result.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class SearchResultFragment : BaseModelFragment<FragmentSearchResultBinding, SearchViewModel>() {

    private var keywords = ""

    private val searchTypeList = mutableListOf<SearchType>()

    override val viewModel: SearchViewModel by viewModels {
        InjectorUtils.provideSearchViewModelFactory(requireContext())
    }
    override val layoutId: Int
        get() = R.layout.fragment_search_result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keywords = arguments?.getString(KEYWORDS) ?: ""
    }

    override fun initData() {
        super.initData()
        viewModel.getSearchTypes()
    }

    override fun initView() {
        super.initView()
        searchResultVp.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return searchTypeList.size
            }

            override fun createFragment(position: Int): Fragment {
                val type = searchTypeList[position]
                when (type.type) {
                    1 -> { //单曲
                        return SearchSongsFragment.newInstance(type)
                    }
                    10 -> { //专辑
                        return SearchAlbumsFragment.newInstance(type)
                    }
                    100 -> { //歌手
                        return SearchSingersFragment.newInstance(type)
                    }
                    1000 -> { // 歌单
                        return SearchPlayListsFragment.newInstance(type)
                    }
                    1002 -> { // 用户
                        return SearchUsersFragment.newInstance(type)
                    }
                    1009 -> {  //电台
                        return SearchDJsFragment.newInstance(type)
                    }
                    1014 -> { //视频
                        return SearchVideosFragment.newInstance(type)
                    }
                    else -> { //综合
                        return SearchCompositeFragment.newInstance(type)
                    }
                }
            }
        }

        TabLayoutMediator(searchTypeTab, searchResultVp) { tab, position ->
            tab.text = searchTypeList[position].name
        }.attach()
    }

    override fun startObserve() {
        super.startObserve()
        viewModel.searchTypes.observe(this, Observer {
            searchTypeList.clear()
            it.forEach { type ->
                type.keywords = keywords
            }
            searchTypeList.addAll(it)
            searchResultVp.adapter?.notifyDataSetChanged()
        })
    }

    companion object {
        private const val KEYWORDS = "keywords"
        fun newInstance(keywords: String): SearchResultFragment {
            return SearchResultFragment().apply {
                arguments = Bundle().apply { putString(KEYWORDS, keywords) }
            }
        }
    }
}