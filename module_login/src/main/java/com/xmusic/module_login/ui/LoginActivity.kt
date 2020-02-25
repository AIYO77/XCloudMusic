package com.xmusic.module_login.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.xmusic.module_login.R
import com.xw.lib_common.ext.*
import com.xw.lib_coremodel.provider.RouterPath
import kotlinx.android.synthetic.main.activity_login.*

@Route(path = RouterPath.module_login.PATH_LOGIN)
class LoginActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        // 设置状态栏颜色
        setStatusBarColor(ContextCompat.getColor(this, R.color.color_da2d1f), 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        navController = findNavController(R.id.loginNavFragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val id = destination.id
            if (id == R.id.login_mode_fragment) {
                toolbar.invisible()
            } else {
                toolbar.show()
            }
        }
        appBarConfiguration = AppBarConfiguration.Builder().build()

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
