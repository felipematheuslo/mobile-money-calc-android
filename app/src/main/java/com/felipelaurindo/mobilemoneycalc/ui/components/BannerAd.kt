package com.felipelaurindo.mobilemoneycalc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.felipelaurindo.mobilemoneycalc.ui.LightBackground
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

// Google AdMob Sample Test Banner Ad Unit ID
const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

// Pre-allocated height for standard banner (50dp) to prevent Cumulative Layout Shift (CLS)
val BANNER_HEIGHT = 50.dp

/**
 * Renders a fixed top banner with pre-allocated space.
 * Prevents UI jumps by reserving the fixed height and status bar insets from the initial composition.
 */
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val isInPreview = LocalInspectionMode.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(LightBackground)
            .statusBarsPadding()
            .height(BANNER_HEIGHT),
        contentAlignment = Alignment.Center
    ) {
        if (isInPreview) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AdMob Banner (320x50)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280)
                )
            }
        } else {
            AndroidView(
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(AdSize.BANNER)
                        adUnitId = BANNER_AD_UNIT_ID
                        loadAd(AdRequest.Builder().build())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
