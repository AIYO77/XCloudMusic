package com.xmusic.module_search.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.adapter.BaseSearchAdapter
import com.xmusic.module_search.adapter.SearchPlayListAdapter
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.bean.home.PlayList
import com.xw.lib_coremodel.utils.DataHolder

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 搜索结果：单曲
 */
class SearchPlayListsFragment : SearchResultVpFragment<PlayList>() {

    override val adapter: BaseSearchAdapter<PlayList> = SearchPlayListAdapter()

    companion object {
        fun newInstance(searchType: SearchType): SearchPlayListsFragment {
            return SearchPlayListsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(SEARCH_TYPE,searchType)
                }
            }
        }
    }
}