import re

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    text = f.read()

# Add to ProfileScreen
text = text.replace(
    "onOpenMonetizationHub: () -> Unit = {},",
    "onOpenDataAnalysis: () -> Unit = {},\n    onOpenMonetizationHub: () -> Unit = {},"
)

# Add to ProfileScreen Content pass-through
text = text.replace(
    "onOpenMonetizationHub = {\n                    showMoreSettingsSheet = false\n                    onOpenMonetizationHub()\n                }",
    "onOpenDataAnalysis = {\n                    showMoreSettingsSheet = false\n                    onOpenDataAnalysis()\n                },\n                onOpenMonetizationHub = {\n                    showMoreSettingsSheet = false\n                    onOpenMonetizationHub()\n                }"
)

# Add to ProfileSettingsSheetContent
text = text.replace(
    "    onOpenMonetizationHub: () -> Unit = {},",
    "    onOpenDataAnalysis: () -> Unit = {},\n    onOpenMonetizationHub: () -> Unit = {},"
)

# Split SettingsRowItems
old_row = """        SettingsRowItem(
            icon = Icons.Default.MonetizationOn,
            title = "Analytics & Monetization",
            subtitle = "View your earnings, neighborhood trends, and analytics",
            onClick = onOpenMonetizationHub
        )"""

new_rows = """        SettingsRowItem(
            icon = androidx.compose.material.icons.Icons.Default.Analytics,
            title = "Data Analysis",
            subtitle = "View account reach, connection data, and performance analytics",
            onClick = onOpenDataAnalysis
        )

        SettingsRowItem(
            icon = androidx.compose.material.icons.Icons.Default.MonetizationOn,
            title = "Monetization",
            subtitle = "View your earnings, ad revenue, and views",
            onClick = onOpenMonetizationHub
        )"""

if "Analytics & Monetization" in text:
    text = text.replace(old_row, new_rows)

if "import androidx.compose.material.icons.filled.Analytics" not in text:
    text = text.replace("import androidx.compose.material.icons.filled.*", "import androidx.compose.material.icons.filled.*\nimport androidx.compose.material.icons.filled.Analytics")

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.write(text)
