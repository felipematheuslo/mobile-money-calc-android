package com.felipelaurindo.mobilemoneycalc

import com.felipelaurindo.mobilemoneycalc.model.MockData
import com.felipelaurindo.mobilemoneycalc.model.ProviderConfig
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.math.ceil

class TariffCalculationTest {

    private lateinit var providers: List<ProviderConfig>
    private lateinit var mpesaKe: ProviderConfig
    private lateinit var mtnUg: ProviderConfig
    private lateinit var airtelUg: ProviderConfig
    private lateinit var mpesaTz: ProviderConfig

    @Before
    fun setUp() {
        providers = MockData.getProviders()
        mpesaKe = providers.first { it.id == "mpesa_ke" }
        mtnUg = providers.first { it.id == "mtn_ug" }
        airtelUg = providers.first { it.id == "airtel_ug" }
        mpesaTz = providers.first { it.id == "mpesa_tz" }
    }

    // =========================================================================
    // 🇰🇪 1. SAFARICOM M-PESA (KENYA) — COMPLETE 4-INTENT TESTS
    // =========================================================================

    @Test
    fun testSafaricomKenyaSchemaAndLimits() {
        assertEquals("Kenya", mpesaKe.country)
        assertEquals("Safaricom M-Pesa", mpesaKe.providerName)
        assertEquals("KES", mpesaKe.currency)
        assertFalse("Kenya has no statutory government tax", mpesaKe.hasGovernmentTax)
        assertEquals(0.0, mpesaKe.taxPercentage, 0.001)
        assertEquals(1, mpesaKe.tariffs.minOf { it.min })
        assertEquals(250000, mpesaKe.tariffs.maxOf { it.max })
        assertNotNull("Kenya must have Pochi La Biashara tariffs", mpesaKe.pochiTariffs)
    }

    @Test
    fun testSafaricomKenyaSendOnlyP2P() {
        // Free transfers for 1 - 100 KES
        val band49 = mpesaKe.tariffs.find { 49 in it.min..it.max }!!
        assertEquals(0.0, band49.transferOnNet, 0.001)

        val band100 = mpesaKe.tariffs.find { 100 in it.min..it.max }!!
        assertEquals(0.0, band100.transferOnNet, 0.001)

        // Tiered transfers
        val band500 = mpesaKe.tariffs.find { 500 in it.min..it.max }!!
        assertEquals(7.0, band500.transferOnNet, 0.001)

        val band1000 = mpesaKe.tariffs.find { 1000 in it.min..it.max }!!
        assertEquals(13.0, band1000.transferOnNet, 0.001)

        val band10k = mpesaKe.tariffs.find { 10000 in it.min..it.max }!!
        assertEquals(90.0, band10k.transferOnNet, 0.001)

        // Max cap tier (50,001 - 250,000 KES) -> 108 KES fee
        val band250k = mpesaKe.tariffs.find { 250000 in it.min..it.max }!!
        assertEquals(108.0, band250k.transferOnNet, 0.001)
    }

    @Test
    fun testSafaricomKenyaWithdrawOnlyAgentCashOut() {
        // Minimum withdrawal threshold is 50 KES (under 50 KES withdrawalAgent is null)
        val band49 = mpesaKe.tariffs.find { 49 in it.min..it.max }!!
        assertNull("Agent withdrawal not allowed under 50 KES", band49.withdrawalAgent)

        // 50 - 100 KES -> Fee 11 KES
        val band100 = mpesaKe.tariffs.find { 100 in it.min..it.max }!!
        assertEquals(11.0, band100.withdrawalAgent ?: 0.0, 0.001)

        // 10,000 KES -> Fee 115 KES -> Total deduction: 10,115 KES
        val band10k = mpesaKe.tariffs.find { 10000 in it.min..it.max }!!
        assertEquals(115.0, band10k.withdrawalAgent ?: 0.0, 0.001)

        // 250,000 KES -> Fee 309 KES -> Total deduction: 250,309 KES
        val band250k = mpesaKe.tariffs.find { 250000 in it.min..it.max }!!
        assertEquals(309.0, band250k.withdrawalAgent ?: 0.0, 0.001)
    }

