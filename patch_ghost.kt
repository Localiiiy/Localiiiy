import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Warning

@Composable
fun GhostProtocolButton(
    onTriggered: () -> Unit
) {
    var isActivated by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(5) }
    val coroutineScope = rememberCoroutineScope()
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(isActivated) {
        if (isActivated) {
            while (countdown > 0) {
                pulseScale.animateTo(1.05f, tween(100, easing = LinearEasing))
                pulseScale.animateTo(1f, tween(100, easing = LinearEasing))
                delay(800)
                countdown -= 1
            }
            onTriggered()
            isActivated = false
            countdown = 5
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .scale(pulseScale.value),
        shape = RoundedCornerShape(12.dp),
        color = if (isActivated) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.errorContainer,
        onClick = {
            if (!isActivated) isActivated = true
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Ghost Protocol",
                tint = if (isActivated) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isActivated) "GHOST PROTOCOL INITIATED ($countdown)" else "GHOST PROTOCOL",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = if (isActivated) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Instantly encrypts local DB & forces absolute stealth.",
                    style = MaterialTheme.typography.bodySmall,
                    color = (if (isActivated) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onErrorContainer).copy(alpha = 0.8f)
                )
            }
        }
    }
}
