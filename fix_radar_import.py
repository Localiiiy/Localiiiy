import re

with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "r") as f:
    text = f.read()

if "import androidx.compose.ui.text.font.FontWeight" not in text:
    text = text.replace("import androidx.compose.ui.layout.ContentScale", "import androidx.compose.ui.layout.ContentScale\nimport androidx.compose.ui.text.font.FontWeight")

with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "w") as f:
    f.write(text)
