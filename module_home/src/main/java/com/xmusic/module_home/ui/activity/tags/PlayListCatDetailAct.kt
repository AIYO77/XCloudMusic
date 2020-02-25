package com.xmusic.module_home.ui.activity.tags

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.xw.lib_common.base.view.activity.BaseBindingActivity
import com.xw.lib_common.utils.StatusBarUtil
import com.xw.lib_coremodel.model.bean.home.PlayListCat
import com.xmusic.module_home.R
import com.xmusic.module_home.databinding.ActivityPlaylistCatDetailBinding
import com.xmusic.module_home.ui.fragment.PlayListSquareFragment

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PlayListCatDetailAct : BaseBindingActivity<ActivityPlaylistCatDetailBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_playlist_cat_detail

    override fun initView() {
        setSupportActionBar(bindingView.toolBar)
        bindingView.toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        val cat = intent.getParcelableExtra<PlayListCat>(CAT_DATA)
        supportActionBar?.title = cat.name
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, PlayListSquareFragment.newInstance(cat))
            .commit()
    }

    override fun initData() = Unit

    companion object {

        private const val CAT_DATA = "cat_data"

        fun launch(context: Context, playListCat: PlayListCat) = context.apply {
            val intent = Intent(this, PlayListCatDetailAct::class.java)
            intent.putExtra(CAT_DATA, playListCat)
            startActivity(intent)
        }
    }


}