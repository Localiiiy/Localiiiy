package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumSubscriptionScreen(
    isSubscribed: Boolean = false,
    onNavigateBack: () -> Unit,
    onSubscribe: (isAnnual: Boolean) -> Unit,
    onCancelSubscription: () -> Unit = {}
) {
    var selectedPlanIsAnnual by remember { mutableStateOf(false) }
    var showCancelConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Localiiiy Premium", fontWeight = FontWeight.Bold)
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = Color(0xFFFFD700),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF141414),
                    titleContentColor = Color(0xFFFFD700),
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF141414)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header Hero
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFD700).copy(alpha = 0.15f),
                            modifier = Modifier.size(88.dp)
                        ) {}
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFD700).copy(alpha = 0.3f),
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isSubscribed) "You're a Premium Member! 👑" else "Upgrade to Localiiiy Premium",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isSubscribed)
                            "All creator boosts, 0% market escrow, and advanced radar tools are fully unlocked."
                        else
                            "Empower your local reach with 5x Feed Boosts, 0% Market Escrow, Gold Pro Badge & 4K Studio.",
                        fontSize = 13.5.sp,
                        color = Color(0xFFCCCCCC),
                        modifier = Modifier.padding(top = 6.dp, start = 12.dp, end = 12.dp),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    // Note clarifying Free Ghost Mode
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFF0F2A38),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                            Text(
                                text = "Note: Passive Ghost Shield is 100% Free for everyone in Privacy",
                                fontSize = 11.sp,
                                color = Color(0xFF7DD3FC),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // If already subscribed: Status Card
            if (isSubscribed) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2818)),
                        border = BorderStroke(1.5.dp, Color(0xFF4ADE80)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(24.dp))
                                    Text("SUBSCRIPTION ACTIVE", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF4ADE80))
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF2E4522)
                                ) {
                                    Text(
                                        "₹499 / Month",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Your membership is active and renews next month. Enjoy unrestricted 5x hyperlocal visibility, 0% market platform fees, and Gold verified standing.",
                                fontSize = 12.5.sp,
                                color = Color(0xFFD1E7DD),
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = { showCancelConfirmDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8A80)),
                                border = BorderStroke(1.dp, Color(0xFFFF8A80).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancel Subscription", fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                // Plan Selector (Monthly approx 500 INR vs Annual)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Choose Your Affordable Plan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )

                        // Monthly Card: ₹499/mo (approx 500 INR)
                        PlanSelectionCard(
                            title = "Monthly Membership",
                            price = "₹499 / month",
                            subtext = "Approx 500 INR • ~$5.99 USD • Cancel anytime",
                            badge = "MOST POPULAR",
                            badgeColor = Color(0xFFFFD700),
                            isSelected = !selectedPlanIsAnnual,
                            onClick = { selectedPlanIsAnnual = false }
                        )

                        // Annual Card: ₹4,790/yr (Save 20%)
                        PlanSelectionCard(
                            title = "Annual Membership",
                            price = "₹4,790 / year",
                            subtext = "Approx ₹399/mo • Save 20% • Full 12 Months",
                            badge = "BEST VALUE (SAVE 20%)",
                            badgeColor = Color(0xFF00E676),
                            isSelected = selectedPlanIsAnnual,
                            onClick = { selectedPlanIsAnnual = true }
                        )
                    }
                }
            }

            // Feature Highlights (Advance Features that attract users to buy)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                            Text(
                                text = "Premium Pro Benefits",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFFFFD700)
                            )
                        }
                        Text(
                            text = "Every feature below is activated immediately upon subscribing:",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                        )

                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 8.dp))

                        PremiumAdvancedFeatureItem(
                            icon = Icons.Default.Bolt,
                            title = "5x Hyperlocal Reach & Feed Boost",
                            description = "Your posts, clips, and marketplace goods receive 5x priority distribution across neighbor feeds and live radar spotlight."
                        )
                        PremiumAdvancedFeatureItem(
                            icon = Icons.Default.Storefront,
                            title = "0% Safe-Haven Market Escrow Fees",
                            description = "Pay zero seller platform fees on all marketplace transactions ($0 fee instead of standard 5%) with complimentary Escrow Protection."
                        )
                        PremiumAdvancedFeatureItem(
                            icon = Icons.Default.Radar,
                            title = "Advanced Radar Sonar & Skill Filters",
                            description = "Filter the Live Radar by verified trades (electricians, tutors, mechanics, bakers, doctors) and high-priority emergency tags."
                        )
                        PremiumAdvancedFeatureItem(
                            icon = Icons.Default.Stars,
                            title = "Gold Pro Verified Badge & Halo",
                            description = "Stand out with an animated golden halo and exclusive Gold Pro badge beside your name on radar blips, comments, and profile."
                        )
                        PremiumAdvancedFeatureItem(
                            icon = Icons.Default.Insights,
                            title = "Neighborhood Footprint & Heatmap Analytics",
                            description = "Unlock merchant and creator analytics: see who viewed your market listings, local foot-traffic heatmaps, and peak viewer hours."
                        )
                        PremiumAdvancedFeatureItem(
                            icon = Icons.Default.VideoCall,
                            title = "4K Ultra-HD Long Video & 4-Hour Studio",
                            description = "Upload high-bitrate 4K HDR long-form video podcasts up to 240 minutes (free tier is 30 mins standard)."
                        )
                        PremiumAdvancedFeatureItem(
                            icon = Icons.Default.Headphones,
                            title = "100% Ad-Free & Background Reel Audio",
                            description = "Listen to clips and video podcasts in the background with screen locked, zero video ads, and instant streak claims."
                        )
                    }
                }
            }

            // Subscription Button
            if (!isSubscribed) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF3B330F), Color(0xFF26210A), Color(0xFF1E1E1E))
                                ),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        border = BorderStroke(2.dp, Color(0xFFFFD700))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (selectedPlanIsAnnual) "₹4,790 / year" else "₹499 / month",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = if (selectedPlanIsAnnual)
                                    "Approx ₹399/mo (Billed annually). Cancel anytime."
                                else
                                    "Approx 500 INR/month (~$5.99 USD). Cancel anytime.",
                                fontSize = 12.sp,
                                color = Color(0xFFFFE082),
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = { onSubscribe(selectedPlanIsAnnual) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.Black)
                                    Text(
                                        text = if (selectedPlanIsAnnual) "Subscribe Now (₹4,790/yr)" else "Subscribe Now (₹499/mo)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(14.dp))
                                    Text("Instant Activation", fontSize = 11.sp, color = Color.LightGray)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(14.dp))
                                    Text("Safe & Encrypted", fontSize = 11.sp, color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = false },
            title = { Text("Cancel Premium Subscription?") },
            text = { Text("Your premium benefits like 5x Hyperlocal Boost and 0% Market Escrow fee will end at the close of your current billing period.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancelSubscription()
                        showCancelConfirmDialog = false
                    }
                ) {
                    Text("Confirm Cancel", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmDialog = false }) {
                    Text("Keep Premium")
                }
            }
        )
    }
}

@Composable
private fun PlanSelectionCard(
    title: String,
    price: String,
    subtext: String,
    badge: String,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFF2A2610) else Color(0xFF1E1E1E),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFFFFD700) else Color.DarkGray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color.White)
                Text(text = subtext, fontSize = 11.5.sp, color = Color(0xFFAAAAAA))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = price, fontWeight = FontWeight.Black, fontSize = 16.sp, color = if (isSelected) Color(0xFFFFD700) else Color.White)
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFD700), unselectedColor = Color.Gray)
                )
            }
        }
    }
}

@Composable
private fun PremiumAdvancedFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.padding(vertical = 9.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFFFD700).copy(alpha = 0.15f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(17.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
            Text(
                text = description,
                fontSize = 11.5.sp,
                color = Color(0xFFB0B0B0),
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
