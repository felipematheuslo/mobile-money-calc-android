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
    val tariffs: List<TariffBand>,
    @SerialName("pochi_tariffs") val pochiTariffs: List<TariffBand>? = null
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
    private const val PROVIDERS_JSON = """
    [
        {
          "id": "mpesa_ke",
          "country": "Kenya",
          "provider_name": "Safaricom M-Pesa",
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
          ],
          "pochi_tariffs": [
            { "min": 1, "max": 49, "transfer_on_net": 0.0, "transfer_off_net": 0.0, "withdrawal_agent": null },
            { "min": 50, "max": 100, "transfer_on_net": 0.0, "transfer_off_net": 0.0, "withdrawal_agent": null },
            { "min": 101, "max": 200, "transfer_on_net": 0.0, "transfer_off_net": 0.0, "withdrawal_agent": null },
            { "min": 201, "max": 500, "transfer_on_net": 7.0, "transfer_off_net": 7.0, "withdrawal_agent": null },
            { "min": 501, "max": 1000, "transfer_on_net": 13.0, "transfer_off_net": 13.0, "withdrawal_agent": null },
            { "min": 1001, "max": 1500, "transfer_on_net": 23.0, "transfer_off_net": 23.0, "withdrawal_agent": null },
            { "min": 1501, "max": 2500, "transfer_on_net": 33.0, "transfer_off_net": 33.0, "withdrawal_agent": null },
            { "min": 2501, "max": 250000, "transfer_on_net": 50.0, "transfer_off_net": 50.0, "withdrawal_agent": null }
          ]
        },
        {
          "id": "mtn_ug",
          "country": "Uganda",
          "provider_name": "MTN MoMo",
          "currency": "UGX",
          "has_government_tax": true,
          "tax_percentage": 0.5,
          "tax_free_threshold": 0.0,
          "tariffs": [
            { "min": 500, "max": 2500, "transfer_on_net": 100.0, "transfer_off_net": 100.0, "withdrawal_agent": 330.0 },
            { "min": 2501, "max": 5000, "transfer_on_net": 100.0, "transfer_off_net": 100.0, "withdrawal_agent": 440.0 },
            { "min": 5001, "max": 15000, "transfer_on_net": 500.0, "transfer_off_net": 500.0, "withdrawal_agent": 700.0 },
            { "min": 15001, "max": 30000, "transfer_on_net": 500.0, "transfer_off_net": 500.0, "withdrawal_agent": 880.0 },
            { "min": 30001, "max": 45000, "transfer_on_net": 500.0, "transfer_off_net": 500.0, "withdrawal_agent": 1210.0 },
            { "min": 45001, "max": 60000, "transfer_on_net": 500.0, "transfer_off_net": 500.0, "withdrawal_agent": 1500.0 },
            { "min": 60001, "max": 125000, "transfer_on_net": 1000.0, "transfer_off_net": 1000.0, "withdrawal_agent": 1925.0 },
            { "min": 125001, "max": 250000, "transfer_on_net": 1000.0, "transfer_off_net": 1000.0, "withdrawal_agent": 3575.0 },
            { "min": 250001, "max": 500000, "transfer_on_net": 1000.0, "transfer_off_net": 1000.0, "withdrawal_agent": 7000.0 },
            { "min": 500001, "max": 1000000, "transfer_on_net": 1500.0, "transfer_off_net": 1500.0, "withdrawal_agent": 12500.0 },
            { "min": 1000001, "max": 2000000, "transfer_on_net": 2000.0, "transfer_off_net": 2000.0, "withdrawal_agent": 15000.0 },
            { "min": 2000001, "max": 4000000, "transfer_on_net": 2000.0, "transfer_off_net": 2000.0, "withdrawal_agent": 18000.0 },
            { "min": 4000001, "max": 5000000, "transfer_on_net": 2000.0, "transfer_off_net": 2000.0, "withdrawal_agent": 20000.0 }
          ],
          "pochi_tariffs": null
        },
        {
          "id": "airtel_ug",
          "country": "Uganda",
          "provider_name": "Airtel Money",
          "currency": "UGX",
          "has_government_tax": true,
          "tax_percentage": 0.5,
          "tax_free_threshold": 0.0,
          "tariffs": [
            { "min": 500, "max": 2500, "transfer_on_net": 100.0, "transfer_off_net": 100.0, "withdrawal_agent": 330.0 },
            { "min": 2501, "max": 5000, "transfer_on_net": 100.0, "transfer_off_net": 100.0, "withdrawal_agent": 440.0 },
            { "min": 5001, "max": 15000, "transfer_on_net": 500.0, "transfer_off_net": 500.0, "withdrawal_agent": 700.0 },
            { "min": 15001, "max": 30000, "transfer_on_net": 500.0, "transfer_off_net": 500.0, "withdrawal_agent": 880.0 },
            { "min": 30001, "max": 45000, "transfer_on_net": 500.0, "transfer_off_net": 500.0, "withdrawal_agent": 1210.0 },
            { "min": 45001, "max": 60000, "transfer_on_net": 500.0, "transfer_off_net": 500.0, "withdrawal_agent": 1500.0 },
            { "min": 60001, "max": 125000, "transfer_on_net": 1000.0, "transfer_off_net": 1000.0, "withdrawal_agent": 1925.0 },
            { "min": 125001, "max": 250000, "transfer_on_net": 1000.0, "transfer_off_net": 1000.0, "withdrawal_agent": 3575.0 },
            { "min": 250001, "max": 500000, "transfer_on_net": 1000.0, "transfer_off_net": 1000.0, "withdrawal_agent": 7000.0 },
            { "min": 500001, "max": 1000000, "transfer_on_net": 1500.0, "transfer_off_net": 1500.0, "withdrawal_agent": 12500.0 },
            { "min": 1000001, "max": 2000000, "transfer_on_net": 2000.0, "transfer_off_net": 2000.0, "withdrawal_agent": 15000.0 },
            { "min": 2000001, "max": 3000000, "transfer_on_net": 2000.0, "transfer_off_net": 2000.0, "withdrawal_agent": 18000.0 },
            { "min": 3000001, "max": 4000000, "transfer_on_net": 2000.0, "transfer_off_net": 2000.0, "withdrawal_agent": 18000.0 },
            { "min": 4000001, "max": 5000000, "transfer_on_net": 2000.0, "transfer_off_net": 2000.0, "withdrawal_agent": 18000.0 }
          ],
          "pochi_tariffs": null
        },
        {
          "id": "mpesa_tz",
          "country": "Tanzania",
          "provider_name": "Vodacom M-Pesa",
          "currency": "TZS",
          "has_government_tax": false,
          "tax_percentage": 0.0,
          "tax_free_threshold": 0.0,
          "tariffs": [
            { "min": 1, "max": 999, "transfer_on_net": 10.0, "transfer_off_net": 15.0, "withdrawal_agent": 185.0 },
            { "min": 1000, "max": 1999, "transfer_on_net": 30.0, "transfer_off_net": 35.0, "withdrawal_agent": 360.0 },
            { "min": 2000, "max": 2999, "transfer_on_net": 30.0, "transfer_off_net": 45.0, "withdrawal_agent": 410.0 },
            { "min": 3000, "max": 3999, "transfer_on_net": 50.0, "transfer_off_net": 68.0, "withdrawal_agent": 614.0 },
            { "min": 4000, "max": 4999, "transfer_on_net": 60.0, "transfer_off_net": 81.0, "withdrawal_agent": 677.0 },
            { "min": 5000, "max": 6999, "transfer_on_net": 130.0, "transfer_off_net": 180.0, "withdrawal_agent": 1004.0 },
            { "min": 7000, "max": 9999, "transfer_on_net": 150.0, "transfer_off_net": 180.0, "withdrawal_agent": 1056.0 },
            { "min": 10000, "max": 14999, "transfer_on_net": 350.0, "transfer_off_net": 495.0, "withdrawal_agent": 1552.0 },
            { "min": 15000, "max": 19999, "transfer_on_net": 360.0, "transfer_off_net": 540.0, "withdrawal_agent": 1645.0 },
            { "min": 20000, "max": 29999, "transfer_on_net": 380.0, "transfer_off_net": 612.0, "withdrawal_agent": 2156.0 },
            { "min": 30000, "max": 39999, "transfer_on_net": 400.0, "transfer_off_net": 675.0, "withdrawal_agent": 2201.0 },
            { "min": 40000, "max": 49999, "transfer_on_net": 410.0, "transfer_off_net": 720.0, "withdrawal_agent": 2769.0 },
            { "min": 50000, "max": 99999, "transfer_on_net": 720.0, "transfer_off_net": 1125.0, "withdrawal_agent": 3273.0 },
            { "min": 100000, "max": 199999, "transfer_on_net": 1000.0, "transfer_off_net": 1440.0, "withdrawal_agent": 4357.0 },
            { "min": 200000, "max": 299999, "transfer_on_net": 1200.0, "transfer_off_net": 1710.0, "withdrawal_agent": 6121.0 },
            { "min": 300000, "max": 399999, "transfer_on_net": 1500.0, "transfer_off_net": 2070.0, "withdrawal_agent": 7338.0 },
            { "min": 400000, "max": 499999, "transfer_on_net": 1500.0, "transfer_off_net": 2250.0, "withdrawal_agent": 7932.0 },
            { "min": 500000, "max": 599999, "transfer_on_net": 2200.0, "transfer_off_net": 2880.0, "withdrawal_agent": 8745.0 },
            { "min": 600000, "max": 699999, "transfer_on_net": 3300.0, "transfer_off_net": 3870.0, "withdrawal_agent": 9532.0 },
            { "min": 700000, "max": 799999, "transfer_on_net": 3300.0, "transfer_off_net": 3870.0, "withdrawal_agent": 9700.0 },
            { "min": 800000, "max": 899999, "transfer_on_net": 3500.0, "transfer_off_net": 5400.0, "withdrawal_agent": 9750.0 },
            { "min": 900000, "max": 1000000, "transfer_on_net": 3500.0, "transfer_off_net": 5400.0, "withdrawal_agent": 9776.0 },
            { "min": 1000001, "max": 3000000, "transfer_on_net": 4800.0, "transfer_off_net": 5400.0, "withdrawal_agent": 9875.0 },
            { "min": 3000001, "max": 5000000, "transfer_on_net": 4800.0, "transfer_off_net": 5400.0, "withdrawal_agent": 12000.0 }
          ],
          "pochi_tariffs": null
        }
    ]
    """
    private val json = Json { ignoreUnknownKeys = true }
    fun getProviders(): List<ProviderConfig> = json.decodeFromString(PROVIDERS_JSON)
}