    @Test
    fun testSafaricomKenyaSendForCashReverseMath() {
        // Scenario: Recipient needs 10,000 KES in cash from an agent
        val netCash = 10000.0
        val withdrawBand = mpesaKe.tariffs.find { netCash.toInt() in it.min..it.max }!!
        val agentFee = withdrawBand.withdrawalAgent!! // 115 KES
        val subtotal = netCash + agentFee // 10,115 KES

        val sendBand = mpesaKe.tariffs.find { subtotal.toInt() in it.min..it.max }!!
        val sendFee = sendBand.transferOnNet // 100 KES (Band 10,001 - 15,000)
        val totalDeducted = subtotal + sendFee // 10,215 KES

        assertEquals(115.0, agentFee, 0.001)
        assertEquals(10115.0, subtotal, 0.001)
        assertEquals(100.0, sendFee, 0.001)
        assertEquals(10215.0, totalDeducted, 0.001)
    }

    @Test
    fun testSafaricomKenyaPayPochiMerchantTariffs() {
        val pochi = mpesaKe.pochiTariffs!!

        // Free merchant payments up to 200 KES
        val pochiBand100 = pochi.find { 100 in it.min..it.max }!!
        assertEquals(0.0, pochiBand100.transferOnNet, 0.001)

        val pochiBand200 = pochi.find { 200 in it.min..it.max }!!
        assertEquals(0.0, pochiBand200.transferOnNet, 0.001)

        // Flat 50 KES fee for amounts over 2,500 KES up to 250,000 KES
        val pochiBand5k = pochi.find { 5000 in it.min..it.max }!!
        assertEquals(50.0, pochiBand5k.transferOnNet, 0.001)

        val pochiBand100k = pochi.find { 100000 in it.min..it.max }!!
        assertEquals(50.0, pochiBand100k.transferOnNet, 0.001)
    }

    // =========================================================================
    // 🇺🇬 2. MTN MOBILE MONEY (UGANDA) — COMPLETE TESTS WITH 0.5% TAX
    // =========================================================================

    @Test
    fun testMtnUgandaSchemaAndLimits() {
        assertEquals("Uganda", mtnUg.country)
        assertEquals("MTN MoMo", mtnUg.providerName)
        assertEquals("UGX", mtnUg.currency)
        assertTrue("MTN Uganda must have statutory government tax", mtnUg.hasGovernmentTax)
        assertEquals(0.5, mtnUg.taxPercentage, 0.001)
        assertEquals(500, mtnUg.tariffs.minOf { it.min })
        assertEquals(5000000, mtnUg.tariffs.maxOf { it.max })
        assertNull("MTN Uganda does not have Pochi tariffs", mtnUg.pochiTariffs)
    }

    @Test
    fun testMtnUgandaSendOnlyP2P() {
        val testCases = listOf(
            1000 to 100.0,
            5000 to 100.0,
            10000 to 500.0,
            50000 to 500.0,
            100000 to 1000.0,
            500000 to 1000.0,
            1000000 to 1500.0,
            2000000 to 2000.0,
            5000000 to 2000.0
        )

        for ((amount, expectedFee) in testCases) {
            val band = mtnUg.tariffs.find { amount in it.min..it.max }!!
            assertEquals("MTN Send fee mismatch for $amount UGX", expectedFee, band.transferOnNet, 0.001)
        }
    }

