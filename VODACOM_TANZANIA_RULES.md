# Vodacom Tanzania M-Pesa Tariff Rules & Business Logic

## 1. Provider Overview
*   **Country:** Tanzania (🇹🇿)
*   **Operator:** Vodacom
*   **Brand:** M-Pesa
*   **Currency:** TZS (Tanzanian Shilling)
*   **Source:** Verified from July 2025 Tariff updates.
*   **Statutory Taxation:** Tanzania applies a Government Levy on mobile money withdrawals and transfers. In official Vodacom tables, the withdrawal fee presented to the user is typically a **Total Cost** (M-Pesa Fee + Government Levy).

## 2. Calculation Intents & Mapping

### Intent 1: "Send Only" (P2P Transfer)
*   **Scenario:** Transferring TZS from a Vodacom wallet to another M-Pesa customer.
*   **Logic:** Look up the exact `send_fee` corresponding to the amount tier.
*   **Formula:** `Amount + Send Fee = Total Deducted`

### Intent 2: "Send for Cash" (Reverse Math)
*   **Scenario:** The sender wants the recipient to withdraw a precise net cash amount (e.g., 50,000 TZS) from a Tanzanian agent.
*   **Logic:** 
    1. Look up the `withdrawal_fee_total` (M-Pesa Fee + Levy) for the desired net cash amount.
    2. Add them: `Subtotal = Net Cash + withdrawal_fee_total`.
    3. Look up the `send_fee` required to transfer that `Subtotal`.
    4. Calculate grand total: `Total = Subtotal + send_fee`.

### Intent 3: "Withdraw Only"
*   **Scenario:** Withdrawing physical TZS cash at a Wakala (Agent).
*   **Logic:** Cash Amount + Withdrawal Total Fee (Fee + Levy).

## 3. Official Tariff Tiers (Effective July 1st, 2025)

*Source: Vodacom Tanzania M-Pesa Transaction Fees Schedule PDF (Verified October 2026).*

| Amount Range (Tsh) From - To | Send to M-Pesa (Tsh) | Withdrawing cash from M-Pesa agents: Total Cost (Tsh) |
| :--- | :--- | :--- |
| **0 – 999** | 10 | **185** |
| **1,000 – 1,999** | 30 | **360** |
| **2,000 – 2,999** | 30 | **410** |
| **3,000 – 3,999** | 50 | **614** |
| **4,000 – 4,999** | 60 | **677** |
| **5,000 – 6,999** | 130 | **1,004** |
| **7,000 – 9,999** | 150 | **1,056** |
| **10,000 – 14,999** | 350 | **1,552** |
| **15,000 – 19,999** | 360 | **1,645** |
| **20,000 – 29,999** | 380 | **2,156** |
| **30,000 – 39,999** | 400 | **2,201** |
| **40,000 – 49,999** | 410 | **2,769** |
| **50,000 – 99,999** | 720 | **3,273** |
| **100,000 – 199,999** | 1,000 | **4,357** |
| **200,000 – 299,999** | 1,200 | **6,121** |
| **300,000 – 399,999** | 1,500 | **7,338** |
| **400,000 – 499,999** | 1,500 | **7,932** |
| **500,000 – 599,999** | 2,200 | **8,745** |
| **600,000 – 699,999** | 3,300 | **9,532** |
| **700,000 – 799,999** | 3,300 | **9,700** |
| **800,000 – 899,999** | 3,500 | **9,750** |
| **900,000 – 1,000,000** | 3,500 | **9,776** |
| **1,000,001 – 3,000,000** | 4,800 | **9,875** |
| **> 3,000,000** | 4,800 | **12,000** |

## 4. Implementation Notes for Kotlin (`tariffs.json`)
- Set `"has_government_tax": false` in the config since we are directly inserting the **Total Cost** (which already includes the 10 - 2000 Tsh Govt Levy) into the `withdrawal_fee` field.
- Update `strings.xml` to ensure `TZS` (Tanzanian Shilling) looks natural in the Swahili UI.
