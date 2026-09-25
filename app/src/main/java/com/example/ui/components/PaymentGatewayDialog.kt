package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PaymentGatewayProvider(
    val title: String,
    val subtitle: String,
    val badge: String,
    val badgeColor: Color
) {
    GOOGLE_PLAY(
        title = "Google Play Billing",
        subtitle = "Play Balance • Stored Cards • Family Library",
        badge = "1-TAP RECOMMENDED",
        badgeColor = Color(0xFF00E676)
    ),
    RAZORPAY(
        title = "Razorpay Fast Pay",
        subtitle = "UPI (GPay, PhonePe, Paytm) • Cards • Netbanking",
        badge = "LIVE UPI / QR",
        badgeColor = Color(0xFF00B0FF)
    ),
    STRIPE(
        title = "Stripe Global",
        subtitle = "Credit / Debit Cards • Global Currencies • Apple Pay",
        badge = "WORLDWIDE SECURE",
        badgeColor = Color(0xFFFFD700)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentGatewayDialog(
    planTitle: String,
    amountDisplay: String,
    onDismissRequest: () -> Unit,
    onPaymentSuccess: (provider: PaymentGatewayProvider, transactionId: String) -> Unit
) {
    var selectedGateway by remember { mutableStateOf(PaymentGatewayProvider.RAZORPAY) }
    var isProcessing by remember { mutableStateOf(false) }
    var processingStep by remember { mutableStateOf("") }

    // Razorpay Fields
    var upiAppSelected by remember { mutableStateOf("Google Pay") }
    var customUpiId by remember { mutableStateOf("") }
    var razorpayMethodTab by remember { mutableIntStateOf(0) } // 0 = UPI, 1 = Card, 2 = Netbanking

    // Stripe Fields
    var cardNumber by remember { mutableStateOf("•••• •••• •••• 4242") }
    var cardExpiry by remember { mutableStateOf("12/28") }
    var cardCvc by remember { mutableStateOf("888") }

    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) onDismissRequest() },
        containerColor = Color(0xFF141414),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
                color = Color.DarkGray,
                shape = CircleShape
            ) {
                Box(modifier = Modifier.size(width = 38.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Plan & Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                        Text(
                            text = "256-BIT SECURE CHECKOUT",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E676)
                        )
                    }
                    Text(
                        text = planTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF26210A),
                    border = BorderStroke(1.2.dp, Color(0xFFFFD700))
                ) {
                    Text(
                        text = amountDisplay,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFFFFD700),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f))

            if (isProcessing) {
                // Energetic Live Processing State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFFD700),
                            strokeWidth = 3.5.dp,
                            modifier = Modifier.size(52.dp)
                        )
                        Text(
                            text = processingStep,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Secure gateway session encrypted. Please do not close.",
                            fontSize = 11.5.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Gateway Provider Selector
                Text(
                    text = "Select Payment Gateway",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )

                PaymentGatewayProvider.values().forEach { provider ->
                    val isSelected = selectedGateway == provider
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF221E0B) else Color(0xFF1C1C1C),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) Color(0xFFFFD700) else Color(0xFF333333)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedGateway = provider }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = provider.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = provider.badgeColor.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = provider.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = provider.badgeColor,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = provider.subtitle,
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedGateway = provider },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFFFD700),
                                    unselectedColor = Color.Gray
                                )
                            )
                        }
                    }
                }

                // Provider Detail Area
                when (selectedGateway) {
                    PaymentGatewayProvider.GOOGLE_PLAY -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF19231A),
                            border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                                    Text("Google Play Account Ready", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                }
                                Text(
                                    text = "Your subscription of $amountDisplay will be managed through Google Play. Auto-renews unless cancelled in Play Store settings.",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFB9F6CA),
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    PaymentGatewayProvider.RAZORPAY -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Method Switcher: UPI vs Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Instant UPI", "Card / RuPay", "Netbanking").forEachIndexed { idx, title ->
                                    val isTabSelected = razorpayMethodTab == idx
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isTabSelected) Color(0xFF00B0FF).copy(alpha = 0.2f) else Color(0xFF222222),
                                        border = BorderStroke(1.dp, if (isTabSelected) Color(0xFF00B0FF) else Color.DarkGray),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { razorpayMethodTab = idx }
                                    ) {
                                        Text(
                                            text = title,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isTabSelected) Color(0xFF00B0FF) else Color.LightGray,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 7.dp)
                                        )
                                    }
                                }
                            }

                            if (razorpayMethodTab == 0) {
                                // UPI Apps Fast Pick
                                Text("Popular UPI Apps:", fontSize = 11.sp, color = Color.Gray)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Google Pay", "PhonePe", "Paytm", "BHIM UPI").forEach { app ->
                                        val isAppChosen = upiAppSelected == app
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = if (isAppChosen) Color(0xFF00B0FF) else Color(0xFF262626),
                                            modifier = Modifier.clickable { upiAppSelected = app }
                                        ) {
                                            Text(
                                                text = app,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isAppChosen) Color.Black else Color.White,
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = customUpiId,
                                    onValueChange = { customUpiId = it },
                                    placeholder = { Text("Or enter UPI ID (e.g. mobile@upi)", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF00B0FF), modifier = Modifier.size(16.dp)) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = "Direct bank redirection with RuPay, Visa, Mastercard, SBI, HDFC & ICICI enabled via Razorpay 3D Secure.",
                                    fontSize = 11.5.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }

                    PaymentGatewayProvider.STRIPE -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                label = { Text("Card Number") },
                                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFFFFD700)) },
                                trailingIcon = {
                                    TextButton(onClick = { cardNumber = "4242 4242 4242 4242" }) {
                                        Text("Autofill", fontSize = 10.5.sp, color = Color(0xFFFFD700))
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { cardExpiry = it },
                                    label = { Text("MM/YY") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = cardCvc,
                                    onValueChange = { cardCvc = it },
                                    label = { Text("CVC") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Pay Button
                Button(
                    onClick = {
                        isProcessing = true
                        coroutineScope.launch {
                            processingStep = "Initiating ${selectedGateway.title} secure handshake..."
                            delay(1000)
                            processingStep = when (selectedGateway) {
                                PaymentGatewayProvider.GOOGLE_PLAY -> "Verifying Google Play token & family permissions..."
                                PaymentGatewayProvider.RAZORPAY -> "Awaiting UPI prompt confirmation on ${upiAppSelected}..."
                                PaymentGatewayProvider.STRIPE -> "Authorizing 3D Secure Card Verification with bank..."
                            }
                            delay(1400)
                            processingStep = "Payment Captured! Generating Localiiiy Titan credentials..."
                            delay(800)
                            val txnId = when (selectedGateway) {
                                PaymentGatewayProvider.GOOGLE_PLAY -> "GPA.3392-8172-4912-${(1000..9999).random()}"
                                PaymentGatewayProvider.RAZORPAY -> "pay_Lcl${(100000..999999).random()}"
                                PaymentGatewayProvider.STRIPE -> "ch_3O7${(10000..99999).random()}stripe"
                            }
                            isProcessing = false
                            onPaymentSuccess(selectedGateway, txnId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.Black)
                        Text(
                            text = "Pay $amountDisplay via ${selectedGateway.title.split(" ").first()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
