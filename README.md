# 📱 Mobile Money Calculator (East Africa)

A high-performance, **100% offline**, zero-friction financial utility app built for the African market (Kenya, Uganda, Tanzania). 

The app eliminates arguments at payment counters and eliminates mental math errors by instantly calculating sending and withdrawal fees in real time.

---

## 🌟 The 4 Core Calculation Modes

### 1. 📤 "Send Only" (Wallet to Wallet)
* **The Situation:** You want to send funds directly to another person's digital wallet (for bill pay, school fees, or digital balance).
* **The Math:** `Amount to Send + Transfer Fee = Total Deducted`.

### 2. 🤝 "Send for Cash" (Reverse Math) — Killer Feature
* **The Situation:** A recipient needs an exact amount of cash in hand from an M-Pesa/MoMo/Airtel agent (e.g. 10,000 KES / 100,000 UGX) and you agree to cover all fees.
* **The Math:** Automatically calculates the agent withdrawal fee, computes government taxes where statutory (e.g. 0.5% in Uganda), determines the required transfer subtotal, calculates the send fee for that subtotal, and gives you the exact total deduction.

### 3. 💵 "Withdraw Only" (Agent Cash-Out)
* **The Situation:** You are standing at a mobile money kiosk to withdraw cash from your own account and need to know the agent fee, taxes, and total balance required.
* **The Math:** `Cash to Withdraw + Agent Fee (+ Govt Tax) = Total from Balance`.

### 4. 🏪 "Pay Pochi" (Merchant Payments)
* **The Situation:** Paying a street vendor or market merchant using Safaricom's Pochi La Biashara wallet.
* **The Math:** Applies discounted merchant transfer rates (capped at a flat **50 KES fee** for amounts over 2,500 KES up to 250,000 KES). No withdrawal fee is charged to the sender.

### 5. 🛡️ Reactive Limit Protection
* **Maximum Cap Warnings:** Instantly alerts the user when an entered amount exceeds provider limits (e.g. `250,000 KES` or `5,000,000 UGX`).
* **Minimum Threshold Warnings:** Transparently informs when an amount is below the provider's minimum allowed transfer or agent withdrawal threshold (e.g. `500 UGX` or `50 KES`).

### 6. ⌨️ Large-Button Custom Numpad
* Replaces the standard OS keyboard with an ergonomic, thumb-friendly numeric keypad covering the bottom half of the screen for one-handed operation. Zero taps to open/close keyboard.

---

## 🌍 Supported Countries & Operators (Plug & Play)

The app's offline JSON repository is pre-loaded with official tariff structures for major East African networks:

| Country | Operator | Currency | Features Supported | Official Reference & Rules |
| :--- | :--- | :--- | :--- | :--- |
| 🇰🇪 **Kenya** | **Safaricom M-Pesa** | `KES` | Send, Reverse Math, Agent Cash-Out, Pochi La Biashara | [Safaricom Tariffs](https://www.safaricom.co.ke/main-mpesa/m-pesa-for-you/tariffs-limits/consumer-tariffs-limits) ([Rules](SAFARICOM_KENYA_RULES.md)) |
| 🇺🇬 **Uganda** | **MTN Mobile Money** | `UGX` | Send, Reverse Math, Agent Cash-Out, 0.5% Statutory Govt Tax | [MTN Uganda Tariffs](https://www.mtn.co.ug/tariffs/mobile-money-tariffs/) ([Rules](MTN_UGANDA_RULES.md)) |
| 🇺🇬 **Uganda** | **Airtel Money** | `UGX` | Send, Reverse Math, Agent Cash-Out, 0.5% Statutory Govt Tax | [Airtel Uganda Tariffs](https://www.airtelmoney.ug/transaction_fees) ([Rules](AIRTEL_UGANDA_RULES.md)) |
| 🇹🇿 **Tanzania** | **Vodacom M-Pesa** | `TZS` | Send, Reverse Math, Agent Cash-Out, High-volume bands | Official Tiered Schedule |

---

## 🎨 "Lightweight Premium" Design System

Designed specifically for optimal performance on entry-level Android devices (1GB–3GB RAM, Android Go) prevalent in the African market:

* **Dynamic Multi-Carrier Brand Theming:** Automatically adapts primary colors and UI accents to the active provider (🟢 Safaricom Green, 🟡 MTN Sunshine Yellow with high-contrast text, 🔴 Airtel Red, 🔴 Vodacom Red).
* **Adaptive 5-Row Layout Engine:** Smoothly accommodates up to 5 breakdown rows (including statutory government taxes) without vertical clipping or overlap.
* **Country Flag Dropdown:** Instant context switching with flags (🇰🇪, 🇺🇬, 🇹🇿).
* **Extreme Sunlight Readability:** High-contrast typography with deep charcoal (`#191C1E`) and crisp labels.
* **Hardware-Accelerated Native Micro-Animations:** Fluid transitions (`AnimatedContent` and `animateContentSize`) with **0 KB external graphic assets**.
* **Instant Cold Start:** Opens and is ready to type in under 100ms.

---

## 🏗️ Architecture & Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Single-Activity Architecture)
* **Architecture:** MVVM (Model-View-ViewModel) with Kotlin `StateFlow`
* **Persistence:** `SharedPreferences` (remembers selected operator/country with zero latency)
* **Serialization:** `kotlinx.serialization`
* **Compatibility:** `minSdk = 24` (Android 7.0+), covering **>97%** of active smartphones in Africa.
* **Future-Proof Universal Schema:** Tariff rules are completely decoupled from business logic and driven by JSON data, ready to scale to Uganda (MTN/Airtel), Tanzania, and Ghana.

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/felipematheuslo/mobile-money-calc-android.git
   ```
2. Open the project in Android Studio.
3. Build and run on an Android device or emulator running API 24+.
