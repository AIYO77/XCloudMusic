package com.xw.xmusic

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.orhanobut.logger.Logger
import com.xmusic.module_home.ui.fragment.HomeFragment
import com.xmusic.module_home.ui.fragment.HomeUserFragment
import com.xmusic.module_video.ui.fragment.HomeVideoFragment
import com.xw.lib_common.adapter.ViewPagerAdapter
import com.xw.lib_common.base.view.activity.BaseActivity
import com.xw.lib_common.service.MusicPlayer
import com.xw.lib_common.utils.StatusBarUtil
import com.xw.lib_common.view.TextViewIndicator
import com.xw.lib_coremodel.ext.navigation
import com.xw.lib_coremodel.provider.RouterPath
import com.xw.lib_coremodel.viewmodel.BaseViewModel
import com.xw.xmusic.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    TextViewIndicator.OnChildClickListener {

    companion object {
        fun launch(context: Context) = context.apply {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_main)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        home_indicator.setOnChildClickListener(this)
        initViewPager()
//        initDrawerLayout()
        vp_content.currentItem = 1
    }

    private fun initDrawerLayout() {
        val drawerLayout: DrawerLayout = drawer_layout

        StatusBarUtil.setColorNoTranslucentForDrawerLayout(
            this@MainActivity, drawer_layout,
            ContextCompat.getColor(this, R.color.colorTheme)
        )
//        val navView: NavigationView = nav_view
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        navView.setNavigationItemSelectedListener(this)
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
        vp_content.offscreenPageLimit = 2
        vp_content.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) = Unit

            override fun onPageSelected(position: Int) = home_indicator.setIndex(position)

            override fun onPageScrollStateChanged(state: Int) = Unit
        })
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
                RouterPath.module_search.PATH_SEARCH.navigation()
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
            }else{
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {

        MusicPlayer.exitService()
        super.onDestroy()

    }
}
