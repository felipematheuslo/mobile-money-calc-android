package com.felipelaurindo.mobilemoneycalc.viewmodel

import androidx.lifecycle.ViewModel
import com.felipelaurindo.mobilemoneycalc.model.MockData
import com.felipelaurindo.mobilemoneycalc.model.ProviderConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CalculationMode {
    SEND_ONLY,        // Transfer directly to recipient's wallet (no withdrawal fee)
    SEND_FOR_CASH,    // Reverse math (recipient withdraws cash from agent)
    WITHDRAW_ONLY,    // User withdrawing cash at an agent booth
    PAY_POCHI         // Paying a merchant via Pochi La Biashara
}

data class CalculationResult(
    val mode: CalculationMode = CalculationMode.SEND_ONLY,
    val baseAmount: Double = 0.0,
    val sendFee: Double = 0.0,
    val withdrawalFee: Double = 0.0,
    val governmentTax: Double = 0.0,
    val youMustSend: Double = 0.0,
    val totalRequired: Double = 0.0,
    val errorMessage: String = ""
)

class CalculatorViewModel : ViewModel() {

    val providers: List<ProviderConfig> = MockData.getProviders()

    private val _selectedProvider = MutableStateFlow(providers.first())
    val selectedProvider: StateFlow<ProviderConfig> = _selectedProvider.asStateFlow()

    private val _inputAmount = MutableStateFlow("0")
    val inputAmount: StateFlow<String> = _inputAmount.asStateFlow()

    private val _calculationMode = MutableStateFlow(CalculationMode.SEND_ONLY)
    val calculationMode: StateFlow<CalculationMode> = _calculationMode.asStateFlow()

    private val _uiState = MutableStateFlow(CalculationResult())
    val uiState: StateFlow<CalculationResult> = _uiState.asStateFlow()

    init {
        recalculate()
    }

    fun selectProvider(provider: ProviderConfig) {
        _selectedProvider.value = provider
        // If the selected provider does not support Pochi and we are on Pochi mode, fallback to SEND_ONLY
        if (_calculationMode.value == CalculationMode.PAY_POCHI && provider.pochiTariffs == null) {
            _calculationMode.value = CalculationMode.SEND_ONLY
        }
        recalculate()
    }

    fun setCalculationMode(mode: CalculationMode) {
        _calculationMode.value = mode
        recalculate()
    }

    fun onNumberClick(digit: String) {
        val current = _inputAmount.value
        if (current == "0") {
            _inputAmount.value = digit
        } else if (current.length < 8) { // Accommodate larger amounts for UGX/TZS
            _inputAmount.value = current + digit
        }
        recalculate()
    }

    fun onClearClick() {
        _inputAmount.value = "0"
        recalculate()
    }

    fun onDeleteClick() {
        val current = _inputAmount.value
        if (current.length > 1) {
            _inputAmount.value = current.dropLast(1)
        } else {
            _inputAmount.value = "0"
        }
        recalculate()
    }

    private fun formatCurrency(amount: Double): String {
        val format = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    private fun computeGovernmentTax(taxableAmount: Double, config: ProviderConfig): Double {
        if (!config.hasGovernmentTax || taxableAmount <= config.taxFreeThreshold) return 0.0
        return taxableAmount * (config.taxPercentage / 100.0)
    }

    private fun recalculate() {
        val inputAmount = _inputAmount.value
        val mode = _calculationMode.value
        val amount = inputAmount.toDoubleOrNull() ?: 0.0
        val config = _selectedProvider.value
        
        if (amount == 0.0) {
            _uiState.value = CalculationResult(mode = mode)
            return
        }

        val activeTariffs = when (mode) {
            CalculationMode.PAY_POCHI -> config.pochiTariffs ?: config.tariffs
            else -> config.tariffs
        }

        // Check against maximum allowable tariff band
        val maxLimit = activeTariffs.maxOfOrNull { it.max }?.toDouble() ?: 250000.0
        if (amount > maxLimit) {
            _uiState.value = CalculationResult(
                mode = mode,
                errorMessage = "Maximum transaction limit is ${formatCurrency(maxLimit)} ${config.currency}"
            )
            return
        }

        when (mode) {
            CalculationMode.SEND_ONLY -> {
                // 1. Send Only (Wallet to Wallet Transfer)
                val band = activeTariffs.find { amount >= it.min && amount <= it.max }
                if (band == null) {
                    _uiState.value = CalculationResult(mode = mode, errorMessage = "Amount out of range")
                    return
                }
                val sendFee = band.transferOnNet
                _uiState.value = CalculationResult(
                    mode = mode,
                    baseAmount = amount,
                    sendFee = sendFee,
                    withdrawalFee = 0.0,
                    governmentTax = 0.0,
                    totalRequired = amount + sendFee
                )
            }

            CalculationMode.SEND_FOR_CASH -> {
                // 2. Send for Cash (Reverse Math)
                val withdrawBand = activeTariffs.find { amount >= it.min && amount <= it.max }
                if (withdrawBand == null) {
                    _uiState.value = CalculationResult(mode = mode, errorMessage = "Amount out of range for withdrawal")
                    return
                }
                val withdrawalFee = withdrawBand.withdrawalAgent ?: 0.0
                val govTax = computeGovernmentTax(amount, config)
                val subtotal = amount + withdrawalFee + govTax
                
                val sendBand = activeTariffs.find { subtotal >= it.min && subtotal <= it.max }
                if (sendBand == null) {
                    _uiState.value = CalculationResult(
                        mode = mode,
                        errorMessage = "The required transfer (${formatCurrency(subtotal)} ${config.currency}) exceeds the maximum limit."
                    )
                    return
                }
                val sendFee = sendBand.transferOnNet
                val total = subtotal + sendFee

                _uiState.value = CalculationResult(
                    mode = mode,
                    baseAmount = amount,
                    sendFee = sendFee,
                    withdrawalFee = withdrawalFee,
                    governmentTax = govTax,
                    youMustSend = subtotal,
                    totalRequired = total
                )
            }

            CalculationMode.WITHDRAW_ONLY -> {
                // 3. Withdraw Only (Direct Agent Cash-Out)
                val band = activeTariffs.find { amount >= it.min && amount <= it.max }
                if (band == null) {
                    _uiState.value = CalculationResult(mode = mode, errorMessage = "Amount out of range for withdrawal")
                    return
                }
                val withdrawalFee = band.withdrawalAgent ?: 0.0
                val govTax = computeGovernmentTax(amount, config)
                _uiState.value = CalculationResult(
                    mode = mode,
                    baseAmount = amount,
                    sendFee = 0.0,
                    withdrawalFee = withdrawalFee,
                    governmentTax = govTax,
                    totalRequired = amount + withdrawalFee + govTax
                )
            }

            CalculationMode.PAY_POCHI -> {
                // 4. Pay Merchant (Pochi La Biashara)
                val band = activeTariffs.find { amount >= it.min && amount <= it.max }
                if (band == null) {
                    _uiState.value = CalculationResult(mode = mode, errorMessage = "Amount out of range")
                    return
                }
                val sendFee = band.transferOnNet
                _uiState.value = CalculationResult(
                    mode = mode,
                    baseAmount = amount,
                    sendFee = sendFee,
                    withdrawalFee = 0.0,
                    governmentTax = 0.0,
                    totalRequired = amount + sendFee
                )
            }
        }
    }
}


