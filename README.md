# 📱 Mobile Money Calculator (East Africa)

A high-performance, **100% offline**, zero-friction financial utility app built for the African market (Kenya, Uganda, Tanzania). 

The app eliminates arguments at payment counters and eliminates mental math errors by instantly calculating sending and withdrawal fees in real time.

---

## 🌟 The 4 Core Calculation Modes

### 1. 📤 "Send Only" (Wallet to Wallet)
* **The Situation:** You want to send funds directly to another person's digital wallet (for bill pay, school fees, or digital balance).
* **Scenario Context:** Sender pays the transfer fee; recipient receives the net amount in their digital wallet.
* **The Math:** `Amount to Send + Transfer Fee = Total Deducted`.

### 2. 🤝 "Send for Cash" (Reverse Math) — Killer Feature
* **The Situation:** A recipient needs an exact amount of cash in hand from an M-Pesa/MoMo/Airtel agent (e.g. 10,000 KES / 100,000 UGX) and you agree to cover all fees.
* **Scenario Context:** Sender covers both transfer fee & agent cash-out fee so recipient receives full cash in hand.
* **The Math:** Automatically calculates the agent withdrawal fee, computes government taxes where statutory (e.g. 0.5% in Uganda), determines the required transfer subtotal, calculates the send fee for that subtotal, and gives you the exact total deduction.

### 3. 💵 "Withdraw Only" (Agent Cash-Out)
* **The Situation:** You are standing at a mobile money kiosk to withdraw cash from your own account and need to know the agent fee, taxes, and total balance required.
* **Scenario Context:** Agent cash-out fee and taxes are deducted directly from your mobile wallet balance.
* **The Math:** `Cash to Withdraw + Agent Fee (+ Govt Tax) = Total from Balance`.

### 4. 🏪 "Pay Pochi" (Merchant Payments)
* **The Situation:** Paying a street vendor or market merchant using Safaricom's Pochi La Biashara wallet.
* **Scenario Context:** Discounted merchant transfer rates capped at a flat 50 KES fee (no withdrawal fee).
* **The Math:** Applies discounted merchant transfer rates (capped at a flat **50 KES fee** for amounts over 2,500 KES up to 250,000 KES). No withdrawal fee is charged to the sender.

### 5. 🛡️ Reactive Limit Protection
* **Maximum Cap Warnings:** Instantly alerts the user when an entered amount exceeds provider limits (e.g. `250,000 KES` or `5,000,000 UGX`).
* **Minimum Threshold Warnings:** Transparently informs when an amount is below the provider's minimum allowed transfer or agent withdrawal threshold (e.g. `500 UGX` or `50 KES`).

### 6. ⌨️ Large-Button Custom Numpad
* Replaces the standard OS keyboard with an ergonomic, thumb-friendly numeric keypad covering the bottom of the screen for one-handed operation. Zero taps to open/close keyboard.

---

## 🌍 Supported Countries & Operators (Plug & Play)

The app's offline JSON repository is pre-loaded with official tariff structures for major East African networks (verified & updated: **August 2026**):

| Country | Operator | Currency | Features Supported | Official Reference & Rules |
| :--- | :--- | :--- | :--- | :--- |
| 🇰🇪 **Kenya** | **Safaricom M-Pesa** | `KES` | Send, Reverse Math, Agent Cash-Out, Pochi La Biashara | [Safaricom Tariffs](https://www.safaricom.co.ke/main-mpesa/m-pesa-for-you/tariffs-limits/consumer-tariffs-limits) ([Rules Guide](SAFARICOM_KENYA_RULES.md)) |
| 🇺🇬 **Uganda** | **MTN Mobile Money** | `UGX` | Send, Reverse Math, Agent Cash-Out, 0.5% Statutory Govt Tax | [MTN Uganda Tariffs](https://www.mtn.co.ug/tariffs/mobile-money-tariffs/) ([Rules Guide](MTN_UGANDA_RULES.md)) |
| 🇺🇬 **Uganda** | **Airtel Money** | `UGX` | Send, Reverse Math, Agent Cash-Out, 0.5% Statutory Govt Tax | [Airtel Uganda Tariffs](https://www.airtelmoney.ug/transaction_fees) ([Rules Guide](AIRTEL_UGANDA_RULES.md)) |
| 🇹🇿 **Tanzania** | **Vodacom M-Pesa** | `TZS` | Send, Reverse Math, Agent Cash-Out, High-volume bands | Official Tiered Schedule |

---

## 🎨 "Lightweight Premium" Design System & UI Architecture

Designed specifically for optimal performance on entry-level Android devices (1GB–3GB RAM, Android Go) prevalent in the African market:

* **Banking-Grade Header Card (`ProviderHeaderCard`):** Displays operator brand badge, provider name, country flag, currency (`Currency: %s`), and instant modal bottom sheet selector.
* **Modal Bottom Sheet Provider Selector:** Features operator brand avatars, dedicated carrier tiles with selection checkmarks, an expanding *"More operators & countries coming soon"* banner, and a verified tariff timestamp footer (*"Tariffs updated: August 2026"*).
* **Single-Row Mode Selector (`ModeSingleRowSelector`):** Fluid horizontal single-row mode switcher with dynamic carrier color accents.
* **Smart Receipt Card (`SmartReceiptCard`):** Neutral slate digital receipt (`#1E293B` / `#0F172A`) with real-time scenario explanations, animated breakdown items, and a fixed anchored grand total box with zero mode-switch jumpiness.
* **Dynamic Multi-Carrier Brand Theming:** Automatically adapts primary colors and UI accents to the active provider (🟢 Safaricom Green, 🟡 MTN Sunshine Yellow with high-contrast text, 🔴 Airtel Red, 🔴 Vodacom Red).
* **AdMob Monetization (`BannerAd`):** Dedicated banner space positioned between the receipt card and numpad with fixed dimensions to eliminate Cumulative Layout Shift (CLS).
* **Adaptive 5-Row Layout Engine:** Smoothly accommodates up to 5 breakdown rows (including statutory government taxes) without vertical clipping or overlap.
* **Extreme Sunlight Readability:** High-contrast typography with deep charcoal (`#191C1E`) and crisp labels.
* **Edge-to-Edge & System Bar Handling:** Full support for Android window insets, ensuring crisp navigation bar contrast and padding.
* **Hardware-Accelerated Native Micro-Animations:** Fluid transitions (`AnimatedContent` and `animateContentSize`) with **0 KB external graphic assets**.
* **Instant Cold Start:** Opens and is ready to type in under 100ms.

---

## 🏗️ Architecture & Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Single-Activity Architecture)
* **Architecture:** MVVM (Model-View-ViewModel) with Kotlin `StateFlow`
* **Monetization:** Google Mobile Ads SDK (AdMob)
* **Internationalization (i18n):** 100% strings extracted into `strings.xml`
* **Persistence:** `SharedPreferences` (remembers selected operator/country with zero latency)
* **Serialization:** `kotlinx.serialization`
* **Compatibility:** `minSdk = 24` (Android 7.0+), covering **>97%** of active smartphones in Africa.
* **Testing:** Complete automated JUnit 4 test suite (`TariffCalculationTest.kt`) covering all carriers, reverse math tiers, and regulatory boundaries.

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/felipematheuslo/mobile-money-calc-android.git
   ```
2. Open the project in Android Studio.
3. Build and run on an Android device or emulator running API 24+.

