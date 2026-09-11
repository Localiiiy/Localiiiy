with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "r") as f:
    text = f.read()

imports = """import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
"""
text = text.replace("import com.example.data.MarketplaceItemEntity\n", "import com.example.data.MarketplaceItemEntity\n" + imports)

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "w") as f:
    f.write(text)
