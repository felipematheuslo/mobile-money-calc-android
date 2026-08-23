package com.felipelaurindo.mobilemoneycalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.felipelaurindo.mobilemoneycalc.ui.CalculatorScreen
import com.felipelaurindo.mobilemoneycalc.ui.LightBackground
import com.felipelaurindo.mobilemoneycalc.ui.components.BannerAd
import com.felipelaurindo.mobilemoneycalc.ui.theme.MobileMoneyCalcTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize AdMob SDK once on launch
        MobileAds.initialize(this)
        enableEdgeToEdge()
        setContent {
            MobileMoneyCalcTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBackground)
                        .navigationBarsPadding()
                ) {
                    // Fixed top banner with pre-allocated height to prevent UI jumps
                    BannerAd()
                    // Main calculator content filling remaining vertical space
                    CalculatorScreen(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    MobileMoneyCalcTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackground)
        ) {
            BannerAd()
            CalculatorScreen(modifier = Modifier.weight(1f))
        }
    }
}