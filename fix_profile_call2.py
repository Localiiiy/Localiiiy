import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    text = f.read()

# Only target the one in ProfileScreen. It's around line 640.
target = """                        creatorEarnings = creatorEarnings,
                        onOpenMonetizationHub = { viewModel.openMonetizationHub() },"""
replacement = """                        creatorEarnings = creatorEarnings,
                        onOpenDataAnalysis = { viewModel.openDataAnalysis() },
                        onOpenMonetizationHub = { viewModel.openMonetizationHub() },"""

text = text.replace(target, replacement)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(text)
