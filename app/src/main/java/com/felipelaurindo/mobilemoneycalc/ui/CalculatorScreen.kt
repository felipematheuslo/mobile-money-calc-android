package com.felipelaurindo.mobilemoneycalc.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipelaurindo.mobilemoneycalc.BuildConfig
import com.felipelaurindo.mobilemoneycalc.R
import com.felipelaurindo.mobilemoneycalc.model.ProviderConfig
import com.felipelaurindo.mobilemoneycalc.ui.components.BannerAd
import com.felipelaurindo.mobilemoneycalc.viewmodel.CalculationMode
import com.felipelaurindo.mobilemoneycalc.viewmodel.CalculationResult
import com.felipelaurindo.mobilemoneycalc.viewmodel.CalculatorViewModel

// Modern Fintech slate color palette with distinct surface hierarchy
val LightBackground = Color(0xFFF1F5F9) // Slate-100 neutral canvas background
val CardBackground = Color(0xFFFFFFFF) // Pure white card surface
val ModeContainerBackground = Color(0xFFE2E8F0) // Slate-200 segmented control container
val NumpadDeckBackground = Color(0xFFE2E8F0) // Slate-200 console base surface
val TextDark = Color(0xFF0F172A) // Slate-900 high-contrast primary text
val TextMuted = Color(0xFF64748B) // Slate-500 secondary labels
val BorderSubtle = Color(0xFFCBD5E1) // Slate-300 crisp card/button borders
val CardBorder = Color(0xFFE2E8F0) // Slate-200 subtle divider
val ErrorRed = Color(0xFFDC2626) // Red-600 error
val ErrorSubtle = Color(0xFFFEE2E2) // Red-100 error background
val ErrorBorder = Color(0xFFFCA5A5) // Red-300 error border

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
    "mpesa_tz" -> Color(0xFFE60000) // Vodacom Red
    else -> Color(0xFF00B365) // Safaricom Green
}

fun getProviderOnBrandColor(providerId: String): Color = when (providerId) {
    "mtn_ug" -> Color(0xFF0F172A) // High-contrast dark slate on yellow
    else -> Color.White
}

