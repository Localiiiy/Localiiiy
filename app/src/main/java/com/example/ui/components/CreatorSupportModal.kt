package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CreatorSupportSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorSupportModal(
    creatorName: String,
    creatorUsername: String,
    creatorAvatar: String,
    supportSettings: CreatorSupportSettings = CreatorSupportSettings(),
    onDismissRequest: () -> Unit,
    onSendTip: (amount: Double, message: String) -> Unit,
    onJoinPatron: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Tip Jar, 1 = ₹99/mo Patron
    var selectedTipIndex by remember { mutableIntStateOf(1) } // default 100 INR
    var customTipNote by remember { mutableStateOf("Love your local clips & neighborhood radar updates! ☕") }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val tipAmounts = supportSettings.presetTipAmountsINR

    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) onDismissRequest() },
        containerColor = Color(0xFF161616),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Creator Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                    border = BorderStroke(1.5.dp, Color(0xFFFFD700)),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("☕", fontSize = 22.sp)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Support $creatorName",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Text(
                        text = "@$creatorUsername • Local Hyperlocal Creator",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Tab Selector: Tip Jar vs Patron Tier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedTab == 0) Color(0xFFFFD700) else Color(0xFF222222),
                    border = BorderStroke(1.dp, if (selectedTab == 0) Color(0xFFFFD700) else Color.DarkGray),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 0 }
                ) {
                    Text(
                        text = "☕ Send a Quick Tip",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) Color.Black else Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedTab == 1) Color(0xFFFFD700) else Color(0xFF222222),
                    border = BorderStroke(1.dp, if (selectedTab == 1) Color(0xFFFFD700) else Color.DarkGray),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 1 }
                ) {
                    Text(
                        text = "👑 Patron (₹99/mo)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 1) Color.Black else Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }

            if (selectedTab == 0) {
                // Tipping Jar Flow
                Text(
                    text = supportSettings.tippingPrompt,
                    fontSize = 13.sp,
                    color = Color(0xFFFFE082),
                    lineHeight = 18.sp
                )

                Text("Choose Tip Amount:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tipAmounts.forEachIndexed { index, amount ->
                        val isSelected = selectedTipIndex == index
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFFFD700).copy(alpha = 0.2f) else Color(0xFF222222),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) Color(0xFFFFD700) else Color.DarkGray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTipIndex = index }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "₹$amount",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color(0xFFFFD700) else Color.White
                                )
                                Text(
                                    text = when (index) {
                                        0 -> "Chai ☕"
                                        1 -> "Snack 🍪"
                                        2 -> "Meal 🍕"
                                        else -> "Super 🚀"
                                    },
                                    fontSize = 9.5.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = customTipNote,
                    onValueChange = { customTipNote = it },
                    label = { Text("Add a warm neighborhood note:") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val amount = tipAmounts.getOrElse(selectedTipIndex) { 100 }.toDouble()
                        isProcessing = true
                        coroutineScope.launch {
                            delay(1200)
                            isProcessing = false
                            onSendTip(amount, customTipNote)
                            successMessage = "₹${amount.toInt()} Tip sent to @$creatorUsername with UPI Fast Pay! 🎉"
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val amount = tipAmounts.getOrElse(selectedTipIndex) { 100 }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                        Text(
                            text = "Send ₹$amount Tip via UPI / Cards",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.5.sp
                        )
                    }
                }
            } else {
                // ₹99/mo Neighborhood Patron Tier
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1E1A0C),
                    border = BorderStroke(1.5.dp, Color(0xFFFFD700)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = supportSettings.patronTierName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color(0xFFFFD700)
                                )
                                Text(
                                    text = "${supportSettings.activePatronCount} Active Patrons Supporting",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF00E676),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFD700)
                            ) {
                                Text(
                                    text = "₹${supportSettings.patronTierPriceINR} / mo",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Text("Exclusive Patron Perks Included:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)

                        supportSettings.patronPerks.forEach { perk ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                Text(perk, fontSize = 12.sp, color = Color.LightGray)
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        isProcessing = true
                        coroutineScope.launch {
                            delay(1200)
                            isProcessing = false
                            onJoinPatron()
                            successMessage = "You are now an official Neighborhood Patron of @$creatorUsername! 👑"
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = Color.Black)
                        Text(
                            text = "Subscribe as Patron (₹99 / month)",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.5.sp
                        )
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onDismissRequest()
            },
            title = { Text("Support Confirmed! 💛", fontWeight = FontWeight.Bold) },
            text = { Text(successMessage, fontSize = 13.5.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
