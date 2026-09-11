package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.AdPlacement
import com.example.ui.theme.EditorialVerified
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.LocalizationHelper
import com.example.util.LocaliiiyStringKey

@Composable
fun SponsoredAdCard(
    ad: AdPlacement,
    currentCurrency: LocaliiiyCurrency,
    currentLanguage: LocaliiiyLanguage,
    onAdImpression: (String) -> Unit,
    onAdClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var hasRecordedImpression by remember(ad.id) { mutableStateOf(false) }
    var showAdDetailsDialog by remember { mutableStateOf(false) }

    // Trigger impression event once per composition
    LaunchedEffect(ad.id) {
        if (!hasRecordedImpression) {
            hasRecordedImpression = true
            onAdImpression(ad.id)
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("sponsored_ad_card_${ad.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header (Advertiser info + Sponsored Tag)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ad.advertiserAvatar)
                            .crossfade(true)
                            .build(),
                        contentDescription = ad.advertiserName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = ad.advertiserName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (ad.isVerifiedAdvertiser) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Advertiser",
                                    tint = EditorialVerified,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = LocalizationHelper.getString(LocaliiiyStringKey.SPONSORED_LABEL, currentLanguage) + " • ${ad.targetRegion}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { showAdDetailsDialog = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Ad Info",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Media Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clickable { onAdClick(ad.id) }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ad.mediaUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = ad.headline,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Sponsored Pill Overlay
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "Sponsored Ad",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Headline, Description & Call To Action Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = ad.headline,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = ad.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Action Button (CTA)
                Button(
                    onClick = { onAdClick(ad.id) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("ad_cta_button_${ad.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = ad.callToAction,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAdDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showAdDetailsDialog = false },
            title = { Text("About this Worldwide Ad") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Advertiser: ${ad.advertiserName}\nTarget Region: ${ad.targetRegion}\nEffective CPM: ${CurrencyHelper.format(ad.cpmRateUSD, currentCurrency)}",
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Your ad views support content creators globally.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAdDetailsDialog = false }) {
                    Text("Got It")
                }
            }
        )
    }
}
