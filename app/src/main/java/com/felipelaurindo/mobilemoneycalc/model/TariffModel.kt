package com.felipelaurindo.mobilemoneycalc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ProviderConfig(
    val id: String,
    val country: String,
    @SerialName("provider_name") val providerName: String,
    val currency: String,
    @SerialName("has_government_tax") val hasGovernmentTax: Boolean,
    @SerialName("tax_percentage") val taxPercentage: Double,
    @SerialName("tax_free_threshold") val taxFreeThreshold: Double,
    val tariffs: List<TariffBand>
)

@Serializable
data class TariffBand(
    val min: Int,
    val max: Int,
    @SerialName("transfer_on_net") val transferOnNet: Double,
    @SerialName("transfer_off_net") val transferOffNet: Double,
    @SerialName("withdrawal_agent") val withdrawalAgent: Double? = null,
    @SerialName("is_fee_percentage") val isFeePercentage: Boolean = false,
    @SerialName("withdrawal_tax_min") val withdrawalTaxMin: Double? = null,
    @SerialName("withdrawal_tax_max") val withdrawalTaxMax: Double? = null,
    @SerialName("tax_is_flat_rate") val taxIsFlatRate: Boolean = false
)

object MockData {
    private const val SAFARICOM_JSON = """
    {
      "id": "mpesa_ke",
      "country": "Kenya",
      "provider_name": "M-Pesa",
      "currency": "KES",
      "has_government_tax": false,
      "tax_percentage": 0.0,
      "tax_free_threshold": 0.0,
      "tariffs": [
        { "min": 1, "max": 49, "transfer_on_net": 0.0, "transfer_off_net": 0.0, "withdrawal_agent": null },
        { "min": 50, "max": 100, "transfer_on_net": 0.0, "transfer_off_net": 0.0, "withdrawal_agent": 11.0 },
        { "min": 101, "max": 500, "transfer_on_net": 7.0, "transfer_off_net": 7.0, "withdrawal_agent": 29.0 },
        { "min": 501, "max": 1000, "transfer_on_net": 13.0, "transfer_off_net": 13.0, "withdrawal_agent": 29.0 },
        { "min": 1001, "max": 1500, "transfer_on_net": 23.0, "transfer_off_net": 23.0, "withdrawal_agent": 29.0 },
        { "min": 1501, "max": 2500, "transfer_on_net": 33.0, "transfer_off_net": 33.0, "withdrawal_agent": 29.0 },
        { "min": 2501, "max": 3500, "transfer_on_net": 53.0, "transfer_off_net": 53.0, "withdrawal_agent": 52.0 },
        { "min": 3501, "max": 5000, "transfer_on_net": 57.0, "transfer_off_net": 57.0, "withdrawal_agent": 69.0 },
        { "min": 5001, "max": 7500, "transfer_on_net": 78.0, "transfer_off_net": 78.0, "withdrawal_agent": 87.0 },
        { "min": 7501, "max": 10000, "transfer_on_net": 90.0, "transfer_off_net": 90.0, "withdrawal_agent": 115.0 },
        { "min": 10001, "max": 15000, "transfer_on_net": 100.0, "transfer_off_net": 100.0, "withdrawal_agent": 167.0 },
        { "min": 15001, "max": 20000, "transfer_on_net": 105.0, "transfer_off_net": 105.0, "withdrawal_agent": 185.0 },
        { "min": 20001, "max": 35000, "transfer_on_net": 108.0, "transfer_off_net": 108.0, "withdrawal_agent": 197.0 },
        { "min": 35001, "max": 50000, "transfer_on_net": 108.0, "transfer_off_net": 108.0, "withdrawal_agent": 278.0 },
        { "min": 50001, "max": 250000, "transfer_on_net": 108.0, "transfer_off_net": 108.0, "withdrawal_agent": 309.0 }
      ]
    }
    """
    private val json = Json { ignoreUnknownKeys = true }
    fun getConfig(): ProviderConfig = json.decodeFromString(SAFARICOM_JSON)
}
