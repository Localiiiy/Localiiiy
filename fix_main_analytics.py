import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    text = f.read()

# State
state_target = "val showMonetizationHub by viewModel.showMonetizationHub.collectAsStateWithLifecycle()"
state_replacement = "val showDataAnalysis by viewModel.showDataAnalysis.collectAsStateWithLifecycle()\n    val showMonetizationHub by viewModel.showMonetizationHub.collectAsStateWithLifecycle()"
text = text.replace(state_target, state_replacement)

# Pass to ProfileScreen (tab 1 and tab 4)
text = text.replace("onOpenMonetizationHub = { viewModel.openMonetizationHub() }", "onOpenDataAnalysis = { viewModel.openDataAnalysis() },\n                        onOpenMonetizationHub = { viewModel.openMonetizationHub() }")

# Add DataAnalysisSheet
sheet_target = "if (showMonetizationHub) {"
sheet_replacement = """if (showDataAnalysis) {
            com.example.ui.components.DataAnalysisSheet(
                userProfile = userProfile,
                onDismiss = { viewModel.closeDataAnalysis() }
            )
        }
        if (showMonetizationHub) {"""
text = text.replace(sheet_target, sheet_replacement)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(text)