fun getProviderAccentColor(providerId: String): Color = when (providerId) {
    "mtn_ug" -> Color(0xFFB45309) // Amber-700 for text on white background
    "airtel_ug" -> Color(0xFFED1C24)
    "mpesa_tz" -> Color(0xFFE60000) // Vodacom Red
    else -> Color(0xFF00B365) // Safaricom Green default
}

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputAmount by viewModel.inputAmount.collectAsState()
    val calculationMode by viewModel.calculationMode.collectAsState()
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val providers = viewModel.providers

    val brandColor = getProviderBrandColor(selectedProvider.id)
    val onBrandColor = getProviderOnBrandColor(selectedProvider.id)
    val accentColor = getProviderAccentColor(selectedProvider.id)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // 1. Upper Functional Area (Expanded weight: gives comfortable breathing space for receipt & total)
        Column(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Distinct Operator Header Card (Fintech Bank Header Style)
            ProviderHeaderCard(
                providers = providers,
                selectedProvider = selectedProvider,
                brandColor = brandColor,
                onProviderSelected = { viewModel.selectProvider(it) }
            )

            // Sleek Single-Row Mode Selector
            ModeSingleRowSelector(
                currentMode = calculationMode,
                hasPochi = selectedProvider.pochiTariffs != null,
                brandColor = brandColor,
                onBrandColor = onBrandColor,
                onModeSelected = { viewModel.setCalculationMode(it) }
            )

            // Smart Receipt Card (Fills upper area with generous room for total)
            SmartReceiptCard(
                inputAmount = inputAmount,
                calculationMode = calculationMode,
                selectedProvider = selectedProvider,
                uiState = uiState,
                accentColor = accentColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        // 2. Middle AdMob Banner (Generous padding separating from receipt and keypad)
        BannerAd(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 10.dp)
        )

        // 3. Hardware Numpad Console Deck (Ergonomic thumb height, clean separation)
        Box(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = NumpadDeckBackground,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .heightIn(max = 270.dp)
            ) {
                CustomNumpad(
                    onNumberClick = { viewModel.onNumberClick(it) },
                    onClearClick = { viewModel.onClearClick() },
                    onDeleteClick = { viewModel.onDeleteClick() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderHeaderCard(
    providers: List<ProviderConfig>,
    selectedProvider: ProviderConfig,
    brandColor: Color,
    onProviderSelected: (ProviderConfig) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Surface(
        onClick = { showSheet = true },
        shape = RoundedCornerShape(14.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Operator Avatar/Flag with brand-tinted background
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = brandColor.copy(alpha = 0.14f),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = getCountryFlag(selectedProvider.country),
                            fontSize = 18.sp
                        )
                    }
                }
                Column {
                    Text(
                        text = "${selectedProvider.providerName} • ${selectedProvider.country}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(R.string.currency_format, selectedProvider.currency),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }

            // Sleek Chevron Dropdown Indicator Pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ModeContainerBackground,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.provider_select_desc),
                        tint = TextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = CardBackground,
            dragHandle = { BottomSheetDefaults.DragHandle(color = BorderSubtle) },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            scrimColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 20.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = stringResource(R.string.provider_sheet_title),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                Text(
                    text = stringResource(R.string.provider_sheet_subtitle),
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 14.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    providers.forEach { provider ->
                        val isSelected = provider.id == selectedProvider.id
                        val itemBrandColor = getProviderBrandColor(provider.id)

                        Surface(
                            onClick = {
                                onProviderSelected(provider)
                                showSheet = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) itemBrandColor.copy(alpha = 0.08f) else ModeContainerBackground.copy(alpha = 0.45f),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) itemBrandColor else BorderSubtle.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = itemBrandColor.copy(alpha = 0.16f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = getCountryFlag(provider.country),
                                                fontSize = 20.sp
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = "${provider.providerName} (${provider.country})",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) itemBrandColor else TextDark,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = stringResource(R.string.currency_format, provider.currency),
                                            fontSize = 11.5.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = itemBrandColor,
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    color = CardBorder,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Coming soon banner
                Surface(
                    color = ModeContainerBackground.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "🌍",
                            fontSize = 20.sp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.more_providers_coming_soon),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDark
                            )
                            Text(
                                text = stringResource(R.string.more_providers_coming_soon_subtitle),
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(R.string.tariffs_last_updated),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                    Text(
                        text = stringResource(R.string.app_version_format, BuildConfig.VERSION_NAME),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun ModeSingleRowSelector(
    currentMode: CalculationMode,
    hasPochi: Boolean,
    brandColor: Color,
    onBrandColor: Color,
    onModeSelected: (CalculationMode) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ModeContainerBackground,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            ModeTab(
                title = stringResource(R.string.mode_send),
                icon = "📤",
                isSelected = currentMode == CalculationMode.SEND_ONLY,
                brandColor = brandColor,
                onBrandColor = onBrandColor,
                onClick = { onModeSelected(CalculationMode.SEND_ONLY) },
                modifier = Modifier.weight(1f)
            )
            ModeTab(
                title = stringResource(R.string.mode_for_cash),
                icon = "🤝",
                isSelected = currentMode == CalculationMode.SEND_FOR_CASH,
                brandColor = brandColor,
                onBrandColor = onBrandColor,
                onClick = { onModeSelected(CalculationMode.SEND_FOR_CASH) },
                modifier = Modifier.weight(1.15f)
            )
            ModeTab(
                title = stringResource(R.string.mode_withdraw),
                icon = "💵",
                isSelected = currentMode == CalculationMode.WITHDRAW_ONLY,
                brandColor = brandColor,
                onBrandColor = onBrandColor,
                onClick = { onModeSelected(CalculationMode.WITHDRAW_ONLY) },
                modifier = Modifier.weight(1.15f)
            )
            if (hasPochi) {
                ModeTab(
                    title = stringResource(R.string.mode_pochi),
                    icon = "🏪",
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
fun ModeTab(
    title: String,
    icon: String,
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
            shape = RoundedCornerShape(9.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp, pressedElevation = 0.dp),
            modifier = modifier
        ) {
            Text(
                text = "$icon $title",
                color = onBrandColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = TextDark
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
            modifier = modifier
        ) {
            Text(
                text = "$icon $title",
                color = TextDark,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SmartReceiptCard(
    inputAmount: String,
    calculationMode: CalculationMode,
    selectedProvider: ProviderConfig,
    uiState: CalculationResult,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val amountValue = inputAmount.toDoubleOrNull() ?: 0.0
    val formattedInputText = "${formatCurrency(amountValue)} ${selectedProvider.currency}"
    val displayFontSize = if (formattedInputText.length > 13) 23.sp else if (formattedInputText.length > 10) 26.sp else 29.sp

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 10.dp)
        ) {
            // Conversational Scenario Question & Supportive Subtitle Header
            AnimatedContent(
                targetState = calculationMode,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220)) + slideInVertically(animationSpec = tween(220)) { it / 3 }) togetherWith
                            (fadeOut(animationSpec = tween(150)) + slideOutVertically(animationSpec = tween(150)) { -it / 3 })
                },
                label = "ScenarioHeaderAnimation",
                modifier = Modifier.fillMaxWidth()
            ) { mode ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 3.dp)
                ) {
                    Text(
                        text = when (mode) {
                            CalculationMode.SEND_ONLY -> stringResource(R.string.scenario_send_question)
                            CalculationMode.SEND_FOR_CASH -> stringResource(R.string.scenario_for_cash_question)
                            CalculationMode.WITHDRAW_ONLY -> stringResource(R.string.scenario_withdraw_question)
                            CalculationMode.PAY_POCHI -> stringResource(R.string.scenario_pochi_question)
                        },
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = when (mode) {
                            CalculationMode.SEND_ONLY -> stringResource(R.string.scenario_send_subtitle)
                            CalculationMode.SEND_FOR_CASH -> stringResource(R.string.scenario_for_cash_subtitle)
                            CalculationMode.WITHDRAW_ONLY -> stringResource(R.string.scenario_withdraw_subtitle)
                            CalculationMode.PAY_POCHI -> stringResource(R.string.scenario_pochi_subtitle)
                        },
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Big Formatted Input Display
            Text(
                text = formattedInputText,
                fontSize = displayFontSize,
                fontWeight = FontWeight.Black,
                color = TextDark,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(color = CardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 3.dp))

            // Breakdown Rows or Error Message
            if (uiState.errorMessage.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ErrorSubtle,
                        border = BorderStroke(1.dp, ErrorBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    when (uiState.mode) {
                        CalculationMode.SEND_ONLY -> {
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_send_amount),
                                value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}",
                                accentColor = accentColor
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_transfer_fee),
                                value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}",
                                accentColor = accentColor
                            )
                        }
                        CalculationMode.SEND_FOR_CASH -> {
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_cash_receiver_needs),
                                value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}",
                                accentColor = accentColor
                            )
                            Spacer(modifier = Modifier.height(1.5.dp))
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_withdrawal_fee_covered),
                                value = "${formatCurrency(uiState.withdrawalFee)} ${selectedProvider.currency}",
                                accentColor = accentColor
                            )
                            if (uiState.governmentTax > 0.0) {
                                Spacer(modifier = Modifier.height(1.5.dp))
                                BreakdownRow(
                                    label = stringResource(R.string.breakdown_govt_tax, selectedProvider.taxPercentage.toString()),
                                    value = "${formatCurrency(uiState.governmentTax)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                            }
                            Spacer(modifier = Modifier.height(1.5.dp))
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_send_fee),
                                value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}",
                                accentColor = accentColor
                            )
                            Spacer(modifier = Modifier.height(1.5.dp))
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_you_must_transfer),
                                value = "${formatCurrency(uiState.youMustSend)} ${selectedProvider.currency}",
                                highlight = true,
                                accentColor = accentColor
                            )
                        }
                        CalculationMode.WITHDRAW_ONLY -> {
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_cash_to_withdraw),
                                value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}",
                                accentColor = accentColor
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_agent_fee),
                                value = "${formatCurrency(uiState.withdrawalFee)} ${selectedProvider.currency}",
                                accentColor = accentColor
                            )
                            if (uiState.governmentTax > 0.0) {
                                Spacer(modifier = Modifier.height(3.dp))
                                BreakdownRow(
                                    label = stringResource(R.string.breakdown_govt_tax, selectedProvider.taxPercentage.toString()),
                                    value = "${formatCurrency(uiState.governmentTax)} ${selectedProvider.currency}",
                                    accentColor = accentColor
                                )
                            }
                        }
                        CalculationMode.PAY_POCHI -> {
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_pochi_payment),
                                value = "${formatCurrency(uiState.baseAmount)} ${selectedProvider.currency}",
                                accentColor = accentColor
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            BreakdownRow(
                                label = stringResource(R.string.breakdown_pochi_fee),
                                value = "${formatCurrency(uiState.sendFee)} ${selectedProvider.currency}",
                                accentColor = accentColor
                            )
                        }
                    }
                }

                HorizontalDivider(color = CardBorder, thickness = 1.dp, modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))

                // Bottom Highlighted Total Row (Generous bottom spacing)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.mode == CalculationMode.WITHDRAW_ONLY) {
                            stringResource(R.string.total_balance_needed)
                        } else {
                            stringResource(R.string.total_deducted_from_account)
                        },
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp) // Add spacing between label and amount
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
                            color = accentColor,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            softWrap = false // Prevent breaking currency to the next line
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BreakdownRow(
    label: String,
    value: String,
    highlight: Boolean = false,
    accentColor: Color = Color(0xFF00B365)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (highlight) 13.5.sp else 13.sp,
            color = if (highlight) accentColor else TextMuted,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            fontSize = if (highlight) 14.5.sp else 13.5.sp,
            fontWeight = if (highlight) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (highlight) accentColor else TextDark,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.End
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
            .padding(top = 8.dp, bottom = 8.dp, start = 14.dp, end = 14.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        val rowModifier = Modifier
            .weight(1f)
            .fillMaxWidth()

        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            NumpadButton(text = "1", onClick = { onNumberClick("1") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "2", onClick = { onNumberClick("2") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "3", onClick = { onNumberClick("3") }, modifier = Modifier.weight(1f))
        }
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            NumpadButton(text = "4", onClick = { onNumberClick("4") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "5", onClick = { onNumberClick("5") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "6", onClick = { onNumberClick("6") }, modifier = Modifier.weight(1f))
        }
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            NumpadButton(text = "7", onClick = { onNumberClick("7") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "8", onClick = { onNumberClick("8") }, modifier = Modifier.weight(1f))
            NumpadButton(text = "9", onClick = { onNumberClick("9") }, modifier = Modifier.weight(1f))
        }
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            NumpadButton(text = stringResource(R.string.numpad_clear), isClear = true, onClick = onClearClick, modifier = Modifier.weight(1f))
            NumpadButton(text = "0", onClick = { onNumberClick("0") }, modifier = Modifier.weight(1f))
            NumpadButton(text = stringResource(R.string.numpad_delete), isDelete = true, onClick = onDeleteClick, modifier = Modifier.weight(1f))
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
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ErrorSubtle,
                contentColor = ErrorRed
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp, pressedElevation = 0.dp),
            border = BorderStroke(1.dp, ErrorBorder)
        ) {
            Text(text = text, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
    } else if (isDelete) {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxHeight(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CardBackground,
                contentColor = TextDark
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp, pressedElevation = 0.dp),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = stringResource(R.string.content_desc_delete),
                modifier = Modifier.size(22.dp),
                tint = TextDark
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxHeight(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CardBackground,
                contentColor = TextDark
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp, pressedElevation = 0.dp),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Text(text = text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

fun formatCurrency(amount: Double): String {
    val format = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
    format.maximumFractionDigits = 0
    return format.format(amount)
}
