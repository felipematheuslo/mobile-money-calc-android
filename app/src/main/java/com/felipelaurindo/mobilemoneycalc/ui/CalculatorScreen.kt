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

fun getProviderBrandColor(providerId: String): Color = when (providerId) {
    "mtn_ug" -> Color(0xFFFFCC00) // MTN Sunshine Yellow
    "airtel_ug" -> Color(0xFFED1C24) // Airtel Red
    "vodacom_tz" -> Color(0xFFE60000) // Vodacom Red
    else -> Color(0xFF00B365) // Safaricom Green
}

fun getProviderOnBrandColor(providerId: String): Color = when (providerId) {
    "mtn_ug" -> Color(0xFF191C1E) // High-contrast dark charcoal on yellow
    else -> Color.White
}

fun getProviderAccentColor(providerId: String): Color = when (providerId) {
    "mtn_ug" -> Color(0xFFC67D00) // Rich Amber/Gold for text on white background
    "airtel_ug" -> Color(0xFFED1C24)
    "vodacom_tz" -> Color(0xFFE60000)
    else -> Color(0xFF00B365)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val inputAmount by viewModel.inputAmount.collectAsState()
    val calculationMode by viewModel.calculationMode.collectAsState()
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val providers = viewModel.providers

    val brandColor = getProviderBrandColor(selectedProvider.id)
    val onBrandColor = getProviderOnBrandColor(selectedProvider.id)
    val accentColor = getProviderAccentColor(selectedProvider.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // Top Area (Dropdown, 4-Mode Selector, Input, Breakdown)
        Column(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ProviderDropdown(
                providers = providers,
                selectedProvider = selectedProvider,
                brandColor = brandColor,
                onProviderSelected = { viewModel.selectProvider(it) }
            )

            // Dynamic Mode Selector (adapts if Pochi is available)
            ModeGridSelector(
                currentMode = calculationMode,
                hasPochi = selectedProvider.pochiTariffs != null,
                brandColor = brandColor,
                onBrandColor = onBrandColor,
                onModeSelected = { viewModel.setCalculationMode(it) }
            )

            // Main Input Display
            val amountValue = inputAmount.toDoubleOrNull() ?: 0.0
            val formattedInputText = "${formatCurrency(amountValue)} ${selectedProvider.currency}"
            val displayFontSize = if (formattedInputText.length > 13) 26.sp else if (formattedInputText.length > 10) 30.sp else 34.sp

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = formattedInputText,
                    fontSize = displayFontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Breakdown Area
            if (uiState.errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorSubtle),
                    border = BorderStroke(1.dp, ErrorRed)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ErrorRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        when (uiState.mode) {
                            CalculationMode.SEND_ONLY -> {
                                BreakdownRow(
                                    label = "Amount to Send",
                                    value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                BreakdownRow(
                                    label = "Transfer Fee",
                                    value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                            }
                            CalculationMode.SEND_FOR_CASH -> {
                                BreakdownRow(
                                    label = "Cash Receiver Needs",
                                    value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                BreakdownRow(
                                    label = "Withdrawal Fee to Cover",
                                    value = "${formatCurrency(uiState.withdrawalFee)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                                if (uiState.governmentTax > 0.0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    BreakdownRow(
                                        label = "Govt. Tax (${selectedProvider.taxPercentage}%)",
                                        value = "${formatCurrency(uiState.governmentTax)} ${selectedProvider.currency}",
                                        accentColor = accentColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                BreakdownRow(
                                    label = "Send Fee",
                                    value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                BreakdownRow(
                                    label = "You Must Transfer",
                                    value = "${formatCurrency(uiState.youMustSend)} ${selectedProvider.currency}",
                                    highlight = true,
                                    accentColor = accentColor
                                )
                            }
                            CalculationMode.WITHDRAW_ONLY -> {
                                BreakdownRow(
                                    label = "Cash to Withdraw",
                                    value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                BreakdownRow(
                                    label = "Agent Withdrawal Fee",
                                    value = "${formatCurrency(uiState.withdrawalFee)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                                if (uiState.governmentTax > 0.0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    BreakdownRow(
                                        label = "Govt. Tax (${selectedProvider.taxPercentage}%)",
                                        value = "${formatCurrency(uiState.governmentTax)} ${selectedProvider.currency}",
                                        accentColor = accentColor
                                    )
                                }
                            }
                            CalculationMode.PAY_POCHI -> {
                                BreakdownRow(
                                    label = "Payment to Merchant",
                                    value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                BreakdownRow(
                                    label = "Pochi Merchant Fee",
                                    value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState.mode == CalculationMode.WITHDRAW_ONLY) "Total from Balance" else "Total Deducted",
                                fontSize = 15.sp,
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
                                val formattedTotal = "${formatCurrency(targetTotal)} ${selectedProvider.currency}"
                                val totalFontSize = if (formattedTotal.length > 13) 18.sp else if (formattedTotal.length > 10) 20.sp else 22.sp
                                Text(
                                    text = formattedTotal,
                                    fontSize = totalFontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = accentColor
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
    brandColor: Color,
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
                focusedBorderColor = brandColor,
                unfocusedBorderColor = brandColor.copy(alpha = 0.8f),
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
    brandColor: Color,
    onBrandColor: Color,
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
                brandColor = brandColor,
                onBrandColor = onBrandColor,
                onClick = { onModeSelected(CalculationMode.SEND_ONLY) },
                modifier = Modifier.weight(1f)
            )
            ModeButton(
                title = "🤝 Send for Cash",
                isSelected = currentMode == CalculationMode.SEND_FOR_CASH,
                brandColor = brandColor,
                onBrandColor = onBrandColor,
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
                brandColor = brandColor,
                onBrandColor = onBrandColor,
                onClick = { onModeSelected(CalculationMode.WITHDRAW_ONLY) },
                modifier = Modifier.weight(1f)
            )
            if (hasPochi) {
                ModeButton(
                    title = "🏪 Pay Pochi",
                    isSelected = currentMode == CalculationMode.PAY_POCHI,
                    brandColor = brandColor,
                    onBrandColor = onBrandColor,
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
    brandColor: Color,
    onBrandColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSelected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = brandColor,
                contentColor = onBrandColor
            ),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
            modifier = modifier
        ) {
            Text(
                text = title,
                color = onBrandColor,
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
    highlight: Boolean = false,
    accentColor: Color = PrimaryActive
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (highlight) accentColor else TextGray,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (highlight) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (highlight) accentColor else TextDark
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
