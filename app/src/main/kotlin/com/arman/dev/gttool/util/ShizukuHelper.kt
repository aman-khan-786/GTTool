package com.arman.dev.gttool.util

import android.content.Context
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku

object ShizukuHelper {
    
    fun isShizukuInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("moe.shizuku.privileged.api", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    fun isShizukuRunning(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }
    
    fun checkPermission(): Boolean {
        return try {
            if (Shizuku.isPreV11()) {
                false
            } else {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) {
            false
        }
    }
    
    fun requestPermission(requestCode: Int) {
        if (!Shizuku.isPreV11()) {
            Shizuku.requestPermission(requestCode)
        }
    }
    
    fun getVersion(): Int {
        return try {
            Shizuku.getVersion()
        } catch (e: Exception) {
            -1
        }
    }
    
    fun isReady(): Boolean {
        return isShizukuRunning() && checkPermission()
    }
}