package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.OtherUserEntity
import com.example.ui.LocaliiiyViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketSandboxUpgradesScreen(
    currentCurrencySymbol: String = "$",
    onBackToGoods: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LocaliiiyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val creatorEarnings by viewModel.creatorEarnings.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Handshake Escrow States
    var escrowStep by remember { mutableIntStateOf(1) } // 1: Hold, 2: Meet, 3: Scan release, 4: Released
    var buyerScanned by remember { mutableStateOf(false) }
    var sellerScanned by remember { mutableStateOf(false) }

    // Cashless Barter & Swap States
    var swapItems by remember {
        mutableStateOf(
            listOf(
                Triple("Bosch Power Drill 🛠️", "Wants: Heavy-duty ladder or lawnmower", "Alki block • 400m"),
                Triple("Rooftop Organic Tomatoes (5kg) 🍅", "Wants: Sourdough bread or fresh honey", "Seattle St • 1.1km"),
                Triple("Classic Acoustic Guitar 🎸", "Wants: Electric keyboard or Spanish lessons", "Pike Corridor • 800m")
            )
        )
    }

    // Time-Bank Ledger States
    var timeBankHoursBalance by remember { mutableIntStateOf(4) }
    var timeBankList by remember {
        mutableStateOf(
            listOf(
                Triple("Need Help: Repair bathroom faucet 🚰", "Duration: 2 Hours", "Requester: Mrs. Gable"),
                Triple("Need Help: Walk golden retriever 🐕", "Duration: 1 Hour", "Requester: Jordan"),
                Triple("Offer Help: English lessons / Writing tutor 📚", "Earn: 1 Hour/hr", "Provider: Alex")
            )
        )
    }

    // Daily-Bite Kitchen States
    var kitchenList by remember {
        mutableStateOf(
            listOf(
                Triple("Mrs. Kapoor's Fresh Samosas 🥟", "₹120 ($1.50) for 4 pcs • Harvested 10 mins ago", "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=400"),
                Triple("Alki Rooftop Strawberry Bowl 🍓", "₹200 ($2.50) • Harvested fresh this morning", "https://images.unsplash.com/photo-1518635017498-87f514b751ba?w=400")
            )
        )
    }

    // Solar & Resource States
    var householdSolarKw by remember { mutableFloatStateOf(4.2f) }
    var sharedChargerOccupied by remember { mutableStateOf(false) }

    // Tool-Sharing Directory States
    var toolsList by remember {
        mutableStateOf(
            listOf(
                Triple("Aluminum Extension Ladder (5m) 🪜", "Deposit: 100 Sparks (Refundable)", "Status: Available"),
                Triple("Lawnmower Gas Powered 🚜", "Deposit: 300 Sparks (Refundable)", "Status: Borrowed by Alex"),
                Triple("Heavy Car Jack 🚗", "Deposit: 150 Sparks (Refundable)", "Status: Available")
            )
        )
    }

    // Direct UPI payment states
    var showUpiSimulator by remember { mutableStateOf(false) }
    var upiAmountInput by remember { mutableStateOf("150") }

    // Green-Pulse Bounties States
    var cleanupBountiesList by remember {
        mutableStateOf(
            listOf(
                Triple("Alki Beach Waste Recovery 🧹", "Reward: 200 Sparks + Green Badge", "Saturday 8:00 AM"),
                Triple("District Park Tree-Planting Drive 🌳", "Reward: 300 Sparks + Civic Badge", "Sunday 9:00 AM")
            )
        )
    }

    // Merchant Space ID States
    var checkInMerchantName by remember { mutableStateOf("West Coast Precision Cycles") }
    var checkingInActive by remember { mutableStateOf(false) }

    // Merchant Sponsored Bounties
    var merchantCampaignsList by remember {
        mutableStateOf(
            listOf(
                Triple("Visit West Coast Cycles 🚴", "Record a clip inside the store, get 150 Sparks!", "Ends Oct 15"),
                Triple("Rooftop Organic Market promo 🍅", "Post a photo story of your purchase, get 100 Sparks", "Ends Oct 20")
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070A12))
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)),
                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🛍️", fontSize = 24.sp)
                            Column {
                                Text(
                                    "HYPERLOCAL COMMERCE & EXCHANGE TOOLS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "Connected, Secure & Sustainable Merchant Tools",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                        IconButton(onClick = onBackToGoods) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Explore decentralized commerce integrations, cashless exchanges, direct UPI gateways, and merchant check-in panels designed specifically for Localiiiy’s Handshake Marketplace.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // --- Safe-Haven Handshake Escrow ---
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, if (escrowStep == 4) Color.Green else Color.DarkGray)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🤝", fontSize = 18.sp)
                        Column {
                            Text("Safe-Haven Escrow Release Simulator", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Protects transactions by releasing funds only on QR matching", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Active Transaction: Apple Watch Series 9 (₹24,500)", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
                    Text("Escrow Ledger: Funds held securely in Localiiiy vault.", fontSize = 10.5.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Current Escrow State:", fontSize = 10.sp, color = Color.Gray)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (escrowStep) {
                                1 -> Color.Yellow.copy(alpha = 0.2f)
                                2 -> Color.Cyan.copy(alpha = 0.2f)
                                3 -> Color.Magenta.copy(alpha = 0.2f)
                                else -> Color.Green.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = when (escrowStep) {
                                    1 -> " FNDS HELD IN ESCROW 🪙 "
                                    2 -> " MEET AT SAFE SPOT 📍 "
                                    3 -> " SCAN BOTH QRS 📱 "
                                    else -> " RELEASED TO SELLER ✔️ "
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (escrowStep) {
                                    1 -> Color.Yellow
                                    2 -> Color.Cyan
                                    3 -> Color.Magenta
                                    else -> Color.Green
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    if (escrowStep < 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (escrowStep == 1) escrowStep = 2
                                    else if (escrowStep == 2) {
                                        buyerScanned = true
                                        if (sellerScanned) {
                                            escrowStep = 4
                                            viewModel.addSparks(2450) // Credit seller wallet balance with equivalent Sparks/USD
                                            Toast.makeText(context, "Escrow Released! Credited 2,450 Sparks directly into seller's wallet!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (buyerScanned) Color.DarkGray else Color(0xFF10B981)),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(if (buyerScanned) "Buyer Scanned ✔️" else "Scan Buyer QR", fontSize = 10.sp)
                            }

                            Button(
                                onClick = {
                                    if (escrowStep == 1) escrowStep = 2
                                    else if (escrowStep == 2) {
                                        sellerScanned = true
                                        if (buyerScanned) {
                                            escrowStep = 4
                                            viewModel.addSparks(2450) // Credit seller wallet
                                            Toast.makeText(context, "Escrow Released! Credited 2,450 Sparks directly into seller's wallet!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (sellerScanned) Color.DarkGray else Color(0xFF10B981)),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(if (sellerScanned) "Seller Scanned ✔️" else "Scan Seller QR", fontSize = 10.sp)
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                escrowStep = 1
                                buyerScanned = false
                                sellerScanned = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reset Escrow Simulator Cycle", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // --- Cashless Barter & Swap Circles ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🔄", fontSize = 18.sp)
                        Column {
                            Text("Barter & Swap Circles", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Exchange goods directly without spending currency", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    swapItems.forEach { swap ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(swap.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            Text(swap.second, fontSize = 11.sp, color = Color(0xFF10B981))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(swap.third, fontSize = 9.5.sp, color = Color.Gray)
                                Button(
                                    onClick = {
                                        val otherUser = OtherUserEntity(
                                            username = "Alex_Plum",
                                            fullName = "Alex Plum",
                                            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100",
                                            bio = "Barter and Swap Circle member",
                                            locationName = "Seattle"
                                        )
                                        viewModel.startChatWithUser(otherUser)
                                        coroutineScope.launch {
                                            delay(500)
                                            viewModel.sendChatMessage("Hi Alex! I saw your barter swap offer for '${swap.first}' (${swap.second}). I'd like to propose a direct cashless trade. Let's discuss terms! 🤝")
                                        }
                                        Toast.makeText(context, "Opened direct message to negotiate the barter swap details! 🤝", Toast.LENGTH_SHORT).show()
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Offer Swap", fontSize = 9.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Neighborhood Time-Bank Ledger ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("⏳", fontSize = 18.sp)
                        Column {
                            Text("Time-Bank Cashless Ledger", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Donate chore hours & redeem English tutoring or plumbing", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Your Time-Credit Balance:", fontSize = 11.5.sp, color = Color.White)
                        Text("$timeBankHoursBalance Hours", fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF), fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    timeBankList.forEach { ledger ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(ledger.first, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ledger.second + " • " + ledger.third, fontSize = 10.sp, color = Color.Gray)
                                Button(
                                    onClick = {
                                        if (ledger.third.contains("Requester")) {
                                            timeBankHoursBalance += 2
                                            Toast.makeText(context, "Chore accepted! Added 2 hours directly to your Time-Bank ledger!", Toast.LENGTH_LONG).show()
                                        } else {
                                            if (timeBankHoursBalance >= 1) {
                                                timeBankHoursBalance--
                                                Toast.makeText(context, "1 Hour spent. English tutoring session successfully booked with Alex!", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "Insufficient hour balance! Please perform neighborhood chores first to earn credits.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text(if (ledger.third.contains("Requester")) "Perform Chore" else "Book Tutor", fontSize = 9.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Daily-Bite Live Micro-Kitchens ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🍳", fontSize = 18.sp)
                        Column {
                            Text("Daily-Bite Micro-Kitchens", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Buy fresh home-cooked meals & organic rooftop veggies direct", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    kitchenList.forEach { dish ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(dish.third)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = dish.first,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(45.dp).clip(RoundedCornerShape(6.dp))
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(dish.first, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
                                Text(dish.second, fontSize = 10.sp, color = Color.LightGray)
                            }
                            Button(
                                onClick = {
                                    val otherUser = OtherUserEntity(
                                        username = "Chef_Kapoor",
                                        fullName = "Mrs. Kapoor (Home Chef)",
                                        avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100",
                                        bio = "Passionate home chef specializing in traditional bites",
                                        locationName = "Seattle"
                                    )
                                    val success = viewModel.deductSparks(15) // Deducts equivalent of $1.50 USD
                                    if (success) {
                                        viewModel.startChatWithUser(otherUser)
                                        coroutineScope.launch {
                                            delay(500)
                                            viewModel.sendChatMessage("Hi Mrs. Kapoor! I would like to order one plate of '${dish.first}' (${dish.second}). I've paid 15 Sparks ($1.50 equivalent) directly from my secure wallet. Let me know when and where I can pick it up! 🥟")
                                        }
                                        Toast.makeText(context, "Order placed! Direct messaging started with Chef Kapoor to coordinate meal handovers! 🥟", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Insufficient Sparks balance! Please claim daily check-in Sparks or refer neighbors to complete this purchase.", Toast.LENGTH_LONG).show()
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Order Plate", fontSize = 9.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Rooftop Solar & Resource Trading Grid ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("☀️", fontSize = 18.sp)
                        Column {
                            Text("Rooftop Resource & Charger Grid", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Trade solar output, organic soils, or shared EV chargers", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text("Rooftop Solar Array: $householdSolarKw KW Generated Today", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (sharedChargerOccupied) "Your Shared EV Charger: BUSY 🔌" else "Your Shared EV Charger: AVAILABLE 🔌", fontSize = 10.5.sp, color = Color.LightGray)
                            Switch(
                                checked = sharedChargerOccupied,
                                onCheckedChange = { sharedChargerOccupied = it },
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    }
                }
            }
        }

        // --- Tool-Sharing Inventory Directory ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🪜", fontSize = 18.sp)
                        Column {
                            Text("Tool-Sharing Neighborhood Registry", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Borrow expensive household gear securely with escrow deposits", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    toolsList.forEach { tool ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(tool.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                Text(tool.third, color = if (tool.third.contains("Available")) Color.Green else Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(tool.second, fontSize = 10.sp, color = Color.LightGray)
                                if (tool.third.contains("Available")) {
                                    Button(
                                        onClick = {
                                            val success = viewModel.deductSparks(100) // Deducts escrow deposit
                                            if (success) {
                                                Toast.makeText(context, "Tool Request sent! 100 Sparks deposit secured safely in escrow.", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "Insufficient Spark funds in wallet balance to secure escrow deposit!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text("Request Tool", fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Direct UPI Intent Payments ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("💸", fontSize = 18.sp)
                            Column {
                                Text("Direct UPI Payment Gateway", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Pay sellers directly bypassing App Store commission cuts", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        IconButton(onClick = { showUpiSimulator = !showUpiSimulator }) {
                            Icon(if (showUpiSimulator) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = "Toggle")
                        }
                    }

                    if (showUpiSimulator) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = upiAmountInput,
                                onValueChange = { upiAmountInput = it },
                                label = { Text("Enter Amount (INR)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.bodySmall
                            )
                            Button(
                                onClick = {
                                    try {
                                        val uri = Uri.parse("upi://pay?pa=localiiiy@upi&pn=Localiiiy%20Handshake&am=$upiAmountInput&cu=INR")
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        context.startActivity(intent)
                                        Toast.makeText(context, "Redirecting to installed Indian UPI payment apps... 💸", Toast.LENGTH_LONG).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "UPI Payment of ₹$upiAmountInput simulated successfully! No external apps found.", Toast.LENGTH_LONG).show()
                                    }
                                    showUpiSimulator = false
                                }
                            ) {
                                Text("Send UPI Pay", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // --- Physical QR Code Space ID for Merchants ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🏢", fontSize = 18.sp)
                        Column {
                            Text("Storefront Space ID Scanner", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Scan merchant stickers to read tips, reviews, and catalog discounts", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Active Merchant sticker scan simulated: $checkInMerchantName", fontSize = 11.5.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            checkingInActive = true
                            viewModel.addSparks(50) // Credited real Sparks
                            Toast.makeText(context, "Successfully checked in at $checkInMerchantName! Earned +50 loyalty Sparks directly in your wallet!", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (checkingInActive) Color.DarkGray else Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (checkingInActive) "Checked In at Storefront ✔️" else "Scan Merchant QR Sticker", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- Merchant Sponsored Proximity Bounties ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🚴", fontSize = 18.sp)
                        Column {
                            Text("Merchant Sponsored Bounties", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Record clips visiting stores to earn massive Sparks", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    merchantCampaignsList.forEach { campaign ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(campaign.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            Text(campaign.second, fontSize = 10.5.sp, color = Color.LightGray)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(campaign.third, fontSize = 9.sp, color = Color.Gray)
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Campaign registered! Open your Studio camera to record your clip visit.", Toast.LENGTH_SHORT).show()
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Claim Bounty", fontSize = 9.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Green-Pulse Bounties ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🧹", fontSize = 18.sp)
                        Column {
                            Text("Green-Pulse Clean Up Bounties", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Help clean parks and plant trees to earn Sparks & Civic Badges", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    cleanupBountiesList.forEach { bounty ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(bounty.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            Text(bounty.second, fontSize = 11.sp, color = Color(0xFF10B981))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(bounty.third, fontSize = 9.sp, color = Color.Gray)
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Signed up for cleanup! Map coordinates shared with your local green circle.", Toast.LENGTH_LONG).show()
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Register", fontSize = 9.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}
