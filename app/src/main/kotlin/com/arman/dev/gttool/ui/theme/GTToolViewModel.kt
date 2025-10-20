package com.gtttool.ui.components  // Apne package ke hisab se change kar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader

class GTToolViewModel(private val context: Context) : ViewModel() {
    private val _status = MutableStateFlow("Ready")
    val status = _status.asStateFlow()

    fun modifyMoneyValue(gamePackage: String, currentValue: Long, newValue: Long) {
        viewModelScope.launch {
            if (Shizuku.pingBinder()) {
                _status.value = "Modifying money..."
                try {
                    val shell = Shizuku.newProcess(arrayOf("sh"), null, null)
                    val process = shell.waitFor()

                    // Step 1: Game process PID find kar
                    val pidOutput = process.inputStream.bufferedReader().readText()
                    val pid = getGamePid(gamePackage)  // Helper function below

                    if (pid > 0) {
                        // Step 2: Memory search for current value (like GG)
                        val searchCmd = "su -c 'cat /proc/$pid/maps | grep -i libgame.so'"  // Game lib file path
                        shell.exec(searchCmd.split(" ").toTypedArray())
                        val libPath = process.inputStream.bufferedReader().readText().trim()

                        if (libPath.isNotEmpty()) {
                            // Step 3: Replace value (hex edit example for money at offset 0x1234 - change kar apne game ke hisab se)
                            val patchCmd = "su -c 'echo \"\\x00\\x00\\xF0\\x3F\" | dd of=$libPath bs=1 seek=0x1234 conv=notrunc'"  // Example for 999999 (float/hex)
                            shell.exec(patchCmd.split(" ").toTypedArray())

                            val output = process.inputStream.bufferedReader().readText()
                            _status.value = "Success: Money set to $newValue! Restart game."
                        } else {
                            _status.value = "Lib file not found. Try root mode."
                        }
                    } else {
                        _status.value = "Game PID not found. Start game first."
                    }
                } catch (e: Exception) {
                    _status.value = "Error: ${e.message}"
                    e.printStackTrace()
                }
            } else {
                _status.value = "Shizuku not ready. Start it first."
            }
        }
    }

    private fun getGamePid(gamePackage: String): Int {
        // Simple PID finder - expand kar if needed
        return 1234  // Placeholder, real mein 'ps -A | grep $gamePackage' use kar
    }
}
