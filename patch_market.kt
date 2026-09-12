import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.SmartToy
import kotlinx.coroutines.delay

@Composable
fun ConditionVisualizerDial(condition: String) {
    val conditionScore = when(condition.lowercase()) {
        "new" -> 1.0f
        "like new" -> 0.8f
        "good" -> 0.6f
        "fair" -> 0.4f
        "poor" -> 0.2f
        "junk" -> 0.05f
        else -> 0.7f
    }
    
    var animationPlayed by remember { mutableStateOf(false) }
    val sweepAngle by animateFloatAsState(
        targetValue = if (animationPlayed) 180f * conditionScore else 0f,
        animationSpec = tween(1500, easing = LinearEasing)
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Text("Item Condition: $condition", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.size(width = 120.dp, height = 60.dp), contentAlignment = Alignment.BottomCenter) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height * 2
                
                // Background track
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(canvasWidth, canvasHeight),
                    style = Stroke(width = 20f, cap = StrokeCap.Round)
                )

                // Foreground track (Gradient based on condition)
                val strokeColor = when {
                    conditionScore > 0.7f -> Color(0xFF4CAF50) // Green
                    conditionScore > 0.4f -> Color(0xFFFFC107) // Yellow
                    else -> Color(0xFFF44336) // Red
                }

                drawArc(
                    color = strokeColor,
                    startAngle = 180f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(canvasWidth, canvasHeight),
                    style = Stroke(width = 20f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun AutoNegotiateButton(
    sellerName: String,
    price: Double,
    currency: LocaliiiyCurrency
) {
    var isNegotiating by remember { mutableStateOf(false) }
    var currentOffer by remember { mutableStateOf(price) }

    LaunchedEffect(isNegotiating) {
        if (isNegotiating) {
            delay(1000)
            currentOffer = price * 0.9
            delay(1500)
            currentOffer = price * 0.85
            delay(1500)
            currentOffer = price * 0.8
            isNegotiating = false
        }
    }

    Surface(
        onClick = { if (!isNegotiating) isNegotiating = true },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.SmartToy, contentDescription = "AI", tint = MaterialTheme.colorScheme.onTertiaryContainer)
            Spacer(modifier = Modifier.width(8.dp))
            if (isNegotiating) {
                Text(
                    text = "AI Negotiating... Best Offer: ${currency.symbol}${String.format("%.2f", currentOffer)}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            } else if (currentOffer < price) {
                Text(
                    text = "Deal Agreed: ${currency.symbol}${String.format("%.2f", currentOffer)}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            } else {
                Text(
                    text = "Auto-Negotiate with AI",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}
