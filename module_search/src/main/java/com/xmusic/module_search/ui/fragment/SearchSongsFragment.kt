package com.xmusic.module_search.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import com.xmusic.module_search.adapter.BaseSearchAdapter
import com.xmusic.module_search.adapter.SearchSongsAdapter
import com.xmusic.module_search.adapter.viewholder.SearchSongViewHolder
import com.xw.lib_coremodel.data.SearchType
import com.xw.lib_coremodel.model.bean.Song
import com.xw.lib_coremodel.model.bean.home.SongInfo
import com.xw.lib_coremodel.utils.DataHolder

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc: 搜索结果：单曲
 */
class SearchSongsFragment : SearchResultVpFragment<Song>() {

    override val adapter: BaseSearchAdapter<Song>  =  SearchSongsAdapter()

    companion object {
        fun newInstance(searchType: SearchType): SearchSongsFragment {
            return SearchSongsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(SEARCH_TYPE,searchType)
                }
            }
        }
    }
}