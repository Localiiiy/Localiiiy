with open("app/src/main/java/com/example/ui/LocaliViewModel.kt", "r") as f:
    content = f.read()

# Add imports if not present
if "import com.example.util.HotspotAlert" not in content:
    content = content.replace(
        "import com.example.util.UserLocationData",
        "import com.example.util.UserLocationData\nimport com.example.util.HotspotAlert\nimport com.example.util.HotspotManager"
    )

# Add state properties near location state
old_loc_state = """    // --- Location & Nearby Radar State ---
    private val _currentLocation = MutableStateFlow<UserLocationData?>(null)
    val currentLocation: StateFlow<UserLocationData?> = _currentLocation.asStateFlow()
    private val _isDetectingLocation = MutableStateFlow(false)
    val isDetectingLocation: StateFlow<Boolean> = _isDetectingLocation.asStateFlow()"""

new_loc_state = old_loc_state + """
    private val _activeHotspotAlert = MutableStateFlow<HotspotAlert?>(null)
    val activeHotspotAlert: StateFlow<HotspotAlert?> = _activeHotspotAlert.asStateFlow()
    private var lastAlertedHotspotId: String? = null

    fun dismissHotspotAlert() {
        _activeHotspotAlert.value = null
    }

    fun simulateEnterHotspot(hotspotName: String) {
        val hotspot = HotspotManager.predefinedHotspots.find { it.name.contains(hotspotName, ignoreCase = true) } ?: HotspotManager.predefinedHotspots[0]
        lastAlertedHotspotId = hotspot.id
        val currentPosts = allPosts.value
        val activePostsCount = currentPosts.size.coerceAtLeast(5)
        _activeHotspotAlert.value = HotspotAlert(
            hotspotName = hotspot.name,
            postCount = activePostsCount,
            message = "🔥 Hotspot Alert: You entered ${hotspot.name}! ${activePostsCount} Pulse posts active right now."
        )
    }"""

if "_activeHotspotAlert" not in content:
    content = content.replace(old_loc_state, new_loc_state)

# Update detectCurrentLocation
old_detect = """    fun detectCurrentLocation(context: Context) {
        viewModelScope.launch {
            _isDetectingLocation.value = true
            try {
                val loc = LocationHelper.getCurrentLocation(context)
                _currentLocation.value = loc
                _autoDetectedLocation.value = loc
            } catch (_: Exception) {
            } finally {
                _isDetectingLocation.value = false
            }
        }
    }"""

new_detect = """    fun detectCurrentLocation(context: Context) {
        viewModelScope.launch {
            _isDetectingLocation.value = true
            try {
                val loc = LocationHelper.getCurrentLocation(context)
                _currentLocation.value = loc
                _autoDetectedLocation.value = loc

                val (hotspot, alert) = HotspotManager.checkHotspotEntry(loc.latitude, loc.longitude, allPosts.value, lastAlertedHotspotId)
                if (hotspot != null && alert != null) {
                    lastAlertedHotspotId = hotspot.id
                    _activeHotspotAlert.value = alert
                }
            } catch (_: Exception) {
            } finally {
                _isDetectingLocation.value = false
            }
        }
    }"""

if "HotspotManager.checkHotspotEntry" not in content:
    content = content.replace(old_detect, new_detect)

with open("app/src/main/java/com/example/ui/LocaliViewModel.kt", "w") as f:
    f.write(content)

print("LocaliViewModel updated successfully with Hotspot Geofencing!")
