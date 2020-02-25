package com.xmusic.module_home.adapter

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.xw.lib_common.ext.*
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xw.lib_coremodel.model.bean.home.TagsEntry
import com.xmusic.module_home.R
import com.xmusic.module_home.callback.OnDragVHListener
import com.xmusic.module_home.callback.OnItemMoveListener
import com.xmusic.module_home.databinding.ItemMyTagHeaderBinding
import com.xmusic.module_home.utils.TagGridLayoutManager
import com.xmusic.module_home.databinding.ItemMyTagBinding
import com.xmusic.module_home.databinding.ItemOtherTagHeaderBinding
import java.util.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class TagsManagerAdapter(
    private val mContext: Context,
    private val mItemTouchHelper: ItemTouchHelper,
    private val mSpanCount: Int,
    private val mSpace: Int,
    private val mMargin: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnItemMoveListener {

    private var mMyTags = mutableListOf<PlayListCat>()
    private var mAllTagsEntries = listOf<TagsEntry>()
    var myTagsSize = 0
    private var otherHeaderPosList = arrayListOf<Int>()
    private var mInflater: LayoutInflater = LayoutInflater.from(mContext)
    private var mItemWidth: Int =
        (mContext.getScreenWidth() - mSpace * (mSpanCount + 1) - mMargin * 2) / mSpanCount

    private var mIsAniming = false
    var mPlayListTagItemClickListener: PlayListTagItemClickListener? = null

    // 是否为 编辑 模式
    private var isEditMode = false

    private var totalSize = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_MY_TAG_HEADER -> {
                return MyTagHeaderViewHolder(
                    ItemMyTagHeaderBinding.inflate(
                        mInflater,
                        parent,
                        false
                    )
                )
            }
            TYPE_MY -> {
                return MyTagViewHolder(
                    parent as RecyclerView,
                    ItemMyTagBinding.inflate(mInflater, parent, false)
                )
            }
            TYPE_OTHER_TAG_HEADER -> {
                return OtherTagHeaderViewHolder(
                    ItemOtherTagHeaderBinding.inflate(
                        mInflater,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return OtherTagViewHolder(
                    parent as RecyclerView,
                    ItemMyTagBinding.inflate(mInflater, parent, false)
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return totalSize
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_MY_TAG_HEADER -> {
                val myHeaderHolder = holder as MyTagHeaderViewHolder
                if (isEditMode) {
                    myHeaderHolder.bind.tvBtnEdit.text = getString(R.string.label_suc)
                    myHeaderHolder.bind.tips.text = getString(R.string.label_drag_edit)
                } else {
                    myHeaderHolder.bind.tvBtnEdit.text = getString(R.string.label_edit)
                    myHeaderHolder.bind.tips.text = getString(R.string.label_long_click_edit)
                }
            }
            TYPE_MY -> {
                val myHolder = holder as MyTagViewHolder
                val myListTag = mMyTags[position - 1]
                myHolder.bind.tv.text = myListTag.name
                myHolder.isResident = myListTag.isResident

                if (isEditMode) {
                    if (myHolder.isResident) {
                        myHolder.setIconHot(myListTag.hot)
                        myHolder.itemView.isEnabled = false
                        myHolder.itemView.alpha = 0.2f
                    } else {
                        myHolder.setIconDel()
                        myHolder.itemView.alpha = 1f
                        myHolder.itemView.isEnabled = true
                    }
                } else {
                    myHolder.setIconHot(myListTag.hot)
                    myHolder.itemView.isEnabled = true
                    myHolder.itemView.alpha = 1f
                }
            }
            TYPE_OTHER_TAG_HEADER -> {
                val otherHeaderHolder = holder as OtherTagHeaderViewHolder
                val headerIndex = getOtherHeaderIndex(position)
                if (headerIndex != -1 && headerIndex < mAllTagsEntries.size) {
                    otherHeaderHolder.bind.tv.text = mAllTagsEntries[headerIndex].name
                }
            }
            TYPE_OTHER -> {
                val otherHolder = holder as OtherTagViewHolder
                val cat = getOtherTag(position)
                cat?.apply {
                    otherHolder.bind.tv.text = name
                    otherHolder.itemView.isEnabled = !isDisable
                    otherHolder.itemView.alpha = if (isDisable) 0.2f else 1f
                    if (isEditMode) {
                        otherHolder.setIconPlus()
                    } else {
                        otherHolder.setIconHot(hot)
                    }
                }
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(mMyTags, fromPosition - 1, toPosition - 1)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_MY_TAG_HEADER
        } else if (position > 0 && position < myTagsSize + 1) {
            return TYPE_MY
        } else {
            if (otherHeaderPosList.size > 0) {
                return if (otherHeaderPosList.contains(position)) {
                    TYPE_OTHER_TAG_HEADER
                } else {
                    TYPE_OTHER
                }
            }
        }
        return super.getItemViewType(position)
    }

    //--------- viewHolder start ---------
    inner class MyTagHeaderViewHolder(val bind: ItemMyTagHeaderBinding) :
        RecyclerView.ViewHolder(bind.root) {

        init {
            bind.tvBtnEdit.setOnClickListener {
                if (isEditMode.not()) {
                    startEditMode()
                    bind.tvBtnEdit.text = getString(R.string.label_suc)
                    bind.tips.text = getString(R.string.label_drag_edit)
                } else {
                    cancelEditMode()
                    bind.tvBtnEdit.text = getString(R.string.label_edit)
                    bind.tips.text = getString(R.string.label_long_click_edit)
                    mPlayListTagItemClickListener?.finishEdit()
                }
            }
        }
    }

    inner class MyTagViewHolder(
        private val recyclerView: RecyclerView,
        val bind: ItemMyTagBinding
    ) : RecyclerView.ViewHolder(bind.root), View.OnClickListener, View.OnLongClickListener,
        OnDragVHListener {

        var isResident = false

        init {
            with(bind) {
                onClick = this@MyTagViewHolder
                onLongClick = this@MyTagViewHolder
            }
        }

        fun setIconPlus() {
            bind.icon.show()
            bind.icon.setImageDrawable(getDrawable(R.drawable.ic_plus))
        }

        fun setIconDel() {
            bind.icon.show()
            bind.icon.setImageDrawable(getDrawable(R.drawable.ic_less))
        }

        fun setIconHot(isHot: Boolean) {
            isHot.yes {
                bind.icon.show()
                bind.icon.setImageDrawable(getDrawable(R.drawable.ic_hot))
            }.no {
                bind.icon.gone()
            }
        }

        override fun onClick(v: View?) {
            if (mIsAniming)
                return
            if (myTagsSize<=6){
                toast(getString(R.string.label_cant_del))
                return
            }
            val position = adapterPosition
            val startPosition = position - 1
            if (startPosition > mMyTags.size - 1 || startPosition < 0) {
                return
            }
            val myTag = mMyTags[startPosition]
            if (isEditMode) {
                val manager = recyclerView.layoutManager as GridLayoutManager
                if (myTag.isResident) {
                    return
                }

                val otherPos: Int = getOtherPosition(myTag)
                if (otherPos < 0) {
                    return
                }
                val currentView = manager.findViewByPosition(position)
                val targetView = manager.findViewByPosition(otherPos)
                var otherIndex = getOtherTagIndex(otherPos)
                if (otherIndex < 0) {
                    return
                }
                otherIndex %= mSpanCount
                val targetX = getXoffset(otherIndex)
                if (recyclerView.indexOfChild(targetView) >= 0) {
                    var targetY = targetView!!.top
                    val lastMyPosition = myTagsSize - 1
                    if (lastMyPosition % mSpanCount == 0) { //我的里面最后一个在最后一个第一行
                        targetY = targetY - targetView.height - mSpace
                    }
                    startAnimationMy(
                        recyclerView,
                        currentView!!,
                        this,
                        myTag,
                        targetX.toFloat(),
                        targetY.toFloat()
                    )
                } else {
                    startAnimationMy(
                        recyclerView, currentView!!, this,
                        myTag,
                        targetX.toFloat(),
                        (recyclerView.bottom + 100).toFloat()
                    )
                }
                notifyItemChanged(otherPos)
                removeMyTag(position)
            } else {
                mPlayListTagItemClickListener?.onTagClick(myTag,true)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (!isEditMode) {
                startEditMode()
                // header 按钮文字 改成 "完成"
                val view = recyclerView.getChildAt(0)
                if (view == recyclerView.layoutManager?.findViewByPosition(0)) {
                    val tvBtnEdit =
                        view.findViewById<View>(R.id.tv_btn_edit) as TextView
                    tvBtnEdit.text = getString(R.string.label_suc)
                    val tips = view.findViewById<View>(R.id.tips) as TextView
                    tips.text = getString(R.string.label_drag_edit)
                }
            }
            mItemTouchHelper.startDrag(this)
            return true
        }

        override fun onItemSelected() {
            itemView.setBackgroundResource(R.drawable.my_tag_move_bg)
        }

        override fun onItemFinish() {
            itemView.setBackgroundResource(R.drawable.nor_bg_channel)
        }
    }

    inner class OtherTagHeaderViewHolder(val bind: ItemOtherTagHeaderBinding) :
        RecyclerView.ViewHolder(bind.root)

    inner class OtherTagViewHolder(
        private val recyclerView: RecyclerView,
        val bind: ItemMyTagBinding
    ) :
        RecyclerView.ViewHolder(bind.root), View.OnClickListener, View.OnLongClickListener {

        init {
            with(bind) {
                onClick = this@OtherTagViewHolder
                onLongClick = this@OtherTagViewHolder
            }
        }

        fun setIconPlus() {
            bind.icon.show()
            bind.icon.setImageDrawable(getDrawable(R.drawable.ic_plus))
        }

        fun setIconDel() {
            bind.icon.show()
            bind.icon.setImageDrawable(getDrawable(R.drawable.ic_less))
        }

        fun setIconHot(isHot: Boolean) {
            isHot.yes {
                bind.icon.show()
                bind.icon.setImageDrawable(getDrawable(R.drawable.ic_hot))
            }.no {
                bind.icon.gone()
            }
        }

        override fun onClick(v: View?) {
            if (mIsAniming) {
                return
            }
            val manager = recyclerView.layoutManager
            val position = adapterPosition
            val otherTag = getOtherTag(position)

            if (isEditMode) {
                // 如果RecyclerView滑动到底部,移动的目标位置的y轴 - height
                val currentView = manager!!.findViewByPosition(position)
                // 目标位置的前一个item  即当前MyChannel的最后一个
                val preTargetView =
                    manager.findViewByPosition(myTagsSize) // -1 + 1
                val myIndex = (myTagsSize - 1 + 1) % mSpanCount
                val targetX = getXoffset(myIndex)

                if (otherTag!!.isDisable) {
                    return
                }
                // 如果targetView不在屏幕内,则为-1
                // 如果在屏幕内,则添加一个位移动画
                // 如果targetView不在屏幕内,则为-1
                if (recyclerView.indexOfChild(preTargetView) >= 0) {
                    var targetY = preTargetView!!.top
                    val targetPosition = myTagsSize - 1 + 1 + 1
                    val itemHeight = preTargetView.height
                    // target 在最后一行第一个
                    var animTime: Long =
                        ANIM_TIME_OTHER
                    if ((targetPosition - 1) % mSpanCount == 0) {
                        targetY += itemHeight + mSpace
                        animTime = ANIM_TIME_OTHER_FIRST
                    }
                    startAnimationOther(
                        recyclerView,
                        currentView!!,
                        this,
                        otherTag,
                        targetX.toFloat(),
                        targetY.toFloat(),
                        animTime
                    )
                } else {
                    startAnimationOther(
                        recyclerView,
                        currentView!!,
                        this,
                        otherTag,
                        targetX.toFloat(),
                        -currentView.height - 100.toFloat(),
                        ANIM_TIME
                    )
                }

                otherTag.isDisable = true
                notifyItemChanged(position)

                addMyTag(recyclerView, otherTag)
            } else {
                mPlayListTagItemClickListener?.onTagClick(otherTag,false)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (!isEditMode) {
                startEditMode()
                // header 按钮文字 改成 "完成"
                val view = recyclerView.getChildAt(0)
                if (view == recyclerView.layoutManager!!.findViewByPosition(0)) {
                    val tvBtnEdit = view.findViewById<View>(R.id.tv_btn_edit) as TextView
                    tvBtnEdit.text = getString(R.string.label_suc)
                    val tips = view.findViewById<View>(R.id.tips) as TextView
                    tips.text = getString(R.string.label_drag_edit)
                }
            }
            return true
        }
    }

    //----------- viewHolder end-------------

    private fun startEditMode() {
        isEditMode = true
        notifyDataSetChanged()
    }

    private fun cancelEditMode() {
        isEditMode = false
        notifyDataSetChanged()
    }

    private fun checkDisable() {
        for (i in mAllTagsEntries.indices) {
            val list: List<PlayListCat> = mAllTagsEntries[i].tags
            for (tag in list) {
                for (myTag in mMyTags) {
                    if (tag.name == myTag.name) {
                        tag.isDisable = true
                    }
                }
            }
        }
    }

    private fun getXoffset(index: Int): Int {
        return if (index > mSpanCount - 1) {
            -1
        } else mSpace + index * (mItemWidth + mSpace)
    }

    fun getMyTags(): List<PlayListCat> {
        return mMyTags
    }

    fun refreshList(myTags: MutableList<PlayListCat>?, allTagsEntries: List<TagsEntry>) {
        mMyTags = myTags ?: arrayListOf()
        mAllTagsEntries = allTagsEntries
        myTagsSize = mMyTags.size
        otherHeaderPosList.clear()
        if (mAllTagsEntries.isNotEmpty()) {
            otherHeaderPosList.add(myTagsSize + 1)
        }
        for (i in 0 until mAllTagsEntries.size - 1) {
            val pos: Int = otherHeaderPosList[i] + mAllTagsEntries[i].tags.size + 1
            otherHeaderPosList.add(pos)
        }
        totalSize = mMyTags.size + 1
        for ((_, _, tags) in mAllTagsEntries) {
            totalSize += tags.size + 1
        }
        checkDisable()
        notifyDataSetChanged()
    }

    fun removeMyTag(position: Int) {
        val startPosition = position - 1
        if (startPosition > mMyTags.size - 1) {
            return
        }
        mMyTags.removeAt(startPosition)
        myTagsSize = mMyTags.size
        otherHeaderPosList.clear()
        if (mAllTagsEntries.isNotEmpty()) {
            otherHeaderPosList.add(myTagsSize + 1)
        }
        for (i in 0 until mAllTagsEntries.size - 1) {
            val pos: Int = otherHeaderPosList[i] + mAllTagsEntries[i].tags.size + 1
            otherHeaderPosList.add(pos)
        }
        totalSize = mMyTags.size + 1
        for ((_, _, tags) in mAllTagsEntries) {
            totalSize += tags.size + 1
        }
        notifyItemRemoved(position)
    }

    fun addMyTag(recyclerView: RecyclerView, item: PlayListCat) {
        for (tag in mMyTags) {
            if (tag.name == item.name) {
                return
            }
        }
        mMyTags.add(item)
        myTagsSize = mMyTags.size
        otherHeaderPosList.clear()
        if (mAllTagsEntries.isNotEmpty()) {
            otherHeaderPosList.add(myTagsSize + 1)
        }
        for (i in 0 until mAllTagsEntries.size - 1) {
            val pos: Int = otherHeaderPosList[i] + mAllTagsEntries[i].tags.size + 1
            otherHeaderPosList.add(pos)
        }
        totalSize = mMyTags.size + 1
        for ((_, _, tags) in mAllTagsEntries) {
            totalSize += tags.size + 1
        }
        notifyItemInserted(myTagsSize)
        val gridLayoutManager: GridLayoutManager =
            recyclerView.layoutManager as GridLayoutManager
        val pos: Int = gridLayoutManager.findLastVisibleItemPosition()
        Log.d("jiabin", "pos:$pos | totalSize:$totalSize")
        if (pos < totalSize) { //当没到底的时候insert需要notifychanged，因为会导致滚到下一页的时候item显示不正确，但是不能使用notifyDataSetChanged，否则会使动画消失
            notifyItemRangeChanged(pos, totalSize - pos, "payload")
        }
    }

    private val delayHandler = Handler()

    /**
     * 开始增删动画 从我的到其他
     */
    private fun startAnimationMy(
        recyclerView: RecyclerView,
        currentView: View,
        myViewHolder: MyTagViewHolder,
        myTag: PlayListCat,
        targetX: Float,
        targetY: Float
    ) {
        val viewGroup = recyclerView.parent as ViewGroup
        myViewHolder.setIconPlus()
        val mirrorView: ImageView =
            addMirrorView(viewGroup, recyclerView, currentView)
        myViewHolder.setIconDel()
        val animation = getTranslateAnimator(
            targetX - currentView.left,
            targetY - currentView.top,
            ANIM_TIME
        )
        currentView.visibility = View.INVISIBLE
        mirrorView.startAnimation(animation)
        val gridLayoutManager = recyclerView.layoutManager as TagGridLayoutManager
        gridLayoutManager.setScrollEnabled(false)
        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                mIsAniming = true
            }

            override fun onAnimationEnd(animation: Animation) {
                delayHandler.postDelayed({
                    viewGroup.removeView(mirrorView)
                    if (currentView.visibility == View.INVISIBLE) {
                        currentView.visibility = View.VISIBLE
                    }
//                    if (mPlayListTagChangeListener != null) {
//                        mPlayListTagChangeListener.onMyTagRemove(
//                            mMyTags as ArrayList<PlayListCat>,
//                            myTag
//                        )
//                    }
                    gridLayoutManager.setScrollEnabled(true)
                    mIsAniming = false
                }, 0)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    /**
     * 开始增删动画 从其他到我的
     */
    private fun startAnimationOther(
        recyclerView: RecyclerView,
        currentView: View,
        otherViewHolder: OtherTagViewHolder,
        otherTag: PlayListCat,
        targetX: Float,
        targetY: Float,
        animTime: Long
    ) {
        val viewGroup = recyclerView.parent as ViewGroup
        otherViewHolder.setIconDel()
        val mirrorView: ImageView =
            addMirrorView(viewGroup, recyclerView, currentView)
        otherViewHolder.setIconPlus()
        val animation = getTranslateAnimator(
            targetX - currentView.left, targetY - currentView.top, animTime
        )
        mirrorView.startAnimation(animation)
        val gridLayoutManager =
            recyclerView.layoutManager as TagGridLayoutManager?
        gridLayoutManager!!.setScrollEnabled(false)
        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                mIsAniming = true
                recyclerView.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animation) {
                recyclerView.isEnabled = true
                delayHandler.postDelayed(Runnable {
                    viewGroup.removeView(mirrorView)
                    notifyDataSetChanged()
//                    if (mPlayListTagChangeListener != null) {
//                        mPlayListTagChangeListener.onMyTagAdd(
//                            mMyTags as ArrayList<PlayListCat>,
//                            otherTag
//                        )
//                    }
                    gridLayoutManager.setScrollEnabled(true)
                    mIsAniming = false
                }, 0)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    /**
     * 获取位移动画
     */
    private fun getTranslateAnimator(
        targetX: Float,
        targetY: Float,
        animTime: Long
    ): TranslateAnimation? {
        return TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.ABSOLUTE, targetX,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.ABSOLUTE, targetY
        ).apply {
            duration = animTime
            fillAfter = true
        }
    }

    private fun getOtherPosition(myTag: PlayListCat): Int {
        var otherPos = myTagsSize + 1
        for (i in mAllTagsEntries.indices) {
            val entry = mAllTagsEntries[i]
            if (entry.category == myTag.category) {
                for (j in entry.tags.indices) {
                    if (myTag.name == entry.tags[j].name) {
                        otherPos += j + 1
                        entry.tags[j].isDisable = false
                        return otherPos
                    }
                }
            }
            otherPos += entry.tags.size + 1
        }
        return -1
    }

    private fun addMirrorView(
        parent: ViewGroup,
        recyclerView: RecyclerView,
        view: View
    ): ImageView { //PixelCopy.request
        view.destroyDrawingCache()
        view.isDrawingCacheEnabled = true
        val mirrorView =
            ImageView(recyclerView.context)
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        mirrorView.setImageBitmap(bitmap)
        view.isDrawingCacheEnabled = false
        val locations = IntArray(2)
        view.getLocationOnScreen(locations)
        val parenLocations = IntArray(2)
        recyclerView.getLocationOnScreen(parenLocations)
        val params =
            FrameLayout.LayoutParams(bitmap.width, bitmap.height)
        params.setMargins(locations[0], locations[1] - parenLocations[1], 0, 0)
        parent.addView(mirrorView, params)
        return mirrorView
    }

    /**
     * 查找头部
     *
     * @param position
     * @return
     */
    private fun getOtherHeaderIndex(position: Int): Int {
        if (otherHeaderPosList.isEmpty()) {
            return -1
        }
        for (i in otherHeaderPosList.indices) {
            if (otherHeaderPosList[i] == position) {
                return i
            }
        }
        return -1
    }

    private fun getOtherTag(position: Int): PlayListCat? {
        var index = position - (myTagsSize + 1)
        for (i in mAllTagsEntries.indices) {
            if (index <= mAllTagsEntries[i].tags.size + 1) {
                return mAllTagsEntries[i].tags[index - 1]
            } else {
                index -= (mAllTagsEntries[i].tags.size + 1)
                if (index < 0) {
                    return null
                }
            }
        }
        return null
    }

    private fun getOtherTagIndex(position: Int): Int {
        var index = position - (myTagsSize + 1)
        for (i in mAllTagsEntries.indices) {
            if (index <= mAllTagsEntries[i].tags.size + 1) {
                return index - 1
            } else {
                index -= (mAllTagsEntries[i].tags.size + 1)
                if (index < 0) {
                    return -1
                }
            }
        }
        return -1
    }

    interface PlayListTagItemClickListener {
        fun onTagClick(cat: PlayListCat? , isMyTag:Boolean)

        fun finishEdit()
    }


    companion object {
        private const val ANIM_TIME = 360L
        private const val ANIM_TIME_OTHER = 360L
        private const val ANIM_TIME_OTHER_FIRST = 500L

        // 我的 标题部分
        const val TYPE_MY_TAG_HEADER = 1001
        // 我的
        const val TYPE_MY = 1002
        // 其他 标题部分
        const val TYPE_OTHER_TAG_HEADER = 1003
        // 其他
        const val TYPE_OTHER = 1004
    }
}