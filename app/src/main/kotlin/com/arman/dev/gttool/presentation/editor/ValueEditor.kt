package com.arman.dev.gttool.presentation.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arman.dev.gttool.presentation.overlay.OverlayEvent
import com.arman.dev.gttool.presentation.overlay.OverlayState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValueEditor(
    state: OverlayState,
    onEvent: (OverlayEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Value Editor",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (state.scanResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No addresses to edit. Perform a scan first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = "${state.scanResults.size} addresses loaded",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.scanResults) { result ->
                    EditableAddressCard(
                        result = result,
                        onValueChange = { address, value ->
                            onEvent(OverlayEvent.ModifyValue(address, value))
                        },
                        onFreeze = { address ->
                            if (result.isFrozen) {
                                onEvent(OverlayEvent.UnfreezeValue(address))
                            } else {
                                onEvent(OverlayEvent.FreezeValue(address, result.value))
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableAddressCard(
    result: com.arman.dev.gttool.data.model.ScanResult,
    onValueChange: (Long, String) -> Unit,
    onFreeze: (Long) -> Unit
) {
    var editValue by remember { mutableStateOf(result.value) }
    var isEditing by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isFrozen) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = result.toDisplayAddress(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = result.valueType.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                
                Row {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Save" else "Edit"
                        )
                    }
                    
                    IconButton(onClick = { onFreeze(result.address) }) {
                        Icon(
                            imageVector = if (result.isFrozen) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = if (result.isFrozen) "Unfreeze" else "Freeze",
                            tint = if (result.isFrozen) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isEditing) {
                OutlinedTextField(
                    value = editValue,
                    onValueChange = { editValue = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("New Value") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onValueChange(result.address, editValue)
                                isEditing = false
                            }
                        ) {
                            Icon(Icons.Default.Send, "Apply")
                        }
                    }
                )
            } else {
                Text(
                    text = "Value: ${result.value}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}