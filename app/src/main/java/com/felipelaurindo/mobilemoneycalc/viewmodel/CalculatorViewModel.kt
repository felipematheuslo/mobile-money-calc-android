package com.felipelaurindo.mobilemoneycalc.viewmodel

import androidx.lifecycle.ViewModel
import com.felipelaurindo.mobilemoneycalc.model.MockData
import com.felipelaurindo.mobilemoneycalc.model.ProviderConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CalculationResult(
    val baseAmount: Double = 0.0,
    val sendFee: Double = 0.0,
    val withdrawalFee: Double = 0.0,
    val totalRequired: Double = 0.0,
    val errorMessage: String = ""
)

class CalculatorViewModel : ViewModel() {

    val providers: List<ProviderConfig> = MockData.getProviders()

    private val _selectedProvider = MutableStateFlow(providers.first())
    val selectedProvider: StateFlow<ProviderConfig> = _selectedProvider.asStateFlow()

    private val _inputAmount = MutableStateFlow("0")
    val inputAmount: StateFlow<String> = _inputAmount.asStateFlow()

    private val _isSendingMode = MutableStateFlow(true)
    val isSendingMode: StateFlow<Boolean> = _isSendingMode.asStateFlow()

    private val _uiState = MutableStateFlow(CalculationResult())
    val uiState: StateFlow<CalculationResult> = _uiState.asStateFlow()

    init {
        recalculate()
    }

    fun selectProvider(provider: ProviderConfig) {
        _selectedProvider.value = provider
        recalculate()
    }

    fun onNumberClick(digit: String) {
        val current = _inputAmount.value
        if (current == "0") {
            _inputAmount.value = digit
        } else if (current.length < 7) {
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

    fun toggleMode(isSending: Boolean) {
        _isSendingMode.value = isSending
        recalculate()
    }

    private fun recalculate() {
        val inputAmount = _inputAmount.value
        val isSendingMode = _isSendingMode.value
        val amount = inputAmount.toDoubleOrNull() ?: 0.0
        val config = _selectedProvider.value
        
        if (amount == 0.0) {
            _uiState.value = CalculationResult()
            return
        }

        if (isSendingMode) {
            // Mode 1: Direct Math
            val band = config.tariffs.find { amount >= it.min && amount <= it.max }
            if (band == null) {
                _uiState.value = CalculationResult(errorMessage = "Amount out of range")
                return
            }
            val sendFee = band.transferOnNet
            val withdrawalFee = band.withdrawalAgent ?: 0.0
            
            _uiState.value = CalculationResult(
                baseAmount = amount,
                sendFee = sendFee,
                withdrawalFee = withdrawalFee,
                totalRequired = amount + sendFee
            )
        } else {
            // Mode 2: Reverse Math (The receiver needs an exact net amount)
            val targetWithdrawalBand = config.tariffs.find { amount >= it.min && amount <= it.max }
            if (targetWithdrawalBand == null) {
                _uiState.value = CalculationResult(errorMessage = "Amount out of range")
                return
            }
            val withdrawalFee = targetWithdrawalBand.withdrawalAgent ?: 0.0
            val grossAmount = amount + withdrawalFee
            
            val targetSendBand = config.tariffs.find { grossAmount >= it.min && grossAmount <= it.max }
            if (targetSendBand == null) {
                _uiState.value = CalculationResult(errorMessage = "Total exceeds maximum limits")
                return
            }
            val sendFee = targetSendBand.transferOnNet
            
            _uiState.value = CalculationResult(
                baseAmount = amount,
                sendFee = sendFee,
                withdrawalFee = withdrawalFee,
                totalRequired = grossAmount + sendFee
            )
        }
    }
}
