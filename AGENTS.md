# Project Context: Mobile Money Calculator

## 1. Overview and Objective
The "Mobile Money Calculator" app is a financial utility tool aimed at the African market. The main objective is to provide transparency and accuracy in calculating transaction and withdrawal fees for Mobile Money platforms (e.g., M-Pesa). The app's standout feature is "Reverse Math": allowing the user to input the exact net amount the receiver needs to withdraw, while the app automatically calculates the total gross amount the sender must transfer to cover all sending and withdrawal fees.

**MVP Scope:** The application will initially be launched in Kenya, focusing exclusively on the M-Pesa company system and its specific tariff structures.

## 2. Technologies and Architecture
*   Language: Kotlin.
*   UI Framework: Jetpack Compose (Single-Activity Architecture).
*   Architectural Pattern: MVVM (Model-View-ViewModel).
*   Data Processing: kotlinx.serialization.
*   Strategic Approach: Offline-first (The app must function 100% without the internet, using a local mocked database during initialization).

## 3. Data Structure and Scalability
The project was designed to be "Future-Proof". The business logic must not contain hardcoded rules for specific countries. All tariff rules must stem from a single, universal JSON schema that maps:
*   Identification (Country, Currency, Provider Name).
*   Global Fiscal Configurations (Government taxes in percentage and tax-free thresholds).
*   Tariff Bands, containing minimum and maximum values, transfer fees (on-net and off-net), agent withdrawal fees, and min/max tax limits applicable to that specific band.

## 4. Core Features (Business Rules)
*   **Mode 1 - "I am sending" (Direct Calculation):** The user inputs how much they want to send. The app identifies the tariff band and displays the sending fee, the withdrawal fee, and the final total required.
*   **Mode 2 - "They must receive" (Reverse Math):** The user inputs the required net amount. The ViewModel must calculate the withdrawal fee for that amount. Then, it adds the net amount to the withdrawal fee and uses this subtotal to find the transfer fee, displaying the absolute total cost required from the sender.
*   **Reactive Limitations:** The app must visually warn the user if the entered amount exceeds the maximum limit allowed by the provider's rules (e.g., 250,000 KES for M-Pesa).

## 5. User Interface (Visual Requirements)
The screen must be optimized for fast usage in commercial environments (e.g., agent shops and kiosks):
*   Header containing the provider selection (e.g., M-Pesa - Kenya) and the main control to toggle between calculation Modes (Mode 1 vs Mode 2).
*   Large central display focusing on the readability of the inputted amount and presenting a clear breakdown of the involved fees.
*   Custom Numeric Keypad (Numpad). The use of the standard OS keyboard is strictly forbidden. The Numpad must occupy the bottom half of the screen with large buttons, facilitating fast, one-handed typing. The buttons must communicate directly with the ViewModel's state.

## 6. Data Schema & Mock (Strict Guidelines)
The agent MUST use the following Kotlin Data Classes and JSON structure for the MVP. Do not invent new keys.

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
```

### MVP Mock Data (M-Pesa Kenya):
The agent must embed this exact JSON string into an object/repository to act as the offline database:

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
  ]
}
```

## 7. The User Experience (Mobile Money Calculator)

### 7.1. The First Contact: Opening (Zero Friction)
*   **Context:** The user (a small merchant or someone paying a bill on the street) needs quick information. Internet is poor.
*   **What happens:** The user taps the app icon. Since we use an offline-first architecture, the app opens instantly.
*   **The Screen:** There is no login screen or complex menus. The app opens directly to the calculator. The screen is divided in half: the top is a clean Display, and the bottom is a giant, custom-made Numeric Keypad (Numpad).

### 7.2. Traditional Flow: I am sending (Direct Calculation)
*   **The Scenario:** The user wants to send money to a relative but needs to know how much the fees will deduct from their balance.
*   **The Action:** The toggle switch at the top is set to "I am sending". The user types 1500 on the Numpad.
*   **The Reaction:** In real-time, with each digit entered, the display updates. No need to press an equals or calculate button. The app shows:
    *   Amount: 1,500 KES
    *   Send Fee: 23 KES
    *   Withdrawal Fee: 29 KES
    *   Total Deducted: 1,523 KES
*   **Perceived Value:** Instant transparency. The user knows exactly how much it will cost.

### 7.3. The Key Differentiator: They must receive (Reverse Math)
*   **The Scenario:** This is the killer feature. The user is buying materials, and the seller says: "It costs 10,000 KES net. You pay the withdrawal fee." Usually, people do mental math, look at dirty printed tables, and end up sending the wrong amount.
*   **The Action:** The user flips the toggle at the top to "They must receive". They type 10000 on the Numpad.
*   **The Magic:** The app understands that 10,000 is the net amount. It checks the table and finds that to withdraw 10,000, the seller will pay a 115 KES fee. The app adds this (10,115) and calculates that the send fee for 10,115 is 100 KES.
*   **The Display shows:**
    *   Net Amount: 10,000 KES
    *   Withdrawal Fee to Cover: 115 KES
    *   Send Fee: 100 KES
    *   You must send: 10,115 KES
    *   Total Deducted: 10,215 KES
*   **Perceived Value:** No more arguments at the counter and no more losing money by over-sending "just in case." The complex calculation is solved in a second.

### 7.4. Resilience and Error Prevention
*   **The Scenario:** The user gets carried away and tries to type a 300,000 KES transfer, forgetting the M-Pesa limit is 250,000.
*   **The Action:** They type the numbers on the keypad.
*   **The Reaction:** The display prevents the app from crashing. As soon as the amount exceeds 250,000, the result numbers are hidden, and a clear red message appears: "Maximum transaction limit is 250,000 KES". The user presses the "C" (Clear) button on the keypad, and the display instantly resets.

### 7.5. Tactile Navigation
*   **The Custom Keypad:** Since the standard Android keyboard is forbidden (it is small and requires two taps to open and close), the app's keypad occupies half the screen. The buttons are large, allowing a van driver or a shopkeeper to type values quickly using just one hand (their thumb) without making mistakes.
