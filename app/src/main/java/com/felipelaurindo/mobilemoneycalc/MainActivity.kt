package com.felipelaurindo.mobilemoneycalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.felipelaurindo.mobilemoneycalc.ui.CalculatorScreen
import com.felipelaurindo.mobilemoneycalc.ui.LightBackground
import com.felipelaurindo.mobilemoneycalc.ui.theme.MobileMoneyCalcTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize AdMob SDK once on launch
        MobileAds.initialize(this)
        
        // Force light status bar and navigation bar styles (dark icons on light background)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        setContent {
            MobileMoneyCalcTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBackground)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    // Calculator screen containing header, mode tabs, receipt, middle ad, and numpad
                    CalculatorScreen(modifier = Modifier.fillMaxSize())
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
            CalculatorScreen(modifier = Modifier.fillMaxSize())
        }
    }
}