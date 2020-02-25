package com.xw.lib_common.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.orhanobut.logger.Logger
import com.xw.lib_common.R
import com.xw.lib_common.ext.getString

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
class PermissionHelper(private val mActivity: Activity) {

    companion object {
        private const val READ_EXTERNAL_STORAGE = 101

        private const val WRITE_EXTERNAL_STORAGE_CODE = 102

        private const val REQUEST_OPEN_APPLICATION_SETTINGS_CODE = 12345

    }

    private var mOnApplyPermissionListener: OnApplyPermissionListener? = null

    private val mPermissionModels = arrayOf(
        PermissionModel(
            getString(id = R.string.label_permission_write_external_storage),
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            getString(R.string.label_write_external_storage_des),
            WRITE_EXTERNAL_STORAGE_CODE
        ),
        PermissionModel(
            getString(R.string.label_permission_read_external_storage),
            Manifest.permission.READ_EXTERNAL_STORAGE,
            getString(R.string.label_read_external_storage_des),
            READ_EXTERNAL_STORAGE
        )
    )

    fun applyPermissions() {
        try {
            mPermissionModels.forEach {
                if (ContextCompat.checkSelfPermission(
                        mActivity,
                        it.permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        mActivity,
                        arrayOf(it.permission),
                        it.requestCode
                    )
                    return
                }
            }
            mOnApplyPermissionListener?.onAfterApplyAllPermission()
        } catch (e: Throwable) {
            Logger.e(e.localizedMessage)
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE_CODE -> {
                // 如果用户不允许，我们视情况发起二次请求或者引导用户到应用页面手动打开
                if (PackageManager.PERMISSION_GRANTED != grantResults[0]) {
                    // 二次请求，表现为：以前请求过这个权限，但是用户拒接了
                    // 在二次请求的时候，会有一个“不再提示的”checkbox
                    // 因此这里需要给用户解释一下我们为什么需要这个权限，否则用户可能会永久不在激活这个申请
                    // 方便用户理解我们为什么需要这个权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            mActivity,
                            permissions[0]
                        )
                    ) {
                        val builder = AlertDialog.Builder(mActivity)
                            .setTitle(getString(R.string.title_permission))
                            .setMessage(findPermissionExplain(permissions[0]))
                            .setPositiveButton(getString(R.string.label_confirm)) { _, _ -> applyPermissions() }
                        builder.setCancelable(false)
                        builder.show()
                    } else {
                        val builder = AlertDialog.Builder(mActivity)
                            .setTitle(getString(R.string.title_permission))
                            .setMessage(
                                getString(
                                    R.string.label_ple_open_permission_on_window,
                                    findPermissionName(permissions[0])
                                )
                            )
                            .setPositiveButton(
                                getString(R.string.label_go_setting)
                            ) { _, _ ->
                                openApplicationSettings(
                                    REQUEST_OPEN_APPLICATION_SETTINGS_CODE
                                )
                            }
                            .setNegativeButton(
                                getString(R.string.label_cancel)
                            ) { _, _ -> mActivity.finish() }
                        builder.setCancelable(false)
                        builder.show()
                    }
                    return
                }
                // 到这里就表示用户允许了本次请求，我们继续检查是否还有待申请的权限没有申请
                if (isAllRequestedPermissionGranted()) {
                    mOnApplyPermissionListener?.onAfterApplyAllPermission()
                } else {
                    applyPermissions()
                }
            }
        }
    }

    /**
     * 对应Activity的 `onActivityResult(...)` 方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_OPEN_APPLICATION_SETTINGS_CODE -> if (isAllRequestedPermissionGranted()) {
                mOnApplyPermissionListener?.onAfterApplyAllPermission()
            } else {
                mActivity.finish()
            }
        }
    }

    /**
     * 判断是否所有的权限都被授权了
     *
     * @return
     */
    fun isAllRequestedPermissionGranted(): Boolean {
        for (model in mPermissionModels) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                    mActivity,
                    model.permission
                )
            ) {
                return false
            }
        }
        return true
    }


    /**
     * 打开应用设置界面
     *
     * @param requestCode 请求码
     * @return
     */
    private fun openApplicationSettings(requestCode: Int): Boolean {
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + mActivity.packageName)
            )
            intent.addCategory(Intent.CATEGORY_DEFAULT)

            // Android L 之后Activity的启动模式发生了一些变化
            // 如果用了下面的 Intent.FLAG_ACTIVITY_NEW_TASK ，并且是 startActivityForResult
            // 那么会在打开新的activity的时候就会立即回调 onActivityResult
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivityForResult(intent, requestCode)
            return true
        } catch (e: Throwable) {
            Logger.e(e.localizedMessage)
        }

        return false
    }

    /**
     * 查找申请权限的解释短语
     *
     * @param permission 权限
     * @return
     */
    private fun findPermissionExplain(permission: String): String? {
        for (model in mPermissionModels) {
            if (model.permission == permission) {
                return model.explain
            }
        }
        return null
    }

    /**
     * 查找申请权限的名称
     *
     * @param permission 权限
     * @return
     */
    private fun findPermissionName(permission: String): String {
        for (model in mPermissionModels) {
            if (model.permission == permission) {
                return model.name
            }
        }
        return ""
    }

    fun setOnApplyPermissionListener(onApplyPermissionListener: OnApplyPermissionListener) {
        mOnApplyPermissionListener = onApplyPermissionListener
    }

    private class PermissionModel(
        /**
         * 权限名称
         */
        var name: String,
        /**
         * 请求的权限
         */
        var permission: String,
        /**
         * 解析为什么请求这个权限
         */
        var explain: String,
        /**
         * 请求代码
         */
        var requestCode: Int
    )

    /**
     * 权限申请事件监听
     */
    interface OnApplyPermissionListener {

        /**
         * 申请所有权限之后的逻辑
         */
        fun onAfterApplyAllPermission()
    }
}