import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Replace class MainActivity
class_replacement = '''
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt
import android.content.Context
import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity(), SensorEventListener {
    private val deepLinkClipIdState = mutableStateOf<Long?>(null)
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    
    // Emergency Panic Cloak Gesture
    private val isPanicCloakActive = mutableStateOf(false)
    private var lastUpdate: Long = 0
    private var last_x = 0f
    private var last_y = 0f
    private var last_z = 0f
    private val SHAKE_THRESHOLD = 800

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        parseDeepLink(intent)
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:109876543210:android:abcdef0123456789")
                    .setProjectId("Localiiiy-app")
                    .setApiKey("AIzaSyLocaliiiyFirebaseApiKeyMock")
                    .build()
                FirebaseApp.initializeApp(this, options)
            }
        } catch (e: Exception) {
            android.util.Log.w("MainActivity", "FirebaseApp init fallback: ${e.message}")
        }
        enableEdgeToEdge()
        setContent {
            LocaliiiyTheme {
                if (isPanicCloakActive.value) {
                    // Decoy Screen
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier
                            .androidx.compose.foundation.layout.fillMaxSize()
                            .androidx.compose.foundation.background(androidx.compose.ui.graphics.Color.Black),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.Text(
                            "Calculator",
                            color = androidx.compose.ui.graphics.Color.White,
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                        )
                    }
                } else {
                    LocaliiiyApp(
                        deepLinkClipId = deepLinkClipIdState.value,
                        onClearDeepLink = { deepLinkClipIdState.value = null }
                    )
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            if ((curTime - lastUpdate) > 100) {
                val diffTime = (curTime - lastUpdate)
                lastUpdate = curTime
                
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                
                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000
                if (speed > SHAKE_THRESHOLD) {
                    // Activate Panic Cloak
                    isPanicCloakActive.value = true
                }
                
                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
'''

# We need to carefully replace just the class block
pattern = re.compile(r'class MainActivity : ComponentActivity\(\) \{.*?\n\n    override fun onNewIntent\(intent: android.content.Intent\) \{', re.DOTALL)
match = pattern.search(content)
if match:
    # Need to keep onNewIntent.
    content = content.replace(match.group(0), class_replacement + '\n    override fun onNewIntent(intent: android.content.Intent) {')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
