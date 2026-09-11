with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "r") as f:
    text = f.read()

# Fix imports in ExploreScreen
bad_imports = """@Composable
import com.example.data.ClipEntity
import com.example.data.MarketItemEntity
fun ExploreScreen("""
good_imports = """@Composable
fun ExploreScreen("""
text = text.replace(bad_imports, good_imports)

text = text.replace("import com.example.data.UserProfileEntity", "import com.example.data.UserProfileEntity\nimport com.example.data.ClipEntity\nimport com.example.data.MarketplaceItemEntity")

# Fix MarketItemEntity usage
text = text.replace("List<MarketItemEntity>", "List<MarketplaceItemEntity>")

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "w") as f:
    f.write(text)
