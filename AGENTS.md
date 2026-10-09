# Project Context: Mobile Money Calculator

## 1. Overview and Objective
The "Mobile Money Calculator" app is a financial utility tool designed specifically for the African market (starting with Kenya). The main objective is to provide 100% offline transparency and accuracy in calculating transaction and withdrawal fees for Mobile Money platforms (primarily Safaricom M-Pesa).

**Key Differentiator:** "Reverse Math" — allowing the user to enter the exact net amount the recipient needs in cash, while the app automatically computes the gross amount to send, fully covering all sending and agent withdrawal fees.
## 2. Technologies and Architecture
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Single-Activity Architecture)
*   **State & Architecture:** MVVM (Model-View-ViewModel) + StateFlow
*   **Monetization & Ads:** Google Mobile Ads SDK (AdMob) with dedicated zero-CLS reserved layout space
*   **Internationalization (i18n):** 100% extracted UI strings in `strings.xml` for effortless multi-language localization
*   **Serialization:** `kotlinx.serialization`
*   **Approach:** 100% Offline-first (instant cold boot, zero data usage, embedded JSON repository)
*   **Persistence:** `SharedPreferences` for remembering the selected country/provider across app launches
*   **Target Device Profile:** Optimized for entry-level Android devices (1GB–3GB RAM, Android Go)
*   **Compatibility:** `minSdk = 24` (Android 7.0+), covering >97% of active African smartphones
*   **Testing:** Comprehensive JUnit 4 test suite (`TariffCalculationTest`) covering all calculation intents, tax thresholds, and provider boundaries.

## 3. Core Features & The 4 Calculation Intents

The app covers all 4 fundamental real-world Mobile Money consumer flows:

### Intent 1: "Send Only" (Wallet to Wallet)
*   **Use Case:** Transferring funds directly to another person's digital wallet (e.g., paying a friend, splitting a bill, or sending funds they will spend digitally).
*   **Scenario Explanation:** *"Sender pays the transfer fee; recipient receives the net amount in their digital wallet."*
*   **Outputs:** Amount to Send + Transfer Fee $\rightarrow$ **Total Deducted**.

### Intent 2: "Send for Cash" (Reverse Math / Cash-Out)
*   **Use Case:** The recipient needs physical cash in hand from an M-Pesa/MoMo/Airtel agent and the sender agrees to cover all intermediate fees.
*   **Scenario Explanation:** *"Sender covers both transfer fee & agent cash-out fee so recipient receives full cash in hand."*
*   **Computation:**
    1. Lookup agent `withdrawalFee` for `netAmount`.
    2. Compute statutory government tax if applicable (`netAmount * taxPercentage`).
    3. Compute `subtotal = netAmount + withdrawalFee + taxAmount`.
    4. Lookup transfer `sendFee` for `subtotal`.
    5. Compute `totalDeducted = subtotal + sendFee`.
*   **Outputs:** Net Cash Needed + Withdrawal Fee to Cover + Govt Tax (if statutory) + Send Fee + **You Must Transfer** (`subtotal`) $\rightarrow$ **Total Deducted**.

### Intent 3: "Withdraw Only" (Agent Cash-Out)
*   **Use Case:** The user is standing at an agent kiosk withdrawing cash from their own account and wants to know the agent fee and final balance deduction.
*   **Scenario Explanation:** *"Agent cash-out fee and taxes are deducted directly from your mobile wallet balance."*
*   **Outputs:** Cash to Withdraw + Agent Withdrawal Fee (+ Govt Tax) $\rightarrow$ **Total from Balance**.

### Intent 4: "Pay Pochi" (Pochi La Biashara Merchant Payments)
*   **Use Case:** Paying an informal merchant or street vendor via Safaricom's discounted Pochi wallet.
*   **Scenario Explanation:** *"Discounted merchant transfer rates capped at a flat 50 KES fee (no withdrawal fee)."*
*   **Outputs:** Payment Amount + Discounted Merchant Fee (capped at 50 KES) $\rightarrow$ **Total Deducted**.

### Reactive Limit Protection
The app continuously monitors regulatory and provider transaction bounds:
* **Maximum Transaction Limit:** If the entered amount exceeds the provider's max transaction cap (e.g., `250,000 KES` for Safaricom or `5,000,000 UGX` for MTN/Airtel), numeric outputs are hidden and replaced with an instant warning: *"Maximum transaction limit is [Cap] [Currency]"*.
* **Minimum Limit Protection:** If an amount is below the provider's minimum allowed transfer or agent withdrawal threshold (e.g., `500 UGX` in Uganda or `50 KES` for Kenyan cash-outs), a clear warning is displayed: *"Minimum withdrawal amount is [Min] [Currency]"* or *"Minimum transaction amount is [Min] [Currency]"*.

