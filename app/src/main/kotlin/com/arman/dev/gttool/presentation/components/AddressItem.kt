package com.arman.dev.gttool.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arman.dev.gttool.data.model.ScanResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressItem(
    result: ScanResult,
    onEdit: (Long, String) -> Unit,
    onFreeze: (Long) -> Unit,
    onUnfreeze: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isFrozen) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.toDisplayAddress(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = result.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Row {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, "Edit")
                }
                
                IconButton(
                    onClick = {
                        if (result.isFrozen) onUnfreeze(result.address)
                        else onFreeze(result.address)
                    }
                ) {
                    Icon(
                        imageVector = if (result.isFrozen) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = if (result.isFrozen) "Frozen" else "Not Frozen",
                        tint = if (result.isFrozen) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
                
                IconButton(onClick = { onDelete(result.address) }) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
        }
    }
    
    if (showEditDialog) {
        EditValueDialog(
            currentValue = result.value,
            onConfirm = { newValue ->
                onEdit(result.address, newValue)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
private fun EditValueDialog(
    currentValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newValue by remember { mutableStateOf(currentValue) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Value") },
        text = {
            OutlinedTextField(
                value = newValue,
                onValueChange = { newValue = it },
                label = { Text("New Value") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(newValue) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}