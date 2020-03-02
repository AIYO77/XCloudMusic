package com.xw.xmusic

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.orhanobut.logger.Logger
import com.xmusic.module_home.ui.fragment.HomeFragment
import com.xmusic.module_home.ui.fragment.HomeUserFragment
import com.xmusic.module_video.ui.fragment.HomeVideoFragment
import com.xw.lib_common.adapter.ViewPagerAdapter
import com.xw.lib_common.base.view.activity.BaseActivity
import com.xw.lib_common.ext.toast
import com.xw.lib_common.service.MusicPlayer
import com.xw.lib_common.utils.GlideUtils
import com.xw.lib_common.utils.StatusBarUtil
import com.xw.lib_common.view.TextViewIndicator
import com.xw.lib_coremodel.ext.navigation
import com.xw.lib_coremodel.provider.RouterPath
import com.xw.lib_coremodel.utils.InjectorUtils
import com.xw.lib_coremodel.viewmodel.MainViewModel
import com.xw.xmusic.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(),
    NavigationView.OnNavigationItemSelectedListener,
    TextViewIndicator.OnChildClickListener {

    companion object {
        fun launch(context: Context) = context.apply {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private lateinit var mLoggedVs: ViewStub
    private lateinit var mNotLoginVs: ViewStub

    override val viewModel: MainViewModel by viewModels {
        InjectorUtils.provideMainViewModelFactory(this)
    }

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initData() {
    }

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        home_indicator.setOnChildClickListener(this)
        initViewPager()
        initDrawerLayout()
        vp_content.currentItem = 1
        val layout = nav_view.getHeaderView(0)
        mLoggedVs = layout.findViewById(R.id.loggedVs)
        mNotLoginVs = layout.findViewById(R.id.notLoginVs)
    }

    override fun startObserve() {
        super.startObserve()

        viewModel.loginUser?.observe(this, Observer {
            if (it != null) {
                if (mLoggedVs.parent != null) {
                    val view = mLoggedVs.inflate()
                    val imageView = view.findViewById<AppCompatImageView>(R.id.userLogo)
                    val bgIv = view.findViewById<AppCompatImageView>(R.id.bgIv)
                    GlideUtils.loadImageCircleCrop(this, imageView, it.avatarUrl)
                    GlideUtils.loadGaussian(bgIv, it.avatarUrl)
                    view.findViewById<AppCompatTextView>(R.id.userName).text = it.nickname
                }
            } else {
                if (mNotLoginVs.parent != null) {
                    val view = mNotLoginVs.inflate()
                    val textView = view.findViewById<AppCompatTextView>(R.id.loginTip)
                    textView.text =
                        getString(R.string.label_login_tip, getString(R.string.app_name))
                    view.findViewById<AppCompatTextView>(R.id.loginTv).setOnClickListener {
                        drawer_layout.closeDrawer(GravityCompat.START)
                        RouterPath.module_login.PATH_LOGIN.navigation()
                    }
                }
            }
        })
    }

    private fun initDrawerLayout() {
        val drawerLayout: DrawerLayout = drawer_layout

        StatusBarUtil.setColorNoTranslucentForDrawerLayout(
            this@MainActivity, drawer_layout,
            ContextCompat.getColor(this, R.color.colorTheme)
        )
        val navView: NavigationView = nav_view
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    private val videoFragment = HomeVideoFragment.newInstance(false)
    private fun initViewPager() {
        vp_content.adapter = ViewPagerAdapter(
            supportFragmentManager,
            listOf(
                HomeUserFragment.newInstance(),
                HomeFragment.newInstance(),
                videoFragment
            )
        )
        vp_content.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) = Unit

            override fun onPageSelected(position: Int) = home_indicator.setIndex(position)

            override fun onPageScrollStateChanged(state: Int) = Unit
        })
        vp_content.offscreenPageLimit = 2

    }

    override fun onChildClick(index: Int) {
        vp_content.currentItem = index
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                toast("待完成...")
//                RouterPath.module_search.PATH_SEARCH.navigation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (videoFragment.onBackPressed()) {
                return
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {

        MusicPlayer.exitService()
        super.onDestroy()

    }

}
