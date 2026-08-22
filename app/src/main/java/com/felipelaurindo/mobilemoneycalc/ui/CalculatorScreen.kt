package com.felipelaurindo.mobilemoneycalc.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipelaurindo.mobilemoneycalc.model.ProviderConfig
import com.felipelaurindo.mobilemoneycalc.viewmodel.CalculationMode
import com.felipelaurindo.mobilemoneycalc.viewmodel.CalculatorViewModel

// Brand-tailored high performance color palette (Kenya Safaricom theme)
val LightBackground = Color(0xFFF7F9FA)
val CardBackground = Color.White
val PrimaryActive = Color(0xFF00B365) // Safaricom Green
val PrimarySubtle = Color(0xFFE8F8F1)
val ErrorRed = Color(0xFFE53935)
val ErrorSubtle = Color(0xFFFFEBEE)
val TextDark = Color(0xFF191C1E)
val TextGray = Color(0xFF6B7280)
val BorderSubtle = Color(0xFFE5E7EB)

fun getCountryFlag(country: String): String = when (country.lowercase()) {
    "kenya" -> "🇰🇪"
    "uganda" -> "🇺🇬"
    "tanzania" -> "🇹🇿"
    "ghana" -> "🇬🇭"
    "rwanda" -> "🇷🇼"
    else -> "🌍"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val inputAmount by viewModel.inputAmount.collectAsState()
    val calculationMode by viewModel.calculationMode.collectAsState()
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val providers = viewModel.providers

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // Top Area (Dropdown, 4-Mode Selector, Input, Breakdown)
        Column(
            modifier = Modifier
                .weight(1.35f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ProviderDropdown(
                providers = providers,
                selectedProvider = selectedProvider,
                onProviderSelected = { viewModel.selectProvider(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dynamic Mode Selector (adapts if Pochi is available)
            ModeGridSelector(
                currentMode = calculationMode,
                hasPochi = selectedProvider.pochiTariffs != null,
                onModeSelected = { viewModel.setCalculationMode(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Main Input Display
            val amountValue = inputAmount.toDoubleOrNull() ?: 0.0
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "${formatCurrency(amountValue)} ${selectedProvider.currency}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Breakdown Area
            if (uiState.errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorSubtle),
                    border = BorderStroke(1.dp, ErrorRed)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        when (uiState.mode) {
                            CalculationMode.SEND_ONLY -> {
                                BreakdownRow(
                                    label = "Amount to Send",
                                    value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}"
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                BreakdownRow(
                                    label = "Transfer Fee",
                                    value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}"
                                )
                            }
                            CalculationMode.SEND_FOR_CASH -> {
                                BreakdownRow(
                                    label = "Cash Receiver Needs",
                                    value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}"
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                BreakdownRow(
                                    label = "Withdrawal Fee to Cover",
                                    value = "${formatCurrency(uiState.withdrawalFee)} ${selectedProvider.currency}"
                                )
                                if (uiState.governmentTax > 0.0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    BreakdownRow(
                                        label = "Govt. Tax (${selectedProvider.taxPercentage}%)",
                                        value = "${formatCurrency(uiState.governmentTax)} ${selectedProvider.currency}"
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                BreakdownRow(
                                    label = "Send Fee",
                                    value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}"
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                BreakdownRow(
                                    label = "You Must Transfer",
                                    value = "${formatCurrency(uiState.youMustSend)} ${selectedProvider.currency}",
                                    highlight = true
                                )
                            }
                            CalculationMode.WITHDRAW_ONLY -> {
                                BreakdownRow(
                                    label = "Cash to Withdraw",
                                    value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}"
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                BreakdownRow(
                                    label = "Agent Withdrawal Fee",
                                    value = "${formatCurrency(uiState.withdrawalFee)} ${selectedProvider.currency}"
                                )
                                if (uiState.governmentTax > 0.0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    BreakdownRow(
                                        label = "Govt. Tax (${selectedProvider.taxPercentage}%)",
                                        value = "${formatCurrency(uiState.governmentTax)} ${selectedProvider.currency}"
                                    )
                                }
                            }
                            CalculationMode.PAY_POCHI -> {
                                BreakdownRow(
                                    label = "Payment to Merchant",
                                    value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}"
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                BreakdownRow(
                                    label = "Pochi Merchant Fee",
                                    value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState.mode == CalculationMode.WITHDRAW_ONLY) "Total from Balance" else "Total Deducted",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            AnimatedContent(
                                targetState = uiState.totalRequired,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        (slideInVertically { height -> height } + fadeIn()) togetherWith
                                                (slideOutVertically { height -> -height } + fadeOut())
                                    } else {
                                        (slideInVertically { height -> -height } + fadeIn()) togetherWith
                                                (slideOutVertically { height -> height } + fadeOut())
                                    }
                                },
                                label = "TotalAmountAnimation"
                            ) { targetTotal ->
                                Text(
                                    text = "${formatCurrency(targetTotal)} ${selectedProvider.currency}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryActive
                                )
                            }
                        }
                    }
                }
            }
        }

        // Numpad Area
        CustomNumpad(
            onNumberClick = { viewModel.onNumberClick(it) },
            onClearClick = { viewModel.onClearClick() },
            onDeleteClick = { viewModel.onDeleteClick() },
            modifier = Modifier.weight(1f)
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
            value = "${getCountryFlag(selectedProvider.country)} ${selectedProvider.country} - ${selectedProvider.providerName} (${selectedProvider.currency})",
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(12.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = PrimaryActive,
                unfocusedBorderColor = BorderSubtle,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark,
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground
            ),
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CardBackground)
        ) {
            providers.forEach { provider ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = "${getCountryFlag(provider.country)} ${provider.country} - ${provider.providerName} (${provider.currency})",
                            color = TextDark,
                            fontWeight = FontWeight.Medium
                        )
                    },
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
fun ModeGridSelector(
    currentMode: CalculationMode,
    hasPochi: Boolean,
    onModeSelected: (CalculationMode) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ModeButton(
                title = "📤 Send Only",
                isSelected = currentMode == CalculationMode.SEND_ONLY,
                onClick = { onModeSelected(CalculationMode.SEND_ONLY) },
                modifier = Modifier.weight(1f)
            )
            ModeButton(
                title = "🤝 Send for Cash",
                isSelected = currentMode == CalculationMode.SEND_FOR_CASH,
                onClick = { onModeSelected(CalculationMode.SEND_FOR_CASH) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ModeButton(
                title = "💵 Withdraw",
                isSelected = currentMode == CalculationMode.WITHDRAW_ONLY,
                onClick = { onModeSelected(CalculationMode.WITHDRAW_ONLY) },
                modifier = Modifier.weight(1f)
            )
            if (hasPochi) {
                ModeButton(
                    title = "🏪 Pay Pochi",
                    isSelected = currentMode == CalculationMode.PAY_POCHI,
                    onClick = { onModeSelected(CalculationMode.PAY_POCHI) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ModeButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSelected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryActive),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
            modifier = modifier
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, BorderSubtle),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
            modifier = modifier
        ) {
            Text(
                text = title,
                color = TextDark,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BreakdownRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (highlight) PrimaryActive else TextGray,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (highlight) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (highlight) PrimaryActive else TextDark
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
            .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rowModifier = Modifier
            .weight(1f)
            .fillMaxWidth()

        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumpadButton(text = "1", onClick = { onNumberClick("1") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "2", onClick = { onNumberClick("2") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "3", onClick = { onNumberClick("3") }, modifier = Modifier.weight(1f))
        }
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumpadButton(text = "4", onClick = { onNumberClick("4") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "5", onClick = { onNumberClick("5") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "6", onClick = { onNumberClick("6") }, modifier = Modifier.weight(1f))
        }
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumpadButton(text = "7", onClick = { onNumberClick("7") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "8", onClick = { onNumberClick("8") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "9", onClick = { onNumberClick("9") }, modifier = Modifier.weight(1f))
        }
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumpadButton(text = "C", isClear = true, onClick = onClearClick, modifier = Modifier.weight(1f))
            NumpadButton(text = "0", onClick = { onNumberClick("0") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "DEL", isDelete = true, onClick = onDeleteClick, modifier = Modifier.weight(1f))
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
    if (isClear) {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorSubtle,
                contentColor = ErrorRed
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
        ) {
            Text(text = text, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        }
    } else if (isDelete) {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CardBackground,
                contentColor = TextDark
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = "Delete",
                modifier = Modifier.size(26.dp),
                tint = TextDark
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CardBackground,
                contentColor = TextDark
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Text(text = text, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun formatCurrency(amount: Double): String {
    val format = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
    format.maximumFractionDigits = 0
    return format.format(amount)
}
