import java.io.File
fun main() {
    val file = File("app/src/main/java/com/example/ui/screens/CreateScreen.kt")
    var text = file.readText()
    text = text.replace("if (creationMode == CreationMode.MARKETPLACE) \"\$selectedLandmark, \$selectedLocation (Required)\" else if (locationOptional) \"United States (Detected Country)\" else \"\$selectedLandmark, \$selectedLocation\"", "if (!locationOptional) \"United States (Detected Country)\" else \"\$selectedLandmark, \$selectedLocation\"")
    text = text.replace("if (creationMode != CreationMode.MARKETPLACE) {\n                Row", "Row")
    // Wait, let's just use regex to clean up any remaining MARKETPLACE
    text = text.replace(Regex("if \\(creationMode != CreationMode\\.MARKETPLACE\\) \\{([\\s\\S]*?)\\}"), "$1")
    file.writeText(text)
}