## 4. User Interface, Monetization & Dynamic Operator Theming ("Lightweight Premium")

The UI is built with a banking-grade visual hierarchy optimized for fast, one-handed operation in high-paced commercial environments (kiosks, markets, street vendors):

*   **Provider Header Card (`ProviderHeaderCard`):** Modern top header displaying the active carrier brand badge, operator name, country flag, currency (`Currency: %s`), and instant modal bottom sheet selector.
    *   **Modal Bottom Sheet Provider Selector:** Features dedicated carrier cards with brand styling, checkmark selection indicators, an expanding *"More operators and countries coming soon"* informational card, and a verified tariff timestamp footer (*"Tariffs updated: August 2026"*).
*   **Single-Row Mode Selector (`ModeSingleRowSelector`):** Streamlined horizontal single-row mode switcher with smooth sliding pill indicators and dynamic carrier color accents.
*   **Smart Receipt Card (`SmartReceiptCard`):**
    *   Sleek dark slate aesthetic (`#1E293B` / `#0F172A`) with high outdoor sunlight contrast.
    *   Distinct formatted input display section with real-time scenario subtitle explanations.
    *   Animated line item breakdown with tabular typography.
    *   Fixed anchored grand total deduction box with high-contrast text and zero mode-switch jumpiness.
*   **Integrated AdMob Banner (`BannerAd`):**
    *   Placed between the receipt card and numpad with dedicated breathing room and fixed height (`50.dp` / standard banner).
    *   Prevents Cumulative Layout Shift (CLS) during ad loads.
    *   Graceful placeholder support during design previews and network latency.
*   **Custom Full-Screen Numpad:** Standard Android OS keyboard is strictly disabled. Large squarcle keys (`RoundedCornerShape(16.dp)`) take up the bottom ~40% of the screen for instant thumb reach. Zero taps to open/close keyboard.
*   **Dynamic Multi-Carrier Brand Theming:**
    *   🇰🇪 **Safaricom M-Pesa:** Safaricom Green (`#00B365`) with white button text and green accents.
    *   🇺🇬 **MTN Mobile Money:** MTN Sunshine Yellow (`#FFCC00`) with high-contrast dark charcoal button text (`#191C1E`) and rich amber text highlights (`#C67D00`).
    *   🇺🇬 **Airtel Money:** Airtel Red (`#ED1C24`) with white button text.
    *   Background: Clean cool off-white (`#F7F9FA`)
    *   Text: Deep charcoal (`#191C1E`) for high outdoor sunlight readability
    *   Clear Button: Soft error pill (`#FFFFEBEE` / `#E53935`)
*   **Adaptive 5-Row Layout Engine:** Dynamically renders between 2 to 5 breakdown rows (accommodating statutory government taxes, agent fees, transfer subtotals, and final totals) without vertical clipping or layout shifts.
*   **Edge-to-Edge & System Navigation Bar Insets:** Complete support for Android edge-to-edge window insets, fixing 3-button system navigation bar contrast and gesture pill padding.
*   **Hardware-Accelerated Micro-Animations:**
    *   `AnimatedContent` for smooth sliding transitions on total figures.
    *   `animateContentSize` on breakdown cards when toggling between modes.
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

## 6. Automated Testing Suite

The repository includes a comprehensive JUnit test suite in `app/src/test/java/com/felipelaurindo/mobilemoneycalc/TariffCalculationTest.kt`:
*   **Safaricom Kenya:** Validates P2P tiered transfer fees, reverse math agent cash-out calculations across all tiers, agent cash withdrawals, and Pochi La Biashara flat 50 KES cap.
*   **MTN Uganda:** Validates P2P transfers, statutory 0.5% government tax rounding, and high-value tiers up to 5,000,000 UGX.
*   **Airtel Money Uganda:** Validates on-net vs off-net rates, agent cash-outs, 0.5% excise tax computation, and full reverse math flows.
*   **Edge Cases:** Verifies boundary conditions (minimum amount threshold, maximum transaction cap, zero/negative inputs, and exact tier border transitions).

## 7. Strategic Decisions & Product Roadmap

