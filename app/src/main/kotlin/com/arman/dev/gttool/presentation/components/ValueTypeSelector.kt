package com.arman.dev.gttool.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arman.dev.gttool.data.model.ValueType
import com.arman.dev.gttool.ui.GTToolViewModel  // Import your ViewModel (create if not exists)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValueTypeSelector(
    gamePackage: String,  // Pass game package from parent
    currentValue: Long,   // Current money value from UI input
    newValue: Long,       // New money value from UI input
    modifier: Modifier = Modifier
) {
    val viewModel: GTToolViewModel = viewModel()  // Get ViewModel
    var selectedType by remember { mutableStateOf(ValueType.DWORD) }  // Default to DWORD for money
    var expanded by remember { mutableStateOf(false) }
    val status by viewModel.status.collectAsState()  // Observe status from ViewModel
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dropdown for Value Type
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedType.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Value Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ValueType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName) },
                        onClick = {
                            selectedType = type
                            expanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Modify Button - Triggers the hack
        Button(
            onClick = {
                viewModel.modifyMoneyValue(gamePackage, currentValue, newValue, selectedType)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Modify Money Value")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Status Text (shows success/error)
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
