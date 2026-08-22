# Project Context: Mobile Money Calculator

## 1. Overview and Objective
The "Mobile Money Calculator" app is a financial utility tool designed specifically for the African market (starting with Kenya). The main objective is to provide 100% offline transparency and accuracy in calculating transaction and withdrawal fees for Mobile Money platforms (primarily Safaricom M-Pesa).

**Key Differentiator:** "Reverse Math" — allowing the user to enter the exact net amount the recipient needs in cash, while the app automatically computes the gross amount to send, fully covering all sending and agent withdrawal fees.

## 2. Technologies and Architecture
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Single-Activity Architecture)
*   **State & Architecture:** MVVM (Model-View-ViewModel) + StateFlow
*   **Serialization:** `kotlinx.serialization`
*   **Approach:** 100% Offline-first (instant cold boot, zero data usage, embedded JSON repository)
*   **Persistence:** `SharedPreferences` for remembering the selected country/provider across app launches
*   **Target Device Profile:** Optimized for entry-level Android devices (1GB–3GB RAM, Android Go)
*   **Compatibility:** `minSdk = 24` (Android 7.0+), covering >97% of active African smartphones

## 3. Core Features & The 4 Calculation Intents

The app covers all 4 fundamental real-world Mobile Money consumer flows:

### Intent 1: "Send Only" (Wallet to Wallet)
*   **Use Case:** Transferring funds directly to another person's digital wallet (e.g., paying a friend, splitting a bill, or sending funds they will spend digitally).
*   **Outputs:** Amount to Send + Transfer Fee $\rightarrow$ **Total Deducted**.

### Intent 2: "Send for Cash" (Reverse Math / Cash-Out)
*   **Use Case:** The recipient needs physical cash in hand from an M-Pesa agent and the sender agrees to cover all intermediate fees.
*   **Computation:**
    1. Lookup agent `withdrawalFee` for `netAmount`.
    2. Compute `subtotal = netAmount + withdrawalFee`.
    3. Lookup transfer `sendFee` for `subtotal`.
    4. Compute `totalDeducted = subtotal + sendFee`.
*   **Outputs:** Net Cash Needed + Withdrawal Fee to Cover + Send Fee + **You Must Transfer** (`subtotal`) $\rightarrow$ **Total Deducted**.

### Intent 3: "Withdraw Only" (Agent Cash-Out)
*   **Use Case:** The user is standing at an agent kiosk withdrawing cash from their own account and wants to know the agent fee and final balance deduction.
*   **Outputs:** Cash to Withdraw + Agent Withdrawal Fee $\rightarrow$ **Total from Balance**.

### Intent 4: "Pay Pochi" (Pochi La Biashara Merchant Payments)
*   **Use Case:** Paying an informal merchant or street vendor via Safaricom's discounted Pochi wallet.
*   **Outputs:** Payment Amount + Discounted Merchant Fee (capped at 50 KES) $\rightarrow$ **Total Deducted**.

### Reactive Limit Protection
If the entered amount exceeds the provider's max transaction limit (e.g., 250,000 KES), numerical outputs are safely hidden and replaced with an instant, clear warning message: *"Maximum transaction limit is 250,000 KES"*.

## 4. User Interface & Design System ("Lightweight Premium")

The UI is optimized for fast, one-handed operation in high-paced commercial environments (kiosks, markets, street vendors):

*   **Custom Full-Screen Numpad:** Standard Android OS keyboard is strictly disabled. Large squarcle keys (`RoundedCornerShape(16.dp)`) take up the bottom ~45% of the screen for instant thumb reach.
*   **Brand Identity Colors:**
    *   Primary Accent: **Safaricom Green (`#00B365`)**
    *   Background: Clean cool off-white (`#F7F9FA`)
    *   Text: Deep charcoal (`#191C1E`) for high outdoor sunlight readability
    *   Clear Button: Soft error pill (`#FFFFEBEE` / `#E53935`)
*   **Hardware-Accelerated Micro-Animations:**
    *   `AnimatedContent` for smooth sliding transitions on total figures.
    *   `animateContentSize` on breakdown cards when toggling between Person and Pochi modes.
    *   **Zero external assets (0 KB Lottie/GIF overhead)** to preserve RAM and battery.

## 5. Universal Data Schema & Mock Data

### Kotlin Data Classes
```kotlin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
```

### Embedded Mock Data (Kenya M-Pesa & Pochi La Biashara)
```json
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
}
```

## 6. Strategic Decisions & Product Roadmap

*   **Multi-Country Plug & Play:** The app is pre-configured with embedded schemas for:
    *   🇰🇪 **Kenya:** Safaricom M-Pesa (with Pochi La Biashara support).
    *   🇺🇬 **Uganda:** MTN Mobile Money & Airtel Money (with 0.5% statutory government excise tax engine).
    *   🇹🇿 **Tanzania:** Vodacom M-Pesa (TZS currency formatting and high-volume transaction bands).
*   **Dynamic UI Adaptability:** If a network provider does not feature a merchant wallet like Pochi, the UI dynamically collapses to the 3 standard options without empty space or error states.
*   **Government Tax Calculation Engine:** Automatically checks `hasGovernmentTax` and applies percentage-based or flat-rate taxes above thresholds, displaying them transparently in the breakdown.
*   **ATM Withdrawal Tariffs:** Excluded by design to maintain zero-friction simplicity for the 99% peer-to-peer / kiosk cash withdrawal use case.
*   **Zero-Internet Guarantee:** All computations happen locally with zero latency, zero tracking overhead, and zero dependence on cellular data connectivity.