    @Test
    fun testMtnUgandaWithdrawOnlyWith05Tax() {
        // 50,000 UGX -> Agent Fee: 1,500 UGX | Gov Tax (0.5%): 250 UGX | Total: 51,750 UGX
        val amount = 50000.0
        val band = mtnUg.tariffs.find { amount.toInt() in it.min..it.max }!!
        val agentFee = band.withdrawalAgent!!
        val tax = ceil(amount * 0.005)
        val total = amount + agentFee + tax

        assertEquals(1500.0, agentFee, 0.001)
        assertEquals(250.0, tax, 0.001)
        assertEquals(51750.0, total, 0.001)

        // Top Tier: 4,500,000 UGX -> Agent Fee: 20,000 UGX | Tax: 22,500 UGX | Total: 4,542,500 UGX
        val amountTop = 4500000.0
        val bandTop = mtnUg.tariffs.find { amountTop.toInt() in it.min..it.max }!!
        val agentFeeTop = bandTop.withdrawalAgent!!
        val taxTop = ceil(amountTop * 0.005)
        val totalTop = amountTop + agentFeeTop + taxTop

        assertEquals(20000.0, agentFeeTop, 0.001)
        assertEquals(22500.0, taxTop, 0.001)
        assertEquals(4542500.0, totalTop, 0.001)
    }

    @Test
    fun testMtnUgandaSendForCashReverseMath() {
        // Net Cash needed: 50,000 UGX
        val netCash = 50000.0
        val withdrawBand = mtnUg.tariffs.find { netCash.toInt() in it.min..it.max }!!
        val agentFee = withdrawBand.withdrawalAgent!! // 1,500 UGX
        val tax = ceil(netCash * 0.005) // 250 UGX
        val subtotal = netCash + agentFee + tax // 51,750 UGX
        val sendBand = mtnUg.tariffs.find { subtotal.toInt() in it.min..it.max }!!
        val sendFee = sendBand.transferOnNet // 500 UGX
        val total = subtotal + sendFee // 52,250 UGX

        assertEquals(1500.0, agentFee, 0.001)
        assertEquals(250.0, tax, 0.001)
        assertEquals(51750.0, subtotal, 0.001)
        assertEquals(500.0, sendFee, 0.001)
        assertEquals(52250.0, total, 0.001)
    }

    // =========================================================================
    // 🇺🇬 3. AIRTEL MONEY (UGANDA) — COMPLETE 14 BANDS & REVERSE MATH TESTS
    // =========================================================================

    @Test
    fun testAirtelUgandaSchemaAnd14BandsContiguity() {
        assertEquals("Uganda", airtelUg.country)
        assertEquals("Airtel Money", airtelUg.providerName)
        assertEquals("UGX", airtelUg.currency)
        assertTrue(airtelUg.hasGovernmentTax)
        assertEquals(0.5, airtelUg.taxPercentage, 0.001)
        assertEquals(14, airtelUg.tariffs.size)
        assertEquals(500, airtelUg.tariffs.minOf { it.min })
        assertEquals(5000000, airtelUg.tariffs.maxOf { it.max })

        for (i in 0 until airtelUg.tariffs.size - 1) {
            assertEquals(airtelUg.tariffs[i].max + 1, airtelUg.tariffs[i + 1].min)
        }
    }

    @Test
    fun testAirtelUgandaSendOnlyAllBands() {
        val testCases = listOf(
            500 to 100.0, 2500 to 100.0, 2501 to 100.0, 5000 to 100.0,
            5001 to 500.0, 15000 to 500.0, 15001 to 500.0, 30000 to 500.0,
            30001 to 500.0, 45000 to 500.0, 45001 to 500.0, 60000 to 500.0,
            60001 to 1000.0, 125000 to 1000.0, 125001 to 1000.0, 250000 to 1000.0,
            250001 to 1000.0, 500000 to 1000.0, 500001 to 1500.0, 1000000 to 1500.0,
            1000001 to 2000.0, 2000000 to 2000.0, 2000001 to 2000.0, 3000000 to 2000.0,
            3000001 to 2000.0, 4000000 to 2000.0, 4000001 to 2000.0, 5000000 to 2000.0
        )

        for ((amount, expectedFee) in testCases) {
            val band = airtelUg.tariffs.find { amount in it.min..it.max }!!
            assertEquals("Airtel Send Fee for $amount UGX", expectedFee, band.transferOnNet, 0.001)
        }
    }

