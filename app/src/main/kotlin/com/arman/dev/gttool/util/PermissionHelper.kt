package com.arman.dev.gttool.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object PermissionHelper {
    
    const val SHIZUKU_PERMISSION_REQUEST_CODE = 1000
    const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
    
    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }
    
    fun requestOverlayPermission(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}")
        )
        activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }
    
    fun hasShizukuPermission(): Boolean {
        return ShizukuHelper.checkPermission()
    }
    
    fun requestShizukuPermission() {
        ShizukuHelper.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
    }
    
    fun hasAllPermissions(context: Context): Boolean {
        return hasOverlayPermission(context) && hasShizukuPermission()
    }
    
    fun checkAllPermissions(context: Context): Pair<Boolean, String> {
        if (!hasOverlayPermission(context)) {
            return false to "Overlay permission required"
        }
        if (!hasShizukuPermission()) {
            return false to "Shizuku permission required"
        }
        return true to "All permissions granted"
    }
}