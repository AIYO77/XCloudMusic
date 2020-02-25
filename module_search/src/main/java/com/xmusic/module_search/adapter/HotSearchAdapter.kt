package com.xmusic.module_search.adapter

import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.xmusic.module_search.R
import com.xmusic.module_search.databinding.ItemHotSearchBinding
import com.xmusic.module_search.databinding.ItemSearchHistoryBinding
import com.xw.lib_common.ext.*
import com.xw.lib_common.utils.GlideApp
import com.xw.lib_coremodel.data.SearchHistory
import com.xw.lib_coremodel.model.bean.search.HotSearchData

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class HotSearchAdapter(private val onItemClickListener: OnItemClick) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val searchHistoryList = mutableListOf<SearchHistory>()

    var currentList = mutableListOf<HotSearchData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_HISTORY -> {
                return SearchHistoryViewHolder(
                    ItemSearchHistoryBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }
            else -> {
                return HotSearchViewHolder(
                    ItemHotSearchBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_HISTORY -> {
                (holder as SearchHistoryViewHolder).bind(
                    createDeleteHistoryClick(), searchHistoryList
                )
            }
            else -> {
                (holder as HotSearchViewHolder).bind(
                    currentList[position],
                    position ,
                    View.OnClickListener {
                        onItemClickListener.onHotSearchClick(currentList[position])
                    })
            }
        }
    }

    private fun createDeleteHistoryClick(): View.OnClickListener {
        return View.OnClickListener {
            onItemClickListener.onDeleteHistoryClick()
        }
    }

    class HotSearchViewHolder(private val binding: ItemHotSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            hotSearchData: HotSearchData,
            position: Int,
            onClickListener: View.OnClickListener
        ) {
            with(binding) {
                onClick = onClickListener
                hotData = hotSearchData
                listNum.text = (position).toString()
                if (position < 4) {
                    listNum.setTextColor(getColor(R.color.colorTheme))
                    keywords.typeface = Typeface.DEFAULT_BOLD
                } else {
                    listNum.setTextColor(getColor(R.color.black_fifty_percent))
                    keywords.typeface = Typeface.DEFAULT
                }
                GlideApp.with(tagImg).load(hotSearchData.iconUrl).into(tagImg)
                executePendingBindings()
            }
        }
    }

    inner class SearchHistoryViewHolder(private val binding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            onDeleteClickListener: View.OnClickListener,
            data: List<SearchHistory>
        ) {
            with(binding) {
                deleteClick = onDeleteClickListener
                if (data.isNullOrEmpty()) {
                    historyLayout.gone()
                } else {
                    historyLayout.show()
                    historyItemContent.removeAllViews()
                    data.forEach {
                        val view = AppCompatTextView(root.context).apply {
                            layoutParams = ViewGroup.MarginLayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                30f.dip2px()
                            ).apply {
                                setMargins(0,0,8f.dip2px(),0)
                            }
                            gravity = Gravity.CENTER
                            background = getDrawable(R.drawable.nor_bg_channel)
                            text = it.keywords
                            isClickable = true
                            isFocusable = true
                            textSize = 13f
                            setTextColor(getColor(R.color.black_eighty_percent))
                            setOnClickListener { _ ->
                                onItemClickListener.onHistoryClick(it)
                            }
                        }
                        historyItemContent.addView(view)
                    }
                }
                executePendingBindings()
            }
        }
    }


    interface OnItemClick {
        fun onDeleteHistoryClick()
        fun onHistoryClick(searchHistory: SearchHistory)
        fun onHotSearchClick(hotSearchData: HotSearchData)
    }

    override fun getItemViewType(position: Int): Int {
        val data = currentList[position]
        return data.type
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object {
        const val VIEW_TYPE_HISTORY = -1
    }

}