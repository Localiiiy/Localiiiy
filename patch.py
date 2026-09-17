import os

fpath = "app/src/main/java/com/example/ui/screens/LiveRadarScreen.kt"
with open(fpath, "r") as f:
    content = f.read()

content = content.replace(
"""import com.example.data.OtherUserEntity""",
"""import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.data.OtherUserEntity"""
)

content = content.replace(
"""    var isGhostModeActive by remember { mutableStateOf(false) }

    val hasLocationPermission = locationPermissionsState.allPermissionsGranted || exploreBypassAllowed""",
"""    var isGhostModeActive by remember { mutableStateOf(false) }
    var isAppInForeground by remember { mutableStateOf(true) }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        isAppInForeground = true
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        isAppInForeground = false
    }

    val hasLocationPermission = locationPermissionsState.allPermissionsGranted || exploreBypassAllowed
    val shouldShowBlips = !isGhostModeActive && isAppInForeground"""
)

content = content.replace(
"""                nearbyUsers = if (isGhostModeActive) nearbyUsers else nearbyUsers,""",
"""                nearbyUsers = if (shouldShowBlips) nearbyUsers else emptyList(),"""
)

with open(fpath, "w") as f:
    f.write(content)
