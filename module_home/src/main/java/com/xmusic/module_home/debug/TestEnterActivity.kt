package com.xmusic.module_home.debug

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.xmusic.module_home.R

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class TestEnterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debug_enter_activity)
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this,R.id.navHostFragment).navigateUp()
    }
}