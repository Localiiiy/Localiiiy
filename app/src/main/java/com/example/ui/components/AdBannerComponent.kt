package com.example.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBannerComponent(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111" // Standard AdMob Test Banner ID
) {
    AndroidView(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        factory = { context ->
            val frameLayout = FrameLayout(context)
            val adView = AdView(context)
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = adUnitId
            
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
            adView.layoutParams = layoutParams
            
            frameLayout.addView(adView)
            
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            
            frameLayout
        }
    )
}
