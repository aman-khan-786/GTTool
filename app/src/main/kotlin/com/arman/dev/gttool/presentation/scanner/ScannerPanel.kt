package com.arman.dev.gttool.presentation.scanner

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
import com.arman.dev.gttool.data.model.ValueType
import com.arman.dev.gttool.presentation.overlay.OverlayEvent
import com.arman.dev.gttool.presentation.overlay.OverlayState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerPanel(
    state: OverlayState,
    onEvent: (OverlayEvent) -> Unit
) {
    var searchValue by remember { mutableStateOf("") }
    var selectedValueType by remember { mutableStateOf(ValueType.DWORD) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Value Type Selector
        Text(
            text = "Value Type:",
            style = MaterialTheme.typography.titleSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ValueType.values().take(3).forEach { type ->
                FilterChip(
                    selected = selectedValueType == type,
                    onClick = { selectedValueType = type },
                    label = { Text(type.name) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Search Input
        OutlinedTextField(
            value = searchValue,
            onValueChange = { searchValue = it },
            label = { Text("Search Value") },
            placeholder = { Text("Enter value to search") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Scan Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (searchValue.isNotEmpty()) {
                        onEvent(
                            OverlayEvent.StartScan(
                                pid = 0,
                                searchValue = searchValue,
                                valueType = selectedValueType
                            )
                        )
                    }
                },
                enabled = !state.isScanning && searchValue.isNotEmpty()
            ) {
                if (state.isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Scan")
            }
            
            Button(
                onClick = {
                    if (searchValue.isNotEmpty()) {
                        onEvent(OverlayEvent.RefineScan(searchValue))
                    }
                },
                enabled = state.scanResults.isNotEmpty() && !state.isScanning
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(4.dp))
                Text("Refine")
            }
            
            IconButton(
                onClick = { onEvent(OverlayEvent.ClearResults) },
                enabled = state.scanResults.isNotEmpty()
            ) {
                Icon(Icons.Default.Delete, "Clear")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Results Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Results: ${state.scanResults.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (state.isScanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        
        // Error Display
        state.scanError?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Results List
        if (state.scanResults.isEmpty() && !state.isScanning) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No results. Start a scan!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.scanResults) { result ->
                    ResultCard(
                        result = result,
                        onEdit = {
                            onEvent(
                                OverlayEvent.ModifyValue(
                                    result.address,
                                    "999"
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultCard(
    result: com.arman.dev.gttool.data.model.ScanResult,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Value: ${result.value}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Type: ${result.valueType.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}