    @Test
    fun testAirtelUgandaWithdrawOnlyWith05Tax() {
        val testCases = listOf(
            Triple(1000.0, 330.0, 5.0),
            Triple(5000.0, 440.0, 25.0),
            Triple(10000.0, 700.0, 50.0),
            Triple(25000.0, 880.0, 125.0),
            Triple(40000.0, 1210.0, 200.0),
            Triple(50000.0, 1500.0, 250.0),
            Triple(100000.0, 1925.0, 500.0),
            Triple(200000.0, 3575.0, 1000.0),
            Triple(400000.0, 7000.0, 2000.0),
            Triple(800000.0, 12500.0, 4000.0),
            Triple(1500000.0, 15000.0, 7500.0),
            Triple(2500000.0, 18000.0, 12500.0),
            Triple(3500000.0, 18000.0, 17500.0),
            Triple(4500000.0, 18000.0, 22500.0) // Fixed 18,000 UGX vs MTN 20,000 UGX
        )

        for ((amount, expectedAgentFee, expectedTax) in testCases) {
            val band = airtelUg.tariffs.find { amount.toInt() in it.min..it.max }!!
            val agentFee = band.withdrawalAgent ?: 0.0
            val govTax = ceil(amount * 0.005)
            val total = amount + agentFee + govTax

            assertEquals(expectedAgentFee, agentFee, 0.001)
            assertEquals(expectedTax, govTax, 0.001)
            assertEquals(amount + expectedAgentFee + expectedTax, total, 0.001)
        }
    }

    @Test
    fun testAirtelUgandaSendForCashReverseMath() {
        val netCash = 100000.0
        val withdrawBand = airtelUg.tariffs.find { netCash.toInt() in it.min..it.max }!!
        val agentFee = withdrawBand.withdrawalAgent!! // 1,925 UGX
        val tax = ceil(netCash * 0.005) // 500 UGX
        val subtotal = netCash + agentFee + tax // 102,425 UGX
        val sendBand = airtelUg.tariffs.find { subtotal.toInt() in it.min..it.max }!!
        val sendFee = sendBand.transferOnNet // 1,000 UGX
        val total = subtotal + sendFee // 103,425 UGX

        assertEquals(1925.0, agentFee, 0.001)
        assertEquals(500.0, tax, 0.001)
        assertEquals(102425.0, subtotal, 0.001)
        assertEquals(1000.0, sendFee, 0.001)
        assertEquals(103425.0, total, 0.001)
    }

    // =========================================================================
    // 🇹🇿 4. VODACOM M-PESA (TANZANIA) — COMPLETE ON-NET & OFF-NET TESTS
    // =========================================================================

    @Test
    fun testVodacomTanzaniaSchemaAndLimits() {
        assertEquals("Tanzania", mpesaTz.country)
        assertEquals("Vodacom M-Pesa", mpesaTz.providerName)
        assertEquals("TZS", mpesaTz.currency)
        assertFalse("Tanzania has no statutory government tax", mpesaTz.hasGovernmentTax)
        assertEquals(100, mpesaTz.tariffs.minOf { it.min })
        assertEquals(5000000, mpesaTz.tariffs.maxOf { it.max })
        assertNull("Vodacom Tanzania does not have Pochi tariffs", mpesaTz.pochiTariffs)
    }

