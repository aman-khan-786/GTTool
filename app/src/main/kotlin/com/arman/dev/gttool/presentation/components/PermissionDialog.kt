package com.arman.dev.gttool.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    hasOverlay: Boolean,
    hasShizuku: Boolean,
    onDismiss: () -> Unit,
    onRequestOverlay: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Required Permissions") },
        text = {
            Column {
                PermissionStatus("Overlay Permission", hasOverlay)
                Spacer(Modifier.height(8.dp))
                PermissionStatus("Shizuku Permission", hasShizuku)
                Spacer(Modifier.height(16.dp))
                
                if (!hasShizuku) {
                    Text(
                        text = "Please install and activate Shizuku app first.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            if (!hasOverlay) {
                TextButton(onClick = onRequestOverlay) {
                    Text("Grant Overlay")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Later")
            }
        }
    )
}

@Composable
private fun PermissionStatus(name: String, granted: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (granted) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.width(8.dp))
        Text(name)
    }
}