*   **Multi-Country Plug & Play:** The app is pre-configured with embedded schemas for:
    *   🇰🇪 **Kenya:** Safaricom M-Pesa ([Official Source](https://www.safaricom.co.ke/main-mpesa/m-pesa-for-you/tariffs-limits/consumer-tariffs-limits) | [Rules](SAFARICOM_KENYA_RULES.md)).
    *   🇺🇬 **Uganda:** MTN Mobile Money ([Official Source](https://www.mtn.co.ug/tariffs/mobile-money-tariffs/) | [Rules](MTN_UGANDA_RULES.md)) & Airtel Money ([Official Source](https://www.airtelmoney.ug/transaction_fees) | [Rules](AIRTEL_UGANDA_RULES.md)) with 0.5% statutory government excise tax engine.
    *   *Upcoming additions:* 🇹🇿 Tanzania (Vodacom M-Pesa), 🇬🇭 Ghana (MTN/Vodafone), 🇷🇼 Rwanda (MTN/Airtel).
*   **Dynamic UI Adaptability:** If a network provider does not feature a merchant wallet like Pochi, the UI dynamically collapses to the 3 standard options without empty space or error states.
*   **Government Tax Calculation Engine:** Automatically checks `hasGovernmentTax` and applies percentage-based or flat-rate taxes above thresholds, displaying them transparently in the breakdown.
*   **ATM Withdrawal Tariffs:** Excluded by design to maintain zero-friction simplicity for the 99% peer-to-peer / kiosk cash withdrawal use case.
*   **Zero-Internet Guarantee:** All computations happen locally with zero latency, zero tracking overhead, and zero dependence on cellular data connectivity.

## 8. Mandatory Agent Rules & Tariff Maintenance

> [!IMPORTANT]
> ### 🚨 TARIFF UPDATE PROTOCOL FOR AI AGENTS
> Whenever modifying, adding, or auditing tariff bands, provider schemas, or tax rules in `MockData` / `tariffs.json`:
> 1. **Update Tariff Freshness Strings:** AI agents **MUST ALWAYS** update the tariff update date strings in `app/src/main/res/values/strings.xml`:
>    - `tariffs_last_updated` (e.g. `Tariffs updated: August 2026`)
>    - `tariffs_last_updated_short` (e.g. `Aug 2026`)
> 2. **Maintain 100% i18n:** Never hardcode user-visible text in Kotlin composables. Always declare and reference keys in `strings.xml`.
> 3. **Run Automated Test Suite:** Execute `./gradlew testDebugUnitTest` immediately to verify that no reverse math calculations or threshold limits have regressed.

## 9. App Store Optimization (ASO) & Play Store Metadata

This project strictly decouples the device app name from the Play Store ASO title to maintain both brand clarity and search engine visibility.
- **Device App Name (`app_name` in strings.xml):** `MomoCalc` (Ensures clean, untruncated launcher icon).
- **Play Store Title:** `Fee Calc for M-Pesa & MoMo` (Rich in keywords, compliant with Google\'s impersonation policy using the \"for\" preposition).

### English (Default - en-US)
* **Title (30 chars):** Fee Calc for M-Pesa & MoMo
* **Short Description (80 chars):** Calculate M-Pesa, MTN MoMo & Airtel transfer and agent cash-out fees offline.
* **Full Description:**
```text
Never guess or argue about Mobile Money charges again! MomoCalc is the fast, accurate, and 100% offline Mobile Money fee calculator built specifically for East Africa (Kenya & Uganda).

Whether you are sending money to family, buying groceries with merchant payments, or withdrawing cash at an agent kiosk, MomoCalc instantly computes exact transfer fees, cash-out rates, and statutory government taxes with zero internet needed.

🌟 THE 4 SMART CALCULATION MODES

1. 🤝 "SEND FOR CASH" (Reverse Math Calculator) — Killer Feature
Need your recipient to receive the EXACT net amount in physical cash from an agent?
• Just enter the cash amount they need in hand (e.g. 1,000 KES or 50,000 UGX).
• MomoCalc automatically computes the agent cash-out fee, adds statutory excise taxes (e.g. 0.5% in Uganda), and determines the exact transfer amount you must send so they withdraw without losing a single cent.

2. 📤 "SEND ONLY" (Wallet to Wallet Transfer)
Calculate the exact deduction when transferring funds directly to another person's mobile wallet (on-net or off-net).

3. 💵 "WITHDRAW ONLY" (Agent Cash-Out)
Standing at an agent counter or kiosk? Know the exact agent withdrawal fee and total balance needed before you transact.

4. 🏪 "PAY POCHI" (Merchant Payments)
Calculate discounted merchant transfer rates for Safaricom Kenya's Pochi La Biashara (capped at a flat 50 KES fee).

🌍 SUPPORTED COUNTRIES & OPERATORS
All tariff bands and tax thresholds are pre-loaded and regularly updated (verified: October 2026):

🇰🇪 KENYA
• Safaricom M-Pesa (P2P Transfers, Agent Cash-Out, ATM, and Pochi La Biashara)

🇺🇬 UGANDA
• MTN Mobile Money (MoMo) — includes statutory 0.5% government excise tax calculation
• Airtel Money Uganda — includes on-net/off-net rates and agent withdrawal taxes

*Upcoming additions to more African countries coming soon.

⚡ KEY HIGHLIGHTS & FEATURES
• 🛡️ Real-Time Transaction Limit Alerts: Instant warning if an amount exceeds regulatory maximum caps (e.g. 250,000 KES or 5,000,000 UGX) or falls below agent kiosk minimums.
• ⌨️ Large Ergonomic Numpad: Custom numeric keypad covering the bottom of your screen for lightning-fast, one-handed operation. No waiting for the system keyboard.
• 🚀 100% Offline & Private: No internet data required, zero account sign-ups, zero tracking, and instant cold launch in under 100ms.
• 🔋 Lightweight & Battery Friendly: Highly optimized for Android Go and entry-level smartphones.

Download MomoCalc today and take total control of your Mobile Money transaction fees!

DISCLAIMER:
MomoCalc is an independent utility tool designed for fee calculations. It is not affiliated with, endorsed, or sponsored by Safaricom, MTN, Airtel, or any mobile network operator. All trademarks and brand names belong to their respective owners.
```

### Swahili / Kiswahili (sw-KE)
* **Title (30 chars):** Kikokotoo cha M-Pesa & MoMo
* **Short Description (80 chars):** Kokotoa makato ya kutoa na kutuma pesa taslimu kwa M-Pesa, MTN na Airtel.
* **Full Description:**
```text
Usikisie au kubishana tena kuhusu makato ya kutuma na kutoa pesa! MomoCalc ni kikokotoo cha haraka, sahihi, na kinachofanya kazi 100% bila intaneti, kikiwa kimetengenezwa maalum kwa ajili ya Afrika Mashariki (Kenya na Uganda).

Iwe unatuma pesa kwa familia, unalipa wafanyabiashara kupitia Pochi, au unatoa pesa taslimu kwa wakala, MomoCalc inakokotoa papo hapo gharama za kutuma, makato ya kutoa, na kodi za serikali bila kuhitaji intaneti (offline).

🌟 NJIA 4 ZA KUKOKOTOA KIJANJA

1. 🤝 "KUTUMA KWA TASLIMU" (Hesabu za Kurudi Nyuma) — Kipengele Muhimu
Unahitaji mpokeaji apate kiasi KAMILI cha pesa taslimu mkononi kutoka kwa wakala?
• Weka tu kiasi cha pesa taslimu anachohitaji mkononi (k.m., 1,000 KES au 50,000 UGX).
• MomoCalc itakokotoa makato ya wakala, itaongeza kodi ya serikali, na kukuambia kiasi halisi cha kutuma ili atoe bila kupoteza hata senti moja.

2. 📤 "KUTUMA TU" (Kutoka Simu hadi Simu)
Kokotoa makato sahihi unapohamisha pesa moja kwa moja kwenye simu ya mtu mwingine.

3. 💵 "KUTOA TASLIMU" (Kutoa kwa Wakala)
Uko kwa wakala? Jua makato kamili ya kutoa na salio linalohitajika kwenye akaunti kabla ya kufanya muamala.

4. 🏪 "KULIPA POCHI" (Kwa Wafanyabiashara)
Kokotoa gharama zilizopunguzwa ukitumia Pochi La Biashara kutoka Safaricom Kenya (kikomo cha juu cha makato ni 50 KES).

🌍 NCHI NA MITANDAO INAYOSAIDIWA
Viwango vyote vya ushuru na kodi vimewekwa tayari na vinasasishwa (Imethibitishwa: Oktoba 2026):

🇰🇪 KENYA
• Safaricom M-Pesa

🇺🇬 UGANDA
• MTN Mobile Money (MoMo) — pamoja na kodi ya serikali ya 0.5%
• Airtel Money Uganda

*Nchi zaidi za Kiafrika zitaongezwa hivi karibuni.

⚡ VIPENGELE MUHIMU
• 🛡️ Maonyo ya Viwango vya Muamala: Utapata onyo papo hapo kama kiasi kikizidi kikomo (k.m., 250,000 KES) au kikiwa chini ya kiwango cha chini cha kutoa pesa.
• ⌨️ Kitufe Kikubwa cha Namba: Kitufe maalum kwenye skrini kwa matumizi ya haraka ya mkono mmoja, bila kusubiri kibodi ya simu kufunguka.
• 🚀 100% Haina Intaneti & Kwa Faragha: Huhitaji data, huhitaji kufungua akaunti, na hakuna taarifa zako kufuatiliwa (zero tracking).
• 🔋 Nyepesi kwa Betri: Imetengenezwa maalum kwa simu za Android Go na simu za kawaida.

Pakua MomoCalc leo na udhibiti kikamilifu makato yako ya Mobile Money!

KANUSHO:
MomoCalc ni zana huru iliyoundwa kwa ajili ya kukokotoa gharama na makato. Haina uhusiano, kuidhinishwa, au kufadhiliwa na Safaricom, MTN, Airtel, au mtandao wowote wa simu. Majina ya chapa ni miliki ya wamiliki wao.
```