    @Test
    fun testVodacomTanzaniaOnNetVsOffNetTransfers() {
        // 1,000 TZS -> On-Net: 40 TZS | Off-Net: 200 TZS
        val band1k = mpesaTz.tariffs.find { 1000 in it.min..it.max }!!
        assertEquals(40.0, band1k.transferOnNet, 0.001)
        assertEquals(200.0, band1k.transferOffNet, 0.001)

        // 50,000 TZS -> On-Net: 850 TZS | Off-Net: 3,800 TZS
        val band50k = mpesaTz.tariffs.find { 50000 in it.min..it.max }!!
        assertEquals(850.0, band50k.transferOnNet, 0.001)
        assertEquals(3800.0, band50k.transferOffNet, 0.001)

        // 3,500,000 TZS -> On-Net: 4,000 TZS | Off-Net: 20,000 TZS
        val band3_5m = mpesaTz.tariffs.find { 3500000 in it.min..it.max }!!
        assertEquals(4000.0, band3_5m.transferOnNet, 0.001)
        assertEquals(20000.0, band3_5m.transferOffNet, 0.001)
    }

    @Test
    fun testVodacomTanzaniaWithdrawOnlyAgentCashOut() {
        // 10,000 TZS -> Agent Fee: 1,800 TZS -> Total from balance: 11,800 TZS
        val band10k = mpesaTz.tariffs.find { 10000 in it.min..it.max }!!
        assertEquals(1800.0, band10k.withdrawalAgent ?: 0.0, 0.001)

        // 1,000,000 TZS -> Agent Fee: 16,000 TZS -> Total from balance: 1,016,000 TZS
        val band1m = mpesaTz.tariffs.find { 1000000 in it.min..it.max }!!
        assertEquals(16000.0, band1m.withdrawalAgent ?: 0.0, 0.001)
    }

    @Test
    fun testVodacomTanzaniaSendForCashReverseMath() {
        // Recipient needs 20,000 TZS in cash
        val netCash = 20000.0
        val withdrawBand = mpesaTz.tariffs.find { netCash.toInt() in it.min..it.max }!!
        val agentFee = withdrawBand.withdrawalAgent!! // 2,500 TZS
        val subtotal = netCash + agentFee // 22,500 TZS

        val sendBand = mpesaTz.tariffs.find { subtotal.toInt() in it.min..it.max }!!
        val sendFee = sendBand.transferOnNet // 500 TZS (Band 20,000 - 39,999)
        val totalDeducted = subtotal + sendFee // 23,000 TZS

        assertEquals(2500.0, agentFee, 0.001)
        assertEquals(22500.0, subtotal, 0.001)
        assertEquals(500.0, sendFee, 0.001)
        assertEquals(23000.0, totalDeducted, 0.001)
    }

    // =========================================================================
    // 📊 5. CROSS-CARRIER COMPARATIVE & REGULATORY EDGE CASE TESTS
    // =========================================================================

    @Test
    fun testUgandaHighVolumeWithdrawalComparison() {
        val topTierAmount = 4500000.0

        val airtelBand = airtelUg.tariffs.find { topTierAmount.toInt() in it.min..it.max }!!
        val mtnBand = mtnUg.tariffs.find { topTierAmount.toInt() in it.min..it.max }!!

        assertEquals(18000.0, airtelBand.withdrawalAgent ?: 0.0, 0.001)
        assertEquals(20000.0, mtnBand.withdrawalAgent ?: 0.0, 0.001)

        val tax = ceil(topTierAmount * 0.005) // 22,500 UGX
        val airtelTotal = topTierAmount + airtelBand.withdrawalAgent!! + tax
        val mtnTotal = topTierAmount + mtnBand.withdrawalAgent!! + tax

        assertEquals(4540500.0, airtelTotal, 0.001)
        assertEquals(4542500.0, mtnTotal, 0.001)
        assertEquals(2000.0, mtnTotal - airtelTotal, 0.001)
    }

    @Test
    fun testAllProvidersHaveCompleteAndNonEmptyTariffs() {
        assertEquals("Total registered providers count", 4, providers.size)
        for (provider in providers) {
            assertTrue("Provider ${provider.providerName} must have tariffs", provider.tariffs.isNotEmpty())
            assertTrue("Provider ${provider.currency} currency must not be blank", provider.currency.isNotBlank())
            assertTrue("Provider ${provider.country} country must not be blank", provider.country.isNotBlank())
        }
    }
}
