package com.xmusic.module_video.ui.activity

import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.xmusic.module_video.R
import com.xmusic.module_video.ui.fragment.HomeVideoFragment

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class TestVideoActivity : AppCompatActivity() {

    private var newFragment: HomeVideoFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        // 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
            window.enterTransition = Explode()
            window.exitTransition = Explode()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_video)

        newFragment = HomeVideoFragment.newInstance(true)
        val transaction =
            supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, newFragment!!)
        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()

    }

    override fun onBackPressed() {
        if (newFragment?.onBackPressed() == true){
            return
        }
        super.onBackPressed()
    }
}