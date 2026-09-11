import re

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "r") as f:
    text = f.read()

# Remove from middle
text = text.replace("import com.example.data.MarketplaceItemEntity\n\n@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun PulseFeedComponent", "@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun PulseFeedComponent")

# Add to top
if "import com.example.data.MarketplaceItemEntity" not in text:
    text = text.replace("package com.example.ui.components\n\n", "package com.example.ui.components\n\nimport com.example.data.MarketplaceItemEntity\n")

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "w") as f:
    f.write(text)
