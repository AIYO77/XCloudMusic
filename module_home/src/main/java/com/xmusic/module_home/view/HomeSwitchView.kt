package com.xmusic.module_home.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import com.xw.lib_common.ext.dip2px
import com.xw.lib_common.ext.getColor
import com.xw.lib_common.ext.getString
import com.xw.lib_common.ext.toast
import com.xw.lib_coremodel.model.bean.home.AlbumItemInfo
import com.xw.lib_coremodel.model.bean.home.AlbumsAndSongs
import com.xw.lib_coremodel.model.bean.home.SongItem
import com.xmusic.module_home.databinding.ItemAlbumNormalBinding
import com.xmusic.module_home.databinding.ItemSongNormalBinding
import com.xmusic.module_home.R

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class HomeSwitchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var titleOne: TextView
    private var titleTwo: TextView
    private var rightTxt: TextView
    private var container: LinearLayout

    private var albumsAndSongs: AlbumsAndSongs? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_home_switch, this)
        titleOne = findViewById(R.id.titleOne)
        titleTwo = findViewById(R.id.titleTwo)
        rightTxt = findViewById(R.id.rightTxt)
        container = findViewById(R.id.container)
        titleOne.setOnClickListener(this)
        titleTwo.setOnClickListener(this)
        rightTxt.setOnClickListener(this)
        clipChildren = false
        clipToPadding = false

        titleOne.isSelected = true
        textViewChange(titleOne, true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.titleOne -> {
                if (titleOne.isSelected.not()) {
                    textViewChange(titleOne, true)
                    textViewChange(titleTwo, false)
                    rightTxt.text = getString(R.string.label_new_more_album)
                    setAlbumList(this.albumsAndSongs?.albums)
                    titleTwo.isSelected = false
                    titleOne.isSelected = true

                }
            }
            R.id.titleTwo -> {
                if (titleTwo.isSelected.not()) {
                    textViewChange(titleTwo, true)
                    textViewChange(titleOne, false)
                    rightTxt.text = getString(R.string.label_new_song_recommend)
                    setNewSongs(this.albumsAndSongs?.newSong)
                    titleTwo.isSelected = true
                    titleOne.isSelected = false
                }
            }
        }
    }

    private fun textViewChange(textView: TextView, isScale: Boolean) {
        if (isScale) {
            textView.apply {
                //                AnimationUtils.scale(this, 1f, 1.14f)
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(getColor(R.color.black))
            }
        } else {
            textView.apply {
                //                AnimationUtils.scale(this, 1.14f, 1f)
                textSize = 12f
                typeface = Typeface.DEFAULT
                setTextColor(getColor(R.color.colorDesText))
            }
        }
    }

    fun setData(albumsAndSongs: AlbumsAndSongs) {
        this.albumsAndSongs = albumsAndSongs
        setAlbumList(albumsAndSongs.albums)
    }

    private fun setAlbumList(albumItemInfo: List<AlbumItemInfo>?) {
        container.removeAllViews()
        albumItemInfo?.let {
            it.subList(0, if (it.size >= 3) 3 else it.size).forEachIndexed { index, al ->
                val binding = ItemAlbumNormalBinding.inflate(LayoutInflater.from(context))
                with(binding) {
                    clickListener = createAlbumClick(al)
                    album = al
                    if (index == 1) {
                        val params = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(8f.dip2px(), 0, 8f.dip2px(), 0)
                        root.layoutParams = params
                    }
                    container.addView(root)
                    executePendingBindings()
                }
            }
        }

    }

    private fun setNewSongs(newSongItem: List<SongItem>?) {
        container.removeAllViews()
        newSongItem?.subList(0, 3)?.forEachIndexed { index, it ->
            val binding = ItemSongNormalBinding.inflate(LayoutInflater.from(context))
            with(binding) {
                clickListener = createSongClick(it)
                song = it
                if (index == 1) {
                    val params = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(8f.dip2px(), 0, 8f.dip2px(), 0)
                    root.layoutParams = params
                }
                container.addView(root)

                executePendingBindings()
            }
        }
    }

    private fun createAlbumClick(albumItemInfo: AlbumItemInfo): OnClickListener {
        return OnClickListener {
            toast(albumItemInfo.name)
        }
    }

    private fun createSongClick(songItem: SongItem): OnClickListener {
        return OnClickListener {
            toast(songItem.name)
        }
    }
}