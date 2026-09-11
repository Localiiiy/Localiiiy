package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BoostCampaignRequest
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency

@Composable
fun BoostPostDialog(
    targetPostId: Long = 1L,
    currentCurrency: LocaliiiyCurrency,
    onLaunchCampaign: (BoostCampaignRequest) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedRegion by remember { mutableStateOf("Worldwide (195 Countries)") }
    var dailyBudgetUSD by remember { mutableDoubleStateOf(10.0) }
    var durationDays by remember { mutableIntStateOf(7) }
    var selectedCTA by remember { mutableStateOf("Visit Profile") }
    var isSubmitting by remember { mutableStateOf(false) }

    val regions = listOf(
        "Worldwide (195 Countries)",
        "North America (USA & Canada)",
        "Europe & UK",
        "Asia-Pacific & India",
        "Latin America",
        "Middle East & Africa",
        "Hyperlocal (Within 25 km)"
    )

    val ctaOptions = listOf(
        "Visit Profile",
        "Shop Marketplace",
        "Watch Full Studio Video",
        "Send Direct Message"
    )

    val totalBudgetUSD = dailyBudgetUSD * durationDays
    val totalBudgetLocal = CurrencyHelper.format(totalBudgetUSD, currentCurrency)
    val dailyBudgetLocal = CurrencyHelper.format(dailyBudgetUSD, currentCurrency)

    val estimatedReach = when {
        dailyBudgetUSD < 10 -> "12,000 – 28,000 people"
        dailyBudgetUSD < 25 -> "45,000 – 95,000 people"
        dailyBudgetUSD < 50 -> "110,000 – 250,000 people"
        else -> "300,000 – 750,000 people"
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = null,
                tint = LocaliiiyPrimaryTeal
            )
        },
        title = {
            Text(
                text = "Boost Content Worldwide",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Amplify your post to reach millions worldwide.",
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 1. Target Region
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Target Territory",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    regions.forEach { region ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRegion = region }
                                .padding(vertical = 3.dp)
                        ) {
                            RadioButton(
                                selected = (selectedRegion == region),
                                onClick = { selectedRegion = region }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = region, fontSize = 12.5.sp)
                        }
                    }
                }

                // 2. Daily Budget Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Daily Budget",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "$dailyBudgetLocal / day",
                            fontWeight = FontWeight.Bold,
                            color = LocaliiiyPrimaryTeal,
                            fontSize = 13.sp
                        )
                    }

                    Slider(
                        value = dailyBudgetUSD.toFloat(),
                        onValueChange = { dailyBudgetUSD = it.toDouble() },
                        valueRange = 5f..100f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = LocaliiiyPrimaryTeal,
                            activeTrackColor = LocaliiiyPrimaryTeal
                        )
                    )
                }

                // 3. Duration Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Campaign Duration: $durationDays days",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(3, 7, 14, 30).forEach { days ->
                            val isSelected = (durationDays == days)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { durationDays = days }
                            ) {
                                Text(
                                    text = "$days d",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }

                // 4. Summary Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LocaliiiyPrimaryTeal.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Investment:", fontSize = 12.sp)
                            Text(
                                text = totalBudgetLocal,
                                fontWeight = FontWeight.Black,
                                color = LocaliiiyPrimaryTeal,
                                fontSize = 14.sp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Est. Audience Reach:", fontSize = 12.sp)
                            Text(
                                text = estimatedReach,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val req = BoostCampaignRequest(
                        postIdOrClipId = targetPostId,
                        targetAudience = selectedRegion,
                        dailyBudgetUSD = dailyBudgetUSD,
                        durationDays = durationDays,
                        callToAction = selectedCTA,
                        estimatedReach = estimatedReach
                    )
                    onLaunchCampaign(req)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                modifier = Modifier.testTag("launch_boost_campaign_button")
            ) {
                Text("Launch Campaign", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
