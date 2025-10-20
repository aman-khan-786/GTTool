package com.arman.dev.gttool.data.repository

import android.app.Application
import android.content.pm.ApplicationInfo
import com.arman.dev.gttool.data.model.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(private val application: Application) {
    
    suspend fun getInstalledGames(): List<Game> = withContext(Dispatchers.IO) {
        val packageManager = application.packageManager
        val packages = packageManager.getInstalledApplications(0)
        
        packages
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .map { appInfo ->
                Game(
                    packageName = appInfo.packageName,
                    name = appInfo.loadLabel(packageManager).toString(),
                    version = try {
                        packageManager.getPackageInfo(appInfo.packageName, 0).versionName ?: "1.0"
                    } catch (e: Exception) {
                        "1.0"
                    }
                )
            }
            .sortedBy { it.name }
    }
}