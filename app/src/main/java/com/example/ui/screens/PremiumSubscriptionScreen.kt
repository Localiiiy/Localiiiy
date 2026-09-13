package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumSubscriptionScreen(
    onNavigateBack: () -> Unit,
    onSubscribe: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Localiiiy Premium", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B1B1B),
                    titleContentColor = Color(0xFFFFD700),
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF1B1B1B)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Upgrade to Premium",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Unlock Master Ghost Mode, Advanced Radar Filters, and Exclusive Badges.",
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Premium Features",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        PremiumFeatureItem(
                            title = "Master Ghost Mode",
                            description = "Browse the network entirely invisibly. No traces on radar or read receipts."
                        )
                        PremiumFeatureItem(
                            title = "Advanced Radar Filters",
                            description = "Filter the Live Radar by precise occupation, skills, or specific interests."
                        )
                        PremiumFeatureItem(
                            title = "Premium Profile Badge",
                            description = "Stand out with a glowing gold ring and exclusive 'Premium Neighbor' badge."
                        )
                        PremiumFeatureItem(
                            title = "Zero Video Ads",
                            description = "Claim Gamified Daily Check-ins and Referral rewards instantly without ads."
                        )
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF332D10), Color(0xFF2A2A2A))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    border = BorderStroke(2.dp, Color(0xFFFFD700))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$9.99 / month",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Cancel anytime. Billed monthly.",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )
                        
                        Button(
                            onClick = onSubscribe,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text("Subscribe Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumFeatureItem(title: String, description: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            Text(text = description, fontSize = 12.sp, color = Color.LightGray)
        }
    }
}
