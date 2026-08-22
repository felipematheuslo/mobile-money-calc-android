package com.felipelaurindo.mobilemoneycalc.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipelaurindo.mobilemoneycalc.model.ProviderConfig
import com.felipelaurindo.mobilemoneycalc.viewmodel.CalculationResult
import com.felipelaurindo.mobilemoneycalc.viewmodel.CalculatorViewModel

val DarkBackground = Color(0xFF121212)
val LightBlueAccent = Color(0xFFA8C7FA)
val GrayText = Color(0xFF9AA0A6)
val PinkAlert = Color(0xFFF2B8B5)
val DarkGrayToggle = Color(0xFF303134)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val inputAmount by viewModel.inputAmount.collectAsState()
    val isSendingMode by viewModel.isSendingMode.collectAsState()
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val providers = viewModel.providers

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Area (Dropdown, Toggle, Input, Breakdown)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ProviderDropdown(
                providers = providers,
                selectedProvider = selectedProvider,
                onProviderSelected = { viewModel.selectProvider(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModeSelector(
                isSendingMode = isSendingMode,
                onToggleMode = { viewModel.toggleMode(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Main Input
            val amountValue = inputAmount.toDoubleOrNull() ?: 0.0
            Text(
                text = "${formatCurrency(amountValue)} ${selectedProvider.currency}",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )

            Spacer(modifier = Modifier.weight(1f))

            // Breakdown Area
            if (uiState.errorMessage.isNotEmpty()) {
                Text(
                    text = uiState.errorMessage,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PinkAlert,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    BreakdownRow(
                        label = "Send Fee",
                        value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BreakdownRow(
                        label = if (isSendingMode) "Withdrawal Fee" else "Withdrawal Fee to Cover",
                        value = "${formatCurrency(uiState.withdrawalFee)} ${selectedProvider.currency}"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkGrayToggle, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Deducted",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LightBlueAccent
                        )
                        Text(
                            text = "${formatCurrency(uiState.totalRequired)} ${selectedProvider.currency}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LightBlueAccent
                        )
                    }
                }
            }
        }

        // Numpad Area
        CustomNumpad(
            onNumberClick = { viewModel.onNumberClick(it) },
            onClearClick = { viewModel.onClearClick() },
            onDeleteClick = { viewModel.onDeleteClick() },
            modifier = Modifier.weight(1f) // Takes bottom 50%
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDropdown(
    providers: List<ProviderConfig>,
    selectedProvider: ProviderConfig,
    onProviderSelected: (ProviderConfig) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedProvider.providerName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = LightBlueAccent,
                unfocusedBorderColor = GrayText,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkGrayToggle)
        ) {
            providers.forEach { provider ->
                DropdownMenuItem(
                    text = { Text(provider.providerName, color = Color.White) },
                    onClick = {
                        onProviderSelected(provider)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ModeSelector(
    isSendingMode: Boolean,
    onToggleMode: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(DarkGrayToggle)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ModeButton(
            text = "I am sending",
            isSelected = isSendingMode,
            onClick = { onToggleMode(true) },
            modifier = Modifier.weight(1f)
        )
        ModeButton(
            text = "They must receive",
            isSelected = !isSendingMode,
            onClick = { onToggleMode(false) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) LightBlueAccent else Color.Transparent,
        label = "ModeButtonBackground"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) DarkBackground else GrayText,
        label = "ModeButtonText"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun BreakdownRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = GrayText
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun CustomNumpad(
    onNumberClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        val buttonModifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            NumpadButton(text = "1", onClick = { onNumberClick("1") }, modifier = buttonModifier)
            NumpadButton(text = "2", onClick = { onNumberClick("2") }, modifier = buttonModifier)
            NumpadButton(text = "3", onClick = { onNumberClick("3") }, modifier = buttonModifier)
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            NumpadButton(text = "4", onClick = { onNumberClick("4") }, modifier = buttonModifier)
            NumpadButton(text = "5", onClick = { onNumberClick("5") }, modifier = buttonModifier)
            NumpadButton(text = "6", onClick = { onNumberClick("6") }, modifier = buttonModifier)
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            NumpadButton(text = "7", onClick = { onNumberClick("7") }, modifier = buttonModifier)
            NumpadButton(text = "8", onClick = { onNumberClick("8") }, modifier = buttonModifier)
            NumpadButton(text = "9", onClick = { onNumberClick("9") }, modifier = buttonModifier)
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            NumpadButton(text = "C", isClear = true, onClick = onClearClick, modifier = buttonModifier)
            NumpadButton(text = "0", onClick = { onNumberClick("0") }, modifier = buttonModifier)
            NumpadButton(text = "DEL", isDelete = true, onClick = onDeleteClick, modifier = buttonModifier)
        }
    }
}

@Composable
fun NumpadButton(
    text: String,
    isClear: Boolean = false,
    isDelete: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .clickable(onClick = onClick)
    ) {
        if (isDelete) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Text(
                text = text,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = if (isClear) PinkAlert else Color.White
            )
        }
    }
}

fun formatCurrency(amount: Double): String {
    val format = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
    format.maximumFractionDigits = 0
    return format.format(amount)
}
