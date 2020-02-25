package com.xw.xmusic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.orhanobut.logger.Logger
import com.xw.lib_common.ext.beforeL
import com.xw.lib_common.ext.beforeM
import com.xw.lib_common.utils.PermissionHelper

class SplashActivity : AppCompatActivity() {

    private lateinit var mPermissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置全屏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // 移除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)

        mPermissionHelper = PermissionHelper(this)
        mPermissionHelper.setOnApplyPermissionListener(object :
            PermissionHelper.OnApplyPermissionListener {
            override fun onAfterApplyAllPermission() {
                runApp()
            }
        })
        if (beforeM()){
            runApp()
        }else{
            if (mPermissionHelper.isAllRequestedPermissionGranted()){
                runApp()
            }else{
                mPermissionHelper.applyPermissions()
            }
        }
    }

    private fun runApp() {
        MainActivity.launch(this)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionHelper.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mPermissionHelper.onActivityResult(requestCode,resultCode,data)
    }
}
