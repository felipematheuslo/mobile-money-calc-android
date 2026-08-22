package com.felipelaurindo.mobilemoneycalc.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.felipelaurindo.mobilemoneycalc.viewmodel.CalculationResult
import com.felipelaurindo.mobilemoneycalc.viewmodel.CalculatorViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val inputAmount by viewModel.inputAmount.collectAsState()
    val isSendingMode by viewModel.isSendingMode.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        DisplayArea(
            inputAmount = inputAmount,
            uiState = uiState,
            isSendingMode = isSendingMode,
            onModeToggle = { viewModel.toggleMode(it) },
            modifier = Modifier.weight(1f) // Takes top 50%
        )
        CustomNumpad(
            onNumberClick = { viewModel.onNumberClick(it) },
            onClearClick = { viewModel.onClearClick() },
            onDeleteClick = { viewModel.onDeleteClick() },
            modifier = Modifier.weight(1f) // Takes bottom 50%
        )
    }
}

@Composable
fun DisplayArea(
    inputAmount: String,
    uiState: CalculationResult,
    isSendingMode: Boolean,
    onModeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Header and Toggle Switch
        ModeSelector(
            isSendingMode = isSendingMode,
            onToggleMode = onModeToggle
        )
        
        Spacer(modifier = Modifier.weight(1f))

        // Main Input
        Text(
            text = formatCurrency(inputAmount.toDoubleOrNull() ?: 0.0),
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
        
        Spacer(modifier = Modifier.weight(1f))

        // Breakdown and Grand Total or Error
        if (uiState.errorMessage.isNotEmpty()) {
            Text(
                text = uiState.errorMessage,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
        } else {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                BreakdownRow(
                    label = "Send Fee",
                    value = formatCurrency(uiState.sendFee)
                )
                Spacer(modifier = Modifier.height(8.dp))
                BreakdownRow(
                    label = if (isSendingMode) "Withdrawal Fee" else "Withdrawal Fee to Cover",
                    value = formatCurrency(uiState.withdrawalFee)
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Deducted",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatCurrency(uiState.totalRequired),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
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
            .background(MaterialTheme.colorScheme.surfaceVariant)
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
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "ModeButtonBackground"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
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
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
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
    Column(modifier = modifier.fillMaxWidth()) {
        val buttonModifier = Modifier.weight(1f).fillMaxHeight()
        
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
            NumpadButton(text = "C", onClick = onClearClick, modifier = buttonModifier)
            NumpadButton(text = "0", onClick = { onNumberClick("0") }, modifier = buttonModifier)
            NumpadButton(text = "DEL", onClick = onDeleteClick, modifier = buttonModifier)
        }
    }
}

@Composable
fun NumpadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        if (text == "DEL") {
            Text(
                text = "⌫",
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = text,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
                color = if (text == "C") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun formatCurrency(amount: Double): String {
    val format = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
    format.maximumFractionDigits = 0
    return format.format(amount)
}
