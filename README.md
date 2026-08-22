# 📱 Mobile Money Calculator (Kenya MVP)

A high-performance, **100% offline**, zero-friction financial utility app built for the African market (starting with Kenya's Safaricom M-Pesa). 

The app eliminates arguments at payment counters and eliminates mental math errors by instantly calculating sending and withdrawal fees in real time.

---

## 🌟 The 4 Core Calculation Modes

### 1. 📤 "Send Only" (Wallet to Wallet)
* **The Situation:** You want to send funds directly to another person's digital wallet (for bill pay, school fees, or digital balance).
* **The Math:** `Amount to Send + Transfer Fee = Total Deducted`.

### 2. 🤝 "Send for Cash" (Reverse Math) — Killer Feature
* **The Situation:** A recipient needs an exact amount of cash in hand from an M-Pesa agent (e.g. 10,000 KES) and you agree to cover all fees.
* **The Math:** Automatically calculates the agent withdrawal fee (`115 KES`), determines the required transfer (`10,115 KES`), computes the send fee for that subtotal (`100 KES`), and gives you the exact total deduction (`10,215 KES`).

### 3. 💵 "Withdraw Only" (Agent Cash-Out)
* **The Situation:** You are standing at an M-Pesa kiosk to withdraw cash from your own account and need to know the fee and total balance required.
* **The Math:** `Cash to Withdraw + Agent Fee = Total from Balance`.

### 4. 🏪 "Pay Pochi" (Merchant Payments)
* **The Situation:** Paying a street vendor or market merchant using Safaricom's Pochi La Biashara wallet.
* **The Math:** Applies discounted merchant transfer rates (capped at a flat **50 KES fee** for amounts over 2,500 KES up to 250,000 KES). No withdrawal fee is charged to the sender.

### 5. 🛡️ Reactive Limit Protection
* Instantly alerts the user when an entered amount exceeds provider limits (e.g. 250,000 KES).

### 6. ⌨️ Large-Button Custom Numpad
* Replaces the standard OS keyboard with an ergonomic, thumb-friendly numeric keypad covering the bottom half of the screen for one-handed operation. Zero taps to open/close keyboard.

---

## 🌍 Supported Countries & Operators (Plug & Play)

The app's offline JSON repository is pre-loaded with official tariff structures for major East African networks:

| Country | Operator | Currency | Features Supported |
| :--- | :--- | :--- | :--- |
| 🇰🇪 **Kenya** | **Safaricom M-Pesa** | `KES` | Send, Reverse Math, Agent Cash-Out, Pochi La Biashara |
| 🇺🇬 **Uganda** | **MTN Mobile Money** | `UGX` | Send, Reverse Math, Agent Cash-Out, 0.5% Statutory Govt Tax |
| 🇺🇬 **Uganda** | **Airtel Money** | `UGX` | Send, Reverse Math, Agent Cash-Out, 0.5% Statutory Govt Tax |
| 🇹🇿 **Tanzania** | **Vodacom M-Pesa** | `TZS` | Send, Reverse Math, Agent Cash-Out, High-volume bands |

---

## 🎨 "Lightweight Premium" Design System

Designed specifically for optimal performance on entry-level Android devices (1GB–3GB RAM, Android Go) prevalent in the African market:

* **Country Flag Dropdown:** Instant context switching with flags (🇰🇪, 🇺🇬, 🇹🇿).
* **Safaricom Green Accent (`#00B365`):** Instantly recognizable brand identity.
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
